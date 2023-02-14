package com.chxxyx.projectfintech.domain.account.controller;

import com.chxxyx.projectfintech.domain.account.model.DepositBalance;
import com.chxxyx.projectfintech.domain.account.model.TransactionList;
import com.chxxyx.projectfintech.domain.account.model.TransferBalance;
import com.chxxyx.projectfintech.domain.account.model.WithdrawBalance;
import com.chxxyx.projectfintech.domain.account.exception.AccountException;
import com.chxxyx.projectfintech.domain.account.service.TransactionService;
import com.chxxyx.projectfintech.domain.account.model.DepositBalance.Response;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping("/deposit")
	@PreAuthorize("hasRole('USER')")
	public Response depositBalance(
		@RequestBody @Valid DepositBalance.Request request) {
		try {
			return DepositBalance.Response.from(
				transactionService.depositBalance(request.getUsername(), request.getPassword(),
					request.getAccountNumber(), request.getAccountPassword(), request.getAmount()));
		} catch (AccountException e) {
			transactionService.saveFailedDepositTransaction(request.getAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@PostMapping("/withdraw")
	@PreAuthorize("hasRole('USER')")
	public WithdrawBalance.Response withdrawBalance(
		@RequestBody @Valid WithdrawBalance.Request request) {
		try {
			log.info("비밀번호   " + request.getAccountPassword());
			return WithdrawBalance.Response.from(
				transactionService.withdrawBalance(request.getUsername(), request.getPassword(),
					request.getAccountNumber(), request.getAccountPassword(), request.getAmount()));
		} catch (AccountException e) {
			transactionService.saveFailedWithdrawTransaction(request.getAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@PostMapping("/transfer")
	@PreAuthorize("hasRole('USER')")
	public TransferBalance.Response transferBalance(
		@RequestBody @Valid TransferBalance.Request request) {
		try {
			return TransferBalance.Response.from(
				transactionService.transferBalance(request.getUsername(), request.getPassword(),
					request.getSenderName(), request.getSenderAccountNumber(),
					request.getAccountPassword(), request.getReceiverName(),
					request.getReceiverAccountNumber(), request.getAmount()));
		} catch (AccountException e) {
			transactionService.saveFailedTransfer(request.getSenderAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@GetMapping("/list")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getTransactionList(
		@RequestBody @Valid TransactionList.Request request) {
		return new ResponseEntity<>(
			transactionService.getTransactionList(request.getUsername(), request.getPassword(),
				request.getAccountNumber(), request.getAccountPassword(), request.getStartDate(),
				request.getEndDate()), HttpStatus.OK);

	}

}
