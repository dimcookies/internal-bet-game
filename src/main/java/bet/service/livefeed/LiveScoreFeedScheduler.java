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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Periodically runs live feed and updates results and compute user bets
 */
@Component
public class LiveScoreFeedScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LiveScoreFeedScheduler.class);

	//interval for live update
	@Value("${application.live_feed.interval:300000}")
	private final int interval = 300000;

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

	@CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
	@Scheduled(fixedRate = interval)
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
		liveFeed.getLiveFeed().forEach(gameDto -> checkMatchChanged(gameDto));
		this.lastUpdateDate = now;
	}

	/**
	 * Checks if a game score has changed and updates results
	 * for user bets
	 * @param gameDto
	 */
	@CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
	public void checkMatchChanged(GameDto gameDto) {
		//if game already finished, do not check
		Game dbGame = gameRepository.findOne(gameDto.getId());
		if (dbGame.getStatus().equals(GameStatus.FINISHED)) {
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
		}

		//update game entry
		gameRepository.save(liveGame);
	}

	private int[] getPoints(Game game) {
		Odd odd = oddRepository.findOneByGame(game);

		//over points = betting factor * 0.5 * multiplier
		int overPoints = (int) Math.floor(odd.getOddForOver(game.getOverResult()) * 0.5 * odd.getMultiplier());

		//score points = betting factor * multiplier
		int scorePoints = (int) Math.floor(odd.getOddForScore(game.getScoreResult()) * odd.getMultiplier());

		return new int[] { scorePoints, overPoints };
	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
}
