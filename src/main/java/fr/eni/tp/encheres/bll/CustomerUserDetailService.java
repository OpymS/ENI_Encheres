package fr.eni.tp.encheres.bll;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.dal.UserDAO;

@Service
public class CustomerUserDetailService implements UserDetailsService {
	
	@Autowired
	private UserDAO userDAO;

	 @Override
	    public UserDetails loadUserByUsername(String username) {
	        User user = null;
	            if (username.contains("@")) {
	                user = userDAO.readByEmail(username);
	            } else {
	                user = userDAO.readByPseudo(username);
	            }
	        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());

	 }
	 
}
