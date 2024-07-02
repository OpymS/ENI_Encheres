package fr.eni.tp.encheres.dal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import fr.eni.tp.encheres.bo.Auction;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestAuctionDaoImpl {
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	
	@Test
	void testRead() {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", 1);
		mapSqlParameterSource.addValue("articleId", 1);
		assertNotNull(jdbcTemplate.queryForObject("SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_utilisateur = :userId and no_article = :articleId", mapSqlParameterSource, new AuctionRowMapper()));
		
//		MapSqlParameterSource mapSqlParameterSource2 = new MapSqlParameterSource();
//		mapSqlParameterSource2.addValue("userId", 5);
//		mapSqlParameterSource2.addValue("articleId", 3);
//		assertNull(jdbcTemplate.queryForObject("SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_utilisateur = :userId and no_article = :articleId", mapSqlParameterSource2, new AuctionRowMapper()));
	}
	
	@Test
	void testFindAll() {
		String FIND_BY_ARTICLE = "SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_article = :articleId";
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", 1);
		List<Auction> auctionList = jdbcTemplate.query(FIND_BY_ARTICLE, mapSqlParameterSource, new AuctionRowMapper());
		assertEquals(2, auctionList.size());
	}

}
