package com.chxxyx.projectfintech.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountError {
	USER_NOT_FOUND("사용자가 없습니다."),
	MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개입니다"),
	ACCOUNT_NOT_FOUND("계좌가 없습니다."),
	USER_ACCOUNT_UN_MATCH("사용자와 계좌의 소유주가 다릅니다."),
	ACCOUNT_ALREADY_UNREGISTERED("이미 해지된 계좌입니다."),
	INVALID_REQUEST("잘못된 요청입니다.")
	;

	private final String description;
}
