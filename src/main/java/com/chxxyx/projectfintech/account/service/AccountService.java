package com.chxxyx.projectfintech.account.service;

import static com.chxxyx.projectfintech.account.type.AccountStatus.IN_USE;
import static com.chxxyx.projectfintech.account.type.AccountStatus.UNREGISTERED;

import com.chxxyx.projectfintech.ProjectFinTechApplication;
import com.chxxyx.projectfintech.account.dto.AccountDto;
import com.chxxyx.projectfintech.account.entity.Account;
import com.chxxyx.projectfintech.account.exception.AccountException;
import com.chxxyx.projectfintech.account.repository.AccountRepository;
import com.chxxyx.projectfintech.account.type.AccountError;
import com.chxxyx.projectfintech.account.type.AccountStatus;
import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
	private final AccountRepository accountRepository;
    private final UserRepository userRepository;
	private static final Logger logger = LoggerFactory.getLogger(ProjectFinTechApplication.class);

	@Transactional
	public AccountDto createAccount(String username, String password,
									String accountPassword, Long balance) {

		// 해당 유저가 회원가입이 되어있는 유저인지 먼저 확인 (로그인 한 회원만 계좌 서비스 이용 가능)
		User accountUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));

		validateCreateAccount(accountUser);

		// 계좌 생성 (수정 필요)
		String accountNumber = accountRepository.findFirstByOrderByAccountNumberDesc().map(account
			-> (Integer.parseInt(account.getAccountNumber())) + 1 + "").orElse("1000000000");
		// 계좌 비번 암호화
//		String accountPW = BCrypt.hashpw(accountPassword, BCrypt.gensalt());

		return AccountDto.fromEntity(
			accountRepository.save(Account.builder()
					.accountUser(accountUser)
					.accountNumber(accountNumber)
					.accountPassword(accountPassword)
					.accountStatus(IN_USE)
					.balance(balance)
					.registeredAt(LocalDateTime.now())
					.build()
		));
	}
	private void validateCreateAccount(User accountUser) {
		if (accountRepository.countByAccountUser(accountUser) >= 10) {
			throw new AccountException(AccountError.MAX_ACCOUNT_PER_USER_10);
		}
	}

	@Transactional
	public AccountDto deleteAccount(String username, String password, String accountNumber, String accountPassword) {
		User accountUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(AccountError.USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		
		validateDeleteAccount(accountUser, account);

		account.setAccountStatus(UNREGISTERED);
		account.setUnRegisteredAt(LocalDateTime.now());

		accountRepository.save(account);

		return AccountDto.fromEntity(account);
	}

	private void validateDeleteAccount(User accountUser, Account account) {
		if (accountUser.getUsername() != account.getAccountUser().getUsername()) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		if (account.getAccountStatus() == UNREGISTERED) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
	}
}
