package acs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ChildIdWrapper;
import acs.boundaries.ElementBoundary;
import acs.data.EmailValidator;
import acs.logic.exceptions.InputWrongException;
import acs.logic.serviceInterfaces.Enhanced2ElementService;

@RestController
public class ElementController {
	private Enhanced2ElementService elementService;
	
	
	@Autowired
	public ElementController(Enhanced2ElementService elementService) {
		super();
		this.elementService = elementService;
	}
	
	
/***------------ GET METHODS ------------***/
	@RequestMapping(path = "/acs/elements/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElements 
				(@PathVariable("userEmail") String userEmail,
				@RequestParam(name = "page", required=false, defaultValue="0") int page,
				@RequestParam(name = "size", required=false, defaultValue="10") int size) {
		EmailValidator.validateEmail(userEmail);
		return this.elementService
				.getAll(userEmail, page, size)
				.toArray(new ElementBoundary[0]);
		}
	
	@RequestMapping(path = "/acs/elements/{userEmail}/{elementId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary getSpecificElement (@PathVariable("userEmail") String userEmail,
			@PathVariable("elementId") String elementId) {
		EmailValidator.validateEmail(userEmail);
		return this.elementService.getSpecificElement(userEmail, elementId);
	}
	
/***------------ POST METHOD ------------***/

	@RequestMapping(path = "/acs/elements/{managerEmail}", method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary createNewElement(@PathVariable("managerEmail") String managerEmail,
			@RequestBody ElementBoundary input) {
		EmailValidator.validateEmail(managerEmail);
		if(input.getName() == null || input.getName().trim().equals(""))
			throw new InputWrongException("Element name is missing");
		if(input.getType() == null || input.getType().trim().equals(""))
			throw new InputWrongException("Element type is missing");
		return this.elementService.create(managerEmail, input);

	}
	
/***------------ PUT METHOD ------------***/

	@RequestMapping(path = "/acs/elements/{managerEmail}/{elementId}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElementDetails (@PathVariable("managerEmail") String managerEmail, 
			@PathVariable("elementId") String elementId,@RequestBody ElementBoundary update) {
		EmailValidator.validateEmail(managerEmail);
		if(update.getName() != null && update.getName().trim().equals(""))
			throw new InputWrongException("Element name is missing");
		if (update.getType() != null && update.getType().trim().equals("")) 
			throw new InputWrongException("Element type is missing");
		this.elementService.update(managerEmail, elementId, update);
	}
	
	/***------------ RELATIONSHIP METHODS ------------***/

	@RequestMapping(path = "/acs/elements/{managerEmail}/{parentElementId}/children",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void addChildToElement(
			@PathVariable("managerEmail") String managerEmail,
			@PathVariable("parentElementId") String parentElementId,
			@RequestBody ChildIdWrapper childId) {
		EmailValidator.validateEmail(managerEmail);
		this.elementService.addChildToElement(managerEmail, parentElementId, childId.getId());
	}
	
		
	@RequestMapping(path = "/acs/elements/{userEmail}/{parentId}/children",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getChildrens(
			@PathVariable("userEmail") String userEmail,
			@PathVariable("parentId") String parentId,
			@RequestParam(name = "page", required=false, defaultValue="0") int page,
			@RequestParam(name = "size", required=false, defaultValue="10") int size) {
		EmailValidator.validateEmail(userEmail);
		return this.elementService.getChildrens(userEmail, parentId, page, size)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/acs/elements/{userEmail}/{childId}/parents",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getParents(
			@PathVariable("userEmail") String userEmail,
			@PathVariable("childId") String childElementId,
			@RequestParam(name = "page", required=false, defaultValue="0") int page,
			@RequestParam(name = "size", required=false, defaultValue="10") int size) {
		EmailValidator.validateEmail(userEmail);
		return this.elementService.getParents(userEmail, childElementId, page, size)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/acs/elements/{userEmail}/search/ByName/{name}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsByName(
			@PathVariable("userEmail") String userEmail,
			@PathVariable("name") String name,
			@RequestParam(name = "page", required=false, defaultValue="0") int page,
			@RequestParam(name = "size", required=false, defaultValue="10") int size) {
		EmailValidator.validateEmail(userEmail);
		return this.elementService.
				getElementsContainingSpecificElementName(userEmail, name, page, size)
				.toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/acs/elements/{userEmail}/search/ByType/{type}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsByType(
			@PathVariable("userEmail") String userEmail,
			@PathVariable("type") String type,
			@RequestParam(name = "page", required=false, defaultValue="0") int page,
			@RequestParam(name = "size", required=false, defaultValue="10") int size) {
		EmailValidator.validateEmail(userEmail);
		return this.elementService.
				getElementsContainingSpecificElementType(userEmail, type, page, size)
				.toArray(new ElementBoundary[0]);
	}
	
}
