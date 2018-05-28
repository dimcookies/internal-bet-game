package bet.model;

import bet.api.constants.GameStatus;
import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * The representation of the football game
 */
@Entity
@Table(name = "GAME", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class Game implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "HOME_ID")
	private int homeId;

	@Column(name = "HOME_NAME")
	private String homeName;

	@Column(name = "AWAY_ID")
	private int awayId;

	@Column(name = "AWAY_NAME")
	private String awayName;

	@Column(name = "GAME_DATE")
	@Type(type = "java.time.ZonedDateTime")
	private ZonedDateTime gameDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private GameStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "MATCH_DAY")
	private int matchDay;

	@Column(name = "GOALS_HOME")
	private int goalsHome;

	@Column(name = "GOALS_AWAY")
	private int goalsAway;

	public Game() {
		super();
	}

	public Game(int id) {
		this.id = id;
	}

	public Game(int id, int homeId, String homeName, int awayId, String awayName, ZonedDateTime gameDate, GameStatus status,
			int matchDay, int goalsHome, int goalsAway) {
		super();
		this.id = id;
		this.homeId = homeId;
		this.homeName = homeName;
		this.awayId = awayId;
		this.awayName = awayName;
		this.gameDate = gameDate;
		this.status = status;
		this.matchDay = matchDay;
		this.goalsHome = goalsHome;
		this.goalsAway = goalsAway;
	}

	@Transient
	public ScoreResult getScoreResult() {
		return goalsHome > goalsAway ? ScoreResult.HOME_1 : goalsHome < goalsAway ? ScoreResult.AWAY_2 : ScoreResult.DRAW_X;
	};

	@Transient
	public OverResult getOverResult() {
		return goalsHome + goalsAway > 2 ? OverResult.OVER : OverResult.UNDER;
	}

	@Transient
	public boolean isGroupStage() {
		return this.matchDay < 4;
	}

}
