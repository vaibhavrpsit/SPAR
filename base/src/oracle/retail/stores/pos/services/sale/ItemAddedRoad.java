/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ItemAddedRoad.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/06/12 - set entry type to ENTRY_TYPE_TRANS so that the
 *                         journal entry will be complete.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *         26486 - EJournal enhancements for VAT.
 *    4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:22 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:35 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/29 17:03:25  mweis
 *   @scr 6040 EJ not showing kit header, kit components, nor serialized items.
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 07 2003 12:37:14   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 05 2003 14:14:14   baa
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//--------------------------------------------------------------------------
/**
    Road to traverse after a line item has been added to the transaction.
    <ul>
    <li>Journals the item
    <li>Displays the item on the pole display
    </ul>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class ItemAddedRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
       Journals the added line item information and makes the call to
       display the item info on the pole display device.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc                cargo       = (SaleCargoIfc)bus.getCargo();
        SaleReturnLineItemIfc       item        = cargo.getLineItem();
        SaleReturnTransactionIfc    transaction = cargo.getTransaction();

        //make a journal entry
        JournalManagerIfc journal =
                (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
                (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        if (journal != null)
        {
            if (!item.isKitComponent()) //KitComponentLineItems are journaled by their parent header
            {
                StringBuffer sb = new StringBuffer();
                EYSDate dob = transaction.getAgeRestrictedDOB();
                String itemID = null;
                if(item.getRelatedItemSequenceNumber() > -1)
                {
                    itemID = transaction.getLineItems()[item.getRelatedItemSequenceNumber()].getItemID();
                }
                sb.append(formatter.toJournalString(item, dob, itemID));

                if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) // add status
                {
//                    sb.append(Util.EOL).append("  Status: New");
//
                	String transactionSaleStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_SALE_STATUS, null);
                	sb.append(Util.EOL).append(transactionSaleStatus);
                }
                journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
                journal.journal(cargo.getOperator().getLoginID(),
                                transaction.getTransactionID(),
                                sb.toString());
            }
        }
        else
        {
            logger.error("No JournalManager found");
        }

        //Show item on Line Display device
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        try
        {
            pda.lineDisplayItem(item);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage() + "");
        }

    }//end traverse
}
