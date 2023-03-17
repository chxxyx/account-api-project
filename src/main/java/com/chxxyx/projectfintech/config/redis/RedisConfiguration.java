package com.chxxyx.projectfintech.config.redis;

import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

	private final RedisProperties redisProperty;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {

		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisProperty.getHost(),
			redisProperty.getPort());
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public RedisTemplate<String, UserDto> userRedisTemplate(
		RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, UserDto> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<UserDto>(UserDto.class));
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

}
