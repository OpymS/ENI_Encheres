package fr.eni.tp.encheres.bo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Article {
	private long idArticle;
	private String articleName;
	private String description;
	private LocalDate auctionStartDate;
	private LocalDate auctionEndDate;
	private int beginningPrice;
	private int finalPrice;
	private String state;
	private Category category;
	private PickupLocation pickupLocation;
	private List<Auction> bids;

	public Article() {
		bids = new ArrayList<Auction>();
	}

	public long getIdArticle() {
		return idArticle;
	}

	public void setIdArticle(long idArticle) {
		this.idArticle = idArticle;
	}

	public String getArticleName() {
		return articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getAuctionStartDate() {
		return auctionStartDate;
	}

	public void setAuctionStartDate(LocalDate auctionStartDate) {
		this.auctionStartDate = auctionStartDate;
	}

	public LocalDate getAuctionEndDate() {
		return auctionEndDate;
	}

	public void setAuctionEndDate(LocalDate auctionEndDate) {
		this.auctionEndDate = auctionEndDate;
	}

	public int getBeginningPrice() {
		return beginningPrice;
	}

	public void setBeginningPrice(int beginningPrice) {
		this.beginningPrice = beginningPrice;
	}

	public int getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(int finalPrice) {
		this.finalPrice = finalPrice;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<Auction> getBids() {
		return bids;
	}

	public void setBids(List<Auction> auctions) {
		this.bids = auctions;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public PickupLocation getPickupLocation() {
		return pickupLocation;
	}

	public void setPickupLocation(PickupLocation pickupLocation) {
		this.pickupLocation = pickupLocation;
	}

	@Override
	public String toString() {
		return String.format(
				"Article [idArticle=%s, articleName=%s, description=%s, auctionStartDate=%s, auctionEndDate=%s, beginningPrice=%s, finalPrice=%s, state=%s, category=%s, pickupLocation=%s, bids=%s]",
				idArticle, articleName, description, auctionStartDate, auctionEndDate, beginningPrice, finalPrice,
				state, category, pickupLocation, bids);
	}

}
