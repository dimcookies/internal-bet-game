package bet.web;

import bet.model.Comment;
import bet.model.User;
import bet.repository.CommentRepository;
import bet.repository.UserRepository;
import bet.service.cache.ClearCacheTask;
import bet.service.model.CommentsService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Web services related to comments
 */
@RestController
@RequestMapping("/comments/")
public class CommentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private ClearCacheTask clearCacheTask;

    /**
     * Get all comments sorted by comment date
     * @param limit
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Cacheable("comments")
    public List<Comment> allComments(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {

        return commentRepository.findAllOrdered(new PageRequest(0, 1000, new Sort(Sort.Direction.DESC, "comment_date")))
                .stream()
                //get only parent comments, the other will be retrieved as replies
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> {
                    comment.setCommentDate(comment.getCommentDate().withZoneSameInstant(ZoneId.of("Europe/Athens")));
                    return comment;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Add a new comment for the currently logged in user
     * @param comment
     * @param principal
     * @return
     * @throws Exception
     */
    @CacheEvict("comments")
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Comment addComments(@RequestParam(value = "comment", required = true) String comment,
                               @RequestParam(value = "parentCommentId", required = false) Integer parentCommentId,
                               Principal principal) throws Exception {
        User user = userRepository.findOneByUsername(principal.getName());

        Comment parent = null;
        if (parentCommentId != null) {
            parent = commentRepository.findOne(parentCommentId);
            if (parent == null) {
                throw new RuntimeException("Parent comment not found " + parentCommentId);
            }
        }

        if(comment == null || comment.length() == 0) {
            return null;
        }

        try {
            comment = commentsService.enhanceComment(comment);
        } catch (Exception e) {
            LOGGER.error(user.getUsername() + " tried to upload:" + comment);
            throw e;
        }
        clearCacheTask.clearCache("comments");
        Comment c = new Comment(comment, user, ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Athens")), parent);
        return commentRepository.save(c);
    }

    /**
     * Add a new comment for the currently logged in user
     * @param comment
     * @param principal
     * @return
     * @throws Exception
     */
    @CacheEvict("comments")
    @RequestMapping(value = "/add2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Comment addComments2(@RequestBody String comment, Principal principal) throws Exception {
        return addComments(comment, null, principal);
    }

    /**
     * Add a new comment for the currently logged in user
     *
     * @param commentsParamDto
     * @param principal
     * @return
     * @throws Exception
     */
    @CacheEvict("comments")
    @RequestMapping(value = "/add3", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Comment addComments3(@RequestBody CommentsParamDto commentsParamDto, Principal principal) throws Exception {
        return addComments(commentsParamDto.getComment(), commentsParamDto.getParentCommentId(), principal);
    }

    /**
     * Delete a user comment
     * @param commentId
     * @param principal
     * @return
     * @throws Exception
     */
    @CacheEvict("comments")
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Comment delete(@RequestParam(value = "commentId", required = true) Integer commentId, Principal principal) throws Exception {
        Comment comment = commentRepository.findOne(commentId);

        if(comment != null) {
            if(!comment.getUser().getUsername().equals(principal.getName())) {
                throw new RuntimeException("User " + principal.getName() + " tried to delete comment " + comment);
            }
            commentRepository.delete(commentId);
        }
        return comment;

    }


    /**
     * Add a new comment for the currently logged in user
     * @param principal
     * @return
     * @throws Exception
     */
    @CacheEvict("comments")
    @RequestMapping(value = "/toogleLike", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Boolean toogleLike(@RequestParam(value = "commentId", required = true) Integer commentId, Principal principal) throws Exception {
        User user = userRepository.findOneByUsername(principal.getName());
        Comment comment = commentRepository.findOne(commentId);
        if(comment == null) {
            throw new RuntimeException();
        }
        return commentsService.toogleLike(comment, user);
    }


}


@Data
class CommentsParamDto {
    private String comment;
    private Integer parentCommentId;

    public CommentsParamDto() {

    }
}