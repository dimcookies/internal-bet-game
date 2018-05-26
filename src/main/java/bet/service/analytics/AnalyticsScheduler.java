package bet.service.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsScheduler.class);

	@Scheduled(cron = "*/10 * * * * *")
	public void getRssFeed() {
		LOGGER.info("Analytics run");
	}


}
