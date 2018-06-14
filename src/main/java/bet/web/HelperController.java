package bet.web;

import bet.model.Game;
import bet.model.Odd;
import bet.model.RssFeed;
import bet.repository.OddRepository;
import bet.repository.RssFeedRepository;
import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.utils.GamesSchedule;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Misc web services
 */
@RestController
public class HelperController {

	@Autowired
	private LiveScoreFeedScheduler liveScoreFeedScheduler;

	@Autowired
	private OddRepository oddRepository;

	@Autowired
	private RssFeedRepository rssFeedRepository;

	@Value("${application.timezone}")
	private String timezone;

	@Autowired
	private GamesSchedule gamesSchedule;

	/**
	 * Get last update date of live feed
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/livefeed/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String liveFeedLastUpdate() throws Exception {
		ZonedDateTime lastUpdate = liveScoreFeedScheduler.getLastUpdateDate();
		return lastUpdate != null ? lastUpdate.withZoneSameInstant(ZoneId.of(timezone)).toString() : "N/A";
	}

	/**
	 * Return all games
	 * @param matchDays
	 * @param matchId
	 * @return
	 * @throws Exception
	 */
	@Cacheable(value = "games")
	@RequestMapping(value = "/games/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Odd> allGames(@RequestParam(value = "matchDays", required = false) List<Integer> matchDays,
			@RequestParam(value = "matchId", required = false) Integer matchId) throws Exception {
		return Lists.newArrayList(oddRepository.findAll()).stream()
				.filter(odd -> matchDays == null || matchDays.indexOf(odd.getGame().getMatchDay()) != -1)
				.filter(odd -> matchId == null || matchId.equals(odd.getGame().getId()))
				.sorted((o1, o2) -> {
					int res = o1.getGame().getGameDate().compareTo(o2.getGame().getGameDate());
					if(res != 0) {
						return res;
					}
					return o1.getGame().getId().compareTo(o2.getGame().getId());
				})
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/games/live", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Game> liveGames() throws Exception {
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

		return gamesSchedule.getActiveGames(now);
	}

	/**
	 * Get all rss
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	@Cacheable(value = "rss")
	@RequestMapping(value = "/rss/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<RssFeed> allRss(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {

		return rssFeedRepository.findAllOrdered(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "publish_date")));
	}









}

