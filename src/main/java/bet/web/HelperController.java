package bet.web;

import bet.model.Odd;
import bet.model.RssFeed;
import bet.repository.OddRepository;
import bet.repository.RssFeedRepository;
import bet.service.livefeed.LiveScoreFeedScheduler;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

	@RequestMapping(value = "/livefeed/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String liveFeedLastUpdate() throws Exception {
		ZonedDateTime lastUpdate = liveScoreFeedScheduler.getLastUpdateDate();
		return lastUpdate != null ? lastUpdate.withZoneSameInstant(ZoneId.of(timezone)).toString() : "N/A";
	}

	@RequestMapping(value = "/games/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Odd> allGames(@RequestParam(value = "matchDays", required = false) List<Integer> matchDays,
			@RequestParam(value = "matchId", required = false) Integer matchId) throws Exception {
		return Lists.newArrayList(oddRepository.findAll()).stream()
				.filter(odd -> matchDays == null || matchDays.indexOf(odd.getGame().getMatchDay()) != -1)
				.filter(odd -> matchId == null || matchId.equals(odd.getGame().getId()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/rss/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<RssFeed> allRss(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {

		return rssFeedRepository.findAllOrdered(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "publish_date")));
	}









}

