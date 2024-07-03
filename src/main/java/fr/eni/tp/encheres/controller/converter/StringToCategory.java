package fr.eni.tp.encheres.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bo.Category;

@Component
public class StringToCategory implements Converter<String, Category> {
	private AuctionService auctionService;

	public StringToCategory(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	@Override
	public Category convert(String categoryId) {
		int id = Integer.parseInt(categoryId);
		Category category = new Category();
		if (id == 0) {
			category.setCategoryId(0);
		} else {
			category = auctionService.findCategoryById(id);
		}
		return category;
	}

}
