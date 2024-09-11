/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/ReturnFindTransCargo.java /main/21 2014/03/11 17:13:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/11/14 - add support for returning ASA ordered items
 *    sgu       03/13/13 - rename to order summary
 *    sgu       03/07/13 - handle multiple return orders
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Pre code reveiw clean up.
 *    jswan     05/11/10 - Returns flow refactor: deprecated obsolete data
 *                         members and methods.
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abhayg    03/02/10 - For fixing the issue when user entere the reciept no
 *                         which is having last 4 charcters alphabet
 *  
 *    
 *    
 *    abondala  01/03/10 - update header date
 *    cgreene   03/20/09 - keep kit components off receipts by implementing new
 *                         method getLineItemsExceptExclusions
 *    atirkey   01/12/09 - added logic for sale return item level information
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/25/2008 5:14:26 AM   Sujay Beesnalli
 *         Forward ported from v12x. Modified to allow for the retrieval of
 *         and selection from multple transactions.
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse
 *
 *   Revision 1.6  2004/03/10 14:16:47  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.5  2004/02/24 15:15:34  baa
 *   @scr 3561 returns enter item
 *
 *   Revision 1.4  2004/02/13 22:46:22  baa
 *   @scr 3561 Returns - capture tender options on original trans.
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 29 2003 15:36:14   baa
 * return enhancements
 *
 *    Rev 1.0   Aug 29 2003 16:06:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:05:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:44   msg
 * Initial revision.
 *
 *    Rev 1.1   10 Dec 2001 12:28:44   jbp
 * Added getTransaction to ReturnTransactionCargoIfc and abstracted the functionalty to AbstractFindTransactionCargo
 * Resolution for POS SCR-418: Return Updates
 *
 *    Rev 1.0   Sep 21 2001 11:24:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.transaction.ItemSummaryIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractFindTransactionCargo;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnExternalOrderItemsCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnTransactionsCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnableItemCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Cargo for the Find Transaction By ID service.
 */
