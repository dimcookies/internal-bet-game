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

    @Override
    public void run() {
        LOGGER.info("Run streak module");
        //delete current values
        userStreakRepository.deleteAll();
        //for all users
        StreamSupport.stream(userRepository.findAll().spliterator(),false).forEach(user -> {
            //get all bets
            List<Bet> userBets = betRepository.findByUser(user);
            //sort by game date
            userBets.sort(Comparator.comparing(o -> o.getGame().getGameDate()));
            int streak = 0;
            boolean isPrevCorrect = false;

            for(Bet bet: userBets) {
                //compute streak only for finished games
                if(bet.getGame().getStatus().equals(GameStatus.FINISHED)) {
                    boolean isCurrentCorrect = bet.getResultPoints() > 0;
                    //in case of status change set streak to zero
                    if(isCurrentCorrect != isPrevCorrect) {
                        streak =0;
                        isPrevCorrect = !isPrevCorrect;
                    }
                    if(isCurrentCorrect) { //correct, positive streak
                        streak++;
                    } else { //incorrect, negative streak
                        streak--;
                    }
                }
            }
            //save updated value
            userStreakRepository.save(new UserStreak(streak, user));
        });
    }
}