package acs.logic.mockup;

import java.util.Collections;
import acs.data.EmailValidator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acs.boundaries.UserBoundary;
import acs.boundaries.UserType;
import acs.data.BoundaryEntityConverter;
import acs.data.UserEntity;
import acs.logic.exceptions.EnumTypeWrongException;
import acs.logic.exceptions.InputWrongException;
import acs.logic.exceptions.NotFoundException;
import acs.logic.serviceInterfaces.UserService;

//@Service
public class UserServiceMockup implements UserService{
	private Map<String, UserEntity> userDatabase;
	private BoundaryEntityConverter userEntityConverter; 
	
	@Autowired
	public void setUserEntityCoverter(BoundaryEntityConverter userEntityConverter) {
		this.userEntityConverter = userEntityConverter;
	}

	//after build the object instance (constructor) , this method will work 
	@PostConstruct
	public void init() {
		this.userDatabase = Collections.synchronizedMap(new TreeMap<>());
	}

	@Override
	public UserBoundary createUser(UserBoundary user) {
		EmailValidator.validateEmail(user.getEmail());
		if (user.getRole() != UserType.ADMIN || user.getRole() != UserType.PLAYER 
				|| user.getRole() != UserType.MANAGER || user.getRole() != null) {
			throw new EnumTypeWrongException("role is wrong");
		}
		UserEntity entity = this.userEntityConverter.userBoundaryToEntity(user);
		this.userDatabase.put(user.getEmail(), entity);
		return this.userEntityConverter.userEntityToBoundary(entity);
		
	}

	@Override
	public UserBoundary login(String userEmail) {
		EmailValidator.validateEmail(userEmail);
		UserEntity entity = this.userDatabase.get(userEmail);
		if (entity != null) {
			return this.userEntityConverter.userEntityToBoundary(entity);
		} else {
			throw new NotFoundException ("could not find user for mail: " + userEmail);
		}
	}

	@Override
	public UserBoundary updateUser(String userEmail, UserBoundary update) {
		EmailValidator.validateEmail(userEmail);
		if (update.getEmail().equals(userEmail) == false) {
			throw new InputWrongException("Could not change user email");
		}
		UserBoundary existing = this.login(userEmail);
		boolean dirty = false;
		if (update.getAvatar() != null) {
			existing.setAvatar(update.getAvatar());
			dirty = true;
		}
		if (update.getUsername() != null) {
			existing.setUsername(update.getUsername());
			dirty = true;
		}
 		if (update.getRole() != null
			&& (update.getRole() == UserType.ADMIN ||
			    update.getRole() == UserType.PLAYER ||
			    update.getRole() == UserType.MANAGER)) {
			existing.setRole(update.getRole());
			dirty = true;
		}
		if (dirty) {
			this.userDatabase.put(userEmail, this.userEntityConverter.userBoundaryToEntity(existing));
		}
		return existing;
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminEmail) {
		EmailValidator.validateEmail(adminEmail);
		return this.userDatabase.values().stream()
					.map(entity -> this.userEntityConverter.userEntityToBoundary(entity)).collect(Collectors.toList());
	}

	@Override
	public void deleteAllUsers(String adminEmail) {
		EmailValidator.validateEmail(adminEmail);
		this.userDatabase.clear();
	}
}
