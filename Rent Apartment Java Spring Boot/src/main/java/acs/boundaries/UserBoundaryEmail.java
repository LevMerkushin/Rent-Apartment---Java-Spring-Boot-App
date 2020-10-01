package acs.boundaries;

public class UserBoundaryEmail {
	private String email;

	public UserBoundaryEmail() {
	}
	
	public UserBoundaryEmail(String email) {
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
		return "UserBoundaryEmail [email=" + email + "]";
	}
	
	
}
