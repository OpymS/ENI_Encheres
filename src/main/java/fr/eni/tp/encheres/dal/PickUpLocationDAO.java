package fr.eni.tp.encheres.dal;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.PickupLocation;

public interface PickUpLocationDAO {
	PickupLocation findByArticleId(int articleId); 
	void create(PickupLocation pickupLocation, int articleId);
	void updatePickUpLocationByArticleId(int articleId, PickupLocation pickupLocation);
}
