package fr.eni.tp.encheres.controller.converter;

import org.springframework.core.convert.converter.Converter;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bo.Category;

public class StringToCategory implements Converter<String, Category>{
	private AuctionService auctionService;
	
	public StringToCategory(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	@Override
	public Category convert(String categoryId) {
		int id = Integer.parseInt(categoryId);
		if (id == 0) {
			Category category = new Category();
			category.setCategoryId(0);
			return category;
		} else {
			return auctionService.findCategoryById(id);
		}
	}

}
