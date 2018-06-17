package bet.repository;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.base.AbstractBetIntegrationTest;
import bet.model.Bet;
import bet.model.Game;
import bet.model.Odd;
import bet.model.User;
import bet.service.GamesInitializer;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class BetRepositoryTest extends AbstractBetIntegrationTest {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private CustomBetRepository customBetRepository;

    @Autowired
    private GamesInitializer gamesInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OddRepository oddRepository;

    @Before
    public void setUp() {
        super.setUp();
        gamesInitializer.initialize();
        userRepository.save(new User(null, "user1", "user1", "", "", "user1",false));
        userRepository.save(new User(null, "user2", "user2", "", "", "user2", false));
    }

    @Test
    public void testListAllPoints() {
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        int userId1 = userRepository.findOneByUsername("user1").getId();
        int userId2 = userRepository.findOneByUsername("user2").getId();
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        betRepository.save(new Bet(null, games.get(0).getId(), userId1, ScoreResult.HOME_1, 100, OverResult.OVER, 200, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 300, OverResult.OVER, 400, now));
        betRepository.save(new Bet(null, games.get(0).getId(), userId2, ScoreResult.HOME_1, 500, OverResult.OVER, 600, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId2, ScoreResult.HOME_1, 700, OverResult.OVER, 800, now));
        Map<String, Integer> points = customBetRepository.listAllPoints();

        assertEquals(2, points.size());
        assertEquals(1000, (int) points.get("user1"));
        assertEquals(2600, (int) points.get("user2"));
    }

    @Test
    public void testListRiskIndex() {
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        Odd odd1 = updateOdd(games.get(1).getId(), 1.2f, 3.4f, 2.2f, 5.5f, 4.5f);
        Odd odd2 = updateOdd(games.get(2).getId(), 2.2f, 4.4f, 3.2f, 6.5f, 5.5f);
        int userId1 = userRepository.findOneByUsername("user1").getId();
        int userId2 = userRepository.findOneByUsername("user2").getId();
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        betRepository.save(new Bet(null, games.get(1).getId(), userId1, ScoreResult.HOME_1, 0, OverResult.OVER, 0, now));
        betRepository.save(new Bet(null, games.get(2).getId(), userId1, ScoreResult.AWAY_2, 0, OverResult.OVER, 0, now));
        betRepository.save(new Bet(null, games.get(1).getId(), userId2, ScoreResult.DRAW_X, 0, OverResult.UNDER, 0, now));
        betRepository.save(new Bet(null, games.get(2).getId(), userId2, ScoreResult.HOME_1, 0, null, 0, now));

        Map<String, Double> riskIndex = customBetRepository.listRiskIndex();
        assertEquals(odd1.getOddsHome() + odd1.getOddsOver() + odd2.getOddsAway() + odd2.getOddsOver(), riskIndex.get("user1"), 0.001);
        assertEquals(odd1.getOddsTie() + odd1.getOddsUnder() + odd2.getOddsHome(), riskIndex.get("user2"), 0.001);

    }

    private Odd updateOdd(int gameId, float oddHome, float oddTie, float oddAway, float oddUnder, float oddOver) {
        Odd odd = oddRepository.findOneByGame(gameRepository.findOne(gameId));
        odd.setOddsHome(oddHome);
        odd.setOddsTie(oddTie);
        odd.setOddsAway(oddAway);
        odd.setOddsUnder(oddUnder);
        odd.setOddsOver(oddOver);
        oddRepository.save(odd);
        return odd;
    }
}
