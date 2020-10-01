
package acs.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import acs.boundaries.ElementBoundary;
import acs.dal.ElementDao;
import acs.dal.LastIdValueForElement;
import acs.dal.LastValueDaoForElements;
import acs.dal.UserDao;
import acs.data.BoundaryEntityConverter;
import acs.data.ElementEntity;
import acs.data.Location;
import acs.data.UserEmailEntity;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.logic.exceptions.InputWrongException;
import acs.logic.exceptions.NotFoundException;
import acs.logic.exceptions.PermissionDeniedException;
import acs.logic.serviceInterfaces.Enhanced2ElementService;

@Service
public class ElementServiceWithDb implements Enhanced2ElementService {
	private ElementDao elementDao;
	private BoundaryEntityConverter elementEntityConverter;
	private LastValueDaoForElements lastValueDao;
	private UserDao userDao;

	@Autowired
	public ElementServiceWithDb(ElementDao elementDao, LastValueDaoForElements lastValueDao, UserDao userDao) {
		this.elementDao = elementDao;
		this.lastValueDao = lastValueDao;
		this.userDao = userDao;
	}

	@Autowired
	public void setElementEntityCoverter(BoundaryEntityConverter elementEntityConverter) {
		this.elementEntityConverter = elementEntityConverter;
	}

