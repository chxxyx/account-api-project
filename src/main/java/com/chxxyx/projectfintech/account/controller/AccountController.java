package com.chxxyx.projectfintech.account.controller;

import com.chxxyx.projectfintech.account.dto.AccountInfo;
import com.chxxyx.projectfintech.account.dto.CreateAccount;
import com.chxxyx.projectfintech.account.dto.DeleteAccount;
import com.chxxyx.projectfintech.account.dto.ModifyAccount;
import com.chxxyx.projectfintech.account.service.AccountService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping("/account/create")
	@PreAuthorize("hasRole('USER')")
	public CreateAccount.Response createAccount(@RequestBody @Valid CreateAccount.Request request) {

		return CreateAccount.Response.from(
			accountService.createAccount(request.getUsername(), request.getPassword(),
				request.getAccountPassword(), request.getBalance()));
	}

	@DeleteMapping("/account/delete")
	@PreAuthorize("hasRole('USER')")
	public DeleteAccount.Response deleteAccount(@RequestBody @Valid DeleteAccount.Request request) {

		return DeleteAccount.Response.from(
			accountService.deleteAccount(request.getUsername(), request.getPassword(),
				request.getAccountNumber(), request.getAccountPassword()));
	}

	@GetMapping("/account")
	@PreAuthorize("hasRole('USER')")
	public List<AccountInfo> getAccountByUserId(@RequestParam("user_id") UUID userId) {

		return accountService.getAccountsByUserId(userId).stream().map(
			accountDto -> AccountInfo.builder().accountNumber(accountDto.getAccountNumber())
				.balance(accountDto.getBalance()).build()).collect(Collectors.toList());
	}

	// 계좌 정보 수정 (비밀 번호)
	@PostMapping("/account/modify")
	@PreAuthorize("hasRole('USER')")
	public ModifyAccount.Response modifyAccountInfo(
		@RequestBody @Valid ModifyAccount.Request request) {

		return ModifyAccount.Response.from(
			accountService.modifyAccountInfo(request.getUsername(), request.getPassword(),
				request.getAccountNumber(), request.getAccountPassword(),
				request.getAccountRePassword()));
	}

}