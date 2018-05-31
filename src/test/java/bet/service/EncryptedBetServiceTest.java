package bet.service;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.EncryptedBetDto;
import bet.base.AbstractBetIntegrationTest;
import bet.model.Bet;
import bet.model.EncryptedBet;
import bet.model.Game;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.EncryptedBetRepository;
import bet.repository.GameRepository;
import bet.repository.UserRepository;
import bet.service.GamesInitializer;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EncryptedBetServiceTest extends AbstractBetIntegrationTest {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private EncryptedBetService encryptedBetService;

    @Autowired
    private EncryptedBetRepository encryptedBetRepository;

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
        userRepository.save(new User(null, "user1", "user1", "", "", "user1"));
        userRepository.save(new User(null, "user2", "user2", "", "", "user2"));
    }

    @Test
    public void testEncryptionSalt() {
        List<User> users = Lists.newArrayList(userRepository.findAll());
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        int userId1 = users.get(0).getId();
        int userId2 = users.get(1).getId();
        int gameId1 = games.get(0).getId();
        int gameId2 = games.get(1).getId();
        String now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toString();

        encryptedBetService.create(new EncryptedBetDto(null, gameId1, userId1, ScoreResult.HOME_1.toString(), OverResult.OVER.toString(), now));
        encryptedBetService.create(new EncryptedBetDto(null, gameId2, userId1, ScoreResult.HOME_1.toString(), OverResult.OVER.toString(), now));
        encryptedBetService.create(new EncryptedBetDto(null, gameId1, userId2, ScoreResult.HOME_1.toString(), OverResult.OVER.toString(), now));
        encryptedBetService.create(new EncryptedBetDto(null, gameId2, userId2, ScoreResult.HOME_1.toString(), OverResult.OVER.toString(), now));

        List<EncryptedBet> encryptedBets = Lists.newArrayList(encryptedBetRepository.findAll());

        assertEquals(4, encryptedBets.stream().map(EncryptedBet::getScoreResult).collect(Collectors.toSet()).size());
        assertEquals(4, encryptedBets.stream().map(EncryptedBet::getOverResult).collect(Collectors.toSet()).size());
    }

    @Test
    public void testDecryptAndCopy() {
        List<User> users = Lists.newArrayList(userRepository.findAll());
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        int userId1 = users.get(0).getId();
        int userId2 = users.get(1).getId();
        int gameId1 = games.get(0).getId();
        int gameId2 = games.get(1).getId();
        String now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toString();

        encryptedBetService.create(new EncryptedBetDto(null, gameId1, userId1, ScoreResult.HOME_1.toString(), OverResult.OVER.toString(), now));
        encryptedBetService.create(new EncryptedBetDto(null, gameId2, userId1, ScoreResult.AWAY_2.toString(), OverResult.UNDER.toString(), now));
        encryptedBetService.create(new EncryptedBetDto(null, gameId1, userId2, ScoreResult.DRAW_X.toString(), OverResult.OVER.toString(), now));
        encryptedBetService.create(new EncryptedBetDto(null, gameId2, userId2, ScoreResult.DRAW_X.toString(), null, now));

        assertEquals(4, encryptedBetService.list().size());



        encryptedBetService.decryptAndCopy();

        assertEquals(0, encryptedBetService.list().size());
        List<Bet> bets = Lists.newArrayList(betRepository.findAll());
        assertEquals(4, bets.size());

        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId1 && bet.getGame().getId() == gameId1
            && bet.getScoreResult().equals(ScoreResult.HOME_1) && bet.getOverResult().equals(OverResult.OVER)));
        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId1 && bet.getGame().getId() == gameId2
                && bet.getScoreResult().equals(ScoreResult.AWAY_2) && bet.getOverResult().equals(OverResult.UNDER)));
        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId2 && bet.getGame().getId() == gameId1
                && bet.getScoreResult().equals(ScoreResult.DRAW_X) && bet.getOverResult().equals(OverResult.OVER)));
        assertTrue(bets.stream().anyMatch(bet -> bet.getUser().getId() == userId2 && bet.getGame().getId() == gameId2
                && bet.getScoreResult().equals(ScoreResult.DRAW_X) && bet.getOverResult() == null));

    }

    @Test
    public void testCreateAll() {

        List<User> users = Lists.newArrayList(userRepository.findAll());
        List<Game> games = Lists.newArrayList(gameRepository.findAll());
        User user = users.get(0);
        int gameId1 = games.get(0).getId();
        int gameId2 = games.get(1).getId();
        String now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toString();

        IntStream.range(0,3).forEach(value -> {
            List<EncryptedBetDto> bets = Arrays.asList(new EncryptedBetDto(null, gameId1, null, ScoreResult.HOME_1.toString(), OverResult.OVER.toString(), now),
                    new EncryptedBetDto(null, gameId2, null, ScoreResult.AWAY_2.toString(), OverResult.UNDER.toString(), now));

            encryptedBetService.createAll(bets, user);

            assertEquals(2, encryptedBetService.list().size());

            List<EncryptedBetDto> dbBets = Lists.newArrayList(encryptedBetService.list());
            assertTrue(dbBets.stream().anyMatch(bet -> bet.getUserId() == user.getId() && bet.getGameId() == gameId1
                    && bet.getScoreResult().equals(ScoreResult.HOME_1.toString()) && bet.getOverResult().equals(OverResult.OVER.toString())));
            assertTrue(dbBets.stream().anyMatch(bet -> bet.getUserId() == user.getId() && bet.getGameId() == gameId2
                    && bet.getScoreResult().equals(ScoreResult.AWAY_2.toString()) && bet.getOverResult().equals(OverResult.UNDER.toString())));
        });

    }

}
