package bet.service.analytics;

import bet.api.constants.GameStatus;
import bet.model.Game;
import bet.service.cache.ClearCacheTask;
import bet.service.utils.GamesSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A scheduler for reporting odules.
 */
@Component
public class AnalyticsScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsScheduler.class);

	@Autowired
	private ApplicationContext context;

    @Autowired
    private ClearCacheTask clearCacheTask;

	/* Date analytics was run */
	private ZonedDateTime lastUpdateDate;

	@Autowired
	private GamesSchedule gamesSchedule;

	//@Scheduled(cron = "*/10 * * * * *")
	@CacheEvict(allEntries = true, cacheNames = {"analytics1", "analytics1", "analytics3", "analytics4", "analytics5", "analytics6", "analytics7"})
	@Scheduled(cron = "0 0 1 * * *")
	public void runAnalytics() {
		//check if there was any games for yesterday. If not do not run analytics
		List<Game> yesterdayGames = gamesSchedule.getGamesAtDate(ZonedDateTime.now().minusDays(1), Arrays.asList(GameStatus.FINISHED));
		if (yesterdayGames.size() == 0) {
			return;
		}
		LOGGER.info("Analytics run");
        clearCacheTask.clearCaches();
		//get all reporting modules and run
		Map<String,Object> customPageActions = context.getBeansWithAnnotation(Analytics.class);
		customPageActions.forEach((s, o) -> ((AnalyticsModule)o).run());

		this.lastUpdateDate = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

}
