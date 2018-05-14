package bet.repository;

import bet.model.Game;
import bet.model.Odd;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {

    List<Game> findByMatchDay(int matchDay);

}
