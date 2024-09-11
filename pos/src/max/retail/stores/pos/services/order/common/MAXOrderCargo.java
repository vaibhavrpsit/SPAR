/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.common;

// Java imports
import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;

//------------------------------------------------------------------------------
/**
 * Carries data common to order services.
 * 
 * @version $Revision: 5$
 **/
// ------------------------------------------------------------------------------
public class MAXOrderCargo extends OrderCargo implements MAXOrderCargoIfc

{

	private static final long serialVersionUID = 574747080115057547L;

	protected PLUItemIfc pluItem;
	protected BigDecimal itemQuantity;
	protected String itemSerial;
	protected OrderLineItemIfc lineItem;
	
	public OrderLineItemIfc getLineItem() {
		return lineItem;
	}

	public void setLineItem(OrderLineItemIfc lineItem) {
		this.lineItem = lineItem;
	}

	public PLUItemIfc getPLUItem() {
		return pluItem;
	}

	public void setPLUItem(PLUItemIfc pluItem) {
		this.pluItem = pluItem;
	}

	public BigDecimal getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(BigDecimal itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public String getItemSerial() {
		return itemSerial;
	}

	public void setItemSerial(String itemSerial) {
		this.itemSerial = itemSerial;
	}

	public void setServiceType(int serviceType, Locale locale)
    {
        this.serviceType = serviceType;

        if(this.serviceType != MAXOrderCargoIfc.SERVICE_TYPE_NOT_SET)
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String serviceNameText =
                utility.retrieveText("Common",
                        BundleConstantsIfc.ORDER_BUNDLE_NAME,
                        MAXOrderCargoIfc.SERVICE_NAME_TAG_LIST[serviceType],
                        MAXOrderCargoIfc.SERVICE_NAME_TEXT_LIST[serviceType]);
            setServiceName(serviceNameText);
        }
        else
        {
            setServiceName(null);
        }
    }
}
