package com.chxxyx.projectfintech.domain.account.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class DeleteAccount {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank
		private String accountPassword;

	}
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response { //이너 클래스

		private String username;
		private String accountNumber;
		private LocalDateTime unRegisteredAt;

		public static Response from(AccountDto accountDto) {

			return Response.builder()
				.username(accountDto.getUsername())
				.accountNumber(accountDto.getAccountNumber())
				.unRegisteredAt(accountDto.getUnRegisteredAt())
				.build();
		}
	}
}
