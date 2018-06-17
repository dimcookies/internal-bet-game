package bet.service.comment;

import bet.base.AbstractBetIntegrationTest;
import bet.model.Comment;
import bet.model.CommentLike;
import bet.model.User;
import bet.repository.CommentLikeRepository;
import bet.repository.CommentRepository;
import bet.repository.UserRepository;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class CommentServiceTest extends AbstractBetIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Before
    public void setUp() {
        super.setUp();
        userRepository.save(new User(null, "user1", "user1", "", "", "user1", false));
        userRepository.save(new User(null, "user2", "user2", "", "", "user2", false));
        userRepository.save(new User(null, "user3", "user3", "", "", "user3", false));
    }

    @Test
    public void test_comment_noLikes() {
        List<User> users = Lists.newArrayList(userRepository.findAll());

        User user1 = users.get(0);

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        Comment comment = commentRepository.save(new Comment("comment1", user1, now));
        assertEquals(0, comment.getCommentLikes().size());
    }

    @Test
    public void test_comment_oneLike() {
        List<User> users = Lists.newArrayList(userRepository.findAll());

        User user1 = users.get(0);
        User user2 = users.get(1);

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        Comment comment = commentRepository.save(new Comment("comment1", user1, now));

        commentLikeRepository.save(new CommentLike(comment, user2));
        comment = commentRepository.findOne(comment.getId());
        assertEquals(1, comment.getCommentLikes().size());

    }

    @Test
    public void test_comment_multipleLikes() {
        List<User> users = Lists.newArrayList(userRepository.findAll());

        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        Comment comment = commentRepository.save(new Comment("comment1", user1, now));

        commentLikeRepository.save(new CommentLike(comment, user2));
        commentLikeRepository.save(new CommentLike(comment, user3));
        comment = commentRepository.findOne(comment.getId());
        assertEquals(2, comment.getCommentLikes().size());

    }

    @Test
    public void test_comment_toogleLikeAdd() {
        List<User> users = Lists.newArrayList(userRepository.findAll());

        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        Comment comment = commentRepository.save(new Comment("comment1", user1, now));

        assertTrue(commentsService.toggleLike(comment, user2));

        comment = commentRepository.findOne(comment.getId());
        assertEquals(1, comment.getCommentLikes().size());

    }


    @Test
    public void test_comment_toggleLikeRemove() {
        List<User> users = Lists.newArrayList(userRepository.findAll());

        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        Comment comment = commentRepository.save(new Comment("comment1", user1, now));

        commentLikeRepository.save(new CommentLike(comment, user2));
        commentLikeRepository.save(new CommentLike(comment, user3));

        assertFalse(commentsService.toggleLike(comment, user2));

        comment = commentRepository.findOne(comment.getId());
        assertEquals(1, comment.getCommentLikes().size());

    }

}
