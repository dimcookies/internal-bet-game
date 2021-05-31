package bet.api.dto;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.model.Bet;
import bet.model.Deadline;
import bet.model.Game;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;


@Data
public class DeadlineDto implements ManagementDto<Deadline, Integer> {

	private static final long serialVersionUID = 2592379971444078279L;

	private Integer id;

	private String dateFrom;

	private String dateTo;

	private String currentMatchDays;

	private String allowedMatchDays;

	private String betDeadlineText;

	public DeadlineDto() {
	}

	@Override
	public void fromEntity(Deadline entity) {
		if (entity != null) {
			setId(entity.getId());
			setDateFrom(entity.getDateFrom().toString());
			setDateTo(entity.getDateTo().toString());
			setCurrentMatchDays(entity.getCurrentMatchDays());
			setAllowedMatchDays(entity.getAllowedMatchDays());
			setBetDeadlineText(entity.getBetDeadlineText());
		}
	}

	@Override
	public Deadline toEntity() {
		return new Deadline(id, ZonedDateTime.parse(this.dateFrom), ZonedDateTime.parse(this.dateTo),
				currentMatchDays, allowedMatchDays, betDeadlineText);
	}

}
