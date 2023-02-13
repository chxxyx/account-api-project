package com.chxxyx.projectfintech.domain.account.service;

import static com.chxxyx.projectfintech.domain.account.type.TransactionType.DEPOSIT;
import static com.chxxyx.projectfintech.domain.account.type.TransactionType.REMIT;
import static com.chxxyx.projectfintech.domain.account.type.TransactionType.WITHDRAW;

import com.chxxyx.projectfintech.domain.account.dto.TransactionDto;
import com.chxxyx.projectfintech.domain.account.dto.TransferDto;
import com.chxxyx.projectfintech.domain.account.entity.Account;
import com.chxxyx.projectfintech.domain.account.entity.Transaction;
import com.chxxyx.projectfintech.domain.account.entity.Transfer;
import com.chxxyx.projectfintech.domain.account.exception.AccountException;
import com.chxxyx.projectfintech.domain.account.repository.AccountRepository;
import com.chxxyx.projectfintech.domain.account.repository.TransactionRepository;
import com.chxxyx.projectfintech.domain.account.repository.TransferRepository;
import com.chxxyx.projectfintech.domain.account.type.AccountError;
import com.chxxyx.projectfintech.domain.account.type.AccountStatus;
import com.chxxyx.projectfintech.domain.account.type.TransactionResultType;
import com.chxxyx.projectfintech.domain.account.type.TransactionType;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

	// 입금
	@Transactional
	public TransactionDto depositBalance(String username, String password, String accountNumber,
		String accountPassword, Long amount) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		validateDepositBalance(user, account, accountPassword);
		account.deposit(amount);

		return TransactionDto.fromEntity(saveAndGetTransaction(DEPOSIT, TransactionResultType.SUCCESS, account, amount));
	}

	private void validateDepositBalance(User user, Account account, String accountPassword) {
		if (!Objects.equals(user.getUsername(), account.getAccountUser().getUsername())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		if (!Objects.equals(account.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
		if (account.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
	}

	@Transactional
	public void saveFailedDepositTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		saveAndGetTransaction(DEPOSIT, TransactionResultType.FAIL, account, amount);
	}

	// 출금
	@Transactional
	public TransactionDto withdrawBalance(String username, String password, String accountNumber,
		String accountPassword, Long amount) {
		log.info("비밀번호 " + accountPassword);

		User user = userRepository.findByUsername(username) // 사용자 조회
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));

		Account account = accountRepository.findByAccountNumber(accountNumber) // 계좌 조회
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		log.info(" account ::::: " + String.valueOf(account));

		log.info("계좌에서 가져온 비밀번호  ::: " + account.getAccountPassword());
		log.info("입력한 비밀번호  ::: " + accountPassword);

		validateWithdrawBalance(user, account, amount, accountPassword);
		account.withdraw(amount);

		return TransactionDto.fromEntity(saveAndGetTransaction(WITHDRAW, TransactionResultType.SUCCESS, account, amount));
	}

	// 출금할 계좌 확인
	private void validateWithdrawBalance(User user, Account account, Long amount,
		String accountPassword) {
		if (!Objects.equals(user.getUsername(), account.getAccountUser().getUsername())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		if (!Objects.equals(account.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
		if (account.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
		if (account.getBalance() < amount) {
			throw new AccountException(AccountError.ACCOUNT_EXCEED_BALANCE);
		}
	}

	// 출금 실패 저장
	@Transactional
	public void saveFailedWithdrawTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		saveAndGetTransaction(WITHDRAW, TransactionResultType.FAIL, account, amount);
	}

	@Transactional
	public TransferDto transferBalance(String username, String password, String senderName,
		String senderAccountNumber, String accountPassword, String receiverName,
		String receiverAccountNumber, Long amount) {

		Account senderAccount = validateSenderBalance(username, senderAccountNumber, amount,
			accountPassword);
		senderAccount.withdraw(amount);

		Account receiverAccount = validateReceiverBalance(receiverName, receiverAccountNumber);
		receiverAccount.deposit(amount);
		saveAndGetTransaction(DEPOSIT, TransactionResultType.SUCCESS, receiverAccount, amount);

		return TransferDto.fromEntity(transferRepository.save(Transfer.builder()
			.transaction(saveAndGetTransaction(REMIT, TransactionResultType.SUCCESS, senderAccount, amount))
			.senderName(senderAccount.getAccountUser().getName())
			.senderAccountNumber(senderAccount.getAccountNumber())
			.receiverName(receiverAccount.getAccountUser().getName())
			.receiverAccountNumber(receiverAccount.getAccountNumber()).build())

		);
	}

	// 송금하는 회원 계좌 확인
	private Account validateSenderBalance(String username, String account, Long amount,
		String accountPassword) {

		User senderUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		// 계좌가 존재하는 지 체크
		Account senderAccount = accountRepository.findByAccountNumber(account)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		// 비밀번호 확인
		if (!Objects.equals(senderAccount.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
		// 계좌와 계좌 소유주의 이름이 다를 때
		if (!Objects.equals(senderUser.getName(), senderAccount.getAccountUser().getName())) {
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

	// 송금할 계좌, 계좌주 확인
	private Account validateReceiverBalance(String receiverName, String receiverAccountNumber) {
		User receiverUser = userRepository.findByName(receiverName)
			.orElseThrow(() -> new AccountException(AccountError.USER_ACCOUNT_UN_MATCH));

		Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		// 계좌와 계좌 소유주의 이름이 다를 때
		if (!Objects.equals(receiverUser.getName(), receiverAccount.getAccountUser().getName())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		// 계좌가 해지된 상태일 때
		if (receiverAccount.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
		return receiverAccount;
	}

	// 송금 실패 결과 저장
	@Transactional
	public void saveFailedTransfer(String account, Long amount) {
		Account sencerAccount = accountRepository.findByAccountNumber(account)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		saveAndGetTransaction(REMIT, TransactionResultType.FAIL, sencerAccount, amount);
	}

	// 코드 중복 최소화, 저장하는 부분 공통화
	private Transaction saveAndGetTransaction(TransactionType transactionType,
		TransactionResultType transactionResultType, Account account, Long amount) {
		return transactionRepository.save(Transaction.builder().transactionType(transactionType)
			.transactionResultType(transactionResultType).account(account).amount(amount)
			.balanceSnapshot(account.getBalance()).transactedAt(LocalDateTime.now()).build());
	}

	// 거래내역 조회
	public List<TransactionDto> getTransactionList(String username, String password,
		String accountNumber, String accountPassword, LocalDate startDate, LocalDate endDate) {
		// 회원 정보 확인
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		// 계좌 번호 확인
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		// 계좌 비밀번호 확인
		if (!Objects.equals(account.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}

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
