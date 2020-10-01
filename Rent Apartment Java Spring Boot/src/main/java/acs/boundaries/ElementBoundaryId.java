package acs.boundaries;

public class ElementBoundaryId {
	private String elementId;
	public ElementBoundaryId() {
		
	}
	
	public ElementBoundaryId(String id) {
		super();
		this.elementId = id;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	@Override
	public String toString() {
		return "ElementBoundaryId [elementId=" + elementId + "]";
	}

}
