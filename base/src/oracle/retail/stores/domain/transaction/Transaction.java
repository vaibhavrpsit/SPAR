/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/Transaction.java /main/41 2013/05/09 08:03:18 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/07/13 - Modified to support sending voided order returns to
 *                         the cross channel order repository.
 *    rgour     04/01/13 - CBR cleanup
 *    rgour     10/18/12 - CBR fix for storing original transaction currency
 *                         code
 *    sgu       05/29/12 - set up retail transaction technician to handle
 *                         creation of xc customer order
 *    icole     04/17/12 - Added country and currency to clone, equal, and
 *                         toString.
 *    rsnayak   03/22/12 - cross border return changes
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    blarsen   12/16/10 - Added isCompleted() helper method.
 *    abondala  09/27/10 - Now both the system date and business date are
 *                         displyed where ever required. TransactionTracker can
 *                         search transactions by business date.
 *    abhayg    08/04/10 - TRANSACTION NUMBER IS SKIPPED DUE TO APPLICATION
 *                         TIME OUT
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    vikini    02/25/09 - Setting Item Basket trn complete boolean in Clone
 *    acadar    02/09/09 - use default locale for display of date and time
 *    vikini    02/03/09 - Adding the boolean to check if transaction comes
 *                         from ITem basket flow
 *    atirkey   12/31/08 - Suspended Transaction
 *    deghosh   12/23/08 - EJ i18n changes
 *    vchengeg  12/05/08 - Formatted Ejournal entry for HPQC bug : 990
 *    deghosh   12/02/08 - EJ i18n changes
 *    cgreene   11/13/08 - added isCanceled method
 *    mdecama   11/07/08 - I18N - updated toString()
 *    mdecama   11/07/08 - I18N - Fixed Clone Method
 *    akandru   10/31/08 - EJ Changes_I18n
 *    akandru   10/30/08 - EJ changes
 *    mdecama   10/24/08 - I18N updates for Suspend Transaction Reason Codes.
 *
 * ===========================================================================

     $Log:
      13   360Commerce 1.12        4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuilder to StringBuilder
      12   360Commerce 1.11        2/22/2008 2:15:22 PM   Christian Greene
           CR30318 Roll back to two version to CTR slowness fix also include
           CompressionUtility into fixes.
      11   360Commerce 1.10        2/20/2008 3:22:09 PM   Alan N. Sinton  CR
           30318: Reverting changes to unbreak CTR.
      10   360Commerce 1.9         2/13/2008 1:50:19 PM   Christian Greene
           Update returns-lookup to eliminate the extra isVoided call to
           CentralOffice by setting voided transaction's status upon
           post-void. Also compress XML data retrieved from CO during
           returns-lookup.
      9    360Commerce 1.8         5/21/2007 9:17:04 AM   Anda D. Cadar   Ej
           changes and cleanup
      8    360Commerce 1.7         5/17/2007 1:39:23 PM   Mathews Kochummen
           make date format consistent
      7    360Commerce 1.6         5/8/2007 5:28:25 PM    Alan N. Sinton  CR
           26486 - Refactor of some EJournal code.
      6    360Commerce 1.5         5/3/2007 10:02:51 AM   Mathews Kochummen
           handle locale specific date and time
      5    360Commerce 1.4         8/10/2006 11:17:01 AM  Brendan W. Farrell
           16500 -Merge fix from v7.x.  Maintain sales associate to be used in
            reporting.
      4    360Commerce 1.3         1/25/2006 4:11:53 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:30:33 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:20 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:11 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/10/2005 10:30:34    Rohit Sachdeva
           Optional date in the transaction ID should be taken up as business
           date when entered as per format specified
      3    360Commerce1.2         3/31/2005 15:30:33     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:26:20     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:15:11     Robert Pearse
     $
     Revision 1.10.2.1  2004/11/04 20:20:32  kll
     @scr 7375: indicate transaction re-entry mode on journal header if applicable

     Revision 1.10  2004/09/23 00:30:51  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.9  2004/06/21 14:53:28  khassen
     @scr 5684 - Feature enhancements for capture customer use case.  Added clone call.

     Revision 1.8  2004/06/17 21:36:28  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.7  2004/04/08 22:04:15  bjosserand
     @scr 4093 Transaction Reentry

     Revision 1.6  2004/02/27 00:48:30  jdeleau
     @scr 0 The clone and equals methods were failing with NPE on
     voidTransaction objects created with the default constructor.
     This corrects those problems.

     Revision 1.5  2004/02/26 16:47:08  rzurga
     @scr 0 Add optional and customizable date to the transaction id and its receipt barcode

     Revision 1.4  2004/02/17 16:18:52  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:14:42  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:28:51  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Nov 13 2003 15:06:28   nrao
 * Added salesAssociate variable and getters and setters for Instant Credit Enrollment.
 *
 *    Rev 1.1   Oct 02 2003 10:45:54   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 *
 *    Rev 1.0   Aug 29 2003 15:41:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 10 2003 16:26:48   mpm
 * Added support for post-processing-status code.
 *
 *    Rev 1.1   Feb 13 2003 14:30:06   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 17:06:28   msg
 * Initial revision.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.transaction;

