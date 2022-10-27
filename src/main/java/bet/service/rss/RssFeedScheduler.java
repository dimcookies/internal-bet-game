package bet.service.rss;

import bet.model.RssFeed;
import bet.repository.RssFeedRepository;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Get rss feeds from the selected sources and stores to database
 */
@Component
@Profile("live")
public class RssFeedScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedScheduler.class);

	@Value("${application.rss_feed}")
	private String[] feeds;

	@Autowired
	private RssFeedRepository rssFeedRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Scheduled(fixedRate = 3600000)
	public void getRssFeed() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		final Date minRssDate = sdf.parse("01/06/2018");

		List<RssFeed> allFeeds = Arrays.stream(feeds).flatMap(feedUrl -> {
			List<RssFeed> feeds = new ArrayList<>();
			try {
				SyndFeedInput input = new SyndFeedInput();
				input.setXmlHealerOn(true);

				//SyndFeed feed = input.build(new XmlReader(feedUrl));
				SyndFeed feed = input.build(getResponse(feedUrl));

				feed.getEntries().forEach (e -> {
					SyndEntry entry = (SyndEntry) e;
					if(entry.getPublishedDate() == null || entry.getPublishedDate().before(minRssDate)) {
						return;
					}
					//get image if exist
					String imageUrl = entry.getEnclosures() != null && entry.getEnclosures().size() > 0 ? ((SyndEnclosure)entry.getEnclosures().get(0)).getUrl().replaceFirst("^//","http://") : "/images/emptyRss.jpg";
					feeds.add(new RssFeed(entry.getTitle(), entry.getLink(), entry.getPublishedDate(), imageUrl));
				});
			} catch (Exception e) {
				LOGGER.error("Error loading rss " + feedUrl, e);
			}
			return feeds.stream();
		}).collect(Collectors.toList());

		if(allFeeds.size() > 0) {
			//delete existing records
			rssFeedRepository.deleteAll();
			rssFeedRepository.save(allFeeds);
		}
	}

	private String cleanTextContent(String text)
	{
		// strips off all non-ASCII characters
		text = text.replaceAll("[^\\x00-\\x7F]", " ");

		// erases all the ASCII control characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ");

		// removes non-printable characters from Unicode
		text = text.replaceAll("\\p{C}", " ");

		return text.trim();
	}

	private Reader getResponse(String feedUrl) {
		HttpHeaders headers = new HttpHeaders();
		//headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");

		HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(feedUrl, HttpMethod.GET, requestEntity, String.class);
		String response = cleanTextContent(responseEntity.getBody());

		return new StringReader(response);

	}


}
