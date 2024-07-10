package fr.eni.tp.encheres.bo.dto;

import java.util.HashMap;
import java.util.Map;

import fr.eni.tp.encheres.bo.Category;

public class SearchCriteria {
	private String wordToFind;
	private String radioButton;
	private Category category;
	private Map<String,Boolean> filters;
	
	public SearchCriteria() {
		filters = new HashMap<String, Boolean>();
	}

	public String getWordToFind() {
		return wordToFind;
	}

	public void setWordToFind(String wordTofind) {
		this.wordToFind = wordTofind;
	}

	public String getRadioButton() {
		return radioButton;
	}

	public void setRadioButton(String radioButton) {
		this.radioButton = radioButton;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	public Map<String, Boolean> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Boolean> filters) {
		this.filters = filters;
	}

	@Override
	public String toString() {
		return String.format("SearchCriteria [wordToFind=%s, radioButton=%s, category=%s, filters=%s]", wordToFind,
				radioButton, category, filters);
	}
	
}
