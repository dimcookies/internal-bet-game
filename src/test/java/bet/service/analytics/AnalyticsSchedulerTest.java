package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.EncryptedBetDto;
import bet.api.dto.GameDto;
import bet.base.AbstractBetIntegrationTest;
import bet.model.*;
import bet.repository.*;
import bet.service.GamesInitializer;
import bet.service.analytics.AnalyticsScheduler;
import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.mgmt.EncryptedBetService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
    private AnalyticsScheduler analyticsScheduler;

    @Autowired
    private RankHistoryRepository rankHistoryRepository;

    @Before
    public void setUp() {
        super.setUp();
        gamesInitializer.initialize();
        userRepository.save(new User(null, "user1", "user1", "", ""));
        userRepository.save(new User(null, "user2", "user2", "", ""));
    }

    @Test
    public void testSaveRank() {
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        int userId1 = userRepository.findOneByName("user1").getId();
        int userId2 = userRepository.findOneByName("user2").getId();
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(0).getId(), userId2, ScoreResult.HOME_1, 500, OverResult.OVER, 600, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId2, ScoreResult.HOME_1, 700, OverResult.OVER, 800, now));

        analyticsScheduler.saveUserRank();

        List<RankHistory> ranking = Lists.newArrayList(rankHistoryRepository.findAll());
        assertEquals(2, ranking.size());
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId2 && rankHistory.getRank() == 1));
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId1 && rankHistory.getRank() == 2));

        analyticsScheduler.saveUserRank();
        ranking = Lists.newArrayList(rankHistoryRepository.findAll());
        assertEquals(4, ranking.size());

        rankHistoryRepository.deleteAll();
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.HOME_1, 1000, OverResult.OVER, 2000, now));
        analyticsScheduler.saveUserRank();
        ranking = Lists.newArrayList(rankHistoryRepository.findAll());
        assertEquals(2, ranking.size());
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId1 && rankHistory.getRank() == 1));
        assertTrue(ranking.stream().anyMatch(rankHistory -> rankHistory.getUser().getId() == userId2 && rankHistory.getRank() == 2));


    }





}
