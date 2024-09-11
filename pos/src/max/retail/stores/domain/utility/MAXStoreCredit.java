/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 2.1		May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *  Rev 2.0     May 15,2023             Kumar Vaibhav       CN lock
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Initial Changes for StoreCredit FES
 *
 ********************************************************************************/

package max.retail.stores.domain.utility;

import oracle.retail.stores.domain.utility.StoreCredit;

public class MAXStoreCredit extends StoreCredit implements
MAXStoreCreditIfc{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7706108638935874053L;
	//Rev 2.0 start
	private Boolean storeCreditLock;
	//Rev 2.0 end

	//changes Starts rev 1.0 (Ashish : Store Credit)
	public String redeemTransactionID;
	public String status;
	
	/**
	 * @return the redeemTransactionID
	 */
	public String getRedeemTransactionID() {
		return redeemTransactionID;
	}

	/**
	 * @param redeemTransactionID the redeemTransactionID to set
	 */
	public void setRedeemTransactionID(String redeemTransactionID) {
		this.redeemTransactionID = redeemTransactionID;
	}
	//changes ends rev 1.0 (Ashish : Store Credit)
	private int storeCreditType;

	public int getStoreCreditType() {
		return storeCreditType;
	}

	public void setStoreCreditType(int storeCreditType) {
		this.storeCreditType = storeCreditType;
	}

	// MAX Changes Rev 1.0 Ends
	/*
	 * priyanka code start for issue Bug 5879 
	 * Return receipt change for Exchnage slip and contact number 
	 */
	 protected String phoneNumber = "";
	 public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	protected String postalCode = "";
	

	
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	//priyanka code end
	public Object clone() { // begin clone()
		MAXStoreCreditIfc tsc = new MAXStoreCredit();

		// set attributes in clone
		setCloneAttributes(tsc);

		return tsc;
	} // end clone()

	// ----------------------------------------------------------------------------
	/**
	 * Sets attributes in clone of this object.
	 * <P>
	 * 
	 * @param newClass
	 *            new instance of object
	 **/
	// ----------------------------------------------------------------------------
	public void setCloneAttributes(MAXStoreCreditIfc newClass) { // begin
																	// setCloneAttributes()
		super.setCloneAttributes((StoreCredit) newClass);
		newClass.setStoreCreditType(storeCreditType);
		newClass.setStatus(status);
		newClass.setAmount(amount);
		/*newClass.setPostalCode(postalCode);
		newClass.setPhoneNumber(phoneNumber);*/
	}

	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = false;
		MAXStoreCreditIfc c = null;
		if (obj instanceof StoreCredit) {
			c = (MAXStoreCredit) obj; // downcast the input object
			// compare all the attributes of TenderCash
			if (super.equals(obj)) {
				isEqual = true; // set the return code to true
			}
		}
		return (isEqual);
	} // end equals()

	//changes Starts rev 1.0 (Ashish : Store Credit)
	@Override
	public String getStatus() {
		return status;
	}
	@Override
	public void setStatus(String status) {
		this.status = status;
		
	}
	//changes ends rev 1.0 (Ashish : Store Credit)
	//Rev 2.0 start
	public Boolean isStoreCreditLock() {
		return storeCreditLock;
	}

	public void setStoreCreditLock(Boolean storeCreditLock) {
		this.storeCreditLock = storeCreditLock;
	}
//Rev 2.0 end
	
	// chnages for Rev 2.1 starts
		protected String mobileNumber = "";
		 public String getSCmobileNumber() {
			return mobileNumber;
		}

		public void setSCmobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}
		// chnages for Rev 2.1 ends
}
