/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/DeleteItemRoad.java /main/26 2013/09/05 14:38:44 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/04/14 - added offset to delete item index
 *    abondala  09/05/13 - deprecate some of the API related to tax that is not
 *                         referred as that is causing outOfMemory issues.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    yiqzhao   10/12/12 - Fix ClassCastException error
 *    yiqzhao   06/29/12 - Add dialog for deleting ship item, disable change
 *                         price for ship item
 *    sgu       06/21/12 - rename resetOrderID to resyncOrderID
 *    sgu       06/20/12 - remove setting exception code
 *    sgu       06/20/12 - refactor get order id
 *    sgu       06/19/12 - handle xc order id creation failure
 *    mjwallac  04/25/12 - Fixes for Fortify redundant null check, take2
 *    blarsen   03/08/12 - Moved beanModel.getRowsToDelete() into its own
 *                         method so it could be overriden for MPOS (since it
 *                         does not have beanModels).
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    icole     07/08/11 - Remove DeviceExceptions related to Payment CPOI to
 *                         be consistent with other Payment methods.
 *    blarsen   06/14/11 - Adding storeID to scrolling receipt request.
 *    icole     06/09/11 - Correct merge problem
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    mchellap  03/22/11 - BUG# Tender buttons are not enabled during return
 *                         with receipt
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    miparek   02/21/09 - fixing d#2248, forward port 7696992, removed tender
 *                         elem from txn for selected item
 *    ranojha   02/16/09 - Forward Port Defect for PinPad showing correct
 *                         prices.
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         5/6/2008 6:58:59 PM    Sandy Gu        reset
 *          relate line item info when lineitem vector is updated. code
 *         reviewed by Dan Baker
 *    7    360Commerce 1.6         8/2/2007 6:29:00 AM    Naveen Ganesh
 *         Corrected the message Sales Assoc. for CR 27977
 *    6    360Commerce 1.5         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
 *         26486 - Changes per review comments.
 *    5    360Commerce 1.4         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *         26486 - EJournal enhancements for VAT.
 *    4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse
 *
 *   Revision 1.8  2004/06/10 23:06:35  jriggins
 *   @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service
 *
 *   Revision 1.7  2004/06/02 19:06:51  lzhao
 *   @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 *   Revision 1.6  2004/04/05 15:47:54  jdeleau
 *   @scr 4090 Code review comments incorporated into the codebase
 *
 *   Revision 1.5  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 *   Revision 1.4  2004/03/17 16:00:15  epd
 *   @scr 3561 Bug fixing and refactoring
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
 *    Rev 1.2   Nov 21 2003 13:29:58   rrn
 * Changed "Removed" to "Deleted" in writeJournalEntry method.
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.1   Nov 07 2003 12:36:32   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 05 2003 14:13:58   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.commons.lang3.StringUtils;

/**
 * This road is traversed when the user presses the Delete key from the
 * SELL_ITEM screen.
 *
 */
