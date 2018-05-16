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

@Entity
@Table(name = "ENCRYPTED_BET", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class EncryptedBet implements Serializable {

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
	private String scoreResult;

	@Column(name = "OVER_BET")
	private String overResult;

	@Column(name = "BET_DATE")
	@Type(type = "java.time.ZonedDateTime")
	private ZonedDateTime betDate;

	public EncryptedBet() {
		super();
	}

	public EncryptedBet(Integer id, Integer gameId, Integer userId, String scoreResult, String overResult, ZonedDateTime betDate) {
		this.id = id;
		this.game = new Game(gameId);
		this.user = new User(userId);
		this.scoreResult = scoreResult;
		this.overResult = overResult;
		this.betDate = betDate;
	}
}
