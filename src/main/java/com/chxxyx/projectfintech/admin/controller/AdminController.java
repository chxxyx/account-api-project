package com.chxxyx.projectfintech.admin.controller;

import com.chxxyx.projectfintech.account.dto.UserAccountInfo;
import com.chxxyx.projectfintech.admin.service.AdminService;
import com.chxxyx.projectfintech.user.dto.UserInfo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	// 회원 정보 조회
	@GetMapping("/admin/userInfo")
	public List<UserInfo> getUserInfo() {

		return adminService.getUserInfo().stream().map(
			userDto -> UserInfo.builder().SSN(userDto.getSSN()).name(userDto.getName())
				.username(userDto.getUsername()).password(userDto.getPassword())
				.createdAt(userDto.getCreatedAt()).modifiedAt(userDto.getModifiedAt())
				.role(userDto.getRole()).build()).collect(Collectors.toList());
	}

	// 회원 계좌 정보 조회
	@GetMapping("/admin/userAccountInfo")
	public List<UserAccountInfo> getUserAccountInfo() {

		return adminService.getUserAccountInfo().stream().map(
			accountDto -> UserAccountInfo.builder().username(accountDto.getUsername())
				.accountNumber(accountDto.getAccountNumber())
				.accountPassword(accountDto.getAccountPassword()).name(accountDto.getName())
				.build()).collect(Collectors.toList());
	}

}
