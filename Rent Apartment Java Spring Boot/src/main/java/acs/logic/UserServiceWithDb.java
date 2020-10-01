package acs.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import acs.boundaries.UserBoundary;
import acs.boundaries.UserType;
import acs.dal.UserDao;
import acs.data.BoundaryEntityConverter;
import acs.data.EmailValidator;
import acs.data.UserEntity;
import acs.logic.exceptions.EnumTypeWrongException;
import acs.logic.exceptions.InputWrongException;
import acs.logic.exceptions.NotFoundException;
import acs.logic.exceptions.PermissionDeniedException;
import acs.logic.serviceInterfaces.EnhancedUserService;

@Service
public class UserServiceWithDb implements EnhancedUserService{
	private UserDao userDao;
	private BoundaryEntityConverter userEntityConverter; 

	@Autowired
	public UserServiceWithDb(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setUserEntityCoverter(BoundaryEntityConverter userEntityConverter) {
		this.userEntityConverter = userEntityConverter;
	}
	
		
	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {
		if (user.getRole() != null 
				&& user.getRole() == UserType.ADMIN 
				&& user.getRole() == UserType.PLAYER 
				&& user.getRole() == UserType.MANAGER) {
			throw new EnumTypeWrongException("role is wrong");
		}
		UserEntity entity = this.userEntityConverter.userBoundaryToEntity(user);
		this.userDao.save(entity);
		return this.userEntityConverter.userEntityToBoundary(entity);		
	}

	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String userEmail) {
		EmailValidator.validateEmail(userEmail);
		Optional<UserEntity> entity = this.userDao.findById(userEmail);
		if(entity.isPresent()) {
			return this.userEntityConverter.userEntityToBoundary(entity.get());
		} else {
			throw new NotFoundException("could not find user for mail: " + userEmail);
		}		
	}

	@Override
	@Transactional
	public UserBoundary updateUser(String userEmail, UserBoundary update) {
		EmailValidator.validateEmail(userEmail);
		if (update.getEmail()!= null && update.getEmail().equals(userEmail) == false) {
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
				&& (update.getRole() == UserType.ADMIN 
				|| update.getRole() == UserType.PLAYER 
				|| update.getRole() == UserType.MANAGER)) {
			existing.setRole(update.getRole());
			dirty = true;
		}
		if (dirty) {
			this.userDao.save(this.userEntityConverter.userBoundaryToEntity(existing));
		}
		return existing;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminEmail) {
		UserBoundary userBoundary = login(adminEmail);
		if (userBoundary.getRole() == UserType.ADMIN) {
			EmailValidator.validateEmail(adminEmail);
			List<UserBoundary> rv = new ArrayList<>();
			Iterable<UserEntity> content = this.userDao.findAll();
			for (UserEntity user : content) {
				rv.add(this.userEntityConverter.userEntityToBoundary(user));
			}
			return rv;
		}
		throw new PermissionDeniedException("Only admin can get all users");
	}

	@Override
	@Transactional
	public void deleteAllUsers(String adminEmail) {
		UserBoundary userBoundary = login(adminEmail);
		if (userBoundary.getRole() == UserType.ADMIN) {
			EmailValidator.validateEmail(adminEmail);
			this.userDao.deleteAll();
		} else {
			throw new PermissionDeniedException("Only admin can delete all users");
		}
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminEmail, int page, int size) {
		UserBoundary userBoundary = login(adminEmail);
		if (userBoundary.getRole() == UserType.ADMIN) {
			return this.userDao.findAll(PageRequest.of(page, size, Direction.ASC, "email")).getContent().stream()
					.map(this.userEntityConverter::userEntityToBoundary).collect(Collectors.toList());
		} else {
			throw new PermissionDeniedException("Only admin can get all users");
		}
	}
}
