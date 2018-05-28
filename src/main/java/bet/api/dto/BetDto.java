package bet.api.dto;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.model.Bet;
import bet.model.Game;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Represents a public available user bet on a game
 */
@Data
@JsonIgnoreProperties(value = { "competitionId", "odds" })
public class BetDto implements ManagementDto<Bet, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private Integer id;

	private Integer gameId;

	private Integer userId;

	/* The score bet */
	private ScoreResult scoreResult;

	/* Points from correct score bet */
	private int resultPoints;

	/* The under/over bet (only for playoffs, for group stage it is null) */
	private OverResult overResult;

	/* Points from correct under/over bet */
	private int overPoints;

	/* Date this bet was placed */
	private String betDate;

	public BetDto() {
	}

	@Override
	public void fromEntity(Bet entity) {
		if (entity != null) {
			setId(entity.getId());
			setGameId(entity.getGame().getId());
			setUserId(entity.getUser().getId());
			setScoreResult(entity.getScoreResult());
			setResultPoints(entity.getResultPoints());
			setOverResult(entity.getOverResult());
			setOverPoints(entity.getOverPoints());
			setBetDate(entity.getBetDate().toString());
		}
	}

	@Override
	public Bet toEntity() {
		return new Bet(this.id, this.gameId, this.userId, this.scoreResult,
				this.resultPoints, this.overResult, this.overPoints, ZonedDateTime.parse(this.betDate));
	}

}
