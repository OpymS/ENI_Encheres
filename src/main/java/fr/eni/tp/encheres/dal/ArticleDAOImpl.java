package fr.eni.tp.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.ArticleState;
//import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.bo.PickupLocation;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.bo.dto.SearchCriteria;

@Repository
public class ArticleDAOImpl implements ArticleDAO{
	private static final Logger articleDaoLogger = LoggerFactory.getLogger(ArticleDAOImpl.class);
	
	private static final String FIND_BY_ID = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS WHERE no_article = :articleId";
	
	private static final String FIND_ALL = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS";
	private static final String FIND_BY_CATEGORY = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS WHERE no_categorie = :categoryId";
	private static final String FIND_BY_NAME = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS WHERE nom_article LIKE :name";
	private static final String FIND_BY_CATEGORY_AND_NAME = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS WHERE no_categorie = :categoryId AND nom_article LIKE :name";
	
	private static final String INSERT = "INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur,no_categorie, etat_vente, imageUUID) VALUES (:name, :description, :startDate, :endDate, :startPrice, :endPrice, :userId, :categoryId, :state, :imageUUID)";
	private static final String DELETE = "DELETE FROM ARTICLES_VENDUS WHERE no_article = :articleId";
	
	private static final String UPDATE_SELL_PRICE_AND_BUYER = "UPDATE ARTICLES_VENDUS SET prix_vente = :newBid, no_acheteur = :userId WHERE no_article = :articleId";
	private static final String UPDATE = "UPDATE ARTICLES_VENDUS SET nom_article = :name, description =:description, date_debut_encheres =:startDate, date_fin_encheres=:endDate, prix_initial=:startPrice, prix_vente=:endPrice, no_categorie=:categoryId, no_acheteur =:buyerId, etat_vente =:state, imageUUID = :imageUUID WHERE no_article = :articleId";
	private static final String UPDATE_STATE_BY_ID ="UPDATE ARTICLES_VENDUS SET etat_vente =:state WHERE no_article = :articleId";
	
	private static final String SCHEDULED_COUNT = "SELECT count(*) FROM ARTICLES_VENDUS WHERE no_article > :idMin";
	
	private static final String COUNT_FINISHED_BY_USER_ID = "SELECT count(*) FROM ARTICLES_VENDUS WHERE etat_vente = 3 AND no_utilisateur = :userId";
	private static final String COUNT_BUYERS_BY_USER_ID = "SELECT count(*) FROM ARTICLES_VENDUS WHERE etat_vente = 2 AND no_acheteur = :userId";

	private static final String FIND_TO_UPDATE_TO_FINISHED = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS WHERE date_fin_encheres < GETDATE() AND etat_vente = 2";
	private static final String FIND_TO_UPDATE_TO_STARTED = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS WHERE date_debut_encheres < GETDATE() AND etat_vente = 1";
	
	private static final String FIND_CANCELLABLE_BY_SELLER_ID = "SELECT no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS WHERE no_utilisateur = :userId AND etat_vente IN (2,3)";
	private static final String ERASE_BY_USER_ID = "UPDATE ARTICLES_VENDUS SET no_utilisateur = 0 WHERE no_utilisateur = :userId";
	
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private UserDAO userDAO;
	private CategoryDAO categoryDAO;
	private PickUpLocationDAO pickupLocationDAO;
	//private AuctionDAO auctionDAO;
	
	public ArticleDAOImpl(NamedParameterJdbcTemplate jdbcTemplate,
			UserDAO userDAO, CategoryDAO categoryDAO, PickUpLocationDAO pickupLocationDAO) {
		this.jdbcTemplate = jdbcTemplate;
		this.userDAO = userDAO;
		this.categoryDAO = categoryDAO;
		this.pickupLocationDAO = pickupLocationDAO;
		//this.auctionDAO = auctionDAO;
	}
	


	@Override
	public Article read(int articleId) {
		articleDaoLogger.info("Méthode read");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);
		
