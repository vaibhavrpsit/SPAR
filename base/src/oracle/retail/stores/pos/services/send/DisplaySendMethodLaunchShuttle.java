/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/DisplaySendMethodLaunchShuttle.java /main/13 2012/04/17 13:33:51 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/1/2007 12:15:40 PM   Brett J. Larsen CR
 *         26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *         feature)
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:10  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/09/01 13:53:32  rsachdeva
 *   @scr 6791 Transaction Level Send Javadoc
 *
 *   Revision 1.5  2004/08/27 14:41:48  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.4  2004/08/09 19:27:06  rsachdeva
 *   @scr 6791 Send Level In Progress
 *
 *   Revision 1.3  2004/06/21 13:13:55  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.2  2004/06/19 14:06:40  lzhao
 *   @scr 4670: Integrate with capture customer
 *
 *   Revision 1.1  2004/06/16 13:42:07  lzhao
 *   @scr 4670: refactoring Send for 7.0.
 *
 *   Revision 1.4  2004/06/11 19:10:35  lzhao
 *   @scr 4670: add customer present feature
 *
 *   Revision 1.3  2004/06/04 20:23:44  lzhao
 *   @scr 4670: add Change send functionality.
 *
 *   Revision 1.2  2004/06/02 19:06:51  lzhao
 *   @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;


import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;

//------------------------------------------------------------------------------
/**
    Launch shuttle class for send.displaysendmethod service. <P>
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class DisplaySendMethodLaunchShuttle implements ShuttleIfc
{                                       // begin class DisplaySendMethodLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4408989349772150598L;

    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /main/13 $";
    /**
       Cargo from the Item service.
    **/
    protected ItemCargo itemCargo = null;
    /**
       send level in progress
    **/
    protected boolean sendLevelInProgress = false;

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
       @param  bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        // retrieve cargo
        itemCargo = (ItemCargo) bus.getCargo();
        sendLevelInProgress = itemCargo.isTransactionLevelSendInProgress();
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
       @param bus service bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        // retrieve cargo
        SendCargo cargo = (SendCargo) bus.getCargo();

        cargo.setTransactionLevelSendInProgress(sendLevelInProgress);


        // initialize cargo with line items, ShipToInfo, etc
        if ( itemCargo.isItemUpdate() )
        {
            SaleReturnLineItemIfc[] items = itemCargo.getItems();
            int sendLabelIndex = 0;
            for ( int i = 0; i < items.length; i++ )
            {
                if( items[i].getItemSendFlag() )
                {
                    sendLabelIndex = items[i].getSendLabelCount();
                    break;
                }
            }

            RetailTransactionIfc transaction = ((SaleReturnTransactionIfc)itemCargo.getTransaction());
            ShippingMethodIfc shippingMethod = transaction.getSendPackages()[sendLabelIndex-1].getShippingMethod();
            CustomerIfc shippingCustomer = transaction.getSendPackages()[sendLabelIndex-1].getCustomer();

            cargo.setShipToInfo(shippingCustomer);
            cargo.setShippingMethod(shippingMethod);
            cargo.setItemUpdate(true);
            cargo.setSendIndex(sendLabelIndex);
        }
        else
        {
            cargo.setShipToInfo(itemCargo.getCustomer());
        }
        cargo.setLineItems(itemCargo.getItems());
        cargo.setOperator(itemCargo.getOperator());
        cargo.setStoreStatus(itemCargo.getStoreStatus());


        cargo.setCustomer(itemCargo.getCustomer());

        if ( itemCargo.getTransaction() instanceof SaleReturnTransactionIfc )
        {
            cargo.setTransaction((SaleReturnTransactionIfc)itemCargo.getTransaction());
        }
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
        String strResult = new String("Class:  DisplaySendMethodLaunchShuttle (Revision " +
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
    }                                   // end getRevisionNumber()

}                                       // end class DisplaySendMethodLaunchShuttle

