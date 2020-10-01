package acs.logic.mockup;
//package acs.logic;
import java.util.Collections;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import acs.boundaries.ElementBoundary;
import acs.data.BoundaryEntityConverter;
import acs.data.ElementEntity;
import acs.data.UserEmailEntity;
import acs.logic.exceptions.InputWrongException;
import acs.logic.exceptions.NotFoundException;
import acs.logic.serviceInterfaces.ElementService;


//@Service
public class ElementServiceMockup implements ElementService{
	private Map<Long, ElementEntity> elementDatabase;
	private BoundaryEntityConverter elementEntityConverter; 
	private AtomicLong nextId;
	
	public ElementServiceMockup() {

	}

	@Autowired
	public void setElementEntityCoverter(BoundaryEntityConverter elementEntityConverter) {
		this.elementEntityConverter = elementEntityConverter;
	}
	
	//after build the object instance (constructor) , this method will work 
	@PostConstruct
	public void init() {
		this.elementDatabase = Collections.synchronizedMap(new TreeMap<>());
		this.nextId = new AtomicLong(0L);
	}

	@Override
	public ElementBoundary create(String managerEmail, ElementBoundary element) {
		if (element.getType() == null || element.getName() == null) {
			throw new InputWrongException("Could not create element without elementName and ElementType");
		}
		Long newId = nextId.incrementAndGet();
		ElementEntity entity = this.elementEntityConverter.elementBoundaryToEntity(element);
		entity.setCreatedBy(new UserEmailEntity(managerEmail));
		entity.setCreatedTimestamp(new Date());
		entity.setElementId(newId);
		this.elementDatabase.put(newId, entity);
		return this.elementEntityConverter.elementEntityToBoundary(entity);
	}

	@Override
	public ElementBoundary update(String managerEmail, String elementId, ElementBoundary update) {
		ElementBoundary existing = this.getSpecificElement(managerEmail, elementId);
		boolean dirty = false;
		if (update.getActive() != null) {
			existing.setActive(update.getActive());
			dirty = true;
		}
		if (update.getName() != null) {
			existing.setName(update.getName());
			dirty = true;
		}
		if (update.getType() != null) {
			existing.setType(update.getType());
			dirty = true;
		}
		if (update.getElementAttributes() != null) {
			existing.setElementAttributes(update.getElementAttributes());
			dirty = true;
		}
		if (update.getLocation() != null) {
			existing.setLocation(update.getLocation());
			dirty = true;
		}
		if (dirty) {
			this.elementDatabase.put(this.elementEntityConverter.stringToLong(elementId),
					this.elementEntityConverter.elementBoundaryToEntity(existing));
		}
		return existing;
	}

	@Override
	public List<ElementBoundary> getAll(String userEmail) {
		return this.elementDatabase.values().stream()
				.map(entity -> this.elementEntityConverter.elementEntityToBoundary(entity))
				.collect(Collectors.toList());
	}

	@Override
	public ElementBoundary getSpecificElement(String userEmail, String elementId) {
		ElementEntity entity = this.elementDatabase.get(this.elementEntityConverter.stringToLong(elementId));
		if (entity != null) {
			return this.elementEntityConverter.elementEntityToBoundary(entity);
		} else {
			throw new NotFoundException("could not find element for id: " + elementId);
		}
	}

	@Override
	public void deleteAllElements(String adminEmail) {
		this.elementDatabase.clear();
	}
}
