package acs.data;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acs.boundaries.ElementBoundaryId;
import acs.boundaries.UserBoundaryEmail;

// SQL: CREATE TABLE MESSAGES()
// Table name: ACTIONS
@Entity
@Table (name = "ACTIONS")
public class ActionEntity {
	private Long actionId; //ID
	private String type; // TYPE
	private ElementIdEntity element; //ELEMENT
	private Date createdTimestamp; //CREATED_TIMESTAMP
	private UserEmailEntity invokedBy; //INVOKED_BY
	private String actionAttributes; //ACTION_ATTRIBUTES
	
	public ActionEntity() {
		super();
	}
	

	public ActionEntity(Long actionId, String type, ElementIdEntity element, Date createdTimestamp,
			UserEmailEntity invokedBy, String actionAttributes) {
		super();
		this.actionId = actionId;
		this.type = type;
		this.element = element;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.actionAttributes = actionAttributes;
	}


	@Id
	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	


	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	

	@Lob
	public String getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(String actionAttributes) {
		this.actionAttributes = actionAttributes;
	}

	@Embedded
	public ElementIdEntity getElement() {
		return element;
	}


	public void setElement(ElementIdEntity element) {
		this.element = element;
	}

	@Embedded
	public UserEmailEntity getInvokedBy() {
		return invokedBy;
	}


	public void setInvokedBy(UserEmailEntity invokedBy) {
		this.invokedBy = invokedBy;
	}

	
}
