package bet.model;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Represents a user bet on a game which is stored encrypted
 */
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
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
	private Game game;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
	private User user;

	/* The score bet */
	@Column(name = "RESULT_BET")
	private String scoreResult;

	/* The under/over bet (only for playoffs, for group stage it is null) */
	@Column(name = "OVER_BET")
	private String overResult;

	/* Date this bet was placed */
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
