package fr.eni.tp.encheres.bo;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PickupLocation {
	@Size(max = 30)
	private String street;
	@Pattern(regexp = "^\\d{5}$")
	private String zipCode;
	@Size(max = 30)
	private String city;
	
	public PickupLocation() {
	}
	
	public PickupLocation(String street, String zipCode,  String city) {
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
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

	@Override
	public String toString() {
		return String.format("PickupLocation [street=%s, zipCode=%s, city=%s]", street, zipCode, city);
	}
	
	

}
