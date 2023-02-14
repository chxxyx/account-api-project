package com.chxxyx.projectfintech.domain.account.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ModifyAccount {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank
		private String accountNumber;
		@NotBlank
		private String accountPassword;
		@NotBlank
		private String accountRePassword;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response { //이너 클래스

		private String username;
		private String accountNumber;
		private String accountPassword;

		public static Response from(AccountDto accountDto) {

			return Response.builder().username(accountDto.getUsername())
				.accountNumber(accountDto.getAccountNumber())
				.accountPassword(accountDto.getAccountPassword()).build();
		}
	}
}
