package bet.utils;

import bet.api.constants.GameStatus;
import bet.base.AbstractBetIntegrationTest;
import bet.repository.GameRepository;
import bet.service.GamesInitializer;
import bet.service.utils.GamesSchedule;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.Arrays;


public class GameSchedulerTest extends AbstractBetIntegrationTest {

	@Autowired
	private GamesSchedule gameScheduler;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GamesInitializer gamesInitializer;

	@Before
	public void setUp() {
		super.setUp();
		gamesInitializer.initialize();
	}

	@Test
	public void testSchedule() {

		gameRepository.findAll().forEach(game -> {
			game.setStatus(GameStatus.IN_PLAY);
			gameRepository.save(game);
		});

		assertTrue(gameRepository.findAll().iterator().hasNext());
		ZonedDateTime c1 = ZonedDateTime.parse("2018-06-14T14:00:00Z[UTC]");

		assertFalse(gameScheduler.hasActiveGame(c1));
		assertTrue(gameScheduler.hasActiveGame(c1.plusMinutes(61)));


		gameRepository.findAll().forEach(game -> {
			game.setStatus(GameStatus.FINISHED);
			gameRepository.save(game);
		});

		assertFalse(gameScheduler.hasActiveGame(c1.plusMinutes(61)));



	}

	@Test
	public void testScheduleFinished() {

		gameRepository.findAll().forEach(game -> {
			game.setStatus(GameStatus.FINISHED);
			gameRepository.save(game);
		});

		ZonedDateTime c1 = ZonedDateTime.parse("2018-06-15T14:00:00Z[UTC]");

		assertEquals(3, gameScheduler.getGamesAtDate(c1, Arrays.asList(GameStatus.FINISHED)).size());


	}

}