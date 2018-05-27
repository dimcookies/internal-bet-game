package bet.service.analytics;

import bet.api.constants.GameStatus;
import bet.model.Bet;
import bet.model.UserStreak;
import bet.repository.BetRepository;
import bet.repository.UserRepository;
import bet.repository.UserStreakRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

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

    @Override
    public void run() {
        LOGGER.info("Run streak module");
        userStreakRepository.deleteAll();
        StreamSupport.stream(userRepository.findAll().spliterator(),false).forEach(user -> {
            List<Bet> userBets = betRepository.findByUser(user);
            userBets.sort(Comparator.comparing(o -> o.getGame().getGameDate()));
            int streak = 0;
            boolean prevWon = false;
            for(Bet bet: userBets) {
                if(bet.getGame().getStatus().equals(GameStatus.FINISHED)) {
                    boolean won = bet.getResultPoints() > 0;
                    if(won != prevWon) {
                        streak =0;
                        prevWon = !prevWon;
                    }
                    if(won) {
                        streak++;
                    } else {
                        streak--;
                    }
                }
            }
            userStreakRepository.save(new UserStreak(streak, user));
        });
    }
}