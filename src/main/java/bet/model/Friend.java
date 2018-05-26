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

@Entity
@Table(name = "FRIEND", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class Friend implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "dev.entity-cache")
	private User user;

	@OneToOne
	@JoinColumn(name = "FRIEND_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "dev.entity-cache")
	private User friend;

	public Friend() {
		super();
	}

	public Friend(User user, User friend) {
		this.user = user;
		this.friend = friend;
	}

}
