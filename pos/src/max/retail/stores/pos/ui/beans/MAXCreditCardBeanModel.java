/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.0  28/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCreditCardBeanModel extends POSBaseBeanModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1257538878391200137L;

	public static String revisionNumber = "$Revision: 1.1 $";

	protected String cardNumber = "";
	protected String expirationDate = "";
	protected boolean cardSwiped = false;

	protected String bankName  = "";
	protected String authCode = "";

	protected String bankCode = "";
	protected String bankDesc = "";
	protected int selectedBank = 0;
	protected String selectedBankName = "";

	/** Flag to indicate whether to allow editing of card number/expiration date **/
	protected boolean editable = true; // not debit screen

	/** format used to display  date **/
	protected String format= "";

	protected String[] bankDes;


	//---------------------------------------------------------------------------
	/**
    Get the value of the CreditCard field
    @return the value of CreditCard
	 **/
	//---------------------------------------------------------------------------
	public String getCardNumber()
	{
		return cardNumber;
	}

	//---------------------------------------------------------------------------
	/**
    Get the value of the ExpirationDate field
    @return the value of ExpirationDate
	 **/
	//---------------------------------------------------------------------------
	public String getExpirationDate()
	{
		return expirationDate;
	}
	//---------------------------------------------------------------------------
	/**
    Get the dateFormat for the expiration date field
    @return the date format
	 **/
	//---------------------------------------------------------------------------
	public String getDateFormat()
	{
		return format;
	}

	//---------------------------------------------------------------------------
	/**
    Set the format of the ExpirationDate field
    @return the format of ExpirationDate
	 **/
	//---------------------------------------------------------------------------
	public void setDateFormat(String value)
	{
		format = value;
	}
	//---------------------------------------------------------------------------
	/**
    Get the value of the CardSwiped field
    @return the value of CardSwiped
	 **/
	//---------------------------------------------------------------------------
	public boolean isCardSwiped()
	{
		return cardSwiped;
	}

	//---------------------------------------------------------------------------
	/**
    Sets the Card number field
    @param number value to be set for cardNumber
	 **/
	//---------------------------------------------------------------------------
	public void setCardNumber(String number)
	{
		cardNumber = number;
	}

	//---------------------------------------------------------------------------
	/**
    Sets the ExpirationDate field
    @param date value to be set for ExpirationDate
	 **/
	//---------------------------------------------------------------------------
	public void setExpirationDate(String date)
	{
		expirationDate = date;
	}

	//---------------------------------------------------------------------------
	/**
    Sets the CardSwiped field
    @param swiped value to be set for CardSwiped
	 **/
	//---------------------------------------------------------------------------
	public void setCardSwiped(boolean swiped)
	{
		cardSwiped = swiped;
	}

	//---------------------------------------------------------------------
	/**
   Gets the editable flag.
   @return the editable flag value
	 **/
	//---------------------------------------------------------------------
	public boolean getEditable()
	{
		return editable;
	}

	//---------------------------------------------------------------------
	/**
   Sets the editable flag.
   @param  boolean  true - can edit, false - cannot edit
	 **/
	//---------------------------------------------------------------------
	public void setEditable(boolean value)
	{
		editable = value;
	}



	//---------------------------------------------------------------------------
	/**
    Converts to a string representing the data in this Object
    @returns string representing the data in this Object
	 **/
	//---------------------------------------------------------------------------
	public String toString()
	{
		StringBuffer buff = new StringBuffer();

		buff.append("Class: CreditCardBeanModel Revision: "
				+ revisionNumber
				+ "\n");
		buff.append("CardNumber     [" + cardNumber + "]\n");
		buff.append("ExpirationDate [" + expirationDate + "]\n");
		buff.append("CardSwiped     [" + cardSwiped + "]\n");

		return(buff.toString());
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankDesc() {
		return bankDesc;
	}

	public void setBankDesc(String bankDesc) {
		this.bankDesc = bankDesc;
	}

	public String[] getBankDes()
	{
		return bankDes;
	}

	public void setBankDes(String[] bankDes)
	{
		this.bankDes = bankDes;
	}


	public int getSelectedBank() {
		return selectedBank;
	}

	public void setSelectedBank(int selectedBank) {
		this.selectedBank = selectedBank;
	}
	
	public String getSelectedBankName() {
		return selectedBankName;
	}

	public void setSelectedBankName(String selectedBank) {
		this.selectedBankName = selectedBank;
	}


}
