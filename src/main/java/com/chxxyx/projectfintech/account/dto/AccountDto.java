package com.chxxyx.projectfintech.account.dto;

import com.chxxyx.projectfintech.account.entity.Account;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccountDto {

	private String accountNumber;
	private Long balance;
	private String username;

	private LocalDateTime registeredAt;
	private LocalDateTime unRegisteredAt;
	public static AccountDto fromEntity(Account account) {
		return AccountDto.builder()
			.username(account.getAccountUser().getUsername())
			.accountNumber(account.getAccountNumber())
			.balance(account.getBalance())
			.registeredAt(account.getRegisteredAt())
			.unRegisteredAt(account.getUnRegisteredAt())
			.build();
	}
}
