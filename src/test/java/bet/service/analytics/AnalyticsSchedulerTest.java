package bet.service.analytics;

import bet.api.constants.GameStatus;
import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.base.AbstractBetIntegrationTest;
import bet.model.*;
import bet.repository.*;
import bet.service.GamesInitializer;
import bet.service.livefeed.LiveScoreFeedScheduler;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class AnalyticsSchedulerTest extends AbstractBetIntegrationTest {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private LiveScoreFeedScheduler liveScoreFeedScheduler;

    @Autowired
    private GamesInitializer gamesInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OddRepository oddRepository;

    @Autowired
    private StreakAnalyticsModule streakAnalyticsModule;

    @Autowired
    private UserRankAnalyticsModule userRankAnalyticsModule;

    @Autowired
    private RankHistoryRepository rankHistoryRepository;

    @Autowired
    private UserStreakRepository userStreakRepository;

    @Autowired
    private UserStreakHistoryRepository userStreakHistoryRepository;

    @Before
    public void setUp() {
        super.setUp();
        gamesInitializer.initialize();
        userRepository.save(new User(null, "user1", "user1", "", "", "user1"));
        userRepository.save(new User(null, "user2", "user2", "", "", "user2"));
    }

    @Test
    public void testSaveRank() {
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        int userId1 = userRepository.findOneByUsername("user1").getId();
        int userId2 = userRepository.findOneByUsername("user2").getId();
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(0).getId(), userId2, ScoreResult.HOME_1, 500, OverResult.OVER, 600, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId2, ScoreResult.HOME_1, 700, OverResult.OVER, 800, now));

        userRankAnalyticsModule.run();

        List<RankHistory> ranking = Lists.newArrayList(rankHistoryRepository.findAll());
        assertEquals(2, ranking.size());
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId2 && rankHistory.getRank() == 1 && rankHistory.getPoints() == 2600));
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId1 && rankHistory.getRank() == 2 && rankHistory.getPoints() == 1000));

        userRankAnalyticsModule.run();
        ranking = Lists.newArrayList(rankHistoryRepository.findAll());
        assertEquals(4, ranking.size());

        rankHistoryRepository.deleteAll();
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.HOME_1, 1000, OverResult.OVER, 2000, now));
        userRankAnalyticsModule.run();
        ranking = Lists.newArrayList(rankHistoryRepository.findAll());
        assertEquals(2, ranking.size());
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId1 && rankHistory.getRank() == 1 && rankHistory.getPoints() == 4000));
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId2 && rankHistory.getRank() == 2 && rankHistory.getPoints() == 2600));
    }


    @Test
    public void testCalculateStreak() {

        gameRepository.findAll().forEach(game -> {
            game.setStatus(GameStatus.FINISHED);
            gameRepository.save(game);
        });

        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        games.sort(Comparator.comparing(Game::getGameDate));

        int userId1 = userRepository.findOneByUsername("user1").getId();
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        streakAnalyticsModule.run();
        validateStreak(userId1, 3);
        validateStreakHistory(userId1, 3, 0);
        betRepository.deleteAll();
        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.HOME_1, 0, OverResult.OVER, 0, now));
        streakAnalyticsModule.run();
        validateStreak(userId1, -1);
        validateStreakHistory(userId1, 3, -1);
        betRepository.deleteAll();
        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.HOME_1, 0, OverResult.OVER, 0, now));
        betRepository.save(new Bet(null, games.get(3).getId(), userId1, ScoreResult.HOME_1, 0, OverResult.OVER, 0, now));
        streakAnalyticsModule.run();
        validateStreak(userId1, -2);
        validateStreakHistory(userId1, 3, -2);
        betRepository.deleteAll();
        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.HOME_1, 0, OverResult.OVER, 0, now));
        betRepository.save(new Bet(null, games.get(3).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        streakAnalyticsModule.run();
        validateStreak(userId1, 1);
        validateStreakHistory(userId1, 3, -2);

        betRepository.deleteAll();
        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.HOME_1, 0, OverResult.OVER, 0, now));
        betRepository.save(new Bet(null, games.get(3).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(4).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        streakAnalyticsModule.run();
        validateStreak(userId1, 2);
        validateStreakHistory(userId1, 3, -2);

    }

    private void validateStreak(int userId, int correctStreak) {
        List<UserStreak> streaks = Lists.newArrayList(userStreakRepository.findAll());

        assertEquals(correctStreak, (long)streaks.stream().filter(userStreak -> userStreak.getUser().getId().equals(userId)).map(UserStreak::getStreak).findFirst().get());

    }

    private void validateStreakHistory(int userId, int correctMaxStreak, int correctMinStreak) {
        List<UserStreakHistory> streaks = Lists.newArrayList(userStreakHistoryRepository.findAll());

        Optional<UserStreakHistory> history = streaks.stream().filter(userStreak -> userStreak.getUser().getId().equals(userId)).findFirst();
        assertTrue(history.isPresent());
        assertEquals(correctMaxStreak, (int)history.get().getMaxStreak());
        assertEquals(correctMinStreak, (int)history.get().getMinStreak());

    }



}
