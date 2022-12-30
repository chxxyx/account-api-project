package com.chxxyx.projectfintech.user.exception;

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
public class UserException extends RuntimeException {

	private UserError userError;
	private String errorMessage;

	public UserException(UserError userError) {
		super(userError.getDescription());
		this.userError = userError;
	}

}
