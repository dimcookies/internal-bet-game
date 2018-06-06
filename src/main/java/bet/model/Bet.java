package bet.model;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Represents a public available user bet on a game
 */
@Entity
@Table(name = "BET", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class Bet implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "GAME_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
	private Game game;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
	private User user;

	/* The score bet */
	@Enumerated(EnumType.STRING)
	@Column(name = "RESULT_BET")
	private ScoreResult scoreResult;

	/* Points from correct score bet */
	@Column(name = "RESULT_POINTS")
	private int resultPoints;

	/* The under/over bet (only for playoffs, for group stage it is null) */
	@Enumerated(EnumType.STRING)
	@Column(name = "OVER_BET")
	private OverResult overResult;

	/* Points from correct under/over bet */
	@Column(name = "OVER_POINTS")
	private int overPoints;

	/* Date this bet was placed */
	@Column(name = "BET_DATE")
	@Type(type = "java.time.ZonedDateTime")
	private ZonedDateTime betDate;

	public Bet() {
		super();
	}

	public Bet(Integer id, Integer gameId, Integer userId, ScoreResult scoreResult, int resultPoints, OverResult overResult,
			int overPoints, ZonedDateTime betDate) {
		this.id = id;
		this.game = new Game(gameId);
		this.user = new User(userId);
		this.scoreResult = scoreResult;
		this.resultPoints = resultPoints;
		this.overResult = overResult;
		this.overPoints = overPoints;
		this.betDate = betDate;
	}
}
