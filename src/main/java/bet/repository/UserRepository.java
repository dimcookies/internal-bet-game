package bet.repository;

import bet.model.Game;
import bet.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	public User findOneByName(String name);

}