public class DeleteItemRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7410317201191199678L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/26 $";

    /**
     * static index value indicating no selected row
     */
    protected static final int NO_SELECTION = -1;

    /**
     * Deletes the selected item from the transaction.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /*
         * Get the indices of all selected items
         */

        int selected                             = NO_SELECTION;
        SaleCargoIfc cargo                       = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction     = cargo.getTransaction();
        SaleReturnTransactionIfc[] originalTrans = cargo.getOriginalReturnTransactions();
        int[] allSelected = getItemsToDelete(bus, transaction);

        // if the item being deleted is a primary item
        // with deletable items
        ArrayList<Integer> itemsToDelete = new ArrayList<Integer>();
        for (int i = 0; i < allSelected.length; i++)
        {        	
            // make sure that the item was net set to be deleted already
            // because it is a related item for a primary item that is
            // to be deleted.
            Integer itemNumber = Integer.valueOf(allSelected[i]);
            if (!itemsToDelete.contains(itemNumber))
            {
                itemsToDelete.add(itemNumber);
            }

            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) transaction.retrieveItemByIndex(allSelected[i]);
            SaleReturnLineItemIfc[] relatedLineItems = item.getRelatedItemLineItems();
            if (relatedLineItems != null)
            {
                for (int j = 0;j < relatedLineItems.length;j++)
                {
                    int lineNumber = relatedLineItems[j].getLineNumber();
                    if (!itemsToDelete.contains(new Integer(lineNumber)))
                        itemsToDelete.add(new Integer(lineNumber));
                }
            }
        }
        //Deletion is from back of the array, hence sort the line number
        Collections.sort(itemsToDelete);

        // for each item in the array.  Process from the back of the array,
        // because if you process from the front, items will not be in the
        // expected locations in the transaction when processing the end of the array.
        for (int i = itemsToDelete.size() - 1; i > -1; i--)
        {
            selected = itemsToDelete.get(i);
            /*
             * Update the transaction in the cargo
             */
            SaleReturnLineItemIfc item =
                (SaleReturnLineItemIfc) transaction.retrieveItemByIndex(selected);

            // If the line item is associated with a price adjustment, remove that data from the
            // list of original transactions for price adjustments so that it may be reentered later.
            if (item != null && (item.isPriceAdjustmentLineItem() || item.isPartOfPriceAdjustment()) )
            {
                PriceAdjustmentLineItemIfc priceAdjLineItem = null;
                TransactionIDIfc origTransID = null;

                if (item.isPartOfPriceAdjustment())
                {
                    priceAdjLineItem =
                        transaction.retrievePriceAdjustmentByReference(item.getPriceAdjustmentReference());
                }
                else
                {
                    priceAdjLineItem = (PriceAdjustmentLineItemIfc)item;
                }

                ReturnItemIfc returnItem = priceAdjLineItem.getReturnItem();
                if (returnItem != null)
                {
                    origTransID = returnItem.getOriginalTransactionID();
                }

                if (origTransID != null)
                {
                    cargo.removeOriginalPriceAdjustmentTransaction(origTransID.getTransactionIDString());
                }
            }

            // Remove the line item from the transaction
            transaction.removeLineItem(selected);
            //  Remove tender elements
            if(transaction.getReturnTenderElements()!=null)
            {
                transaction = removeReturnTenderElement(transaction);
            }

            // Save each to be cleared line item into the deletedLineItems vector
            transaction.addDeletedLineItems(item);

            // Attempt to restore the original transaction.
            if ((item != null) && (item.getReturnItem() != null) &&
                    // if return item original line number is -1, there was no original transaction
                    (item.getReturnItem().getOriginalLineNumber() != -1)  &&  (originalTrans != null))
            {
                SaleReturnTransactionIfc restored = restoreOriginalTransaction(item, originalTrans);
                if (restored != null)
                {
                    // This methods replaces existing transactions.
                    cargo.addOriginalReturnTransaction(restored);
                }
            }


            if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                setOrderIDDuringOrderInitiate(bus, cargo);
            }

            writeJournalEntry(bus, transaction, item, bus.getServiceName());


            //set the refresh flag to true so that line items is refreshed in the CPOI display
            cargo.setRefreshNeeded(true);
        }
    }

    /**
     * Get the items to delete.
     */
    protected int[] getItemsToDelete(BusIfc bus, SaleReturnTransactionIfc transaction)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /*
         * Get the indices of all selected items
         */
        LineItemsModel beanModel = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
        int[] allSelected = beanModel.getRowsToDelete();
        int offset = getOffsetToDeleteItem(transaction);
        
        for (int i = 0; i < allSelected.length; i++)
        {
        	allSelected[i] = allSelected[i] + offset;
        }
        return allSelected;
    }

    /**
     * Removes the tender elements from the transactions for the selected item.
     *
     * @param transaction SaleReturnTransactionIfc
     * @param selected selected item's position
     *
     * @return SaleReturnTransaction for the given item
     */
    protected SaleReturnTransactionIfc removeReturnTenderElement(SaleReturnTransactionIfc transaction)
    {
        int lineItemsSize = transaction.getLineItemsSize();
        ArrayList<ReturnTenderDataElementIfc> returnTender =  new ArrayList<ReturnTenderDataElementIfc>(Arrays.asList(transaction.getReturnTenderElements()));
        if (lineItemsSize == 0)
        {
            for (int j = 0; j < returnTender.size(); j++)
            {
                returnTender.remove(j);
            }
            ReturnTenderDataElementIfc[] ReturnTenderDataElementArray = returnTender.toArray(new ReturnTenderDataElementIfc[returnTender.size()]);
            transaction.setReturnTenderElements(ReturnTenderDataElementArray);
        }
        return transaction;
    }

    /**
     * Reverses the returned count on the original transaction line item.
     *
     * @param item Item to return
     * @param originalTrans array of transactions to search for the return item in
     *
     * @return SaleReturnTransaction for the given item
     */
    protected SaleReturnTransactionIfc restoreOriginalTransaction(SaleReturnLineItemIfc item,
                                                                  SaleReturnTransactionIfc[] originalTrans)
    {
        ReturnItemIfc            ri = item.getReturnItem();
        TransactionIDIfc originalID = ri.getOriginalTransactionID();
        int               lineIndex = ri.getOriginalLineNumber();

        // find the original transaction.
        SaleReturnTransactionIfc trans = null;
        for(int i = 0; i < originalTrans.length; i++)
        {
            if (originalID.equals(originalTrans[i].getTransactionIdentifier()))
            {
                trans = originalTrans[i];
                i     = originalTrans.length;
            }
        }

        // Find the original line item.
        if (trans != null && lineIndex < trans.getLineItemsSize())
        {
            if(lineIndex >= 0)
            {
                SaleReturnLineItemIfc originalItem =
                    (SaleReturnLineItemIfc) trans.retrieveItemByIndex(lineIndex);
                originalItem.setQuantityReturned(originalItem.getQuantityReturnedDecimal().add(item.getItemQuantityDecimal()));
                trans.replaceLineItem(originalItem, lineIndex);
            }
        }

        return trans;
    }

    /**
     * Writes an entry in the journal.
     *
     * @param transaction The void transaction to journal
     * @param item line item
     * @param serviceName service name for log
     */
    protected void writeJournalEntry(BusIfc bus, SaleReturnTransactionIfc transaction,
                                     SaleReturnLineItemIfc item,
                                     String serviceName)
    {
        /*
         * Write the journal entry
         */
        JournalManagerIfc journal =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        if (journal != null)
        {
            StringBuffer sb = new StringBuffer();
            sb.append(formatter.toJournalRemoveString(item));

            ItemDiscountStrategyIfc[] itemDiscounts =
                item.getItemPrice().getItemDiscounts();

            if((itemDiscounts != null) && (itemDiscounts.length > 0))
            {
                for(int i = 0; i < itemDiscounts.length; i++)
                {
                    if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                    {
                        sb.append(Util.EOL);
                        sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i], true));
                    }
                }
            }

            String saTrans = transaction.getSalesAssociate().getEmployeeID();
            if(item.getSalesAssociate() != null)
            {
                String saItem = item.getSalesAssociate().getEmployeeID();
                if(!(saTrans.equals(saItem)))
                {
                    Object dataArgs[]={item.getItemID()};
                    String itemId = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM, dataArgs);

                    Object salesAssocDataArgs[]={item.getItemID()};
                    String salesAssocDeleted = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_SALESID_DELETED, salesAssocDataArgs);

                    sb.append(Util.EOL).append(Util.EOL)
                      .append(itemId)
                      .append(Util.EOL)
                      .append(salesAssocDeleted);

                }
            }

            journal.journal(transaction.getSalesAssociateID(),
                            transaction.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.error( "No JournalManager found");
        }
    }

    /**
     * Set new order id for an order initiate transaction
     * @param bus
     * @param cargo
     * @throws DataException
     */
    protected void setOrderIDDuringOrderInitiate(BusIfc bus, SaleCargoIfc cargo)
    {
        // If all xc order items are deleted, the transaction becomes a store order.
        // A store order id has to be assigned to this transaction.
        OrderManagerIfc orderMgr = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
        OrderTransactionIfc orderTransaction = (OrderTransactionIfc)cargo.getTransaction();

        String orderID = orderTransaction.resyncNewOrderID();
        if (!orderTransaction.containsXChannelOrderLineItem() && StringUtils.isBlank(orderID))
        {
            RegisterIfc register = cargo.getRegister();
            orderTransaction.assignNewOrderID(orderMgr.getNewOrderID(register), false);
            orderTransaction.setUniqueID(register.getCurrentUniqueID());
        }
    }
    
    /**
     * Get the offset to delete item index
     * @param transaction the sale return transaction 
     * @return the offset 
     */
    protected int getOffsetToDeleteItem(SaleReturnTransactionIfc transaction)
    {
    	// The delete item index is the index from the UI list model, it has to be adjusted
    	// to reflect the index of the line item in the transaction.
        // For an order pickup transaction with repriced order items, the price cancelled 
        // order items are hidden from the UI, and we are adding the offset to reflect 
        // this. Only take with items can be deleted in an order pickup transaction, and 
        // take with items always comes after order items. 
    	int offset = 0;
    	for (AbstractTransactionLineItemIfc lineItem : transaction.getLineItems())
    	{
    		SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItem;
    		if (srli.isPriceCancelledDuringPickup())
    		{
    			offset++;
    		}
    	}
    	
    	return offset;
    }
}
