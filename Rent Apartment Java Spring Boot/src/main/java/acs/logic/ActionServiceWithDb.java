package acs.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import acs.boundaries.ActionBoundary;
import acs.dal.ActionDao;
import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.dal.LastIdValueForAction;
import acs.dal.LastValueDaoForActions;
import acs.data.ActionEntity;
import acs.data.BoundaryEntityConverter;
import acs.data.ElementEntity;
import acs.data.UserEmailEntity;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.logic.exceptions.NotFoundException;
import acs.logic.exceptions.PermissionDeniedException;
import acs.logic.serviceInterfaces.EnhancedActionService;


@Service
public class ActionServiceWithDb implements EnhancedActionService{
	private ActionDao actionDao;	
	private ElementDao elementDao;
	private BoundaryEntityConverter actionEntityConverter;
	private LastValueDaoForActions lastValueDao;
	private UserDao userDao;

	@Autowired
	public ActionServiceWithDb(ActionDao actionDao, LastValueDaoForActions lastValueDao, UserDao userDao, ElementDao elementDao) {
		this.actionDao = actionDao;
		this.lastValueDao = lastValueDao;
		this.userDao = userDao;
		this.elementDao = elementDao;
	}

	@Autowired
	public void setElementEntityCoverter(BoundaryEntityConverter actionEntityConverter) {
		this.actionEntityConverter = actionEntityConverter;
	}
	
	@Override
	@Transactional
	public ActionBoundary invokeAction(ActionBoundary action) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(action.getInvokedBy().getEmail());
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for this email");
		}

		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			LastIdValueForAction idValue = this.lastValueDao.save(new LastIdValueForAction());
			ActionEntity entity = this.actionEntityConverter.actionBoundaryToEntity(action);
			String elementId = action.getElement().getElementId();
			if (elementExist(elementId)) {
				entity.setInvokedBy(new UserEmailEntity(action.getInvokedBy().getEmail().toString()));
				entity.setCreatedTimestamp(new Date());
				entity.setActionId(idValue.getLastId2()); // use the new id
				this.lastValueDao.delete(idValue);
				this.actionDao.save(entity);
				return this.actionEntityConverter.actionEntityToBoundary(entity);
			} else {
			throw new NotFoundException("could not find element for id: " + elementId);
			}
		} else {
			throw new PermissionDeniedException("Only player can invoke actions");
		}
	}


	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions(String adminEmail) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(adminEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		}else {
			throw new NotFoundException("could not find user for this email: " + adminEmail);
		}
		if (presentUser!=null && presentUser.getRole() == UserRole.ADMIN) {
			List<ActionBoundary> rv = new ArrayList<>();
			Iterable<ActionEntity> content = this.actionDao.findAll();
			for (ActionEntity action : content) {
				rv.add(this.actionEntityConverter.actionEntityToBoundary(action));
			}
			return rv;
		} else {
			throw new PermissionDeniedException("Only admin can get all actions");
		}
	}

	@Override
	@Transactional
	public void deleteAllActions(String adminEmail) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(adminEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		}else {
			throw new NotFoundException("could not find user for this email: " + adminEmail);
		}
		if (presentUser!=null && presentUser.getRole() == UserRole.ADMIN) {
			this.actionDao.deleteAll();
		} else {
			throw new PermissionDeniedException("Only admin can delete all actions");
		}
	}

	@Override
	public List<ActionBoundary> getAllActions(String adminEmail, int page, int size) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(adminEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		}else {
			throw new NotFoundException("could not find user for this email: " + adminEmail);
		}
		if (presentUser!=null && presentUser.getRole() == UserRole.ADMIN) {
			return this.actionDao.findAll(PageRequest.of(page, size, Direction.ASC, "createdTimestamp", "actionId"))
					.getContent().stream().map(this.actionEntityConverter::actionEntityToBoundary)
					.collect(Collectors.toList());
		} else {
			throw new PermissionDeniedException("Only admin can get all actions");
		}
	}

	public boolean elementExist(String elementId) {
		Optional<ElementEntity> element = this.elementDao.findById(Long.parseLong(elementId));
		if (element.isPresent()) {
			ElementEntity presentElement = element.get();
			if (presentElement.getActive() == true) 
				return true;
		}
		return false;
	}
}

