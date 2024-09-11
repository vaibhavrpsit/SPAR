/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *  Rev 1.2		05th May 2020		Karni Singh		POS REQ: Register CRM customer with OTP.
 *	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.main;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

public class MAXCustomerMainCargo extends CustomerMainCargo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 242140079670860271L;
	public boolean isTICCustomerLookup = false;
	protected boolean ticCustomerPhoneNoFlag;
	
	protected PhoneIfc ticCustomerPhoneNo;
	protected MAXTICCustomerIfc ticcustomer = null;
	private StoreStatusIfc storeStatus;	
	private SaleReturnTransactionIfc transaction;	
	private boolean ticCustomerrequire=true;
	
	//Rev 1.2 start
	private boolean isCRMEnrolmentOTPValidated=false;
	private String crmEnrolmentOTP = null;
	private int otpRetries = 0;	
	//Rev 1.2 end

	
    public MAXCustomerMainCargo()
    {
    	super();
    }

	public boolean isTICCustomerLookup() {
		return isTICCustomerLookup;
	}

	public void setTICCustomerLookup(boolean isTICCustomerLookup) {
		this.isTICCustomerLookup = isTICCustomerLookup;
	}
    
	 
	public MAXTICCustomerIfc getTICCustomer() {
		return ticcustomer;
	}

	public void setTICCustomer(MAXTICCustomerIfc ticCustomer) {
		this.ticcustomer=ticCustomer;
	}

	public void setStoreStatus(StoreStatusIfc storeStatus) {
		this.storeStatus = storeStatus;
	}

	public StoreStatusIfc getStoreStatus() {
		return storeStatus;
	}

	public void setTransaction(SaleReturnTransactionIfc transaction) {
		this.transaction = transaction;
	}

	public SaleReturnTransactionIfc getTransaction() {
		return transaction;
	}
	
	public void setTICCustomerRequire(boolean ticCustomerrequire) {
		this.ticCustomerrequire = ticCustomerrequire;
	}

	public boolean isTICCustomerRequire() {
		return ticCustomerrequire;
	}
	
	// Changes starts for code merging
	public boolean isTicCustomerPhoneNoFlag() {
		return ticCustomerPhoneNoFlag;
	}

	public void setTicCustomerPhoneNoFlag(boolean ticCustomerPhoneNoFlag) {
		this.ticCustomerPhoneNoFlag = ticCustomerPhoneNoFlag;
	}
	// Changes ends for code merging
	
	//Changes for Rev 1.0 : Starts
	public PhoneIfc getTicCustomerPhoneNo() {
		return ticCustomerPhoneNo;
	}

	public void setTicCustomerPhoneNo(PhoneIfc ticCustomerPhoneNo) {
		this.ticCustomerPhoneNo = ticCustomerPhoneNo;
	}
	//Changes for Rev 1.0 : Ends

		//Rev 1.2 start
	public boolean isCRMEnrolmentOTPValidated() {
		return isCRMEnrolmentOTPValidated;
	}

	public void setCRMEnrolmentOTPValidated(boolean isCRMEnrolmentOTPValidated) {
		this.isCRMEnrolmentOTPValidated = isCRMEnrolmentOTPValidated;
	}			

	public String getCrmEnrolmentOTP() {
		return crmEnrolmentOTP;
	}

	public void setCrmEnrolmentOTP(String crmEnrolmentOTP) {
		this.crmEnrolmentOTP = crmEnrolmentOTP;
	}


	public int getOtpRetries() {
		return otpRetries;
	}

	public void setOtpRetries(int otpRetries) {
		this.otpRetries = otpRetries;
	}		


	//Rev 1.2 end	
	}
