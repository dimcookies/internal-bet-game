package bet.repository;

import java.util.Map;

public interface CustomBetRepository {
    /**
     * Sums points from bets for all users
     *
     * @return map username->total points from bets.
     */
    Map<String, Integer> listAllPoints();

    /**
     * Sums odds for bets for all users. The higher
     * the value represents a user with risky bets
     *
     * @return map username-> cumulative odds
     */
    Map<String, Double> listRiskIndex();
}
