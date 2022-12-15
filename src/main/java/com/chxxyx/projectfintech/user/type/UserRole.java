package com.chxxyx.projectfintech.user.type;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum UserRole {
	ROLE_ADMIN("관리자"),
	ROLE_USER("사용자");

	UserRole(String value) {
		this.value = value;
	}

	private String value;
}
