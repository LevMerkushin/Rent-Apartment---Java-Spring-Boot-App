package acs.logic.serviceInterfaces;

import java.util.List;

import acs.boundaries.ElementBoundary;

public interface Enhanced2ElementService extends EnhancedElementService {

	public List<ElementBoundary> getAll(String userEmail, int page, int size);

	public List<ElementBoundary> getChildrens(String userEmail, String parentId, int page, int size);

	public List<ElementBoundary> getParents(String userEmail, String childElementId, int page, int size);
	
	public List<ElementBoundary> getElementsContainingSpecificElementName(String userEmail, String name, int page,
			int size);

	public List<ElementBoundary> getElementsContainingSpecificElementType(String userEmail, String type, int page,
			int size);

}
