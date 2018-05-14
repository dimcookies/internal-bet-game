package bet.repository;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.base.AbstractBetIntegrationTest;
import bet.model.Bet;
import bet.model.Game;
import bet.model.User;
import bet.service.GamesInitializer;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BetRepositoryTest extends AbstractBetIntegrationTest {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private GamesInitializer gamesInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Before
    public void setUp() {
        super.setUp();
        gamesInitializer.initialize();
        userRepository.save(new User("user1"));
        userRepository.save(new User("user2"));
    }

    @Test
    public void testListAllPoints() {
        List<User> users = Lists.newArrayList(userRepository.findAll());
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        betRepository.save(new Bet(games.get(0).getId(), users.get(0).getId(), ScoreResult.SCORE_1, 100, OverResult.OVER, 200));
        betRepository.save(new Bet(games.get(1).getId(), users.get(0).getId(), ScoreResult.SCORE_1, 300, OverResult.OVER, 400));
        betRepository.save(new Bet(games.get(0).getId(), users.get(1).getId(), ScoreResult.SCORE_1, 500, OverResult.OVER, 600));
        betRepository.save(new Bet(games.get(1).getId(), users.get(1).getId(), ScoreResult.SCORE_1, 700, OverResult.OVER, 800));
        Map<String, Integer> points = betRepository.listAllPoints();

        assertEquals(2, points.size());
        assertEquals(1000, (int) points.get("user1"));
        assertEquals(2600, (int) points.get("user2"));
    }

}
