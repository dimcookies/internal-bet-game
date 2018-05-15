package bet.repository;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.EncryptedBetDto;
import bet.base.AbstractBetIntegrationTest;
import bet.model.Bet;
import bet.model.Game;
import bet.model.User;
import bet.service.GamesInitializer;
import bet.service.mgmt.EncryptedBetService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EncryptedBetServiceTest extends AbstractBetIntegrationTest {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private EncryptedBetService encryptedBetService;

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
        userRepository.save(new User(null, "user1", "user1", "", ""));
        userRepository.save(new User(null, "user2", "user2", "", ""));
    }

    @Test
    public void testDescryptAndCopy() {
        List<User> users = Lists.newArrayList(userRepository.findAll());
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        int userId1 = users.get(0).getId();
        int userId2 = users.get(1).getId();
        int gameId1 = games.get(0).getId();
        int gameId2 = games.get(1).getId();
        encryptedBetService.create(new EncryptedBetDto(null, gameId1, userId1, ScoreResult.SCORE_1.toString(), OverResult.OVER.toString()));
        encryptedBetService.create(new EncryptedBetDto(null, gameId2, userId1, ScoreResult.SCORE_2.toString(), OverResult.UNDER.toString()));
        encryptedBetService.create(new EncryptedBetDto(null, gameId1, userId2, ScoreResult.SCORE_X.toString(), OverResult.OVER.toString()));
        encryptedBetService.create(new EncryptedBetDto(null, gameId2, userId2, ScoreResult.SCORE_X.toString(), OverResult.UNDER.toString()));

        assertEquals(4, encryptedBetService.list().size());

        encryptedBetService.decryptAndCopy();

        assertEquals(0, encryptedBetService.list().size());
        List<Bet> bets = Lists.newArrayList(betRepository.findAll());
        assertEquals(4, bets.size());

        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId1 && bet.getGame().getId() == gameId1
            && bet.getScoreResult().equals(ScoreResult.SCORE_1) && bet.getOverResult().equals(OverResult.OVER)));
        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId1 && bet.getGame().getId() == gameId2
                && bet.getScoreResult().equals(ScoreResult.SCORE_2) && bet.getOverResult().equals(OverResult.UNDER)));
        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId2 && bet.getGame().getId() == gameId1
                && bet.getScoreResult().equals(ScoreResult.SCORE_X) && bet.getOverResult().equals(OverResult.OVER)));
        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId2 && bet.getGame().getId() == gameId2
                && bet.getScoreResult().equals(ScoreResult.SCORE_X) && bet.getOverResult().equals(OverResult.UNDER)));

    }

}
