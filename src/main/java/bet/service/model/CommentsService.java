package bet.service.model;

import bet.model.Comment;
import bet.model.CommentLike;
import bet.model.User;
import bet.repository.CommentLikeRepository;
import bet.service.utils.UrlExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper methods for games schedule
 */
@Component
public class CommentsService {

	final String HTML_TAG_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
	final private Pattern pattern = Pattern.compile(HTML_TAG_PATTERN, Pattern.MULTILINE);

	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@Autowired
	private UrlExtractor urlExtractor;

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

	private boolean stringContainsHtml(String text) {
		final Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	public String enhanceComment(String comment) {
		if(stringContainsHtml(comment)) {
			throw new RuntimeException("Comments contains html:" + comment);
		}
		List<String> links = urlExtractor.pullLinks(comment);
		if(links.size() == 0) {
			return comment;
		}
		String link = links.get(0);

		StringBuilder result = new StringBuilder(comment);
		result.append("<br>");
		if(isImage(link)) {
			result.append(String.format("<img class=\"img-responsive\" src=\"%s\" />", link));
		} else if(isYoutubeLink(link)) {
			link = link.replace("watch?v=", "embed/");
			result.append(String.format("<div class=\"embed-responsive embed-responsive-4by3\"><iframe class=\"embed-responsive-item\" src=\"%s?rel=0\" allowfullscreen></iframe></div>", link));
		} else {
            result.append(String.format("<a target=\"_blank\" href=\"%s\" >%s</a>", link, link));
		}

		return result.toString();
	}

	private boolean isYoutubeLink(String link) {
		return link.indexOf("youtube.com") != -1;
	}

	private boolean isImage(String link) {
		try {
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");
			String contentType = con.getHeaderField("content-type");
			if(contentType != null) {
				if(contentType.startsWith("image")) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

}