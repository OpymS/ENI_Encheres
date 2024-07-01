package fr.eni.tp.encheres.configuration.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	UserDetailsManager users(DataSource dataSource) {
		
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
		    .requestMatchers("/signup").permitAll()
		    .requestMatchers("/profil").hasAnyRole("ADMIN", "MEMBRE")
		    .requestMatchers("/profil/modify").hasAnyRole("ADMIN", "MEMBRE")
		    .requestMatchers("/auctions/*").hasAnyRole("ADMIN","MEMBRE")
				.anyRequest().authenticated()
			);
		
		http.formLogin(Customizer.withDefaults());
		
		/*
		http.formLogin(form->{
			form.loginPage("/login");
			form.permitAll();
			form.defaultSuccessUrl("/session");
		});
		*/
		
		http.logout(form ->{
			form.permitAll();
			form.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
			form.invalidateHttpSession(true);
			form.clearAuthentication(true);
			form.deleteCookies("JSESSIONID");
			form.logoutSuccessUrl("/auctions");
		});
			
		return http.build();
	}
}
