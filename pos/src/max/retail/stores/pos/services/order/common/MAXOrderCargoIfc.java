/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.common;

// Java imports
import java.math.BigDecimal;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;

//------------------------------------------------------------------------------
/**
 * Interface to access Order data common to order services.
 * 
 * @version $Revision: 3$
 **/
// ------------------------------------------------------------------------------
public interface MAXOrderCargoIfc extends OrderCargoIfc {

	public static String revisionNumber = "$Revision: 3$";

	public static final String SERVICE_ALTER = "Alter";

	public static final int SERVICE_ALTER_TYPE = 5;

	public static final String SERVICE_ALTER_TAG = "ServiceAlter";

	static final String SERVICE_NAME_TAG_LIST[] = { SERVICE_FILL_TAG,
			SERVICE_PICKUP_TAG, SERVICE_CANCEL_TAG, SERVICE_VIEW_TAG,
			SERVICE_PRINT_TAG, SERVICE_ALTER_TAG };

	static final String SERVICE_NAME_TEXT_LIST[] = { SERVICE_FILL,
			SERVICE_PICKUP, SERVICE_CANCEL, SERVICE_VIEW, SERVICE_PRINT,
			SERVICE_ALTER };

	public PLUItemIfc getPLUItem();

	public void setPLUItem(PLUItemIfc pluItem);

	public BigDecimal getItemQuantity();

	public void setItemQuantity(BigDecimal itemQuantity);

	public String getItemSerial();

	public void setItemSerial(String itemSerial);
	
	public OrderLineItemIfc getLineItem();

	public void setLineItem(OrderLineItemIfc lineItem);
	
}