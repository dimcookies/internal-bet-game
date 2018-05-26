package bet.repository;

import bet.model.RankHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankHistoryRepository extends CrudRepository<RankHistory, Integer> {


}
