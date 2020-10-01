package acs.dal;

import org.springframework.data.repository.CrudRepository;

// basic operations on the table
public interface LastValueDaoForElements extends CrudRepository<LastIdValueForElement, Long>{

	
}
