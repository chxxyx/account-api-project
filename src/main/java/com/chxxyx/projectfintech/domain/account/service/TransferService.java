package com.chxxyx.projectfintech.domain.account.service;

import static com.chxxyx.projectfintech.config.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static com.chxxyx.projectfintech.domain.account.type.TransactionType.DEPOSIT;
import static com.chxxyx.projectfintech.domain.account.type.TransactionType.TRANSFER;

import com.chxxyx.projectfintech.config.jwt.TokenProvider;
import com.chxxyx.projectfintech.domain.account.entity.Account;
import com.chxxyx.projectfintech.domain.account.entity.Transaction;
import com.chxxyx.projectfintech.domain.account.entity.Transfer;
import com.chxxyx.projectfintech.domain.account.exception.AccountException;
import com.chxxyx.projectfintech.domain.account.model.TransferBalance;
import com.chxxyx.projectfintech.domain.account.model.TransferDto;
import com.chxxyx.projectfintech.domain.account.repository.AccountRepository;
import com.chxxyx.projectfintech.domain.account.repository.TransactionRepository;
import com.chxxyx.projectfintech.domain.account.repository.TransferRepository;
import com.chxxyx.projectfintech.domain.account.type.AccountError;
import com.chxxyx.projectfintech.domain.account.type.AccountStatus;
import com.chxxyx.projectfintech.domain.account.type.TransactionResultType;
import com.chxxyx.projectfintech.domain.account.type.TransactionType;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.exception.UserError;
import com.chxxyx.projectfintech.domain.user.exception.UserException;
import com.chxxyx.projectfintech.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

	private final TransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TransferRepository transferRepository;
	private final TokenProvider tokenProvider;

	/*
		송금
	 */
	@Transactional
	public TransferDto transferBalance(String token, TransferBalance.Request parameter) {

		User loginSenderUser = validateUser(token);

		Account senderAccount = validateSenderBalance(loginSenderUser,
			parameter.getSenderAccountNumber(), parameter.getAmount(),
			parameter.getAccountPassword());

		senderAccount.withdraw(parameter.getAmount());

		Account receiverAccount = validateReceiverBalance(parameter.getReceiverName(),
			parameter.getReceiverAccountNumber());

		receiverAccount.deposit(parameter.getAmount());

		saveAndGetTransaction(DEPOSIT, TransactionResultType.SUCCESS, receiverAccount,
			parameter.getAmount());

		return TransferDto.fromEntity(transferRepository.save(Transfer.builder().transaction(
				saveAndGetTransaction(TRANSFER, TransactionResultType.SUCCESS, senderAccount,
					parameter.getAmount())).senderName(senderAccount.getAccountUser().getName())
			.senderAccountNumber(senderAccount.getAccountNumber())
			.receiverName(receiverAccount.getAccountUser().getName())
			.receiverAccountNumber(receiverAccount.getAccountNumber()).build()));
	}

	/*
	 	송금 실패 결과 저장
	 */
	@Transactional
	public void saveFailedTransfer(String account, Long amount) {
		Account sencerAccount = accountRepository.findByAccountNumber(account)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		saveAndGetTransaction(TRANSFER, TransactionResultType.FAIL, sencerAccount, amount);
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
	 	송금하는 회원 계좌 확인
	 	송금 계좌, 계좌 비밀번호, 계좌주, 계좌 상태, 잔액 확인
 	*/
	private Account validateSenderBalance(User senderUser, String account, Long amount,
		String accountPassword) {

		Account senderAccount = accountRepository.findByAccountNumber(account)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		if (!Objects.equals(senderAccount.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}

		if (!Objects.equals(senderUser.getName(), senderAccount.getAccountUser().getName())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}

		if (senderAccount.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}

		if (senderAccount.getBalance() < amount) {
			throw new AccountException(AccountError.ACCOUNT_EXCEED_BALANCE);
		}
		return senderAccount;
	}

	/*
	 	송금받는 회원 계좌 확인
	 	계좌주 이름, 송금 받는 계좌 번호, 계좌주, 계좌 상태 확인
	 */
	private Account validateReceiverBalance(String receiverName, String receiverAccountNumber) {

		User receiverUser = userRepository.findByName(receiverName)
			.orElseThrow(() -> new AccountException(AccountError.USER_ACCOUNT_UN_MATCH));

		Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		if (!Objects.equals(receiverUser.getName(), receiverAccount.getAccountUser().getName())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}

		if (receiverAccount.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
		return receiverAccount;
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
