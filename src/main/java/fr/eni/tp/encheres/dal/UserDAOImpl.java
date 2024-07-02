package fr.eni.tp.encheres.dal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.User;

/**
 * The Class UserDAOImpl.
 */
@Repository
public class UserDAOImpl implements UserDAO {
	
	/** The Constant INSERT. */
	private static final String INSERT = "INSERT INTO UTILISATEURS(pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur) VALUES (:pseudo, :nom, :prenom, :email, :telephone, :rue, :code_postal, :ville, :mot_de_passe, :credit, :administrateur)";
	
	/** The Constant FIND_BY_PSEUDO. */
	private static final String FIND_BY_PSEUDO = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur FROM UTILISATEURS WHERE email = :pseudo";
	
	/** The Constant FIND_BY_EMAIL. */
	private static final String FIND_BY_EMAIL = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur FROM UTILISATEURS WHERE email = :email";
	
	/** The Constant FIND_BY_ID. */
	private static final String FIND_BY_ID = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur FROM UTILISATEURS WHERE no_utilisateur = :id";
	
	/** The Constant UPDATE. */
	private static final String UPDATE = "UPDATE UTILISATEURS SET pseudo = :pseudo, nom = :nom, prenom = ;prenom, email = :email, telephone = :telephone, rue = :rue, code_postal = :code_postal , ville = :ville, mot_de_passe = :mot_de_passe, credit = :credit, administrateur = :administrateur WHERE  email = :email";
	
	/** The Constant DELETE_BY_EMAIL. */
	private static final String DELETE_BY_EMAIL = "DELETE FROM UTILISATEURS WHERE eamil = :email";
	
	/** The Constant DELETE_BY_ID. */
	private static final String DELETE_BY_ID = "DELETE FROM UTILISATEURS WHERE no_utilisateur = :id";
	
	/** The Constant FIND_ALL. */
	private static final String FIND_ALL = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur FROM UTILISATEURS";
	

	/** The jdbc template. */
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	

	/**
	 * Create a User.
	 *
	 * @param user the user
	 */
	@Override
	public void create(User user) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("pseudo", user.getPseudo());
		namedParameters.addValue("nom", user.getName());
		namedParameters.addValue("prenom", user.getFirstName());
		namedParameters.addValue("email", user.getEmail());
		namedParameters.addValue("telephone", user.getPhoneNumber());
		namedParameters.addValue("rue", user.getStreet());
		namedParameters.addValue("code_postal", user.getZipCode());
		namedParameters.addValue("ville", user.getCity());
		namedParameters.addValue("mot_de_passe", user.getPassword());
		namedParameters.addValue("credit", user.getCredit());
		namedParameters.addValue("administrateur", user.isAdmin());
		
		jdbcTemplate.update(INSERT, namedParameters, keyHolder);
		
        if (keyHolder != null && keyHolder.getKey() != null) {
            user.setUserId(keyHolder.getKey().intValue());
        }
	}

	/**
	 * Read by pseudo.
	 *
	 * @param pseudo the pseudo
	 * @return the user
	 */
	@Override
	public User readByPseudo(String pseudo) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("pseudo", pseudo);
		
		return jdbcTemplate.queryForObject(FIND_BY_PSEUDO, namedParameters, new BeanPropertyRowMapper<>(User.class));
	}

	/**
	 * Read by email.
	 *
	 * @param email the email
	 * @return the user
	 */
	@Override
	public User readByEmail(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("email", email);
		
		return jdbcTemplate.queryForObject(FIND_BY_EMAIL, namedParameters, new BeanPropertyRowMapper<>(User.class));
	}


	/**
	 * Read by id.
	 *
	 * @param id the id
	 * @return the user
	 */
	@Override
	public User readById(int id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		
		return jdbcTemplate.queryForObject(FIND_BY_ID, namedParameters, new BeanPropertyRowMapper<>(User.class));
	}


	/**
	 * Update.
	 *
	 * @param user the user
	 */
	@Override
	public void update(User user) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("pseudo", user.getPseudo());
		namedParameters.addValue("nom", user.getName());
		namedParameters.addValue("prenom", user.getFirstName());
		namedParameters.addValue("email", user.getEmail());
		namedParameters.addValue("telephone", user.getPhoneNumber());
		namedParameters.addValue("rue", user.getStreet());
		namedParameters.addValue("code_postal", user.getZipCode());
		namedParameters.addValue("ville", user.getCity());
		namedParameters.addValue("mot_de_passe", user.getPassword());
		namedParameters.addValue("credit", user.getCredit());
		namedParameters.addValue("administrateur", user.isAdmin());
		
		jdbcTemplate.update(UPDATE, namedParameters);
		
	}


	/**
	 * Delete by email.
	 *
	 * @param email the email
	 */
	@Override
	public void deleteByEmail(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("email", email);
		
		jdbcTemplate.update(DELETE_BY_EMAIL, namedParameters);

	}


	/**
	 * Delete by id.
	 *
	 * @param id the id
	 */
	@Override
	public void deleteById(int id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		
		jdbcTemplate.update(DELETE_BY_ID, namedParameters);
		
	}


	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@Override
	public List<User> findAll() {
		return jdbcTemplate.query(FIND_ALL, new BeanPropertyRowMapper<>(User.class));
	}






}
