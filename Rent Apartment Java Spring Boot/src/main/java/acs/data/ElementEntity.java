package acs.data;

import java.util.Date;
import javax.persistence.JoinColumn;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name = "ELEMENTS")
public class ElementEntity {
	private Long elementId;
	private String type;
	private String name;
	private boolean active;
	private Date createdTimestamp;
	private UserEmailEntity createdBy;
	private Location location;
	private String elementAttributes;
	private Set<ElementEntity> childrens;
	private Set<ElementEntity> parents;
	
	public ElementEntity() {
		this.childrens = new HashSet<>();
		this.parents = new HashSet<>();
	}

	@Id
	public Long getElementId() {
		return elementId;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@Embedded
	public UserEmailEntity getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserEmailEntity createdBy) {
		this.createdBy = createdBy;
	}

	@Embedded
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Lob
	public String getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(String elementAttributes) {
		this.elementAttributes = elementAttributes;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="origin_children", joinColumns={@JoinColumn(referencedColumnName="elementId")}
										, inverseJoinColumns={@JoinColumn(referencedColumnName="elementId")})	
	public Set<ElementEntity> getChildrens() {
		return childrens;
	}

	public void setChildrens(Set<ElementEntity> childrens) {
		this.childrens = childrens;
	}
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy="childrens")
	public Set<ElementEntity> getParents() {
		return parents;
	}

	public void setParents(Set<ElementEntity> parents) {
		this.parents = parents;
	}


	public void addChild(ElementEntity child) {
		this.childrens.add(child);
		child.getParents().add(this);
	}

}
