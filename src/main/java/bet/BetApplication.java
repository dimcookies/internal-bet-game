package bet;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.util.Base64;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class BetApplication extends SpringBootServletInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(BetApplication.class);

	private static final String CONFIG_NAME = "bet-application";

	public static void main(String[] args) {
		new SpringApplicationBuilder(BetApplication.class)
				.properties(ImmutableMap.of("spring.config.name", CONFIG_NAME))
				.build()
				.run(args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.properties(ImmutableMap.of("spring.config.name", CONFIG_NAME));
	}

	/**
	 * Use md5 for password hashing and base64 for representation
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {
			@Override public String encode(CharSequence charSequence) {
				return hashPassword(charSequence.toString());
			}

			private String hashPassword(String password) {
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(password.getBytes());
					return new String(Base64.getEncoder().encode(md.digest()));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override public boolean matches(CharSequence charSequence, String s) {
				return hashPassword(charSequence.toString()).equals(s);
			}
		};
	}


}
