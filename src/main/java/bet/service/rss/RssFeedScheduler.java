package bet.service.rss;

import bet.model.RssFeed;
import bet.repository.RssFeedRepository;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Get rss feeds from the selected sources and stores to databse
 */
@Component
@Profile("live")
public class RssFeedScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedScheduler.class);

	@Value("${application.rss_feed}")
	private String[] feeds;

	@Autowired
	private RssFeedRepository rssFeedRepository;

	@Scheduled(fixedRate = 3600000)
	public void getRssFeed() {
		//delete existing records
		rssFeedRepository.deleteAll();

		Arrays.stream(feeds).map(feed -> {
			try {
				return new URL(feed);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}).forEach(feedUrl -> {
			try {
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(feedUrl));
				feed.getEntries().forEach (e -> {
					SyndEntry entry = (SyndEntry) e;
					//get image if exist
					String imageUrl = entry.getEnclosures() != null && entry.getEnclosures().size() > 0 ? ((SyndEnclosure)entry.getEnclosures().get(0)).getUrl().replaceFirst("^//","http://") : "/images/emptyRss.jpg";
					rssFeedRepository.save(new RssFeed(entry.getTitle(), entry.getLink(),  entry.getPublishedDate(), imageUrl));
				});
			} catch (Exception e) {
				LOGGER.error("Error loading rss " + feedUrl, e);
			}
		});

	}


}
