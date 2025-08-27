package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
				.authorizeHttpRequests(requests -> requests.requestMatchers("/login**").permitAll()
						.requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll()
						.requestMatchers("/images/**", "/assets/**").permitAll().requestMatchers("/403**").permitAll()
						.requestMatchers("/festivals/new/**", "/festivals/edit/**", "/vendors/new/**",
								"/vendors/edit/**", "/addresses/**", "/addresses/edit/{id}")
						.hasRole("ADMIN").requestMatchers("/festivals/{id}/reviews").hasRole("USER").anyRequest()
						.hasAnyRole("USER", "ADMIN"))
				.formLogin(form -> form.defaultSuccessUrl("/festivals", true).loginPage("/login")
						.usernameParameter("username").passwordParameter("password"))
				.exceptionHandling(handling -> handling.accessDeniedPage("/403"));

		return http.build();
	}
}