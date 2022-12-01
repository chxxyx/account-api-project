package com.chxxyx.projectfintech.user.service;

import com.chxxyx.projectfintech.user.Repository.UserRepository;
import com.chxxyx.projectfintech.user.dto.UserDto;
import com.chxxyx.projectfintech.user.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public boolean userRegister(UserDto parameter) {


		String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());
		String encSSN = BCrypt.hashpw(parameter.getSSN(), BCrypt.gensalt());

		User user = User.builder()
			.id(UUID.randomUUID())
			.SSN(encSSN)
			.userName(parameter.getUserName())
			.password(encPassword)
			.createdAt(LocalDateTime.now())
			.emailId(parameter.getEmailId())
			.emailAuthYn(false)
			.userType(User.USER)
			.build();

		userRepository.save(user);

		return true;

	}

}
