package bet.service.mgmt;

import bet.api.dto.GameDto;
import bet.model.Game;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class GameService extends AbstractManagementService<Game, Integer, GameDto> {

	@Override
	public List<GameDto> list() {
		return Lists.newArrayList(repository.findAll()).stream().map(entity -> {
			GameDto dto = new GameDto();
			dto.fromEntity(entity);
			return dto;
		}).collect(Collectors.toList());
	}

	//not allowed to create games
	@Override
	public GameDto create(GameDto dto) {
		throw new NotImplementedException();
	}

	//not allowed to update games
	@Override
	public GameDto update(GameDto dto) {
		throw new NotImplementedException();
	}

}
