package bet.repository;

import bet.model.RankHistory;
import bet.model.UserStreak;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStreakRepository extends CrudRepository<UserStreak, Integer> {


}
