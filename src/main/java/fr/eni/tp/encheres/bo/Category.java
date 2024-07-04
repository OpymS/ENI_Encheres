package fr.eni.tp.encheres.bo;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Size;

public class Category {
	private int categoryId;
	@Size(max = 30)
	private String label;
	private List<Article> articles;

	public Category() {
		articles = new ArrayList<Article>();
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
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
