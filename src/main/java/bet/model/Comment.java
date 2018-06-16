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
@Table(name = "COMMENTS", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class Comment implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "COMMENT")
	private String comment;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
	private User user;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM HH:mm")
	@Column(name = "COMMENT_DATE")
	@Type(type = "java.time.ZonedDateTime")
	private ZonedDateTime commentDate;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "COMMENT_PARENT", referencedColumnName = "ID", nullable = true)
	//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
	private Comment parentComment;

	@OneToMany(mappedBy = "comment", targetEntity = CommentLike.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.collection-cache")
	private List<CommentLike> commentLikes = new ArrayList<>();

	@OneToMany(mappedBy = "parentComment", targetEntity = Comment.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.collection-cache")
	private List<Comment> replies = new ArrayList<>();


	public Comment() {
		super();
	}

	public Comment(String comment, User user, ZonedDateTime commentDate) {
		this.comment = comment;
		this.user = user;
		this.commentDate = commentDate;
	}

	public Comment(String comment, User user, ZonedDateTime commentDate, Comment parentComment) {
		this(comment, user, commentDate);
		this.parentComment = parentComment;
	}


}
