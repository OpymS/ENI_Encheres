package fr.eni.tp.encheres.bll;

import java.util.List;

import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;

/**
 * The Interface UserService.
 */
public interface UserService {
	
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
	 * @throws BusinessException 
	 */
	void createAccount(String pseudo, String name, String firstName, String email, String phoneNumber, String street, String zipCode, String city, String password, String passwordConfirm) throws BusinessException;
		
	/**
	 * Forgot password.
	 *
	 * @param email the email
	 */
	void forgotPassword(String email);
	

	/**
	 * Delete account.
	 *
	 * @param userId the user id
	 * @throws BusinessException 
	 */
	void deleteAccount(int userId) throws BusinessException;
	
	/**
	 * View own points.
	 *
	 * @param userId the user id
	 * @return the int
	 */
	int viewOwnPoints(int userId);
	
	/**
	 * View user profile.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	User viewUserProfile(int userId);
	
	
	/**
	 * Gets the user by email.
	 *
	 * @param email the email
	 * @return the user by email
	 */
	User getUserByEmail(String email);

	/**
	 * Update profile with userWithUpdate and checks with currentUser
	 *
	 * @param user
	 * @throws BusinessException 
	 */
	void updateProfile(User userWithUpdate, User currentUser) throws BusinessException;
	
	
	User fillUserAttributes(User userToFill, User UserThatFills);

	/**
	 * Gets user's password with his id.
	 *
	 * @param idUser the id
	 * @return the password hash from that user
	 */
	String getUserPasswordById(int idUser);

	/**
	 * Gets the user by id.
	 *
	 * @param userId the id
	 * @return the user by id
	 */
	User getUserById(int userId);

	/**
	 * Updates the user credit balance.
	 *
	 * @param User the user
	 * @return the user with the credit changed
	 */
	void updateUserCredit(User user);

	List<User> getAllUsers();
}