		return jdbcTemplate.queryForObject(FIND_BY_ID, mapSqlParameterSource, new ArticleRowMapper());
	}

	@Override
	public List<Article> findAll() {
		articleDaoLogger.info("Méthode findAll");
		return jdbcTemplate.query(FIND_ALL, new ArticleRowMapper());
	}
	
	@Override
	public List<Article> findCancellableBySellerId(int userId) {
		articleDaoLogger.info("Méthode findCancellableBySellerId");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		
		return jdbcTemplate.query(FIND_CANCELLABLE_BY_SELLER_ID, mapSqlParameterSource, new ArticleRowMapper());
	}

	@Override
	public List<Article> findByCategory(int categoryId) {
		articleDaoLogger.info("Méthode findByCategory");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("categoryId", categoryId);
		
		return jdbcTemplate.query(FIND_BY_CATEGORY, mapSqlParameterSource, new ArticleRowMapper());
	}

	@Override
	public List<Article> findByCategoryAndName(int categoryId, String name) {
		articleDaoLogger.info("Méthode findByCategoryAndName");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("categoryId", categoryId);
		mapSqlParameterSource.addValue("name", name);
		
		return jdbcTemplate.query(FIND_BY_CATEGORY_AND_NAME, mapSqlParameterSource, new ArticleRowMapper());
	}

	@Override
	public List<Article> findByName(String name) {
		articleDaoLogger.info("Méthode findByName");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("name", "%"+name+"%");

		return jdbcTemplate.query(FIND_BY_NAME, mapSqlParameterSource, new ArticleRowMapper());
	}

	@Override
	public void create(Article article) {
		articleDaoLogger.info("Méthode create");
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("name", article.getArticleName());
		mapSqlParameterSource.addValue("description", article.getDescription());
		mapSqlParameterSource.addValue("startDate", article.getAuctionStartDate());
		mapSqlParameterSource.addValue("endDate", article.getAuctionEndDate());
		mapSqlParameterSource.addValue("startPrice", article.getBeginningPrice());
		mapSqlParameterSource.addValue("endPrice", article.getCurrentPrice());
		mapSqlParameterSource.addValue("userId", article.getSeller().getUserId());
		mapSqlParameterSource.addValue("categoryId", article.getCategory().getCategoryId());
		mapSqlParameterSource.addValue("state", ArticleState.toInt(article.getState()));
		mapSqlParameterSource.addValue("imageUUID", article.getImageUUID());
		
		jdbcTemplate.update(INSERT, mapSqlParameterSource, keyHolder);
		
		if (keyHolder != null && keyHolder.getKey() != null) {
			article.setArticleId(keyHolder.getKey().intValue());
		}
		
	}
	
	@Override
	public void updateSellPriceAndBuyer(int articleId, int newPrice, int userId) {
		articleDaoLogger.info("Méthode updateSellPriceAndBuyer");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);
		mapSqlParameterSource.addValue("userId", userId);
		mapSqlParameterSource.addValue("newBid", newPrice);
		
		jdbcTemplate.update(UPDATE_SELL_PRICE_AND_BUYER, mapSqlParameterSource);
	}
	
	@Override
	public void updateArticle(Article article) {
		articleDaoLogger.info("Méthode updateArticle");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", article.getArticleId());
		mapSqlParameterSource.addValue("name", article.getArticleName());
		mapSqlParameterSource.addValue("description", article.getDescription());
		mapSqlParameterSource.addValue("startDate", article.getAuctionStartDate());
		mapSqlParameterSource.addValue("endDate", article.getAuctionEndDate());
		mapSqlParameterSource.addValue("startPrice", article.getBeginningPrice());
		mapSqlParameterSource.addValue("endPrice", article.getCurrentPrice());
		mapSqlParameterSource.addValue("categoryId", article.getCategory().getCategoryId());
		mapSqlParameterSource.addValue("buyerId", article.getCurrentBuyer().getUserId());
		mapSqlParameterSource.addValue("state", ArticleState.toInt(article.getState()));
		mapSqlParameterSource.addValue("imageUUID", article.getImageUUID());
		
		jdbcTemplate.update(UPDATE, mapSqlParameterSource);
	}

	@Override
	public void updateArticleState(ArticleState articleState, int articleId) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);
		mapSqlParameterSource.addValue("state", ArticleState.toInt(articleState));
		
		jdbcTemplate.update(UPDATE_STATE_BY_ID, mapSqlParameterSource);
	}
	
	
	@Override
	public void delete(int articleId) {
		articleDaoLogger.info("Méthode delete");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("articleId", articleId);
		
		jdbcTemplate.update(DELETE, mapSqlParameterSource);
	}
	
	@Override
	public void eraseSellerByUserId(int userId) {
		articleDaoLogger.info("Méthode eraseSellerByUserId");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		
		jdbcTemplate.update(ERASE_BY_USER_ID, mapSqlParameterSource);
	}
	
	@Override
	public int countArticles() {
		articleDaoLogger.info("Méthode countArticles");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("idMin", 0);

		return jdbcTemplate.queryForObject(SCHEDULED_COUNT,mapSqlParameterSource, Integer.class);
	}
		
	@Override
	public int countArticlesFinishedBySellerId(int userId) {
		articleDaoLogger.info("Méthode countArticlesFinishedBySeller");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);

		return jdbcTemplate.queryForObject(COUNT_FINISHED_BY_USER_ID,mapSqlParameterSource, Integer.class);
	}

	@Override
	public int countArticlesByBuyerId(int userId) {
		articleDaoLogger.info("Méthode countArticlesByBuyerId");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);

		return jdbcTemplate.queryForObject(COUNT_BUYERS_BY_USER_ID,mapSqlParameterSource, Integer.class);
	}
	
	@Override
	public List<Article> findArticleToUpdateToFinished() {
		articleDaoLogger.info("Méthode findArticleToUpdateToFinished");
		return jdbcTemplate.query(FIND_TO_UPDATE_TO_FINISHED, new ArticleRowMapper());
	}
	
	@Override
	public List<Article> findArticleToUpdateToStarted() {
		articleDaoLogger.info("Méthode findArticleToUpdateToStarted");
		return jdbcTemplate.query(FIND_TO_UPDATE_TO_STARTED, new ArticleRowMapper());
	}
	
	@Override
	public List<Article> findWithFilters(SearchCriteria research, int userId){
		articleDaoLogger.info("Méthode findWithFilters");
		//Récupérer les paramètres de filtre :
    
		String textFilter = "%"+research.getWordToFind()+"%";
		int categoryId = research.getCategory().getCategoryId();
    
		// Construire la requête à envoyée en fonction des filtres
		String SQLQuery = "";

		//Corps commun de requête
		SQLQuery = SQLQuery.concat("SELECT DISTINCT av.no_article, nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, av.no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID FROM ARTICLES_VENDUS av LEFT JOIN ENCHERES en ON av.no_article = en.no_article");
		
		//Permettra de savoir si il faudra enlever le OR ou AND à la fin de la requête.
		boolean containsConditions = false;
		
		
		//Si filters.containsValue(true) OU un categoryId est sélectionné OU le texte a recherché est vide, on ajoute WHERE
		if(research.getFilters().containsValue(true) ||categoryId!=0 || !research.getWordToFind().isBlank()) {
			containsConditions = true;
			SQLQuery = SQLQuery.concat(" WHERE");
			
			//Ajout du filtre de catégorie
			if(categoryId!=0) {
				SQLQuery = SQLQuery.concat(" (no_categorie = :categoryId) AND");
			}
			//Ajout du filtre dans le nom
			if(!textFilter.isBlank()) {
				SQLQuery = SQLQuery.concat(" (nom_article LIKE :textFilter OR description LIKE :textFilter) AND");
			}
			
			// On boucle à travers filters
			String[] filterSQL = {""}; // Astuce pour faire passer une variable dans une forEach ...
			research.getFilters().forEach((key, value)->{
				// on ajoute la condition de requête si la value est true !
				String conditionSQL = value ? mapToSQLCondition(key) : "";
				filterSQL[0] = filterSQL[0].concat(conditionSQL);
			});
			SQLQuery = SQLQuery.concat(filterSQL[0]);
		}
		
		//Il y aura un AND ou OR en trop si true
		if(containsConditions) {
				SQLQuery = SQLQuery.substring(0, SQLQuery.length()-3);
		} else { // s'il n'y a aucune case cochée, on retourne une liste vide.
			List<Article> listeVide = new ArrayList<Article>();
			return listeVide;
		}
		
		
		//Appel à la base de donnée	
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("userId", userId);
		mapSqlParameterSource.addValue("categoryId", categoryId);
		mapSqlParameterSource.addValue("textFilter", textFilter);
		
		
		articleDaoLogger.debug(SQLQuery);
		return jdbcTemplate.query(SQLQuery, mapSqlParameterSource, new ArticleRowMapper());
			
	}
	
	private String mapToSQLCondition(String conditionTitle) {
		articleDaoLogger.info("Méthode mapToSQLCondition");
		String conditionSQL = "";
		switch(conditionTitle) {
			case "open":
				conditionSQL = " (etat_vente = 2) OR";
				break;
			case "current":
				conditionSQL = " (etat_vente = 2 AND av.no_utilisateur = :userId) OR";
				break;
			case "won":
				conditionSQL = " (etat_vente IN (3,4) AND no_acheteur = :userId) OR";
				break;
			case "currentVente":
				conditionSQL = " (etat_vente = 2 AND av.no_utilisateur = :userId) OR";
				break;
			case "notstarted":
				conditionSQL = " (etat_vente = 1 AND av.no_utilisateur = :userId) OR";
				break;
			case "finished":
				conditionSQL = " (etat_vente IN (3,4) AND av.no_utilisateur = :userId) OR";
				break;
		}
		return conditionSQL;
	}
	

	class ArticleRowMapper implements RowMapper<Article> {
		
		@Override
		public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
			Article article = new Article();
			
			Category category = categoryDAO.readById(rs.getInt("no_categorie"));
			PickupLocation pickupLocation = pickupLocationDAO.findByArticleId(rs.getInt("no_article"));
			User seller = userDAO.readById(rs.getInt("no_utilisateur"));
			
			
			if(rs.getInt("no_acheteur")==0) {
				User emptyBuyer = new User();
				emptyBuyer.setUserId(0);
				article.setCurrentBuyer(emptyBuyer);
			}else {
				User currentBuyer = userDAO.readById(rs.getInt("no_acheteur"));
				article.setCurrentBuyer(currentBuyer);
			}
			
			article.setImageUUID(rs.getString("imageUUID"));
			
			article.setState(ArticleState.toArticleState(rs.getInt("etat_vente")));
			
			article.setArticleId(rs.getInt("no_article"));
			article.setArticleName(rs.getString("nom_article"));
			article.setDescription(rs.getString("description"));
			article.setAuctionStartDate(rs.getTimestamp("date_debut_encheres").toLocalDateTime());
			article.setAuctionEndDate(rs.getTimestamp("date_fin_encheres").toLocalDateTime());
			
			article.setBeginningPrice(rs.getInt("prix_initial"));
			article.setCurrentPrice(rs.getInt("prix_vente"));
			
			article.setCategory(category);
			article.setPickupLocation(pickupLocation);
			article.setSeller(seller);
			
			
			return article;
		}
	}
}