public class ReturnFindTransCargo extends AbstractFindTransactionCargo implements ReturnTransactionsCargoIfc,
        DBErrorCargoIfc, ReturnableItemCargoIfc, ReturnExternalOrderItemsCargoIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 6267575668370321627L;

    public static final String NUMBER_TYPE_RECEIPT_TAG = "NumberTypeReceipt";

    /**
     * Number type receipt bundle tag.
     */
    public static final String NUMBER_TYPE_RECEIPT_TEXT = "receipt";

    /**
     * Indicates the return search is being performed with an unknown tender
     * type.
     */
    public static final int RETURN_TENDER_UNKNOWN = 0;

    /**
     * Indicates the return search is being performed against the credit card
     * tender type.
     */
    public static final int RETURN_TENDER_CREDIT = 1;

    /**
     * Indicates the return search is being performed against the check tender
     * type.
     */
    public static final int RETURN_TENDER_CHECK = 2;

    /**
     * Provides a list representing an empty list of transaction summaries.
     */
    protected static final TransactionSummaryIfc[] EMPTY_LIST = new TransactionSummaryIfc[0];

    /**
     * Represents the value for an unselected index.
     */
    protected static final int UNSELECTED_INDEX = -1;

    /**
     * indicates printer input (e.g. MICR) should be ignored
     */
    protected boolean printerIgnored = false;

    /**
     * The list of transactions retrieved from the DB
     */
    protected Vector<SaleReturnTransactionIfc> transactions = new Vector<SaleReturnTransactionIfc>();

    /**
     * The list of orders retrieved from the DB
     */
    protected Vector<OrderSummaryEntryIfc> orderSummaries = new Vector<OrderSummaryEntryIfc>();

    /**
     * this flag is set when the child should transfer its Cargo to the parent's
     * Cargo
     */
    protected boolean transferCargo = true;

    /**
     * The error code, if any, generated by DB lookup
     */
    protected int dataExceptionErrorCode = DataException.NONE;

    /**
     * The text on the Receipt and Other Number buttons will be used later
     * @deprecated in 13.3 no longer used.
     */
    protected String numberTypeText = null;

    /**
     * Contains all data for return items; passed from a child service to the
     * parent service.
     */
    protected ReturnData returnData = null;

    /**
     * CustomerIfc object
     */
    protected CustomerIfc customer = null;

    /**
     * Represents the tender type being utilized to perform the search for
     * returns by tender.
     */
    private int m_returnTenderType;

    /**
     * Current list of available transaction summary objects.
     */
    private TransactionSummaryIfc[] m_transactionSummary = EMPTY_LIST;

    /**
     * List of previously utilized transaction summary lists being maintained in
     * the event a roll back is requested and the prior list is now to become
     * active within the cargo.
     */
    private List<TransactionSummaryIfc> previousTransactionSummaries = new ArrayList<TransactionSummaryIfc>();

    /**
     * summary list selection index
     */
    private int selectedIndex = UNSELECTED_INDEX;

    /**
     * Class Constructor.
     */
    public ReturnFindTransCargo()
    {
    }

    /**
     * Sets the transfer cargo flag.
     * 
     * @param value the transfer cargo flag
     */
    public void setTransferCargo(boolean value)
    {
        transferCargo = value;
    }

    /**
     * Gets the transfer cargo flag.
     * 
     * @return the transfer cargo flag
     */
    public boolean getTransferCargo()
    {
        return (transferCargo);
    }

    /**
     * Returns the error code returned with a DataException.
     * 
     * @return the integer value
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the error code returned with a DataException.
     * 
     * @param value integer value
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Resets the transactions vector.
     */
    public void resetTransactions()
    {
        transactions = new Vector<SaleReturnTransactionIfc>();
    }

    /**
     * Adds a transaction to the transactions vector.
     * 
     * @param transaction a sales return transaction
     */
    public void addToTransactions(SaleReturnTransactionIfc transaction)
    {
        transactions.addElement(transaction);
    }

    /**
     * Gets a transaction from a specific index
     * 
     * @param index the index.
     * @return SaleReturnTransactionIfc a sales return transaction
     */
    public SaleReturnTransactionIfc getTransactionFromCollection(int index)
    {
        return ((SaleReturnTransactionIfc)transactions.get(index));
    }

    /**
     * Gets the list of tranactions
     * 
     * @return SaleReturnTransactionIfc a sales return transaction
     */
    public SaleReturnTransactionIfc[] getTransactions()
    {
        return transactions.toArray(new SaleReturnTransactionIfc[transactions.size()]);
    }

    /**
     * Gets order summaries
     * 
     * @return order summaries
     */
    public OrderSummaryEntryIfc[] getOrderSummaries()
    {
        OrderSummaryEntryIfc[] results = orderSummaries.toArray(new OrderSummaryEntryIfc[orderSummaries.size()]);
        return results;
    }

    /**
     * Sets order summaries
     * 
     * @param orders order summaries
     */
    public void setOrderSummaries(OrderSummaryEntryIfc[] orders)
    {
        this.orderSummaries.clear();
        for (OrderSummaryEntryIfc order : orders)
        {
            this.orderSummaries.add(order);
        }
    }

    /**
     * Gets an order from a specific index
     * 
     * @param index the index
     * @return the order
     */
    public OrderSummaryEntryIfc getOrderSummaryFromCollection(int index)
    {
        return ((OrderSummaryEntryIfc)orderSummaries.elementAt(index));
    }

    /**
     * Converts the vector of transactions to an array of transaction summaries.
     * The used to load the model for the transaction list bean. @ return
     * TransactionSummaryIfc[] the array of sales return transactions
     */
    public TransactionSummaryIfc[] getTransactionSummary()
    {
        // Removed check for null on m_transactionSummary; This data member
        // has been initialized for other purposes. Additionally, it
        // would be possible to retrieve a different set of transaction and
        // this variable would never be re-initialized with the new set of data.
        m_transactionSummary = new TransactionSummaryIfc[transactions.size()];
        for (int i = 0; i < transactions.size(); i++)
        {
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)transactions.elementAt(i);
            TransactionSummaryIfc summary = DomainGateway.getFactory().getTransactionSummaryInstance();
            TransactionIDIfc id = DomainGateway.getFactory().getTransactionIDInstance();
            id.setTransactionID(trans.getTransactionID());
            summary.setTransactionID(id);
            summary.setTransactionType(trans.getTransactionType());
            summary.setTransactionStatus(trans.getTransactionStatus());
            summary.setBusinessDate(trans.getBusinessDay());
            summary.setStore(trans.getWorkstation().getStore());
            if (trans.getLineItemsVector() != null && trans.getLineItemsVector().size() > 0)
            {
                SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)trans.getLineItemsVector().elementAt(0);
                summary.setLocalizedDescriptions(item.getLocalizedItemDescriptions());
                Vector<AbstractTransactionLineItemIfc> lineItems = trans.getLineItemsVector();
                ItemSummaryIfc[] lineItemSummary = new ItemSummaryIfc[lineItems.size()];
                for (int j = 0; j < lineItems.size(); j++)
                {
                    SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems.get(j);
                    lineItemSummary[j] = DomainGateway.getFactory().getItemSummaryInstance();
                    lineItemSummary[j].setUnitsSold(lineItem.getItemQuantityDecimal());
                }
                summary.setItemSummaries(lineItemSummary);
            }
            summary.setTransactionGrandTotal(trans.getTransactionTotals().getGrandTotal());
            m_transactionSummary[i] = summary;
        }

        return m_transactionSummary;
    }

    /**
     * Sets transaction summary list.
     * 
     * @param value summary list
     */
    public void setTransactionSummary(TransactionSummaryIfc[] value)
    {
        setTransactionSummaries(value);
    }

    /**
     * Provides access to the type of tender being utilized to perform the
     * search.
     * 
     * @return an <code>int</code> representing the tender type.
     * @see #RETURN_TENDER_CREDIT
     * @see #RETURN_TENDER_CHECK
     */
    public int getReturnTenderType()
    {
        return m_returnTenderType;
    }

    /**
     * Method which sets the value determining the type of tender being utilized
     * to perform the return search.
     * 
     * @param type value representing the tender type.
     * @see #RETURN_TENDER_CREDIT
     * @see #RETURN_TENDER_CHECK
     */
    public void setReturnTenderType(int type)
    {
        m_returnTenderType = type;
    }

    /**
     * Sets index of selected transaction.
     * 
     * @param value index of selected transaction
     */
    public void setSelectedIndex(int value)
    {
        selectedIndex = value;
    }

    /**
     * Returns index of selected transaction.
     * 
     * @return index of selected transaction
     */
    public int getSelectedIndex()
    {
        return (selectedIndex);
    }

    /**
     * Returns the numberTypeText.
     * 
     * @return String
     * @deprecated in 13.3 no longer used
     */
    public String getNumberTypeText()
    {
        // If numberTypeText is null load the default value.
        if (numberTypeText == null)
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc) Gateway.getDispatcher().getManager(
                    UtilityManagerIfc.TYPE);
            numberTypeText =
                utility.retrieveText(
                    POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                    BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                    NUMBER_TYPE_RECEIPT_TAG,
                    NUMBER_TYPE_RECEIPT_TEXT);
        }

        return numberTypeText;
    }

    /**
     * Sets the numberTypeText.
     * 
     * @param value
     * @deprecated in 13.1 no longer used
     */
    public void setNumberTypeText(String value)
    {
        numberTypeText = value;
    }

    /**
     * Returns the returnData.
     * 
     * @return ReturnData
     */
    public ReturnData getReturnData()
    {
        return returnData;
    }

    /**
     * Sets the returnData.
     * 
     * @param value the return data
     */
    public void setReturnData(ReturnData value)
    {
        returnData = value;
    }

    /**
     * Retrieves the current customer.
     * 
     * @return CustomerIfc
     */
    public CustomerIfc getCustomer()
    {
        return (customer);
    }

    /**
     * Sets Customer the current customer
     * 
     * @param value the customer
     */
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Provides access to the currently selected transaction summary specific to
     * the return tender. If a member of the list has not been selected then a
     * null value will be provided.
     * 
     * @return the currently selected transaction summary member.
     */
    public TransactionSummaryIfc getSelectedTransactionSummary()
    {
        return m_transactionSummary[selectedIndex];
    }

    /**
     * Provides the currently active list of transaction summaries specific to
     * the return tender.
     * 
     * @return an array of active transaction summary objects.
     */
    public TransactionSummaryIfc[] getTransactionSummaries()
    {
        return m_transactionSummary;
    }

    /**
     * Provides a mechanism to set the currently active list of return tender
     * transaction summaries. This will also insure any previously maintained
     * lists have been discarded.
     * 
     * @param summary the new list of active transaction summaries.
     */
    public void setTransactionSummaries(TransactionSummaryIfc[] summary)
    {
        setTransactionSummaries(summary, false);
    }

    /**
     * Provides a mechanism to set the currently active list of return tender
     * transaction summaries. If the <code>keep</code> parameter is true then
     * the current active list will be maintained within an archive. If the
     * value is false then the archive is reset.
     * 
     * @param summary the new list of active transaction summaries.
     * @param keep a <code>boolean</code> value indicating if the old list
     *            should be archived.
     */
    public void setTransactionSummaries(TransactionSummaryIfc[] summary, boolean keep)
    {
        if (keep)
        {
            previousTransactionSummaries.clear();
            for (TransactionSummaryIfc sum : m_transactionSummary)
            {
                previousTransactionSummaries.add(sum);
            }
        }
        else
        {
            previousTransactionSummaries.clear();
        }

        selectedIndex = UNSELECTED_INDEX;
        m_transactionSummary = summary;
    }

    /**
     * Provides the index of the currently selected transaction summary within
     * the active list.
     * 
     * @return an <code>int</code> value representing the index of the selected
     *         transaction summary.
     */
    public int getSelectedSummaryIndex()
    {
        return selectedIndex;
    }

    /**
     * Provides mechanism to set the index of the currently selected transaction
     * summary within the active list.
     * 
     * @param index an <code>int</code> value representing the index of the
     *            selected transaction summary.
     */
    public void setSelectedSummaryIndex(int index)
    {
        selectedIndex = index;
    }

    /**
     * Indicates if a transaction summary member has been selected from the
     * currently active list.
     * 
     * @return true = selected, false = unselected
     */
    public boolean hasSelectedTransactionSummary()
    {
        return (selectedIndex > UNSELECTED_INDEX);
    }

    /**
     * Indicates if an archive of past transaction summary lists are being
     * maintained within the cargo.
     * 
     * @return true = maintains archive of transaction summary list, false =
     *         does not maintain archive.
     */
    public boolean hasPreviousSummaryList()
    {
        return (previousTransactionSummaries.size() > 0);
    }

    /**
     * Restores the prior transaction summary list as the active summary list
     * and discards the currently active list. Under all circumstances this
     * method will at least reset the selection index.
     */
    public void rollbackSummary()
    {
        if (hasPreviousSummaryList())
        {
            m_transactionSummary = new TransactionSummaryIfc[previousTransactionSummaries.size()];
            m_transactionSummary = (TransactionSummaryIfc[])previousTransactionSummaries.toArray();
        }

        selectedIndex = UNSELECTED_INDEX;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.returns.returncommon.AbstractFindTransactionCargo#toString()
     */
    @Override
    public String toString()
    {
      // result string
        String strResult = new String("Class:  ReturnFindTransCargo (Revision " + getRevisionNumber() + ")"
                + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}