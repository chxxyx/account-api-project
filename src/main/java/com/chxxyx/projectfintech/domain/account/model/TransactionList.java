package com.chxxyx.projectfintech.domain.account.model;

import com.chxxyx.projectfintech.domain.account.type.TransactionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

public class TransactionList {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank
		@Size(min = 10, max = 10)
		private String accountNumber;

		@NotBlank
		private String accountPassword;

		@DateTimeFormat(pattern = "yyyy-MM-dd")
		private LocalDate startDate;

		@DateTimeFormat(pattern = "yyyy-MM-dd")
		private LocalDate endDate;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {

		private String accountNumber;
		private Long amount;
		private Long transactionId;
		private TransactionType transactionType;
		private LocalDateTime transactedAt;

		public static Response from(TransactionDto transactionDto) {
			return Response.builder()
				.accountNumber(transactionDto.getAccountNumber())
				.amount(transactionDto.getAmount())
				.transactionId(transactionDto.getTransactionId())
				.transactionType(transactionDto.getTransactionType())
				.transactedAt(transactionDto.getTransactedAt())
				.build();

		}
	}
}