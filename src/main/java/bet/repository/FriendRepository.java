package bet.repository;

import bet.model.Friend;
import bet.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FriendRepository extends CrudRepository<Friend, Integer> {

	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	List<Friend> findByUser(User user);

	@Transactional
	@Modifying
	@Query("delete from Friend where user = ? ")
	public void deleteByUser(User user);

	@Query("from Friend")
	@QueryHints(value = {
			@QueryHint(name = "org.hibernate.cacheable", value = "true"),
			@QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL"),
			@QueryHint(name = "org.hibernate.cacheRegion", value = "bet.query-cache")
	})
	Iterable<Friend> findAll();
}
