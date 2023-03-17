package com.chxxyx.projectfintech.domain.user.repository;

import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.model.UserDto;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {


	private final RedisTemplate<String, UserDto> userRedisTemplate;
	private final static Duration USER_CACHE_TTL = Duration.ofDays(3); //TTL -> 캐시 정보 3일 간 보관

	public void setUser(UserDto user) {
		String key = getKey(user.getUsername());
		log.info("Set User to Redis {}, {}", key, user);
		userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
	}

	public Optional<UserDto> getUser(String userName) {
		String key = getKey(userName);
		UserDto user = userRedisTemplate.opsForValue().get(key);
		log.info("Get data from Redis {} , {}", key, user);
		return Optional.ofNullable(user); // 처음 저장할 때는 유저의 캐시 정보가 없으므로 널 처리
	}

	private String getKey(String userName) {
		return "USER:" + userName;
	}

}
