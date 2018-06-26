package bet.web;

import bet.api.dto.EncryptedBetDto;
import bet.api.dto.UserDto;
import bet.model.Bet;
import bet.model.Game;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.CustomBetRepository;
import bet.repository.FriendRepository;
import bet.repository.UserRepository;
import bet.service.mgmt.EncryptedBetService;
import bet.service.mgmt.UserService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Web services related to bets
 */
@RestController
@RequestMapping("/bets/")
public class BetsController {

    @Autowired
    private EncryptedBetService encryptedBetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private CustomBetRepository customBetRepository;

    @Value("${application.allowedMatchDays}")
    private String[] allowedMatchDays;

    @Value("${application.currentMatchDays}")
    private String[] currentMatchDays;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRepository friendRepository;

    @Value("${application.betDeadline}")
    private String betDeadline;

    /**
     * Add/update encrypted bets to currently logged in user
     * @param bets
     * @param principal
     * @return
     */
    @RequestMapping(path = "/encrypted/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EncryptedBetDto> create(@RequestBody List<EncryptedBetDto> bets, Principal principal) {
        //User user = userRepository.findOneByUsername(principal.getName());
        UserDto dto = userService.list(principal.getName()).get(0);
        return encryptedBetService.createAll(bets, dto.toEntity());
    }

    /**
     * List encrypted bets for currently logged in user
     * @param principal
     * @return
     */
    @RequestMapping(path = "/encrypted/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EncryptedBetDto> createAllBets(Principal principal) {
        User user = userRepository.findOneByUsername(principal.getName());
        return encryptedBetService.list(user);
    }

    /**
     * Get public available bets
     * @param userId
     * @param userName
     * @param gameId
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Bet> allBets(@RequestParam(value = "userId", required = false) Integer userId,
                             @RequestParam(value = "userName", required = false) String userName,
                             @RequestParam(value = "gameId", required = false) Integer gameId,
                             Principal principal) {
        String currentUsername = principal.getName();
        List<String> friends = StreamSupport.stream(friendRepository.findAll().spliterator(), false)
                .map(friend -> friend.getUser().getUsername() + "_" + friend.getFriend().getUsername()).collect(Collectors.toList());
        Map<String, String> names = userService.list().stream()
                .collect(Collectors.toMap(UserDto::getUsername, UserDto::getName));
        return Lists.newArrayList(betRepository.findAll()).stream()
                //filter by userId
                .filter(bet -> userId == null || bet.getUser().getId().equals(userId))
                //filter by userName
                .filter(bet -> userName == null || bet.getUser().getUsername().equals(userName))
                //filter by game
                .filter(bet -> gameId == null || bet.getGame().getId().equals(gameId))
                .map(bet -> {
                    bet.addArgs("name", names.getOrDefault(bet.getUser().getUsername(), ""));
                    bet.addArgs("isFriend", ""+ friends.contains(currentUsername + "_" +  bet.getUser().getUsername()));
                    return bet;
                })
                .sorted((o1, o2) -> {
                    int res = o1.getGame().getGameDate().compareTo(o2.getGame().getGameDate());
                    if(res != 0) {
                        return res;
                    }

                    return (o1.getArgs().get("name")).compareTo(o2.getArgs().get("name"));
                })
                .collect(Collectors.toList());
    }


    /**
     * Get points for users sorted by points
     * @return
     */
    @Cacheable("points2")
    @RequestMapping(value = "/points", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Map<String, Object>> allPoints() {
        Map<String, String> names = userService.list().stream()
                .collect(Collectors.toMap(UserDto::getUsername, UserDto::getName));
        Map<String, Double> riskIndex = customBetRepository.listRiskIndex();
        Map<String, Long> allBets =
                StreamSupport.stream(betRepository.findAll().spliterator(), false)
                        .filter(bet -> bet.getResultPoints() > 0)
                        .collect(Collectors.groupingBy(o -> o.getUser().getUsername(), Collectors.counting()));
        Map<String, Integer> allPoints = customBetRepository.listAllPoints();
        AtomicInteger atomicInteger = new AtomicInteger(0);

        return allPoints.entrySet().stream()
                .map(e -> new HashMap<String, Object>() {{
                    put("username", e.getKey());
                    put("points", e.getValue());
                    put("riskIndex", riskIndex.getOrDefault(e.getKey(), 0.0));
                    put("correctResults", allBets.getOrDefault(e.getKey(), 0L));
                    put("name", names.getOrDefault(e.getKey(), ""));
                }})
                //sort by points desc, name asc
                .sorted((o1, o2) -> {
                    int res = ((Integer) o2.get("points")).compareTo((Integer) o1.get("points"));
                    if (res != 0) {
                        return res;
                    }
                    return ((String) o1.get("name")).compareTo((String) o2.get("name"));
                })
                .map(hashMap -> {
                    hashMap.put("idx", atomicInteger.incrementAndGet());
                    return hashMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get configured allowed matchdays for bets
     * @return
     */
    @RequestMapping(value = "/allowedMatchDays", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String[] allowedMatchDays() {
        return allowedMatchDays;
    }

    /**
     * Get configured current matchdays for bets
     * @return
     */
    @RequestMapping(value = "/currentMatchDays", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String[] currentMatchDays() {
        return currentMatchDays;
    }

    /**
     * Get grouped bets counts for a particular game
     * @param gameId
     * @return
     */
    @Cacheable(value = "userBets3")
    @RequestMapping(value = "/gameStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Long> allBets(@RequestParam(value = "gameId") Integer gameId) {
        List<Bet> bets;
        if(gameId != null) {
            bets = betRepository.findByGame(new Game(gameId));
        } else {
            bets = Lists.newArrayList(betRepository.findAll());
        }
        Map<String, Long> stats = bets.stream().collect(Collectors.groupingBy(o -> o.getScoreResult().toString(), Collectors.counting()));
        stats.putAll(bets.stream().filter(bet -> bet.getOverResult() != null).collect(Collectors.groupingBy(o -> o.getOverResult().toString(), Collectors.counting())));

        return stats;
    }

    /**
     * Get bet deadline info
     * @return
     */
    @RequestMapping(value = "/betDeadline", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String betDeadline() {
        return betDeadline;
    }

}
