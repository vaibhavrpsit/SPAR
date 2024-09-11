/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.manager.centralvalidation;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
 * MAX Customizations This class identifies a coupon or store credit entry in a
 * coupon or store credit batch respectively.
 * 
 * @author Himanshu
 **/
// -------------------------------------------------------------------------
public class MAXCentralizedDataEntry implements MAXCentralizedDataEntryIfc { // begin
																				// class
																				// MGCentralizedDataEntry

	static final long serialVersionUID = 4291075973083631617L;

	public static final String NO_BATCH_IDENTIFIED = "-1";

	/**
	 * revision number supplied by source-code-control system
	 **/
	public static String revisionNumber = "$Revision: 5$";

	protected String transactionID = null;

	protected EYSDate businessDate = null;

	protected String workstationId = null;

	protected String storeId = null;

	protected String batchID = NO_BATCH_IDENTIFIED;

	protected boolean validationFailed = false;

	// Changes for Rev 1.1 - Start
	protected String strCrdtNumber;

	protected String strCrdtAmt;

	/**
	 * training mode flag
	 **/
	protected boolean trainingMode = false;

	protected int moduleId;
	// Changes for Rev 1.1 - End

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Constructs a MGCentralizedDataEntry object Added by
	 * Himanshu
	 **/
	// ---------------------------------------------------------------------
	public MAXCentralizedDataEntry() {
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Creates clone of this object. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public Object clone() {
		// instantiate new object
		MAXCentralizedDataEntry c = new MAXCentralizedDataEntry();

		// set values
		setCloneAttributes(c);

		// pass back Object
		return ((Object) c);
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets attributes in clone of this object. Added by
	 * Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setCloneAttributes(MAXCentralizedDataEntry newClass) {
		if (businessDate != null) {
			newClass.setBusinessDate((EYSDate) getBusinessDate().clone());
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Determine if two objects are identical. Added by
	 * Himanshu
	 **/
	// ---------------------------------------------------------------------
	public boolean equals(Object obj) {
		boolean isEqual = true;
		// confirm object instanceof this object
		if (obj instanceof MAXCentralizedDataEntry) {
			MAXCentralizedDataEntry c = (MAXCentralizedDataEntry) obj;

			// compare all the attributes of MGCentralizedDataEntry
			if (Util.isObjectEqual(getTransactionID(), c.getTransactionID())
					&& Util.isObjectEqual(getBusinessDate(), c.getBusinessDate())) {
				isEqual = true;
			} else {
				isEqual = false;
			}
		} else {
			isEqual = false;
		}
		return (isEqual);
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Retrieves transaction identifier. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String getTransactionID() {
		return (transactionID);
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets transaction identifier. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setTransactionID(String value) {
		transactionID = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Retrieves business date. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public EYSDate getBusinessDate() {
		return (businessDate);
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets business date. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setBusinessDate(EYSDate value) {
		businessDate = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets workstationId. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String getWorkstationId() {
		return workstationId;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets workstationId. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setWorkstationId(String workstationId) {
		this.workstationId = workstationId;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets storeId. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String getStoreId() {
		return storeId;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets storeId. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets batchID. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String getBatchID() {
		return batchID;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets batchID. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets validationFailed. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public boolean isValidationFailed() {
		return validationFailed;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets validationFailed. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setValidationFailed(boolean validationFailed) {
		this.validationFailed = validationFailed;
	}

	// Changes for Rev 1.1 - Start
	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets strCrdtNumber. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String getStrCrdtNumber() {
		return strCrdtNumber;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets strCrdtNumber. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setStrCrdtNumber(String strCrdtNumber) {
		this.strCrdtNumber = strCrdtNumber;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets strCrdtAmt. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String getStrCrdtAmt() {
		return strCrdtAmt;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets strCrdtAmt. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setStrCrdtAmt(String strCrdtAmt) {
		this.strCrdtAmt = strCrdtAmt;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets trainingMode. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public boolean isTrainingMode() {
		return trainingMode;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets trainingMode. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setTrainingMode(boolean trainingMode) {
		this.trainingMode = trainingMode;
	}
	// Changes for Rev 1.1 - End

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Gets moduleId. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public int getModuleId() {
		return moduleId;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Sets moduleId. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Returns default display string. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		// build result string
		StringBuilder strResult = Util.classToStringHeader("CouponDataEntry", getRevisionNumber(), hashCode());

		// add attributes to string
		if (getTransactionID() == null) {
			strResult.append("transactionID:                      [null]").append(Util.EOL);
		} else {
			strResult.append(getTransactionID());
		}
		if (getBusinessDate() == null) {
			strResult.append("businessDate:                       [null]").append(Util.EOL);
		} else {
			strResult.append("businessDate:                       ").append("[").append(getBusinessDate()).append("]")
					.append(Util.EOL);
		}

		// pass back result
		return (strResult.toString());
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations Retrieves the source-code-control system revision
	 * number. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		// return string
		return (revisionNumber);
	}

	// ---------------------------------------------------------------------
	/**
	 * MAX Customizations MGCentralizedDataEntry main method. Added by Himanshu
	 **/
	// ---------------------------------------------------------------------
	public static void main(String args[]) {
		// instantiate class
		MAXCentralizedDataEntry c = new MAXCentralizedDataEntry();
		// output toString()
		System.out.println(c.toString());
	}

} // end class MGCentralizedDataEntry
