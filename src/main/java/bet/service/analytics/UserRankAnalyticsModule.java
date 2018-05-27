package bet.service.analytics;

import bet.model.RankHistory;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.RankHistoryRepository;
import bet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public void run() {
        LOGGER.info("Run user rank module");
        Map<String, Integer> allPoints = betRepository.listAllPoints();
        List<String> ranking = allPoints.entrySet().stream()
                .sorted((o1, o2) -> {
                    int cmp1 = o2.getValue().compareTo(o1.getValue());
                    if(cmp1 != 0) {
                        return  cmp1;
                    }
                    return o1.getKey().compareTo(o2.getKey());
                })
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        IntStream.range(0, ranking.size()).forEach(idx -> {
            User user = userRepository.findOneByName(ranking.get(idx));
            rankHistoryRepository.save(new RankHistory(idx+1, user, now));
        });
    }
}