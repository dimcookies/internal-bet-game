package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.dto.GameDto;
import bet.model.Game;
import bet.model.Odd;
import bet.repository.BetRepository;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.service.cache.ClearCacheTask;
import bet.service.utils.EhCacheUtils;
import bet.service.utils.GamesSchedule;
import bet.web.BetsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Periodically runs live feed and updates results and compute user bets
 */
@Component
public class LiveScoreFeedScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LiveScoreFeedScheduler.class);

	//interval for live update
	@Value("${application.live_feed.interval:300000}")
	private int interval;

	//last update date of live feed
	private ZonedDateTime lastUpdateDate;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private OddRepository oddRepository;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private LiveFeed liveFeed;

	@Autowired
	private GamesSchedule gameScheduler;

	@Autowired
	private ClearCacheTask clearCacheTask;

	@Autowired
	private BetsController betsController;

	@CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
	@Scheduled(fixedRateString="${application.live_feed.interval}")
	public void getLiveScores() {
		getLiveScores(true);
	}

	@CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
	public void getLiveScores(boolean checkForActiveMatches) {
		LOGGER.trace("Check live scores");
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

		//If there are no matches now, do not try to update scores
		if(checkForActiveMatches) {
			if(!gameScheduler.hasActiveGame(now)) {
				LOGGER.trace("No active games");
				return;
			}
		}

		LOGGER.info("Getting live feed");
		clearCacheTask.clearCaches();
		EhCacheUtils.clearCache();
		liveFeed.getLiveFeed().forEach(this::checkMatchChanged);
		this.lastUpdateDate = now;
	}


	@CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
	public void checkMatchChanged(GameDto gameDto) {
		this.checkMatchChanged(gameDto, false);
	}

	/**
	 * Checks if a game score has changed and updates results
	 * for user bets
	 * @param gameDto
	 */
	@CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
	public void checkMatchChanged(GameDto gameDto, boolean ignoreStatus) {
		//if game already finished, do not check
		Game dbGame = gameRepository.findOne(gameDto.getId());
		if (dbGame.getStatus().equals(GameStatus.FINISHED) && !ignoreStatus) {
			return;
		}
		Game liveGame = gameDto.toEntity();

		//if score has changed
		if (liveGame.getGoalsHome() != dbGame.getGoalsHome() || liveGame.getGoalsAway() != dbGame.getGoalsAway()
				|| liveGame.getStatus() != dbGame.getStatus() ) {
			LOGGER.info("Game changed:" + liveGame.toString());

			//get odd results (0-> result points 1->over points)
			int[] oddPoints = getPoints(liveGame);

			//find all bets for this game
			betRepository.findByGame(liveGame).forEach(bet -> {
				//if bet has the correct result
				if (liveGame.getScoreResult().equals(bet.getScoreResult())) {
					bet.setResultPoints(oddPoints[0]);
					//if this is not a group stage match and bet has the correct over result
					if ((!liveGame.isGroupStage()) && liveGame.getOverResult().equals(bet.getOverResult())) {
						bet.setOverPoints(oddPoints[1]);
					} else {
						//over bet does not match score, zero over points
						bet.setOverPoints(0);
					}
				} else {
					//result bet does not match score, zero all points
					bet.setResultPoints(0);
					bet.setOverPoints(0);
				}
				//save bet
				betRepository.save(bet);
			});
			//logWinners();
		}

		//update game entry
		gameRepository.save(liveGame);
	}

	private void logWinners() {
		try {
			LOGGER.info("##########");
			List<Map<String, Object>> points = betsController.allPoints();
			points.subList(0, 10).forEach(entry -> {
				LOGGER.info(String.format(" %s %s %s ", entry.get("idx").toString(), entry.get("name").toString(), entry.get("points").toString()));
			});
			LOGGER.info("-------------");
			points.subList(points.size() - 4, points.size() - 1).forEach(entry -> {
				LOGGER.info(String.format(" %s %s %s ", entry.get("idx").toString(), entry.get("name").toString(), entry.get("points").toString()));
			});
			LOGGER.info("##########");
		} catch (Throwable e) {
			LOGGER.error("Error during logging winners ", e);
		}
	}

	private int[] getPoints(Game game) {
		Odd odd = oddRepository.findOneByGame(game);

		//over points = betting factor * 0.5 * multiplier
		int overPoints = (int) Math.round(odd.getOddForOver(game.getOverResult()) * 0.5 * odd.getMultiplier());

		//score points = betting factor * multiplier
		int scorePoints = (int) Math.round(odd.getOddForScore(game.getScoreResult()) * odd.getMultiplier());

		return new int[] { scorePoints, overPoints };
	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}
