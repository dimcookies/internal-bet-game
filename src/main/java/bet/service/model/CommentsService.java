package bet.service.model;

import bet.api.constants.GameStatus;
import bet.model.Comment;
import bet.model.CommentLike;
import bet.model.Game;
import bet.model.User;
import bet.repository.CommentLikeRepository;
import bet.repository.GameRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper methods for games schedule
 */
@Component
public class CommentsService {

	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@Transactional
	@Modifying
	public boolean toogleLike(Comment comment, User user) {
		CommentLike like = commentLikeRepository.findOneByUserAndComment(user, comment);
		if(like != null) {
			commentLikeRepository.deleteByUserAndComment(user, comment);
			return false;
		} else {
			 commentLikeRepository.save(new CommentLike(comment, user));
			 return true;
		}

	}
}