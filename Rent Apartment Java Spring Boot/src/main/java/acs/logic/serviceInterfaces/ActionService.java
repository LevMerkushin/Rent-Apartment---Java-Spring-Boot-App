package acs.logic.serviceInterfaces;

import java.util.List;
import acs.boundaries.ActionBoundary;

public interface ActionService {
	
	public ActionBoundary invokeAction(ActionBoundary action);
	
	public List<ActionBoundary> getAllActions(String adminEmail);

	public void deleteAllActions(String adminEmail);

}
