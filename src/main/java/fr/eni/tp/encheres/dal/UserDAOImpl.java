package fr.eni.tp.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.User;

/**
 * The Class UserDAOImpl.
 */
@Repository
public class UserDAOImpl implements UserDAO {
	
	/** The Constant INSERT. */
	private static final String INSERT = "INSERT INTO UTILISATEURS(pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, etat_utilisateur) VALUES (:pseudo, :nom, :prenom, :email, :telephone, :rue, :code_postal, :ville, :mot_de_passe, :credit, :administrateur, :etat_utilisateur)";
	
	/** The Constant FIND_BY_PSEUDO. */
	private static final String FIND_BY_PSEUDO = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, etat_utilisateur FROM UTILISATEURS WHERE pseudo = :pseudo";
	
	/** The Constant FIND_BY_EMAIL. */
	private static final String FIND_BY_EMAIL = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, etat_utilisateur FROM UTILISATEURS WHERE email = :email";
	
	/** The Constant FIND_BY_ID. */
	private static final String FIND_BY_ID = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, etat_utilisateur FROM UTILISATEURS WHERE no_utilisateur = :id";
	
	/** The Constant UPDATE. */ //Attention mot_de_passe enlever pour le moment
	private static final String UPDATE_BY_ID = "UPDATE UTILISATEURS SET pseudo = :pseudo, nom = :nom, prenom = :prenom, email = :email, telephone = :telephone, rue = :rue, code_postal = :code_postal , ville = :ville, mot_de_passe = :mot_de_passe, credit = :credit, administrateur = :administrateur, etat_utilisateur = :etat_utilisateur WHERE no_utilisateur = :id";
	
	/** The Constant DELETE_BY_EMAIL. */
	private static final String DELETE_BY_EMAIL = "DELETE FROM UTILISATEURS WHERE email = :email";
	
	/** The Constant DELETE_BY_ID. */
	private static final String DELETE_BY_ID = "DELETE FROM UTILISATEURS WHERE no_utilisateur = :id";
	
	private static final String DESACTIVATE_BY_ID = "UPDATE UTILISATEURS SET etat_utilisateur = 0 WHERE no_utilisateur = :userId";
	
	
	/** The Constant FIND_ALL. */
	private static final String FIND_ALL = "SELECT no_utilisateur, pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, etat_utilisateur FROM UTILISATEURS";
	
	private static final String GET_PASSWORD_HASH_BY_ID = "SELECT mot_de_passe FROM UTILISATEURS WHERE no_utilisateur = :id";
	
	private static final String UPDATE_CREDIT_BY_ID ="UPDATE UTILISATEURS SET credit = :newCredit WHERE no_utilisateur = :id";
	
	/** REQUETE POUR VALIDER LE PSEUDO */
	private static final String COUNT_BY_PSEUDO = "SELECT count(*) FROM UTILISATEURS WHERE pseudo = :pseudo";
	
	/** REQUETE POUR VALIDER L'EMAIL */
	private static final String COUNT_BY_EMAIL = "SELECT count(*) FROM UTILISATEURS WHERE email = :email";
	

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
		namedParameters.addValue("etat_utilisateur", user.isActivated());
		
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
		
		return jdbcTemplate.queryForObject(FIND_BY_PSEUDO, namedParameters, new UserRowMapper());
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
		
		return jdbcTemplate.queryForObject(FIND_BY_EMAIL, namedParameters,  new UserRowMapper());
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
		
		return jdbcTemplate.queryForObject(FIND_BY_ID, namedParameters,  new UserRowMapper());
	}


	/**
	 * Update.
	 *
	 * @param user the user
	 */
	@Override
	public void update(User user) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", user.getUserId());
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
		namedParameters.addValue("etat_utilisateur", user.isActivated());
		
		jdbcTemplate.update(UPDATE_BY_ID, namedParameters);
		
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
	
	@Override
	public void desactivateById(int userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userId", userId);
		
		jdbcTemplate.update(DESACTIVATE_BY_ID, namedParameters);
	}


	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@Override
	public List<User> findAll() {
		return jdbcTemplate.query(FIND_ALL,  new UserRowMapper());
	}
	
	
	/**
	 * Read the password hash by id.
	 *
	 * @param id the id
	 * @return the password hash
	 */
	@Override
	public String readPasswordById(int idUser) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", idUser);
		
		return jdbcTemplate.queryForObject(GET_PASSWORD_HASH_BY_ID, namedParameters,  String.class);
	}
	
	
	@Override
	public void updateCredit(User user) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", user.getUserId());
		namedParameters.addValue("newCredit", user.getCredit());
		
		jdbcTemplate.update(UPDATE_CREDIT_BY_ID, namedParameters);
	}
	
	
	/**
	 * Count the users in DB by pseudo in order to check if the pseudo is available
	 *
	 * @return an int with the number of users with that pseudo
	 */
	@Override
	public int countPseudo(String pseudoUser) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		namedParameters.addValue("pseudo", pseudoUser);
		return jdbcTemplate.queryForObject(COUNT_BY_PSEUDO, namedParameters, Integer.class);
		
	}

	/**
	 * Count the users in DB by email in order to check if the email is available
	 *
	 * @return an int with the number of users with that pseudo
	 */
	@Override
	public int countEmail(String emailUser) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		namedParameters.addValue("email", emailUser);
		return jdbcTemplate.queryForObject(COUNT_BY_EMAIL, namedParameters, Integer.class);
		
	}

	private static class UserRowMapper implements RowMapper<User> {
		
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		
			User user = new User();
			
			user.setUserId(rs.getInt("no_utilisateur"));
			user.setPseudo(rs.getString("pseudo"));
			user.setName(rs.getString("nom"));
			user.setFirstName(rs.getString("prenom"));
			user.setEmail(rs.getString("email"));
			user.setPhoneNumber(rs.getString("telephone"));
			user.setStreet(rs.getString("rue"));
			user.setZipCode(rs.getString("code_postal"));
			user.setCity(rs.getString("ville"));
			user.setPassword(rs.getString("mot_de_passe"));
			user.setCredit(rs.getInt("credit"));
			user.setAdmin(rs.getBoolean("administrateur"));
			user.setActivated(rs.getBoolean("etat_utilisateur"));
			
			return user;
		}

	}

	

}
