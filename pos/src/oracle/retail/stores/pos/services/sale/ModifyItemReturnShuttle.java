/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ModifyItemReturnShuttle.java /main/20 2013/03/05 14:03:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   02/26/13 - Check for order item after adding related item(s).
 *    hyin      10/03/12 - set itemFromWebStore when going through different
 *                         flow.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    jswan     11/02/10 - Fixed issue with returning transaction from PD).
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    dfung     12/08/09 - Fixed NPE crash by setting PLUItem to sale cargo.
 *    jswan     04/14/09 - Fixed crash when escaping from PDO services.
 *    jswan     04/14/09 - Modified to fix conflict between multi quantity
 *                         items and items that have been marked for Pickup or
 *                         Delivery.
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
 *       26486 - Changes per review comments.
 *  6    360Commerce 1.5         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *       26486 - EJournal enhancements for VAT.
 *  5    360Commerce 1.4         1/22/2006 11:45:01 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  4    360Commerce 1.3         12/13/2005 4:42:35 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
 * $
 * Revision 1.8  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.7  2004/03/16 18:30:42  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.6  2004/03/05 22:57:24  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.5 2004/03/05 00:41:53 bjosserand @scr 3954 Tax Override
 *
 * Revision 1.4 2004/02/24 20:14:10 epd @scr 3561 Updates to manage how items are added to transaction
 *
 * Revision 1.3 2004/02/12 16:48:17 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:22:50 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.2 Jan 09 2004 12:54:18 lzhao set transaction back, remove comments, add date Resolution for 3666: Eltronic
 * Journal for Gift Card Issue and Reload not Correct
 *
 * Rev 1.1 Nov 07 2003 12:37:22 baa use SaleCargoIfc Resolution for 3430: Sale Service Refactoring
 *
 * Rev 1.0 Nov 05 2003 14:14:20 baa Initial revision.
 *
 * Rev 1.1 Sep 03 2003 16:04:34 RSachdeva Add CIDScreen support Resolution for POS SCR-3355: Add CIDScreen support
 *
 * Rev 1.0 Aug 29 2003 16:04:38 CSchellenger Initial revision.
 *
 * Rev 1.7 Jul 17 2003 06:53:56 jgs Modifed journaling for item discounts. Resolution for 3037: The ejournal for a
 * transaction with multiple (3) % discounts applies and removes the first two discounts on the ejournal.
 *
 * Rev 1.6 Jun 30 2003 12:59:14 sfl Used the actual line item's line number for replacing line item in the transaction.
 * Resolution for POS SCR-2246: Kit component items will be doubled when doing Item and Undo during no-receipt return
 *
 * Rev 1.5 Mar 05 2003 18:20:40 DCobb setAlterationItemFlag() name change. Resolution for POS SCR-1808: Alterations
 * instructions not saved and not printed when trans. suspended
 *
 * Rev 1.4 Jan 14 2003 17:26:58 sfl Must allow mutiple item selection even when cargo.getIndex() is less than 1.
 * Resolution for POS SCR-1912: Mulitple items are not able to be selected for Modify Item Send via Multiple Item
 * Select
 *
 * Rev 1.3 Oct 14 2002 16:10:06 DCobb Added alterations service to item inquiry service. Resolution for POS SCR-1753:
 * POS 5.5 Alterations Package
 *
 * Rev 1.2 Aug 27 2002 17:08:40 dfh match 51 fix Resolution for POS SCR-1760: Layaway feature updates
 *
 * Rev 1.1 Aug 21 2002 11:21:30 DCobb Added Alterations service. Resolution for POS SCR-1753: POS 5.5 Alterations
 * Package
 *
 * Rev 1.0 Apr 29 2002 15:09:26 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:42:46 msg Initial revision.
 *
 * Rev 1.9 Mar 08 2002 15:57:56 dfh added serviceitemflag to itemcargo, set this flag to true when services item added,
 * journals this item when returning to pos after successfully adding the services item Resolution for POS SCR-1123:
 * Non Merchandise items selected from list not appearing on EJ
 *
 * Rev 1.8 Feb 05 2002 16:43:04 mpm Modified to use IBM BigDecimal. Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 * Rev 1.7 27 Dec 2001 16:20:24 pjf Added initializeTransaction() method to cargo. Modified sites to remove duplicated
 * code. Resolution for POS SCR-10: Advanced Pricing
 *
 * Rev 1.6 Dec 18 2001 11:51:20 dfh remove journalling item when added to trans., done in pos already - Resolution for
 * POS SCR-260: Special Order feature for release 5.0
 *
 * Rev 1.5 06 Dec 2001 08:02:12 pjf Convertied Item Inquiry to use modifyitem\serialnumber\ service. Needed for kits,
 * modify item updates. Resolution for POS SCR-8: Item Kits
 *
 * Rev 1.4 19 Nov 2001 16:57:06 sfl Allow the shuttle to bring back modified item send flag all the way from the cargo
 * in sendmain service, then modifyitem service to pos service. Resolution for POS SCR-287: Send Transaction
 *
 * Rev 1.3 13 Nov 2001 14:45:02 sfl Made the changes to support multiple items to be shuttled from cargo to cargo.
 * Resolution for POS SCR-282: Multiple Item Selection
 *
 * Rev 1.2 Nov 07 2001 15:50:06 vxs Modified LineDisplayItem() in POSDeviceActionGroup, so accommodating changes for
 * other files as well. Resolution for POS SCR-208: Line Display
 *
 * Rev 1.1 Oct 12 2001 15:40:30 vxs Putting line display mechanism in service code. Resolution for POS SCR-208: Line
 * Display
 *
 * Rev 1.0 Sep 21 2001 11:33:08 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;
