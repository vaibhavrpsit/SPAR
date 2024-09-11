/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.pickup;

import max.retail.stores.domain.lineitem.MAXOrderLineItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

public class MaxCheckTransactionTotalAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -1437490022232562254L;

	public void traverse(BusIfc bus) {

		MAXPickupOrderCargo cargo = (MAXPickupOrderCargo) bus.getCargo();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);

		CurrencyIfc maxTotal = DomainGateway.getBaseCurrencyInstance();

		try {
			maxTotal = DomainGateway.getBaseCurrencyInstance(pm
					.getStringValue("ShippingChargeThresholdAmount"));
		} catch (ParameterException e) {
			logger.error("Cannot read parameter value", e);
		}

		/**
		 * Rev 1.1 changes start here		
		 */
		OrderIfc order = cargo.getOrder();
        AbstractTransactionLineItemIfc[] allItems = order.getLineItems();

        // Save a copy of the existing order in the cargo
        cargo.setSavedOrder((OrderIfc)order.clone());

        for(int i=0; i<allItems.length; i++)
        {
            int status = ((SaleReturnLineItemIfc)allItems[i]).getOrderItemStatus().getStatus().getStatus();
            if (status == MAXOrderLineItemIfc.ORDER_ITEM_STATUS_CANCELED)
            {
            	bus.mail("ConfirmSelection");
            }else{
            	if (cargo.getOrder().getTotals().getBalanceDue().compareTo(maxTotal) >= 0) {
        			bus.mail("ConfirmSelection");
        		} else {
        			bus.mail("CaptureShipping");
        		}

            }
            /**
    		 * Rev 1.1 changes end here		
    		 */
        }
	}
}
