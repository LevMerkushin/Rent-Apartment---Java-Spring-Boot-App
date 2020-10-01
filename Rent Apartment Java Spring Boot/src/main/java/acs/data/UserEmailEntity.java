package acs.data;

import javax.persistence.Embeddable;

@Embeddable
public class UserEmailEntity {
	private String email;

	public UserEmailEntity() {
	}
	
	public UserEmailEntity(String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserEmailEntity [email=" + email + "]";
	}
}
