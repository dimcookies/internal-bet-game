package bet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Represent the position of a user in the rankings at a given day
 */
@Entity
@Table(name = "RANK_HISTORY", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.stats.entity-cache")
public class RankHistory implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "rank")
	private Integer rank;

	@Column(name = "points")
	private Integer points;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "dev.entity-cache")
	private User user;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "RANK_DATE")
	@Type(type = "java.time.ZonedDateTime")
	private ZonedDateTime rankDate;

	public RankHistory() {
		super();
	}

	public RankHistory(Integer rank, User user, Integer points, ZonedDateTime rankDate) {
		this.rank = rank;
		this.points = points;
		this.user = user;
		this.rankDate = rankDate;
	}

}
