package bet.service.analytics;

import bet.api.dto.UserDto;
import bet.model.RankHistory;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.CustomBetRepository;
import bet.repository.RankHistoryRepository;
import bet.repository.UserRepository;
import bet.service.mgmt.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Compute the user rank of users by summing points
 * from bets.
 *
 */
@Component
@Analytics
public class UserRankAnalyticsModule implements AnalyticsModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRankAnalyticsModule.class);

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private CustomBetRepository customBetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RankHistoryRepository rankHistoryRepository;

    @Autowired
    private UserService userService;

    @Override
    @CacheEvict(allEntries = true, cacheNames = {"analytics1", "analytics1", "analytics3", "analytics4", "analytics5", "analytics6"})
    public void run() {
        LOGGER.info("Run user rank module");
        //get points for all users
        Map<String, Integer> allPoints = customBetRepository.listAllPoints();
        Map<String, String> names = userService.list().stream()
                .collect(Collectors.toMap(UserDto::getUsername, UserDto::getName));
        List<Map<String, Object>> ranking = allPoints.entrySet().stream()
                .map(e -> new HashMap<String, Object>() {{
                    put("username", e.getKey());
                    put("points", e.getValue());
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
                .collect(Collectors.toList());

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        //write rankings to database for current date
        IntStream.range(0, ranking.size()).forEach(idx -> {
            Map<String, Object> rankEntry = ranking.get(idx);
            User user = userRepository.findOneByUsername(rankEntry.get("username").toString());
            rankHistoryRepository.save(new RankHistory(idx+1, user, (Integer)rankEntry.get("points"),now));
        });
    }
}