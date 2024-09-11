/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/ShippingMethodLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/08/31 21:16:15  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.4  2004/08/27 14:32:58  rsachdeva
 *   @scr 6791 Item Level Send  to Transaction Level Send Update Flow
 *
 *   Revision 1.3  2004/08/26 22:34:21  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.2  2004/08/25 19:56:45  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.1  2004/08/10 13:41:06  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;


import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
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
public class ShippingMethodLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5308207598797248656L;

    /**
       revision number
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
       @param bus service bus interface
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
       @param bus service bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        // retrieve cargo
        SendCargo sendCargo = (SendCargo) bus.getCargo();
        SaleReturnTransactionIfc transaction = saleCargo.getTransaction();
        sendCargo.setTransaction(transaction);
        //only one send since we have already checked that transaction level send has
        //been assigned
        sendCargo.setSendIndex(1);
        if (transaction != null)
        {
            sendCargo.setShipToInfo(transaction.getSendPackages()[0].getCustomer());
            //Must use only send items
            SaleReturnLineItemIfc[] sendItems = transaction.getSendItemBasedOnIndex(1);
            sendCargo.setLineItems(sendItems);
        }
        sendCargo.setOperator(saleCargo.getOperator());
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
        String strResult = new String("Class:  ShippingMethodLaunchShuttle (Revision " +
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

