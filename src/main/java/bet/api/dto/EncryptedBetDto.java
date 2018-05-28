package bet.api.dto;

import bet.model.EncryptedBet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Represents a user bet on a game which is stored encrypted
 */
@Data
@JsonIgnoreProperties(value = { "competitionId", "odds" })
public class EncryptedBetDto implements ManagementDto<EncryptedBet, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private Integer id;

	private Integer gameId;

	private Integer userId;

	/* The score bet */
	private String scoreResult;

	/* The under/over bet (only for playoffs, for group stage it is null) */
	private String overResult;

	/* Date this bet was placed */
	private String betDate;

	public EncryptedBetDto() {
	}

	public EncryptedBetDto(Integer id, Integer gameId, Integer userId, String scoreResult, String overResult, String betDate) {
		this.id = id;
		this.gameId = gameId;
		this.userId = userId;
		this.scoreResult = scoreResult;
		this.overResult = overResult;
		this.betDate = betDate;
	}

	@Override
	public void fromEntity(EncryptedBet entity) {
		if (entity != null) {
			setId(entity.getId());
			setGameId(entity.getGame().getId());
			setUserId(entity.getUser().getId());
			setScoreResult(entity.getScoreResult());
			setOverResult(entity.getOverResult());
			setBetDate(entity.getBetDate().toString());
		}
	}

	@Override
	public EncryptedBet toEntity() {
		return new EncryptedBet(this.id, this.gameId, this.userId, this.scoreResult, this.overResult, ZonedDateTime.parse(this.betDate));
	}

}
