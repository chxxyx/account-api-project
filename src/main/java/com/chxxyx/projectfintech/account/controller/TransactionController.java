package com.chxxyx.projectfintech.account.controller;

import com.chxxyx.projectfintech.account.dto.DepositBalance;
import com.chxxyx.projectfintech.account.exception.AccountException;
import com.chxxyx.projectfintech.account.service.TransactionService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
	private final TransactionService transactionService;

	@PostMapping("/transaction/use")
	public DepositBalance.Response depositBalance(@RequestBody @Valid DepositBalance.Request request) {
		try {
			return DepositBalance.Response.from(
				transactionService.depositBalance(request.getUsername(), request.getPassword(),
					request.getAccountNumber(), request.getAccountPassword(), request.getAmount())
			);
		} catch (AccountException e) {
			transactionService.saveFailedDepositTransaction(
				request.getAccountNumber(),
				request.getAmount()
			);
			throw e;
		}
	}
}
