package com.chxxyx.projectfintech.domain.account.service;

import static com.chxxyx.projectfintech.config.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static com.chxxyx.projectfintech.domain.account.type.AccountError.ACCOUNT_UNDER_BALANCE;
import static com.chxxyx.projectfintech.domain.account.type.AccountError.INVALID_REQUEST;

import com.chxxyx.projectfintech.ProjectFinTechApplication;
import com.chxxyx.projectfintech.config.jwt.TokenProvider;
import com.chxxyx.projectfintech.domain.account.model.AccountDto;
import com.chxxyx.projectfintech.domain.account.entity.Account;
import com.chxxyx.projectfintech.domain.account.exception.AccountException;
import com.chxxyx.projectfintech.domain.account.model.CreateAccount;
import com.chxxyx.projectfintech.domain.account.model.DeleteAccount;
import com.chxxyx.projectfintech.domain.account.model.ModifyAccount;
import com.chxxyx.projectfintech.domain.account.repository.AccountRepository;
import com.chxxyx.projectfintech.domain.account.type.AccountError;
import com.chxxyx.projectfintech.domain.account.type.AccountStatus;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.exception.UserError;
import com.chxxyx.projectfintech.domain.user.exception.UserException;
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
	private final TokenProvider tokenProvider;
	private static final Logger logger = LoggerFactory.getLogger(ProjectFinTechApplication.class);

	/*
		계좌 생성
	 */
	@Transactional
	public AccountDto createAccount(String token, CreateAccount.Request parameter) {

		// 해당 유저가 회원가입 및 로그인 되어있는 유저인지 먼저 확인 (로그인 한 회원만 계좌 서비스 이용 가능)
		User loginUser = validateUser(token);

		if (parameter.getBalance() <= 0) {
			throw new AccountException(ACCOUNT_UNDER_BALANCE);
		}

		validateCreateAccount(loginUser);

		// 계좌 번호 랜덤 생성
		String accountNumber = accountRepository.findFirstByOrderByAccountNumberDesc()
			.map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
			.orElse("1000000000");

		return AccountDto.from(accountRepository.save(
			Account.builder().accountUser(loginUser).accountNumber(accountNumber)
				.accountPassword(parameter.getAccountPassword()).accountStatus(AccountStatus.IN_USE)
				.balance(parameter.getBalance()).registeredAt(LocalDateTime.now()).build()));
	}

	/*
	 	계좌 삭제
	 */
	@Transactional
	public AccountDto deleteAccount(String accountNumber, String token,
		DeleteAccount.Request parameter) {

		User accountUser = validateUser(token);

		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		validateDeleteAccount(accountUser, account, parameter.getAccountPassword());

		account.setAccountStatus(AccountStatus.UNREGISTERED);
		account.setUnRegisteredAt(LocalDateTime.now());

		accountRepository.save(account);

		return AccountDto.from(account);
	}

	/*
		계좌 조회 (회원이 생성한 계좌를 전부 확인할 수 있는 조회)
	 */
	@Transactional
	public List<AccountDto> getAccountsByUserId(String token, UUID userId) {

		User loginUser = validateUser(token);

		userRepository.findById(userId).orElseThrow(() -> new AccountException(INVALID_REQUEST));

		List<Account> accounts = accountRepository.findByAccountUser(loginUser);

		// dto 타입으로 바꿔주기
		return accounts.stream().map(AccountDto::from).collect(Collectors.toList());
	}

	/*
	 	계좌 정보 수정 (계좌 비밀번호 수정만 가능하다는 전제 하에)
	 */
	@Transactional
	public AccountDto modifyAccountInfo(String token, ModifyAccount.Request parameter) {

		User loginUser = validateUser(token);

		Account account = accountRepository.findByAccountNumber(parameter.getAccountNumber())
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		if (!Objects.equals(account.getAccountUser().getUsername(), loginUser.getUsername())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}

		if (!Objects.equals(account.getAccountPassword(), parameter.getAccountPassword())) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
		account.setAccountPassword(parameter.getAccountRePassword());
		account.setModifiedAt(LocalDateTime.now());
		accountRepository.save(account);

		return AccountDto.from(account);
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
		validate - 계좌 생성은 유저 한 명 당 10개까지만 가능.
 	*/
	private void validateCreateAccount(User accountUser) {
		if (accountRepository.countByAccountUser(accountUser) >= 10) {
			throw new AccountException(AccountError.MAX_ACCOUNT_PER_USER_10);
		}
	}

	/*
		validate - 계좌 삭제 체크
	 */
	private void validateDeleteAccount(User accountUser, Account account, String accountPassword) {

		if (!Objects.equals(accountUser.getUsername(), account.getAccountUser().getUsername())) {
			throw new AccountException(AccountError.USER_ACCOUNT_UN_MATCH);
		}
		if (!Objects.equals(account.getAccountPassword(), accountPassword)) {
			throw new AccountException(AccountError.ACCOUNT_PASSWORD_NOT_SAME);
		}
		if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
			throw new AccountException(AccountError.ACCOUNT_ALREADY_UNREGISTERED);
		}
	}
}
