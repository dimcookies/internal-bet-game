package bet.service;

import bet.api.dto.GameDto;
import bet.api.dto.UserDto;
import bet.model.Odd;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.service.mgmt.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Initialize database, add all matches and odds for them
 */
@Component
public class GamesInitializer {

	public GamesInitializer() {
		super();
	}

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private OddRepository oddRepository;

	@PostConstruct
	@Transactional
	public void initialize() {
		if(!gameRepository.findAll().iterator().hasNext()) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<GameDto>> typeReference = new TypeReference<List<GameDto>>() {};
			//get games from file
			InputStream inputStream = TypeReference.class.getResourceAsStream("/games.json");
			try {
				List<GameDto> games = mapper.readValue(inputStream, typeReference);
				//save games
				gameRepository.save(games.stream().map(gameDto -> gameDto.toEntity()).collect(Collectors.toList()));
				//create an initial odd for this game (to be updated manually later)
				games.forEach(gameDto -> {
					Odd testOdd = new Odd(null, gameDto.getId(), 1, 1, 1, 1, 1);
					oddRepository.save(testOdd);
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
