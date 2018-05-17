package bet.repository;

import bet.model.Comment;
import bet.model.RssFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {

	@Query("from Comment")
	List<Comment> findAllOrdered(Pageable pageable);


}
