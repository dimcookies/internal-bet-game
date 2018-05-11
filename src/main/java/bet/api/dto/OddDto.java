package bet.api.dto;

import bet.model.Game;
import bet.model.Odd;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class OddDto implements ManagementDto<Odd, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private int id;

	private int gameId;

	private float oddsHome;

	private float oddsAway;

	private float oddsTie;

	private float oddsOver;

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
