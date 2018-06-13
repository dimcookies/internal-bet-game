package bet.service.utils;

import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Extract urls from a string
 */
@Component
public class UrlExtractor {

	//Pull all links from the body for easy retrieval
	public List<String> pullLinks(String text) {
		UrlDetector parser = new UrlDetector(text, UrlDetectorOptions.Default);
		return parser.detect().stream().map(url -> url.toString()).collect(Collectors.toList());
	}
}