package fr.eni.tp.encheres.bo;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;

public class Auction {
	private LocalDateTime auctionDate;
	private String formatedDate;
	
	@Min(0)
	private int bidAmount;
	private User user;
	private Article article;

	public Auction() {
	}

	public Auction(LocalDateTime auctionDate, int bidAmount, User user, Article article) {
		this.auctionDate = auctionDate;
		this.bidAmount = bidAmount;
		this.user = user;
		this.article = article;
	}

	public LocalDateTime getAuctionDate() {
		return auctionDate;
	}

	public void setAuctionDate(LocalDateTime auctionDate) {
		this.auctionDate = auctionDate;
		
	}
	
	public String getFormatedDate() {
		return formatedDate;
	}

	public void setFormatedDate(String formatedDate) {
		this.formatedDate = formatedDate;
	}


	public int getBidAmount() {
		return bidAmount;
	}

	public void setBidAmount(int bidAmount) {
		this.bidAmount = bidAmount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	@Override
	public String toString() {
		return String.format("Auction [auctionDate=%s, bidAmount=%s, user=%s, article=%s]", auctionDate, bidAmount,
				user, article);
	}

	

}
