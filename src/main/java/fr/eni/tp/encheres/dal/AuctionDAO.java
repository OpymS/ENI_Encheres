package fr.eni.tp.encheres.dal;

import java.time.LocalDateTime;
import java.util.List;

import fr.eni.tp.encheres.bo.Auction;

public interface AuctionDAO {
	List<Auction> read(int userId, int articleId);

	List<Auction> findByUser(int userId);
	
	List<Auction> findByArticle(int articleId);

	void create(Auction auction);


	void delete(int userId, int articleId, LocalDateTime date);
}
