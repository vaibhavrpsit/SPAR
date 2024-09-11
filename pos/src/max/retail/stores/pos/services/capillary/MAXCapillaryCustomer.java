package max.retail.stores.pos.services.capillary;

import java.util.ArrayList;

import oracle.retail.stores.domain.utility.EYSDate;

public class MAXCapillaryCustomer
{
  
	private String customerId;
	private String mobile;
	private String firstName;
	private String telephonePricvacy;
	private String mailPrivacy;
	private EYSDate birthdate;
  private String cardNumber;
  private String cardType;
  private String customerName;
  private String pointsAvailable;
  private String tier;
  private String pointsExpriringOn;
  private String coBranded;
  private ArrayList offers;
  private EYSDate lastVisit;
  private String lastVisit3months;
  private String lastVisit12months;
  private String pincode;
  private String gender;
  private String email;
  
  
  
public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public String getGender() {
	return gender;
}

public void setGender(String gender) {
	this.gender = gender;
}

public String getPinCode() {
	return pincode;
}

public void setPinCode(String pincode) {
	this.pincode = pincode;
}

public String getCustomerId() {
	return customerId;
}

public void setCustomerId(String customerId) {
	this.customerId = customerId;
}


public String getMobile() {
	return mobile;
}

public void setMobile(String mobile) {
	this.mobile = mobile;
}

public String getFirstName() {
	return firstName;
}

public void setFirstName(String firstName) {
	this.firstName = firstName;
}

public String getTelephonePricvacy() {
	return telephonePricvacy;
}

public void setTelephonePricvacy(String telephonePricvacy) {
	this.telephonePricvacy = telephonePricvacy;
}

public String getMailPrivacy() {
	return mailPrivacy;
}

public void setMailPrivacy(String mailPrivacy) {
	this.mailPrivacy = mailPrivacy;
}


public EYSDate getBirthdate() {
	return birthdate;
}

public void setBirthdate(EYSDate birthdate) {
	this.birthdate = birthdate;
}

public String getCardNumber() {
	return cardNumber;
}

public void setCardNumber(String cardNumber) {
	this.cardNumber = cardNumber;
}

public String getCardType() {
	return cardType;
}

public void setCardType(String cardType) {
	this.cardType = cardType;
}

public String getCustomerName() {
	return customerName;
}

public void setCustomerName(String customerName) {
	this.customerName = customerName;
}

public String getPointsAvailable() {
	return pointsAvailable;
}

public void setPointsAvailable(String pointsAvailable) {
	this.pointsAvailable = pointsAvailable;
}

public String getTier() {
	return tier;
}

public void setTier(String tier) {
	this.tier = tier;
}

public String getPointsExpriringOn() {
	return pointsExpriringOn;
}

public void setPointsExpriringOn(String pointsExpriringOn) {
	this.pointsExpriringOn = pointsExpriringOn;
}

public String getCoBranded() {
	return coBranded;
}

public void setCoBranded(String coBranded) {
	this.coBranded = coBranded;
}

public ArrayList getOffers() {
	return offers;
}

public void setOffers(ArrayList offers) {
	this.offers = offers;
}

public EYSDate getLastVisit() {
	return lastVisit;
}

public void setLastVisit(EYSDate lastVisit) {
	this.lastVisit = lastVisit;
}

public String getLastVisit3months() {
	return lastVisit3months;
}

public void setLastVisit3months(String lastVisit3months) {
	this.lastVisit3months = lastVisit3months;
}

public String getLastVisit12months() {
	return lastVisit12months;
}

public void setLastVisit12months(String lastVisit12months) {
	this.lastVisit12months = lastVisit12months;
}

}