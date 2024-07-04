package fr.eni.tp.encheres.bll;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.dal.UserDAO;
import fr.eni.tp.encheres.exception.BusinessException;

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
	 * @param pseudo          the pseudo
	 * @param name            the name
	 * @param firstName       the first name
	 * @param email           the email
	 * @param phoneNumber     the phone number
	 * @param street          the street
	 * @param zipCode         the zip code
	 * @param city            the city
	 * @param password        the password
	 * @param passwordConfirm the password confirm
	 * @throws BusinessException
	 */
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void createAccount(String pseudo, String name, String firstName, String email, String phoneNumber,
			String street, String zipCode, String city, String password, String passwordConfirm)
			throws BusinessException {
		BusinessException be = new BusinessException();
		boolean isValid = checkPassword(password, passwordConfirm, be) && checkPseudoAvailable(pseudo, be)
				&& checkEmailAvailable(email, be);

		if (isValid) {
			try {
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
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("Un problème est survenu lors de l'accès à la base de données");
				throw be;
			}
		} else {
			throw be;
		}
	}

	private boolean checkPassword(String password, String passwordConfirm, BusinessException be) {
		boolean isValid = false;
		if (!password.isBlank() && password.equals(passwordConfirm)) {
			isValid = true;
		} else if (password.isBlank()) {
			be.add("Le mot de passe ne peut pas être vide");
		} else {
			be.add("Les mots de passe ne sont pas identiques");
		}
		return isValid;
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
	 * @param userId          the user id
	 * @param pseudo          the pseudo
	 * @param name            the name
	 * @param firstName       the first name
	 * @param email           the email
	 * @param phoneNumber     the phone number
	 * @param street          the street
	 * @param zipCode         the zip code
	 * @param city            the city
	 * @param password        the password
	 * @param passwordConfirm the password confirm
	 * @throws BusinessException
	 */
	@Override
	public void updateProfile(User userWithUpdate, User currentUser) throws BusinessException {
		BusinessException be = new BusinessException();
		boolean isValid = false;

		String updatedPseudo = userWithUpdate.getPseudo();
		String currentPseudo = currentUser.getPseudo();

		String updatedEmail = userWithUpdate.getEmail();
		String currentEmail = currentUser.getEmail();

		String currentPassword = currentUser.getPassword();

		String updatedPassword = userWithUpdate.getPassword();
		String updatedPasswordConfirm = userWithUpdate.getPasswordConfirm();

		isValid = verifyPasswordMatch(currentPassword, userDAO.readPasswordById(userWithUpdate.getUserId()), be)
				&& checkPassword(updatedPassword, updatedPasswordConfirm, be);

		isValid &= (updatedPseudo.equals(currentPseudo) || checkPseudoAvailable(updatedPseudo, be));
		isValid &= (updatedEmail.equals(currentEmail) || checkEmailAvailable(updatedEmail, be));

		if (isValid) {
			try {
				userDAO.update(userWithUpdate);
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("Un problème est survenu lors de l'accès à la base de données");
				throw be;
			}
		} else {
			throw be;
		}
	}

	private boolean verifyPasswordMatch(String password1, String password2, BusinessException be) {
		boolean isValid = false;
		if (passwordEncoder.matches(password1, password2)) {
			isValid = true;
		} else {
			be.add("Vous n'avez pas saisi le bon mot de passe dans mot de passe actuel");
		}
		return isValid;
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

	private boolean checkPseudoAvailable(String pseudo, BusinessException be) {
		boolean isValid = false;
		int nbPseudo = userDAO.countPseudo(pseudo);
		if (nbPseudo == 0) {
			isValid = true;
		} else {
			be.add("Ce pseudo n'est pas disponible");
		}
		return isValid;
	}

	private boolean checkEmailAvailable(String email, BusinessException be) {
		boolean isValid = false;
		int nbEmail = userDAO.countEmail(email);
		if (nbEmail == 0) {
			isValid = true;
		} else {
			be.add("Cet email n'est pas disponible");
		}
		return isValid;
	}

	@Override // Fonctionne par référence ou en retournant une instance de l'user hydraté
	public User fillUserAttributes(User userToFill, User userThatFills) {
		userToFill.setUserId(userThatFills.getUserId());
		userToFill.setPseudo(userThatFills.getPseudo());
		userToFill.setName(userThatFills.getName());
		userToFill.setFirstName(userThatFills.getFirstName());
		userToFill.setEmail(userThatFills.getEmail());
		userToFill.setPhoneNumber(userThatFills.getPhoneNumber());
		userToFill.setCredit(userThatFills.getCredit());
		userToFill.setStreet(userThatFills.getStreet());
		userToFill.setZipCode(userThatFills.getZipCode());
		userToFill.setCity(userThatFills.getCity());
		userToFill.setPassword(null); // On ne stocke jamais le mot de passe d'un utilisateur.
		userToFill.setAdmin(userThatFills.isAdmin());
		return userToFill;
	}

}
