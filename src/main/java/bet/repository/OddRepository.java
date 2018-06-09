package bet.repository;

import bet.model.Game;
import bet.model.Odd;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

@Repository
public interface OddRepository extends CrudRepository<Odd, Integer> {

	@Query("from Odd")
	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	Iterable<Odd> findAll();

	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	Odd findOneByGame(Game game);

}
