package bet.repository;

import bet.model.User;
import bet.model.UserStreak;
import bet.model.UserStreakHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStreakHistoryRepository extends CrudRepository<UserStreakHistory, Integer> {

    public UserStreakHistory findOneByUser(User user);


}
