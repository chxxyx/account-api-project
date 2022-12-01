package com.chxxyx.projectfintech.user.Repository;

import com.chxxyx.projectfintech.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, UUID>{

}
