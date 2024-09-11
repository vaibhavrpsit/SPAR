/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/ShippingAddressLaunchShuttle.java /main/15 2012/04/17 13:33:54 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/1/2007 12:15:40 PM   Brett J. Larsen CR
 *         26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *         feature)
 *         
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:10  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/08/27 14:41:48  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.5  2004/08/09 19:59:02  rsachdeva
 *   @scr 6791 Send Level In Progress
 *
 *   Revision 1.4  2004/06/19 14:06:40  lzhao
 *   @scr 4670: Integrate with capture customer
 *
 *   Revision 1.3  2004/06/11 19:10:35  lzhao
 *   @scr 4670: add customer present feature
 *
 *   Revision 1.2  2004/06/04 20:23:44  lzhao
 *   @scr 4670: add Change send functionality.
 *
 *   Revision 1.1  2004/05/04 20:44:39  rsachdeva
 *   @scr 4670 Send: Pre-Tender Multiple Sends
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the modify item send service to the cargo used
    in the shippingAddress service. The shipping address is
    being entered pre-tender now. <p>
    $Revision: /main/15 $
**/
//--------------------------------------------------------------------------
public class ShippingAddressLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{                                       // begin class ShippingAddressLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2171402395207560507L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
       item cargo
    **/
    protected ItemCargo itemCargo = null;
    /**
       send level in progress
    **/
    protected boolean sendLevelInProgress = false;

    //----------------------------------------------------------------------
    /**
       Loads cargo from modify item send service. <P>
       @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        super.load(bus);
        itemCargo = (ItemCargo)bus.getCargo();
        sendLevelInProgress = itemCargo.isTransactionLevelSendInProgress();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads data into shippingAddress service. <P>
       @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        super.unload(bus);
        SendCargo cargo = (SendCargo)bus.getCargo();

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
       	
           	SendPackageLineItemIfc sendPackage = itemCargo.getTransaction().getSendPackages()[sendLabelIndex-1];
            ShippingMethodIfc shippingMethod = sendPackage.getShippingMethod();
            CustomerIfc shippingCustomer = sendPackage.getCustomer();
            if ( cargo.getShipToInfo()==null )
            {
            	// if shipping address is not same as billing, the customer info
            	// has been changed in UpdateCustomerAddressAisle of send.address package
            	cargo.setShipToInfo(shippingCustomer);
            }
            cargo.setShippingMethod(shippingMethod);
            cargo.setItemUpdate(true);
            cargo.setSendIndex(sendLabelIndex);
        }

        cargo.setTransaction((SaleReturnTransactionIfc)itemCargo.getTransaction());
        cargo.setLineItems(itemCargo.getItems());
        cargo.setCustomer(itemCargo.getCustomer());
        cargo.setTransactionLevelSendInProgress(sendLevelInProgress);

    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ShippingAddressLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

}                                       // end class ShippingAddressLaunchShuttle
