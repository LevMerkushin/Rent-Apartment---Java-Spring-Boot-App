package acs.dal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// the last id we saved on database

@Entity
public class LastIdValueForAction {
	private Long lastId;
	
	public LastIdValueForAction() {
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	public Long getLastId2() {
		return lastId;
	}
	
	public void setLastId2(Long lastId) {
		this.lastId = lastId;
	}
}
