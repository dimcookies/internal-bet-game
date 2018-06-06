package bet.model;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The minimum and maximum streak of the user
 */
@Entity
@Table(name = "USER_STREAK_HISTORY", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.stats.entity-cache")
public class UserStreakHistory implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "max_streak")
	private Integer maxStreak;

	@Column(name = "min_streak")
	private Integer minStreak;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "dev.entity-cache")
	private User user;

	public UserStreakHistory() {
		super();
	}

	public UserStreakHistory(Integer maxStreak, Integer minStreak, User user) {
		this.maxStreak = maxStreak;
		this.minStreak = minStreak;
		this.user = user;
	}

}
