/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/XChannelCreateShippingLaunchShuttle.java /main/4 2014/02/19 16:38:51 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/19/14 - To avoid shipping service modify the transaction in
*                        modifyitem service. It may cause problem when OMS
*                        office. Shipping line item will be added into the
*                        transaction.
* yiqzhao     08/31/12 - add kit components and remove kit header from
*                        lineItems in cargo.
* yiqzhao     06/29/12 - handle mutiple shipping packages in one transaction
*                        while delete one or more shipping items
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.modifyitem;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.xchannelcreateshipping.XChannelShippingCargo;


public class XChannelCreateShippingLaunchShuttle implements ShuttleIfc
{                                       // begin class XChannelCreateShippingLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1L;

    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /main/4 $";
    /**
     * send cargo
     */
    protected ItemCargo itemCargo = null;

    //---------------------------------------------------------------------
    /**
       Load parent (Item) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ItemCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo loaded
       </UL>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        // retrieve cargo
        itemCargo = (ItemCargo) bus.getCargo();
    }                                   // end load()

    //---------------------------------------------------------------------
    /**
       Unload to child (Send) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of SendCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo unloaded
       </UL>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        // retrieve cargo
    	XChannelShippingCargo shippingCargo = (XChannelShippingCargo) bus.getCargo();

        SaleReturnTransaction transaction = (SaleReturnTransaction)itemCargo.getTransaction();
        
        shippingCargo.setRegister(itemCargo.getRegister());
        
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
            		lineItems.add(kitLineItem.getKitComponentLineItemArray()[j]);
            	}
    		}
    		else
    		{
    			lineItems.add(lineItem);
    		}
    	}
    	shippingCargo.setLineItems(lineItems.toArray(new SaleReturnLineItemIfc[lineItems.size()]));
    	if ( shippingCargo.getLineItems() != null && shippingCargo.getLineItems().length > 0)
    	{
    		shippingCargo.setLineItem(shippingCargo.getLineItems()[0]);
    	}
     
        shippingCargo.setCustomer(transaction.getCustomer());

        //itemCargo.getItem().getOrderItemStatus().getReference()
        // get the number of item from the send.
        int maxDeliveryDetailID = 0; // item count in the send
        SaleReturnLineItemIfc items[] = (SaleReturnLineItemIfc[])transaction.getLineItems();
        for ( SaleReturnLineItemIfc item : items )
        {
        	int deliverDetailId = item.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID();
            if ( deliverDetailId > maxDeliveryDetailID )
            {
            	maxDeliveryDetailID = deliverDetailId;
            }
        }
        shippingCargo.setDeliveryID(maxDeliveryDetailID);

        shippingCargo.setTransaction((SaleReturnTransaction)transaction.clone());
        shippingCargo.setOperator(itemCargo.getOperator());

        shippingCargo.setStore(itemCargo.getStoreStatus().getStore());
    }                                   // end unload()

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  XChannelCreateShippingLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }  
}// end getRevisionNumber()
