package com.chxxyx.projectfintech.domain.user.repository;

import com.chxxyx.projectfintech.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, UUID>{
	Optional<User> findByUsername(String username);
	Optional<User> findByName(String name);

	Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
