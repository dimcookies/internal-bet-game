package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.dto.GameDto;
import bet.model.Game;
import bet.repository.GameRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of live feed using scores pro rss feed
 */
@Component
@Profile("livefeed-apisports")
public class ApiSportsLiveFeedImpl implements LiveFeed {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiSportsLiveFeedImpl.class);

	@Value("${application.live_feed.apisports.url}")
	private String liveFeedUrl;

	@Value("${application.live_feed.apisports.token}")
	private String token;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GameRepository gameRepository;

	@Override
	public List<GameDto> getLiveFeed() {
			ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

			List<Game> dayGames = StreamSupport.stream(gameRepository.findAll().spliterator(), false)
					.filter(game -> game.getGameDate().withZoneSameInstant(ZoneId.of("UTC")).truncatedTo(ChronoUnit.DAYS)
							.equals(now.truncatedTo(ChronoUnit.DAYS)))
					.collect(Collectors.toList());

			List<GameDto> matches = new ArrayList<>();
			try {
				String response = getResponse(now);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode fixtures;
				try {
					fixtures = objectMapper.readTree(response).get("response");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				return Streams.stream(fixtures.iterator()).map(jsonNode -> {
					String awayTeamName = jsonNode.get("teams").get("away").get("name").textValue();
					String homeTeamName = jsonNode.get("teams").get("home").get("name").textValue();

					Optional<Game> game = dayGames.stream().filter(g ->
								matchNames(g.getHomeName(),homeTeamName)
								&& matchNames(g.getAwayName(), awayTeamName)
					).findFirst();

					if(!game.isPresent()) {
						return null;
					}

					GameDto dto = new GameDto();
					dto.fromEntity(game.get());

					dto.getResult().setGoalsHomeTeam(getScore(jsonNode.get("goals").get("home").asText()));
					dto.getResult().setGoalsAwayTeam(getScore(jsonNode.get("goals").get("away").asText()));

					String status = jsonNode.get("fixture").get("status").get("short").textValue();

					dto.setStatus(getStatus(status));

					return dto;
				})
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error("Error loading feed " + liveFeedUrl, e);
			}

			return matches;
	}

	private int getScore(String score) {
		if (StringUtils.isNotBlank(score) && (!score.equals("null"))) {
			return Integer.parseInt(score);
		}
		return 0;
	}

	private boolean matchNames(String name1, String name2) {
		if(name1.equals(name2)) {
			return true;
		}

		if(StringUtils.isBlank(name1) || StringUtils.isBlank(name2)) {
			return false;
		}

		return Arrays.stream(name1.split(" ")).anyMatch(name2::contains);
	}

	private String getResponse(ZonedDateTime now) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String url = String.format(liveFeedUrl, format.format(now));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "*/*");
		if(token != null && token.length() > 0) {
			headers.add("x-apisports-key", token);
		}

		HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		return cleanTextContent(responseEntity.getBody());

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

	private GameStatus getStatus(String st) {
		GameStatus status = GameStatus.SCHEDULED;
		switch (st) {
			case "TBD":
			case "NS":
				status = GameStatus.SCHEDULED;
				break;
			case "1H":
			case "HT":
			case "2H":
			case "SUSP":
			case "INT":
			case "AWD":
				status = GameStatus.IN_PLAY;
				break;
			case "ET":
			case "P":
			case "FT":
			case "AET":
			case "PEN":
			case "WO":
				status = GameStatus.FINISHED;
				break;
			case "PST":
				status = GameStatus.POSTPONED;
				break;
			case "ABD":
			case "CANC":
				status = GameStatus.CANCELED;
				break;
		}
		return status;
	}

}