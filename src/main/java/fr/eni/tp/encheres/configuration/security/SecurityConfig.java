package fr.eni.tp.encheres.configuration.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final DataSource dataSource;
	
	public SecurityConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
    PasswordEncoder passwordEncoder() {
    	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	UserDetailsManager users() {
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
		users.setUsersByUsernameQuery("SELECT email, mot_de_passe password, 'true' as enabled FROM UTILISATEURS WHERE email = ?");
		users.setAuthoritiesByUsernameQuery("SELECT email, role FROM UTILISATEURS ut INNER JOIN ROLES ro ON ut.administrateur = ro.is_admin WHERE email = ?");
		return users;
	}
	
	
	@Bean
	SecurityFilterChain web(HttpSecurity http) throws Exception{
		http
			.authorizeHttpRequests((authorize) -> authorize
			.requestMatchers("/").permitAll()
		    .requestMatchers("/css/*").permitAll()
		    .requestMatchers("/images/**").permitAll()
		    .requestMatchers("/auctions").permitAll()
		    .requestMatchers("/auctions/newArticle").hasAnyRole("ADMIN", "MEMBRE")
		    .requestMatchers("/forgot-password").permitAll()
		    .requestMatchers("/signup").anonymous()
		    .requestMatchers("/login").anonymous()
		    .requestMatchers("/profil").hasAnyRole("ADMIN", "MEMBRE")
		    .requestMatchers("/profil/modify").hasAnyRole("ADMIN", "MEMBRE")
		    .requestMatchers("/auctions/*").hasAnyRole("ADMIN","MEMBRE")
		    .requestMatchers("/admin/**").hasRole("ADMIN")
		    .requestMatchers("/error").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
					.loginPage("/login")
					.permitAll()
					.defaultSuccessUrl("/session", true)
					.failureUrl("/login?error=true")
			)
			.rememberMe(rememberMe -> rememberMe
					.userDetailsService(users())
					.tokenValiditySeconds(86400)
					.key("uniqueAndSecret")
			)
			.logout(form -> {
				form.permitAll();
				form.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
				form.invalidateHttpSession(true);
				form.clearAuthentication(true);
				form.deleteCookies("JSESSIONID");
				form.logoutSuccessUrl("/session");
		});
			
		return http.build();
	}
}
