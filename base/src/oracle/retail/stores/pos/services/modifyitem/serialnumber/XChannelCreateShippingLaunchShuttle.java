/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/XChannelCreateShippingLaunchShuttle.java /main/1 2013/01/02 11:55:34 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         12/26/12 - create shuttles for create xc shipping item
* sgu         12/26/12 - add new shuttle
* sgu         12/26/12 - add new shuttle
* sgu         12/26/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import java.util.List;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.xchannelcreateshipping.XChannelShippingCargo;

public class XChannelCreateShippingLaunchShuttle extends FinancialCargoShuttle
{
    /**
     * Serial ID
     */
    private static final long serialVersionUID = 7794971433980889244L;
    
    /**
     * Calling Item cargo
     */
    protected SerializedItemCargo itemCargo = null;
    
    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /main/1 $";

    /**
     * Loads the parent cargo
     */
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);
        itemCargo = (SerializedItemCargo)bus.getCargo();
    }

    /**
     * Transfers the item cargo to the pickup delivery order cargo for the
     * modify item service.
     * @param bus Service Bus to copy cargo to.
     */
    public void unload(BusIfc bus)
    {
        // retrieve cargo
        XChannelShippingCargo shippingCargo = (XChannelShippingCargo) bus.getCargo();

        SaleReturnTransaction transaction = (SaleReturnTransaction)itemCargo.getTransaction();
        
        shippingCargo.setRegister(itemCargo.getRegister());
        
        List<SaleReturnLineItemIfc> itemList = itemCargo.getLineItems();
        SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);
        shippingCargo.setLineItems(items);
        if (items != null && items.length > 0)
        {
            shippingCargo.setLineItem(items[0]);
        }
     
        shippingCargo.setCustomer(transaction.getCustomer());

        int maxDeliveryDetailID = 0; // item count in the send
        for (AbstractTransactionLineItemIfc item : transaction.getLineItems() )
        {
            int deliverDetailId = ((SaleReturnLineItemIfc)item).getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID();
            if ( deliverDetailId > maxDeliveryDetailID )
            {
                maxDeliveryDetailID = deliverDetailId;
            }
        }
        shippingCargo.setDeliveryID(maxDeliveryDetailID);

        shippingCargo.setTransaction(transaction);
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
    }
    
    //---------------------------------------------------------------------
    /**
     * Retrieves the Team Connection revision number.
     * <P>
     *
     * @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

}