// java imports
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.AbstractRoutable;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * A record of business activity that involves a financial and/or merchandise
 * unit exchange or the granting of access to conduct business at a specific
 * device, at a specific point in time for a specific employee.
 *
 * @version $Revision: /main/41 $
 */
public class Transaction extends AbstractRoutable implements TransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7871388092505703643L;

    /**
     * revision number
     */
    public static String revisionNumber = "$Revision: /main/41 $";

    /**
     * debug flag
     */
    protected static final boolean DEBUG = false;

    /**
     * A unique, automatically assigned identifier used to identify a
     * transaction.
     */
    protected String transactionID;

    /**
     * A unique, non-significant, automatically assigned sequential number used
     * to identify a transaction within the context of a Workstation and a
     * Business period.
     */
    protected long transactionSequenceNumber;

    /**
     * A code to denote the type of transaction. For example, RETAIL.
     */
    protected int transactionType;

    /**
     * status of transaction
     */
    protected int transactionStatus;

    /**
     * previous status of transaction
     */
    protected int previousTransactionStatus;

    /**
     * The time and date a transaction is initiated.
     */
    protected EYSDate timestampBegin = null;

    /**
     * The time and date a transaction is completed.
     */
    protected EYSDate timestampEnd = null;

    /**
     * The calendar date equivalent of the associated fiscal day, fiscal week
     * and fiscal year.
     */
    protected EYSDate businessDay = null;

    /**
     * register used for transaction
     */
    protected WorkstationIfc workstation;

    /**
     * A unique, automatically assigned number used to identify a workstation
     * operator (cashier).
     */
    protected EmployeeIfc cashier;

    /**
     * A sales associate ID entered within the application
     */
    protected EmployeeIfc salesAssociate;

    /**
     * till identifier
     */
    protected String tillID;

    /**
     * tender limits
     */
    protected TenderLimitsIfc tenderLimits;

    /**
     * transaction identifier
     */
    protected TransactionIDIfc transactionIdentifier;

    /**
     * training mode status
     */
    protected boolean trainingMode = false;

    /**
     * transaction reentry mode status
     */
    protected boolean reentryMode = false;

    /**
     * suspend reason code
     */
    LocalizedCodeIfc suspendReason = null;

    /**
     * customer information object used to store customer postal code or phone
     * with transaction
     */
    protected CustomerInfoIfc customerInfo = null;

    /**
     * locales being used for the sql
     */
    protected LocaleRequestor localeRequestor = null;

    /**
     * post-processing status code
     */
    protected int postProcessingStatus = TransactionIfc.POST_PROCESSING_STATUS_UNPROCESSED;

    /**
     * for the capture customer use case: associates customer info with
     * transaction, to be written to db.
     */
    protected CaptureCustomerIfc captureCustomer;

    /**
     * This flag is used to indicate whether the canceled transaction is
     * printed
     */
    protected boolean canceledTransactionPrinted = false;

    /**
     * This flag is used to indicate whether the canceled transaction is saved
     */
    protected boolean canceledTransactionSaved = false;

    /**
    *
    * Checks if the transaction is obtained from Item Basket flow
    *
    */
   protected boolean isBasketTransactionComplete = false;

   /**
    * ISO Code of the Currency in which the transaction was made
    */
   protected String transactionCurrencyCode = null;

   /**
    * Currency in which the transaction was made
    */
   protected String transactionCurrency = null;
   
   /**
    * Currency in which the original transaction was made
    */
   protected String originaltransactionCurrency = null;

   /**
    * Country in which the transaction was made
    */
   protected String transactionCountry = null;

    /**
     * Creates a new transaction.
     */
    public Transaction()
    {
        setTransactionType(TransactionIfc.TYPE_UNKNOWN);
        setLocaleRequestor(new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)));
        transactionIdentifier = DomainGateway.getFactory().getTransactionIDInstance();
        suspendReason = DomainGateway.getFactory().getLocalizedCode();
    }

    /**
     * Creates a new transaction with the specified transaction identifier.
     *
     * @param transID transaction identifier
     */
    public Transaction(TransactionIDIfc transID)
    {
        this();
        initialize(transID);
    }

    /**
     * Creates a new test transaction. This constructor automatically sets up
     * the fields required to make a transaction unique. It derives it's own
     * transaction sequence number.
     */
    public Transaction(WorkstationIfc station)
    {
        this();
        /*
         * Take care of primary keys
         */
        workstation = station;
        setBusinessDay();
        buildTransactionID("test");

        /*
         * Initialize other relavent fields
         */
        setTimestampBegin();
        transactionStatus = TransactionIfc.STATUS_IN_PROGRESS;
    }

    /**
     * Creates a new transaction. This constructor automatically sets up the
     * fields required to make a transaction unique.
     * This is the preferred constructor for Transactions that are saved to the
     * database.
     */
    public Transaction(WorkstationIfc station, long sequenceNumber)
    {
        this();
        /*
         * Take care of primary keys
         */
        workstation = station;
        setTransactionSequenceNumber(sequenceNumber);
        setBusinessDay();
        buildTransactionID();

        /*
         * Initialize other relavent fields
         */
        setTimestampBegin();
        transactionStatus = TransactionIfc.STATUS_IN_PROGRESS;
    }

    /**
     * Creates a new transaction. This constructor automatically decodes the
     * transaction ID into the appropriate fields.
     * This is the preferred constructor for Transactions that are used to
     * search the database.
     *
     * @see oracle.retail.stores.domain.arts.TransactionDataTransaction
     */
    public Transaction(String transactionID) throws IllegalArgumentException
    {
        this();
        initialize(transactionID);
    }

    /**
     * Initializes a new transaction. This method utomatically sets up the
     * fields required to make a transaction unique. It derives it's own
     * transaction sequence number.
     */
    public void initialize(WorkstationIfc station)
    {
        /*
         * Take care of primary keys
         */
        workstation = station;
        setBusinessDay();
        buildTransactionID("test");

        /*
         * Initialize other relavent fields
         */
        setTimestampBegin();
        transactionStatus = TransactionIfc.STATUS_IN_PROGRESS;
    }

    /**
     * Initializes transaction with transaction ID object.
     *
     * @param transID transaction ID object
     */
    public void initialize(TransactionIDIfc transID)
    {
        transactionIdentifier = transID;
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(transactionIdentifier.getStoreID());
        workstation = DomainGateway.getFactory().getWorkstationInstance();
        workstation.setWorkstationID(transactionIdentifier.getWorkstationID());
        workstation.setStore(store);
        setBusinessDay(transactionIdentifier.getBusinessDate());
        // @TODO: Possibly add barcode date logic
    }

    /**
     * Initializes transaction with string transaction ID. Sets transactionID
     * attribute and decodes transaction ID string to set other attributes.
     *
     * @param transactionID transaction identifier string
     */
    public void initialize(String transactionID)
    {
        this.transactionID = transactionID;
        decodeTransactionID(transactionID);
        EYSDate busDate = transactionIdentifier.getBusinessDate();
        if (busDate != null)
            setBusinessDay(busDate);
    }

    /**
     * Clones Transaction object.
     *
     * @return instance of Transaction object
     */
    public Object clone()
    {
        // instantiate new object
        Transaction t = new Transaction();
        // set attributes
        setCloneAttributes(t); // set tenderLimits
        // pass back object
        return t;
    }

    /**
     * Sets the tenderLimits for the clone of Transaction. Called by
     * SaleReturnTransaction clone constructor.
     *
     * @param Transaction newEntity object
     */
    protected void setCloneAttributes(Transaction newEntity)
    {
        // set items for clone newEntity
        newEntity.setTenderLimits(tenderLimits);
        if (transactionIdentifier != null)
        {
            newEntity.setTransactionIdentifier((TransactionIDIfc)transactionIdentifier.clone());
        }
        // this is done in the TransactionIdentifier clone
        //newEntity.setTransactionSequenceNumber(getTransactionSequenceNumber())
        // ;
        newEntity.setTransactionType(transactionType);
        newEntity.setTransactionStatus(transactionStatus);
        newEntity.setPreviousTransactionStatus(previousTransactionStatus);
        newEntity.setTrainingMode(trainingMode);
        if (suspendReason != null)
        {
            newEntity.setSuspendReason((LocalizedCodeIfc)getSuspendReason().clone());
        }
        newEntity.setCustomerInfo(getCustomerInfo());
        if (captureCustomer != null)
        {
            newEntity.setCaptureCustomer((CaptureCustomerIfc)captureCustomer.clone());
        }
        if (workstation != null)
        {
            newEntity.setWorkstation((WorkstationIfc)workstation.clone());
        }
        if (timestampBegin != null)
        {
            newEntity.setTimestampBegin((EYSDate)timestampBegin.clone());
        }
        if (timestampEnd != null)
        {
            newEntity.setTimestampEnd((EYSDate)timestampEnd.clone());
        }
        if (businessDay != null)
        {
            newEntity.setBusinessDay((EYSDate)businessDay.clone());
        }
        if (tillID != null)
        {
            newEntity.setTillID(new String(tillID));
        }
        if (cashier != null)
        {
            newEntity.setCashier((EmployeeIfc)(cashier.clone()));
        }
        if (salesAssociate != null)
        {
            newEntity.setSalesAssociate((EmployeeIfc)(salesAssociate.clone()));
        }
        newEntity.setPostProcessingStatus(getPostProcessingStatus());
        if (localeRequestor != null)
        {
            newEntity.setLocaleRequestor((LocaleRequestor)localeRequestor.clone());
        }
        //Checking for ItemBasketTransaction Completed
        newEntity.setIsItemBasketTransactionComplete(this.getIsItemBasketTransactionComplete());
    }

    /**
     * Copys transactions attributes to new transaction
     *
     * @param TransactionIfc transaction
     */
    public void setTransactionAttributes(TransactionIfc transaction)
    {
        if (transaction instanceof Transaction)
        {
            setCloneAttributes((Transaction)transaction);
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
        boolean isEqual = false;

        try
        {
            Transaction c = (Transaction)obj;

            if (!Util.isObjectEqual(transactionIdentifier, c.transactionIdentifier))
            {
                isEqual = false;
            }
            else if (transactionType != c.transactionType)
            {
                isEqual = false;
            }
            else if (transactionStatus != c.transactionStatus)
            {
                isEqual = false;
            }
            else if (previousTransactionStatus != c.previousTransactionStatus)
            {
                isEqual = false;
            }
            // this is temporary, until the Sybase JDBC driver starts supporting
            // milliseconds
            else if ((timestampBegin != null && c.timestampBegin != null)
                    && !Util.isObjectEqual(timestampBegin.toString(), c.timestampBegin.toString()))
            {
                isEqual = false;
            }
            // this is temporary, until the Sybase JDBC driver starts supporting
            // milliseconds
            else if ((timestampEnd != null && c.timestampEnd != null)
                    && !Util.isObjectEqual(timestampEnd.toString(), c.timestampEnd.toString()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(businessDay, c.businessDay))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(workstation, c.workstation))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(cashier, c.cashier))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(salesAssociate, c.salesAssociate))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(tillID, c.tillID))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(tenderLimits, c.tenderLimits))
            {
                isEqual = false;
            }
            else if (trainingMode != c.isTrainingMode())
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getSuspendReason(), c.getSuspendReason()))
            {
                isEqual = false;
            }

            else if (!getTransactionID().equals(c.getTransactionID()))
            {
                isEqual = false;
            }
            else if (getTransactionSequenceNumber() != c.getTransactionSequenceNumber())
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getCustomerInfo(), c.getCustomerInfo()))
            {
                isEqual = false;
            }
            else if (getPostProcessingStatus() != c.getPostProcessingStatus())
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(localeRequestor, c.getLocaleRequestor()))
            {
                isEqual = false;
            }        
            else
            {
                isEqual = true;
            }
        }
        catch (Exception e)
        {
            isEqual = false;
        }

        return (isEqual);
    }

    /**
     * Build transaction ID.
     */
    public void buildTransactionID()
    {
        buildTransactionID("prod");
    }

    /**
     * Build transaction ID.
     *
     * @param String test indicator
     */
    public void buildTransactionID(String i)
    {
        // if this is a test, carve sequence number from system time
        if (i.equals("test"))
        {
            /*
             * This requires at least 100 milliseconds between transactions per
             * workstation
             */
            long suffix = System.currentTimeMillis() % 86400000 / 100;
            // cut this number down to fit in four digits
            suffix = suffix % 10000;
            setTransactionSequenceNumber(suffix);
        }

        // Use workstation ID, store ID and sequence number to form
        // transaction ID. If any of these are not numeric, they will
        // be employed as strings. Sequence number is four digits;
        // store number is five digits; workstation ID is three digits.
        transactionIdentifier.setTransactionID(getWorkstation().getStore().getStoreID(),
                                         getWorkstation().getWorkstationID(),
                                         getTransactionSequenceNumber());

        setTransactionID(transactionIdentifier.getTransactionIDString());
    }

    /**
     * Decodes the transaction ID into the appropriate attributes.
     *
     * @param transID The transaction ID to decode
     * @throws IllegalArgumentException If the format of the transaction ID is
     *             invalid
     */
    protected void decodeTransactionID(String transID) throws IllegalArgumentException
    {
        transactionIdentifier.setTransactionID(transID);
        // set values in objects, as needed
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(transactionIdentifier.getStoreID());
        workstation = DomainGateway.getFactory().getWorkstationInstance();
        workstation.setWorkstationID(transactionIdentifier.getWorkstationID());
        workstation.setStore(store);
    }

    /**
     * Get formatted transaction sequence number string.
     *
     * @return formatted transaction sequence number string
     */
    public String getFormattedTransactionSequenceNumber()
    {
        return (transactionIdentifier.getFormattedTransactionSequenceNumber());
    }

    /**
     * Get formatted store identifier string. If store ID is numeric, format to
     * five digits.
     *
     * @return formatted store identifier string
     */
    public String getFormattedStoreID()
    {
        return (transactionIdentifier.getFormattedStoreID());
    }

    /**
     * Get formatted workstation identifier string. If workstation ID is
     * numeric, format to three digits.
     *
     * @return formatted workstation identifier string
     */
    public String getFormattedWorkstationID()
    {
        return (transactionIdentifier.getFormattedWorkstationID());
    }

    /**
     * Returns transaction identifier.
     *
     * @return transaction identifier
     */
    public String getTransactionID()
    {
        return (transactionIdentifier.getTransactionIDString());
    }

    /**
     * Sets transaction ID.
     *
     * @param value transaction identifier
     */
    public void setTransactionID(String value)
    {
        transactionIdentifier.setTransactionID(value);
    }

    /**
     * Returns transaction sequence number.
     *
     * @return transaction sequence number
     */
    public long getTransactionSequenceNumber()
    {
        return (transactionIdentifier.getSequenceNumber());
    }

    /**
     * Sets transaction SequenceNumber.
     *
     * @param value transaction sequence number
     */
    public void setTransactionSequenceNumber(long value)
    {
        transactionIdentifier.setSequenceNumber(value);
    }

    /**
     * Returns transaction identifier.
     *
     * @return transaction identifier
     */
    public TransactionIDIfc getTransactionIdentifier()
    {
        return (transactionIdentifier);
    }

    /**
     * Sets transaction identifier.
     *
     * @param value transaction identifier
     */
    public void setTransactionIdentifier(TransactionIDIfc value)
    {
        // set identifier
        transactionIdentifier = value;
    }

    /**
     * Returns transaction type.
     *
     * @return transaction type
     */
    public int getTransactionType()
    {
        return (transactionType);
    }

    /**
     * Sets transaction type.
     *
     * @param value transaction type
     */
    public void setTransactionType(int value)
    {
        // set type
        transactionType = value;
    }

    /**
     * Returns transaction status.
     *
     * @return transaction status
     */
    public int getTransactionStatus()
    {
        return (transactionStatus);
    }

    /**
     * Sets transaction status.
     *
     * @param value transaction status
     */
    public void setTransactionStatus(int value)
    {
        // set previous status
        previousTransactionStatus = transactionStatus;
        // set status
        transactionStatus = value;
    }

    /**
     * Sets transaction status from XML string to avoid side effects.
     *
     * @param value transaction status
     */
    public void setTransactionStatusFromXML(int value)
    {
        // set status
        transactionStatus = value;
    }

    /**
     * Returns previous transaction status.
     *
     * @return previous transaction status
     */
    public int getPreviousTransactionStatus()
    {
        return (previousTransactionStatus);
    }

    /**
     * Sets previous transaction status.
     *
     * @param value previous transaction status
     */
    public void setPreviousTransactionStatus(int value)
    {
        previousTransactionStatus = value;
    }

    /**
     * Returns cashier.
     *
     * @return cashier
     */
    public EmployeeIfc getCashier()
    {
        return (cashier);
    }

    /**
     * Sets cashier attribute.
     *
     * @param emp cashier
     */
    public void setCashier(EmployeeIfc emp)
    {
        cashier = emp;
    }

    /**
     * Returns business day.
     *
     * @return business day.
     */
    public EYSDate getBusinessDay()
    {
        return (businessDay);
    }

    /**
     * Sets business day to current time.
     */
    public void setBusinessDay()
    {
        businessDay = DomainGateway.getFactory().getEYSDateInstance();
        transactionIdentifier.setBusinessDate(businessDay);
    }

    /**
     * Sets business day to specified value.
     *
     * @param value timestamp setting
     */
    public void setBusinessDay(EYSDate value)
    {
        businessDay = value;
        transactionIdentifier.setBusinessDate(businessDay);
    }

    /**
     * Returns begin timestamp.
     *
     * @return begin timestamp.
     */
    public EYSDate getTimestampBegin()
    {
        return (timestampBegin);
    }

    /**
     * Sets begin timestamp to current time.
     */
    public void setTimestampBegin()
    {
        timestampBegin = DomainGateway.getFactory().getEYSDateInstance();
    }

    /**
     * Sets begin timestamp to specified value.
     *
     * @param value timestamp setting
     */
    public void setTimestampBegin(EYSDate value)
    {
        timestampBegin = value;
    }

    /**
     * Returns end timestamp.
     *
     * @return end timestamp.
     */
    public EYSDate getTimestampEnd()
    {
        return (timestampEnd);
    }

    /**
     * Sets end timestamp.
     */
    public void setTimestampEnd()
    {
        timestampEnd = DomainGateway.getFactory().getEYSDateInstance();
    }

    /**
     * Sets end timestamp to specified value.
     *
     * @param value timestamp setting
     */
    public void setTimestampEnd(EYSDate value)
    {
        timestampEnd = value;
    }

    /**
     * Sets workstation reference.
     *
     * @param value workstation reference
     */
    public void setWorkstation(WorkstationIfc value)
    {
        if (transactionIdentifier == null)
        {
            transactionIdentifier = DomainGateway.getFactory().getTransactionIDInstance();
        }
        workstation = value;
        // set workstation ID in transaction ID object
        transactionIdentifier.setWorkstationID(workstation.getWorkstationID());
        // if store ID exists, set store ID in transaction ID object
        if (workstation.getStore() != null)
        {
            transactionIdentifier.setStoreID(workstation.getStore().getStoreID());
        }
    }

    /**
     * Returns workstation reference.
     *
     * @return workstation reference
     */
    public WorkstationIfc getWorkstation()
    {
        return (workstation);
    }

    /**
     * Returns till identifier.
     *
     * @return till identifier
     */
    public String getTillID()
    {
        return (tillID);
    }

    /**
     * Sets till identifier attribute.
     *
     * @param value till identifier
     */
    public void setTillID(String value)
    {
        tillID = value;
    }

    /**
     * Sets tender limits reference.
     *
     * @param value tender limits reference
     */
    public void setTenderLimits(TenderLimitsIfc value)
    {
        tenderLimits = value;

        // Propagate this to all of the tender line items
        if (this instanceof RetailTransactionIfc)
        {
            TenderLineItemIfc[] tli = ((RetailTransactionIfc)this).getTenderLineItems();
            int numItems = 0;
            if (tli != null)
            {
                numItems = tli.length;
            }
            for (int i = 0; i < numItems; i++)
            {
                tli[i].setTenderLimits(tenderLimits);
            }
        }
    }

    /**
     * Returns tender limits reference.
     *
     * @return tender limits reference
     */
    public TenderLimitsIfc getTenderLimits()
    {
        return (tenderLimits);
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
     * Compare the {@link #transactionStatus} with
     * {@link TransactionConstantsIfc#STATUS_COMPLETED}.
     *
     * @see oracle.retail.stores.domain.transaction.TransactionIfc#isCompleted()
     * @see oracle.retail.stores.domain.transaction.TransactionConstantsIfc#STATUS_COMPLETED
     */
    public boolean isCompleted()
    {
        return transactionStatus == TransactionConstantsIfc.STATUS_COMPLETED;
    }

     /**
     * Compare the {@link #transactionStatus} with
     * {@link TransactionConstantsIfc#STATUS_CANCELED}.
     *
     * @see oracle.retail.stores.domain.transaction.TransactionIfc#isCanceled()
     * @see oracle.retail.stores.domain.transaction.TransactionConstantsIfc#STATUS_CANCELED
     */
    public boolean isCanceled()
    {
        return transactionStatus == TransactionConstantsIfc.STATUS_CANCELED;
    }

    /**
     * Compare the {@link #transactionStatus} with
     * {@link TransactionConstantsIfc#STATUS_SUSPENDED}.
     *
     * @see oracle.retail.stores.domain.transaction.TransactionIfc#isSuspended()
     * @see oracle.retail.stores.domain.transaction.TransactionConstantsIfc#STATUS_SUSPENDED
     */
    public boolean isSuspended()
    {
        return transactionStatus == TransactionConstantsIfc.STATUS_SUSPENDED;
    }



    /**
     * Compare the {@link #transactionStatus} with
     * {@link TransactionConstantsIfc#STATUS_VOIDED}.
     *
     * @see oracle.retail.stores.domain.transaction.TransactionIfc#isVoided()
     * @see oracle.retail.stores.domain.transaction.TransactionConstantsIfc#STATUS_VOIDED
     */
    public boolean isVoided()
    {
        return transactionStatus == TransactionConstantsIfc.STATUS_VOIDED;
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.domain.transaction.TransactionIfc#getSuspendReason()
     */
    public LocalizedCodeIfc getSuspendReason()
    {
        return suspendReason;
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.domain.transaction.TransactionIfc#setSuspendReason
     * (oracle.retail.stores.common.utility.LocalizedCodeIfc)
     */
    public void setSuspendReason(LocalizedCodeIfc suspendedReason)
    {
        this.suspendReason = suspendedReason;
    }

    /**
     * Retrieves customer information.
     *
     * @return CustomerInfo customerInfo
     */
    public CustomerInfoIfc getCustomerInfo()
    {
        return (customerInfo);
    }

    /**
     * Sets customer information.
     *
     * @param value CustomerInfo
     */
    public void setCustomerInfo(CustomerInfoIfc customerInfo)
    {
        this.customerInfo = customerInfo;
    }

    /**
     * Sets the post-processing status
     *
     * @param value the new status
     */
    public void setPostProcessingStatus(int value)
    {
        postProcessingStatus = value;
    }

    /**
     * Gets the post-processing status.
     *
     * @returns post-processing status
     */
    public int getPostProcessingStatus()
    {
        return postProcessingStatus;
    }

    /**
     * Returns string containing status descriptor.
     *
     * @param value status code
     * @return string containing descriptor for status passed in as parameter.
     */
    public static String statusToString(int value)
    {
        String statusString = new String();
        try
        {
            statusString = STATUS_DESCRIPTORS[value];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            statusString = "Invalid value [" + value + "]";
        }

        return (statusString);
    }

    /**
     * Returns string containing status descriptor.
     *
     * @return string containing descriptor for this object's status attribute
     */
    public String statusToString()
    {
        return (statusToString(getTransactionStatus()));
    }


    /**
     * Returns string containing post processing status descriptor.
     *
     * @return string containing descriptor for postprocessing status passed in
     *         as parameter.
     */
    public String postProcessingStatusToString()
    {
        return (postProcessingStatusToString(postProcessingStatus));
    }

    /**
     * Returns string containing post processing status descriptor.
     *
     * @param value status code
     * @return string containing descriptor for postprocessing status passed in
     *         as parameter.
     */
    public static String postProcessingStatusToString(int value)
    {
        String postProcessingStatusString = new String();
        try
        {
            postProcessingStatusString = STATUS_DESCRIPTORS[value];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            postProcessingStatusString = "Invalid value [" + value + "]";
        }

        return (postProcessingStatusString);
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
     * retrieves sales associate id
     *
     * @return sales associate id
     */
    public EmployeeIfc getSalesAssociate()
    {
        return salesAssociate;
    }

    /**
     * Sets sales associate id
     *
     * @param sales
     */
    public void setSalesAssociate(EmployeeIfc sales)
    {
        salesAssociate = sales;
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        StringBuilder strResult = Util.classToStringHeader("Transaction", getRevisionNumber(), hashCode()).append(
                Util.formatToStringEntry("Transaction ID", getTransactionID())).append(
                Util.formatToStringEntry("Transaction type", transactionType)).append(
                Util.formatToStringEntry("Transaction status", statusToString())).append(
                Util.formatToStringEntry("Previous status", statusToString(getPreviousTransactionStatus()))).append(
                Util.formatToStringEntry("Business day", getBusinessDay())).append(
                Util.formatToStringEntry("Start timestamp", timestampBegin)).append(
                Util.formatToStringEntry("End timestamp", timestampEnd)).append(
                Util.formatToStringEntry("Till ID", tillID)).append(
                Util.formatToStringEntry("Training Mode", trainingMode)).append(
                Util.formatToStringEntry("Suspend reason", suspendReason)).append(
                Util.formatToStringEntry("Cashier", getCashier())).append(
                Util.formatToStringEntry("Sales Associate ID", getSalesAssociate())).append(
                Util.formatToStringEntry("Tender limits", tenderLimits)).append(
                Util.formatToStringEntry("Customer info", customerInfo)).append(
                Util.formatToStringEntry("Transaction Country", transactionCountry)).append(
                Util.formatToStringEntry("Transaction Currency", transactionCurrency)).append(
                Util.formatToStringEntry("Transaction Currency Code", transactionCurrencyCode)).append(
                Util.formatToStringEntry("Post processing status", postProcessingStatusToString()));
        // pass back result
        return (strResult.toString());
    }

    /**
     * Write journal footer to string buffer.
     * @param journalLocale Locale received from the client.
     * @return journal fragment string
     */
    public String journalFooter(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        Date date = null;
        if (getTimestampEnd() == null)
        {
            date = new Date();
        }
        else
        {
            date = getTimestampEnd().dateValue();
        }

        String dateString = dateTimeService.formatDate(date, journalLocale, DateFormat.SHORT);
        String timeString = dateTimeService.formatTime(date, journalLocale, DateFormat.SHORT);
        strResult.append(Util.EOL);
        strResult.append(dateString);
        strResult.append(" ");
        strResult.append(timeString);
        return strResult.toString();
    }

    /**
     * Returns default journal string.
     *
     * @param journalLocale Locale received from the client.
     * @return default journal string
     */
    public String toJournalString(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();
        // append training mode banner
        if (isTrainingMode())
        {
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.TRAINING_MODE_LABEL, null,
                    journalLocale)).append(Util.EOL).append(Util.EOL);
        }
        // append transaction reentry mode banner
        if (isReentryMode())
        {
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.TRANSACTION_REENTRY_LABEL, null,
                    journalLocale)).append(Util.EOL).append(Util.EOL);
        }

        // format date and time
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        Date date = getTimestampBegin().dateValue();
        String dateString = dateTimeService.formatDate(date, journalLocale, DateFormat.SHORT);
        String timeString = dateTimeService.formatTime(date, journalLocale, DateFormat.SHORT);
        // String line = blockLine(new StringBuilder(dateString), new
        // StringBuilder(timeString));
        strResult.append(dateString);
        strResult.append(" ");
        strResult.append(timeString);
        // strResult.append(line);

        StringBuilder tillString = new StringBuilder(10);
        Object[] dataArgs = new Object[] { getTillID() };
        tillString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TILL_LABEL, dataArgs,
                journalLocale));

        Date businessDate = getBusinessDay().dateValue();
        dataArgs[0] = dateTimeService.formatDate(businessDate, journalLocale, DateFormat.SHORT);;
        strResult.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.BUSINESS_DATE_LABEL, dataArgs,
                journalLocale))
                .append(Util.EOL);

        // build transaction ID, etc.
        dataArgs[0] = getFormattedTransactionSequenceNumber();
        strResult.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TRANS_LABEL, dataArgs,
                journalLocale))
                .append(Util.EOL);

        dataArgs[0] = getFormattedStoreID();
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.STORE_LABEL, dataArgs,
                journalLocale)).append(Util.EOL);
        dataArgs[0] = getFormattedWorkstationID();

        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.REGISTER_LABEL, dataArgs,
                journalLocale)).append(Util.EOL);

        strResult.append(tillString.toString()).append(Util.EOL);

        dataArgs[0] = getCashier().getEmployeeID();
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.CASHIER_LABEL, dataArgs,
            journalLocale)).append(Util.EOL);

        if(getSalesAssociate() != null )
        {
            dataArgs[0] = getSalesAssociate().getEmployeeID();
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.SALES_ASSOC_LABEL, dataArgs,
                    journalLocale));
        }
        strResult.append(Util.EOL);
        // pass back result
        return (strResult.toString());
    }

    /**
     * Returns the revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * @return Returns the reentryMode.
     */
    public boolean isReentryMode()
    {
        return reentryMode;
    }

    /**
     * @param reentryMode The reentryMode to set.
     */
    public void setReentryMode(boolean reentryMode)
    {
        this.reentryMode = reentryMode;
    }

    public void setCaptureCustomer(CaptureCustomerIfc customer)
    {
        captureCustomer = customer;
    }

    public CaptureCustomerIfc getCaptureCustomer()
    {
        return captureCustomer;
    }

    public void setIsItemBasketTransactionComplete(boolean isBasketTrnType)
    {
      isBasketTransactionComplete = isBasketTrnType;
    }

    public boolean getIsItemBasketTransactionComplete()
    {
        return isBasketTransactionComplete;
    }
    /**
     * Returns the canceledTransactionPrinted flag
     * @return boolean
     */
    public boolean isCanceledTransactionPrinted() {
        return canceledTransactionPrinted;
    }

    /**
     * Sets the canceledTransactionPrinted flag
     * @param transactionPrinted
     */
    public void setCanceledTransactionPrinted(boolean transactionPrinted) {
        this.canceledTransactionPrinted = transactionPrinted;
    }

    /**
     * Returns the canceledTransactionSaved
     * @return boolean
     */
    public boolean isCanceledTransactionSaved() {
        return canceledTransactionSaved;
    }

    /**
     * Sets the canceledTransactionSaved flag
     * @param canceledTransactionSaved
     */
    public void setCanceledTransactionSaved(boolean canceledTransactionSaved) {
        this.canceledTransactionSaved = canceledTransactionSaved;
    }

    /** 
     * Returns the transaction type description
     */
    public String getTransactionTypeDescription()
    {
        int trnType = getTransactionType();

        String trnName = TransactionConstantsIfc.TYPE_DESCRIPTORS[trnType];
        return trnName;
    }

    /**
     * Returns the transaction status description
     */
    public String getTransactionStatusDescription()
    {
        int status = getTransactionStatus();
        String statusDesc = TransactionConstantsIfc.STATUS_DESCRIPTORS[status];

        return statusDesc;
    }
    
    /**
     * @return a boolean flag indicating if the transaction contains cross
     * channel order line item.
     */
    public boolean containsXChannelOrderLineItem()
    {
        return false;
    }
}
