package bet.utils;

import bet.BetApplication;
import bet.api.constants.GameStatus;
import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.GameDto;
import bet.model.Game;
import bet.model.Odd;
import bet.repository.BetRepository;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.service.mgmt.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class LiveScoreFeedScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LiveScoreFeedScheduler.class);

	@Value("${application.live_scores.url}")
	private String liveScoresUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private OddRepository oddRepository;

	@Autowired
	private BetRepository betRepository;

	@Scheduled(fixedRate = 10000)
	public void getLiveScores() {
		LOGGER.info("Check live scores");
		ResponseEntity<GameDto[]> responseEntity = restTemplate.getForEntity(liveScoresUrl, GameDto[].class);
		Arrays.stream(responseEntity.getBody()).forEach(gameDto -> {
			Game dbGame = gameRepository.findOne(gameDto.getId());
			if(dbGame.getStatus().equals(GameStatus.FINISHED)) {
				return;
			}
			Game liveGame = gameDto.toEntity();

			if(liveGame.getGoalsHome() != dbGame.getGoalsHome() || liveGame.getGoalsAway() != dbGame.getGoalsAway()) {
				LOGGER.info(liveGame.toString());
				//update game entry
				gameRepository.save(liveGame);
				//get odd results
				int [] oddPoints = getOdds(liveGame);

				betRepository.findByGame(liveGame).forEach(bet -> {
					if(bet.getScoreResult().equals(liveGame.getScoreResult())) {
						bet.setResultPoints(oddPoints[0]);
						if((!liveGame.isGroupStage()) && bet.getOverResult().equals(liveGame.getOverResult())) {
							bet.setOverPoints(oddPoints[1]);
						}
					}
				});
			}
		});
	}

	private int[] getOdds(Game game) {
		Odd odd = oddRepository.findOneByGame(game);

		int overPoints = (int)( odd.getOddForOver(game.getOverResult()) * 0.5 * odd.getMultiplier());

		int scorePoints = (int) (odd.getOddForScore(game.getScoreResult()) * odd.getMultiplier());

		return new int[] {scorePoints, overPoints};
	}
}
