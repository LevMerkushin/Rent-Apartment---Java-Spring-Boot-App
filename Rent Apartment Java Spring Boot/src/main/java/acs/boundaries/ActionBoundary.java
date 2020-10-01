package acs.boundaries;

import java.util.Date;
import java.util.Map;

public class ActionBoundary {
	private String actionId;
	private String type;
	private ElementBoundaryId element;
	private Date createdTimestamp;
	private UserBoundaryEmail invokedBy;
	private Map<String, Object> actionAttributes;

	public ActionBoundary() {
		
	}
	
	public ActionBoundary(String actionId, String type, ElementBoundaryId element, Date createdTimestamp,
			UserBoundaryEmail invokedBy, Map<String, Object> actionAttributes) {
		super();
		this.actionId = actionId;
		this.type = type;
		this.element = element;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.actionAttributes = actionAttributes;
	}

	
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ElementBoundaryId getElement() {
		return element;
	}

	public void setElement(ElementBoundaryId element) {
		this.element = element;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public UserBoundaryEmail getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(UserBoundaryEmail invokedBy) {
		this.invokedBy = invokedBy;
	}

	public Map<String, Object> getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(Map<String, Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}

	@Override
	public String toString() {
		return "ActionBoundary [actionId=" + actionId + ", type=" + type + ", element=" + element
				+ ", createdTimestamp=" + createdTimestamp + ", invokedBy=" + invokedBy + ", actionAttributes="
				+ actionAttributes + "]";
	}
	

}
