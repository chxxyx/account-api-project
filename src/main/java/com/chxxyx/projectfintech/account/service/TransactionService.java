package com.chxxyx.projectfintech.account.service;

import static com.chxxyx.projectfintech.account.type.TransactionResultType.*;
import static com.chxxyx.projectfintech.account.type.TransactionType.DEPOSIT;

import com.chxxyx.projectfintech.account.dto.DepositBalance;
import com.chxxyx.projectfintech.account.dto.TransactionDto;
import com.chxxyx.projectfintech.account.entity.Account;
import com.chxxyx.projectfintech.account.entity.Transaction;
import com.chxxyx.projectfintech.account.exception.AccountException;
import com.chxxyx.projectfintech.account.repository.AccountRepository;
import com.chxxyx.projectfintech.account.repository.TransactionRepository;
import com.chxxyx.projectfintech.account.type.AccountError;
import com.chxxyx.projectfintech.account.type.AccountStatus;
import com.chxxyx.projectfintech.account.type.TransactionResultType;
import com.chxxyx.projectfintech.account.type.TransactionType;
import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.repository.UserRepository;
import java.time.LocalDateTime;
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

	@Transactional
	public TransactionDto depositBalance(String username, String password, String accountNumber,
										 String accountPassword, Long amount){
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		
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
			.orElseThrow(()-> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
	}

	// 코드 중복 최소화, 저장하는 부분 공통화
	private Transaction saveAndGetTransaction(
		TransactionType transactionType,
		TransactionResultType transactionResultType,
		Account account,
		Long amount) {
		return transactionRepository.save(
			Transaction.builder()
				.transactionType(transactionType)
				.transactionResultType(transactionResultType)
				.account(account)
				.amount(amount)
				.balanceSnapshot(account.getBalance())
				.transactedAt(LocalDateTime.now())
				.build());
	}
}
