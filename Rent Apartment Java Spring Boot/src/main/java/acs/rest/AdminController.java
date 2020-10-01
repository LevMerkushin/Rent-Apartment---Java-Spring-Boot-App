package acs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ActionBoundary;
import acs.boundaries.UserBoundary;
import acs.data.EmailValidator;
import acs.logic.serviceInterfaces.EnhancedActionService;
import acs.logic.serviceInterfaces.EnhancedElementService;
import acs.logic.serviceInterfaces.EnhancedUserService;

@RestController
public class AdminController {
	private EnhancedActionService actionService;
	private EnhancedElementService elementService;
	private EnhancedUserService userService;
	
	@Autowired
	public AdminController(EnhancedUserService userService, EnhancedActionService actionService,EnhancedElementService elementService) {
		super();
		this.userService = userService;
		this.actionService = actionService;
		this.elementService = elementService;
	}

	
/***------------GET METHODS------------***/
	
	@RequestMapping(
			path = "acs/admin/users/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportAllUsers(
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "page", required=false, defaultValue="0") int page,
			@RequestParam(name = "size", required=false, defaultValue="10") int size) {
		EmailValidator.validateEmail(adminEmail);
		return this.userService
				.getAllUsers(adminEmail, page, size)
				.toArray(new UserBoundary[0]);
	}
	
	@RequestMapping(path = "acs/admin/actions/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] exportAllActions(
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "page", required=false, defaultValue="0") int page,
			@RequestParam(name = "size", required=false, defaultValue="10") int size) {
		EmailValidator.validateEmail(adminEmail);
		return this.actionService
				.getAllActions(adminEmail, page, size)
				.toArray(new ActionBoundary[0]);
	}
	
	
/***------------DELETE METHODS------------***/

	// DELETE - delete all actions (SQL: delete)
	@RequestMapping(path = "acs/admin/actions/{adminEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllActions(@PathVariable("adminEmail") String adminEmail) {
		EmailValidator.validateEmail(adminEmail);
		this.actionService.deleteAllActions(adminEmail);
	}
	
	// DELETE - delete all users (SQL: delete)
	@RequestMapping(path = "acs/admin/users/{adminEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllAsers (@PathVariable("adminEmail") String adminEmail) {
		EmailValidator.validateEmail(adminEmail);
		this.userService.deleteAllUsers(adminEmail);
	}
	
	// DELETE - delete all elements (SQL: delete)
	@RequestMapping(path = "acs/admin/elements/{adminEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllElements (@PathVariable("adminEmail") String adminEmail) {
		EmailValidator.validateEmail(adminEmail);
		this.elementService.deleteAllElements(adminEmail);
	}

}
