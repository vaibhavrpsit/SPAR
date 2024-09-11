/********************************************************************************
 *   
 *	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
 *	
 *	Rev	1.0 	18-Aug-2020		Mohan.Yadav		<Comments>	
 *
 ********************************************************************************/
package max.retail.stores.gstinCentralJob.gstin;

import java.io.Serializable;

public class InvoiceResp implements Serializable {

	private static final long serialVersionUID = 1L;
	private String storeID=null;
	private String regID=null;
	private String businessDate=null;
	private String txnID=null;	
	private String status = null;
	private String error = null;
	public String getStoreID() {
		return storeID;
	}
	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}
	public String getRegID() {
		return regID;
	}
	public void setRegID(String regID) {
		this.regID = regID;
	}
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public String getTxnID() {
		return txnID;
	}
	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}


}
