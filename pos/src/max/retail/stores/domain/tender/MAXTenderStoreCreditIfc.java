/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *	Rev 2.1		May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *  Rev 2.0     May 15, 2023            Kumar Vaibhav       CN lock
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Changes for StoreCredit FES
 *
 ********************************************************************************/

package max.retail.stores.domain.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;

public interface MAXTenderStoreCreditIfc extends TenderStoreCreditIfc {
	public boolean isStrCrdtValidated();

	public void setStrCrdtValidated(boolean strCrdtValidated);

	public EYSDate getBusinessDate();

	public void setBusinessDate(EYSDate businessDate);

	public String getAuthorizationCode();

	public void setAuthorizationCode(String authorizationCode);
	// Changes start for Rev 1.0 (Ashish : Store Credit)
	public String getStoreCreditStatus() ;
	public void setStoreCreditStatus(String storeCreditStatus);
	
	public void setRedeemTransactionID(String value);
	
	public String getRedeemTransactionID();
	public DataException getError() ;
	public void setError(DataException de1);
	public String getStatus();
	// Changes end for Rev 1.0 (Ashish : Store Credit)
	//Changes start for manageroverride
	public boolean isManagerOverride();
	public void setManagerOverride(boolean value);
	//Changes end for manageroverride
	//Rev 2.0 start
	public Boolean isStoreCreditLock();
	public void setStoreCreditLock(Boolean storeCreditLock);
	//Rev 2.0 end
	
	//Rev 2.1 start
	public String getSCmobileNumber();
	public String getSCmobileNumber4digit();
	public void setSCmobileNumber(String mobileNumber);
	//Rev 2.1 end
}
