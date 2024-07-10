package fr.eni.tp.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.PickupLocation;

@Repository
public class PickUpLocationDAOImpl implements PickUpLocationDAO{
	private static final Logger pickUpLocationDaoLogger = LoggerFactory.getLogger(PickUpLocationDAOImpl.class);
	
	private static final String FIND_BY_ARTICLE = "SELECT no_article, rue, code_postal, ville FROM RETRAITS WHERE no_article = :articleId";
	private static final String INSERT = "INSERT INTO RETRAITS (no_article, rue, code_postal, ville) VALUES (:articleId, :street,:zipCode,:city)";
	private static final String UPDATE_BY_ARTICLE_ID = "UPDATE RETRAITS SET rue = :street, code_postal= :zipCode, ville= :city WHERE no_article = :articleId";
	
	private NamedParameterJdbcTemplate jdbcTemplate;

	public PickUpLocationDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public PickupLocation findByArticleId(int articleId) {
		pickUpLocationDaoLogger.info("Méthode findByArticleId");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);
		
		return jdbcTemplate.queryForObject(FIND_BY_ARTICLE, mapSqlParameterSource, new PickUpLocationRowMapper());
	}

	@Override
	public void create(PickupLocation pickupLocation, int articleId) {
		pickUpLocationDaoLogger.info("Méthode create");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);
		mapSqlParameterSource.addValue("street", pickupLocation.getStreet());
		mapSqlParameterSource.addValue("zipCode", pickupLocation.getZipCode());
		mapSqlParameterSource.addValue("city", pickupLocation.getCity());
		
		jdbcTemplate.update(INSERT, mapSqlParameterSource);
	}
	
	@Override
	public void updatePickUpLocationByArticleId(int articleId, PickupLocation pickupLocation) {
		pickUpLocationDaoLogger.info("Méthode updatePickUpLocationByArticleId");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("street", pickupLocation.getStreet());
		mapSqlParameterSource.addValue("zipCode", pickupLocation.getZipCode());
		mapSqlParameterSource.addValue("city", pickupLocation.getCity());
		mapSqlParameterSource.addValue("articleId", articleId);
		
		jdbcTemplate.update(UPDATE_BY_ARTICLE_ID, mapSqlParameterSource);
	}

}

class PickUpLocationRowMapper implements RowMapper<PickupLocation>{

	@Override
	public PickupLocation mapRow(ResultSet rs, int rowNum) throws SQLException {
		PickupLocation pickupLocation = new PickupLocation();
		pickupLocation.setStreet(rs.getString("rue"));
		pickupLocation.setZipCode(rs.getString("code_postal"));
		pickupLocation.setCity(rs.getString("ville"));
		return pickupLocation;
	}
	
}
