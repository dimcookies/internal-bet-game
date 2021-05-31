package bet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DEADLINES", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class Deadline implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM HH:mm")
	@Column(name = "DATE_FROM")
	@Type(type = "java.time.ZonedDateTime")
	private ZonedDateTime dateFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM HH:mm")
	@Column(name = "DATE_TO")
	@Type(type = "java.time.ZonedDateTime")
	private ZonedDateTime dateTo;

	@Column(name = "CURRENT_MATCH_DAYS")
	private String currentMatchDays;

	@Column(name = "ALLOWED_MATCH_DAYS")
	private String allowedMatchDays;

	@Column(name = "BET_DEADLINE_TEXT")
	private String betDeadlineText;

	public Deadline() {
		super();
	}

	public Deadline(Integer id, ZonedDateTime dateFrom, ZonedDateTime dateTo, String currentMatchDays,
			String allowedMatchDays, String betDeadlineText) {
		this.id = id;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.currentMatchDays = currentMatchDays;
		this.allowedMatchDays = allowedMatchDays;
		this.betDeadlineText = betDeadlineText;
	}

}
