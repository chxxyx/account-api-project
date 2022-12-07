package com.chxxyx.projectfintech.user.service;

import com.chxxyx.projectfintech.user.repository.UserRepository;
import com.chxxyx.projectfintech.user.dto.UserDto;
import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.type.UserRole;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;

	public boolean userRegister(UserDto parameter) {

		Optional<User> optionalMember = userRepository.findByUsername(parameter.getUsername());

		if(optionalMember.isPresent()) {
			// 현재 userid에 해당하는 데이터가 존재ㅣ
			return false;
		}


		String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());
		String encSSN = BCrypt.hashpw(parameter.getSSN(), BCrypt.gensalt());

		User user = User.builder()
			.id(UUID.randomUUID())
			.SSN(encSSN)
			.name(parameter.getName())
			.password(encPassword)
			.createdAt(LocalDateTime.now())
			.username(parameter.getUsername())
			.role(UserRole.ROLE_MEMBER)
			.build();


		userRepository.save(user);

		return true;

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> userOptional = this.userRepository.findByUsername(username);
		if (userOptional.isEmpty()) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}
		User user = userOptional.get();
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		if(UserRole.ROLE_ADMIN.equals(user.getRole())) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
	}

}
