package bet.repository;

import bet.model.Deadline;
import bet.model.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.time.ZonedDateTime;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Integer> {

	@Query("from Deadline")
	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	Iterable<Deadline> findAll();

	@Query("from Deadline where dateFrom <= ? and dateTo > ?")
	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	Deadline findActiveDeadline(ZonedDateTime dateFrom, ZonedDateTime dateTo);
}
