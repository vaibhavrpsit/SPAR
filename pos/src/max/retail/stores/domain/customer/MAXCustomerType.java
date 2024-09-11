/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	17-Aug-2018		Jyoti.Yadav		Quoting PAN CR
*
********************************************************************************/
package max.retail.stores.domain.customer;

import oracle.retail.stores.domain.utility.EYSDomainIfc;

public class MAXCustomerType implements EYSDomainIfc {
	private static final long serialVersionUID = -5608569665973885242L;
	protected String customerType;
	protected String dataAccepted;
	protected String PANnumber;
	protected String Form60UIN;
	protected String passportNumber;
	protected String visaNumber;
	protected String ITRAckNumber;

	public MAXCustomerType() {
		customerType = "";
		dataAccepted = "";
		PANnumber = "";
		Form60UIN = "";
		passportNumber = "";
		visaNumber = "";
		ITRAckNumber = "";
	}

	/**
	 * @return the customerType
	 */
	public String getCustomerType() {
		return customerType;
	}

	/**
	 * @param customerType
	 *            the customerType to set
	 */
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	/**
	 * @return the dataAccepted
	 */
	public String getDataAccepted() {
		return dataAccepted;
	}

	/**
	 * @param dataAccepted
	 *            the dataAccepted to set
	 */
	public void setDataAccepted(String dataAccepted) {
		this.dataAccepted = dataAccepted;
	}

	/**
	 * @return the pANnumber
	 */
	public String getPANnumber() {
		return PANnumber;
	}

	/**
	 * @param pANnumber
	 *            the pANnumber to set
	 */
	public void setPANnumber(String pANnumber) {
		PANnumber = pANnumber;
	}

	/**
	 * @return the form60UIN
	 */
	public String getForm60UIN() {
		return Form60UIN;
	}

	/**
	 * @param form60uin
	 *            the form60UIN to set
	 */
	public void setForm60UIN(String form60uin) {
		Form60UIN = form60uin;
	}

	/**
	 * @return the passportNumber
	 */
	public String getPassportNumber() {
		return passportNumber;
	}

	/**
	 * @param passportNumber
	 *            the passportNumber to set
	 */
	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	/**
	 * @return the visaNumber
	 */
	public String getVisaNumber() {
		return visaNumber;
	}

	/**
	 * @param visaNumber
	 *            the visaNumber to set
	 */
	public void setVisaNumber(String visaNumber) {
		this.visaNumber = visaNumber;
	}

	/**
	 * @return the iTRAckNumber
	 */
	public String getITRAckNumber() {
		return ITRAckNumber;
	}

	/**
	 * @param iTRAckNumber
	 *            the iTRAckNumber to set
	 */
	public void setITRAckNumber(String iTRAckNumber) {
		ITRAckNumber = iTRAckNumber;
	}

	public Object clone() {
		MAXCustomerType custType = new MAXCustomerType();
		setCloneAttributes(custType);
		return custType;
	}

	private void setCloneAttributes(MAXCustomerType custType) {
		custType.setCustomerType(customerType);
		custType.setDataAccepted(dataAccepted);
		custType.setPANnumber(PANnumber);
		custType.setForm60UIN(Form60UIN);
		custType.setPassportNumber(passportNumber);
		custType.setVisaNumber(visaNumber);
		custType.setITRAckNumber(ITRAckNumber);
	}
}
