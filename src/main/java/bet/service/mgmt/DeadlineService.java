package bet.service.mgmt;

import bet.api.dto.DeadlineDto;
import bet.model.Deadline;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeadlineService extends AbstractManagementService<Deadline, Integer, DeadlineDto> {

	@Override
	public List<DeadlineDto> list() {
		return Lists.newArrayList(repository.findAll()).stream().map(entity -> {
			DeadlineDto dto = new DeadlineDto();
			dto.fromEntity(entity);
			return dto;
		}).collect(Collectors.toList());
	}

}
