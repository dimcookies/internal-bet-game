package bet.repository;

import bet.model.Bet;
import bet.model.Game;
import bet.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface BetRepository extends CrudRepository<Bet, Integer> {

	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	List<Bet> findByGame(Game game);

	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	List<Bet> findByUser(User game);

	@Query("from Bet")
	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	Iterable<Bet> findAll();
}
