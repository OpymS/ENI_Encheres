package fr.eni.tp.encheres.bll;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.dal.UserDAO;

// TODO: Auto-generated Javadoc
/**
 * The Class UserServiceImpl.
 */
@Service
public class UserServiceImpl implements UserService {
	
	/** The user DAO. */
	@Autowired
	private UserDAO userDAO;
	

	/**
	 * Creates the account.
	 *
	 * @param pseudo the pseudo
	 * @param name the name
	 * @param firstName the first name
	 * @param email the email
	 * @param phoneNumber the phone number
	 * @param street the street
	 * @param zipCode the zip code
	 * @param city the city
	 * @param password the password
	 * @param passwordConfirm the password confirm
	 */
	@Override
	public void createAccount(String pseudo, String name, String firstName, String email, String phoneNumber,
			String street, String zipCode, String city, String password, String passwordConfirm) {
		if (!password.equals(passwordConfirm)) {
			throw new IllegalArgumentException("Les mots de passe ne sont pas identiques");
		}
		
		User user = new User();
		user.setPseudo(pseudo);
		user.setName(name);
		user.setFirstName(firstName);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		user.setStreet(street);
		user.setZipCode(zipCode);
		user.setCity(city);
		user.setPassword(password);
		user.setPasswordConfirm(passwordConfirm);
		user.setCredit(0);
		user.setAdmin(false);
		
		userDAO.create(user);
	}

	/**
	 * Login.
	 *
	 * @param email the email
	 * @param password the password
	 * @param rememberMe the remember me
	 * @return true, if successful
	 */
	@Override
	public boolean login(String email, String password, boolean rememberMe) {
		User user = userDAO.readByEmail(email);
		return user != null && user.getPassword().equals(password);
	}

	/**
	 * Forgot password.
	 *
	 * @param email the email
	 */
	@Override
	public void forgotPassword(String email) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Logout.
	 */
	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Delete account.
	 *
	 * @param userId the user id
	 */
	@Override
	public void deleteAccount(int userId) {
		userDAO.deleteById(userId);
		
	}

	/**
	 * View own points.
	 *
	 * @param userId the user id
	 * @return the int
	 */
	@Override
	public int viewOwnPoints(int userId) {
		// TODO Auto-generated method stub
		return userId;

	}

	/**
	 * View user profile.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	@Override
	public User viewUserProfile(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Update profile.
	 *
	 * @param userId the user id
	 * @param pseudo the pseudo
	 * @param name the name
	 * @param firstName the first name
	 * @param email the email
	 * @param phoneNumber the phone number
	 * @param street the street
	 * @param zipCode the zip code
	 * @param city the city
	 * @param password the password
	 * @param passwordConfirm the password confirm
	 */
	@Override
	public void updateProfile(int userId, String pseudo, String name, String firstName, String email,
			String phoneNumber, String street, String zipCode, String city, String password, String passwordConfirm) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gets the user by email.
	 *
	 * @param email the email
	 * @return the user by email
	 */
	@Override
	public User getUserByEmail(String email) {
		return userDAO.readByEmail(email);
	}



}
