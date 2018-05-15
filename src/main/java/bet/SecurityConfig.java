package bet;

import bet.service.utils.EncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.security.MessageDigest;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//	@Autowired
//	private UserRepository userRepository;
//
	@Autowired
	private EncryptUtils encryptUtils;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.
				jdbcAuthentication()
				.usersByUsernameQuery("select name, password, 1 from ALLOWED_USERS where name=?")
				.authoritiesByUsernameQuery("select u.name, u.role from ALLOWED_USERS u where u.name=?")
				.dataSource(dataSource)
				.passwordEncoder(passwordEncoder);

		auth.inMemoryAuthentication()
				.withUser("admin").password("admin").roles("ADMIN");

//		userRepository.findAll().forEach(user -> {
//			try {
//				auth.inMemoryAuthentication()
//						.withUser(user.getName()).password(encryptUtils.decrypt(user.getPassword(), user.getName())).roles("USER");
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		});

	}

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

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().anyRequest().hasRole("ADMIN");//.fullyAuthenticated();
		http.authorizeRequests()
				.antMatchers("/test1").hasRole("USER");
		http.httpBasic();
		http.csrf().disable();

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				.antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}
}