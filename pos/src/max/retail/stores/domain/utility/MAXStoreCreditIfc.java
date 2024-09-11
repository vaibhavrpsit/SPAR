/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *	
 *	Rev 1.1		May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Initial Changes for StoreCredit FES
 *
 ********************************************************************************/
package max.retail.stores.domain.utility;

import oracle.retail.stores.domain.utility.StoreCreditIfc;

public interface MAXStoreCreditIfc extends StoreCreditIfc {

	public static String revisionNumber = "$Revision: 7$";


	// MAX Changes Rev 1.0 Starts
	public int getStoreCreditType();

	public void setStoreCreditType(int storeCrediType);
	public String getStatus();

	public void setStatus(String status);
	// MAX Changes Rev 1.0 Ends
	
	public Boolean isStoreCreditLock();
	public void setStoreCreditLock(Boolean storeCreditLock);
	
	//Change for Rev 1.1 :Starts
		public String getSCmobileNumber();
		public void setSCmobileNumber(String mobileNumber);
	//Change for Rev 1.1 :Ends
}
