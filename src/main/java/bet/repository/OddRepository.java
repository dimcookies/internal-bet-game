package bet.repository;

import bet.model.Bet;
import bet.model.Game;
import bet.model.Odd;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OddRepository extends CrudRepository<Odd, Integer> {

	Odd findOneByGame(Game game);

}
