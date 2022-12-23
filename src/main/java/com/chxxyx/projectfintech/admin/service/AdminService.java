package com.chxxyx.projectfintech.admin.service;

import com.chxxyx.projectfintech.account.dto.AccountDto;
import com.chxxyx.projectfintech.account.entity.Account;
import com.chxxyx.projectfintech.account.repository.AccountRepository;
import com.chxxyx.projectfintech.user.dto.UserDto;
import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;

	public List<UserDto> getUserInfo() {

		List<User> user = userRepository.findAll();
		return user.stream().map(UserDto::fromEntity).collect(Collectors.toList());

	}

	public List<AccountDto> getUserAccountInfo() {
		List<Account> accounts = accountRepository.findAll();

		return accounts.stream().map(AccountDto::fromEntity).collect(Collectors.toList());
	}

}
