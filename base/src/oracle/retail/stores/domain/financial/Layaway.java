/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/Layaway.java /main/22 2012/10/09 16:22:45 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  10/09/12 - Added previousTotalAmountPaid attribute for fiscal
 *                         printing
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    04/20/09 - display location in EJ
 *    acadar    04/13/09 - make layaway location required; refactor the way we
 *                         handle layaway reason codes
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    glwang    02/22/09 - changes per code review
 *    glwang    02/21/09 - add layaway legal statement to bpt.
 *    glwang    02/20/09 - create new layway payment receipts and reset itemid
 *                         dependency at sale receipt
 *    mchellap  11/27/08 - Added
 *                         currentTransactionBusinessDate,currentTransactionSequenceNo
 *                         and workStationID attributes
 *    cgreene   11/04/08 - fix some formatting issues with LayawayReceipt
 *    sgu       10/30/08 - check in after refresh
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    6    360Commerce 1.5         4/25/2007 10:00:56 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         4/27/2006 7:27:24 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         12/13/2005 4:43:47 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:15 PM  Robert Pearse
 *
 *   Revision 1.7.2.1  2004/10/15 18:50:24  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.8  2004/10/11 17:08:50  mweis
 *   @scr 7012 Transistion to use SALEABLE instead of AVAILABLE_TO_SELL
 *
 *   Revision 1.7  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/09/16 20:15:50  mweis
 *   @scr 7012 Correctly update the inventory counts when a layaway is picked up (completed).
 *
 *   Revision 1.5  2004/06/29 21:58:58  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.4  2004/03/30 16:38:40  cdb
 *   @scr 4166 Correcting Unit Test Layaway
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:35:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:52:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:01:02   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:21:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:14:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:37:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class handles the behavior of layaways. A layaway is an agreement to pay
 * for a collection of items over time. The layaway consists of status, terms
 * and an array of payments. The items are identified in a layaway transaction.
 *
 * @see oracle.retail.stores.domain.financial.LayawayIfc
 * @see oracle.retail.stores.domain.transaction.LayawayTransactionIfc
 * @version $Revision: /main/22 $
 */
