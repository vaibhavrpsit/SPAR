/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *	Rev 2.1 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *  Rev 2.0     May 15, 2023            Kumar Vaibhav      	CN lock
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Changes for StoreCredit FES
 *
 ********************************************************************************/
package max.retail.stores.domain.tender;

import max.retail.stores.domain.utility.MAXStoreCredit;
import oracle.retail.stores.domain.tender.TenderStoreCredit;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.StoreCredit;
import oracle.retail.stores.foundation.manager.data.DataException;

//-------------------------------------------------------------------------
/**
 * @author Himanshu
 * 
 **/
// -------------------------------------------------------------------------
public class MAXTenderStoreCredit extends TenderStoreCredit implements MAXTenderStoreCreditIfc {

	private static final long serialVersionUID = 1L;

	protected boolean strCrdtValidated = true;

	protected EYSDate businessDate;

	protected String authorizationCode;
	
	private Boolean storeCreditLock;

	
	// Changes start for Rev 1.0 (Ashish : Storecredit)
	private String storeCreditStatus ="";
	private DataException dataException =null;
	// Changes start for Rev 1.0 (Ashish : Storecredit)
	public Object clone() {
		MAXTenderStoreCredit tsc = new MAXTenderStoreCredit();
		// set attributes in clone
		setCloneAttributes(tsc);

		return tsc;
	}

	protected void setCloneAttributes(MAXTenderStoreCredit newClass) {
		super.setCloneAttributes(newClass);

		newClass.setStrCrdtValidated(strCrdtValidated);
		newClass.setBusinessDate(businessDate);
		newClass.setStoreCreditStatus(storeCreditStatus);
		newClass.setRedeemTransactionID(redeemTransactionID);
	}

	public boolean isStrCrdtValidated() {
		return strCrdtValidated;
	}

	public void setStrCrdtValidated(boolean strCrdtValidated) {
		this.strCrdtValidated = strCrdtValidated;
	}

	public EYSDate getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(EYSDate businessDate) {
		this.businessDate = businessDate;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	
	// Change start for rev 1.0 (Ashish : Store Credit)
	public String getStoreCreditStatus() {
		return storeCreditStatus;
	}
	public void setStoreCreditStatus(String storeCreditStatus) {
		this.storeCreditStatus = storeCreditStatus;
		((MAXStoreCredit)storeCredit).setStatus(storeCreditStatus);
	}
	@Override
	public void setRedeemTransactionID(String value) {
		((MAXStoreCredit)storeCredit).redeemTransactionID=value;
	}
	
	@Override
	public String getRedeemTransactionID() {
		return ((MAXStoreCredit)storeCredit).redeemTransactionID;
	}
	

	@Override
	public DataException getError() {
		// TODO Auto-generated method stub
		return dataException;
	}

	@Override
	public void setError(DataException dataException) {
		this.dataException = dataException;
		
	}
	@Override
	public String getStatus() {
		return ((MAXStoreCredit)storeCredit).getStatus();
	}
	// Change ends for rev 1.0 (Ashish : Store Credit)

	@Override
	public boolean isManagerOverride() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setManagerOverride(boolean value) {
		// TODO Auto-generated method stub
		
	}
	// //Added by Vaibhav LS Credit note code merging start Rev 2.0
			public Boolean isStoreCreditLock() {
				return storeCreditLock;
			}

			public void setStoreCreditLock(Boolean storeCreditLock) {
				this.storeCreditLock = storeCreditLock;
			}
		// end Rev 2.0
	
			//Rev 2.1 Starts
			protected String mobileNumber = "";
			 public String getSCmobileNumber() {
				return mobileNumber;
			}

			public void setSCmobileNumber(String mobileNumber) {
				this.mobileNumber = mobileNumber;
			}
			
				public String getSCmobileNumber4digit() {
				// TODO Auto-generated method stub
			
			MAXStoreCredit storeCredit = (MAXStoreCredit) getStoreCredit();
				String mob = storeCredit.getSCmobileNumber();
				String SCmoblats4digits = null;
				String temp = "******";
				SCmoblats4digits=  mob.substring(6, 10);
				SCmoblats4digits= temp.concat(SCmoblats4digits);
			    return SCmoblats4digits;
			}
			//Rev 2.1 ends
}
