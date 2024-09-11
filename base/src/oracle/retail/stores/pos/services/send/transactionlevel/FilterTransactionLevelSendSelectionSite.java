/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/transactionlevel/FilterTransactionLevelSendSelectionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:03 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/08/31 15:47:29  rsachdeva
 *   @scr  6791 Transaction Level Send
 *
 *   Revision 1.1  2004/08/09 20:40:39  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.transactionlevel;

import java.util.Vector;

import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;

//--------------------------------------------------------------------------
/**
   The purpose of this site is to allow only valid send items to the
   send service
   $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class FilterTransactionLevelSendSelectionSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";   
    /**
       letter for items in transaction
    **/
    public static final String ITEMS_IN_TRANSACTION = "ItemsInTransaction"; 
    
    //----------------------------------------------------------------------
    /**
        Filters for valid send items<P>
        @param  bus  Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        LetterIfc letter = new Letter(ITEMS_IN_TRANSACTION);
        SendManagerIfc sendMgr = null;
        try
        {
            sendMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
        }
        catch (ManagerException e)
        {
            // default to product version
            sendMgr = new SendManager();
        }

        if (cargo.getTransaction() != null && cargo.getItems() != null)
        {
            Vector validItems = new Vector();        
            SaleReturnLineItemIfc[] items = cargo.getItems();
            for (int i = 0; i < items.length; i++)
            {
                // if the item is NOT eligible for send.
                if (sendMgr.checkValidSendItem(items[i]))
                {
                    validItems.add(items[i]);
                }
            }
            SaleReturnLineItemIfc[] validForSend = new SaleReturnLineItemIfc[validItems.size()];
            for (int j = 0; j < validItems.size(); j++)
            {
                validForSend[j] = (SaleReturnLineItemIfc)validItems.get(j);
            }
            cargo.setItems(validForSend);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
    
}
