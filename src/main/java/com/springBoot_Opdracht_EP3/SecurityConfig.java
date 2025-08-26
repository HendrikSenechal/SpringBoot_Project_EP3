package com.springBoot_Opdracht_EP3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		auth.inMemoryAuthentication().withUser("nameUser").password(encoder.encode("12345678")).roles("USER").and()
				.withUser("Hendrik Senechal").password(encoder.encode("admin1234")).roles("USER", "ADMIN");
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
				.authorizeHttpRequests(requests -> requests.requestMatchers("/login**").permitAll()
						.requestMatchers("/403**", "/assets/**", "/css/**", "/js/**", "/images/**", "/webjars/**")
						.permitAll().requestMatchers("/**").hasRole("USER"))
				.formLogin(form -> form.defaultSuccessUrl("/festivals", true).loginPage("/login"));
		return http.build();
	}
}