package acs.data;

import javax.persistence.Embeddable;

@Embeddable
public class ElementIdEntity {
	
	private String elementId;
	public ElementIdEntity() {
		
	}
	
	public ElementIdEntity(String id) {
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
		return "ElementIdEntity [elementId=" + elementId + "]";
	}


}
