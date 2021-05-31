package bet.repository;

import bet.base.AbstractBetIntegrationTest;
import bet.model.Deadline;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class DeadlineRepositoryTest extends AbstractBetIntegrationTest {

	@Autowired
	private DeadlineRepository deadlineRepository;

	@Before
	public void setUp() {
		super.setUp();
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

		deadlineRepository.save(Arrays.asList(
				new Deadline(null, now.minusDays(2), now.minusDays(1), "1", "2", "Text1"),
				new Deadline(null, now.minusDays(1), now, "3", null, null),
				new Deadline(null, now, now.plusDays(1), "2,3", "4,5", "Text2")));
	}

	@Test
	public void testActiveDeadline() {
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
		Deadline deadline = deadlineRepository.findActiveDeadline(now.minusDays(1).minusMinutes(10), now.minusDays(1).minusMinutes(10));
		assertNotNull(deadline);
		assertEquals("1", deadline.getCurrentMatchDays());
		assertEquals("2", deadline.getAllowedMatchDays());
		assertEquals("Text1", deadline.getBetDeadlineText());

		deadline = deadlineRepository.findActiveDeadline(now.minusMinutes(10), now.minusMinutes(10));
		assertNotNull(deadline);
		assertEquals("3", deadline.getCurrentMatchDays());
		assertNull(deadline.getAllowedMatchDays());
		assertNull(deadline.getBetDeadlineText());
	}
}

