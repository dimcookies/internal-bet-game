package bet.service;

import bet.api.dto.GamesV2Dto;
import bet.model.Odd;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.InputStream;
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
			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeReference<GamesV2Dto> typeReference = new TypeReference<GamesV2Dto>() {};
			//get games from file
			InputStream inputStream = TypeReference.class.getResourceAsStream("/euro_games.json");
			try {
				GamesV2Dto games = mapper.readValue(inputStream, GamesV2Dto.class);
				//save games
				gameRepository.save(games.getMatches().stream().map(gameDto -> gameDto.toGame().toEntity()).collect(Collectors.toList()));

				//create an initial odd for this game (to be updated manually later)
				games.getMatches().forEach(gameDto -> {
					Odd testOdd = new Odd(null, gameDto.getId(), 1, 1, 1, 1, 1);
					oddRepository.save(testOdd);
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
