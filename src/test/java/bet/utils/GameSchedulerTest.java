package bet.utils;

import bet.base.AbstractBetIntegrationTest;
import bet.repository.GameRepository;
import bet.service.GamesInitializer;
import bet.service.utils.GameScheduler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;


public class GameSchedulerTest extends AbstractBetIntegrationTest {

	@Autowired
	private GameScheduler gameScheduler;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GamesInitializer gamesInitializer;

	@Before
	public void setUp() {
		super.setUp();
		gamesInitializer.initialize();
	}
//
//	@After
//	@Override
//	public void tearDown() {
//	}

	@Test
	public void testSchedule() {
		assertTrue(gameRepository.findAll().iterator().hasNext());
		ZonedDateTime c1 = ZonedDateTime.parse("2018-06-16T09:00:00Z[UTC]");

		assertFalse(gameScheduler.hasActiveGame(c1));
		assertFalse(gameScheduler.hasActiveGame(c1.plusMinutes(30)));
		assertTrue(gameScheduler.hasActiveGame(c1.plusMinutes(61)));
		assertTrue(gameScheduler.hasActiveGame(c1.plusHours(5)));

		ZonedDateTime c2 = ZonedDateTime.parse("2018-06-16T23:00:00Z[UTC]");
		assertTrue(gameScheduler.hasActiveGame(c2.minusMinutes(1)));
		assertFalse(gameScheduler.hasActiveGame(c2.plusMinutes(1)));

	}

}