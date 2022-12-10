package com.chxxyx.projectfintech.account.entity;

import com.chxxyx.projectfintech.account.type.AccountStatus;
import com.chxxyx.projectfintech.user.entity.User;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {

	@Id
	private String accountNumber;
	private Long balance;
	private String accountPassword;
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;

	@ManyToOne
    private User accountUser;

	private LocalDateTime registeredAt;
	private LocalDateTime unRegisteredAt;

	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime modifiedAt;
}
