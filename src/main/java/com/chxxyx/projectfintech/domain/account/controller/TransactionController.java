package com.chxxyx.projectfintech.domain.account.controller;

import com.chxxyx.projectfintech.domain.account.model.DepositBalance;
import com.chxxyx.projectfintech.domain.account.model.TransactionList;
import com.chxxyx.projectfintech.domain.account.model.TransferBalance;
import com.chxxyx.projectfintech.domain.account.model.WithdrawBalance;
import com.chxxyx.projectfintech.domain.account.exception.AccountException;
import com.chxxyx.projectfintech.domain.account.service.DepositWithdrawService;
import com.chxxyx.projectfintech.domain.account.model.DepositBalance.Response;
import com.chxxyx.projectfintech.domain.account.service.TransferService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

	private final DepositWithdrawService depositWithdrawService;
	private final TransferService transferService;

	@PostMapping("/deposit")
	@PreAuthorize("hasRole('USER')")
	public Response depositBalance(@RequestHeader("Authorization") String token,
		@RequestBody @Valid DepositBalance.Request request) {
		try {
			return DepositBalance.Response.from(
				depositWithdrawService.depositBalance(token, request));
		} catch (AccountException e) {
			depositWithdrawService.saveFailedDepositTransaction(request.getAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@PostMapping("/withdraw")
	@PreAuthorize("hasRole('USER')")
	public WithdrawBalance.Response withdrawBalance(@RequestHeader("Authorization") String token,
		@RequestBody @Valid WithdrawBalance.Request request) {
		try {
			log.info("비밀번호   " + request.getAccountPassword());
			return WithdrawBalance.Response.from(
				depositWithdrawService.withdrawBalance(token, request));
		} catch (AccountException e) {
			depositWithdrawService.saveFailedWithdrawTransaction(request.getAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@PostMapping("/transfer")
	@PreAuthorize("hasRole('USER')")
	public TransferBalance.Response transferBalance(@RequestHeader("Authorization") String token,
		@RequestBody @Valid TransferBalance.Request request) {
		try {
			return TransferBalance.Response.from(
				transferService.transferBalance(token, request));
		} catch (AccountException e) {
			transferService.saveFailedTransfer(request.getSenderAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	/*
		거래내역 조회
 	*/
	@GetMapping("/list")
	@PreAuthorize("hasRole('USER')")
	public List<TransactionList.Response> getTransactionList(@RequestHeader("Authorization") String token,
		@RequestBody @Valid TransactionList.Request request) {

		return depositWithdrawService.getTransactionList(token, request).stream().map(
			transactionDto -> TransactionList.Response.builder().accountNumber(transactionDto.getAccountNumber())
				.amount(transactionDto.getAmount()).transactionId(transactionDto.getTransactionId())
				.transactionType(transactionDto.getTransactionType()).transactedAt(transactionDto.getTransactedAt())
				.build()).collect(Collectors.toList());
	}

}