// java imports
import java.math.BigDecimal;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.journal.JournalFormatterManager;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

//--------------------------------------------------------------------------
/**
 * This shuttle copies information from the Modify Item service cargo to the POS service cargo.
 * <p>
 *
 * @version $Revision: /main/20 $
 */
//--------------------------------------------------------------------------
public class ModifyItemReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2026579451830842000L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.ModifyItemReturnShuttle.class);
    ;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * The modified line item.
     */
    protected SaleReturnLineItemIfc lineItem = null;

    /**
     * The modified line items.
     */
    protected SaleReturnLineItemIfc[] lineItemList = null;

    /**
     * The flag that indicates whether an item is being added
     */
    protected boolean addPLUItem = false;

    /**
     * The item to add.
     */
    protected PLUItemIfc pluItem = null;

    /**
     * Item Quantity
     */
    protected BigDecimal itemQuantity = null;

    /**
     * Flag indicating whether item added is service and added thru inquiry/services
     */
    protected boolean serviceItemFlag = false;
    
    /**
     * Flag indicating whether item is from webStore
     */
    protected boolean itemFromWebStore = false;
    

    /**
     * transaction type - sale or return
     */
    protected SaleReturnTransactionIfc transaction;

    /**
     * item cargo
     */

    protected ItemCargo itemCargo=null;

    //----------------------------------------------------------------------
    /**
     * Copies information from the cargo used in the Modify Item service.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the child
        itemCargo = (ItemCargo) bus.getCargo();

        // set the add item flag
        addPLUItem = itemCargo.getAddPLUItem();

        // set whether service item added thru inquiry/services
        serviceItemFlag = itemCargo.getServiceItemFlag();
        
        // set itemFromWebStore flag
        itemFromWebStore = itemCargo.isItemFromWebStore();

        //return the transaction only if adding an alteration item
        transaction = (SaleReturnTransactionIfc) itemCargo.getTransaction();

        if (addPLUItem && itemCargo.getPLUItem() != null)
        {
            // set the item to add
            pluItem = itemCargo.getPLUItem();
            itemQuantity = itemCargo.getItemQuantity();
        }
        else
        {
            // set the child reference to the temp
            lineItem = itemCargo.getItem();
            lineItemList = itemCargo.getItems();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Copies information to the cargo used in the POS service.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the parent
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        cargo.setRefreshNeeded(true);

        if (addPLUItem && pluItem != null)
        {
            if (cargo.getTransaction() == null)
            {
                if (transaction == null)
                {
                    cargo.initializeTransaction(bus);
                }
                else
                {
                    cargo.setTransaction(transaction);
                }
            }

            cargo.setPLUItem(pluItem);
            SaleReturnLineItemIfc item = cargo.getTransaction().addPLUItem(pluItem, itemQuantity);

            if (itemFromWebStore)
            {
                item.setPluDataFromCrossChannelSource(true);
            }

            String productGroup = pluItem.getProductGroupID();
            if (productGroup != null && productGroup.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_ALTERATION))
            {
                //Set the Alteration Item Flag
                item.setAlterationItemFlag(true);
            }

            //set the line item for the serialized item service
            cargo.setLineItem(item);

            if (serviceItemFlag) // journal the service item added to the transaction
            {
                JournalManagerIfc journal =
                    (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
                JournalFormatterManagerIfc formatter =
                    (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManager.TYPE);
                if (journal != null)
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append(formatter.toJournalString(item, null, null));

                    if (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
                    {
                    	String transactionSaleStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_SALE_STATUS, null);
                    	sb.append(Util.EOL).append(transactionSaleStatus);

                    }

                    journal.journal(
                        cargo.getOperator().getLoginID(),
                        cargo.getTransaction().getTransactionID(),
                        sb.toString());
                }
                else
                {
                    logger.error("No JournalManager found");
                }
            }

            //Show item on Line Display device
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            try
            {
                pda.lineDisplayItem(item);
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to use Line Display: " + e.getMessage() + "");
            }

        } // end if (addPLUItem)
        else if (cargo.getIndex() >= 0 || lineItemList != null)
        {
            // Multi-quantity line items can be expanded to individual line items
            // when a customer is added to the transaction.  As result pickup and 
            // delivery items must manage the update of line items itself.
            // This code prevent a PDO item change with a line item list from being
            // updated here.
            if (lineItemList != null && !itemCargo.isPickupOrDeliveryExecuted())
            {
                for (int i = 0; i < lineItemList.length; i++)
                {
                    cargo.getTransaction().replaceLineItem(lineItemList[i], lineItemList[i].getLineNumber());
                    cargo.setItemModifiedIndex(lineItemList[i].getLineNumber());
                }
            }

        } // end else

        if (transaction != null)
        {
            if (itemCargo.isPickupOrDeliveryExecuted())
            {
                cargo.setTransaction(transaction);
            }
            else
            if (cargo.getTransaction() == null)
            {
                cargo.setTransaction(transaction);
            }
            else 
            if (transaction.getAgeRestrictedDOB() != null && cargo.getTransaction() != null)
            {
                cargo.getTransaction().setAgeRestrictedDOB(transaction.getAgeRestrictedDOB());
            }
        }
        
        for (SaleReturnLineItemIfc orderLineItem: itemCargo.getOrderLineItems() )
        {
            cargo.addOrderLineItem(orderLineItem);
        }
    
    } // end method
}
