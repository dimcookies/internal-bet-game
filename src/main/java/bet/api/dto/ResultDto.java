package bet.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultDto implements Serializable {

	private static final long serialVersionUID = 1592379971444078279L;

	private int goalsHomeTeam;

	private int goalsAwayTeam;

	public ResultDto() {

	}

	public ResultDto(int goalsHomeTeam, int goalsAwayTeam) {
		this.goalsHomeTeam = goalsHomeTeam;
		this.goalsAwayTeam = goalsAwayTeam;

	}


}
