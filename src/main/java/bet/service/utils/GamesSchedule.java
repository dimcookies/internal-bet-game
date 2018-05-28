package bet.service.utils;

import bet.repository.GameRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Helper methods for games schedule
 */
@Component
public class GamesSchedule {

	@Autowired
	private GameRepository gameRepository;

	/**
	 * Checks if there is a currently active game.
	 * Assume that a gave is active 4 hours after it has started
	 * @param date
	 * @return
	 */
	public boolean hasActiveGame(ZonedDateTime date) {
		return Lists.newArrayList(gameRepository.findAll()).stream()
				.anyMatch(game -> game.getGameDate().plusHours(4).isAfter(date) && game.getGameDate().isBefore(date));
	}
}