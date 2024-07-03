package fr.eni.tp.encheres.bll;

import fr.eni.tp.encheres.bo.User;

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
	 */
	void createAccount(String pseudo, String name, String firstName, String email, String phoneNumber, String street, String zipCode, String city, String password, String passwordConfirm);
		
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
	 */
	void deleteAccount(int userId);
	
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
	void updateProfile(int userId, String pseudo, String name, String firstName, String email, String phoneNumber, String street, String zipCode, String city, String password, String passwordConfirm);

	/**
	 * Gets the user by email.
	 *
	 * @param email the email
	 * @return the user by email
	 */
	User getUserByEmail(String email);
}
