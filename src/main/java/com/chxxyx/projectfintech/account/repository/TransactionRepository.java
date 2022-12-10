package com.chxxyx.projectfintech.account.repository;

import com.chxxyx.projectfintech.account.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long>{

}
