package fr.eni.tp.encheres.dal;

import java.util.List;

import fr.eni.tp.encheres.bo.Auction;

public interface AuctionDAO {
	Auction read(long userId, long articleId);

	List<Auction> findByArticle(long articleId);

	void create(Auction auction);

	void update(Auction auction);

	void delete(long userId, long articleId);
}
