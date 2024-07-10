package fr.eni.tp.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.PasswordResetToken;
import fr.eni.tp.encheres.bo.User;

@Repository
public class PasswordResetDAOImpl implements PasswordResetDAO {
	private static final Logger passwordResetDaoLogger = LoggerFactory.getLogger(PasswordResetDAOImpl.class);

	private static final String INSERT = "INSERT INTO TOKEN(token, user_id) VALUES(:token, :user_id)";
	private static final String FIND_BY_USER_ID = "SELECT * FROM TOKEN WHERE user_id = :user_id";

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private RowMapper<PasswordResetToken> rowMapper = new RowMapper<PasswordResetToken>() {
		@Override
		public PasswordResetToken mapRow(ResultSet rs, int rowNum) throws SQLException {
			PasswordResetToken token = new PasswordResetToken();
			token.setId(rs.getInt("id"));
			token.setToken(rs.getString("token"));
			token.setUserId(rs.getInt("user_id"));
			token.setExpiryDate(rs.getTimestamp("expiry_date"));
			return token;
		}
	};

	@Override
	public void save(PasswordResetToken token) {
		passwordResetDaoLogger.info("Méthode save");
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("token", token.getToken());
		namedParameters.addValue("user_id", token.getUserId());

		jdbcTemplate.update(INSERT, namedParameters, keyHolder);
		if (keyHolder != null && keyHolder.getKey() != null) {
			token.setId(keyHolder.getKey().intValue());
		}
	}

	@Override
	public PasswordResetToken findByUserId(int userId) {
		passwordResetDaoLogger.info("Méthode findByUserId");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("user_id", userId);
		return jdbcTemplate.queryForObject(FIND_BY_USER_ID, namedParameters, rowMapper);
	}

}
