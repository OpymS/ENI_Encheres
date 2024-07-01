package fr.eni.tp.encheres.bo;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private long idCategory;
	private String label;
	private List<Article> articles;

	public Category() {
		articles = new ArrayList<Article>();
	}

	public long getIdCategory() {
		return idCategory;
	}

	public void setIdCategory(long idCategory) {
		this.idCategory = idCategory;
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
		return String.format("Category [idCategory=%s, label=%s, articles=%s]", idCategory, label, articles);
	}

}
