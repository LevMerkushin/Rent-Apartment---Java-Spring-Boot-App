package acs.logic.mockup;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acs.boundaries.ActionBoundary;
import acs.data.ActionEntity;
import acs.data.BoundaryEntityConverter;
import acs.logic.exceptions.InputWrongException;
import acs.logic.serviceInterfaces.ActionService;

//@Service
public class ActionServiceMockup implements ActionService{
	private Map<Long, ActionEntity> actionDatabase;
	private BoundaryEntityConverter actionEntityConverter; 
	private AtomicLong nextId;

	
	public ActionServiceMockup() {

	}

	@Autowired
	public void setElementEntityCoverter(BoundaryEntityConverter actionEntityConverter) {
		this.actionEntityConverter = actionEntityConverter;
	}
	
	//after build the object instance (constructor) , this method will work 
	@PostConstruct
	public void init() {
		this.actionDatabase = Collections.synchronizedMap(new TreeMap<>());
		this.nextId = new AtomicLong(0L);
	}

	@Override
	public ActionBoundary invokeAction(ActionBoundary action) {
		if (action.getElement() == null || action.getInvokedBy() == null 
				|| action.getType() == null) {
			throw new InputWrongException("Could not create action without elementId, InvokedBy and ActionType");
		}
		Long newId = nextId.incrementAndGet();
		ActionEntity entity = this.actionEntityConverter.actionBoundaryToEntity(action);
		entity.setCreatedTimestamp(new Date());
		this.actionDatabase.put(newId, entity);
		return this.actionEntityConverter.actionEntityToBoundary(entity);
	}

	@Override
	public List<ActionBoundary> getAllActions(String adminEmail) {
		return this.actionDatabase.values().stream()
				.map(entity -> this.actionEntityConverter.actionEntityToBoundary(entity)).collect(Collectors.toList());
	}

	@Override
	public void deleteAllActions(String adminEmail) {
		this.actionDatabase.clear();
	}

}
