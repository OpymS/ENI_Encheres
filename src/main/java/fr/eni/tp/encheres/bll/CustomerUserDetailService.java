package fr.eni.tp.encheres.bll;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.dal.UserDAO;

@Service
public class CustomerUserDetailService implements UserDetailsService {
	private static final Logger customerUserDetailLogger = LoggerFactory.getLogger(CustomerUserDetailService.class);
	
	@Autowired
	private UserDAO userDAO;

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
	        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());

	 }
	 
}
