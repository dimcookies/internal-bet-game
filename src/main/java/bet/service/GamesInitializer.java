package bet.service;

import bet.api.dto.GameDto;
import bet.repository.GameRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
public final class GamesInitializer {

	private GamesInitializer() {
		super();
	}

	@Autowired
	private GameRepository gameRepository;

	@PostConstruct
	public void initialize() {
		//gameRepository.deleteAll();
		if(!gameRepository.findAll().iterator().hasNext()) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<GameDto>> typeReference = new TypeReference<List<GameDto>>() {
			};
			InputStream inputStream = TypeReference.class.getResourceAsStream("/games.json");
			try {
				List<GameDto> games = mapper.readValue(inputStream, typeReference);
				gameRepository.save(games.stream().map(gameDto -> gameDto.toEntity()).collect(Collectors.toList()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