	@Override
	@Transactional
	public ElementBoundary create(String managerEmail, ElementBoundary element) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(managerEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + managerEmail);
		}
		if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			if (element.getType() == null || element.getName() == null) {
				throw new InputWrongException("Could not create element without elementName and ElementType");
			}
			LastIdValueForElement idValue = this.lastValueDao.save(new LastIdValueForElement());
			ElementEntity entity = this.elementEntityConverter.elementBoundaryToEntity(element);
			if (element.getLocation() == null) {
				Location location = new Location(0, 0);
				entity.setLocation(location);
			}
			entity.setCreatedBy(new UserEmailEntity(managerEmail));
			entity.setCreatedTimestamp(new Date());
			entity.setElementId(idValue.getLastId1());
			this.lastValueDao.delete(idValue);
			this.elementDao.save(entity);
			return this.elementEntityConverter.elementEntityToBoundary(entity);
		}
		throw new PermissionDeniedException("Only manager can create element");
	}

	@Override
	@Transactional
	public ElementBoundary update(String managerEmail, String elementId, ElementBoundary update) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(managerEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + managerEmail);
		}
		if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			
			ElementEntity existing = this.elementDao.findById(this.elementEntityConverter.stringToLong(elementId))
					.orElseThrow(() -> new NotFoundException("could not find element for id: " + elementId));
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
				String json = this.elementEntityConverter.ElementMapToString(update.getElementAttributes());
				existing.setElementAttributes(json);
				dirty = true;
			}
			if (update.getLocation() != null) {
				existing.setLocation(update.getLocation());
				dirty = true;
			}
			if (dirty) {
				this.elementDao.save(existing);

			}
			return this.elementEntityConverter.elementEntityToBoundary(existing);
		}
		throw new PermissionDeniedException("Only manager can upadte element");
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userEmail) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		Iterable<ElementEntity> content = this.elementDao.findAll();
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers;
		} else if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			return rv;
		} else {
			throw new PermissionDeniedException("Admin cannot export elements");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ElementBoundary getSpecificElement(String userEmail, String elementId) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		ElementBoundary elementBoundary;
		Optional<ElementEntity> entity = this.elementDao.findById(Long.parseLong(elementId));
		if (entity.isPresent()) {
			elementBoundary = this.elementEntityConverter.elementEntityToBoundary(entity.get());
		} else {
			throw new NotFoundException("could not find element for id: " + elementId);
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			if (elementBoundary.getActive() == false) {
				throw new NotFoundException("could not find element for id: " + elementId);
			}
		}
		return elementBoundary;
	}

	@Override
	@Transactional
	public void deleteAllElements(String adminEmail) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(adminEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + adminEmail);
		}

		if (presentUser != null && presentUser.getRole() == UserRole.ADMIN) {
			this.elementDao.deleteAll();
		} else {
			throw new PermissionDeniedException("Only admin can delete all elements");
		}
	}

	@Override
	@Transactional
	public void addChildToElement(String managerEmail, String parentId, String childId) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(managerEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + managerEmail);
		}
		if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			ElementEntity parent = this.elementDao.findById(this.elementEntityConverter.stringToLong(parentId))
					.orElseThrow(() -> new NotFoundException("could not find element for id: " + parentId));

			ElementEntity child = this.elementDao.findById(this.elementEntityConverter.stringToLong(childId))
					.orElseThrow(() -> new NotFoundException("could not find element for id: " + childId));

			parent.addChild(child);
			this.elementDao.save(parent);
		} else {
			throw new PermissionDeniedException("Only manager can update element");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Set<ElementBoundary> getChildrens(String userEmail, String parentId) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		ElementEntity parent = this.elementDao.findById(this.elementEntityConverter.stringToLong(parentId))
				.orElseThrow(() -> new NotFoundException("could not find element for id: " + parentId));
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		Iterable<ElementEntity> content = parent.getChildrens();
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers.stream().collect(Collectors.toSet());
		} else {
			return rv.stream().collect(Collectors.toSet());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Set<ElementBoundary> getParents(String userEmail, String childElementId) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		ElementEntity child = this.elementDao.findById(this.elementEntityConverter.stringToLong(childElementId))
				.orElseThrow(() -> new NotFoundException("could not find element for id: " + childElementId));
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		Iterable<ElementEntity> content = child.getParents();
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers.stream().collect(Collectors.toSet());
		} else {
			return rv.stream().collect(Collectors.toSet());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userEmail, int page, int size) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		Iterable<ElementEntity> content = this.elementDao
				.findAll(PageRequest.of(page, size, Direction.ASC, "createdTimestamp", "elementId"));
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers;
		} else if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			return rv;
		} else {
			throw new PermissionDeniedException("Admin cannot export elements");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getChildrens(String userEmail, String parentId, int page, int size) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		
		Optional<ElementEntity> parentEntity = this.elementDao.findById(
				this.elementEntityConverter.stringToLong(parentId));
		
		if(!parentEntity.isPresent()) {
			throw new NotFoundException("could not find element for id: " + parentId);
		}
		
		List<ElementEntity> content = this.elementDao.findAllByParents_elementId(
				this.elementEntityConverter.stringToLong(parentId),
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers;
		} else if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			return rv;
		} else {
			throw new PermissionDeniedException("Admin cannot export elements");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getParents(String userEmail, String childElementId, int page, int size) {
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		
		Optional<ElementEntity> childEntity = this.elementDao.findById(
				this.elementEntityConverter.stringToLong(childElementId));
		
		if(!childEntity.isPresent()) {
			throw new NotFoundException("could not find element for id: " + childElementId);
		}
		
		List<ElementEntity> content = this.elementDao.findAllBychildrens_elementId(
				this.elementEntityConverter.stringToLong(childElementId),
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers;
		} else if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			return rv;
		} else {
			throw new PermissionDeniedException("Admin cannot export elements");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getElementsContainingSpecificElementName(String userEmail, String name, int page,
			int size) {
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		Iterable<ElementEntity> content = this.elementDao.findAllByNameLike(name,
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers;
		} else if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			return rv;
		} else {
			throw new PermissionDeniedException("Admin cannot export elements");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getElementsContainingSpecificElementType(String userEmail, String type, int page,
			int size) {
		List<ElementBoundary> rv = new ArrayList<>();
		List<ElementBoundary> rvForPlayers = new ArrayList<>();
		Iterable<ElementEntity> content = this.elementDao.findAllByTypeLike(type,
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		for (ElementEntity element : content) {
			rv.add(this.elementEntityConverter.elementEntityToBoundary(element));
			if (element.getActive() == true) {
				rvForPlayers.add(this.elementEntityConverter.elementEntityToBoundary(element));
			}
		}
		UserEntity presentUser = null;
		Optional<UserEntity> user = this.userDao.findById(userEmail);
		if (user.isPresent()) {
			presentUser = user.get();
		} else {
			throw new NotFoundException("could not find user for email: " + userEmail);
		}
		if (presentUser != null && presentUser.getRole() == UserRole.PLAYER) {
			return rvForPlayers;
		} else if (presentUser != null && presentUser.getRole() == UserRole.MANAGER) {
			return rv;
		} else {
			throw new PermissionDeniedException("Admin cannot export elements");
		}
	}
}
