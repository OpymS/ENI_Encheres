package fr.eni.tp.encheres.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

	@Bean
    PasswordEncoder passwordEncoder() {
    	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
					.userDetailsService(userDetailsService)
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
