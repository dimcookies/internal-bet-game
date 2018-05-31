package bet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The app user
 */
@Entity
@Table(name = "ALLOWED_USERS", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class User implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "NAME")
	private String name;

	@JsonIgnore
	@Column(name = "EMAIL")
	private String email;

	@JsonIgnore
	@Column(name = "PASSWORD")
	private String password;

	@JsonIgnore
	@Column(name = "ROLE")
	private String role;

	public User() {
		super();
	}

	public User(int id) {
		this.id = id;
	}

	public User(Integer id, String name, String email, String password, String role, String username) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
	}

}
