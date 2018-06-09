package bet.service.analytics;

import bet.model.RankHistory;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.RankHistoryRepository;
import bet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private UserRepository userRepository;

    @Autowired
    private RankHistoryRepository rankHistoryRepository;

    @Override
    @CacheEvict(allEntries = true, cacheNames = {"analytics1","analytics1","analytics3","analytics4"})
    public void run() {
        LOGGER.info("Run user rank module");
        //get points for all users
        Map<String, Integer> allPoints = betRepository.listAllPoints();
        List<Map.Entry<String, Integer>> ranking = allPoints.entrySet().stream()
                //sort by total points and then by username
                .sorted((o1, o2) -> {
                    int cmp1 = o2.getValue().compareTo(o1.getValue());
                    if(cmp1 != 0) {
                        return  cmp1;
                    }
                    return o1.getKey().compareTo(o2.getKey());
                })
                //.map(entry -> entry.getKey())
                .collect(Collectors.toList());
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        //write rankings to database for current date
        IntStream.range(0, ranking.size()).forEach(idx -> {
            Map.Entry<String, Integer> entry = ranking.get(idx);
            User user = userRepository.findOneByUsername(entry.getKey());
            rankHistoryRepository.save(new RankHistory(idx+1, user, entry.getValue(),now));
        });
    }
}