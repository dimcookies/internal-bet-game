package bet.service.comment;

import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Extract urls from a string
 */
@Component
public class WebContentExtractor {


    private final String HTML_TAG_REGEXP = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";

    private final Pattern HTML_TAG_PATTERN = Pattern.compile(HTML_TAG_REGEXP, Pattern.MULTILINE);


    //Pull all links from the body for easy retrieval
    public List<String> pullLinks(String text) {
        UrlDetector parser = new UrlDetector(text, UrlDetectorOptions.Default);
        return parser.detect().stream().map(url -> url.toString()).collect(Collectors.toList());
    }

    public boolean stringContainsHtml(String text) {
        final Matcher matcher = HTML_TAG_PATTERN.matcher(text);
        return matcher.find();
    }

}