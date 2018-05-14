package bet.repository;

import bet.model.Bet;
import bet.model.Game;
import bet.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BetRepository extends CrudRepository<Bet, Integer>, BetRepositoryCustom {

	List<Bet> findByGame(Game game);

	List<Bet> findByUser(User game);

}
