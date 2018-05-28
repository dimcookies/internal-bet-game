package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.dto.GameDto;
import bet.model.Game;
import bet.repository.GameRepository;
import bet.service.mgmt.GameService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of live feed using json api from fifa com live scores
 */
@Component
@Profile("lifefeed-fifacom")
public class FifaComLiveFeedImpl implements LiveFeed {

	@Value("${application.live_feed.fifacom.url}")
	private String liveFeedUrl;

	@Value("${application.live_feed.fifacom.competition}")
	private String competition;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GameRepository gameRepository;

	@Override
	public List<GameDto> getLiveFeed() {
		//get all games of the day
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
		List<Game> dayGames = StreamSupport.stream(gameRepository.findAll().spliterator(), false)
				.filter(game -> game.getGameDate().truncatedTo(ChronoUnit.DAYS).equals(now.truncatedTo(ChronoUnit.DAYS)))
				.collect(Collectors.toList());

		//response from web service
		String response = getResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objectMapper.readTree(response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return Streams.stream(rootNode.iterator())
				//filter wc2018 competition
				.filter(s -> s.get("name").asText().startsWith(competition))
				//get all matches
				.flatMap(jsonNode -> Streams.stream(jsonNode.get("matchlist").iterator()))
				.map( s ->  {
					String homeTeamName = s.get("teamHomeName").asText();
					String awayTeamName = s.get("teamAwayName").asText();
					int scoreHome = s.get("scoreHome").asInt();
					int scoreAway = s.get("scoreAway").asInt();

					//try to find a db game with same teams
					Optional<Game> game = dayGames.stream().filter(g -> g.getHomeName().equals(homeTeamName) && g.getAwayName().equals(awayTeamName)).findFirst();
					if(!game.isPresent()) {
						return null;
					}

					//if found update dto values with these read from web service
					GameDto dto = new GameDto();
					dto.fromEntity(game.get());
					if(s.get("isFinished").asBoolean()) {
						dto.setStatus(GameStatus.FINISHED);
					} else if(s.get("isPostponed").asBoolean()) {
						dto.setStatus(GameStatus.POSTPONED);
					} else if(s.get("isAbandoned").asBoolean()) {
						dto.setStatus(GameStatus.CANCELED);
					} else if(s.get("isStarted").asBoolean()) {
						dto.setStatus(GameStatus.IN_PLAY);
					}
					dto.getResult().setGoalsHomeTeam(scoreHome);
					dto.getResult().setGoalsAwayTeam(scoreAway);

					return dto;
				}).collect(Collectors.toList());


	}

	private String getResponse() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");

		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(liveFeedUrl, HttpMethod.GET, requestEntity, String.class);
		return responseEntity.getBody().replaceFirst("\\_liveMatchesCallback\\(","").replaceFirst("\\)$", "");
	}

}