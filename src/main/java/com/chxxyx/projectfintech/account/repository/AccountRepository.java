package com.chxxyx.projectfintech.account.repository;

import com.chxxyx.projectfintech.account.entity.Account;
import com.chxxyx.projectfintech.user.entity.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository <Account, String> {
	Optional<Account> findFirstByOrderByAccountNumberDesc();
	Integer countByAccountUser(User accountUser);

	Optional<Account> findByAccountNumber(String accountNumber);

	List<Account> findByAccountUser(User accountUser);

}
