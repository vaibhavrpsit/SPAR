/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/DisplaySendMethodLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
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
 *    4    360Commerce 1.3         5/1/2007 12:15:40 PM   Brett J. Larsen CR
 *         26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *         feature)
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/06/22 17:28:10  lzhao
 *   @scr 4670: code review
 *
 *   Revision 1.4  2004/06/21 13:11:39  lzhao
 *   @scr 4670: remove unused attribute.
 *
 *   Revision 1.3  2004/06/04 20:23:45  lzhao
 *   @scr 4670: add Change send functionality.
 *
 *   Revision 1.2  2004/06/03 13:29:21  lzhao
 *   @scr 4670: delete send item.
 *
 *   Revision 1.1  2004/06/02 19:06:51  lzhao
 *   @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

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
    static final long serialVersionUID = 8313327083221553865L;


    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * send cargo
     */
    protected SaleCargo saleCargo = null;

    //---------------------------------------------------------------------
    /**
       Load parent (Sale) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of SaleCargo class
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
        SendCargo sendCargo = (SendCargo) bus.getCargo();

        SaleReturnTransaction transaction = (SaleReturnTransaction)saleCargo.getTransaction();
        sendCargo.setTransaction(transaction);

        int sendIndex = saleCargo.getSendIndex();
        if ( sendIndex > 0 )
        {
            SaleReturnLineItemIfc[] sendItems = transaction.getSendItemBasedOnIndex(sendIndex);
            // some items from this send have been deleted.
            // get the number of item from the send.
            /*int count = 0; // item count in the send
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
                SaleReturnLineItemIfc[] sendItems = new SaleReturnLineItem[count];
                int index = -1;
                for ( int i = 0; i < items.length; i++ )
                {
                    if ( items[i].getSendLabelCount() == sendIndex )
                    {
                        sendItems[++index] = items[i];
                    }
                }
                */
                if ( sendItems != null && sendItems.length > 0 )
                {
                    // set the send items in cargo
                    sendCargo.setLineItems(sendItems);
                    // save the shipping to info in the cargo for later to use
                    sendCargo.setShipToInfo(transaction.getSendPackages()[sendIndex-1].getCustomer());
                }
            //}
            // the indec for updating shipping info and customer vectors.
            sendCargo.setSendIndex(sendIndex);
        }
        sendCargo.setTransaction(transaction);
        sendCargo.setOperator(saleCargo.getOperator());

        // set the flat to ture for disable Cancel and Undo button in
        // shipping method screen.
        sendCargo.setItemUpdate(true);
        sendCargo.setStoreStatus(saleCargo.getStoreStatus());
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

