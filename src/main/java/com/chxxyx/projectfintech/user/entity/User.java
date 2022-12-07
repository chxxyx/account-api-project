package com.chxxyx.projectfintech.user.entity;


import com.chxxyx.projectfintech.user.type.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;// 식별 값

	private String SSN; //주민번호
	private String name; //유저 이름

	@Column(unique =true)
	private String username; //유저 id
	private String password; //비밀번호
	private LocalDateTime createdAt; //회원 생성일
	private LocalDateTime modifiedAt; //회원 정보 수정일

	@Enumerated(EnumType.STRING)
	private UserRole role; // 관리자, 회원 구분

}
