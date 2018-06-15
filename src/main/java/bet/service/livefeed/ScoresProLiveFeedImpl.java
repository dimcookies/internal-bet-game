package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.dto.GameDto;
import bet.model.Game;
import bet.model.RssFeed;
import bet.repository.GameRepository;
import bet.service.mgmt.GameService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of live feed using scores pro rss feed
 */
@Component
@Profile("lifefeed-scorespro")
public class ScoresProLiveFeedImpl implements LiveFeed {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScoresProLiveFeedImpl.class);

	final String regex = "\\(.*\\) (\\w*) vs (\\w*): (\\d)-(\\d) - (.*)";
	final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

	@Value("${application.live_feed.scorespro.url}")
	private String liveFeedUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GameRepository gameRepository;

	@Override
	public List<GameDto> getLiveFeed() {
			ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

			List<Game> dayGames = StreamSupport.stream(gameRepository.findAll().spliterator(), false)
					.filter(game -> game.getGameDate().withZoneSameInstant(ZoneId.of("UTC")).truncatedTo(ChronoUnit.DAYS).equals(now.truncatedTo(ChronoUnit.DAYS)))
					.collect(Collectors.toList());

			List<GameDto> matches = new ArrayList<>();
			try {
				SyndFeedInput input = new SyndFeedInput();
				input.setXmlHealerOn(true);

				SyndFeed feed = input.build(getResponse(liveFeedUrl));

				feed.getEntries().forEach (e -> {
					SyndEntry entry = (SyndEntry) e;
					String description = entry.getDescription().toString();
					Matcher matcher = pattern.matcher(description);
					if(matcher.find()) {
						String homeName = matcher.group(1);
						String awayName = matcher.group(2);
						String homeScore = matcher.group(3);
						String awayScore = matcher.group(4);
						String status = matcher.group(5);
						Optional<Game> game = dayGames.stream().filter(g -> g.getHomeName().equals(homeName) && g.getAwayName().equals(awayName)).findFirst();
						if(!game.isPresent()) {
							return;
						}
						GameDto dto = new GameDto();
						dto.fromEntity(game.get());
						dto.getResult().setGoalsHomeTeam(Integer.parseInt(homeScore));
						dto.getResult().setGoalsAwayTeam(Integer.parseInt(awayScore));

						if("Match Finished".equals(status)) {
							dto.setStatus(GameStatus.FINISHED);
						} else {
							dto.setStatus(GameStatus.IN_PLAY);
						}
						matches.add(dto);

					}
				});
			} catch (Exception e) {
				LOGGER.error("Error loading rss " + liveFeedUrl, e);
			}

			return matches;
	}

	private Reader getResponse(String feedUrl) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "*/*");

		HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(feedUrl, HttpMethod.GET, requestEntity, String.class);
		String response = cleanTextContent(responseEntity.getBody());

		return new StringReader(response);

	}

	private String cleanTextContent(String text)
	{
		// strips off all non-ASCII characters
		text = text.replaceAll("[^\\x00-\\x7F]", "");

		// erases all the ASCII control characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

		// removes non-printable characters from Unicode
		text = text.replaceAll("\\p{C}", "");

		return text.trim();
	}

}