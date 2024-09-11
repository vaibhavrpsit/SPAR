/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.manager.centralvalidation;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

//-------------------------------------------------------------------------
/**
 * MAX Customizations Interface for MAXCentralizedDataEntry class.
 * <P>
 * 
 * 
 * @author Himanshu
 **/
// -------------------------------------------------------------------------
public interface MAXCentralizedDataEntryIfc extends EYSDomainIfc { // begin
																	// interface
																	// MGCentralizedDataEntryIfc
	/**
	 * revision number supplied by source-code-control system
	 **/
	public static String revisionNumber = "$Revision: 5$";

	public static final int USE_COUPON = 1;

	public static final int USE_STORE_CREDIT = 2;

	public String getTransactionID();

	public void setTransactionID(String value);

	public EYSDate getBusinessDate();

	public void setBusinessDate(EYSDate value);

	public String getWorkstationId();

	public void setWorkstationId(String workstationId);

	public String getStoreId();

	public void setStoreId(String storeId);

	public String getBatchID();

	public void setBatchID(String batchID);

	public boolean isValidationFailed();

	public void setValidationFailed(boolean validationStatus);

	public String getStrCrdtNumber();

	public void setStrCrdtNumber(String strCrdtNumber);

	public String getStrCrdtAmt();

	public void setStrCrdtAmt(String strCrdtAmt);

	public boolean isTrainingMode();

	public void setTrainingMode(boolean trainingMode);

	public int getModuleId();

	public void setModuleId(int moduleId);

} // end interface MGCentralizedDataEntryIfc
