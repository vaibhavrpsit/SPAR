/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/AssignTransactionLevelInfoSite.java /main/13 2012/04/30 15:55:31 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/30/12 - move getShippingChargeLineItem to
 *                         SaleReturnLineItemIfc
 *    yiqzhao   04/26/12 - handle shipping charge as sale return line item
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         5/1/2007 12:15:40 PM   Brett J. Larsen CR
 *       26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *       feature)
 *       
 *  3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 * Revision 1.2  2004/09/01 14:47:47  rsachdeva
 * @scr 6791 Transaction Level Send
 *
 * Revision 1.1  2004/08/10 16:47:56  rsachdeva
 * @scr 6791 Transaction Level Send
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.displaysendmethod;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.send.address.SendCargo;


//--------------------------------------------------------------------------
/**
   Updates/ Adds the Shipping to address.
   The Shipping Method display for transaction level send happens
   after you click tender
   @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class AssignTransactionLevelInfoSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
       done letter
    **/
    public static final String DONE = "Done";
    
    //----------------------------------------------------------------------
    /**
        Updates/ Adds the Shipping to address for transaction level send
        in progress. This site assigns the transaction level send<P>
        @param  bus  Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = new Letter(DONE);
        
        SendCargo cargo = (SendCargo) bus.getCargo();

       	SaleReturnTransactionIfc transaction = cargo.getTransaction();

       	TransactionTotalsIfc totals = transaction.getTransactionTotals();
       	ShippingMethodIfc shippingMethod = DomainGateway.getFactory().getShippingMethodInstance();

       	if (cargo.isItemUpdate())
       	{
           //update from single item level send to transaction level send
           int index = cargo.getSendIndex();
           if (index > 0)
           {
        	   //use transaction level send method to replace item item send method
               transaction.updateSendPackageInfo(index-1, shippingMethod, cargo.getShipToInfo());
               //There is only one item shipping charge allow when change to transaction level send
               SaleReturnLineItemIfc lineItem = transaction.getShippingChargeLineItem(1);
               if ( lineItem != null )
               {
            	   //remove item level shipping charge line item from the transaction
            	   transaction.removeLineItem(lineItem.getLineNumber());
               }
           }
        }
        else
        {
           //Add send packages info
           transaction.addSendPackageInfo(shippingMethod, cargo.getShipToInfo());
           //Assign Send label count on Sale Return Line Items*/
           SaleReturnLineItemIfc[] items = cargo.getLineItems();
           for (int i = 0; i < items.length; i++)
           {
               items[i].setItemSendFlag(true);
               items[i].setSendLabelCount(1); //transaction.getItemSendPackagesCount());
               // set send flag for all kit components as well
               if (items[i] instanceof KitHeaderLineItemIfc)
               {
             	   KitHeaderLineItemIfc kitHeader = (KitHeaderLineItemIfc)items[i];
              	   KitComponentLineItemIfc[] kitComponents = kitHeader.getKitComponentLineItemArray();
               	   for (int j = 0; j < kitComponents.length; j++)
                   {
               		   kitComponents[j].setItemSendFlag(true);
               		   kitComponents[j].setSendLabelCount(1);//transaction.getItemSendPackagesCount());
                   }
               }
           }
        }
        transaction.setTransactionLevelSendAssigned(true);
	   
	    bus.mail(letter, BusIfc.CURRENT);  
    }
    
}

