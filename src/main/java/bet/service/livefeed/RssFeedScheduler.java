package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.dto.GameDto;
import bet.model.Game;
import bet.model.Odd;
import bet.model.RssFeed;
import bet.repository.BetRepository;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.repository.RssFeedRepository;
import bet.service.utils.GameScheduler;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("live")
public class RssFeedScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LiveScoreFeedScheduler.class);

	@Value("${application.rss_feed}")
	private String[] feeds;

	@Autowired
	private RssFeedRepository rssFeedRepository;

	@Scheduled(fixedRate = 3600000)
	public void getRssFeed() {
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
					rssFeedRepository.save(new RssFeed(entry.getTitle(), entry.getLink(),  entry.getPublishedDate()));
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

	}


}
