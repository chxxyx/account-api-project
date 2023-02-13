package com.chxxyx.projectfintech.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserError {

	USER_ALREADY_REGISTER("이미 등록된 유저 입니다.");

	private final String description;
}
