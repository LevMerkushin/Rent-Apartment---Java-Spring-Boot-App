package acs.data;

import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementBoundaryId;
import acs.boundaries.UserBoundary;
import acs.boundaries.UserBoundaryEmail;
import acs.boundaries.UserType;

@Component
public class BoundaryEntityConverter {
	private ObjectMapper jackson;
	
	@PostConstruct
	public void setup() {
		this.jackson = new ObjectMapper();
	}
	
	public Long stringToLong(String id) { 
		if (id != null)
			return Long.parseLong(id);
		else 
			return null;
	}
	
	public String longToString(Long id) {
		if (id != null) {
			return id.toString();
		} else {
			return null;
		}
	}

	public ElementEntity elementBoundaryToEntity(ElementBoundary elementBoundary) {
		ElementEntity entity = new ElementEntity();
		if (elementBoundary.getElementId() != null) {
			entity.setElementId(Long.parseLong(elementBoundary.getElementId()));
		} else {
			entity.setElementId(null);
		}
		entity.setCreatedTimestamp(elementBoundary.getCreatedTimestamp());
		if (elementBoundary.getType() != null) {
			entity.setType(elementBoundary.getType());			
		}
		if (elementBoundary.getName() != null) {
			entity.setName(elementBoundary.getName());
		}
		if (elementBoundary.getCreatedBy() != null) {
			entity.setCreatedBy(new UserEmailEntity(elementBoundary.getCreatedBy().getEmail().toString()));			
		}
		if (elementBoundary.getActive() != null) {
			entity.setActive(elementBoundary.getActive());
		}
		if (elementBoundary.getLocation() != null) {
			entity.setLocation(elementBoundary.getLocation());
		}
		try {
			entity.setElementAttributes(this.jackson
					.writeValueAsString(elementBoundary.getElementAttributes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return entity;
	}

	
	public ElementBoundary elementEntityToBoundary (ElementEntity entity) {
		ElementBoundary boundary = new ElementBoundary();
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		boundary.setActive(entity.getActive());
		if (entity.getElementId() != null) {
			boundary.setElementId(""+entity.getElementId());
		} else {
			boundary.setElementId(null);
		}
		boundary.setName(entity.getName());
		boundary.setType(entity.getType());
		boundary.setCreatedBy(new UserBoundaryEmail(entity.getCreatedBy().getEmail().toString()));
		boundary.setLocation(entity.getLocation());
		try {
			boundary.setElementAttributes(
					this.jackson.readValue(
							entity.getElementAttributes(), Map.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return boundary;
	}

	public String ElementMapToString(Map<String, Object> attributes) {
		if (attributes != null) {
			try {
				return this.jackson.writeValueAsString(attributes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}
	
	public ActionEntity actionBoundaryToEntity(ActionBoundary actionBoundary) {
		ActionEntity entity = new ActionEntity();
		if (actionBoundary.getActionId() != null) {
			entity.setActionId(Long.parseLong(actionBoundary.getActionId()));
		} else {
			entity.setActionId(null);
		}
		entity.setCreatedTimestamp(actionBoundary.getCreatedTimestamp());
		if (actionBoundary.getType() != null) {
			entity.setType(actionBoundary.getType());
		}
		if (actionBoundary.getElement() != null) {
			entity.setElement(new ElementIdEntity(actionBoundary.getElement().getElementId().toString()));
		}
		if (actionBoundary.getInvokedBy() != null) {
			entity.setInvokedBy(new UserEmailEntity(actionBoundary.getInvokedBy().getEmail().toString()));
		}
		try {
			entity.setActionAttributes(this.jackson
					.writeValueAsString(actionBoundary.getActionAttributes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return entity;
	}


	public ActionBoundary actionEntityToBoundary(ActionEntity entity) {
		ActionBoundary boundary = new ActionBoundary();
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		if (entity.getActionId() != null) {
			boundary.setActionId(""+entity.getActionId());
		} else {
			boundary.setActionId(null);
		}
		boundary.setType(entity.getType());
		boundary.setElement(new ElementBoundaryId(entity.getElement().getElementId().toString()));
		boundary.setInvokedBy(new UserBoundaryEmail(entity.getInvokedBy().getEmail().toString()));
		try {
			boundary.setActionAttributes(
					this.jackson.readValue(
							entity.getActionAttributes(), Map.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return boundary;
	}

	public UserBoundary userEntityToBoundary(UserEntity entity) {
		UserBoundary boundary = new UserBoundary();
		boundary.setEmail(entity.getEmail());
		boundary.setAvatar(entity.getAvatar());	
		boundary.setUsername(entity.getUsername());
		if (entity.getRole() != null) 
			boundary.setRole(UserType.valueOf(entity.getRole().name()));
		return boundary;
	}

	public UserEntity userBoundaryToEntity(UserBoundary userBoundary) {
		UserEntity entity = new UserEntity();
		entity.setEmail(userBoundary.getEmail());
		entity.setAvatar(userBoundary.getAvatar());
		entity.setUsername(userBoundary.getUsername());
		if (userBoundary.getRole() != null) {
			entity.setRole(UserRole.valueOf(userBoundary.getRole().name()));
		}
		return entity;
	}
}
