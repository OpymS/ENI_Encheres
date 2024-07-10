package fr.eni.tp.encheres.bll;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.dal.ArticleDAO;
import fr.eni.tp.encheres.dal.AuctionDAO;
import fr.eni.tp.encheres.dal.UserDAO;
import fr.eni.tp.encheres.exception.BusinessException;

// TODO: Auto-generated Javadoc
/**
 * The Class UserServiceImpl.
 */
@Service
public class UserServiceImpl implements UserService {
	private static final Logger userServiceLogger = LoggerFactory.getLogger(UserServiceImpl.class);

	private AuctionService auctionService;
	
	private UserDAO userDAO;
	private ArticleDAO articleDAO;
	private AuctionDAO auctionDAO;
	
	public UserServiceImpl(UserDAO userDAO,ArticleDAO articleDAO, AuctionDAO auctionDAO, AuctionService auctionService) {
		this.userDAO= userDAO;
		this.articleDAO = articleDAO;
		this.auctionDAO = auctionDAO;
		this.auctionService = auctionService;
	}

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
		userServiceLogger.info("Méthode createAccount");
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
				user.setActivated(true);

				userDAO.create(user);
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("error.database.access");
				throw be;
			}
		} else {
			throw be;
		}
	}

	private boolean checkPassword(String password, String passwordConfirm, BusinessException be) {
		userServiceLogger.info("Méthode checkPassword");
		boolean isValid = false;
		if (!password.isBlank() && password.equals(passwordConfirm)) {
			isValid = true;
		} else if (password.isBlank()) {
			be.add("error.password.blank");
		} else {
			be.add("error.password.different");
		}
		return isValid;
	}

	
	@Override
	public List<User> getAllUsers(){
		userServiceLogger.info("Méthode getAllUsers");
		return userDAO.findAll();
	}
	
	
	/**
	 * Forgot password.
	 *
	 * @param email the email
	 */
	@Override
	public void forgotPassword(String email) {
		userServiceLogger.info("Méthode forgotPassword");
		// TODO Auto-generated method stub

	}

	/**
	 * Delete account.
	 *
	 * @param userId the user id
	 */
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void deleteAccount(int userId) throws BusinessException{
		userServiceLogger.info("Méthode deleteAccount");
		BusinessException be = new BusinessException();
		
		//Check des conditions: 
		// 1 - Pas d'article dont la vente est terminée mais non récupérée
		// 2 - Pas acheteur courant d'un article en vente
		boolean isDeleteAccepted = checkArticlesState(userId, be);
		isDeleteAccepted &= checkUserBids(userId, be);
				
		
		if (isDeleteAccepted) {
			try {
				//Modifier les "enchères" ou mises de l'utilisateur
				auctionDAO.eraseUserBidsByUserId(userId);
				//Annuler les ventes de cet utilisateur (utiliser cancelArticle de AuctionService ?)
				// 1 - Récup les ventes de cet user, en état 2 et 3
				// 2 - Annuler ces ventes (rembourser les utilisateurs etc...)
				// 3 - Modifier les articles avec no_utilisateur à 0
				List<Article> userArticlesToCancel = articleDAO.findCancellableBySellerId(userId);
				
				if(userArticlesToCancel.size()!=0) {
					userArticlesToCancel.forEach(article -> {
						auctionService.cancelArticle(article); // On annule toutes les ventes d'articles quand c'est possible.
					});
				}
				//Modifier les articles vendus par cet utilisateur (passer le no_utilisateur à 0)
				articleDAO.eraseSellerByUserId(userId);
				//Supprimer l'utilisateur (car plus aucune foreign key)
				userDAO.deleteById(userId);
				
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("error.database.access");
				throw be;
			}
		} else {
			throw be;
		}
		
	}
	
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void desactivateAccount(int userId) throws BusinessException{
		userServiceLogger.info("Méthode desactivateAccount");
		BusinessException be = new BusinessException();
		
		//Check des conditions: 
		// 1 - Pas d'article dont la vente est terminée mais non récupérée
		// 2 - Pas acheteur courant d'un article en vente
		boolean isDesactivateAccepted = checkArticlesState(userId, be);
		isDesactivateAccepted &= checkUserBids(userId, be);
				
		
		if (isDesactivateAccepted) {
			try {
				//Modifier les "enchères" ou mises de l'utilisateur
				//auctionDAO.eraseUserBidsByUserId(userId);
				//Annuler les ventes de cet utilisateur (utiliser cancelArticle de AuctionService ?)
				// 1 - Récup les ventes de cet user, en état 2 et 3
				// 2 - Annuler ces ventes (rembourser les utilisateurs etc...)
				// 3 - Modifier les articles avec no_utilisateur à 0
				List<Article> userArticlesToCancel = articleDAO.findCancellableBySellerId(userId);
				
				if(userArticlesToCancel.size()!=0) {
					userArticlesToCancel.forEach(article -> {
						auctionService.cancelArticle(article); // On annule toutes les ventes d'articles quand c'est possible.
					});
				}
				//Modifier les articles vendus par cet utilisateur (passer le no_utilisateur à 0)
				//articleDAO.eraseSellerByUserId(userId);
				//Supprimer l'utilisateur (car plus aucune foreign key)
				userDAO.desactivateById(userId);
				
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("Un problème est survenu lors de l'accès à la base de données");
				throw be;
			}
		} else {
			throw be;
		}
		
	}
	
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void reactivateAccount(int userId) throws BusinessException{
		userServiceLogger.info("Méthode reactivateAccount");
		BusinessException be = new BusinessException();
		
		//Pour réactiver un compte, pas de check à faire car les articles vendus à la désactivation auront été annulés
		// il faut juste changr l'etat_utilisateur en BDD
		
		try {
			
			userDAO.reactivateById(userId);
			
		} catch (DataAccessException e) {
			e.printStackTrace();
			be.add("error.database.access");
			throw be;
		}
		
	}
	
	
	private boolean checkArticlesState(int userId,  BusinessException be) {
		userServiceLogger.info("Méthode checkArticlesState");
		boolean isDeleteOk = false;
		
		int nbArticlesFinished = articleDAO.countArticlesFinishedBySellerId(userId);
		
		if(nbArticlesFinished==0) {
			isDeleteOk = true;
		}else {
			be.add("error.accountsuppr.article");
		}
		
		return isDeleteOk;
		
	}
	
	private boolean checkUserBids(int userId,  BusinessException be) {
		userServiceLogger.info("Méthode checkUserBids");
		boolean isDeleteOk = false;
		
		int nbPossibleBuy = articleDAO.countArticlesByBuyerId(userId);
		
		if(nbPossibleBuy==0) {
			isDeleteOk = true;
		}else {
			be.add("error.accountsuppr.auction");
		}
		
		return isDeleteOk;
		
	}
	
	
	
	

	/**
	 * View own points.
	 *
	 * @param userId the user id
	 * @return the int
	 */
	@Override
	public int viewOwnPoints(int userId) {
		userServiceLogger.info("Méthode viewOwnPoints");
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
		userServiceLogger.info("Méthode viewUserProfile");
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
	@Transactional(rollbackFor = BusinessException.class)
	public void updateProfile(User userWithUpdate, User currentUser) throws BusinessException {
		userServiceLogger.info("Méthode updateProfile");
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
				userWithUpdate.setPassword(passwordEncoder.encode(userWithUpdate.getPassword()));
				userDAO.update(userWithUpdate);
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("error.database.access");
				throw be;
			}
		} else {
			throw be;
		}
	}
	
	/**
	 * Updates the user credit balance.
	 *
	 * @param User the user
	 * @return nothing
	 * @throws BusinessException 
	 */
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateUserCredit(User user) throws BusinessException {
		userServiceLogger.info("Méthode updateUserCredit");
		BusinessException be = new BusinessException();
		try {
			userDAO.updateCredit(user);
		} catch (DataAccessException e) {
			be.add("error.database.access");
			throw be;
		}
	}
	
	
	

	private boolean verifyPasswordMatch(String password1, String password2, BusinessException be) {
		userServiceLogger.info("Méthode verifyPasswordMatch");
		boolean isValid = false;
		if (passwordEncoder.matches(password1, password2)) {
			isValid = true;
		} else {
			be.add("error.password.typo");
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
		userServiceLogger.info("Méthode getUserByEmail");
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
		userServiceLogger.info("Méthode getUserById");
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
		userServiceLogger.info("Méthode getUserPasswordById");
		return userDAO.readPasswordById(idUser);
	}

	private boolean checkPseudoAvailable(String pseudo, BusinessException be) {
		userServiceLogger.info("Méthode checkPseudoAvailable");
		boolean isValid = false;
		int nbPseudo = userDAO.countPseudo(pseudo);
		if (nbPseudo == 0) {
			isValid = true;
		} else {
			be.add("error.pseudo.notavailable");
		}
		return isValid;
	}

	private boolean checkEmailAvailable(String email, BusinessException be) {
		userServiceLogger.info("Méthode checkEmailAvailable");
		boolean isValid = false;
		int nbEmail = userDAO.countEmail(email);
		if (nbEmail == 0) {
			isValid = true;
		} else {
			be.add("error.email.notavailable");
		}
		return isValid;
	}

	@Override // Fonctionne par référence ou en retournant une instance de l'user hydraté
	public User fillUserAttributes(User userToFill, User userThatFills) {
		userServiceLogger.info("Méthode fillUserAttributes");
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
		userToFill.setActivated(userThatFills.isActivated());
		return userToFill;
	}

}
