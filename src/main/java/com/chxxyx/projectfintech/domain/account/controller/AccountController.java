package com.chxxyx.projectfintech.domain.account.controller;

import com.chxxyx.projectfintech.domain.account.model.AccountInfo;
import com.chxxyx.projectfintech.domain.account.model.CreateAccount;
import com.chxxyx.projectfintech.domain.account.model.DeleteAccount;
import com.chxxyx.projectfintech.domain.account.model.ModifyAccount;
import com.chxxyx.projectfintech.domain.account.service.AccountService;
import com.chxxyx.projectfintech.domain.account.model.CreateAccount.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping("/create")
	@PreAuthorize("hasRole('USER')")
	public CreateAccount.Response createAccount(@RequestHeader("Authorization") String token,
		@RequestBody @Valid CreateAccount.Request request) {
		log.info(":::::::::::::::::::" + token);
		return CreateAccount.Response.from(accountService.createAccount(token, request));
	}

	@DeleteMapping("/info/{accountNumber}")
	@PreAuthorize("hasRole('USER')")
	public DeleteAccount.Response deleteAccount(@PathVariable String accountNumber,
		@RequestHeader("Authorization") String token,
		@RequestBody @Valid DeleteAccount.Request request) {

		return DeleteAccount.Response.from(
			accountService.deleteAccount(accountNumber, token, request));
	}

	/*
		계좌 조회 (회원이 생성한 계좌를 전부 확인할 수 있는 조회)
	 */
	@GetMapping("/list")
	@PreAuthorize("hasRole('USER')")
	public List<AccountInfo> getAccountByUserId(@RequestHeader("Authorization") String token,
		@RequestParam("user_id") UUID userId) {

		return accountService.getAccountsByUserId(token, userId).stream().map(
			accountDto -> AccountInfo.builder().accountNumber(accountDto.getAccountNumber())
				.balance(accountDto.getBalance()).accountStatus(accountDto.getAccountStatus())
				.build()).collect(Collectors.toList());
	}

	/*
	 	계좌 정보 수정 (계좌 비밀번호 수정만 가능하다는 전제 하에)
	 */
	@PutMapping("/info")
	@PreAuthorize("hasRole('USER')")
	public ModifyAccount.Response modifyAccountInfo(@RequestHeader("Authorization") String token,
		@RequestBody @Valid ModifyAccount.Request request) {
		return ModifyAccount.Response.from(accountService.modifyAccountInfo(token, request));
	}

}