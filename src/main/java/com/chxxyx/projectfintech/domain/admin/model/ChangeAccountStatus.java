package com.chxxyx.projectfintech.domain.admin.model;

import com.chxxyx.projectfintech.domain.account.entity.Account;
import com.chxxyx.projectfintech.domain.account.type.AccountStatus;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ChangeAccountStatus {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank
		private String accountNumber;
		@NotBlank
		private String username;
		@NotNull
		private AccountStatus accountStatus;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response { //이너 클래스

		private String username;
		private String name;
		private String accountNumber;
		private AccountStatus accountStatus;

		public static ChangeAccountStatus.Response from(Account account) {

			return Response.builder()
				.username(account.getAccountUser().getUsername())
				.name(account.getAccountUser().getName())
				.accountNumber(account.getAccountNumber())
				.accountStatus(account.getAccountStatus())
				.build();
		}
	}
}
