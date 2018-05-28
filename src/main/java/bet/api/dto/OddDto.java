package bet.api.dto;

import bet.model.Odd;
import lombok.Data;

/**
 * The representation of an odd
 */
@Data
public class OddDto implements ManagementDto<Odd, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private int id;

	private int gameId;

	/* Odds for 1 */
	private float oddsHome;

	/* Odds for 2 */
	private float oddsAway;

	/* Odds for X */
	private float oddsTie;

	/* Odds for over */
	private float oddsOver;

	/* Odds for under */
	private float oddsUnder;

	public OddDto() {
	}

	@Override
	public void fromEntity(Odd entity) {
		if (entity != null) {
			setId(entity.getId());
			setGameId(entity.getGame().getId());
			setOddsHome(entity.getOddsHome());
			setOddsAway(entity.getOddsAway());
			setOddsTie(entity.getOddsTie());
			setOddsOver(entity.getOddsOver());
			setOddsUnder(entity.getOddsUnder());
		}
	}

	@Override
	public Odd toEntity() {
		return new Odd(this.id, this.gameId, this.oddsHome, this.oddsAway,
				this.oddsTie, this.oddsOver, this.oddsUnder);
	}

}
