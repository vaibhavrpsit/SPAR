/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/transaction/MAXSearchCriteriaIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.transaction;

import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;

public interface MAXSearchCriteriaIfc extends SearchCriteriaIfc{
	public void setGiftCardNumber(String value);
	//change for Rev 1.2 : Starts
	public Boolean getInterStateDelivery();
	public void setInterStateDelivery(Boolean interStateDelivery) ;
	public String getFromRegion();
	public void setFromRegion(String fromRegion);
	public String getToRegion();
	public void setToRegion(String toRegion);
	//Change for Rev 1.2 : Ends
	public boolean getEmpID();
	public void setEmpID(boolean empID) ;
	

}
