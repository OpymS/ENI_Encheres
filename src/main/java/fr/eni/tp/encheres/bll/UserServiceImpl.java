package fr.eni.tp.encheres.bll;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	

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
			throw new IllegalArgumentException("Les mots de passe ne sont pas identiques.");
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
		user.setPassword(passwordEncoder.encode(password));
		user.setCredit(0);
		user.setAdmin(false);
		
		userDAO.create(user);
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
		return userDAO.readById(userId);
	}

	/**
	 * Update profile if the pseudo and email are available in DB
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
	public void updateProfile(User userWithUpdate, User currentUser) {

		if(userWithUpdate.getPseudo().equals(currentUser.getPseudo()) && userWithUpdate.getEmail().equals(currentUser.getEmail())) {
			userDAO.update(userWithUpdate);
		}else if(userWithUpdate.getPseudo().equals(currentUser.getPseudo()) && !userWithUpdate.getEmail().equals(currentUser.getEmail())) {
			if(checkEmailAvailable(userWithUpdate.getEmail())) {
				userDAO.update(userWithUpdate);
			}else {
				System.err.println("Impossible de modifier l'utilisateur car l'email est déjà pris !");
			}
		}else if(!userWithUpdate.getPseudo().equals(currentUser.getPseudo()) && userWithUpdate.getEmail().equals(currentUser.getEmail())){
			if(checkPseudoAvailable(userWithUpdate.getPseudo())) {
				userDAO.update(userWithUpdate);
			}else {
				System.err.println("Impossible de modifier l'utilisateur car le pseudo est déjà pris !");
			}
		}else {
			if(checkPseudoAvailable(userWithUpdate.getPseudo()) && checkEmailAvailable(userWithUpdate.getEmail())) {
				userDAO.update(userWithUpdate);
			}else {
				System.err.println("Impossible de modifier l'utilisateur car le pseudo ou l'email est déjà pris !");
			}
		}
	
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
	
	
	private boolean checkPseudoAvailable(String pseudo) {
		int nbPseudo = userDAO.countPseudo(pseudo);
		System.err.println("nbPseudo : "+nbPseudo);
		
		if(nbPseudo>=1) {
			return false;
		}else {
			return true;
		}
	}
	
	private boolean checkEmailAvailable(String email) {
		int nbEmail = userDAO.countEmail(email);
		System.err.println("nbEmail : "+nbEmail);
		
		if(nbEmail>=1) {
			return false;
		}else {
			return true;
		}
	}

	@Override
	public User fillUserAttributes(User userToFill, User UserThatFills) {
		userToFill.setUserId(UserThatFills.getUserId());
		userToFill.setPseudo(UserThatFills.getPseudo());
		userToFill.setName(UserThatFills.getName());
		userToFill.setFirstName(UserThatFills.getFirstName());
		userToFill.setEmail(UserThatFills.getEmail());
		userToFill.setPhoneNumber(UserThatFills.getPhoneNumber());
		userToFill.setCredit(UserThatFills.getCredit());
		userToFill.setStreet(UserThatFills.getStreet());
		userToFill.setZipCode(UserThatFills.getZipCode());
		userToFill.setCity(UserThatFills.getCity());
		//password ?
		userToFill.setAdmin(UserThatFills.isAdmin());
		return null;
	}





}
