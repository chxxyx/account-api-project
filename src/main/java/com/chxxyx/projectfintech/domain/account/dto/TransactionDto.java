package com.chxxyx.projectfintech.domain.account.dto;

import com.chxxyx.projectfintech.domain.account.entity.Transaction;
import com.chxxyx.projectfintech.domain.account.type.TransactionResultType;
import com.chxxyx.projectfintech.domain.account.type.TransactionType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

	private String accountNumber;
	private TransactionType transactionType;
	private TransactionResultType transactionResultType;
	private Long amount;
	private Long balanceSnapshot;
	private LocalDateTime transactedAt;

	public static TransactionDto fromEntity(Transaction transaction) {
		return TransactionDto.builder()
			.accountNumber(transaction.getAccount().getAccountNumber())
			.transactionType(transaction.getTransactionType())
			.transactionResultType(transaction.getTransactionResultType())
			.amount(transaction.getAmount())
			.balanceSnapshot(transaction.getBalanceSnapshot())
			.transactedAt(transaction.getTransactedAt())
			.build();
	}
}
