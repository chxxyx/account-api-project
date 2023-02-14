package com.chxxyx.projectfintech.domain.account.service;

import static com.chxxyx.projectfintech.config.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static com.chxxyx.projectfintech.domain.account.type.TransactionType.DEPOSIT;
import static com.chxxyx.projectfintech.domain.account.type.TransactionType.WITHDRAW;

import com.chxxyx.projectfintech.config.jwt.TokenProvider;
import com.chxxyx.projectfintech.domain.account.model.DepositBalance;
import com.chxxyx.projectfintech.domain.account.model.TransactionDto;
import com.chxxyx.projectfintech.domain.account.entity.Account;
import com.chxxyx.projectfintech.domain.account.entity.Transaction;
import com.chxxyx.projectfintech.domain.account.exception.AccountException;
import com.chxxyx.projectfintech.domain.account.model.TransactionList;
import com.chxxyx.projectfintech.domain.account.model.WithdrawBalance;
import com.chxxyx.projectfintech.domain.account.repository.AccountRepository;
import com.chxxyx.projectfintech.domain.account.repository.TransactionRepository;
import com.chxxyx.projectfintech.domain.account.type.AccountError;
import com.chxxyx.projectfintech.domain.account.type.AccountStatus;
import com.chxxyx.projectfintech.domain.account.type.TransactionResultType;
import com.chxxyx.projectfintech.domain.account.type.TransactionType;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.exception.UserError;
import com.chxxyx.projectfintech.domain.user.exception.UserException;
import com.chxxyx.projectfintech.domain.user.repository.UserRepository;
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
public class DepositWithdrawService {

	private final TransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TokenProvider tokenProvider;

	/*
		입금
	 */
	@Transactional
	public TransactionDto depositBalance(String token, DepositBalance.Request parameter) {

		User loginUser = validateUser(token);

		Account account = accountRepository.findByAccountNumber(parameter.getAccountNumber())
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		validateDepositBalance(loginUser, account, parameter.getAccountPassword());
		account.deposit(parameter.getAmount());

		return TransactionDto.from(
			saveAndGetTransaction(DEPOSIT, TransactionResultType.SUCCESS, account,
				parameter.getAmount()));
	}

	/*
		입금 실패 저장
	 */
	@Transactional
	public void saveFailedDepositTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		saveAndGetTransaction(DEPOSIT, TransactionResultType.FAIL, account, amount);
	}

	/*
		출금
	 */
	@Transactional
	public TransactionDto withdrawBalance(String token, WithdrawBalance.Request parameter) {

		User loginUser = validateUser(token);

		Account account = accountRepository.findByAccountNumber(
				parameter.getAccountNumber()) // 계좌 조회
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		validateWithdrawBalance(loginUser, account, parameter.getAmount(),
			parameter.getAccountPassword());
		account.withdraw(parameter.getAmount());

		return TransactionDto.from(
			saveAndGetTransaction(WITHDRAW, TransactionResultType.SUCCESS, account,
				parameter.getAmount()));
	}

	/*
	 	출금 실패 저장
	 */
	@Transactional
	public void saveFailedWithdrawTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		saveAndGetTransaction(WITHDRAW, TransactionResultType.FAIL, account, amount);
	}

	/*
	 	거래내역 조회
	 */
	public List<TransactionDto> getTransactionList(String token,
		TransactionList.Request parameter) {
		// 회원 정보 확인
		User loginUser = validateUser(token);
		// 계좌 번호 확인
		Account account = accountRepository.findByAccountNumber(parameter.getAccountNumber())
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		// 계좌 비밀번호 확인
		if (!Objects.equals(account.getAccountPassword(), parameter.getAccountPassword())) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}

		LocalDateTime startDt = parameter.getStartDate().atStartOfDay();
		LocalDateTime endDt = parameter.getEndDate().atTime(LocalTime.MAX);

		List<Transaction> transactionList = transactionRepository.findAllByAccount_AccountNumberAndTransactedAtBetween(
			parameter.getAccountNumber(), startDt, endDt);

		List<TransactionDto> transactionDtoList = new ArrayList<>();
		for (Transaction transaction : transactionList) {
			transactionDtoList.add(TransactionDto.from(transaction));
		}
		return transactionDtoList;
	}

	/*
		validate - 유저 토큰 확인 및 회원 정보 확인
	 */
	private User validateUser(String token) {

		String subToken = token.substring(TOKEN_PREFIX.length());

		String username = "";
		username = tokenProvider.getUserPk(subToken);

		log.info(username);

		return userRepository.findByUsername(username)
			.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
	}

	/*
		입금 계좌 확인
		계좌주, 계좌 비밀번호, 계좌 상태 확인
	 */
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

	/*
		출금할 계좌 확인
		계좌주, 계좌 비밀번호, 계좌 상태, 계좌 잔액 확인
	 */
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

	/*
	 	거래 성공 실패 여부 저장 부분 공통화 (코드 중복 최소화)
	 */
	private Transaction saveAndGetTransaction(TransactionType transactionType,
		TransactionResultType transactionResultType, Account account, Long amount) {
		return transactionRepository.save(Transaction.builder().transactionType(transactionType)
			.transactionResultType(transactionResultType).account(account).amount(amount)
			.balanceSnapshot(account.getBalance()).transactedAt(LocalDateTime.now()).build());
	}
}
