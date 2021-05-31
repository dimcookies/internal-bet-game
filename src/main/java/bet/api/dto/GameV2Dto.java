package bet.api.dto;

import bet.api.constants.GameStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GameV2Dto {
	private TeamV2Dto awayTeam;
	private TeamV2Dto homeTeam;
	private int id;
	private int matchday;
	private GameStatus status;
	private String utcDate;

	public GameDto toGame() {
		GameDto dto = new GameDto(status, matchday, homeTeam.getName() == null ? "" :homeTeam.getName(),
		                   getHomeTeam().getId() == null ? 0 : getHomeTeam().getId(),
		                   awayTeam.getName() == null? "" : awayTeam.getName(),
		                   awayTeam.getId() == null ? 0 : awayTeam.getId(), utcDate, 0, 0);
		dto.setId(id);
		return dto;
	}
}
