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
	    if (pseudo == null || pseudo.trim().isEmpty() ||
	        name == null || name.trim().isEmpty() ||
	        firstName == null || firstName.trim().isEmpty() ||
	        email == null || email.trim().isEmpty() ||
	        phoneNumber == null || phoneNumber.trim().isEmpty() ||
	        street == null || street.trim().isEmpty() ||
	        zipCode == null || zipCode.trim().isEmpty() ||
	        city == null || city.trim().isEmpty() ||
	        password == null || password.trim().isEmpty() ||
	        passwordConfirm == null || passwordConfirm.trim().isEmpty()) {
	        throw new IllegalArgumentException("Tous les champs doivent être remplis.");
	    }

	    if (!password.equals(passwordConfirm)) {
	        throw new IllegalArgumentException("Les mots de passe ne sont pas identiques.");
	    }

	    if (!pseudo.matches("^[a-zA-Z0-9]+$")) {
	        throw new IllegalArgumentException("Le pseudo ne doit contenir que des caractères alphanumériques.");
	    }

	    if (!checkPseudoAvailable(pseudo)) {
	        throw new IllegalArgumentException("Le pseudo existe déjà.");
	    }

	    if (!checkEmailAvailable(email)) {
	        throw new IllegalArgumentException("L'email existe déjà.");
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
		
		String updatedPseudo = userWithUpdate.getPseudo();
		String currentPseudo = currentUser.getPseudo();
		
		String updatedEmail = userWithUpdate.getEmail();
		String currentEmail = currentUser.getEmail();
		
		String currentPassword = currentUser.getPassword();
	
		String updatedPassword = userWithUpdate.getPassword();
		String updatedPasswordConfirm = userWithUpdate.getPasswordConfirm();
		
		boolean passwordMatch = passwordEncoder.matches(currentPassword,
				 										userDAO.readPasswordById(userWithUpdate.getUserId()));
		 
		if(!passwordMatch) { // Check password actuel
			 System.err.println("Le mot de passe actuel n'est pas le bon ...");
			 
		}else if(updatedPassword.isBlank() || !updatedPassword.equals(updatedPasswordConfirm)){ // Check égalité entre nouveau mot de passe et la confirmation OU que vide
			 System.err.println("Les mots de passe ne sont pas égaux ou vides !");
			 
		}else if(passwordMatch && !updatedPassword.isBlank() && updatedPassword.equals(updatedPasswordConfirm)){ // Si toutes les condtions favorables réunies, on vérifie le reste
			 
			if(updatedPseudo.equals(currentPseudo) && updatedEmail.equals(currentEmail)) {//Si pseudo et email égaux aux actuels alors pas besoin de vérifié leur dispo
				 	userWithUpdate.setPassword(passwordEncoder.encode(updatedPassword));
					userDAO.update(userWithUpdate);
			}else if(updatedPseudo.equals(currentPseudo) && !updatedEmail.equals(currentEmail)) {//Si email différent, check email
				if(checkEmailAvailable(updatedEmail)) { // Si ok alors modif
					userWithUpdate.setPassword(passwordEncoder.encode(updatedPassword));
					userDAO.update(userWithUpdate);
				}else {// Sinon email déja pris
					System.err.println("Impossible de modifier l'utilisateur car l'email est déjà pris !");
				}
			}else if(!updatedPseudo.equals(currentPseudo) && updatedEmail.equals(currentEmail)){// Si pseudo différent, check pseudo
				if(checkPseudoAvailable(updatedPseudo)) {// Si ok alors modif
					userWithUpdate.setPassword(passwordEncoder.encode(updatedPassword));
					userDAO.update(userWithUpdate);
				}else {// Sinon email déja pris
					System.err.println("Impossible de modifier l'utilisateur car le pseudo est déjà pris !");
				}
			}else {// Si on est là, les conditions précédents ne sont pas remplies, il faut alors check le pseudo et l'email.
				if(checkPseudoAvailable(updatedPseudo) && checkEmailAvailable(updatedEmail)) {
					userWithUpdate.setPassword(passwordEncoder.encode(updatedPassword));
					userDAO.update(userWithUpdate);
				}else {
					System.err.println("Impossible de modifier l'utilisateur car le pseudo ou l'email est déjà pris !");
				}
			}
		}
	}
	
	/**
	 * Updates the user credit balance.
	 *
	 * @param User the user
	 * @return nothing
	 */
	@Override
	public void updateUserCredit(User user) {
		userDAO.updateCredit(user);
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
	
	/**
	 * Gets the user by id.
	 *
	 * @param userId the id
	 * @return the user by id
	 */
	@Override
	public User getUserById(int userId) {
		return userDAO.readById(userId);
	}
	

	/**
	 * Gets user's password with his id.
	 *
	 * @param idUser the id
	 * @return the password hash from that user
	 */
	@Override
	public String getUserPasswordById(int idUser) {
		return userDAO.readPasswordById(idUser);
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

	@Override //Fonctionne par référence ou en retournant une instance de l'user hydraté
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
		userToFill.setPassword(null); // On ne stocke jamais le mot de passe d'un utilisateur.
		userToFill.setAdmin(UserThatFills.isAdmin());
		return userToFill;
	}





}
