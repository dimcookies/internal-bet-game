package bet.repository;

import bet.model.EncryptedBet;
import bet.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface EncryptedBetRepository extends CrudRepository<EncryptedBet, Integer> {

    public List<EncryptedBet> findByUser(User user);

    @Transactional
    @Modifying
    @Query("delete from EncryptedBet where user = ? ")
    public void deleteByUser(User user);


}
