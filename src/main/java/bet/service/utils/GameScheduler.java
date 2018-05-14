package bet.service.utils;

import bet.repository.GameRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class GameScheduler {

	@Autowired
	private GameRepository gameRepository;

	public boolean hasActiveGame(ZonedDateTime date) {
		return Lists.newArrayList(gameRepository.findAll()).stream()
				.filter(game ->  {
					return game.getGameDate().plusHours(4).isAfter(date) && game.getGameDate().isBefore(date);
				})
				.count() > 0;
	}
}