package com.chxxyx.projectfintech.domain.user.model;

import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

	private String ssn;//주민번호
	private String name;//유저 이름
	private String username; //유저 id
	private String password;


	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private UserRole role; // 관리자, 회원 구분

//	public static UserDto fromEntity(User user) {
//		return UserDto.builder().ssn(user.getSSN()).name(user.getName())
//			.username(user.getUsername()).password(user.getPassword())
//			.createdAt(user.getCreatedAt()).modifiedAt(user.getModifiedAt()).role(user.getRole())
//			.build();
//	}

	public static User fromEntity(User entity) {
		return new User(
			entity.getId(),
			entity.getName(),
			entity.getUsername(),
			entity.getPassword(),
			entity.getSSN(),
			entity.getCreatedAt(),
			entity.getModifiedAt(),
			entity.getRole()
		);
	}
}
