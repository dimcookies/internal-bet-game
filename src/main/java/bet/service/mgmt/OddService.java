package bet.service.mgmt;

import bet.api.dto.BetDto;
import bet.api.dto.OddDto;
import bet.model.Bet;
import bet.model.Odd;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OddService extends AbstractManagementService<Odd, Integer, OddDto> {

	@Override
	public List<OddDto> list() {
		return Lists.newArrayList(repository.findAll()).stream().map(entity -> {
			OddDto dto = new OddDto();
			dto.fromEntity(entity);
			return dto;
		}).collect(Collectors.toList());
	}

	//not allowed to create odds
	@Override
	public OddDto create(OddDto dto) {
		throw new NotImplementedException();
	}

	//not allowed to update odds
	@Override
	public OddDto update(OddDto dto) {
		throw new NotImplementedException();
	}


}
