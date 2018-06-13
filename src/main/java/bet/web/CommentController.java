package bet.web;

import bet.model.Comment;
import bet.model.User;
import bet.repository.CommentRepository;
import bet.repository.UserRepository;
import bet.service.model.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentsService commentsService;

    /**
     * Get all comments sorted by comment date
     * @param limit
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Comment> allComments(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {

        return commentRepository.findAllOrdered(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "comment_date")))
                .stream().map(comment -> {
                    comment.setCommentDate(comment.getCommentDate().withZoneSameInstant(ZoneId.of("Europe/Athens")));
                    return comment;
                })
                .collect(Collectors.toList());
    }

    /**
     * Add a new comment for the currently logged in user
     * @param comment
     * @param principal
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Comment addComments(@RequestParam(value = "comment", required = true) String comment, Principal principal) throws Exception {
        if(comment == null || comment.length() == 0) {
            return null;
        } /*else if(comment.length() > 200) {
            comment = comment.substring(0,200)+"...";
        }*/
        User user = userRepository.findOneByUsername(principal.getName());
        Comment c = new Comment(comment, user, ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Athens")));
        return commentRepository.save(c);
    }

    /**
     * Add a new comment for the currently logged in user
     * @param comment
     * @param principal
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/add2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Comment addComments2(@RequestBody String comment, Principal principal) throws Exception {
        return addComments(comment, principal);
    }

    /**
     * Add a new comment for the currently logged in user
     * @param principal
     * @return
             * @throws Exception
     */
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
