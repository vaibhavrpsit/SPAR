/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/RemoveItemRoad.java /main/12 2013/02/11 16:52:16 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  11/14/14 - added code to not remove similar serial item if order has been placed
 *    rgour     02/11/13 - checking the serial number before deleting the item
 *    cgreene   08/10/11 - quickwin - implement dialog for trying to enter
 *                         multiple qty of serialized item
 *    vtemker   07/28/11 - Added the check if its a special order ineligible
 *                         item. If yes, remove all the ineligible items
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:40 PM  Robert Pearse   
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
 *    Rev 1.1   Nov 07 2003 12:37:46   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:14:38   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:04:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:09:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:43:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   14 Nov 2001 06:35:46   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.utility.TransactionUtility;

/**
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class RemoveItemRoad extends PosLaneActionAdapter
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Removes the selected item from the transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        SaleReturnLineItemIfc srli = cargo.getLineItem();
        // Remove all items with the same itemID if the item is in-eligible for
        // special order or was a serialized item.
        if (!TransactionUtility.isItemSpecialOrderEligible(transaction, srli)
                || (srli.isSerializedItem() && srli.getItemSerial() == null))
        {
            removeAllSimilarLineItems(transaction, srli.getItemID());
        }
        else
        {
            cargo.getTransaction().removeLineItem(cargo.getLineItem().getLineNumber());
        }
    }

    /**
     * Remove external order line items, including related items
     * 
     * @param transaction
     * @param itemID
     */
    private void removeAllSimilarLineItems(SaleReturnTransactionIfc transaction, String itemID)
    {

        Vector<AbstractTransactionLineItemIfc> saleLineItems = transaction.getLineItemsVector();
        List<SaleReturnLineItemIfc> itemsForRemoval = new ArrayList<SaleReturnLineItemIfc>(saleLineItems.size());

        SaleReturnLineItemIfc saleItem = null;
        for (int i = 0; i < saleLineItems.size(); i++)
        {
            saleItem = (SaleReturnLineItemIfc)saleLineItems.elementAt(i);

            if (saleItem.getItemID().equals(itemID) && saleItem.getItemSerial() == null)
            {
                boolean removeSimilar = true;
                if (saleItem.getOrderItemStatus() != null && saleItem.getOrderItemStatus().isCrossChannelItem())
                {
                    removeSimilar = false; // do not remove if a cross channel
                                           // order has been created for the
                                           // serial item
                }
                if (removeSimilar)
                {
                    // remove sale items from external order and any related
                    // items
                    // associated with the sale item
                    SaleReturnLineItemIfc[] relatedItems = saleItem.getRelatedItemLineItems();

                    itemsForRemoval.add(saleItem);
                    if (relatedItems != null)
                    {
                        itemsForRemoval.addAll(Arrays.asList(relatedItems));
                    }
                }
            }
        }

        for (int i = 0; i < itemsForRemoval.size(); i++)
        {
            SaleReturnLineItemIfc lineToRemove = itemsForRemoval.get(i);
            transaction.removeLineItem(lineToRemove.getLineNumber());
        }

    }

}
