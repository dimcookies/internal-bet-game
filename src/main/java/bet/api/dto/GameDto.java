package bet.api.dto;

import bet.api.constants.GameStatus;
import bet.api.constants.ScoreResult;
import bet.model.Game;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * The representation of the football game
 */
@Data
@JsonIgnoreProperties(value = { "competitionId", "odds" })
public class GameDto implements ManagementDto<Game, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private int id;

	private GameStatus status;

	private int matchday;

	private String homeTeamName;

	private int homeTeamId;

	private String awayTeamName;

	private int awayTeamId;

	private String date;

	private ResultDto result;

	private String elapsed;

	public GameDto() {
	}

	public GameDto(GameStatus status, int matchday, String homeTeamName, int homeTeamId, String awayTeamName, int awayTeamId,
			String date, int goalsHomeTeam, int goalsAwayTeam) {
		this.status = status;
		this.matchday = matchday;
		this.homeTeamName = homeTeamName;
		this.homeTeamId = homeTeamId;
		this.awayTeamName = awayTeamName;
		this.awayTeamId = awayTeamId;
		this.date = date;
		ResultDto resultDto = new ResultDto(goalsHomeTeam, goalsAwayTeam);
		this.setResult(resultDto);
	}

	@Override
	public void fromEntity(Game entity) {
		if (entity != null) {
			setId(entity.getId());
			setStatus(entity.getStatus());
			setMatchday(entity.getMatchDay());
			setHomeTeamId(entity.getHomeId());
			setHomeTeamName(entity.getHomeName());
			setAwayTeamId(entity.getAwayId());
			setAwayTeamName(entity.getAwayName());
			setDate(entity.getGameDate().toString());
			setResult(new ResultDto(entity.getGoalsHome(), entity.getGoalsAway()));
		}
	}

	@Override
	public Game toEntity() {
		return new Game(this.id, this.homeTeamId, this.homeTeamName, this.awayTeamId, this.awayTeamName,
				ZonedDateTime.parse(this.date),  this.status, this.matchday, this.getResult().getGoalsHomeTeam(), this.getResult().getGoalsAwayTeam());
	}

}
