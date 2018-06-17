package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.dto.GameDto;
import bet.model.Game;
import bet.repository.GameRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of live feed using livescores com scrambling
 */

@Component
@Profile("lifefeed-livescorescom")
public class LivescoresComLiveFeedImpl implements LiveFeed {
	private final List<String> allowedClasses = Arrays.asList("mt4", "row-gray");

	@Value("${application.live_feed.livescorescom.url}")
	protected String liveFeedUrl;

	@Autowired
	protected RestTemplate restTemplate;

	@Autowired
	private GameRepository gameRepository;

	@Override
	public List<GameDto> getLiveFeed() {
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

		Document doc = Jsoup.parse(getResponse(now));
		Elements contentElement =doc.getElementsByClass("content");
		List<Element> matchElements = contentElement.get(0).children().stream()
				.filter(element -> element.classNames()
						.stream()
						.anyMatch(s -> allowedClasses.contains(s)))
				.collect(Collectors.toList());

		List<Game> dayGames = StreamSupport.stream(gameRepository.findAll().spliterator(), false)
				.filter(game -> game.getGameDate().withZoneSameInstant(ZoneId.of("UTC")).truncatedTo(ChronoUnit.DAYS).equals(now.truncatedTo(ChronoUnit.DAYS)))
				.collect(Collectors.toList());

		List<GameDto> matches = new ArrayList<>();
		boolean collecting = false;
		for(Element e : matchElements) {
			if(isDataHeader(e)) {
				collecting = isWorldCupMatch(e);
			} else {
				if(collecting) {
					GameDto game = processMatch(e, dayGames);
					if(game != null) {
						matches.add(game);
					}
				}
			}
		}

		return matches;
	}

	private GameDto processMatch(Element e, List<Game> dayGames) {
		String min = e.getElementsByClass("min").text();
		String score = e.getElementsByClass("sco").text();
		String[] scoreAr = score.split("-");
		List<String> teams = e.getElementsByClass("ply")
				.stream()
				.sorted((o1, o2) -> Integer.compare(o2.classNames().size(), o1.classNames().size()))
				.map(element -> element.text())
				.collect(Collectors.toList());
		//try to find a db game with same teams
		Optional<Game> game = dayGames.stream().filter(g -> g.getHomeName().equals(teams.get(0)) && g.getAwayName().equals(teams.get(1))).findFirst();
		if(!game.isPresent()) {
			return null;
		}
		//if found update dto values with these read from web service
		GameDto dto = new GameDto();
		dto.fromEntity(game.get());
		dto.getResult().setGoalsHomeTeam(convertScore(scoreAr[0]));
		dto.getResult().setGoalsAwayTeam(convertScore(scoreAr[1]));

		if("FT".equals(min)) {
			dto.setStatus(GameStatus.FINISHED);
		} else {
			int minute = parseMinute(min);
			if(minute > 90) {
				dto.setStatus(GameStatus.FINISHED);
			} else if(minute > 0) {
				dto.setStatus(GameStatus.IN_PLAY);
			}
		}

		return dto;

	}

	private int parseMinute(String minute) {
		if (minute.contains("+")) {
			minute = minute.split("\\+")[0];
		}
		minute = minute.replace("'","");
		try {
			return Integer.parseInt(minute);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private int convertScore(String score) {
		score = score.trim();
		if(score.equals("?")) {
			return 0;
		}
		return Integer.valueOf(score);
	}

	private boolean isWorldCupMatch(Element e) {
		Elements competition = e.select("strong");
		if(competition != null && competition.size() > 0) {
			return "World Cup".equals(competition.text());
		}

		return false;
	}

	private boolean isDataHeader(Element e) {
		return e.classNames().contains("mt4");
	}

	private String getResponse(ZonedDateTime now) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String url = String.format(liveFeedUrl, format.format(now));
		HttpHeaders headers = new HttpHeaders();
		//headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");

		HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		return responseEntity.getBody();
	}


	public static void main(String[] args) {
		LivescoresComLiveFeedImpl test = new LivescoresComLiveFeedImpl();
		System.out.println(test.getLiveFeed());
	}


}