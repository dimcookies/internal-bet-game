package bet.model;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * An offline cache of rss feeds
 */
@Entity
@Table(name = "RSS_FEED", schema = "BET")
@DynamicInsert
@DynamicUpdate
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bet.entity-cache")
public class RssFeed implements Serializable {

	private static final long serialVersionUID = -5924099885411409739L;

	/**
	 * Database primary key - No business meaning
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "LINK")
	private String link;

	@Column(name = "IMAGE")
	private String image;

	@Column(name = "PUBLISH_DATE")
	private Date publishDate;

	public RssFeed() {
		super();
	}

	public RssFeed(String title, String link, Date publishDate, String image) {

		this.title = title;
		this.link = link;
		this.publishDate = publishDate;
		this.image = image;

	}

}
