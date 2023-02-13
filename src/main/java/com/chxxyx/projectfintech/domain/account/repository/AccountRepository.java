package com.chxxyx.projectfintech.domain.account.repository;

import com.chxxyx.projectfintech.domain.account.entity.Account;
import com.chxxyx.projectfintech.domain.user.entity.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

	Optional<Account> findFirstByOrderByAccountNumberDesc();

	Integer countByAccountUser(User accountUser);

	Optional<Account> findByAccountNumber(String accountNumber);

	List<Account> findByAccountUser(User accountUser);

	Page<Account> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
