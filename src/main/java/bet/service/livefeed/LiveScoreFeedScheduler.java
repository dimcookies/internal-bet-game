package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.model.Game;
import bet.model.Odd;
import bet.repository.BetRepository;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.service.utils.GameScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Profile("live")
public class LiveScoreFeedScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LiveScoreFeedScheduler.class);

	@Value("${application.live_feed.interval:10000}")
	private final int interval = 10000;

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
	private GameScheduler gameScheduler;

	@Scheduled(fixedRate = interval)
	public void getLiveScores() {
		//betRepository.listAllPoints()
		LOGGER.info("Check live scores");
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
		if(!gameScheduler.hasActiveGame(now)) {
			LOGGER.trace("No active games");
			return;
		}
		liveFeed.getLiveFeed().forEach(gameDto -> {
			Game dbGame = gameRepository.findOne(gameDto.getId());
			if (dbGame.getStatus().equals(GameStatus.FINISHED)) {
				return;
			}
			Game liveGame = gameDto.toEntity();

			if (liveGame.getGoalsHome() != dbGame.getGoalsHome() || liveGame.getGoalsAway() != dbGame.getGoalsAway()) {
				LOGGER.info(liveGame.toString());
				//update game entry
				gameRepository.save(liveGame);
				//get odd results
				int[] oddPoints = getOdds(liveGame);

				betRepository.findByGame(liveGame).forEach(bet -> {
					if (bet.getScoreResult().equals(liveGame.getScoreResult())) {
						bet.setResultPoints(oddPoints[0]);
						if ((!liveGame.isGroupStage()) && bet.getOverResult().equals(liveGame.getOverResult())) {
							bet.setOverPoints(oddPoints[1]);
						}
					}
				});
			}
		});
		this.lastUpdateDate = now;
	}

	private int[] getOdds(Game game) {
		Odd odd = oddRepository.findOneByGame(game);

		int overPoints = (int) (odd.getOddForOver(game.getOverResult()) * 0.5 * odd.getMultiplier());

		int scorePoints = (int) (odd.getOddForScore(game.getScoreResult()) * odd.getMultiplier());

		return new int[] { scorePoints, overPoints };
	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}
}
