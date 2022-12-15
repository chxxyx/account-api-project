package com.chxxyx.projectfintech.account.repository;

import com.chxxyx.projectfintech.account.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

}
