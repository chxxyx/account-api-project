package com.chxxyx.projectfintech.account.dto;

import com.chxxyx.projectfintech.account.entity.Transfer;
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
public class TransferDto {

	private String senderName;
	private String senderAccountNumber;

	private String receiverName;
	private String receiverAccountNumber;

	private LocalDateTime transactedAt;

	private Long amount;

	public static TransferDto fromEntity(Transfer transfer) {
		return TransferDto.builder()
			.senderName(transfer.getSenderName())
			.senderAccountNumber(transfer.getSenderAccountNumber())
			.amount(transfer.getTransaction().getAmount())
			.receiverName(transfer.getReceiverName())
			.receiverAccountNumber(transfer.getReceiverAccountNumber())
			.transactedAt(transfer.getTransaction().getTransactedAt())
			.build();
	}
}
