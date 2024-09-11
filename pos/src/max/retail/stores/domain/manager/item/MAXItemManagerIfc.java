/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	04-May-2017		Kritica.Agarwal		GST Changes	
*
********************************************************************************/

package max.retail.stores.domain.manager.item;

import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

public abstract interface MAXItemManagerIfc extends ItemManagerIfc
{
  public static final String TYPE = "ItemManager";


  public abstract PLUItemIfc getPluItem(MAXSearchCriteriaIfc paramItemSearchCriteriaIfc)
    throws DataException;
}
