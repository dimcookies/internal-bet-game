package bet.repository;

import bet.model.Bet;
import bet.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends CrudRepository<Bet, Integer> {

	List<Bet> findByGame(Game game);

}
