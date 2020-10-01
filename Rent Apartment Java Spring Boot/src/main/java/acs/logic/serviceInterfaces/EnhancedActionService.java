package acs.logic.serviceInterfaces;

import java.util.List;

import acs.boundaries.ActionBoundary;

public interface EnhancedActionService extends ActionService {

	List<ActionBoundary> getAllActions(String adminEmail, int page, int size);

}
