

package bet.repository;

import bet.model.Bet;
import bet.model.Odd;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CustomBetRepositoryImpl implements CustomBetRepository {

	@Autowired
	private OddRepository oddRepository;

	@Autowired
	private BetRepository betRepository;

	@Cacheable(value = "points1")
	public Map<String, Integer> listAllPoints() {
		List<Bet> bets = Lists.newArrayList(betRepository.findAll());
		return bets.stream()
				//sum bet points for each user
				.collect(Collectors.groupingBy(o -> o.getUser().getUsername(), Collectors.summingInt(value -> { return value.getOverPoints() + value.getResultPoints(); })));
	}

	@Cacheable(value = "userBets1")
	public Map<String, Double> listRiskIndex() {
		List<Bet> bets = Lists.newArrayList(betRepository.findAll());
		return bets.stream()
				.collect(Collectors.groupingBy(o -> o.getUser().getUsername(), Collectors.summingDouble(value -> {
					//get odds for this bet and sum them for each user
					Odd odd = oddRepository.findOneByGame(value.getGame());
					return odd.getOddForScore(value.getScoreResult()) +
							//check if under/over exist
							(value.getOverResult() != null? odd.getOddForOver(value.getOverResult()) : 0.0);
				})));
	}

}
