package com.meetu.model.matching;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MatchingId implements Serializable{

	private Integer userId;
    private Integer userPreferId;
    
	@Override
	public int hashCode() {
		return Objects.hash(userId, userPreferId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchingId other = (MatchingId) obj;
		return Objects.equals(userId, other.userId) && Objects.equals(userPreferId, other.userPreferId);
	}
	@Override
	public String toString() {
		return "MatchingId [userId=" + userId + ", userPreferId=" + userPreferId + "]";
	}



}
