package acs.dal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// the last id we saved on database

@Entity
public class LastIdValueForElement {
	private Long lastIdElement;
	
	public LastIdValueForElement() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getLastId1() {
		return lastIdElement;
	}
	
	public void setLastId1(Long lastIdElement) {
		this.lastIdElement = lastIdElement;
	}
}
