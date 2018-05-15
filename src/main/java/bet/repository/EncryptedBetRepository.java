package bet.repository;

import bet.model.EncryptedBet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptedBetRepository extends CrudRepository<EncryptedBet, Integer> {

}
