package com.chxxyx.projectfintech.domain.account.repository;

import com.chxxyx.projectfintech.domain.account.entity.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long>{

	List<Transaction> findAllByAccount_AccountNumberAndTransactedAtBetween (String accountNumber,
													LocalDateTime startDate, LocalDateTime endDate);
}
