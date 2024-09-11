/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.1  22/MAy/2013	Jyoti Rawal, Changes for Credit Card FES
*  Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.utility.EYSDate;

public interface MAXTenderChargeIfc extends TenderChargeIfc { // begin interface
																// TenderChargeIfc

	/**
	 * revision number supplied by source-code control system
	 **/
	public static final String revisionNumber = "$Revision: 4$";

	/**
	 * Rev 1.1 Credit Card FES start
	 */
	public abstract String getInvoiceNumber();

	public abstract void setInvoiceNumber(String s);

	public abstract String getTransactionAcquirer();

	public abstract void setTransactionAcquirer(String s);

	public abstract String getAcquiringBankCode();

	public abstract void setAcquiringBankCode(String s);

	public abstract String getAuthRemarks();

	public abstract void setAuthRemarks(String s);

	public abstract String getBatchNumber();

	public abstract void setBatchNumber(String s);

	public abstract String getRetrievalRefNumber();

	public abstract void setRetrievalRefNumber(String s);

	public abstract String getMerchID();

	public abstract void setMerchID(String s);

	// getter and setter of transaction type of credit tender.
	public void setTransactionType(String transactionType);

	public String getTransactionType();

	public abstract String getTID();

	public abstract void setTID(String tId);

	public String getLastFourDigits();

	public void setLastFourDigits(String bankId);

	/**
	 * Rev 1.1 Credit Card FES ends
	 */
	public String getRrnnumber();
	public void setRrnnumber(String rrnnumber);
	
	public String getQcType();
	public void setQcType(String qcType);
	public EYSDate getQcExpiryDate();
	public void setQcExpiryDate(EYSDate qcExpiryDate) ;
	public String getQcTransId();
	public void setQcTransId(String qcTransId);
	 //added by atul shukla for paytm tender
	
	/**
	 * @return the authCode
	 */
	public String getAuthCode();

	/**
	 * @param authCode
	 *            the authCode to set
	 */
	public void setAuthCode(String authCode);
	/**
	 * @return the bankCode
	 */
	public String getBankCode();

	/**
	 * @param bankCode
	 *            the bankCode to set
	 */
	public void setBankCode(String bankCode);

	public String getBankName();
	public void setBankName(String bankName);
	public String getAquirerStatus();
	public void setAquirerStatus(String aquirerStatus);
	public boolean isEmiTransaction();
	public void setEmiTransaction(boolean emiTransaction);
	public HashMap getResponseDate();

	public void setResponseDate(HashMap responseDate);
	
	
	
	//public String getPaytmPhoneNumber();

	
	//public String getPaytmTotp();
	
	//changes for paytmqr
	public String getMerchantTransactionId();
	public void setMerchantTransactionId(String merchantTransactionId);
	
	 public String getOrderNumber();
	 public void setOrderNumber(String orderNumber);
	 
	 public String getRrnNumber();
   	 public void setRrnNumber(String rrnNumber);
   	 
   	public String getPaytmUPIorWalletPaytment();
	public void setPaytmUPIorWalletPaytment(String paytmUPIorWalletPaytment);
}
