package bet.service.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * A scheduler for reporting odules.
 */
@Component
public class AnalyticsScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsScheduler.class);

	@Autowired
	private ApplicationContext context;

	/* Date analytics was run */
	private ZonedDateTime lastUpdateDate;

	//@Scheduled(cron = "*/10 * * * * *")
	@CacheEvict(allEntries = true, cacheNames = {"analytics1", "analytics1", "analytics3", "analytics4", "analytics5", "analytics6"})
	@Scheduled(cron = "0 0 1 * * *")
	public void runAnalytics() {

		LOGGER.info("Analytics run");
		//get all reporting modules and run
		Map<String,Object> customPageActions = context.getBeansWithAnnotation(Analytics.class);
		customPageActions.forEach((s, o) -> ((AnalyticsModule)o).run());

		this.lastUpdateDate = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

}
