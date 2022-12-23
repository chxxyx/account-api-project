package com.chxxyx.projectfintech.account.service;

import static com.chxxyx.projectfintech.account.type.TransactionResultType.*;
import static com.chxxyx.projectfintech.account.type.TransactionType.DEPOSIT;
import static com.chxxyx.projectfintech.account.type.TransactionType.REMIT;
import static com.chxxyx.projectfintech.account.type.TransactionType.WITHDRAW;

import com.chxxyx.projectfintech.account.dto.TransactionDto;
import com.chxxyx.projectfintech.account.dto.TransferDto;
import com.chxxyx.projectfintech.account.entity.Account;
import com.chxxyx.projectfintech.account.entity.Transaction;
import com.chxxyx.projectfintech.account.entity.Transfer;
import com.chxxyx.projectfintech.account.exception.AccountException;
import com.chxxyx.projectfintech.account.repository.AccountRepository;
import com.chxxyx.projectfintech.account.repository.TransactionRepository;
import com.chxxyx.projectfintech.account.repository.TransferRepository;
import com.chxxyx.projectfintech.account.type.AccountError;
import com.chxxyx.projectfintech.account.type.AccountStatus;
import com.chxxyx.projectfintech.account.type.TransactionResultType;
import com.chxxyx.projectfintech.account.type.TransactionType;
import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TransferRepository transferRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public TransactionDto depositBalance(String username, String password, String accountNumber,
		String accountPassword, Long amount) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		checkAccountPassword(account, accountPassword);
		validateDepositBalance(user, account);
		account.deposit(amount);

		return TransactionDto.fromEntity(saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount));
	}

	private void validateDepositBalance(User user, Account account) {
		if (user.getUsername() != account.getAccountUser().getUsername()) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		if (account.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
	}

	@Transactional
	public void saveFailedDepositTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
	}

	@Transactional
	public TransactionDto withdrawBalance(String username, String password, String accountNumber,
		String accountPassword, Long amount) {

		User user = userRepository.findByUsername(username) // 사용자 조회
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));

		Account account = accountRepository.findByAccountNumber(accountNumber) // 계좌 조회
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		checkAccountPassword(account, accountPassword);
		validateWithdrawBalance(user, account, amount);
		account.withdraw(amount);

		return TransactionDto.fromEntity(saveAndGetTransaction(WITHDRAW, SUCCESS, account, amount));
	}

	private void validateWithdrawBalance(User user, Account account, Long amount) {
		if (user.getUsername() != account.getAccountUser().getUsername()) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		if (account.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
		if (account.getBalance() < amount) {
			throw new AccountException(AccountError.ACCOUNT_EXCEED_BALANCE);
		}
	}

	@Transactional
	public void saveFailedWithdrawTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		saveAndGetTransaction(WITHDRAW, FAIL, account, amount);
	}

	@Transactional
	public TransferDto transferBalance(String username, String password, String senderName,
		String senderAccountNumber, String accountPassword, String receiverName,
		String receiverAccountNumber, Long amount) {

		Account senderAccount = validateSenderBalance(username, senderAccountNumber, amount);
		checkAccountPassword(senderAccount, accountPassword);

		senderAccount.withdraw(amount);

		User receiverUser = userRepository.findByName(receiverName)
			.orElseThrow(() -> new AccountException(AccountError.USER_ACCOUNT_UN_MATCH));

		Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		validateReceiverBalance(receiverAccount);
		receiverAccount.deposit(amount);
		saveAndGetTransaction(DEPOSIT, SUCCESS, receiverAccount, amount);

		return TransferDto.fromEntity(transferRepository.save(Transfer.builder()
			.transaction(saveAndGetTransaction(REMIT, SUCCESS, senderAccount, amount))
			.senderName(senderAccount.getAccountUser().getName())
			.senderAccountNumber(senderAccount.getAccountNumber())
			.receiverName(receiverUser.getName())
			.receiverAccountNumber(receiverAccount.getAccountNumber()).build())

		);
	}

	private Account validateSenderBalance(String username, String account, Long amount) {

		User senderUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		// 계좌가 존재하는 지 체크
		Account senderAccount = accountRepository.findByAccountNumber(account)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		// 계좌와 계좌 소유주의 이름이 다를 때
		if (senderUser.getName() != senderAccount.getAccountUser().getName()) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		// 계좌가 해지된 상태일 때
		if (senderAccount.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
		// 계좌 잔액이 송금액 보다 부족할 때
		if (senderAccount.getBalance() < amount) {
			throw new AccountException(AccountError.ACCOUNT_EXCEED_BALANCE);
		}
		return senderAccount;
	}

	private void validateReceiverBalance(Account account) {
		// 계좌가 해지된 상태일 때
		if (account.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
	}

	@Transactional
	public void saveFailedTransfer(String account, Long amount) {
		Account sencerAccount = accountRepository.findByAccountNumber(account)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		saveAndGetTransaction(REMIT, FAIL, sencerAccount, amount);
	}

	// 코드 중복 최소화, 저장하는 부분 공통화
	private Transaction saveAndGetTransaction(TransactionType transactionType,
		TransactionResultType transactionResultType, Account account, Long amount) {
		return transactionRepository.save(Transaction.builder().transactionType(transactionType)
			.transactionResultType(transactionResultType).account(account).amount(amount)
			.balanceSnapshot(account.getBalance()).transactedAt(LocalDateTime.now()).build());
	}

	private void checkAccountPassword(Account account, String accountPassword) {
		if (!passwordEncoder.matches(accountPassword, account.getAccountPassword())) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
	}

	// 거래내역 조회
	public List<TransactionDto> getTransactionList(String username, String password,
		String accountNumber, String accountPassword, LocalDate startDate, LocalDate endDate) {

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		checkAccountPassword(account, accountPassword);

		LocalDateTime startDt = startDate.atStartOfDay();
		LocalDateTime endDt = endDate.atTime(LocalTime.MAX);

		List<Transaction> transactionList = transactionRepository.findAllByAccount_AccountNumberAndTransactedAtBetween(
			accountNumber, startDt, endDt);

		List<TransactionDto> transactionDtoList = new ArrayList<>();
		for (Transaction transaction : transactionList) {
			transactionDtoList.add(TransactionDto.fromEntity(transaction));
		}
		return transactionDtoList;
	}
}
