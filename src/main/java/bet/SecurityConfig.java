package bet;

import bet.service.utils.EncryptHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.security.MessageDigest;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		//get users from database table
		auth.
				jdbcAuthentication()
				.usersByUsernameQuery("select name, password, 1 from ALLOWED_USERS where name=?")
				.authoritiesByUsernameQuery("select u.name, u.role from ALLOWED_USERS u where u.name=?")
				.dataSource(dataSource)
				.passwordEncoder(passwordEncoder);

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

	/**
	 * Configured protected resources
	 * @param http
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable().
				authorizeRequests()
				.antMatchers(new String[]{"/css/**", "/js/**", "/img/**"}).permitAll()
				//restrict configuration web services to admin user
					.antMatchers("/config/**").hasAuthority("ADMIN")// hasRole("ADMIN")
				//restrict swagger to admin user
					.antMatchers("/swagger-ui.html").hasAuthority("ADMIN")//.hasRole("ADMIN")
				//all other resouces for authenticated users
					.anyRequest().fullyAuthenticated()
				.and()
					.formLogin()
					.loginPage("/login")
					.permitAll()
				.and()
					.logout()
					.invalidateHttpSession(true)
					.clearAuthentication(true)
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login?logout")
					.permitAll();

	}

	/**
	 * Set public available paths with no authorization
	 * @param web
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				.antMatchers("/css/**", "/js/**", "/images/**"/*"/resources/**","/public/**", "/ws/**"*/);
	}

}
