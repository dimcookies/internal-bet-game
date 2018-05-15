package bet.api.dto;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.model.Bet;
import bet.model.EncryptedBet;
import bet.model.Game;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(value = { "competitionId", "odds" })
public class EncryptedBetDto implements ManagementDto<EncryptedBet, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private Integer id;

	private Integer gameId;

	private Integer userId;

	private String scoreResult;

	private String overResult;

	public EncryptedBetDto() {
	}

	public EncryptedBetDto(Integer id, Integer gameId, Integer userId, String scoreResult, String overResult) {
		this.id = id;
		this.gameId = gameId;
		this.userId = userId;
		this.scoreResult = scoreResult;
		this.overResult = overResult;
	}

	@Override
	public void fromEntity(EncryptedBet entity) {
		if (entity != null) {
			setId(entity.getId());
			setGameId(entity.getGame().getId());
			setUserId(entity.getUser().getId());
			setScoreResult(entity.getScoreResult());
			setOverResult(entity.getOverResult());
		}
	}

	@Override
	public EncryptedBet toEntity() {
		return new EncryptedBet(this.id, this.gameId, this.userId, this.scoreResult, this.overResult);
	}

}
