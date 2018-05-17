package bet.web;

import bet.model.Bet;
import bet.model.Odd;
import bet.repository.BetRepository;
import bet.repository.OddRepository;
import bet.service.livefeed.LiveScoreFeedScheduler;
import com.google.common.collect.Lists;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Profile("live")
public class HelperController {

	@Autowired
	private LiveScoreFeedScheduler liveScoreFeedScheduler;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private OddRepository oddRepository;

	@Value("${application.timezone}")
	private String timezone;

	@RequestMapping(value = "/ws/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String liveFeedLastUpdate() throws Exception {
		ZonedDateTime lastUpdate = liveScoreFeedScheduler.getLastUpdateDate();
		return lastUpdate != null ? lastUpdate.withZoneSameInstant(ZoneId.of(timezone)).toString() : "";
	}

	@RequestMapping(value = "/ws/allPoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Map<String, Integer> allPoints() throws Exception {
		Map<String, Integer> allPoints = betRepository.listAllPoints();
		return allPoints;
	}

	@RequestMapping(value = "/ws/allGames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Odd> allGames(@RequestParam(value = "matchDays", required = false) List<Integer> matchDays) throws Exception {
		return Lists.newArrayList(oddRepository.findAll()).stream()
				.filter(odd -> matchDays == null || matchDays.indexOf(odd.getGame().getMatchDay()) != -1)
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/ws/allBets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Bet> allBets(@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam(value = "gameId", required = false) Integer gameId) throws Exception {
		return Lists.newArrayList(betRepository.findAll()).stream()
				.filter(bet -> userId == null || bet.getUser().getId().equals(userId))
				.filter(bet -> gameId == null || bet.getGame().getId().equals(gameId))
				.collect(Collectors.toList());
	}


}
