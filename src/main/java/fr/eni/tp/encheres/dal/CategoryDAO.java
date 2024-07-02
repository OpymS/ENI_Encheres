package fr.eni.tp.encheres.dal;

import java.util.List;

import fr.eni.tp.encheres.bo.Category;

public interface CategoryDAO {
	
	void create(Category category);
	Category readById(int id);
	void update(Category category);
	List<Category> findAll();

}
