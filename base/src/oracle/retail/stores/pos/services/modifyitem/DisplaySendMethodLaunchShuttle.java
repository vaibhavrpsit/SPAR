/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/DisplaySendMethodLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/1/2007 12:15:40 PM   Brett J. Larsen CR
 *         26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *         feature)
 *         
 *    4    360Commerce 1.3         7/21/2006 4:14:15 PM   Brendan W. Farrell
 *         Merge from v7.x.  Use ifc so that it is extendable.
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/06/21 13:16:07  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.3  2004/06/14 23:35:26  lzhao
 *   @scr 4670: fix shipping charge calculation.
 *
 *   Revision 1.2  2004/06/09 17:12:52  lzhao
 *   @scr 4670: set quantity for send item to calculate shipping charge.
 *
 *   Revision 1.1  2004/06/09 14:24:17  lzhao
 *   @scr 4670: add shipping method for update quantity for send item.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.send.address.SendCargo;

//------------------------------------------------------------------------------
/**
    Launch shuttle class for DisplaySendMethod service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DisplaySendMethodLaunchShuttle implements ShuttleIfc
{                                       // begin class DisplaySendMethodLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5764291499593329499L;


    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
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
        SendCargo sendCargo = (SendCargo) bus.getCargo();

        SaleReturnTransaction transaction = (SaleReturnTransaction)itemCargo.getTransaction();
        sendCargo.setTransaction(transaction);

        int sendIndex = itemCargo.getItem().getSendLabelCount();
        // get the number of item from the send.
        int count = 0; // item count in the send
        SaleReturnLineItemIfc items[] = (SaleReturnLineItemIfc[])transaction.getLineItems();
        for ( int i = 0; i < items.length; i++ )
        {
            if ( items[i].getSendLabelCount() == sendIndex )
            {
                count++;
            }
        }
        // get the items in the send
        if ( count > 0 )
        {
            SaleReturnLineItemIfc[] sendItems = new SaleReturnLineItemIfc[count];
            int index = -1;
            for ( int i = 0; i < items.length; i++ )
            {
                if ( items[i].getSendLabelCount() == sendIndex )
                {
                    sendItems[++index] = items[i];
                    if ( items[i].getItemID() == itemCargo.getItem().getItemID() )
                    {
                        sendItems[index].getItemPrice().setItemQuantity(itemCargo.getItemQuantity());
                        sendItems[index].setItemQuantity(itemCargo.getItemQuantity());
                    }
                    else
                    {
                        sendItems[index].getItemPrice().setItemQuantity(items[i].getItemQuantityDecimal());
                        sendItems[index].setItemQuantity(items[i].getItemQuantityDecimal());
                    }
                    sendItems[index].getItemPrice().calculateItemTotal();
                }
            }
            if ( sendItems.length > 0 )
            {
                // set the send items in cargo
                sendCargo.setLineItems(sendItems);
                // save the shipping to info in the cargo for later to use
                sendCargo.setShipToInfo(transaction.getSendPackages()[sendIndex-1].getCustomer());
            }
        }
            // the indec for updating shipping info and customer vectors.
        sendCargo.setSendIndex(sendIndex);
        sendCargo.setTransaction(transaction);
        sendCargo.setOperator(itemCargo.getOperator());

        // set the flat to ture for disable Cancel and Undo button in
        // shipping method screen.
        sendCargo.setItemUpdate(true);
        sendCargo.setStoreStatus(itemCargo.getStoreStatus());
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

