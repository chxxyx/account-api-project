package com.chxxyx.projectfintech.domain.account.service;

import static com.chxxyx.projectfintech.domain.account.type.AccountError.ACCOUNT_UNDER_BALANCE;
import static com.chxxyx.projectfintech.domain.account.type.AccountError.USER_NOT_FOUND;

import com.chxxyx.projectfintech.ProjectFinTechApplication;
import com.chxxyx.projectfintech.domain.account.dto.AccountDto;
import com.chxxyx.projectfintech.domain.account.entity.Account;
import com.chxxyx.projectfintech.domain.account.exception.AccountException;
import com.chxxyx.projectfintech.domain.account.repository.AccountRepository;
import com.chxxyx.projectfintech.domain.account.type.AccountError;
import com.chxxyx.projectfintech.domain.account.type.AccountStatus;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private static final Logger logger = LoggerFactory.getLogger(ProjectFinTechApplication.class);

	// 계좌 생성
	@Transactional
	public AccountDto createAccount(String username, String password, String accountPassword,
		Long balance) {

		// 해당 유저가 회원가입이 되어있는 유저인지 먼저 확인 (로그인 한 회원만 계좌 서비스 이용 가능)
		User accountUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(USER_NOT_FOUND));

		if (balance <= 0) {
			throw new AccountException(ACCOUNT_UNDER_BALANCE);
		}

		validateCreateAccount(accountUser);

		// 계좌 생성 (수정 필요)
		String accountNumber = accountRepository.findFirstByOrderByAccountNumberDesc()
			.map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
			.orElse("1000000000");

		return AccountDto.fromEntity(accountRepository.save(
			Account.builder().accountUser(accountUser).accountNumber(accountNumber)
				.accountPassword(accountPassword).accountStatus(AccountStatus.IN_USE).balance(balance)
				.registeredAt(LocalDateTime.now()).build()));
	}

	private void validateCreateAccount(User accountUser) {
		if (accountRepository.countByAccountUser(accountUser) >= 10) {
			throw new AccountException(AccountError.MAX_ACCOUNT_PER_USER_10);
		}
	}

	// 계좌 삭제
	@Transactional
	public AccountDto deleteAccount(String username, String password, String accountNumber,
		String accountPassword) {

		User accountUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		validateDeleteAccount(accountUser, password, account, accountPassword);

		account.setAccountStatus(AccountStatus.UNREGISTERED);
		account.setUnRegisteredAt(LocalDateTime.now());

		accountRepository.save(account);

		return AccountDto.fromEntity(account);
	}

	private void validateDeleteAccount(User accountUser, String password, Account account, String accountPassword) {

		if (!Objects.equals(accountUser.getUsername(), account.getAccountUser().getUsername())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		if (!passwordEncoder.matches(password, accountUser.getPassword())) {
			throw new RuntimeException("비밀번호가 일치 하지 않습니다.");
		}
		if (!Objects.equals(account.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
		if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
	}

	// 계좌 조회 (회원이 생성한 계좌를 전부 확인할 수 있는 조회)
	@Transactional
	public List<AccountDto> getAccountsByUserId(UUID userId) {
		User accountUser = userRepository.findById(userId)
			.orElseThrow(() -> new AccountException(USER_NOT_FOUND));

		List<Account> accounts = accountRepository.findByAccountUser(accountUser);

		// dto 타입으로 바꿔주기
		return accounts.stream().map(AccountDto::fromEntity).collect(Collectors.toList());
	}

	// 계좌 정보 (비밀 번호만 수정 가능하다는 전제 하에) 수정
	@Transactional
	public AccountDto modifyAccountInfo(String username, String password, String accountNumber,
		String accountPassword, String accountRePassword) {
		// 로그인 한 유저 정보 확인
		User accountUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(USER_NOT_FOUND));
		if (!passwordEncoder.matches(password, accountUser.getPassword())) {
			throw new RuntimeException("비밀번호가 일치 하지 않습니다.");
		}
		// 변경 할 계좌 정보 확인
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));
		// 변경 전 계좌 비밀번호 먼저 확인
		if (!Objects.equals(account.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
		account.setAccountPassword(accountRePassword);
		account.setModifiedAt(LocalDateTime.now());
		accountRepository.save(account);

		return AccountDto.fromEntity(account);
	}
}
