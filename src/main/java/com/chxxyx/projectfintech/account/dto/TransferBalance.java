package com.chxxyx.projectfintech.account.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TransferBalance {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotNull
		private String username;

		@NotNull
		private String password;

		@NotBlank
		private String senderName;

		@NotBlank
		@Size(min = 10, max = 10)
		private String senderAccountNumber;

		@NotBlank
		private String accountPassword;

		@NotBlank
		private String receiverName;

		@NotBlank
		@Size(min = 10, max = 10)
		private String receiverAccountNumber;

		@NotNull
		@Min(10)
		@Max(1000_000_000)
		private Long amount;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response { //이너 클래스
		private String senderName;
		private String senderAccountNumber;
		private String receiverName;
		private String receiverAccountNumber;
		private Long amount;
		private LocalDateTime transactedAt;

		public static Response from(TransferDto transferDto){
			return Response.builder()
				.senderName(transferDto.getSenderName())
				.senderAccountNumber(transferDto.getSenderAccountNumber())
				.receiverName(transferDto.getReceiverName())
				.receiverAccountNumber(transferDto.getReceiverAccountNumber())
				.amount(transferDto.getAmount())
				.transactedAt(transferDto.getTransactedAt())
				.build();
		}
	}

}
