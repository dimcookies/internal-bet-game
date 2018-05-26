package bet.repository;

import bet.model.Friend;
import bet.model.Game;
import bet.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FriendRepository extends CrudRepository<Friend, Integer> {

	List<Friend> findByUser(User user);

	@Transactional
	@Modifying
	@Query("delete from Friend where user = ? ")
	public void deleteByUser(User user);

}
