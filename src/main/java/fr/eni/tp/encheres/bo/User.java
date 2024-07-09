package fr.eni.tp.encheres.bo;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class User {
	private int userId;
	@NotBlank
	@Pattern(regexp="^\\w+$")
	@Size(max = 30)
	private String pseudo;
	@NotBlank
	@Size(max = 30)
	private String name;
	@Size(max = 30)
	private String firstName;
	@NotBlank
	@Email
	private String email;
	@Pattern(regexp="^(0|\\+33|0033)[1-9]\\d{8}$")
	private String phoneNumber;
	@Size(max = 30)
	private String street;
	@Pattern(regexp="^\\d{5}$")
	private String zipCode;
	@Size(max = 30)
	private String city;
	private String password;
	private String passwordConfirm;
	private int credit;
	private boolean admin;
	
	private boolean activated;
	
	
	private List<Article> purchases;
	private List<Article> sales;
	private List<Auction> auctions;
	
	public User() {
		this.activated = true;
		purchases = new ArrayList<Article>();
		sales = new ArrayList<Article>();
		auctions = new ArrayList<Auction>();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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
	
	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
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
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
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
				"User [userId=%s, pseudo=%s, name=%s, firstName=%s, email=%s, phoneNumber=%s, street=%s, zipCode=%s, city=%s, credit=%s, admin=%s, purchases=%s, sales=%s, auctions=%s%n, activated=%s]",
				userId, pseudo, name, firstName, email, phoneNumber, street, zipCode, city, credit, admin,
				purchases, sales, auctions, activated);
	}

	
}
