/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/store/RegisterADO.java /main/24 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    mkutiana  02/05/14 - Fortify Null Derefernce fix
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    asinton   03/15/12 - Retrieve customer from CustomerManager after
 *                         retrieving the transaction.
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    acadar    06/11/10 - changes for postvoid and signature capture
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/20/10 - added support for REENTRY to isInMode method
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         5/6/2008 8:34:27 PM    Michael P. Barnett
 *         Implement RegisterADOIfc.  Reviewed by Anda Cadar.
 *    6    360Commerce 1.5         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    5    360Commerce 1.4         7/18/2007 8:43:35 AM   Alan N. Sinton  CR
 *         27651 - Made Post Void EJournal entries VAT compliant.
 *    4    360Commerce 1.3         1/25/2006 4:11:42 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:38 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:38 PM  Robert Pearse
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.domain.financial.Register;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.employee.EmployeeADOIfc;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.device.POSDeviceActions;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Represents the Workstation. This class also acts as the transaction factory.
 * It handles transaction creation and initialization.
 */
public class RegisterADO extends ADO implements RegisterADOIfc
{
    private static final long serialVersionUID = 7913128618342711815L;

    /** debug logger */
    private static final Logger logger = Logger.getLogger(RegisterADO.class);

    /** The Register RDO instance */
    protected RegisterIfc registerRDO;

    /**
     * The store associated with this register.
     */
    protected StoreADO storeADO;

    // Attributes from merge with VirtualRegisterDAO - Begin

    /** Reference to TenderLimits */
    protected TenderLimitsIfc tenderLimits;

    /** Last Reprintable Transaction ID */
    protected String lastReprintableTransactionID;

    /** Reference to current operator */
    protected EmployeeADOIfc operator;

    /** Reference to hard totals data (from POSDeviceActions or other */
    protected Serializable data;

    /**
     * Gets the current workstation number
     * 
     * @return the current workstation number
     */
    public String getWorkstationID()
    {
        return registerRDO.getWorkstation().getWorkstationID();
    }

    /**
     * Returns the ID of the currently active till
     * 
     * @return the Current Till ID
     */
    public String getCurrentTillID()
    {
        return registerRDO.getCurrentTillID();
    }

    /**
     * Returns the next unique ID
     * 
     * @return the next unique ID
     */
    public String getNextUniqueID()
    {
        return registerRDO.getNextUniqueID();
    }

    /**
     * Get instance of the RegisterJournalIfc. This method caches the instance,
     * which is then returned on future invocations.
     * 
     * @return a RegisterJournalIfc instance.
     */
    public RegisterJournalIfc getRegisterJournal()
    {
        return getJournalFactory().getRegisterJournal();
    }

    /**
     * Registers can be in different modes at any one time. This method simply
     * returns a boolean for a given mode to test to see if the register is
     * currently in the requested mode.
     * 
     * @param mode The mode for which we want the current status.
     * @return The status of the requested mode.
     */
    public boolean isInMode(RegisterMode mode)
    {
        boolean result = false;
        if (registerRDO == null)
        {
            result = false; // "should never happen"
        }
        else if (mode == RegisterMode.CASHIER_ACCOUNTABILITY
                && registerRDO.getAccountability() == RegisterIfc.ACCOUNTABILITY_CASHIER)
        {
            result = true;
        }
        else if (mode == RegisterMode.REGISTER_ACCOUNTABILITY
                && registerRDO.getAccountability() == RegisterIfc.ACCOUNTABILITY_REGISTER)
        {
            result = true;
        }
        else if (mode == RegisterMode.TRAINING && registerRDO.getWorkstation().isTrainingMode())
        {
            result = true;
        }
        else if (mode == RegisterMode.REENTRY && registerRDO.getWorkstation().isTransReentryMode())
        {
            result = true;
        }
        return result;
    }

    /**
     * Returns an array of all the currently active RegisterModes.
     * 
     * @return
     */
    public RegisterMode[] getRegisterModes()
    {
        ArrayList<RegisterMode> modeList = new ArrayList<RegisterMode>(3);

        // cashier/register accountability are mutually exclusive
        if (registerRDO.getAccountability() == RegisterIfc.ACCOUNTABILITY_CASHIER)
        {
            modeList.add(RegisterMode.CASHIER_ACCOUNTABILITY);
        }
        else if (registerRDO.getAccountability() == RegisterIfc.ACCOUNTABILITY_REGISTER)
        {
            modeList.add(RegisterMode.REGISTER_ACCOUNTABILITY);
        }

        if (registerRDO.getWorkstation().isTrainingMode())
        {
            modeList.add(RegisterMode.TRAINING);
        }

        if (registerRDO.getWorkstation().isTransReentryMode())
        {
            modeList.add(RegisterMode.REENTRY);
        }

        // convert list to array
        RegisterMode[] modes = new RegisterMode[modeList.size()];
        modes = modeList.toArray(modes);
        return modes;
    }

