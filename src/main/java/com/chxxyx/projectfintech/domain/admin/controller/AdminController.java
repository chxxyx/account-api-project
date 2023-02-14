package com.chxxyx.projectfintech.domain.admin.controller;

import com.chxxyx.projectfintech.domain.account.model.UserAccountInfo;
import com.chxxyx.projectfintech.domain.admin.model.ChangeAccountStatus;
import com.chxxyx.projectfintech.domain.admin.model.ChangeUserType;
import com.chxxyx.projectfintech.domain.admin.service.AdminService;
import com.chxxyx.projectfintech.domain.admin.model.ChangeAccountStatus.Response;
import com.chxxyx.projectfintech.domain.user.model.UserInfo;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;

	// 회원 정보 조회
	@GetMapping("/user/Info")
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserInfo> getUserInfo() {

		return adminService.getUserInfo().stream().map(
			userDto -> UserInfo.builder().SSN(userDto.getSsn()).name(userDto.getName())
				.username(userDto.getUsername()).password(userDto.getPassword())
				.createdAt(userDto.getCreatedAt()).modifiedAt(userDto.getModifiedAt())
				.role(userDto.getRole()).build()).collect(Collectors.toList());
	}

	// 회원 계좌 정보 조회
	@GetMapping("/account/Info")
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserAccountInfo> getUserAccountInfo() {

		return adminService.getUserAccountInfo().stream().map(
			accountDto -> UserAccountInfo.builder().username(accountDto.getUsername())
				.accountNumber(accountDto.getAccountNumber())
				.accountPassword(accountDto.getAccountPassword()).name(accountDto.getName())
				.build()).collect(Collectors.toList());
	}

	// 회원 상태 변경
	@PutMapping("/user/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ChangeUserType.Response changeUserType(
		@RequestBody @Valid ChangeUserType.Request request) {

		return ChangeUserType.Response.from(
			adminService.changeUserType(request.getUsername(), request.getRole()));
	}

	// 회원 계좌 상태 변경
	@PutMapping("/account/status")
	@PreAuthorize("hasRole('ADMIN')")
	public Response changeAccountStatus(
		@RequestBody @Valid ChangeAccountStatus.Request request) {

		return adminService.changeAccountStatus(request.getUsername(), request.getAccountNumber(),
			request.getAccountStatus());
	}

}
