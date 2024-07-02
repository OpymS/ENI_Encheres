package fr.eni.tp.encheres.bo;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private long categoryId;
	private String label;
	private List<Article> articles;

	public Category() {
		articles = new ArrayList<Article>();
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	@Override
	public String toString() {
		return String.format("Category [categoryId=%s, label=%s, articles=%s]", categoryId, label, articles);
	}

}
