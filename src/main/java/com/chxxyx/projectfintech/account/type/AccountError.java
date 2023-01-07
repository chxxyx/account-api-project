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
	ACCOUNT_PASSWORD_NOT_SAME("계좌 비밀번호가 다릅니다."),
	ACCOUNT_ALREADY_UNREGISTERED("이미 해지된 계좌입니다."),
	INVALID_REQUEST("잘못된 요청입니다."),
	TRANSACTION_NOT_FOUND("거래 내역을 찾을 수 없습니다."),
	ACCOUNT_EXCEED_BALANCE("출금 금액이 계좌 잔액 보다 큽니다."),
	ACCOUNT_UNDER_BALANCE("입금 금액은 0보다 큰 금액만 입금 가능합니다.");

	private final String description;
}
