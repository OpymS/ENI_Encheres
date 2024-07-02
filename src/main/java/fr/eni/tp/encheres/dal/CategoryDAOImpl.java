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
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.Category;

/**
 * The Class CategoryDAOImpl.
 */
@Repository
public class CategoryDAOImpl implements CategoryDAO {
	
	/** The Constant INSERT. */
	private static final String INSERT = "INSERT INTO CATEGORIES( no_categorie, libelle) VALUES (:idCategory, :libelle)";
	
	/** The Constant FIND_BY_ID. */
	private static final String FIND_BY_ID = "SELECT no_categorie, libelle FROM CATEGORIES WHERE no_categorie = :id";
	
	/** The Constant UPDATE. */
	private static final String UPDATE = "UPDATE CATEGORIES SET no_categorie = :idCategory, libelle = :libelle WHERE libelle = :libelle";
	
	/** The Constant FIND_ALL. */
	private static final String FIND_ALL = "SELECT no_categorie, libelle FROM CATEGORIES";
	
		
	/** The jdbc template. */
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	

	/**
	 * Creates the.
	 *
	 * @param category the category
	 */
	@Override
	public void create(Category category) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idCategory", category.getCategoryId());
		namedParameters.addValue("libelle", category.getLabel());
		
		jdbcTemplate.update(INSERT, namedParameters, keyHolder);
		
        if (keyHolder != null && keyHolder.getKey() != null) {
            category.setCategoryId(keyHolder.getKey().intValue());
        }
		
	}

	/**
	 * Read by id.
	 *
	 * @param id the id
	 * @return the category
	 */
	@Override
	public Category readById(int id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		
		return jdbcTemplate.queryForObject(FIND_BY_ID, namedParameters, new CategoryRowMapper());
	}
	
	
	/**
	 * Update.
	 *
	 * @param category the category
	 */
	@Override
	public void update(Category category) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idCategory", category.getCategoryId());
		namedParameters.addValue("libelle", category.getLabel());
		
		jdbcTemplate.update(UPDATE, namedParameters);
		
	}

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@Override
	public List<Category> findAll() {
		return jdbcTemplate.query(FIND_ALL, new CategoryRowMapper());
	}
	
	
	private static class CategoryRowMapper implements RowMapper<Category> {
		
		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			Category category = new Category();
			category.setCategoryId(rs.getInt("no_categorie"));
			category.setLabel(rs.getString("libelle"));
			return category;
		}
	}

}
