package bet.repository;

import bet.model.RssFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface RssFeedRepository extends CrudRepository<RssFeed, Integer> {

	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	@Query("from RssFeed")
	List<RssFeed> findAllOrdered(Pageable pageable);


}
