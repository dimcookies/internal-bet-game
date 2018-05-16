package bet.api.dto;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.model.Bet;
import bet.model.Game;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(value = { "competitionId", "odds" })
public class BetDto implements ManagementDto<Bet, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private Integer id;

	private Integer gameId;

	private Integer userId;

	private ScoreResult scoreResult;

	private int resultPoints;

	private OverResult overResult;

	private int overPoints;

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
