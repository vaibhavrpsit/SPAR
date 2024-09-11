/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/XChannelCreatePickupLaunchShuttle.java /main/2 2013/03/19 11:51:09 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     03/19/13 - Add customer info in the cargo.
* yiqzhao     09/04/12 - add setPreSplitLineNumber for lineitems
* yiqzhao     08/31/12 - add kit components and remove kit header in lineItems.
* yiqzhao     08/29/12 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.services.modifyitem;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class XChannelCreatePickupLaunchShuttle extends PickupLaunchShuttle implements ShuttleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1L;

    /**
     * revision number
     */
    public static final String revisionNumber = "Revision:$";



    // ----------------------------------------------------------------------
    /**
     * Transfers the item cargo to the pickup delivery order cargo for the
     * modify item service.
     * <P>
     *
     * @param bus Service Bus to copy cargo to.
     */
    // ----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
    	PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();

        pickupDeliveryOrderCargo.setRegister(itemCargo.getRegister());
        pickupDeliveryOrderCargo.setTransactionType(itemCargo.getTransactionType());
        pickupDeliveryOrderCargo.setTransaction(itemCargo.getTransaction());
        pickupDeliveryOrderCargo.setStoreStatus(itemCargo.getStoreStatus());
        pickupDeliveryOrderCargo.setOperator(itemCargo.getOperator());
        pickupDeliveryOrderCargo.setTenderLimits(itemCargo.getTenderLimits());
        
        //replace "pickupDeliveryOrderCargo.setLineItems(itemCargo.getItems());" in super class
    	List<SaleReturnLineItemIfc> lineItems = new ArrayList<SaleReturnLineItemIfc>();
    	for ( int i=0; i<itemCargo.getItems().length; i++ )
    	{
    		SaleReturnLineItemIfc lineItem = itemCargo.getItems()[i];
    		if ( lineItem.isKitHeader() )
    		{
    			//use kit components to replace kit header
            	KitHeaderLineItemIfc kitLineItem = (KitHeaderLineItemIfc)lineItem;
            	for ( int j=0; j<kitLineItem.getKitComponentLineItemArray().length; j++ )
            	{
            		KitComponentLineItemIfc kitComp = kitLineItem.getKitComponentLineItemArray()[j];
            		kitLineItem.getKitComponentLineItemArray()[j].setPreSplitLineNumber(kitLineItem.getKitComponentLineItemArray()[j].getLineNumber());
            		lineItems.add(kitComp);
            	}
    		}
    		else
    		{
    			lineItem.setPreSplitLineNumber(lineItem.getLineNumber());
    			lineItems.add(lineItem);
    		}
    	}
    	pickupDeliveryOrderCargo.setLineItems(lineItems.toArray(new SaleReturnLineItemIfc[lineItems.size()]));
    	if ( pickupDeliveryOrderCargo.getLineItems() != null && pickupDeliveryOrderCargo.getLineItems().length > 0)
    	{
    		pickupDeliveryOrderCargo.setItem(pickupDeliveryOrderCargo.getLineItems()[0]);
    	}
        
        // Set up line items so that if a line item split occurs, the tour
        // can find all the items that need to be updated.
        if (pickupDeliveryOrderCargo.getTransaction() instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)pickupDeliveryOrderCargo.getTransaction();
            pickupDeliveryOrderCargo.setCustomer(trans.getCustomer());
            for (AbstractTransactionLineItemIfc lineItem : trans.getItemContainerProxy().getLineItems())
            {
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
                    srli.setPreSplitLineNumber(srli.getLineNumber());
                    if ( srli.isKitHeader() )
                    {
                        for (SaleReturnLineItemIfc compItem: ((KitHeaderLineItemIfc)lineItem).getKitComponentLineItemArray())
                        {
                        	compItem.setPreSplitLineNumber(compItem.getLineNumber());
                        }
                    }
                }
            }
        }
    }
}
