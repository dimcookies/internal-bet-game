package bet.service.analytics;

import bet.api.constants.GameStatus;
import bet.model.Bet;
import bet.model.UserStreak;
import bet.model.UserStreakHistory;
import bet.repository.BetRepository;
import bet.repository.UserRepository;
import bet.repository.UserStreakHistoryRepository;
import bet.repository.UserStreakRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Compute the current streak of each user.
 * Streak is positive for consecutive correct guesses in bets, negative for incorrect.
 * The order to check for streak is game dates
 */
@Component
@Analytics
public class StreakAnalyticsModule implements AnalyticsModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreakAnalyticsModule.class);

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStreakRepository userStreakRepository;

    @Autowired
    private UserStreakHistoryRepository userStreakHistoryRepository;

    @Override
    @CacheEvict(allEntries = true, cacheNames = {"analytics1", "analytics1", "analytics3", "analytics4", "analytics5", "analytics6"})
    public void run() {
        LOGGER.info("Run streak module");
        //delete current values
        userStreakHistoryRepository.deleteAll();
        userStreakRepository.deleteAll();
        //for all users
        StreamSupport.stream(userRepository.findAll().spliterator(),false).forEach(user -> {
            //get all user bets for completed matches order by date
            List<Bet> userBets = betRepository.findByUser(user).stream()
                    .filter(bet -> bet.getGame().getStatus().equals(GameStatus.FINISHED))
                    .sorted(Comparator.comparing(bet -> bet.getGame().getGameDate()))
                    .collect(Collectors.toList());
            int streak = 0;
            boolean isPrevCorrect = false;

            List<Integer> userStreaks = new ArrayList<>();

            for(Bet bet: userBets) {
                boolean isCurrentCorrect = bet.getResultPoints() > 0;
                //in case of status change
                if (isCurrentCorrect != isPrevCorrect) {
                    //save streak
                    userStreaks.add(streak);
                    //reset streak
                    streak = 0;
                    isPrevCorrect = !isPrevCorrect;
                }
                if (isCurrentCorrect) { //correct, positive streak
                    streak++;
                } else { //incorrect, negative streak
                    streak--;
                }
            }
            userStreaks.add(streak);
            //save current user streak
            userStreakRepository.save(new UserStreak(streak, user));
            //save min and max streak of user
            userStreakHistoryRepository.save(new UserStreakHistory(Collections.max(userStreaks), Collections.min(userStreaks), user));
        });
    }
}