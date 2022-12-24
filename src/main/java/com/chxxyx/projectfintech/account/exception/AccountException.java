package com.chxxyx.projectfintech.account.exception;

import com.chxxyx.projectfintech.account.type.AccountError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountException extends RuntimeException {

	private AccountError error;
	private String errorMessage;

	public AccountException(AccountError error) {
		super(error.getDescription());
		this.error = error;
	}
}
