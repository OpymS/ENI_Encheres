package fr.eni.tp.encheres.dal;

import java.util.List;
import fr.eni.tp.encheres.bo.User;

/**
 * The Interface UserDAO.
 */
public interface UserDAO {

	/**
	 * Creates the.
	 *
	 * @param user the user
	 */
	void create(User user);
	
	/**
	 * Read by pseudo.
	 *
	 * @param pseudo the pseudo
	 * @return the user
	 */
	User readByPseudo(String pseudo);
	
	/**
	 * Read by email.
	 *
	 * @param email the email
	 * @return the user
	 */
	User readByEmail(String email);
	
	/**
	 * Read by id.
	 *
	 * @param id the id
	 * @return the user
	 */
	User readById(int id);
	
	/**
	 * Update.
	 *
	 * @param user the user
	 */
	void update(User user);
	
	/**
	 * Delete by email.
	 *
	 * @param email the email
	 */
	void deleteByEmail(String email);
	
	/**
	 * Delete by id.
	 *
	 * @param id the id
	 */
	void deleteById(int id);
	
	/**
	 * Find all.
	 *
	 * @return the list
	 */
	List<User> findAll();

}
