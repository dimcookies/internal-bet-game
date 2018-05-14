

package bet.repository;

import bet.model.Bet;
import org.springframework.beans.factory.annotation.Autowired;
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


	public Map<String, Integer> listAllPoints() {
		List<Bet> bets = entityManager.createQuery("from Bet").getResultList();
		return bets.stream()
				.collect(Collectors.groupingBy(o -> o.getUser().getName(), Collectors.summingInt(value -> { return value.getOverPoints() + value.getResultPoints(); })));
	}

}
