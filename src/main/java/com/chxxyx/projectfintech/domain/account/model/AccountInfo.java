package com.chxxyx.projectfintech.domain.account.model;

import com.chxxyx.projectfintech.domain.account.type.AccountStatus;
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
public class AccountInfo {

	private String accountNumber;
	private Long balance;
	private AccountStatus accountStatus;

}
