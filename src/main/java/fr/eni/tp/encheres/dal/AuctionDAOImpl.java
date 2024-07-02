package fr.eni.tp.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.User;

@Repository
public class AuctionDAOImpl implements AuctionDAO {

	private static final String FIND_BY_USER_AND_ARTICLE = "SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_utilisateur = :userId and no_article = :articleId";
	private static final String FIND_BY_ARTICLE = "SELECT no_utilisateur, no_article, date_enchere, montant_enchere FROM ENCHERES WHERE no_article = :articleId";
	private static final String INSERT = "INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (:userId, :articleId, :date, :bidAmount)";
	private static final String UPDATE = "UPDATE ENCHERES SET date_enchere = :date, montant_enchere = :bidAmount WHERE no_utilisateur = :userId AND no_article = :articleId";
	private static final String DELETE = "DELETE FROM ENCHERES WHERE no_utilisateur = :userId AND no_article = :articleId";

	private NamedParameterJdbcTemplate jdbcTemplate;

	public AuctionDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Auction read(long userId, long articleId) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		mapSqlParameterSource.addValue("articleId", articleId);

		return jdbcTemplate.queryForObject(FIND_BY_USER_AND_ARTICLE, mapSqlParameterSource, new AuctionRowMapper());
	}

	@Override
	public List<Auction> findByArticle(long articleId) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);

		return jdbcTemplate.query(FIND_BY_ARTICLE, mapSqlParameterSource, new AuctionRowMapper());
	}

	@Override
	public void create(Auction auction) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", auction.getUser().getUserId());
		mapSqlParameterSource.addValue("articleId", auction.getArticle().getArticleId());
		mapSqlParameterSource.addValue("date", auction.getAuctionDate());
		mapSqlParameterSource.addValue("bidAmount", auction.getBidAmount());

		jdbcTemplate.update(INSERT, mapSqlParameterSource);

	}

	@Override
	public void update(Auction auction) {
		long userId = auction.getUser().getUserId();
		long articleId = auction.getArticle().getArticleId();
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		mapSqlParameterSource.addValue("articleId", articleId);
		mapSqlParameterSource.addValue("date", auction.getAuctionDate());
		mapSqlParameterSource.addValue("bidAmount", auction.getBidAmount());

		jdbcTemplate.update(UPDATE, mapSqlParameterSource);
	}

	@Override
	public void delete(long userId, long articleId) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		mapSqlParameterSource.addValue("articleId", articleId);

		jdbcTemplate.update(DELETE, mapSqlParameterSource);
	}

}

class AuctionRowMapper implements RowMapper<Auction> {

	@Override
	public Auction mapRow(ResultSet rs, int rowNum) throws SQLException {
		Auction auction = new Auction();

		Article article = new Article();
		article.setArticleId(rs.getLong("no_article"));
		auction.setArticle(article);

		User user = new User();
		user.setUserId(rs.getLong("no_utilisateur"));
		auction.setUser(user);

		auction.setAuctionDate(rs.getTimestamp("date_enchere").toLocalDateTime());
		auction.setBidAmount(rs.getInt("montant_enchere"));
		return auction;
	}

}
