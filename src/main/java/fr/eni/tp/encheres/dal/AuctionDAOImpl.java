package fr.eni.tp.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.User;

@Repository
public class AuctionDAOImpl implements AuctionDAO {
	private static final Logger auctionDaoLogger = LoggerFactory.getLogger(AuctionDAOImpl.class);
	
	private static final String FIND_BY_USER_AND_ARTICLE = "SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_utilisateur = :userId and no_article = :articleId";
	private static final String FIND_BY_USER = "SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_utilisateur = :userId";
	private static final String FIND_BY_ARTICLE = "SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_article = :articleId";
	private static final String INSERT = "INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (:userId, :articleId, :date, :bidAmount)";
	private static final String DELETE = "DELETE FROM ENCHERES WHERE no_utilisateur = :userId AND no_article = :articleId AND date_enchere = :auctionDate";

	private static final String ERASE_BY_USER_ID = "UPDATE ENCHERES SET no_utilisateur = 0 WHERE no_utilisateur = :userId";
	
	
	private NamedParameterJdbcTemplate jdbcTemplate;

	public AuctionDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Auction> read(int userId, int articleId) {
		auctionDaoLogger.info("Méthode read");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		mapSqlParameterSource.addValue("articleId", articleId);

		return jdbcTemplate.query(FIND_BY_USER_AND_ARTICLE, mapSqlParameterSource, new AuctionRowMapper());
	}

	@Override
	public List<Auction> findByUser(int userId) {
		auctionDaoLogger.info("Méthode findByUser");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		
		return jdbcTemplate.query(FIND_BY_USER, mapSqlParameterSource, new AuctionRowMapper());
	}

	@Override
	public List<Auction> findByArticle(int articleId) {
		auctionDaoLogger.info("Méthode findByArticle");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);

		return jdbcTemplate.query(FIND_BY_ARTICLE, mapSqlParameterSource, new AuctionRowMapper());
	}

	@Override
	public void create(Auction auction) {
		auctionDaoLogger.info("Méthode create");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", auction.getUser().getUserId());
		mapSqlParameterSource.addValue("articleId", auction.getArticle().getArticleId());
		mapSqlParameterSource.addValue("date", auction.getAuctionDate());
		mapSqlParameterSource.addValue("bidAmount", auction.getBidAmount());

		jdbcTemplate.update(INSERT, mapSqlParameterSource);

	}

	@Override
	public void delete(int userId, int articleId, LocalDateTime auctionDate) {
		auctionDaoLogger.info("Méthode delete");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		mapSqlParameterSource.addValue("articleId", articleId);
		mapSqlParameterSource.addValue("auctionDate", auctionDate);

		jdbcTemplate.update(DELETE, mapSqlParameterSource);
	}
		
	@Override
	public void eraseUserBidsByUserId(int userId) {
		auctionDaoLogger.info("Méthode eraseUserBidsByUserId");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);

		jdbcTemplate.update(ERASE_BY_USER_ID, mapSqlParameterSource);
	}

}

class AuctionRowMapper implements RowMapper<Auction> {

	@Override
	public Auction mapRow(ResultSet rs, int rowNum) throws SQLException {
		Auction auction = new Auction();

		Article article = new Article();
		article.setArticleId(rs.getInt("no_article"));
		auction.setArticle(article);

		User user = new User();
		user.setUserId(rs.getInt("no_utilisateur"));
		auction.setUser(user);

		auction.setAuctionDate(rs.getTimestamp("date_enchere").toLocalDateTime());
		auction.setBidAmount(rs.getInt("montant_enchere"));
		
		System.err.println(auction);
		
		return auction;
	}

}
