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

}
