/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/XChannelCreateShippingLaunchShuttle.java /main/2 2013/03/05 14:03:17 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/28/13 - Handle orderLineItems for cargos.
* yiqzhao     10/11/12 - Enable shipping webstore items.
* yiqzhao     10/11/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.sale;

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
    public static String revisionNumber = "$Revision: /main/2 $";
    /**
     * send cargo
     */
    protected SaleCargo saleCargo = null;

    //---------------------------------------------------------------------
    /**
       Load parent (Item) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of saleCargo class
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
        saleCargo = (SaleCargo) bus.getCargo();
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

        SaleReturnTransaction transaction = (SaleReturnTransaction)saleCargo.getTransaction();
        
        shippingCargo.setRegister(saleCargo.getRegister());
        
        //SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)saleCargo.getTransaction().
        //        getItemContainerProxy().retrieveLineItemByID(saleCargo.getLineItem().getLineNumber());

    	List<SaleReturnLineItemIfc> lineItems = new ArrayList<SaleReturnLineItemIfc>();
    	for (SaleReturnLineItemIfc lineItem : saleCargo.getOrderLineItems())
    	{
        	if ( lineItem != null )
        	{
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
        			lineItem.setSelectedForItemModification(true);
        			lineItems.add(lineItem);
        		}
        	}
    	}
    	
    	if ( lineItems.size() > 0 )
    	{
        	shippingCargo.setLineItems(lineItems.toArray(new SaleReturnLineItemIfc[lineItems.size()]));
    
        	shippingCargo.setLineItem(lineItems.get(0));
    	}
     
        shippingCargo.setCustomer(transaction.getCustomer());

        //saleCargo.getItem().getOrderItemStatus().getReference()
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

        shippingCargo.setTransaction(transaction);
        shippingCargo.setOperator(saleCargo.getOperator());

        shippingCargo.setStore(saleCargo.getStoreStatus().getStore());
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
