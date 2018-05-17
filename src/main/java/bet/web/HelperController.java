package bet.web;

import bet.model.*;
import bet.repository.*;
import bet.service.livefeed.LiveScoreFeedScheduler;
import com.google.common.collect.Lists;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Principal;
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

	@Autowired
	private RssFeedRepository rssFeedRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Value("${application.timezone}")
	private String timezone;

	@RequestMapping(value = "/ws/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String liveFeedLastUpdate() throws Exception {
		ZonedDateTime lastUpdate = liveScoreFeedScheduler.getLastUpdateDate();
		return lastUpdate != null ? lastUpdate.withZoneSameInstant(ZoneId.of(timezone)).toString() : "N/A";
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

	@RequestMapping(value = "/ws/allRss", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<RssFeed> allBets(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {

		return rssFeedRepository.findAllOrdered(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "publish_date")));
	}

	@RequestMapping(value = "/ws/allComments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Comment> allComments(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {

		return commentRepository.findAllOrdered(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "comment_date")));
	}

	@RequestMapping(value = "/ws/addComment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Comment allComments(@RequestParam(value = "comment", required = true) String comment, Principal principal) throws Exception {
		if(comment.length() > 200) {
			comment = comment.substring(0,200)+"...";
		}
		User user = userRepository.findOneByName(principal.getName());
		Comment c = new Comment(comment, user, ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Athens")));
		return commentRepository.save(c);
	}


}
