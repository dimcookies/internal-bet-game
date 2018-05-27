package bet.web;

import bet.model.Comment;
import bet.model.User;
import bet.repository.CommentRepository;
import bet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments/")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Comment> allComments(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {

        return commentRepository.findAllOrdered(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "comment_date")));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Comment addComments(@RequestParam(value = "comment", required = true) String comment, Principal principal) throws Exception {
        if(comment == null || comment.length() == 0) {
            return null;
        } /*else if(comment.length() > 200) {
            comment = comment.substring(0,200)+"...";
        }*/
        User user = userRepository.findOneByName(principal.getName());
        Comment c = new Comment(comment, user, ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Athens")));
        return commentRepository.save(c);
    }


}
