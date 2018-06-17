package bet.service.comment;

import bet.model.Comment;
import bet.model.CommentLike;
import bet.model.User;
import bet.repository.CommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Helper methods for games schedule
 */
@Component
public class CommentsService {

	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@Autowired
    private WebContentExtractor webContentExtractor;

    @Autowired
    private ImageLinkConverter imageLinkConverter;

    @Autowired
    private SimpleLinkConverter simpleLinkConverter;

    @Autowired
    private YoutubeLinkConverter youtubeLinkConverter;

	@Transactional
	@Modifying
    public boolean toggleLike(Comment comment, User user) {
		CommentLike like = commentLikeRepository.findOneByUserAndComment(user, comment);
		if(like != null) {
			commentLikeRepository.deleteByUserAndComment(user, comment);
			return false;
		} else {
			 commentLikeRepository.save(new CommentLike(comment, user));
			 return true;
		}

	}

	public String enhanceComment(String comment) {
        if (webContentExtractor.stringContainsHtml(comment)) {
			throw new RuntimeException("Comments contains html:" + comment);
		}
        List<String> links = webContentExtractor.pullLinks(comment);
		if(links.size() == 0) {
			return comment;
		}
		String link = links.get(0);

		StringBuilder result = new StringBuilder(comment);
		result.append("<br>");
        result.append(getConverter(link).convertLink(link));

		return result.toString();
	}

    private LinkConverter getConverter(String link) {
        if (imageLinkConverter.isApplicable(link)) {
            return imageLinkConverter;
        } else if (youtubeLinkConverter.isApplicable(link)) {
            return youtubeLinkConverter;
        } else {
            return simpleLinkConverter;
		}
	}



}