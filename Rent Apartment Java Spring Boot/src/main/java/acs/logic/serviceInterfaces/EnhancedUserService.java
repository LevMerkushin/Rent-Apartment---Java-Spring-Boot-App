package acs.logic.serviceInterfaces;

import java.util.List;

import acs.boundaries.UserBoundary;

public interface EnhancedUserService extends UserService{

	List<UserBoundary> getAllUsers(String adminEmail, int page, int size);

}