public class Layaway implements LayawayIfc, LayawayConstantsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2824486950002015478L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/22 $";

    /**
     * layaway id
     */
    protected String layawayID = "";

    /**
     * layaway status. NOTE: This is *not* the inventory status.
     */
    protected int status = LayawayConstantsIfc.STATUS_UNDEFINED;

    /**
     * previous layaway status. NOTE: This is *not* the inventory status.
     */
    protected int previousStatus = LayawayConstantsIfc.STATUS_UNDEFINED;

    /**
     * date of last layaway status change
     */
    protected EYSDate lastStatusChange = null;

    /**
     * date layaway expires
     */
    protected EYSDate expirationDate = null;

    /**
     * grace period date for the layaway
     */
    protected EYSDate gracePeriodDate = null;

    /**
     * minimum down payment amount due
     */
    protected CurrencyIfc minimumDownPayment = null;


    /** @deprecated as of 13.1 use {@link #locationCode} instead */
    protected String location;

    /** Location in store where item is being stored for layaway */
    protected LocalizedCodeIfc locationCode;

    /**
     * total amount of the layaway
     */
    protected CurrencyIfc total = null;

    /**
     * layaway creation fee
     */
    protected CurrencyIfc creationFee = null;

    /**
     * layaway deletion fee
     */
    protected CurrencyIfc deletionFee = null;

    /**
     * total amount paid so far for this layaway
     */
    protected CurrencyIfc totalAmountPaid = null;
    
    /**
     * total amount paid prior to the current transaction
     */
    protected CurrencyIfc previousTotalAmountPaid = null;

    /**
     * layaway balance due
     */
    protected CurrencyIfc balanceDue = null;

    /**
     * customer reference
     */
    protected CustomerIfc customer = null;

    /**
     * description. Typically, this will be the first item description.
     */
    protected LocalizedTextIfc descriptions;

    /**
     * number of payments
     */
    protected int paymentCount = 0;

    /**
     * legal statement of liability of this layaway
     */
    protected String legalStatement = "";

    /**
     * initial transaction identifier
     */
    protected TransactionIDIfc initialTransactionID = null;

    /**
     * initial transaction business date
     */
    protected EYSDate initialTransactionBusinessDate = null;

    /**
     * store identifier
     */
    protected String storeID = "";

    /**
     * training mode status
     */
    protected boolean trainingMode = false;

    /**
     * payment history info collection
     */
    protected List<PaymentHistoryInfoIfc> paymentHistoryInfoCollection = null;

    /**
     * The locale requestor
     */
    protected LocaleRequestor localeRequestor = null;

    /**
     * Workstation identifier
     */
    protected String workStationID;

    /**
     *  Current transaction business date
     */
    protected EYSDate currentTransactionBusinessDate;

    /**
     * Current transaction sequence number
     */
    protected String currentTransactionSequenceNo;

    /**
     * Constructs Layaway object. Calls {@link #initialize()}.
     */
    public Layaway()
    {
        initialize();
    }

    /**
     * Initializes object.
     */
    public void initialize()
    {
        minimumDownPayment = DomainGateway.getBaseCurrencyInstance();
        totalAmountPaid = DomainGateway.getBaseCurrencyInstance();
        total = DomainGateway.getBaseCurrencyInstance();
        creationFee = DomainGateway.getBaseCurrencyInstance();
        deletionFee = DomainGateway.getBaseCurrencyInstance();
        balanceDue = DomainGateway.getBaseCurrencyInstance();
        lastStatusChange = DomainGateway.getFactory().getEYSDateInstance();
        initialTransactionID = DomainGateway.getFactory().getTransactionIDInstance();
        initialTransactionBusinessDate = DomainGateway.getFactory().getEYSDateInstance();
        paymentHistoryInfoCollection = new ArrayList<PaymentHistoryInfoIfc>();
        descriptions = DomainGateway.getFactory().getLocalizedText();
        locationCode = DomainGateway.getFactory().getLocalizedCode();
        localeRequestor = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        // instantiate new object
        Layaway c = new Layaway();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return c;
    }

    /**
     * Sets attributes in clone of this object.
     *
     * @param newClass new instance of object
     */
    protected void setCloneAttributes(Layaway newClass)
    {

        if (layawayID != null)
        {
            newClass.setLayawayID(layawayID);
        }
        newClass.setStatus(getStatus());
        newClass.setPreviousStatus(getPreviousStatus());
        if (lastStatusChange != null)
        {
            newClass.setLastStatusChange((EYSDate)getLastStatusChange().clone());
        }
        if (expirationDate != null)
        {
            newClass.setExpirationDate((EYSDate)getExpirationDate().clone());
        }
        if (gracePeriodDate != null)
        {
            newClass.setGracePeriodDate((EYSDate)getGracePeriodDate().clone());
        }
        if (minimumDownPayment != null)
        {
            newClass.setMinimumDownPayment((CurrencyIfc)getMinimumDownPayment().clone());
        }
        if (totalAmountPaid != null)
        {
            newClass.setTotalAmountPaid((CurrencyIfc)getTotalAmountPaid().clone());
        }
        if (previousTotalAmountPaid != null)
        {
            newClass.setPreviousTotalAmountPaid((CurrencyIfc)getPreviousTotalAmountPaid().clone());
        }
        if (location != null)
        {
            newClass.setLocation(location);
        }
        if (locationCode != null)
        {
            newClass.setLocationCode(locationCode);
        }
        if (total != null)
        {
            newClass.setTotal((CurrencyIfc)getTotal().clone());
        }
        if (creationFee != null)
        {
            newClass.setCreationFee((CurrencyIfc)getCreationFee().clone());
        }
        if (deletionFee != null)
        {
            newClass.setDeletionFee((CurrencyIfc)getDeletionFee().clone());
        }
        if (balanceDue != null)
        {
            newClass.setBalanceDue((CurrencyIfc)getBalanceDue().clone());
        }
        if (customer != null)
        {
            newClass.setCustomer((CustomerIfc)getCustomer().clone());
        }
        if (legalStatement != null)
        {
            newClass.setLegalStatement(legalStatement);
        }
        if (initialTransactionID != null)
        {
            newClass.setInitialTransactionID((TransactionIDIfc)initialTransactionID.clone());
        }
        if (initialTransactionBusinessDate != null)
        {
            newClass.setInitialTransactionBusinessDate((EYSDate)initialTransactionBusinessDate.clone());
        }
        newClass.setPaymentCount(paymentCount);
        newClass.setStoreID(storeID);
        newClass.setLocalizedDescriptions((LocalizedTextIfc)descriptions.clone());
        newClass.setTrainingMode(trainingMode);

        for (int k = 0; k < this.getPaymentHistoryInfoCollection().size(); k++)
        {
            PaymentHistoryInfoIfc paymentHistoryInfo = getPaymentHistoryInfoCollection().get(k);
            newClass.addPaymentHistoryInfo((PaymentHistoryInfoIfc)paymentHistoryInfo.clone());
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
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof Layaway)
        {
            Layaway c = (Layaway)obj; // downcast the input object
            // compare all the attributes of Layaway
            if (Util.isObjectEqual(getLayawayID(), c.getLayawayID()) && getStatus() == c.getStatus()
                    && getPaymentCount() == c.getPaymentCount() && getPreviousStatus() == c.getPreviousStatus()
                    && Util.isObjectEqual(getLastStatusChange(), c.getLastStatusChange())
                    && Util.isObjectEqual(getExpirationDate(), c.getExpirationDate())
                    && Util.isObjectEqual(getGracePeriodDate(), c.getGracePeriodDate())
                    && Util.isObjectEqual(getMinimumDownPayment(), c.getMinimumDownPayment())
                    && Util.isObjectEqual(getTotalAmountPaid(), c.getTotalAmountPaid())
                    && Util.isObjectEqual(getLocationCode(), c.getLocationCode())
                    && Util.isObjectEqual(getTotal(), c.getTotal())
                    && Util.isObjectEqual(getCreationFee(), c.getCreationFee())
                    && Util.isObjectEqual(getDeletionFee(), c.getDeletionFee())
                    && Util.isObjectEqual(getBalanceDue(), c.getBalanceDue())
                    && Util.isObjectEqual(getCustomer(), c.getCustomer())
                    && Util.isObjectEqual(getLegalStatement(), c.getLegalStatement())
                    && Util.isObjectEqual(getInitialTransactionID(), c.getInitialTransactionID())
                    && Util.isObjectEqual(getInitialTransactionBusinessDate(), c.getInitialTransactionBusinessDate())
                    && Util.isObjectEqual(getStoreID(), c.getStoreID())
                    && Util.isObjectEqual(getLocalizedDescriptions(), c.getLocalizedDescriptions())
                    && getTrainingMode() == c.getTrainingMode()
                    && Util.isObjectEqual(this.paymentHistoryInfoCollection, c.getPaymentHistoryInfoCollection()))

            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        else
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Retrieves layaway identifier.
     *
     * @return layaway identifier
     */
    public String getLayawayID()
    {
        return (layawayID);
    }

    /**
     * Sets the layaway identifier.
     *
     * @param value layaway identifier
     */
    public void setLayawayID(String value)
    {
        layawayID = value;
    }

    /**
     * Retrieves current layaway status.
     *
     * @return current layaway status
     */
    public int getStatus()
    {
        return (status);
    }

    /**
     * Sets layaway status, updating previous layaway status and date/time the
     * layaway status change occurred.
     *
     * @param value current layaway status
     */
    public void changeStatus(int value)
    {
        setPreviousStatus(status);
        status = value;
        setLastStatusChange();
    }

    /**
     * Reverts the layway status by setting the current status to the previous
     * status.
     */
    public void revertStatus()
    {
        status = previousStatus;
        setLastStatusChange();
    }

    /**
     * Resets the layway status based on the number of payments. Assumes payment
     * count has already been updated.
     */
    public void resetStatus()
    {
        switch (status)
        {
            case STATUS_NEW:
                // Void seems to be the only possibility, but not sure this is
                // safe.
                status = STATUS_VOIDED;
                break;
            case STATUS_ACTIVE:
            case STATUS_COMPLETED:
                if (paymentCount > 2)
                {
                    status = STATUS_ACTIVE;
                }
                else
                {
                    status = STATUS_NEW;
                }
                break;
            case STATUS_DELETED:
                if (paymentCount > 1)
                {
                    status = STATUS_ACTIVE;
                }
                else
                {
                    status = STATUS_NEW;
                }
                break;
            case STATUS_EXPIRED:
                break;
            default:
                // Shouldn't be getting here in these other cases. Log an error?
                break;
        }
        setLastStatusChange();
    }

    /**
     * Sets layaway status. Previous layaway status is not affected.
     *
     * @param value layaway status
     * @see #changeStatus
     */
    public void setStatus(int value)
    {
        status = value;
    }

    /**
     * Retrieves previous layaway status.
     *
     * @return previous layaway status
     */
    public int getPreviousStatus()
    {
        return (previousStatus);
    }

    /**
     * Sets previous layaway status.
     *
     * @param value previous layaway status
     */
    public void setPreviousStatus(int value)
    {
        previousStatus = value;
    }

    /**
     * Retrieves date of last layaway status change.
     *
     * @return date of last layaway status change
     */
    public EYSDate getLastStatusChange()
    {
        return (lastStatusChange);
    }

    /**
     * Sets timestamp of last status change.
     *
     * @param value timestamp of last status change
     */
    public void setLastStatusChange(EYSDate value)
    {
        lastStatusChange = value;
    }

    /**
     * Sets timestamp of last layaway status change.
     */
    public void setLastStatusChange()
    {
        lastStatusChange = DomainGateway.getFactory().getEYSDateInstance();
    }

    /**
     * Retrieves date layaway expires.
     *
     * @return date layaway expires
     */
    public EYSDate getExpirationDate()
    {
        return (expirationDate);
    }

    /**
     * Sets date layaway expires.
     *
     * @param value date layaway expires
     */
    public void setExpirationDate(EYSDate value)
    {
        expirationDate = value;
    }

    /**
     * Indicates if layaway is expired by testing current date against
     * expiration date.
     *
     * @return true if layaway expired, false otherwise.
     *         <P>
     * @see #isLayawayGracePeriodExpired()
     */
    public boolean isLayawayExpired()
    {
        // get current date and set to date-type-only
        EYSDate currentDate = DomainGateway.getFactory().getEYSDateInstance();
        currentDate.initialize(EYSDate.TYPE_DATE_ONLY);
        // compare grace period date to current date
        boolean returnValue = false;
        if (getExpirationDate().before(currentDate))
        {
            returnValue = true;
        }
        return (returnValue);
    }

    /**
     * Retrieves grace period date for the layaway.
     *
     * @return grace period date for the layaway
     */
    public EYSDate getGracePeriodDate()
    {
        return (gracePeriodDate);
    }

    /**
     * Sets grace period date for the layaway.
     *
     * @param value grace period date for the layaway
     */
    public void setGracePeriodDate(EYSDate value)
    {
        gracePeriodDate = value;
    }

    /**
     * Indicates if the layaway grace period has expired. This method makes no
     * assumptions about the expiration date.
     *
     * @return true if layawy grace period has expired, false otherwise.
     * @see #isLayawayExpired()
     */
    public boolean isLayawayGracePeriodExpired()
    {
        // get current date and set to date-type-only
        EYSDate currentDate = DomainGateway.getFactory().getEYSDateInstance();
        currentDate.initialize(EYSDate.TYPE_DATE_ONLY);
        // compare grace period date to current date
        boolean returnValue = false;
        if (getGracePeriodDate().before(currentDate))
        {
            returnValue = true;
        }
        return (returnValue);
    }

    /**
     * Retrieves minimum down payment amount due.
     *
     * @return minimum down payment amount due
     */
    public CurrencyIfc getMinimumDownPayment()
    {
        return (minimumDownPayment);
    }

    /**
     * Sets minimum down payment amount due.
     *
     * @param value minimum down payment amount due
     */
    public void setMinimumDownPayment(CurrencyIfc value)
    {
        minimumDownPayment = value;
    }

    /**
     * Retrieves total amount paid so far for this layaway.
     *
     * @return total amount paid so far for this layaway
     */
    public CurrencyIfc getTotalAmountPaid()
    {
        return (totalAmountPaid);
    }

    /**
     * Sets total amount paid so far for this layaway.
     *
     * @param value total amount paid so far for this layaway
     */
    public void setTotalAmountPaid(CurrencyIfc value)
    {
        totalAmountPaid = value;
    }
    
    /**
     * Returns the previous total amount paid prior to the current transaction.
     **/
    public CurrencyIfc getPreviousTotalAmountPaid()
    {
        return previousTotalAmountPaid;
    }

    /**
     * @param previousTotalAmountPaid the previousTotalAmountPaid to set
     */
    public void setPreviousTotalAmountPaid(CurrencyIfc previousTotalAmountPaid)
    {
        this.previousTotalAmountPaid = previousTotalAmountPaid;
    }

    /**
     * Retrieves balance due for this layaway.
     *
     * @return balance due for this layaway
     */
    public CurrencyIfc getBalanceDue()
    {
        return (balanceDue);
    }

    //---------------------------------------------------------------------
    /**
        Returns the previous balance due.
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getPreviousBalanceDue()
    {
        CurrencyIfc previousBalanceDue = getBalanceDue();
        if (previousBalanceDue == null)
        {
        	previousBalanceDue = DomainGateway.getBaseCurrencyInstance();
        }

        previousBalanceDue = previousBalanceDue.add(getTotalAmountPaid());
        return previousBalanceDue;
    }
    
    /**
     * Sets layaway balance due for this layaway.
     *
     * @param value layaway balance due for this layaway
     */
    public void setBalanceDue(CurrencyIfc value)
    {
        balanceDue = value;
    }

    /**
     * Adds a payment amount to the layaway, reducing the balance due and
     * increasing the total amount paid.
     *
     * @param value payment amount
     */
    public void addPaymentAmount(CurrencyIfc value)
    {
        balanceDue = balanceDue.subtract(value);
        totalAmountPaid = totalAmountPaid.add(value);

        if (value.signum() == CurrencyIfc.POSITIVE)
        {
            paymentCount = 1; // payment
        }
        else
        {
            paymentCount = 0; // potential refund
        }
    }

    /**
     * @deprecated as of 13.1 use {@link #getLocationCode()} instead
     */
    public String getLocation()
    {
        return locationCode.getText(LocaleMap.getLocale(LocaleMap.DEFAULT));
    }

    /**
     * Gets the location in the store to keep this layaway.
     *
     * @return The location.
     */
    public LocalizedCodeIfc getLocationCode()
    {
        return locationCode;
    }

    /**
     * @deprecated as of 13.1 use {@link #setLocationCode(CodeEntryIfc)} instead
     */
    public void setLocation(String layawayLocation)
    {
        LocalizedTextIfc text = DomainGateway.getFactory().getLocalizedText();
        text.putText(LocaleMap.getLocale(LocaleMap.DEFAULT), layawayLocation);
        locationCode.setText(text);
    }

    /**
     * Sets the location in the store to keep this layaway.
     *
     * @param location The new value for the location.
     */
    public void setLocationCode(LocalizedCodeIfc layawayLocation)
    {
        locationCode = layawayLocation;
    }

    /**
     * Retrieves total amount of the layaway.
     *
     * @return total amount of the layaway
     */
    public CurrencyIfc getTotal()
    {
        return (total);
    }

    /**
     * Sets total amount of the layaway.
     *
     * @param value total amount of the layaway
     */
    public void setTotal(CurrencyIfc value)
    {
        total = value;
    }

    /**
     * Retrieves layaway creation fee.
     *
     * @return layaway creation fee
     */
    public CurrencyIfc getCreationFee()
    {
        return (creationFee);
    }

    /**
     * Sets layaway creation fee.
     *
     * @param value layaway creation fee
     */
    public void setCreationFee(CurrencyIfc value)
    {
        creationFee = value;
    }

    /**
     * Retrieves layaway deletion fee.
     *
     * @return layaway deletion fee
     */
    public CurrencyIfc getDeletionFee()
    {
        return (deletionFee);
    }

    /**
     * Sets layaway deletion fee.
     *
     * @param value layaway deletion fee
     */
    public void setDeletionFee(CurrencyIfc value)
    {
        deletionFee = value;
    }

    /**
     * Retrieves customer.
     *
     * @return customer
     */
    public CustomerIfc getCustomer()
    {
        return (customer);
    }

    /**
     * Sets customer.
     *
     * @param value customer
     */
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Retrieves the number of payments.
     *
     * @return int number of payments
     */
    public int getPaymentCount()
    {
        return paymentCount;
    }

    /**
     * Sets number of payments.
     * <p>
     *
     * @param value number of payments
     */
    public void setPaymentCount(int value)
    {
        paymentCount = value;
    }

    /**
     * Returns true if payments list exists, false otherwise
     *
     * @return true if payments list exists, false otherwise
     */
    public boolean hasPayments()
    {
        return (paymentCount > 0);
    }

    /**
     * Retrieves legal statement of liability of this layaway.
     *
     * @return legal statement of liability of this layaway
     * @deprecated as 13.1 Legal statement text is in receipt blueprints
     */
    public String getLegalStatement()
    {
        return (legalStatement);
    }

    /**
     * Sets legal statement of liability of this layaway.
     *
     * @param value legal statement of liability of this layaway
     * @deprecated as 13.1 Legal statement text is in receipt blueprints
     */
    public void setLegalStatement(String value)
    {
        legalStatement = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.LayawayIfc#getInitialMinimumPayment()
     */
    public CurrencyIfc getInitialMinimumPayment()
    {
        return getMinimumDownPayment().add(getCreationFee());
    }

    /**
     * Retrieves initial transaction identifier.
     *
     * @return initial transaction identifier
     */
    public TransactionIDIfc getInitialTransactionID()
    {
        return (initialTransactionID);
    }

    /**
     * Sets initial transaction identifier.
     *
     * @param value initial transaction identifier
     */
    public void setInitialTransactionID(TransactionIDIfc value)
    {
        initialTransactionID = value;
    }

    /**
     * Retrieves initial transaction business date.
     *
     * @return initial transaction business date
     */
    public EYSDate getInitialTransactionBusinessDate()
    {
        return (initialTransactionBusinessDate);
    }

    /**
     * Sets initial transaction business date.
     *
     * @param value initial transaction business date
     */
    public void setInitialTransactionBusinessDate(EYSDate value)
    {
        initialTransactionBusinessDate = value;
    }

    /**
     * Retrieves store identifier.
     *
     * @return store identifier
     */
    public String getStoreID()
    {
        return (storeID);
    }

    /**
     * Sets store identifier.
     *
     * @param value store identifier
     */
    public void setStoreID(String value)
    {
        storeID = value;
    }

    /**
     * Retrieves description.
     *
     * @return description
     * @deprecated As of 13.1 Use {@link Layaway#getDescription(Locale)}
     */
    public String getDescription()
    {
        return getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }

    /**
     * Retrieves description.
     *
     * @return description
     */
    public String getDescription(Locale locale)
    {
        return descriptions.getText(LocaleMap.getBestMatch(locale));
    }

    /**
     * Retrieves localized descriptions.
     *
     * @return localized descriptions
     */
    public LocalizedTextIfc getLocalizedDescriptions()
    {
        return descriptions;
    }

    /**
     * Sets description.
     *
     * @param value description
     */
    public void setDescription(String value)
    {
        setDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE), value);
    }

    /**
     * Sets description.
     *
     * @param value description
     */
    public void setDescription(Locale locale, String value)
    {
        descriptions.putText(LocaleMap.getBestMatch(locale), value);
    }

    /**
     * Sets localized descriptions.
     *
     * @param value localized descriptions
     */
    public void setLocalizedDescriptions(LocalizedTextIfc value)
    {
        descriptions = value;
    }

    /**
     * Sets the training mode status.
     *
     * @param value training mode status
     */
    public void setTrainingMode(boolean value)
    {
        trainingMode = value;
    }

    /**
     * Returns the training mode status.
     *
     * @return True, if the transaction is for training, false otherwise
     */
    public boolean isTrainingMode()
    {
        return (trainingMode);
    }

    /**
     * Returns the training mode status.
     *
     * @return True, if the transaction is for training, false otherwise
     */
    public boolean getTrainingMode()
    {
        return (isTrainingMode());
    }

    /**
     * Returns string representation of status value.
     *
     * @param value status value
     * @return string representation of status value
     */
    public static String statusToString(int value)
    {
        StringBuffer strResult = new StringBuffer();
        // attempt to use descriptor
        try
        {
            if (value == LayawayConstantsIfc.STATUS_UNDEFINED)
            {
                strResult.append("Undefined");
            }
            else
            {
                strResult.append(LayawayConstantsIfc.STATUS_DESCRIPTORS[value]);
            }
        }
        // if out of bounds, build special message
        catch (ArrayIndexOutOfBoundsException e)
        {
            strResult.append("Invalid [").append(value).append("]");
        }

        return (strResult.toString());
    }

    /**
     * Returns string representation of this layaway's status value.
     *
     * @return string representation of status value
     */
    public String statusToString()
    {
        return (statusToString(getStatus()));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("Layaway", getRevisionNumber(), hashCode());

        strResult.append(Util.formatToStringEntry("layaway ID", getLayawayID())).append(
                Util.formatToStringEntry("status", statusToString())).append(
                Util.formatToStringEntry("previous status", statusToString(getPreviousStatus()))).append(
                Util.formatToStringEntry("last status change", getLastStatusChange().toString()));
        if (getExpirationDate() != null)
        {
            strResult.append(Util.formatToStringEntry("expiration date", getExpirationDate().toString()));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("expiration date", "null"));
        }
        if (getGracePeriodDate() != null)
        {
            strResult.append(Util.formatToStringEntry("grace period date", getGracePeriodDate().toString()));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("grace period date", "null"));
        }
        strResult.append(Util.formatToStringEntry("minimum down payment", getMinimumDownPayment().toString())).append(
                Util.formatToStringEntry("total amount paid", getTotalAmountPaid().toString())).append(
                Util.formatToStringEntry("locationCode", getLocationCode())).append(
                Util.formatToStringEntry("total", getTotal().toString())).append(
                Util.formatToStringEntry("creation fee", getCreationFee().toString())).append(
                Util.formatToStringEntry("deletion fee", getDeletionFee().toString())).append(
                Util.formatToStringEntry("balance due", getBalanceDue().toString())).append(
                Util.formatToStringEntry("customer", getCustomer())).append(
                Util.formatToStringEntry("payment count", getPaymentCount())).append(
                Util.formatToStringEntry("legal statement", getLegalStatement())).append(
                Util.formatToStringEntry("initial transaction ID", getInitialTransactionID())).append(
                Util.formatToStringEntry("initial transaction business date",
                        getInitialTransactionBusinessDate().toString())).append(
                Util.formatToStringEntry("store ID", getStoreID())).append(
                Util.formatToStringEntry("description", getLocalizedDescriptions().toString()));

        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Layawaymain method.
     *
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        Layaway c = new Layaway();
        // output toString()
        System.out.println(c.toString());
    }

    /**
     * Adds payment history info
     *
     * @param paymentHistoryInfo
     */
    public void addPaymentHistoryInfo(PaymentHistoryInfoIfc paymentHistoryInfo)
    {
        this.paymentHistoryInfoCollection.add(paymentHistoryInfo);
    }

    /**
     * Gets the payment history info
     *
     * @return List collection of payment history info
     */
    public List<PaymentHistoryInfoIfc> getPaymentHistoryInfoCollection()
    {
        return (this.paymentHistoryInfoCollection);
    }

    /**
     * Gets the Locale Requestor.
     *
     * @return The locale requestor
     */
    public LocaleRequestor getLocaleRequestor()
    {
        return localeRequestor;
    }

    /**
     * Sets the Locale Requestor
     *
     * @param value locale requestor
     */
    public void setLocaleRequestor(LocaleRequestor value)
    {
        localeRequestor = value;
    }

    /**
     * Gets the current transaction's business date
     * @return the currentTransactionBusinessDate
     */
    public EYSDate getCurrentTransactionBusinessDate()
    {
        return currentTransactionBusinessDate;
    }

    /**
     * Sets the current transaction's businessDate
     * @param currentTransactionBusinessDate the currentTransactionBusinessDate to set
     */
    public void setCurrentTransactionBusinessDate(EYSDate currentTransactionBusinessDate)
    {
        this.currentTransactionBusinessDate = currentTransactionBusinessDate;
    }

    /**
     * Gets the current transaction's sequence number
     * @return the currentTransactionSequenceNo
     */
    public String getCurrentTransactionSequenceNo()
    {
        return currentTransactionSequenceNo;
    }

    /**
     * Sets current transaction's sequence number
     * @param currentTransactionSequenceNo the currentTransactionSequenceNo to set
     */
    public void setCurrentTransactionSequenceNo(String currentTransactionSequenceNo)
    {
        this.currentTransactionSequenceNo = currentTransactionSequenceNo;
    }

    /**
     * Gets the workstation identifier
     * @return the workStationID
     */
    public String getWorkStationID()
    {
        return workStationID;
    }

    /**
     *  Sets the workstation identifier
     * @param workStationID the workStationID to set
     */
    public void setWorkStationID(String workStationID)
    {
        this.workStationID = workStationID;
    }
}