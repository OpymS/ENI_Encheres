package fr.eni.tp.encheres.bll;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.dal.UserDAO;

@Service
public class CustomerUserDetailService implements UserDetailsService {
	private static final Logger customerUserDetailLogger = LoggerFactory.getLogger(CustomerUserDetailService.class);
	
	@Autowired
	private UserDAO userDAO;
	
	public CustomerUserDetailService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	 @Override
	    public UserDetails loadUserByUsername(String username) {
		 customerUserDetailLogger.info("Méthode loadUserByUsername");
		 User user = null;
	            if (username.contains("@")) {
	            	customerUserDetailLogger.info("log in avec l'email");
	                user = userDAO.readByEmail(username);
	            } else {
	            	customerUserDetailLogger.info("login avec le pseudo");
	                user = userDAO.readByPseudo(username);
	            }
	            
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }     
        
        // Récupérer les rôles de l'utilisateur
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (user.isAdmin()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBRE"));

	    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);

	 }
	 
}
