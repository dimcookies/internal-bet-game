

package bet.repository;

import bet.model.Bet;
import bet.model.Odd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class BetRepositoryImpl implements BetRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private OddRepository oddRepository;

	@Cacheable(value = "points1")
	public Map<String, Integer> listAllPoints() {
		List<Bet> bets = entityManager.createQuery("from Bet").getResultList();
		return bets.stream()
				//sum bet points for each user
				.collect(Collectors.groupingBy(o -> o.getUser().getUsername(), Collectors.summingInt(value -> { return value.getOverPoints() + value.getResultPoints(); })));
	}

	@Cacheable(value = "userBets1")
	public Map<String, Double> listRiskIndex() {
		List<Bet> bets = entityManager.createQuery("from Bet").getResultList();
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
