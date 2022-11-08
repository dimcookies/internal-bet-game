package bet;

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
				.usersByUsernameQuery("select username, password, 1 from bet.ALLOWED_USERS where username=?")
				.authoritiesByUsernameQuery("select u.username, u.role from bet.ALLOWED_USERS u where u.username=?")
				.dataSource(dataSource)
				.passwordEncoder(passwordEncoder);

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
				.antMatchers(new String[]{"/css/**", "/js/**", "/images/**", "/vendor/**", "/img/**", "/app*"}).permitAll()
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
					//.invalidateHttpSession(true)
					//.clearAuthentication(true)
					.deleteCookies("JSESSIONID")
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login?logout")
					.permitAll()
				.and()
					.rememberMe()
					.key("AppKey")
					.alwaysRemember(true)
					.rememberMeParameter("rememberMe")
					.rememberMeCookieName("javasampleapproach-remember-me")
					.tokenValiditySeconds(60* 24 * 60 * 60);

	}

	/**
	 * Set public available paths with no authorization
	 * @param web
     */
	@Override
    public void configure(WebSecurity web) {
		web.ignoring()
				.antMatchers("/css/**", "/js/**", "/images/**", "/vendor/**", "/img/**", "/app*" /*"/resources/**","/public/**", "/ws/**"*/);
	}

}
