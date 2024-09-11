/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev 1.1   May 04, 2017	        Kritica Agarwal  GST Changes
*
********************************************************************************/
package max.retail.stores.domain.stock;

import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;

public abstract interface MAXItemSearchCriteriaIfc extends ItemSearchCriteriaIfc
{
	public  Boolean getInterStateDelivery();
	public  void setInterStateDelivery(Boolean interStateDelivery) ;
	public String getFromRegion();
	public void setFromRegion(String fromRegion);
	public String getToRegion();
	public void setToRegion(String toRegion);
}
