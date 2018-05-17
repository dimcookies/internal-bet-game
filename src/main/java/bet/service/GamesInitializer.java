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

@Component
public class GamesInitializer {

	public GamesInitializer() {
		super();
	}

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private OddRepository oddRepository;

	@Autowired
	private UserService userService;

	@PostConstruct
	@Transactional
	public void initialize() {
		if(userService.list().size() == 0) {
			userService.create(new UserDto("koukis","koukis@upstreamsystems.com", "koukis", "ADMIN"));
		}
		if(!gameRepository.findAll().iterator().hasNext()) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<GameDto>> typeReference = new TypeReference<List<GameDto>>() {};
			InputStream inputStream = TypeReference.class.getResourceAsStream("/games.json");
			try {
				List<GameDto> games = mapper.readValue(inputStream, typeReference);
				gameRepository.save(games.stream().map(gameDto -> gameDto.toEntity()).collect(Collectors.toList()));
				games.forEach(gameDto -> {
					Odd testOdd = new Odd(0, gameDto.getId(), 1.2f, 2.2f, 3.4f, 4.5f, 5.5f);
					oddRepository.save(testOdd);
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
