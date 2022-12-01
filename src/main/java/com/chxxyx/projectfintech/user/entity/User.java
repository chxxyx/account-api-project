package com.chxxyx.projectfintech.user.entity;


import com.chxxyx.projectfintech.user.type.UserType;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class User implements UserType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;// 식별 값

	private String SSN; //주민번호

	private String userName; //유저 이름
	private String password; //비밀번호
	private LocalDateTime createdAt; //회원 생성일
	private LocalDateTime modifiedAt; //회원 정보 수정일

	@Column(unique =true)
	private String emailId; //유저 id
	private Boolean emailAuthYn; //이메일 인증 여부
	private String userType; // 관리자, 회원 구분

}
