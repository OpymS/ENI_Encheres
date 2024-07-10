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

import fr.eni.tp.encheres.bo.PasswordResetToken;
import fr.eni.tp.encheres.bo.User;

@Repository
public class PasswordResetDAOImpl implements PasswordResetDAO {
  
	private static final String INSERT = "INSERT INTO TOKEN(token, user_id) VALUES(:token, :user_id)";
    private static final String FIND_BY_USER_ID = "SELECT * FROM TOKEN WHERE user_id = :user_id";
    private static final String FIND_BY_TOKEN = "SELECT * FROM TOKEN WHERE token = :token";
    private static final String DELETE = "DELETE FROM TOKEN WHERE id = :id";
    
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
    	KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("token", token.getToken());
        namedParameters.addValue("user_id", token.getUserId());
        namedParameters.addValue("expiry_date", token.getExpiryDate());
        
        jdbcTemplate.update(INSERT, namedParameters, keyHolder);
        if (keyHolder != null && keyHolder.getKey() != null) {
            token.setId(keyHolder.getKey().intValue());
        }
    }
    
        @Override
        public PasswordResetToken findByUserId(int userId) {
            MapSqlParameterSource namedParameters = new MapSqlParameterSource();
            namedParameters.addValue("user_id", userId);
            return jdbcTemplate.queryForObject(FIND_BY_USER_ID, namedParameters, rowMapper);
        }
        
        @Override
        public PasswordResetToken findByToken(String token) {
            List<PasswordResetToken> tokens = jdbcTemplate.query("SELECT * FROM TOKEN", rowMapper);
            for (PasswordResetToken storedToken : tokens) {
                if (passwordEncoder.matches(token, storedToken.getToken())) {
                    return storedToken;
                }
            }
            return null;
        }
	
        @Override
        public void delete(PasswordResetToken token) {
            MapSqlParameterSource namedParameters = new MapSqlParameterSource();
            namedParameters.addValue("id", token.getId());
            jdbcTemplate.update(DELETE, namedParameters);
        }
}
