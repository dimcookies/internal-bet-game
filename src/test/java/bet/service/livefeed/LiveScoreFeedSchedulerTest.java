package bet.service.livefeed;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.GameDto;
import bet.base.AbstractBetIntegrationTest;
import bet.model.Bet;
import bet.model.Game;
import bet.model.Odd;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.repository.UserRepository;
import bet.service.GamesInitializer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class LiveScoreFeedSchedulerTest extends AbstractBetIntegrationTest {

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

    @Before
    public void setUp() {
        super.setUp();
        gamesInitializer.initialize();
        userRepository.save(new User(null, "user1", "user1", "", "", "user1"));
    }

    @Test
    public void testLiveUpdate() {
        int userId1 =  userRepository.findOneByUsername("user1").getId();
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        List<Game> groupGames = gameRepository.findByMatchDay(1);
        GameDto groupGame = new GameDto();
        groupGame.fromEntity(groupGames.get(0));

        updateOdd(groupGame.getId(), 1.2f,3.4f,2.2f,5.5f,4.5f);
        Bet bet = betRepository.save(new Bet(null, groupGame.getId(), userId1, ScoreResult.HOME_1, 0, null, 0, now));

        changeMatchScore(groupGame, 1,0);
        checkPoints(bet.getId(), 120, 0);
        changeMatchScore(groupGame, 1,0);
        checkPoints(bet.getId(), 120, 0);
        changeMatchScore(groupGame, 1,1);
        checkPoints(bet.getId(), 0, 0);
        changeMatchScore(groupGame, 1,2);
        checkPoints(bet.getId(), 0, 0);

        betRepository.deleteAll();
        bet = betRepository.save(new Bet(null, groupGame.getId(), userId1, ScoreResult.DRAW_X, 0, null, 0, now));

        changeMatchScore(groupGame, 1,0);
        checkPoints(bet.getId(), 0, 0);
        changeMatchScore(groupGame, 1,1);
        checkPoints(bet.getId(), 340, 0);
        changeMatchScore(groupGame, 1,2);
        checkPoints(bet.getId(), 0, 0);

        betRepository.deleteAll();
        bet = betRepository.save(new Bet(null, groupGame.getId(), userId1, ScoreResult.AWAY_2, 0, OverResult.UNDER, 0, now));

        changeMatchScore(groupGame, 1,0);
        checkPoints(bet.getId(), 0, 0);
        changeMatchScore(groupGame, 1,1);
        checkPoints(bet.getId(), 0, 0);
        changeMatchScore(groupGame, 1,2);
        checkPoints(bet.getId(), 220, 0);

        betRepository.deleteAll();
        List<Game> playoffGames = gameRepository.findByMatchDay(4);
        GameDto playOffGame = new GameDto();
        playOffGame.fromEntity(playoffGames.get(0));
        updateOdd(playOffGame.getId(), 1.2f,3.4f,2.2f,5.5f,4.5f);

        Bet bet2 = betRepository.save(new Bet(null, playOffGame.getId(), userId1, ScoreResult.HOME_1, 0, OverResult.UNDER, 0, now));

        changeMatchScore(playOffGame, 1,0);
        checkPoints(bet2.getId(), 240, 550);
        changeMatchScore(playOffGame, 3,0);
        checkPoints(bet2.getId(), 240, 0);
        changeMatchScore(playOffGame, 0,1);
        checkPoints(bet2.getId(), 0, 0);
        changeMatchScore(playOffGame, 0,0);
        checkPoints(bet2.getId(), 0, 0);

        betRepository.deleteAll();
        bet2 = betRepository.save(new Bet(null, playOffGame.getId(), userId1, ScoreResult.DRAW_X, 0, OverResult.OVER, 0, now));

        changeMatchScore(playOffGame, 1,0);
        checkPoints(bet2.getId(), 0, 0);
        changeMatchScore(playOffGame, 3,0);
        checkPoints(bet2.getId(), 0, 0);
        changeMatchScore(playOffGame, 0,1);
        checkPoints(bet2.getId(), 0, 0);
        changeMatchScore(playOffGame, 0,0);
        checkPoints(bet2.getId(), 680, 0);
        changeMatchScore(playOffGame, 2,2);
        checkPoints(bet2.getId(), 680, 450);

        betRepository.deleteAll();
        bet2 = betRepository.save(new Bet(null, playOffGame.getId(), userId1, ScoreResult.AWAY_2, 0, OverResult.UNDER, 0, now));

        changeMatchScore(playOffGame, 1,0);
        checkPoints(bet2.getId(), 0, 0);
        changeMatchScore(playOffGame, 0,1);
        checkPoints(bet2.getId(), 440, 550);
        changeMatchScore(playOffGame, 0,3);
        checkPoints(bet2.getId(), 440, 0);
        changeMatchScore(playOffGame, 0,0);
        checkPoints(bet2.getId(), 0, 0);

        betRepository.deleteAll();
        List<Game> finalGames = gameRepository.findByMatchDay(8);
        GameDto finalGame = new GameDto();
        finalGame.fromEntity(finalGames.get(0));
        updateOdd(finalGame.getId(), 1.2f,3.4f,2.2f,5.5f,4.5f);

        Bet bet3 = betRepository.save(new Bet(null, finalGame.getId(), userId1, ScoreResult.HOME_1, 0, OverResult.UNDER, 0, now));

        changeMatchScore(finalGame, 2,0);
        checkPoints(bet3.getId(), 360, 825);
        changeMatchScore(finalGame, 3,0);
        checkPoints(bet3.getId(), 360, 0);
        changeMatchScore(finalGame, 0,1);
        checkPoints(bet3.getId(), 0, 0);
        changeMatchScore(finalGame, 0,0);
        checkPoints(bet3.getId(), 0, 0);


    }

    private void changeMatchScore(GameDto dto, int goalsHomeTeam, int goalsAwayTeam) {
        dto.getResult().setGoalsHomeTeam(goalsHomeTeam);
        dto.getResult().setGoalsAwayTeam(goalsAwayTeam);
        liveScoreFeedScheduler.checkMatchChanged(dto);
    }

    private void updateOdd(int gameId, float oddHome, float oddTie, float oddAway, float oddUnder, float oddOver) {
        Odd odd = oddRepository.findOneByGame(gameRepository.findOne(gameId));
        odd.setOddsHome(oddHome);
        odd.setOddsTie(oddTie);
        odd.setOddsAway(oddAway);
        odd.setOddsUnder(oddUnder);
        odd.setOddsOver(oddOver);
        oddRepository.save(odd);

    }

    private void checkPoints(int betId, int resultPoints, int overPoints) {
        Bet bet = betRepository.findOne(betId);
        assertEquals(resultPoints, bet.getResultPoints());
        assertEquals(overPoints, bet.getOverPoints());
    }



}
