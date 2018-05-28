package bet.repository;

import bet.model.User;

import java.util.Map;

public interface BetRepositoryCustom {
    /**
     * Sums points from bets for all users
     *
     * @return map username->total points from bets.
     */
    public Map<String, Integer> listAllPoints();

    /**
     * Sums odds for bets for all users. The higher
     * the value represents a user with risky bets
     *
     * @return map username-> cumulative odds
     */
    public Map<String, Double> listRiskIndex();
}
