package com.chxxyx.projectfintech.domain.user.service;

import com.chxxyx.projectfintech.config.jwt.TokenProvider;
import com.chxxyx.projectfintech.domain.user.model.RegisterUser;
import com.chxxyx.projectfintech.domain.user.model.UserDto;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import com.chxxyx.projectfintech.domain.user.model.LoginUser;
import com.chxxyx.projectfintech.domain.user.exception.UserError;
import com.chxxyx.projectfintech.domain.user.exception.UserException;
import com.chxxyx.projectfintech.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	public UserDto registerUser(RegisterUser.Request parameter) {
		Optional<User> optionalUser = userRepository.findByUsername(parameter.getUsername());

		if (optionalUser.isPresent()) {
			// 현재 userid에 해당하는 데이터가 존재ㅣ
			throw new UserException(UserError.USER_ALREADY_REGISTER);
		}

		String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());
		String encSsn = BCrypt.hashpw(parameter.getSsn(), BCrypt.gensalt());

		return UserDto.fromEntity(userRepository.save(
			User.builder().username(parameter.getUsername()).password(encPassword).SSN(encSsn)
				.name(parameter.getName()).createdAt(LocalDateTime.now()).role(UserRole.ROLE_USER)
				.build()));

	}

	public User login(LoginUser parameter) {
		User user = userRepository.findByUsername(parameter.getUsername())
			.orElseThrow(() -> new RuntimeException("존재 하지 않는 ID 입니다."));

		if (!passwordEncoder.matches(parameter.getPassword(), user.getPassword())) {
			throw new RuntimeException("비밀번호가 일치 하지 않습니다.");
		}

		return user;
	}

}
