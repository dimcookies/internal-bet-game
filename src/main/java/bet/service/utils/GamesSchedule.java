package bet.service.utils;

import bet.api.constants.GameStatus;
import bet.model.Game;
import bet.repository.GameRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper methods for games schedule
 */
@Component
public class GamesSchedule {

	private final List<GameStatus> activeStatuses = Arrays.asList(GameStatus.TIMED, GameStatus.IN_PLAY, GameStatus.SCHEDULED);

	@Autowired
	private GameRepository gameRepository;

	/**
	 * Checks if there is a currently active game.
	 * Start date has passed and status is TIMED or IN_PLAY or SCHEDULED
	 * @param date
	 * @return
	 */
	public boolean hasActiveGame(ZonedDateTime date) {
		return getActiveGames(date).size() > 0;
	}

	public List<Game> getActiveGames(ZonedDateTime date) {
		return Lists.newArrayList(gameRepository.findAll()).stream()
				.filter(game -> game.getGameDate().isBefore(date) && activeStatuses.contains(game.getStatus()))
				.collect(Collectors.toList());
	}

	public List<Game> getGamesAtDate(ZonedDateTime date, List<GameStatus> statuses) {
		return Lists.newArrayList(gameRepository.findAll()).stream()
				.filter(game -> game.getGameDate().withZoneSameInstant(ZoneId.of("UTC")).truncatedTo(ChronoUnit.DAYS)
						.equals(date.withZoneSameInstant(ZoneId.of("UTC")).truncatedTo(ChronoUnit.DAYS))
						&& statuses.contains(game.getStatus()))
				.collect(Collectors.toList());
	}
}