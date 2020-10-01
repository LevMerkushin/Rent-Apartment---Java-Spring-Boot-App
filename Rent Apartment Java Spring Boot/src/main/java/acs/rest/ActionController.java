package acs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ActionBoundary;
import acs.data.EmailValidator;
import acs.logic.exceptions.InputWrongException;
import acs.logic.serviceInterfaces.ActionService;

@RestController
public class ActionController {
	private ActionService actionService;
	
	@Autowired
	public ActionController(ActionService actionService) {
		super();
		this.actionService = actionService;
	}

	@RequestMapping(path = "/acs/actions", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeAction (@RequestBody ActionBoundary input) {
		if(input.getType() == null || input.getType().trim().equals(""))
			throw new InputWrongException("Action type is missing");
		if(input.getElement() == null || input.getElement().getElementId().trim().equals(""))
			throw new InputWrongException("Element id that action works on is missing");
		if(input.getInvokedBy() == null || input.getInvokedBy().getEmail().trim().equals("")) { 
			throw new InputWrongException("Email that action was invoked by is missing");
		} else {
			EmailValidator.validateEmail(input.getInvokedBy().getEmail());
		}
		return this.actionService.invokeAction(input);
	}

}
