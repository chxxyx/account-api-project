package com.chxxyx.projectfintech.domain.user.type;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum UserRole {
	ROLE_ADMIN("관리자"),
	ROLE_USER("회원"),
	ROLE_NOT_USER("탈퇴 회원");

	UserRole(String value) {
		this.value = value;
	}

	private String value;
}
