package bet.web;

import bet.api.dto.EncryptedBetDto;
import bet.api.dto.UserDto;
import bet.model.*;
import bet.repository.*;
import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.mgmt.EncryptedBetService;
import bet.service.mgmt.UserService;
import com.google.common.collect.Lists;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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

	@Autowired
    private UserService userService;

	@Autowired
	private EncryptedBetService encryptedBetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	private FriendRepository friendRepository;

	@Value("${application.timezone}")
	private String timezone;

	@Value("${application.allowedMatchDays}")
	private String allowedMatchDays;

	@RequestMapping(value = "/ws/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String liveFeedLastUpdate() throws Exception {
		ZonedDateTime lastUpdate = liveScoreFeedScheduler.getLastUpdateDate();
		return lastUpdate != null ? lastUpdate.withZoneSameInstant(ZoneId.of(timezone)).toString() : "N/A";
	}

	@RequestMapping(value = "/ws/participations", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String participations() throws Exception {
		return ""+Lists.newArrayList(userRepository.findAll()).size();
	}

	@RequestMapping(value = "/ws/allowedMatchDays", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String allowedMatchDays() throws Exception {
		return allowedMatchDays;
	}

	@RequestMapping(value = "/config/liveupdate", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public String liveupdate() throws Exception {
		liveScoreFeedScheduler.getLiveScores(false);
		return "OK";
	}

	@RequestMapping(value = "/ws/allPoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Map<String, String>> allPoints() throws Exception {
		Map<String, Integer> allPoints = betRepository.listAllPoints();
		return allPoints.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
				.map(e -> new HashMap<String, String>() {{
					put("username", e.getKey());
					put("points", e.getValue().toString());
				}}).collect(Collectors.toList());
	}

	@RequestMapping(value = "/ws/allGames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Odd> allGames(@RequestParam(value = "matchDays", required = false) List<Integer> matchDays,
			@RequestParam(value = "matchId", required = false) Integer matchId) throws Exception {
		return Lists.newArrayList(oddRepository.findAll()).stream()
				.filter(odd -> matchDays == null || matchDays.indexOf(odd.getGame().getMatchDay()) != -1)
				.filter(odd -> matchId == null || matchId.equals(odd.getGame().getId()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/ws/allBets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Bet> allBets(@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "gameId", required = false) Integer gameId) throws Exception {
		return Lists.newArrayList(betRepository.findAll()).stream()
				.filter(bet -> userId == null || bet.getUser().getId().equals(userId))
				.filter(bet -> userName == null || bet.getUser().getName().equals(userName))
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
	public Comment addComments(@RequestParam(value = "comment", required = true) String comment, Principal principal) throws Exception {
		if(comment == null || comment.length() == 0) {
			return null;
		} else if(comment.length() > 200) {
			comment = comment.substring(0,200)+"...";
		}
		User user = userRepository.findOneByName(principal.getName());
		Comment c = new Comment(comment, user, ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Athens")));
		return commentRepository.save(c);
	}

	@RequestMapping(path = "/ws/createAllBets", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EncryptedBetDto> createAllBets(@RequestBody List<EncryptedBetDto> bets, Principal principal) {
		User user = userRepository.findOneByName(principal.getName());
		return encryptedBetService.createAll(bets, user);
	}

	@RequestMapping(path = "/ws/listAllBets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EncryptedBetDto> createAllBets(Principal principal) {
		User user = userRepository.findOneByName(principal.getName());
		return encryptedBetService.list(user);
	}


    @RequestMapping(value = "/ws/changePassword", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String changePassword(@RequestParam(value = "password", required = true) String password, Principal principal) throws Exception {
	    if(password == null || password.length() == 0) {
	        throw new RuntimeException();
        }
        User user = userRepository.findOneByName(principal.getName());
        UserDto userDto = new UserDto();
        userDto.fromEntity(user);
        userDto.setPassword(password);
        userService.update(userDto);
        return "OK";
    }


	@RequestMapping(value = "/ws/updateFriends", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Friend> updateFriends(@RequestBody List<String> usernames, Principal principal) throws Exception {
		User user = userRepository.findOneByName(principal.getName());
		friendRepository.deleteByUser(user);

		List<Friend> friends = usernames.stream().map(username -> {
			User friend = userRepository.findOneByName(username);
			if(friend == null) {
				throw new RuntimeException();
			}
			return new Friend(user, friend);
		}).collect(Collectors.toList());

		return Lists.newArrayList(friendRepository.save(friends));


	}

	@RequestMapping(value = "/ws/listFriends", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Friend> listFriends(Principal principal) throws Exception {
		User user = userRepository.findOneByName(principal.getName());
		return friendRepository.findByUser(user);
	}


}
