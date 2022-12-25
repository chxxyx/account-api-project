package com.chxxyx.projectfintech.admin.service;

import com.chxxyx.projectfintech.account.dto.AccountDto;
import com.chxxyx.projectfintech.account.entity.Account;
import com.chxxyx.projectfintech.account.exception.AccountException;
import com.chxxyx.projectfintech.account.repository.AccountRepository;
import com.chxxyx.projectfintech.account.type.AccountError;
import com.chxxyx.projectfintech.account.type.AccountStatus;
import com.chxxyx.projectfintech.admin.dto.ChangeAccountStatus;
import com.chxxyx.projectfintech.admin.dto.ChangeAccountStatus.Response;
import com.chxxyx.projectfintech.user.dto.UserDto;
import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.repository.UserRepository;
import com.chxxyx.projectfintech.user.type.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;

	public List<UserDto> getUserInfo() {

		Pageable limit = PageRequest.of(0, 10);
		Page<User> user = userRepository.findAllByOrderByCreatedAtDesc(limit);

		return user.stream().map(UserDto::fromEntity).collect(Collectors.toList());

	}

	public List<AccountDto> getUserAccountInfo() {

		Pageable limit = PageRequest.of(0, 10);
		Page<Account> accounts = accountRepository.findAllByOrderByCreatedAtDesc(limit);

		return accounts.stream().map(AccountDto::fromEntity).collect(Collectors.toList());
	}

	public UserDto changeUserType(String username, UserRole role) {

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

		user.setRole(role);
		user.setModifiedAt(LocalDateTime.now());
		userRepository.save(user);

		return UserDto.fromEntity(user);
	}

	public Response changeAccountStatus(String username, String accountNumber,
		AccountStatus accountStatus) {

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(AccountError.ACCOUNT_NOT_FOUND));

		account.setAccountStatus(accountStatus);
		account.setUnRegisteredAt(LocalDateTime.now());
		accountRepository.save(account);

		return ChangeAccountStatus.Response.from(account);
	}
}
