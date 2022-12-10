package com.chxxyx.projectfintech.account.dto;

import com.chxxyx.projectfintech.account.dto.CreateAccount.Response;
import com.chxxyx.projectfintech.account.entity.Transaction;
import com.chxxyx.projectfintech.account.type.TransactionResultType;
import java.time.LocalDateTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DepositBalance {

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
		@Size(min = 10, max = 10)
		private String accountNumber;

		@NotBlank
		private String accountPassword;

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

		private String accountNumber;
		private TransactionResultType transactionResult;
		private Long amount;
		private LocalDateTime transactedAt;

		public static Response from(TransactionDto transactionDto) {
			return Response.builder()
				.accountNumber(transactionDto.getAccountNumber())
				.transactionResult(transactionDto.getTransactionResultType())
				.amount(transactionDto.getAmount())
				.transactedAt(transactionDto.getTransactedAt())
				.build();
		}
	}

}
