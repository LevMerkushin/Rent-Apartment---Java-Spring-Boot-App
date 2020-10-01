package acs.logic.serviceInterfaces;

import java.util.Set;

import acs.boundaries.ElementBoundary;

public interface EnhancedElementService extends ElementService{

	public void addChildToElement(String managerEmail, String parentId, String childId);

	public Set<ElementBoundary> getChildrens(String userEmail, String parentId);

	public Set<ElementBoundary> getParents(String userEmail, String childElementId);
	
}
