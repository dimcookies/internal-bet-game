package bet.base;

import bet.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.sf.ehcache.CacheManager;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Superclass of all the integration tests of the module
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
		"spring.config.name = bet-application",
		"spring.jpa.properties.hibernate.cache.region.factory_class = org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory"
})
public abstract class AbstractBetIntegrationTest extends AbstractBetTest {

	public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

	@Autowired
	protected WebApplicationContext webContext;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	protected MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webContext).build();
		deleteDataAndClearL2Cache();
	}

	@After
	public void tearDown() {
		deleteDataAndClearL2Cache();
	}

	@Autowired
	protected ObjectMapper objectMapper = new ObjectMapper()
			.configure(SerializationFeature.INDENT_OUTPUT, true)
			.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);

	public void truncatePostgresTables(JdbcTemplate jdbcTemplate, Class<?>... entities) {
		for (Class<?> c : entities) {
			assertNotNull(c.getAnnotation(Entity.class));
			Table t = c.getAnnotation(Table.class);
			while (t == null && !c.getSuperclass().getName().equals("Object")) {
				c = c.getSuperclass();
				t = c.getAnnotation(Table.class);
			}
			doTruncate(jdbcTemplate, t.schema(), t.name());
			ReflectionUtils.doWithMethods(c, m -> {
				if (m.getAnnotation(ManyToMany.class) != null) {
					JoinTable jt = m.getAnnotation(JoinTable.class);
					if (jt != null) {
						doTruncate(jdbcTemplate, jt.schema(), jt.name());
					}
				}
			});
		}
	}

	protected static void doTruncate(JdbcTemplate jdbcTemplate, String schema, String table) {
		jdbcTemplate.execute("TRUNCATE TABLE " + schema + "." + table + " RESTART IDENTITY CASCADE");
	}

	protected void deleteDataAndClearL2Cache() {
		// Delete all data from Postgres
		truncatePostgresTables(jdbcTemplate,
				Game.class, Bet.class, Odd.class, User.class, Comment.class, EncryptedBet.class,
				Friend.class, RankHistory.class, RssFeed.class, UserStreak.class, CommentLike.class);

		CacheManager manager = CacheManager.getInstance();
		for (String s : manager.getCacheNames()) {
			manager.getCache(s).removeAll();
		}
	}

	protected JdbcTemplate getJdbcTemplate() {
		return null;
	}
}
