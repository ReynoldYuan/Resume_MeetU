package com.meetu.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users_ban")
public class UsersBan {

	@EmbeddedId
	private UsersBanId id;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "userId", referencedColumnName = "userId")
	private UsersProfile userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("banedUserId")
    @JoinColumn(name = "banedUserId", referencedColumnName = "userId")
    private UsersProfile banedUserId;

	public UsersBan() {
	}

	public UsersBanId getId() {
		return id;
	}

	public void setId(UsersBanId id) {
		this.id = id;
	}

	public UsersProfile getUserId() {
		return userId;
	}

	public void setUserId(UsersProfile userId) {
		this.userId = userId;
	}

	public UsersProfile getBanedUserId() {
		return banedUserId;
	}

	public void setBanedUserId(UsersProfile banedUserId) {
		this.banedUserId = banedUserId;
	}
		
	
	
}
