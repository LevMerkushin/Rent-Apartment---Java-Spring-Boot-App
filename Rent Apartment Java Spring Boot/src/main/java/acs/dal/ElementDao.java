package acs.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import acs.data.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, Long> {
	
	public List<ElementEntity> findAllByParents_elementId(
			@Param("parentId") Long parentId,
			Pageable pageable);

	public List<ElementEntity> findAllBychildrens_elementId(
			@Param("childId") Long childId,
			Pageable pageable);
	
	public List<ElementEntity> findAllByNameLike(
			@Param("name") String name,
			Pageable pageable);

	public List<ElementEntity> findAllByTypeLike(	
			@Param("type") String type, 
			Pageable pageable);
}
