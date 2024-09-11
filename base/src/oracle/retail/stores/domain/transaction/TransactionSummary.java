/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TransactionSummary.java /main/21 2013/07/11 12:22:00 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  07/10/13 - Fix to display first item description for each
 *                         suspended txns
 *    rgour     04/01/13 - CBR cleanup
 *    rsnayak   03/22/12 - cross border return changes
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *    ohorne    08/10/11 - masked aba and account number for check
 *    acadar    10/27/10 - changes for resetting the order status in Siebel
 *    cgreene   10/22/10 - update to use java.lang.Comparable3
 *    nkgautam  07/02/10 - bill pay report changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       10/30/08 - refactor layaway and transaction summary object to
 *                         take localized text
 *    mdecama   10/28/08 - I18N - Refactoring Transaction Suspend Reasons
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.BillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.AbstractRoutable;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class describes a summary of a transaction. It is used for displaying
 * lists of transaction for activities such as returns and retrieval of
 * suspended transactions.
 *
 * @version $Revision: /main/21 $
 */
public class TransactionSummary extends AbstractRoutable
    implements TransactionSummaryIfc, Comparable<TransactionSummary>
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -6674408937314393546L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/21 $";

    /**
     * transaction identifier
     */
    protected TransactionIDIfc transactionID = null;

    /**
     * transaction type
     *
     * @see oracle.retail.stores.domain.transaction.TransactionIfc#TYPE_UNKNOWN
     */
    protected int transactionType = TransactionIfc.TYPE_UNKNOWN;

    /**
     * transaction status
     *
     * @see oracle.retail.stores.domain.transaction.TransactionIfc#STATUS_IN_PROGRESS
     */
    protected int transactionStatus = TransactionIfc.STATUS_IN_PROGRESS;

    /**
     * business date
     */
    protected EYSDate businessDate = null;

    /**
     * store
     */
    protected StoreIfc store = null;

    /**
     * description of item on transaction (nominally the first item)
     */
    protected LocalizedTextIfc descriptions = DomainGateway.getFactory().getLocalizedText();

    /**
     * register identifier
     */
    protected String registerID = "";

    /**
     * till identifier
     */
    protected String tillID = "";

    /**
     * customer identifier
     */
    protected String customerID = "";

    /**
     * customer name
     */
    protected PersonNameIfc customerName = null;

    /**
     * grand total of the transaction
     */
    protected CurrencyIfc transactionGrandTotal = null;

    /**
     * tax total of the transaction
     */
    protected CurrencyIfc transactionTaxTotal = null;

    /**
     * inclusive tax total of the transaction
     */
    protected CurrencyIfc transactionInclusiveTaxTotal = null;

    /**
     * suspend reason
     */
    protected LocalizedCodeIfc suspendReason = DomainGateway.getFactory().getLocalizedCode();

    /**
     * cashier identifier
     */
    protected String cashierID = "";

    /**
     * training mode
     */
    protected boolean trainingMode = false;

    /**
     * training mode
     */
    protected boolean trainingModeUsedInQuery = true;

    /**
     * item summary list
     */
    protected List<ItemSummaryIfc> itemSummaryList = null;

    /**
     * Time that the transaction was ended.
     */
    protected EYSDate transactionTimestamp = null;

    /**
     * The locale requestor
     */
    protected LocaleRequestor localeRequestor = null;

    /*
     * List of Bills
     */
    protected List<BillIfc> billPaymentList = null;

    /**
     * External Order Id - if any
     */
    protected String externalOrderID = null;

    /**
     * External Order Id - if any
     */
    protected String internalOrderID = null;
    
    /**
     * External Order Id - if any
     */
    protected String layawayID = null;    

    /**
     * Constructs TransactionSummary object.
     */
    public TransactionSummary()
    {
        transactionGrandTotal = DomainGateway.getBaseCurrencyInstance();
        transactionTaxTotal = DomainGateway.getBaseCurrencyInstance();
        transactionInclusiveTaxTotal = DomainGateway.getBaseCurrencyInstance();
        localeRequestor = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }

    /**
     * Creates clone of this object.
     *
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        TransactionSummaryIfc c = new TransactionSummary();

        // set clone attributes
        setCloneAttributes(c);

        // pass back Object
        return c;
    }

    /**
     * Sets attributes in clone.
     *
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(TransactionSummaryIfc newClass)
    {
        // set values
        if (transactionID != null)
        {
            newClass.setTransactionID((TransactionIDIfc)transactionID.clone());
        }
        newClass.setTransactionType(transactionType);
        newClass.setTransactionStatus(transactionStatus);
        newClass.setSuspendReason(getSuspendReason());
        newClass.setTrainingMode(trainingMode);
        newClass.setTrainingModeUsedInQuery(trainingModeUsedInQuery);
        newClass.setInternalOrderID(internalOrderID);
        newClass.setLayawayID(layawayID);
        if (businessDate != null)
        {
            newClass.setBusinessDate((EYSDate)businessDate.clone());
        }
        if (store != null)
        {
            newClass.setStore((StoreIfc)store.clone());
        }
        if (itemSummaryList != null)
        {
            newClass.setItemSummaries(itemSummaryList.toArray(new ItemSummaryIfc[0]));
        }
        if (descriptions != null)
        {
            newClass.setLocalizedDescriptions((LocalizedTextIfc)descriptions.clone());
        }
        if (registerID != null)
        {
            newClass.setRegisterID(new String(registerID));
        }
        if (tillID != null)
        {
            newClass.setTillID(new String(tillID));
        }
        if (customerID != null)
        {
            newClass.setCustomerID(new String(customerID));
        }
        if (customerName != null)
        {
            newClass.setCustomerName((PersonNameIfc)customerName.clone());
        }
        if (transactionGrandTotal != null)
        {
            newClass.setTransactionGrandTotal((CurrencyIfc)transactionGrandTotal.clone());
        }
        if (transactionTaxTotal != null)
        {
            newClass.setTransactionTaxTotal((CurrencyIfc)transactionTaxTotal.clone());
        }
        if (transactionInclusiveTaxTotal != null)
        {
            newClass.setTransactionInclusiveTaxTotal((CurrencyIfc)transactionInclusiveTaxTotal.clone());
        }
        if (getCashierID() != null)
        {
            newClass.setCashierID(new String(getCashierID()));
        }
        if (getTransactionTimestamp() != null)
        {
            newClass.setTransactionTimestamp((EYSDate)getTransactionTimestamp().clone());
        }
        if (getExternalOrderID() != null)
        {
            newClass.setExternalOrderID(new String(getExternalOrderID()));
        }
        if (getLocaleRequestor() != null)
        {
            newClass.setLocaleRequestor((LocaleRequestor)localeRequestor.clone());
        }
    }

    /**
     * Determine if two objects are identical.

     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        // quick exit
        if (obj == this)
        {
            return true;
        }

        boolean isEqual = false; // set the return code to false

        try
        {
            TransactionSummary c = (TransactionSummary)obj;
            // compare all the attributes of TransactionSummary
            if (Util.isObjectEqual(getTransactionID(), c.getTransactionID())
                    && getTransactionType() == c.getTransactionType() 
                    && getTransactionStatus() == c.getTransactionStatus()
                    && isTrainingMode() == c.isTrainingMode()
                    && isTrainingModeUsedInQuery() == c.isTrainingModeUsedInQuery()
                    && Util.isObjectEqual(getSuspendReason(), c.getSuspendReason())
                    && Util.isObjectEqual(getBusinessDate(), c.getBusinessDate())
                    && Util.isObjectEqual(getTransactionTimestamp(), c.getTransactionTimestamp())
                    && Util.isObjectEqual(getStore(), c.getStore())
                    && Util.isObjectEqual(getLocalizedDescriptions(), c.getLocalizedDescriptions())
                    && Util.isObjectEqual(getRegisterID(), c.getRegisterID())
                    && Util.isObjectEqual(getTillID(), c.getTillID())
                    && Util.isObjectEqual(getCustomerID(), c.getCustomerID())
                    && Util.isObjectEqual(getCashierID(), c.getCashierID())
                    && Util.isObjectEqual(getCustomerName(), c.getCustomerName())
                    && Util.isObjectEqual(getTransactionTaxTotal(), c.getTransactionTaxTotal())
                    && Util.isObjectEqual(getTransactionInclusiveTaxTotal(), c.getTransactionInclusiveTaxTotal())
                    && Util.isObjectEqual(getLocaleRequestor(), c.getLocaleRequestor())
                    && Util.isObjectEqual(getInternalOrderID(), c.getInternalOrderID())
                    && Util.isObjectEqual(getLayawayID(), c.getLayawayID())
                    && Util.isObjectEqual(getTransactionGrandTotal(), c.getTransactionGrandTotal()))
            {
                isEqual = true; // set the return code to true
            }
        }
        catch (Exception e)
        {
            // ignore, leave false
        }
        return isEqual;
    }

    /**
     * Retrieves transaction identifier.
     *
     * @return transaction identifier
     */
    public TransactionIDIfc getTransactionID()
    {
        return transactionID;
    }

    /**
     * Sets transaction identifier.
     *
     * @param value transaction identifier
     */
    public void setTransactionID(TransactionIDIfc value)
    {
        transactionID = value;
    }

    /**
     * Retrieves transaction type.
     *
     * @return transaction type
     */
    public int getTransactionType()
    {
        return transactionType;
    }

    /**
     * Sets transaction type.
     *
     * @param value transaction type
     */
    public void setTransactionType(int value)
    {
        transactionType = value;
    }

    /**
     * Retrieves transaction status.
     *
     * @return transaction status
     */
    public int getTransactionStatus()
    {
        return transactionStatus;
    }

    /**
     * Sets transaction status.
     *
     * @param value transaction status
     */
    public void setTransactionStatus(int value)
    {
        transactionStatus = value;
    }

    /**
     * Retrieves business date.
     *
     * @return business date
     */
    public EYSDate getBusinessDate()
    {
        return businessDate;
    }

    /**
     * Sets business date.
     *
     * @param value business date
     */
    public void setBusinessDate(EYSDate value)
    {
        businessDate = value;
    }

    /**
     * Retrieves store.
     *
     * @return store
     */
    public StoreIfc getStore()
    {
        return store;
    }

    /**
     * Sets store.
     *
     * @param value store
     */
    public void setStore(StoreIfc value)
    {
        store = value;
    }

    /**
     * Retrieves description of item on transaction (nominally the first item).
     *
     * @return description of item on transaction (nominally the first item)
     * @deprecated As of 13.1 Use {@link TransactionSummary#getDescription(Locale)}
     */
    public String getDescription()
    {
        return getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves description. <P>
        @return description
    **/
    //----------------------------------------------------------------------------
    public String getDescription(Locale locale)
    {
    	return descriptions.getText(LocaleMap.getBestMatch(locale));
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves localized descriptions. <P>
        @return localized descriptions
    **/
    //----------------------------------------------------------------------------
    public LocalizedTextIfc getLocalizedDescriptions()
    {
    	return descriptions;
    }

    /**
     * Sets description of item on transaction (nominally the first item).
     *
     * @param value description of item on transaction (nominally the first
     *            item)
     * @deprecated As of 13.1 Use {@link TransactionSummary#setDescription(Locale, String)}
     */
    public void setDescription(String value)
    {
        setDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE), value);
    }

    //----------------------------------------------------------------------------
    /**
        Sets description. <P>
        @param value  description
    **/
    //----------------------------------------------------------------------------
    public void setDescription(Locale locale, String value)
    {
    	descriptions.putText(LocaleMap.getBestMatch(locale), value);
    }

    //----------------------------------------------------------------------------
    /**
        Sets localized descriptions. <P>
        @param value  localized descriptions
    **/
    //----------------------------------------------------------------------------
    public void setLocalizedDescriptions(LocalizedTextIfc value)
    {
    	descriptions = value;
    }

    /**
     * Retrieves register identifier.
     *
     * @return register identifier
     */
    public String getRegisterID()
    {
        return registerID;
    }

    /**
     * Sets register identifier.
     *
     * @param value register identifier
     */
    public void setRegisterID(String value)
    {
        registerID = value;
    }

    /**
     * Retrieves till identifier.
     *
     * @return till identifier
     */
    public String getTillID()
    {
        return tillID;
    }

    /**
     * Sets till identifier.
     *
     * @param value till identifier
     */
    public void setTillID(String value)
    {
        tillID = value;
    }

    /**
     * Retrieves customer identifier.
     *
     * @return customer identifier
     */
    public String getCustomerID()
    {
        return customerID;
    }

    /**
     * Sets customer identifier.
     *
     * @param value customer identifier
     */
    public void setCustomerID(String value)
    {
        customerID = value;
    }

    /**
     * Retrieves customer name.
     *
     * @return customer name
     */
    public PersonNameIfc getCustomerName()
    {
        return customerName;
    }

    /**
     * Sets customer name.
     *
     * @param value customer name
     */
    public void setCustomerName(PersonNameIfc value)
    {
        customerName = value;
    }

    /**
     * Retrieves grand total of the transaction.
     *
     * @return grand total of the transaction
     */
    public CurrencyIfc getTransactionGrandTotal()
    {
        return transactionGrandTotal;
    }

    /*(non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TransactionSummaryIfc#getTransactionTotal()
     */
    public CurrencyIfc getTransactionTotal()
    {
        return getTransactionGrandTotal().subtract(getTransactionTaxTotal());
    }


    /**
     * Sets grand total of the transaction.

     *
     * @param value grand total of the transaction
     */
    public void setTransactionGrandTotal(CurrencyIfc value)
    {
        transactionGrandTotal = value;
    }

    /**
     * Retrieves tax total of the transaction.
     *
     * @return tax total of the transaction
     */
    public CurrencyIfc getTransactionTaxTotal()
    {
        return transactionTaxTotal;
    }

    /**
     * Sets tax total of the transaction.
     *
     * @param value tax total of the transaction
     */
    public void setTransactionTaxTotal(CurrencyIfc value)
    {
        transactionTaxTotal = value;
    }

    /**
     * Retrieves inclusive tax total of the transaction.
     *
     * @return inclusive tax total of the transaction
     */
    public CurrencyIfc getTransactionInclusiveTaxTotal()
    {
        return transactionInclusiveTaxTotal;
    }

    /**
     * Sets inclusive tax total of the transaction.
     *
     * @param value inclusive tax total of the transaction
     */
    public void setTransactionInclusiveTaxTotal(CurrencyIfc value)
    {
        transactionInclusiveTaxTotal = value;
    }

    /**
     * Returns suspend reason code.
     *
     * @return suspend reason code
     * @deprecated as of 13.1 Use {@link #getSuspendReason()}
     */
    public int getSuspendReasonCode()
    {
        return Integer.valueOf(suspendReason.getCode());
    }

    /**
     * Sets suspend reason code.
     *
     * @param value suspend reason code
     * @deprecated as of 13.1 Use {@link #setSuspendReason(LocalizedCodeIfc)}
     */
    public void setSuspendReasonCode(int value)
    {
        suspendReason.setCode(Integer.toString(value));
    }

    /**
     * Retrieves the Suspend Transaction Reason
     * @return
     */
    public LocalizedCodeIfc getSuspendReason ()
    {
        return suspendReason;
    }

    /**
     * Sets the Suspend Transaction Reason
     * @param suspendReason
     */
    public void setSuspendReason (LocalizedCodeIfc suspendReason)
    {
        this.suspendReason = suspendReason;
    }

    /**
     * Retrieves cashier identifier.
     *
     * @return cashier identifier
     */
    public String getCashierID()
    {
        return cashierID;
    }

    /**
     * Sets cashier identifier.
     *
     * @param value cashier identifier
     */
    public void setCashierID(String value)
    {
        cashierID = value;
    }

    /**
     * Set training mode
     *
     * @param value training mode status
     */
    public void setTrainingMode(boolean value)
    {
        trainingMode = value;
    }

    /**
     * Get training mode
     *
     * @return true if in training mode, false otherwise
     */
    public boolean isTrainingMode()
    {
        return trainingMode;
    }

    /**
     * @return the trainingModeUsedInQuery
     */
    public boolean isTrainingModeUsedInQuery()
    {
        return trainingModeUsedInQuery;
    }

    /**
     * @param trainingModeUsedInQuery the trainingModeUsedInQuery to set
     */
    public void setTrainingModeUsedInQuery(boolean trainingModeUsedInQuery)
    {
        this.trainingModeUsedInQuery = trainingModeUsedInQuery;
    }

    /**
     * Retrieves a list of item summaries.
     *
     * @return list of item summaries
     */
    public ItemSummaryIfc[] getItemSummaries()
    {
        ItemSummaryIfc[] apr = null;
        if (itemSummaryList != null)
        {
            apr = new ItemSummaryIfc[itemSummaryList.size()];
            itemSummaryList.toArray(apr);
        }
        return apr;
    }

    /**
     * Sets a list of item summaries.
     *
     * @param value list of item summaries
     */
    public void setItemSummaries(ItemSummaryIfc[] value)
    {
        if (value != null)
        {
            if (itemSummaryList != null)
            {
                itemSummaryList.clear();
            }
            else
            {
                itemSummaryList = new ArrayList<ItemSummaryIfc>();
            }
            itemSummaryList.addAll(Arrays.asList(value));

        }
    }

    /**
     * Returns true if item summaries list exists, false otherwise
     *
     * @return true if item summaries list exists, false otherwise
     */
    public boolean hasItemSummaries()
    {
        boolean hasItems = false;
        if (itemSummaryList != null)
        {
            hasItems = !itemSummaryList.isEmpty();
        }
        return hasItems;
    }

    /**
     * gets the item summary iterator
     *
     * @return item summary iterator
     */
    public Iterator<ItemSummaryIfc> getItemSummaryIterator()
    {
        Iterator<ItemSummaryIfc> listIterator = null;
        if (itemSummaryList != null)
        {
            listIterator = itemSummaryList.iterator();
        }
        return listIterator;
    }

    /**
     * Adds a item summary to the collection of summaries
     *
     * @param value the item summary
     */
    public void addItemSummary(ItemSummaryIfc value)
    {
        if (itemSummaryList == null)
        {
            itemSummaryList = new ArrayList<ItemSummaryIfc>();
        }
        itemSummaryList.add(value);
    }

    /**
     * Adds a item summary to the collection of summaries.
     *
     * @param itemID item identifier
     * @param posItemID item as identified by the point-of-sale
     * @param description item description
     */
    public void addItemSummary(String itemID, String posItemID, String description)
    {
        ItemSummaryIfc summary = DomainGateway.getFactory().getItemSummaryInstance();
        summary.initialize(itemID, posItemID, description);
        addItemSummary(summary);
    }

    /**
     * Adds a item summary to the collection of summaries.
     *
     * @param itemID item identifier
     * @param posItemID item as identified by the point-of-sale
     * @param sequenceNumber line item sequence number
     * @param units units sold for that item
     * @deprecated as of 14.0.  Use addItemSummary with an additional arg description instead
     */
    public void addItemSummary(String itemID, String posItemID, int sequenceNumber, BigDecimal units)
    {
        addItemSummary(itemID,posItemID, sequenceNumber, units,null);
    }
   
    /**
     * Adds a item summary to the collection of summaries.
     *
     * @param itemID item identifier
     * @param posItemID item as identified by the point-of-sale
     * @param sequenceNumber line item sequence number
     * @param units units sold for that item
     * @param description item description
     */
    public void addItemSummary(String itemID, String posItemID, int sequenceNumber, BigDecimal units, String description)
    {
        ItemSummaryIfc summary = DomainGateway.getFactory().getItemSummaryInstance();
        summary.initialize(itemID, posItemID, sequenceNumber, units, description);
        addItemSummary(summary);
    }
    
    /**
     * Adds a item summary to the collection of summaries.
     *
     * @param itemID item identifier
     * @param posItemID item as identified by the point-of-sale
     * @param sequenceNumber line item sequence number
     */
    public void addItemSummary(String itemID, String posItemID, int sequenceNumber)
    {
        ItemSummaryIfc summary = DomainGateway.getFactory().getItemSummaryInstance();
        summary.initialize(itemID, posItemID, sequenceNumber);
        addItemSummary(summary);
    }

    /**
     * Returns true if item found in summary, false otherwise. Both the itemID
     * and the POS item ID are checked.
     *
     * @param itemNumber item identifier
     * @return true if item found in summary, false otherwise.
     */
    public boolean isItemInSummary(String itemNumber)
    {
        boolean itemExists = false;
        
        if (itemNumber != null)
        {
            Iterator<ItemSummaryIfc> iter = getItemSummaryIterator();
            ItemSummaryIfc item = null;
            while (iter != null && iter.hasNext())
            {
                item = iter.next();
                if (itemNumber.equals(item.getItemID()) || itemNumber.equals(item.getPosItemID()))
                {
                    itemExists = true;
                    break;
                }
            }    
        }        
        return itemExists;
    }

    /**
     * Set the timestamp that this transaction was completed
     *
     * @param date transaction timestamp date
     * @see oracle.retail.stores.domain.transaction.TransactionSummaryIfc#setTransactionTimestamp(oracle.retail.stores.domain.utility.EYSDate)
     */
    public void setTransactionTimestamp(EYSDate date)
    {
        this.transactionTimestamp = date;
    }

    /**
     * Get the timestamp that this transaction was completed on
     *
     * @return timestamp
     * @see oracle.retail.stores.domain.transaction.TransactionSummaryIfc#getTransactionTimestamp()
     */
    public EYSDate getTransactionTimestamp()
    {
        return this.transactionTimestamp;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Locale Requestor.
        @return The locale requestor
    **/
    //---------------------------------------------------------------------
    public LocaleRequestor getLocaleRequestor()
    {
    	return localeRequestor;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Locale Requestor
        @param value locale requestor
    **/
    //---------------------------------------------------------------------
    public void setLocaleRequestor(LocaleRequestor value)
    {
    	localeRequestor = value;
    }

    /**
     * Determine the relative ordering of two objects. The relative ordering is
     * reverse chronological order based on transaction timestamp.
     *
     * @param comparee the Object to compare
     * @return integer > 0 if specified object's transaction timestamp is
     *         greater, 0 if equal, < 0 if specified object lesser
     */
    public int compareTo(TransactionSummary comparee)
    {
        int result = -1;
        EYSDate compDate = null;

        compDate = comparee.getTransactionTimestamp();

        // Both are null then we are equal.
        if (getTransactionTimestamp() == null && compDate == null)
        {
            result = 0;
        }
        // If this object is null, then return 1 (we are going for reverse
        // order here)
        else if (getTransactionTimestamp() == null)
        {
            result = 1;
        }
        // Other object is null, return -1 (it is before this object)
        else if (compDate == null)
        {
            result = -1;
        }
        // Both have dates
        else
        {
            // Other object is after this one
            if (compDate.after(getTransactionTimestamp()))
            {
                result = 1;
            }
            // Other object is before this one
            else if (compDate.before(getTransactionTimestamp()))
            {
                result = -1;
            }
            else
            {
                result = 0;
            }
        } // else both have dates

        return result;
    }

    /**
     * Gets the List of Bill Payments
     * @return
     */
    public List<BillIfc> getBillPaymentList()
    {
      return billPaymentList;
    }

    /**
     * Sets the list of bill payments
     * @param billPaymentList
     */
    public void setBillPaymentList(List<BillIfc> billPaymentList)
    {
      this.billPaymentList = billPaymentList;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.utility.AbstractRoutable#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder("Class:  TransactionSummary (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode()).append(Util.EOL).append(
                "transactionType:                    [").append(getTransactionType()).append("]").append(Util.EOL)
                .append("transactionStatus:                  [").append(getTransactionStatus()).append("]").append(
                        Util.EOL).append("description:                        [").append(getLocalizedDescriptions()).append("]")
                .append(Util.EOL).append("registerID:                         [").append(getRegisterID()).append("]")
                .append(Util.EOL).append("tillID:                             [").append(getTillID()).append("]")
                .append(Util.EOL).append("cashierID:                          [").append(getCashierID()).append("]")
                .append(Util.EOL).append("suspendReasonCode:                  [").append(getSuspendReason().getCode())
                .append("]").append(Util.EOL);
        // add attributes to string
        if (transactionGrandTotal == null)
        {
            strResult.append("transactionGrandTotal:              [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("transactionGrandTotal:              [").append(transactionGrandTotal.toString()).append(
                    "]").append(Util.EOL);
        }
        if (transactionTaxTotal == null)
        {
            strResult.append("transactionTaxTotal:                [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("transactionTaxTotal:                [").append(transactionTaxTotal.toString())
                    .append("]").append(Util.EOL);
        }
        if (transactionInclusiveTaxTotal == null)
        {
            strResult.append("transactionInclusiveTaxTotal:       [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("transactionInclusiveTaxTotal:       [").append(transactionInclusiveTaxTotal.toString())
                    .append("]").append(Util.EOL);
        }
        if (businessDate == null)
        {
            strResult.append("businessDate:                       [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("businessDate:                       [").append(businessDate.toString()).append("]")
                    .append(Util.EOL);
        }
        if (getTransactionTimestamp() == null)
        {
            strResult.append("transactionTimestamp:               [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("transactionTimestamp:               [").append(getTransactionTimestamp().toString())
                    .append("]").append(Util.EOL);
        }
        if (transactionID == null)
        {
            strResult.append("transactionID:                      [null]").append(Util.EOL);
        }
        else
        {
            strResult.append(transactionID.toString()).append(Util.EOL);
        }
        if (store == null)
        {
            strResult.append("store:                              [null]").append(Util.EOL);
        }
        else
        {
            strResult.append(store.toString()).append(Util.EOL);
        }
        if (getCustomerName() == null)
        {
            strResult.append("customerName:                       [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("customerName:").append(Util.EOL).append(customerName.toString()).append(Util.EOL);
        }
        if (getExternalOrderID() == null)
        {
            strResult.append("externalOrderID:                       [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("externalOrderID:").append(Util.EOL).append(externalOrderID).append(Util.EOL);
        }
        // pass back result
        return strResult.toString();
    }

    /**
     * Retrieves the source-code-control system revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }

    /**
     * TransactionSummary main method.
     *
     * <B>Pre-Condition(s) </B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s) </B>
     * <UL>
     * <LI>toString() output
     * </UL>
     *
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        TransactionSummary c = new TransactionSummary();
        // output toString()
        System.out.println(c.toString());
    }

    /**
     * @return the externalOrderID
     */
    public String getExternalOrderID()
    {
        return externalOrderID;
    }

    /**
     * @param externalOrderID the externalOrderID to set
     */
    public void setExternalOrderID(String externalOrderID)
    {
        this.externalOrderID = externalOrderID;
    }

    /**
     * @return the internalOrderID
     */
    public String getInternalOrderID()
    {
        return internalOrderID;
    }

    /**
     * @param internalOrderID the internalOrderID to set
     */
    public void setInternalOrderID(String internalOrderID)
    {
        this.internalOrderID = internalOrderID;
    }

    /**
     * @return the layawayID
     */
    public String getLayawayID()
    {
        return layawayID;
    }

    /**
     * @param layawayID the layawayID to set
     */
    public void setLayawayID(String layawayID)
    {
        this.layawayID = layawayID;
    }
    
    
}
