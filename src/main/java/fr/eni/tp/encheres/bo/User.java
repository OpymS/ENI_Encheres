package fr.eni.tp.encheres.bo;

import java.util.ArrayList;
import java.util.List;

public class User {
	private long idUser;
	private String pseudo;
	private String name;
	private String firstName;
	private String email;
	private String phoneNumber;
	private String street;
	private String zipCode;
	private String city;
	private String password;
	private int credit;
	private boolean admin;
	
	private List<Article> purchases;
	private List<Article> sales;
	private List<Auction> auctions;
	
	public User() {
		purchases = new ArrayList<Article>();
		sales = new ArrayList<Article>();
		auctions = new ArrayList<Auction>();
	}

	public long getIdUser() {
		return idUser;
	}

	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<Article> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<Article> purchases) {
		this.purchases = purchases;
	}

	public List<Article> getSales() {
		return sales;
	}

	public void setSales(List<Article> sales) {
		this.sales = sales;
	}

	public List<Auction> getAuction() {
		return auctions;
	}

	public void setAuction(List<Auction> auction) {
		this.auctions = auction;
	}

	@Override
	public String toString() {
		return String.format(
				"User [idUser=%s, pseudo=%s, name=%s, firstName=%s, email=%s, phoneNumber=%s, street=%s, zipCode=%s, city=%s, password=%s, credit=%s, admin=%s, purchases=%s, sales=%s, auctions=%s]",
				idUser, pseudo, name, firstName, email, phoneNumber, street, zipCode, city, password, credit, admin,
				purchases, sales, auctions);
	}
	
}
