/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/LayawayCargo.java /main/18 2014/05/07 17:05:41 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     05/07/14 - Forward port fix for not item serial number not
 *                         being captured.
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  09/20/10 - refractored code to use a single class for checking
 *                         cash in drawer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    nkgautam  02/01/10 - implemented methods for cash drawer warnings
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/16/09 - Code review comments
 *    nkgautam  12/15/09 - Added SerialisationEnabled boolean and LineItem as
 *                         Cargo Attributes
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    5    360Commerce 1.4         2/24/2008 2:51:06 PM   Pardee Chhabra  CR
 *         30468:Tender refund options are not displayed as per specification
 *         for layaway delete feature.
 *    4    360Commerce 1.3         4/25/2007 8:52:25 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:15 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/27 22:32:05  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:50:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 19 2004 15:43:56   DCobb
 * Removed unused imports.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 *
 *    Rev 1.0   Aug 29 2003 16:00:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:19:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:20:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway;

import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.layaway.find.FindLayawayCargoIfc;
import oracle.retail.stores.pos.services.layaway.payment.LayawayPaymentCargoIfc;

/**
 * Data and methods common to the sites in Layaway Services.
 * 
 * @version $Revision: /main/18 $
 */
public class LayawayCargo extends AbstractFinancialCargo implements LayawayCargoIfc, FindLayawayCargoIfc,
        LayawayPaymentCargoIfc, RetailTransactionCargoIfc
{
    private static final long serialVersionUID = 3885788182061751296L;

    /**
     * revision number
     */
    public static String revisionNumber = "$Revision: /main/18 $";

    /**
     * current layaway search method flag
     */
    protected int layawaySearchMethod = -1; // not set yet

    /**
     * current layaway operation flag
     */
    protected int layawayOperation = LAYAWAY_UNDEFINED; // not set yet

    /**
     * current layaway id to search
     */
    protected String layawaySearchID = ""; // not set yet

    /**
     * List of Layaway Summary Entries to display
     */
    protected LayawaySummaryEntryIfc[] layawaySummaryEntryList = null;

    /**
     * Selected Layaway Summary Entries in list of same
     */
    protected int selectedLayawayIndex = -1;

    /**
     * current layaway customer
     */
    protected CustomerIfc customer = null;

    /**
     * Sales associate for the layaway transaction
     */
    protected EmployeeIfc salesAssociate = null;

    /**
     * current layaway transaction
     */
    protected LayawayIfc layaway = null;

    /**
     * origional layaway transaction
     */
    protected LayawayTransactionIfc initialLayawayTransaction = null;

    /**
     * origional layaway transaction
     */
    protected TransactionIfc seedTransaction = null;

    /**
     * sale transaction in progress
     */
    protected SaleReturnTransactionIfc saleTransaction = null;

    /**
     * payment object
     */
    protected PaymentIfc payment = null;

    /**
     * final payment initialized to false
     */
    protected boolean finalPayment = false;

    /**
     * first run initialized to true
     */
    protected boolean firstRun = true;

    /**
     * payment flag initialized to false
     */
    protected boolean paymentFlag = false;

    /**
     * Total prior to creation fee adition
     */
    protected CurrencyIfc preCreationFeeTotal;

    /**
     * tenderable transaction in progress
     */
    protected TenderableTransactionIfc transaction = null;

    /**
     * For refund options
     */
    private Vector<SaleReturnTransactionIfc> originalReturnTransactions;

    // Serialized line items saved in the cargo
    protected AbstractTransactionLineItemIfc[] serializedItems = null;

    // Serialized line items counter
    protected int serializedItemsCounter = 0;

    /**
     * Serialization enabled boolean
     */
    protected boolean isSerializationEnabled;

    /**
     * Current Line Item
     */
    protected SaleReturnLineItemIfc lineItem;

    /**
     * Gets the Serialisation enabled boolean
     * 
     * @return
     */
    public boolean isSerializationEnabled()
    {
        return isSerializationEnabled;
    }

    /**
     * Sets the Serialisation boolean
     * 
     * @param isSerializationEnabled
     */
    public void setSerializationEnabled(boolean isSerializationEnabled)
    {
        this.isSerializationEnabled = isSerializationEnabled;
    }

    /**
     * Gets the Current Line Item
     * 
     * @return
     */
    public SaleReturnLineItemIfc getLineItem()
    {
        return lineItem;
    }

    /**
     * Sets the current line Item
     * 
     * @param lineItem
     */
    public void setLineItem(SaleReturnLineItemIfc lineItem)
    {
        this.lineItem = lineItem;
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
     * Sets the layaway search method.
     * 
     * @param name int value of the searchMethod
     */
    public void setLayawaySearch(int name)
    {
        layawaySearchMethod = name;
    }

    /**
     * Gets the layaway search method.
     * 
     * @return int representation of the searchMethod
     */
    public int getLayawaySearch()
    {
        return layawaySearchMethod;
    }

    /**
     * Sets the layaway operation.
     * 
     * @param name int value of the layaway operation
     */
    public void setLayawayOperation(int name)
    {
        layawayOperation = name;
    }

    /**
     * Gets the value of layaway operation.
     * 
     * @return int representation of the layaway operation
     */
    public int getLayawayOperation()
    {
        return layawayOperation;
    }

    /**
     * Gets the layaway customer.
     * 
     * @return Customer
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * Sets the layaway customer.
     * 
     * @param Customer
     */
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Gets the layaway id for searches.
     *
     * @return layaway id
     */
    public String getLayawaySearchID()
    {
        return layawaySearchID;
    }

    /**
     * Sets the layaway id for searches.
     *
     * @param layaway id
     */
    public void setLayawaySearchID(String value)
    {
        layawaySearchID = value;
    }

    /**
     * Gets the list of LayawaySummaryEntryIfcs.
     *
     * @return list of LayawaySummaryEntryIfcs
     */
    public LayawaySummaryEntryIfc[] getLayawaySummaryEntryList()
    {
        return layawaySummaryEntryList;
    }

    /**
     * Sets the list of LayawaySummaryEntryIfcs.
     *
     * @param values list of LayawaySummaryEntryIfcs
     */
    public void setLayawaySummaryEntryList(LayawaySummaryEntryIfc[] values)
    {
        layawaySummaryEntryList = values;
    }

    /**
     * Gets the index of the selected LayawaySummaryEntryIfc.
     *
     * @return index of the selected LayawaySummaryEntryIfc
     */
    public int getSelectedLayawayIndex()
    {
        return selectedLayawayIndex;
    }

    /**
     * Sets the index of the selected LayawaySummaryEntryIfc.
     *
     * @param value index of the selected LayawaySummaryEntryIfc
     */
    public void setSelectedLayawayIndex(int value)
    {
        selectedLayawayIndex = value;
    }

    /**
     * Gets the current layaway.
     *
     * @return layaway
     */
    public LayawayIfc getLayaway()
    {
        return layaway;
    }

    /**
     * Sets the current layaway.
     *
     * @param layaway
     */
    public void setLayaway(LayawayIfc value)
    {
        layaway = value;
    }

    /**
     * Gets the current sale tansaction.
     *
     * @return sale transaction
     */
    public SaleReturnTransactionIfc getSaleTransaction()
    {
        return saleTransaction;
    }

    /**
     * Sets the sale transaction.
     *
     * @param sale transaction
     */
    public void setSaleTransaction(SaleReturnTransactionIfc value)
    {
        saleTransaction = value;
    }

    /**
     * Sets the sales associate.
     *
     * @param EmployeeIfc the sales associate
     */
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

    /**
     * Gets the sales associate.
     *
     * @return EmployeeIfc sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Resets the layaway cargo attributes to null.
     */
    public void resetLayawayCargo()
    {
        customer = null;
        initialLayawayTransaction = null;
        saleTransaction = null;
        setDataExceptionErrorCode(DataException.NONE);
        layawaySearchMethod = -1;
        layawayOperation = -1;
        layawaySearchID = "";
        layawaySummaryEntryList = null;
        selectedLayawayIndex = -1;
        payment = null;
        finalPayment = false;
        firstRun = true;
        paymentFlag = false;
        preCreationFeeTotal = null;
        transaction = null;
        seedTransaction = null;
    }

    /**
     * Gets the current payment object.
     *
     * @return payment
     */
    public PaymentIfc getPayment()
    {
        return payment;
    }

    /**
     * Sets the payment object.
     *
     * @param payment
     */
    public void setPayment(PaymentIfc value)
    {
        this.payment = value;
    }

    /**
     * Gets the final payment.
     *
     * @return boolean final payment
     */
    public boolean isFinalPayment()
    {
        return finalPayment;
    }

    /**
     * Sets the final payment.
     *
     * @param boolean finalPayment
     */
    public void setFinalPayment(boolean value)
    {
        this.finalPayment = value;
    }

    /**
     * Gets the firstRun.
     *
     * @return boolean first run
     */
    public boolean isFirstRun()
    {
        return firstRun;
    }

    /**
     * Sets the firstRun.
     *
     * @param boolean firstRun
     */
    public void setFirstRun(boolean value)
    {
        this.firstRun = value;
    }

    /**
     * Gets the paymentFlag.
     *
     * @return boolean paymentFlag
     */
    public boolean isPaymentFlag()
    {
        return paymentFlag;
    }

    /**
     * Sets the paymentFlag.
     *
     * @param boolean paymentFlag
     */
    public void setPaymentFlag(boolean value)
    {
        this.paymentFlag = value;
    }

    /**
     * Gets the preCreationFeeTotal.
     *
     * @return CurrencyIfc preCreationFeeTotal
     */
    public CurrencyIfc getPreCreationFeeTotal()
    {
        return preCreationFeeTotal;
    }

    /**
     * Sets the preCreationFeeTotal.
     *
     * @param CurrencyIfc preCreationFeeTotal
     */
    public void setPreCreationFeeTotal(CurrencyIfc value)
    {
        this.preCreationFeeTotal = value;
    }

    /**
     * Gets the current tenderable tansaction.
     *
     * @return tenderable transaction
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return transaction;
    }

    /**
     * Gets the current tenderable tansaction.
     *
     * @return tenderable transaction
     */
    public TenderableTransactionIfc getTransaction()
    {
        return getTenderableTransaction();
    }

    /**
     * Retrieves the saved transaction
     * 
     * @return the RetailTransactionIfc that is being printed
     */
    public TenderableTransactionIfc getTransactionIfc()
    {
        return (getTenderableTransaction());
    }

    /**
     * Retrieve the till ID.
     * 
     * @return String till ID
     */
    public String getTillID()
    {
        return (transaction.getTillID());
    }

    /**
     * Sets the tenderable transaction.
     *
     * @param tenderable transaction
     */
    public void setTransaction(TenderableTransactionIfc value)
    {
        setTenderableTransaction(value);
    }

    /**
     * Sets the tenderable transaction.
     *
     * @param tenderable transaction
     */
    public void setTenderableTransaction(TenderableTransactionIfc value)
    {
        transaction = value;
    }

    /**
     * Retrieves the saved transaction
     * 
     * @return the RetailTransactionIfc that is being printed
     */
    public RetailTransactionIfc getRetailTransaction()
    {
        return ((RetailTransactionIfc) getTenderableTransaction());
    }

    /**
     * Sets the initial Layaway Transaction.
     *
     * @param LayawayTransactionIfc
     */
    public void setInitialLayawayTransaction(LayawayTransactionIfc value)
    {
        initialLayawayTransaction = value;
        if (initialLayawayTransaction != null)
        {
            setLayaway(initialLayawayTransaction.getLayaway());
        }
    }

    /**
     * Retrieves the initial layaway transactoin
     * 
     * @return LayawayTransactoinIfc
     */
    public LayawayTransactionIfc getInitialLayawayTransaction()
    {
        return (initialLayawayTransaction);
    }

    /**
     * Retrieves the seed transactoin
     * 
     * @return TransactionIfc
     */
    public TransactionIfc getSeedLayawayTransaction()
    {
        return (seedTransaction);
    }

    /**
     * Sets the seed Transaction.
     *
     * @param LayawayTransactionIfc
     */
    public void setSeedLayawayTransaction(TransactionIfc value)
    {
        seedTransaction = value;
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     * 
     * @return SaleReturnTransaction[]
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        SaleReturnTransactionIfc[] transactions = null;

        if (originalReturnTransactions != null)
        {
            transactions = new SaleReturnTransactionIfc[originalReturnTransactions.size()];
            originalReturnTransactions.copyInto(transactions);
        }
        return transactions;
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     * 
     * @return SaleReturnTransaction[]
     */
    public void setOriginalReturnTransactions(SaleReturnTransactionIfc[] origTxns)
    {
        originalReturnTransactions = new Vector<SaleReturnTransactionIfc>();
        for (int i = 0; (origTxns != null) && (i < origTxns.length); i++)
        {
            originalReturnTransactions.addElement(origTxns[i]);
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
            originalReturnTransactions = new Vector<SaleReturnTransactionIfc>();
        }
        else
        {
            // Check to see if this transaction is already in the array.
            // if so, remove the current reference.
            int size = originalReturnTransactions.size();
            for (int i = 0; i < size; i++)
            {
                SaleReturnTransactionIfc temp = originalReturnTransactions.elementAt(i);
                if (areTransactionIDsTheSame(temp, transaction))
                {
                    originalReturnTransactions.removeElementAt(i);
                    // Stop the loop.
                    i = size;
                }
            }
        }
        originalReturnTransactions.addElement(transaction);
    }

    /**
     * Test the two SaleReturnTransactionIfc objects to see if they refer to the
     * same transaction. Cannot use the equals because the numbers of returned
     * items in the SaleReturnLineItems will not be the same.
     * 
     * @param tran1 Transaction to compare against trans2
     * @param tran2 Transaction to compare against trans1
     * @return boolean true if the transaction objects refer to the same
     *         transaction.
     */
    static public boolean areTransactionIDsTheSame(SaleReturnTransactionIfc tran1, SaleReturnTransactionIfc tran2)
    {
        boolean theSame = false;
        if (Util.isObjectEqual(tran1.getTransactionIdentifier(), tran2.getTransactionIdentifier())
                && Util.isObjectEqual(tran1.getBusinessDay(), tran2.getBusinessDay()))
        {
            theSame = true;
        }
        return theSame;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("LayawayCargo", getRevisionNumber(), hashCode());
        strResult.append(Util.formatToStringEntry("layawaySearchMethod ", getLayawaySearch()));
        strResult.append(Util.formatToStringEntry("layawayOperation ", getLayawayOperation()));
        if (getLayawaySearchID() == null)
        {
            strResult.append(Util.formatToStringEntry("layawaySearchID", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("layawaySearchID", getLayawaySearchID()));
        }
        if (getCustomer() == null)
        {
            strResult.append(Util.formatToStringEntry("customer", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("customer", getCustomer()));
        }
        if (getSalesAssociate() == null)
        {
            strResult.append(Util.formatToStringEntry("salesAssociate", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("salesAssociate", getSalesAssociate()));
        }
        if (getInitialLayawayTransaction() == null)
        {
            strResult.append(Util.formatToStringEntry("initialLayawayTransaction", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("initialLlayawayTransaction", getInitialLayawayTransaction()));
        }
        if (getSaleTransaction() == null)
        {
            strResult.append(Util.formatToStringEntry("saleTransaction", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("saleTransaction", getSaleTransaction()));
        }
        // pass back result
        return (strResult.toString());

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