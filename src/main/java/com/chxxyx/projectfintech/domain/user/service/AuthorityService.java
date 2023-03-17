package com.chxxyx.projectfintech.domain.user.service;

import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.exception.UserError;
import com.chxxyx.projectfintech.domain.user.exception.UserException;
import com.chxxyx.projectfintech.domain.user.model.LoginUser;
import com.chxxyx.projectfintech.domain.user.model.UserDto;
import com.chxxyx.projectfintech.domain.user.repository.UserCacheRepository;
import com.chxxyx.projectfintech.domain.user.repository.UserRepository;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorityService implements UserDetailsService{
	private final UserRepository userRepository;
	private final UserCacheRepository userCacheRepository;

//	public UserDto loadUserByUsername(String username) {
////		return
////			userCacheRepository.getUser(username).orElseGet(()->
////				UserDto.fromEntity(userRepository.findByUsername(username)
////				.orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username))));
//
//		return userCacheRepository.getUser(username).orElseGet(() ->
//			(userRepository.findByUsername(username).map(UserDto::fromEntity)
//			.orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND"))));
//	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user =

//			userCacheRepository.getUser(username).orElseGet(()->
			userRepository.findByUsername(username).orElseThrow(()->
				new UserException(UserError.USER_NOT_FOUND))
//		)
		;

		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		if (UserRole.ROLE_ADMIN.equals(user.getRole())) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(),
			user.getPassword(), authorities);
	}

}
