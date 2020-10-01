package acs.rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.UserBoundary;
import acs.data.EmailValidator;
import acs.logic.exceptions.InputWrongException;
import acs.logic.serviceInterfaces.UserService;

@RestController
public class UserController {
	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	
/***------------ POST METHOD ------------***/
	
	@RequestMapping(path = "acs/users", method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser(@RequestBody UserBoundary input) {
		EmailValidator.validateEmail(input.getEmail());
		if(input.getAvatar() == null || input.getAvatar().trim().equals(""))
			throw new InputWrongException("Avatar is missing");
		if (input.getUsername() == null || input.getUsername().trim().equals("")) 
			throw new InputWrongException("Username is missing");			
		return this.userService.createUser(input);
	}
	
	
/***------------ PUT METHOD ------------***/

	
	@RequestMapping(path = "acs/users/{email}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUserDetails(
			@PathVariable("email") String email, 
			@RequestBody UserBoundary update) {
		if(update.getAvatar() != null && update.getAvatar().trim().equals(""))
			throw new InputWrongException("Avatar is missing");
		if (update.getUsername() != null && update.getUsername().trim().equals("")) 
			throw new InputWrongException("Username is missing");	
		this.userService.updateUser(email, update);
	}
		
/***------------ GET METHOD ------------***/

	@RequestMapping(path = "acs/users/login/{email}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(@PathVariable("email") String email) {
		return this.userService.login(email);		
	}
}
