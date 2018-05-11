package bet.service.mgmt;

import bet.api.dto.BetDto;
import bet.api.dto.GameDto;
import bet.api.dto.OddDto;
import bet.model.Bet;
import bet.model.Game;
import bet.model.Odd;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BetService extends AbstractManagementService<Bet, Integer, BetDto> {

	@Override
	public List<BetDto> list() {
		return Lists.newArrayList(repository.findAll()).stream().map(entity -> {
			BetDto dto = new BetDto();
			dto.fromEntity(entity);
			return dto;
		}).collect(Collectors.toList());
	}

}
