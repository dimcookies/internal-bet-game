package bet.repository;

import bet.model.User;
import bet.model.UserStreakHistory;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

@Repository
public interface UserStreakHistoryRepository extends CrudRepository<UserStreakHistory, Integer> {

    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
    })
    public UserStreakHistory findOneByUser(User user);


}
