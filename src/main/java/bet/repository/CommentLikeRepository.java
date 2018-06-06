package bet.repository;

import bet.model.Comment;
import bet.model.CommentLike;
import bet.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CommentLikeRepository extends CrudRepository<CommentLike, Integer> {

    CommentLike findOneByUserAndComment(User user, Comment comment);

    @Transactional
    @Modifying
    @Query("delete from CommentLike where user = ? and comment = ? ")
    public void deleteByUserAndComment(User user, Comment comment);

}
