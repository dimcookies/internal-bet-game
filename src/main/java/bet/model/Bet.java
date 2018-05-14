package bet.model;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

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
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "dev.entity-cache")
	private Game game;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "dev.entity-cache")
	private User user;

	@Column(name = "RESULT_BET")
	private ScoreResult scoreResult;

	@Column(name = "RESULT_POINTS")
	private int resultPoints;

	@Column(name = "OVER_BET")
	private OverResult overResult;

	@Column(name = "OVER_POINTS")
	private int overPoints;

	public Bet() {
		super();
	}

	public Bet(Integer gameId, Integer userId, ScoreResult scoreResult, int resultPoints, OverResult overResult, int overPoints) {
		this.game = new Game(gameId);
		this.user = new User(userId);
		this.scoreResult = scoreResult;
		this.resultPoints = resultPoints;
		this.overResult = overResult;
		this.overPoints = overPoints;
	}
}
