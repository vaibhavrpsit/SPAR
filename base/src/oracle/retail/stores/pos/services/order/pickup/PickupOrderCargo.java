/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/PickupOrderCargo.java /main/13 2012/10/29 14:51:49 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/29/12 - disable pickup and cancel buttons when not
 *                         applicable
 *    sgu       10/24/12 - refactor order view and cancel flow
 *    sgu       10/04/12 - split order item for pickup
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Added SaleReturnLine Item attribute for
 *                         serialisation.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         2/22/2008 10:30:34 AM  Pardee Chhabra  CR
 *         30191: Tender Refund options are not displayed as per specification
 *          for Special Order Cancel feature.
 *    4    360Commerce 1.3         5/8/2007 5:22:00 PM    Alan N. Sinton  CR
 *         26486 - Refactor of some EJournal code.
 *    3    360Commerce 1.2         3/31/2005 4:29:21 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:05 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:03 PM  Robert Pearse
 *
 *   Revision 1.6  2004/10/06 02:44:24  mweis
 *   @scr 7012 Special and Web Orders now have Inventory.
 *
 *   Revision 1.5  2004/06/29 22:03:32  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.4.2.1  2004/06/14 17:48:08  aachinfiev
 *   Inventory location/state related modifications
 *
 *   Revision 1.4  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.3  2004/02/12 16:51:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 19 2004 15:34:14   DCobb
 * Use TendarableTransactionCargoIfc in oracle/retail/stores/pos/services/common.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 *
 *    Rev 1.0   Aug 29 2003 16:03:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:11:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:44   msg
 * Initial revision.
 *
 *    Rev 1.3   29 Jan 2002 18:34:40   cir
 * Added serialized items array
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   29 Jan 2002 15:11:50   sfl
 * Clone the whole order to support the ESC in the
 * Tender Options screen during doing the tender
 * for a special order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   15 Jan 2002 18:42:08   cir
 * Implements TenderableTransactionCargoIfc,RetailTransactionCargoIfc
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:01:18   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;

/**
 * Carries data common to order services.
 *
 * @version $Revision: /main/13 $
 */
public class PickupOrderCargo extends OrderCargo implements RetailTransactionCargoIfc
{
    private static final long serialVersionUID = 7473730407241557651L;

    //revision number supplied by source-code-control system
    public static String revisionNumber = "$Revision: /main/13 $";

    //OrderTransactionIfc attribute
    protected OrderTransactionIfc transaction = null;

    // Serialized line items saved in the cargo
    protected AbstractTransactionLineItemIfc[] serializedItems = null;

    // Serialized line items counter
    protected int serializedItemsCounter = 0;

    private List<SaleReturnTransactionIfc> originalReturnTransactions;

    /**
     * Current Line Item
     */
    protected SaleReturnLineItemIfc lineItem;

    /**
     * Gets the current line item
     * @return
     */
    public SaleReturnLineItemIfc getLineItem()
    {
        return lineItem;
    }

    /**
     * Sets the current line item.
     *
     * @param lineItem
     */
    public void setLineItem(SaleReturnLineItemIfc lineItem)
    {
        this.lineItem = lineItem;
    }

    /**
     * Sets the transaction created from the order.
     *
     * @param OrderTransactionIfc
     */
    public void setTransaction(OrderTransactionIfc value)
    {
        transaction = value;
    }

    /**
     * Gets the transaction created from the order.
     *
     * @return OrderTransactionIfc
     */
    public OrderTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Sets the serialized line items in cargo.
     *
     * @param value as AbstractTransactionLineItemIfc[]
     */
    public void setSerializedItems(AbstractTransactionLineItemIfc[] value)
    {
        serializedItems = value;
    }

    /**
     * Gets the serialized line items from the cargo.
     *
     * @return AbstractTransactionLineItemIfc[]
     */
    public AbstractTransactionLineItemIfc[] getSerializedItems()
    {
        return serializedItems;
    }

    /**
     * Sets the serialized line items counter in cargo.
     *
     * @param value as int
     */
    public void setSerializedItemsCounter(int value)
    {
        serializedItemsCounter = value;
    }

    /**
     * Gets the serialized line items counter from the cargo.
     *
     * @return serializedItemsCounter as int
     */
    public int getSerializedItemsCounter()
    {
        return serializedItemsCounter;
    }

    /**
     * Get the tenderable transaction
     *
     * @return TenderableTransactionIfc
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return transaction;
    }

    /**
     * Get the till id
     *
     * @return String
     */
    public String getTillID()
    {
        return (getRegister().getCurrentTillID());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc#getRetailTransaction()
     */
    @Override
    public RetailTransactionIfc getRetailTransaction()
    {
        return transaction;
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     *
     * @return SaleReturnTransaction[]
     */
    public void setOriginalReturnTransactions(SaleReturnTransactionIfc[] origTxns)
    {
        originalReturnTransactions = new ArrayList<SaleReturnTransactionIfc>();
        for (int i = 0; (origTxns != null) && (i < origTxns.length); i++)
        {
            originalReturnTransactions.add(origTxns[i]);
        }
    }

    /**
     * Add a transaction to the vector of transactions on which items have been
     * returned. This cargo does not track this data.
     *
     * @param transaction SaleReturnTransactionIfc
     */
    public void addOriginalReturnTransaction(SaleReturnTransactionIfc transaction)
    {
        // check to see if an array already exist; if not make one.
        if (originalReturnTransactions == null)
        {
            originalReturnTransactions = new ArrayList<SaleReturnTransactionIfc>();
        }
        else
        {
            // Check to see if this transaction is already in the array.
            // if so, remove the current reference.
            int size = originalReturnTransactions.size();
            for(int i = 0; i < size; i++)
            {
                SaleReturnTransactionIfc temp = originalReturnTransactions.get(i);
                if (areTransactionIDsTheSame(temp, transaction))
                {
                    originalReturnTransactions.remove(i);
                    // Stop the loop.
                    i = size;
                }
            }
        }
        originalReturnTransactions.add(transaction);
    }

    /**
     * Test the two SaleReturnTransactionIfc objects to see if they refer to the
     * same transaction.  Cannot use the equals because the numbers of returned
     * items in the SaleReturnLineItems will not be the same.
     *
     * @param tran1 Transaction to compare against trans2
     * @param tran2 Transaction to compare against trans1
     *
     * @return boolean true if the transaction objects refer to the same transaction.
     */
    static public boolean areTransactionIDsTheSame(SaleReturnTransactionIfc tran1,
            SaleReturnTransactionIfc tran2)
    {
        boolean theSame = false;
        if (Util.isObjectEqual(tran1.getTransactionIdentifier(),
                tran2.getTransactionIdentifier()) &&
                Util.isObjectEqual(tran1.getBusinessDay(),
                        tran2.getBusinessDay()))
        {
            theSame = true;
        }
        return theSame;
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     *
     * @return SaleReturnTransaction[]
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        if (originalReturnTransactions != null)
        {
            return originalReturnTransactions.toArray(new SaleReturnTransactionIfc[originalReturnTransactions.size()]);
        }
        return null;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + getClass().getName() + "(Revision " + getRevisionNumber() + ")"
                + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
