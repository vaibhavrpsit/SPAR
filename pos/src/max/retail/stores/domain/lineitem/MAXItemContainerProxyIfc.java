/********************************************************************************
*   
*	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*
*	
*	Rev	1.0 	Nov 7, 2016		Mansi Goel			Changes for Discount Rule FES
*
********************************************************************************/

package max.retail.stores.domain.lineitem;

import java.math.BigDecimal;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;

public interface MAXItemContainerProxyIfc extends ItemContainerProxyIfc {

	public boolean itemsSellingPriceExceedsMRP();
	
	public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty, boolean isApplyBestDeal);

	public boolean isTaxApplied();

	public void setTaxApplied(boolean taxApplied);

	//public void replaceLineItemWithoutBestDeal(AbstractTransactionLineItemIfc lineItem, int index);
}
