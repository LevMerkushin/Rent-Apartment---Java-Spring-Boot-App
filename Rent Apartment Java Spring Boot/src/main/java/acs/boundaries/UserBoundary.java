package acs.boundaries;

public class UserBoundary {
	private String email;
	private UserType role;
	private String username;
	private String avatar;
	
	//------------ Constructors ------------
	
	public UserBoundary() {
		
	}
	
	public UserBoundary(String email, UserType role, String username, String avatar) {
		super();
		this.email = email;
		this.username = username;
		this.avatar = avatar;
		this.role = role;				
	}

	//------------ Getters & Setters ------------
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public UserType getRole() {
		return role;
	}

	public void setRole(UserType role) {
//		if (role != UserType.ADMIN || role != UserType.PLAYER 
//				|| role != UserType.MANAGER || role != null) {
//			throw new EnumTypeWrongException("role is wrong bitch");
//		}
		this.role = role;
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}