    /**
     * Initialize a txn with Register information. Encapsulated here due to work
     * that must be done with the RDO objects.
     * 
     * @param txn The transaction to be initialized.
     */
    public void initializeTransaction(RetailTransactionADOIfc txn)
    {
        if (logger.isInfoEnabled())
        {
            logger.debug("Initializing transaction...");
        }

        // Set RDO register related info on RDO txn
        TransactionIfc txnRDO = (TransactionIfc)((ADO)txn).toLegacy();
        txnRDO.setWorkstation(registerRDO.getWorkstation());
        txnRDO.setTillID(registerRDO.getCurrentTillID());
        txnRDO.setTrainingMode(isInMode(RegisterMode.TRAINING));
        txnRDO.setReentryMode(isInMode(RegisterMode.REENTRY));
        txnRDO.setTransactionSequenceNumber(registerRDO.getNextTransactionSequenceNumber());
    }

    /**
     * @return
     */
    public StoreADO getStoreADO()
    {
        return storeADO;
    }

    /**
     * @param storeADO
     */
    public void setStoreADO(StoreADO storeADO)
    {
        this.storeADO = storeADO;
    }
    
    /**
     * Attempts to load an ADO transaction from persistent storage
     * 
     * @param transactionID String value of desired transaction ID
     * @return a retrieved transaction instance
     * @deprecated As of 13.1 Use
     *             {@link RegisterADO#loadTransaction(LocaleRequestor, String)}
     */
    public RetailTransactionADOIfc loadTransaction(String transactionID) throws DataException
    {
        return loadTransaction(new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)),
                transactionID);
    }

    /**
     * Attempts to load an ADO transaction from persistent storage
     * 
     * @param transactionID String value of desired transaction ID
     * @return a retrieved transaction instance
     */
    public RetailTransactionADOIfc loadTransaction(LocaleRequestor localeReq, String transactionID)
            throws DataException
    {
        if (logger.isInfoEnabled())
        {
            logger.debug("Loading transaction...");
        }

        // attempt to retrieve the original RDO transaction
        TransactionIfc txnRDO = loadRDOTransaction(localeReq, transactionID);

        // check if customer exists for this transaction
        if(txnRDO instanceof TenderableTransactionIfc)
        {
            TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)txnRDO;
            if(StringUtils.isNotBlank(tenderableTransaction.getCustomerId()))
            {
                CustomerManagerIfc customerManager = (CustomerManagerIfc)Gateway.getDispatcher().getManager(CustomerManagerIfc.TYPE);
                CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, tenderableTransaction.getCustomerId(), localeReq);
                Locale extendedDataRequestLocale = null;
                if(operator != null && operator.toLegacy() instanceof EmployeeIfc)
                {
                    extendedDataRequestLocale = ((EmployeeIfc)operator.toLegacy()).getPreferredLocale();
                }
                if(extendedDataRequestLocale == null)
                {
                    extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                }
                criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
                //search for the customer
                CustomerIfc customer = customerManager.getCustomer(criteria);
                
                tenderableTransaction.setCustomer(customer);
               
            }
        }

        RetailTransactionADOIfc txnADO = null;
        if (txnRDO != null)
        {
            TransactionPrototypeEnum enumer = TransactionPrototypeEnum
                    .makeEnumFromTransactionType(txnRDO.getTransactionType());
            try
            {
                txnADO = enumer.getTransactionADOInstance();
            }
            catch (ADOException e)
            {
                // KLM: More thought probably needs to put into this and a more
                // suitable exception thrown, given that a
                // DataException seems to typically represents a database
                // related error. in any case, it should be incrementally
                // better in that caller will at least know something went
                // wrong.
                //
                throw new DataException(DataException.UNKNOWN, e.getMessage(), e);
            }
            ((ADO)txnADO).fromLegacy(txnRDO);
        }
        return txnADO;
    }

    

    /**
     * Attempt to load the transaction from persistent storage
     * 
     * @param txnID The ID of the desired transaction
     * @return An RDO transaction is retrieved, or null.
     */
    protected TransactionIfc loadRDOTransaction(LocaleRequestor localeReq, String txnID) throws DataException
    {
        // create and initialize search transaction used for searching
        TransactionIfc searchTransaction = DomainGateway.getFactory().getTransactionInstance();
        searchTransaction.initialize(txnID);
        if (searchTransaction.getBusinessDay() == null)
        {
            searchTransaction.setBusinessDay(storeADO.getBusinessDate());
        }
        searchTransaction.setTrainingMode(isInMode(RegisterMode.TRAINING));
        searchTransaction.setReentryMode(isInMode(RegisterMode.REENTRY));
        searchTransaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
        searchTransaction.setLocaleRequestor(localeReq);

        TransactionReadDataTransaction dbTxn = null;

        dbTxn = (TransactionReadDataTransaction)DataTransactionFactory
                .create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

        TransactionIfc loadedTransaction = null;
        try
        {
            loadedTransaction = dbTxn.readTransaction(searchTransaction);
        }
        catch (DataException de)
        {
            // KLM: No need to log here since we either re-throw
            // or we successfully load the transaction from the
            // list of suspended transactions
            //
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                // try another read, this time for suspended transactions
                try
                {
                    searchTransaction.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
                    loadedTransaction = dbTxn.readTransaction(searchTransaction);
                }
                catch (DataException e)
                {
                    // KLM: No need to log since we're rethrowing
                    //
                    throw e;
                }

            }
            else
            {
                throw de;
            }
        }
        catch (Exception e)
        {
            // KLM: No need to log since we're rethrowing
            //
            throw new DataException(DataException.UNKNOWN, e.getMessage(), e);
        }

        return loadedTransaction;
    }

    /**
     * Creates and initializes a new ADO transaction based on input parameters.
     * NOTE: Be sure only to use this when creating a NEW transaction. Do not
     * use this if, for example, an RDO transaction already exists and an ADO
     * transaction needs to be built from that RDO because the initialization
     * routine will assign a new transaction number, but the RDO transaction
     * already has one.
     * 
     * @param attributes Map containing information needed to generate
     *            transaction
     * @param customerInfoRDO The customer info information for this
     *            transaction.
     * @param operatorRDO The current cashier/operator.
     * @return a new ADO transaction instance
     */
    public RetailTransactionADOIfc createTransaction(TransactionPrototypeEnum type, CustomerInfoIfc customerInfoRDO,
            EmployeeIfc operatorRDO)
    {
        // get a cloned copy of prototype transaction
        RetailTransactionADOIfc txn = null;
        try
        {
            txn = type.getTransactionADOInstance();
            ((AbstractRetailTransactionADO)txn).setParameterManager(getParameterManager());
            // initialize the transaction with information
            ((AbstractRetailTransactionADO)txn).initialize(customerInfoRDO, operatorRDO, this);
        }
        catch (ADOException e)
        {
            logger.error("Error obtaining RetailTransactionADO: " + e.getMessage(), e);
        }        

        return txn;
    }

    /**
     * Add the transaction to the RDO register to make sure totals are updated
     * 
     * @param txnADO The transaction to be added
     */
    public void addTransaction(RetailTransactionADOIfc txnADO)
    {
        if (logger.isInfoEnabled())
        {
            logger.info("Adding transaction...");
        }

        if (!isInMode(RegisterMode.TRAINING))
        {
            registerRDO.addTransaction((TenderableTransactionIfc)((ADO)txnADO).toLegacy());
        }
    }

    /**
     * Establish POSDeviceActions before calling private
     * writeHardTotals(POSDevicActions). This change was made to expose
     * POSDeviceActions for unit testing.
     * 
     * @throws DeviceException Thrown when
     */
    public void writeHardTotals() throws DeviceException
    {
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)TourContext.getInstance().getTourBus());
        writeHardTotals(pda);
    }

    /**
     * The register, which contains all the information needed, should be
     * responsible for writing the hard totals
     * 
     * @throws DeviceException Thrown when
     */
    public void writeHardTotals(POSDeviceActions pda) throws DeviceException
    {
        // set up hard totals
        HardTotalsIfc ht = DomainGateway.getFactory().getHardTotalsInstance();
        ht.setStoreStatus((StoreStatusIfc)storeADO.toLegacy());
        ht.setRegister((RegisterIfc)toLegacy());
        ht.setLastUpdate();

        try
        {
            HardTotalsBuilderIfc builder = DomainGateway.getFactory().getHardTotalsBuilderInstance();
            ht.getHardTotalsData(builder);
            // POSDeviceActions pda = new
            // POSDeviceActions((SessionBusIfc)((TourADOContext)getContext()).getBus());
            pda.writeHardTotals(builder.getHardTotalsOutput());
        }
        catch (HardTotalsFormatException htre)
        {
            // No need to log here since we're rethrowing
            //
            throw new DeviceException(0, "Hard Totals Data Format Error", htre);
        }
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        RegisterIfc register = (RegisterIfc)rdo;
        registerRDO = register;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return registerRDO;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    public EYSDomainIfc toLegacy(Class type)
    {
        EYSDomainIfc result = null;
        if (type == RegisterIfc.class || type == Register.class)
        {
            result = toLegacy();
        }
        return result;
    }

    /**
     * @return TenderLimits reference
     */
    public TenderLimitsIfc getTenderLimits()
    {
        return tenderLimits;
    }

    /**
     * @param ifc reference to TenderLimits
     */
    public void setTenderLimits(TenderLimitsIfc ifc)
    {
        tenderLimits = ifc;
    }

    /**
     * Sets identifier of last reprintable transaction.
     * 
     * @param value new identifier of last reprintable transaction
     */
    public void setLastReprintableTransactionID(String value)
    {
        lastReprintableTransactionID = value;
    }

    /**
     * Returns identifier of last reprintable transaction.
     * 
     * @return identifier of last reprintable transaction
     */
    public String getLastReprintableTransactionID()
    {
        return (lastReprintableTransactionID);
    }

    /**
     * @return
     */
    public EmployeeADOIfc getOperator()
    {
        return operator;
    }

    /**
     * @param ifc
     */
    public void setOperator(EmployeeADOIfc ifc)
    {
        operator = ifc;
    }

    /**
     * @return data
     */
    public Serializable getHardTotalsStream()
    {
        return (this.data);
    }

    /**
     * @param data
     */
    public void setHardTotalsStream(Serializable data)
    {
        this.data = data;
    }
}