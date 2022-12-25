package com.chxxyx.projectfintech.admin.controller;

import com.chxxyx.projectfintech.account.dto.UserAccountInfo;
import com.chxxyx.projectfintech.admin.dto.ChangeAccountStatus;
import com.chxxyx.projectfintech.admin.dto.ChangeUserType;
import com.chxxyx.projectfintech.admin.service.AdminService;
import com.chxxyx.projectfintech.user.dto.UserInfo;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	// 회원 정보 조회
	@GetMapping("/admin/user/Info")
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserInfo> getUserInfo() {

		return adminService.getUserInfo().stream().map(
			userDto -> UserInfo.builder().SSN(userDto.getSSN()).name(userDto.getName())
				.username(userDto.getUsername()).password(userDto.getPassword())
				.createdAt(userDto.getCreatedAt()).modifiedAt(userDto.getModifiedAt())
				.role(userDto.getRole()).build()).collect(Collectors.toList());
	}

	// 회원 계좌 정보 조회
	@GetMapping("/admin/account/Info")
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserAccountInfo> getUserAccountInfo() {

		return adminService.getUserAccountInfo().stream().map(
			accountDto -> UserAccountInfo.builder().username(accountDto.getUsername())
				.accountNumber(accountDto.getAccountNumber())
				.accountPassword(accountDto.getAccountPassword()).name(accountDto.getName())
				.build()).collect(Collectors.toList());
	}

	// 회원 상태 변경
	@PostMapping("/admin/user/changeType")
	@PreAuthorize("hasRole('ADMIN')")
	public ChangeUserType.Response changeUserType(
		@RequestBody @Valid ChangeUserType.Request request) {

		return ChangeUserType.Response.from(
			adminService.changeUserType(request.getUsername(), request.getRole()));
	}

	// 회원 계좌 상태 변경
	@PostMapping("/admin/account/changeStatus")
	public ChangeAccountStatus.Response changeAccountStatus(
		@RequestBody @Valid ChangeAccountStatus.Request request) {

		return adminService.changeAccountStatus(request.getUsername(), request.getAccountNumber(),
			request.getAccountStatus());
	}

}
