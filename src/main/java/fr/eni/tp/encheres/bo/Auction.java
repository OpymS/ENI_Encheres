package fr.eni.tp.encheres.bo;

import java.time.LocalDate;

public class Auction {
	private LocalDate auctionDate;
	private int bidAmount;
	
	public Auction(LocalDate auctionDate, int bidAmount) {
		this.auctionDate = auctionDate;
		this.bidAmount = bidAmount;
	}

	public LocalDate getAuctionDate() {
		return auctionDate;
	}

	public void setAuctionDate(LocalDate auctionDate) {
		this.auctionDate = auctionDate;
	}

	public int getBidAmount() {
		return bidAmount;
	}

	public void setBidAmount(int bidAmount) {
		this.bidAmount = bidAmount;
	}

	@Override
	public String toString() {
		return String.format("Auction [auctionDate=%s, bidAmount=%s]", auctionDate, bidAmount);
	}
	
}
