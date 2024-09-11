/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/manager/utility/TransactionUtilityManager.java /main/11 2014/03/24 14:16:54 mjwallac Exp $
 * ===========================================================================
 * Rev 1.0	Sep 01,2016	Ashish Yadav	Changes done for code merging
 *
 * ===========================================================================
 */
package max.retail.stores.pos.manager.utility;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionWriteDataTransaction;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tax.TaxRateCalculatorIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.RelatedItemTransactionInfoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.ado.utility.tdo.TaxTDO;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.archive.ArchiveException;
import oracle.retail.stores.pos.manager.archive.RetailTransactionArchiveEntry;
import oracle.retail.stores.pos.manager.archive.RetailTransactionArchiveEntryIfc;
import oracle.retail.stores.pos.manager.archive.TransactionArchiveManagerIfc;
import oracle.retail.stores.pos.manager.utility.TransactionUtilityManager;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.WriteHardTotalsCargoIfc;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.timer.TimeoutSettingsUtility;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Split from {@link UtilityManager} to only include {@link BusIfc}-related
 * utilities.
 *
 *
 * @author cgreene
 * @since 13.4.1
 */
public class MAXTransactionUtilityManager extends TransactionUtilityManager
{
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(TransactionUtilityManager.class);

    /**
     * Empty sales associate for Journal Index
     */
    protected static final String EMPTY_SALESASSOCIATE = "EMPTYASSOC";

    /**
     * Empty sales associate for Journal Index
     */
    protected static final String MANUAL_ENTRY_ID = "ManualEntryID";

    /**
     * Hashtable with tax group and have a vector of tax rules.
     */
    protected static final Map<String,Map<Integer,List<TaxRuleIfc>>> taxGroupTaxRulesMapping = new HashMap<String,Map<Integer,List<TaxRuleIfc>>>(1);

    protected static RelatedItemTransactionInfoIfc relatedItemTransInfo = null;

    protected static boolean restockingFeeOverriddenTransaction = false;
    
    protected TransactionArchiveManagerIfc archiveMgr = null;

    /**
     * The default constructor; sets up a unique address.
     *
     * @exception IllegalStateException is thrown if the manager cannot be
     *                created.
     */
    public MAXTransactionUtilityManager()
    {
    	super();
    }


    /**
     * Initializes a new transaction and writes a journal entry.
     *
     * @param trans Transaction to initialize
     * @param seq A supplied sequence number, -1 if none
     * @param custID The customer id, null if none
     */
    public void initializeTransaction(TransactionIfc trans, long seq, String custID)
    {

        // get cargo reference
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc) getBus().getCargo();

        boolean writeTransactionToJournal = true;
        // always write exit training mode txn to journal
        if (trans.getTransactionType() != TransactionIfc.TYPE_EXIT_TRAINING_MODE)
        {
            writeTransactionToJournal = journalTransaction(cargo.getRegister().getWorkstation().isTrainingMode());
        }

        // Since we're about to start using a new transaction, index the
        // previous one in the journal. The transaction is indexed using the
        // transactionID(combination of storeID + RegisterID + sequenceNumber)
        // which is set to sequenceNumber in the JournalManager. Need to
        // change it

        // store loginid or employeeid based upon ManualEntryID parameter

        JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher()
                                                 .getManager(JournalFormatterManagerIfc.TYPE);
        JournalManagerIfc journal = null;
        ParameterManagerIfc pm = (ParameterManagerIfc) getBus().getManager(ParameterManagerIfc.TYPE);
        if (writeTransactionToJournal)
        {
            String meidParamValue = null;
            try
            {
                meidParamValue = pm.getStringValue(MANUAL_ENTRY_ID);
            }
            catch (ParameterException pe)
            {
                logger.error("ParameterException caught in UtilityManager.initializeTransaction(): ", pe);
                throw new IllegalStateException(
                        "ParameterException received in UtilityManager.initializeTransaction():" + pe.getMessage());
            }
            journal = (JournalManagerIfc)getBus().getManager(JournalManagerIfc.TYPE);
            String sequenceNo = journal.getSequenceNumber();
            if (sequenceNo != null && sequenceNo != "")
            {
                indexTransactionInJournal(sequenceNo);
            }

            // set storeID,registerID,cashierID,salesAssociateId for journal
            journal.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            journal.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
            if (meidParamValue.equalsIgnoreCase("Employee"))
            {
                journal.setCashierID(cargo.getOperator().getEmployeeID());
            }
            else
            {
                journal.setCashierID(cargo.getOperator().getLoginID());
            }
            if (trans.getSalesAssociate() != null && trans.getSalesAssociate().getEmployeeID() != null)
            {
                if (trans.getSalesAssociate().getEmployeeID().equals(""))
                {
                    // This is being done for Journal Index file so that
                    // the file entry is correctly parsed.
                    journal.setSalesAssociateID(EMPTY_SALESASSOCIATE);

                }
                else
                {
                    if (meidParamValue.equalsIgnoreCase("Employee"))
                    {
                        journal.setSalesAssociateID(trans.getSalesAssociate().getEmployeeID());
                    }
                    else
                    {
                        journal.setSalesAssociateID(trans.getSalesAssociate().getLoginID());
                    }
                }
            }
            else
            {
                if (meidParamValue.equalsIgnoreCase("Employee"))
                {
                    journal.setSalesAssociateID(cargo.getOperator().getEmployeeID());
                }
                else
                {
                    journal.setSalesAssociateID(cargo.getOperator().getLoginID());
                }
            }
            if (getRestockingFeeOverRiddenTransaction())
            {
                journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
            }
            else
            {
                journal.setEntryType(JournalableIfc.ENTRY_TYPE_START);
            }
            journal.setBusinessDate(cargo.getStoreStatus().getBusinessDate());
        }

        // set workstation ID, business date
        trans.setCashier(cargo.getOperator());
        trans.setWorkstation(cargo.getRegister().getWorkstation());
        trans.setTillID(cargo.getRegister().getCurrentTillID());
        trans.setBusinessDay(cargo.getStoreStatus().getBusinessDate());
        trans.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
        trans.setCustomerInfo(cargo.getCustomerInfo());
        
        // below the attributes/Data members are need to set for Cross Border
        // Return
        
        if (trans instanceof RetailTransactionIfc)
        {
            RetailTransactionIfc tran = (RetailTransactionIfc)trans;            
            tran.setTransactionCountryCode(cargo.getRegister().getWorkstation().getStore().getAddress().getCountry());           
        }
        if (trans instanceof BillPayTransactionIfc)
        {
        	BillPayTransactionIfc tran = (BillPayTransactionIfc)trans;            
            tran.setTransactionCountryCode(cargo.getRegister().getWorkstation().getStore().getAddress().getCountry());           
        }

        // set the TenderLimits object for transaction level checking
        trans.setTenderLimits(cargo.getTenderLimits());
        if ((trans instanceof TenderableTransactionIfc) && trans.getTenderLimits() == null)
        {
            logger.error("Tender limits are null.");
            throw new NullPointerException("UtilityManager.initializeTransaction() - null TenderLimits reference.");
        }

        if (trans instanceof SaleReturnTransactionIfc)
        {
            ((SaleReturnTransactionIfc) trans).setTransactionTax(getInitialTransactionTax());

        } // end if SaleReturnTransaction

        // get a sequence number
        long sequenceNumber;

        if (seq == -1)
        {
            // Get the next sequence number
            sequenceNumber = cargo.getRegister().getNextTransactionSequenceNumber();
            // Record the sequence number in hard totals
            try
            {
			// Chnages starts for Rev 1.0
                //writeHardTotals();
  // Changes start for code merging(in base 14 below method does not accept bus, but in base 12 or max customised version it accepts bus as parameter)
			//writeHardTotals(bus);
            	writeHardTotals();
  // Changes for code merging ends
			// Changes ends for rev 1.0
            }
            catch (DeviceException e)
            {
                logger.error("Unable to update hard totals with next used transaction number: " + sequenceNumber, e);
            }

            if (trans.getTransactionStatus() == TransactionIfc.STATUS_UNKNOWN)
            { // this covers those places that create a transaction and don't
                // bother to set the status.
                trans.setTransactionStatus(TransactionIfc.STATUS_IN_PROGRESS);
            }
        }
        else
        { // Use the one supplied to us
            sequenceNumber = seq;
        }

        trans.setTransactionSequenceNumber(sequenceNumber);
        trans.buildTransactionID();
        trans.setTimestampBegin();
        trans.setReentryMode(cargo.getRegister().getWorkstation().isTransReentryMode());

        // If no customer has been linked reset subsystem defaults.
        if (Util.isEmpty(custID))
        {
            // Set up default locales for pole display and receipt
            Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);

            UIUtilities.setUILocaleForCustomer(defaultLocale);
        }
        if (seq == -1)
        {
            // If we did build a new one, log and journal the fact
            // that we have a new number
            if (logger.isInfoEnabled())
                logger.info("Transaction ID created:  " + trans.getTransactionID() + "");
            // write journal entry
            if (writeTransactionToJournal)
            {
                if (journal != null)
                {
                    journal.setSequenceNumber(trans.getTransactionID());
                    // journal customer ID
                    if (custID != null)
                    {
                        StringBuilder journalTxt = new StringBuilder();

                        journalTxt.append(formatter.toJournalString(trans, pm));
                        journalTxt.append(Util.EOL
                                + Util.EOL
                                + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                        JournalConstantsIfc.ENTERING_CUSTOMER_LABEL, null));
                        Object[] dataArgs = new Object[2];
                        dataArgs[0] = custID;
                        journalTxt.append(Util.EOL
                                + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                        JournalConstantsIfc.LINK_CUSTOMER_LABEL, dataArgs));
                        journal.journal(cargo.getOperator().getLoginID(), trans.getTransactionID(), journalTxt
                                .toString());
                    }
                    else
                    {
                        if (!(trans instanceof OrderTransactionIfc))
                        {
                            journal.journal(cargo.getOperator().getLoginID(), trans.getTransactionID(), formatter
                                    .toJournalString(trans, pm));
                        }
                        else
                        {
                            // Journal a "dummy" JournalableIfc.ENTRY_TYPE_START
                            // entry so that a single transaction's entry can
                            // be grouped together.
                            journal.journal("");
                        }
                    }
                    journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
                }
                else
                {
                    logger.error("No JournalManager found");
                }
            }
        }

        // A new transaction is opened, set the transaction status flag to true.
        // This tells the timer to use TimeoutInactiveWithTransaction parameter.
        TimeoutSettingsUtility.setTransactionActive(true);
    }

    /**
     * Initializes a new transaction and writes a journal entry.
     *
     * @param trans Transaction to initialize
     */
    public void initializeTransaction(TransactionIfc trans)
    {
        initializeTransaction(trans, GENERATE_SEQUENCE_NUMBER, null);
    }

    /**
     * Initializes a new transaction and writes a journal entry.
     *
     * @param trans Transaction to initialize
     * @param seq A supplied sequence number, -1 if none
     */
    public void initializeTransaction(TransactionIfc trans, long seq)
    {
        initializeTransaction(trans, seq, null);
    }

    /**
     * Saves the transaction to the database.
     *
     * @param trans The transaction to save to persistent storage
     * @exception DataException thrown if error occurs processing the
     *                transaction
     */
    public void saveTransaction(TransactionIfc trans) throws DataException
    {
        // Set the complete transaction journaling flag.
        saveTransaction(trans, true);
    }

    /**
     * Saves the transaction to the database.
     *
     * @param trans The transaction to save to persistent storage
     * @param journalEndOfTransaction if true, forces end of transaction
     *            journaling.
     * @exception DataException thrown if error occurs processing the
     *                transaction
     */
    public void saveTransaction(TransactionIfc trans, boolean journalEndOfTransaction) throws DataException
    {
        saveTransaction(trans, null, null, null, journalEndOfTransaction);
    }

    /**
     * Saves the transaction to the database.
     *
     * @param trans The transaction to save to persistent storage
     * @param totals financial totals for the transaction
     * @param till till which processed the transaction
     * @param register register which processed the transaction
     * @exception DataException thrown if error occurs processing the
     *                transaction
     */
    public void saveTransaction(TransactionIfc trans, FinancialTotalsIfc totals, TillIfc till, RegisterIfc register)
            throws DataException
    {

        // Set the end of transaction journaling flag.
        saveTransaction(trans, totals, till, register, true);

    }

    /**
     * Saves the transaction to the database.
     *
     * @param trans The transaction to save to persistent storage
     * @param totals financial totals for the transaction
     * @param till till which processed the transaction
     * @param register register which processed the transaction
     * @param journalEndOfTransaction if true, forces end of transaction
     *            journaling.
     * @exception DataException thrown if error occurs processing the
     *                transaction
     */
    public void saveTransaction(TransactionIfc trans, FinancialTotalsIfc totals, TillIfc till,
            RegisterIfc register, boolean journalEndOfTransaction) throws DataException
    {
        // set transaction name and instantiate data transaction
        TransactionWriteDataTransaction dbTrans = null;

        if (trans.getTransactionType() == TransactionIfc.TYPE_CLOSE_REGISTER
                || trans.getTransactionType() == TransactionIfc.TYPE_CLOSE_STORE)
        {
            dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION);
        }
        else
        {
            dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.TRANSACTION_WRITE_DATA_TRANSACTION);
        }

        try
        {
            if (trans.getTransactionStatus() == TransactionIfc.STATUS_IN_PROGRESS)
            { // this covers those places that complete a transaction and
                // don't bother to update the status.
                trans.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
            }
            if (trans.getTimestampEnd() == null)
            { // this covers those places that complete a transaction and
                // don't bother to set the end timestamp. Lookups for
                // transaction export
                // depends on this being set in the client so that the ordering
                // of the
                // the transactions in the extract is correct.
                trans.setTimestampEnd();
            }
            
          	// 17312379 Logging addition for field support
            String tranID = null;
            String dbTranName = null;
            
            if (logger.isInfoEnabled())
            {
            	tranID = trans.getTransactionID();
            	dbTranName = dbTrans.getTransactionName();
            	int tranType = trans.getTransactionType();
            	int tranStatus = trans.getTransactionStatus();
            	StringBuffer sb = new StringBuffer();
            	sb.append("TransactionUtilityManager.saveTransaction() - before DM Call. Transaction ID: ");
            	sb.append(tranID);
            	sb.append(", Transaction Type: ");
            	sb.append(tranType);
            	sb.append(", Transaction Status: ");
            	sb.append(tranStatus);
            	sb.append(", DataTransaction Name: ");
            	sb.append(dbTranName);
            	logger.info(sb.toString());
            }
            dbTrans.saveTransaction(trans, totals, till, register);
            
          	// 17312379 Logging addition for field support
            if (logger.isInfoEnabled())
            {
            	logger.info("TransactionUtilityManager.saveTransaction() - before DM Call. Transaction ID: " + tranID);
            }
            
        }
        catch (DataException e)
        {
            logger.error("An error occurred saving the transaction: " + e + "");
            throw e;
        }

        if (journalEndOfTransaction)
        {
            if (journalTransaction(trans.isTrainingMode()))
            {
                completeTransactionJournaling(trans);
            }
        }

        // The transaction is successfully persisted, set the transaction status
        // flag to true. This tells the timer to use TimeoutInactiveWithoutTransaction
        // parameter for screen timeout.
        TimeoutSettingsUtility.setTransactionActive(false);
    }

    /**
     * Performs journaling housekeeping for a completed transaction
     *
     * @param trans the transaction
     */
    public void completeTransactionJournaling(TransactionIfc trans)
    {
        // Add the Journal Footer to the this transaction
        JournalManagerIfc journal = (JournalManagerIfc)getBus().getManager(JournalManagerIfc.TYPE);
        journal.setRegisterID(trans.getWorkstation().getWorkstationID());
        StringBuilder journalTxt = new StringBuilder();
        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        journalTxt.append(trans.journalFooter(journalLocale));
        journal.setEntryType(JournalableIfc.ENTRY_TYPE_END);
        setRestockingFeeOverriddenTransaction(false);
        journal.journal(journalTxt.toString());
        indexTransactionInJournal(journal.getSequenceNumber());
        journal.setEntryType(JournalableIfc.ENTRY_TYPE_NOTTRANS);
        journal.setSalesAssociateID("");
        journal.setCashierID("");
    }

    /**
     * Saves the transaction to the database.
     *
     * @param trans The transaction to save to persistent storage
     * @param till till which processed the transaction
     * @param register register which processed the transaction
     * @exception DataException thrown if error occurs processing the
     *                transaction
     */
    public void saveTransaction(TransactionIfc trans, TillIfc till, RegisterIfc register) throws DataException
    {
        saveTransaction(trans, null, till, register);
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc#writeHardTotals()
     */
    public void writeHardTotals() throws DeviceException
    {
       writeHardTotals(null);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc#writeHardTotals(String)
     */
    public void writeHardTotals(String prefix) throws DeviceException
    {
        // get cargo reference
        WriteHardTotalsCargoIfc cargo = (WriteHardTotalsCargoIfc) getBus().getCargo();
        RegisterIfc register = cargo.getRegister();

        if (register != null)
        {
            // write hard totals
            POSDeviceActions pda = new POSDeviceActions(getBus());

            // set up hard totals
            HardTotalsIfc ht = DomainGateway.getFactory().getHardTotalsInstance();
            ht.setStoreStatus(cargo.getStoreStatus());
            ht.setRegister(cargo.getRegister());
            ht.setLastUpdate();

            try
            {
                HardTotalsBuilderIfc builder = DomainGateway.getFactory().getHardTotalsBuilderInstance();
                ht.getHardTotalsData(builder);
                if (Util.isEmpty(prefix))
                {
                    pda.writeHardTotals(builder.getHardTotalsOutput());
                }
                else
                {
                    pda.writeHardTotals(builder.getHardTotalsOutput(), prefix);
                }
            }
            catch (HardTotalsFormatException htre)
            {
                DeviceException de = new DeviceException(0, "Hard Totals Data Format Error", htre);
                throw de;
            }
        }
    }

    /**
     * This method initializes the transaction tax object. It is called every
     * time a transaction is initialized and the parameter is retrieved every
     * time. The value of the default tax amount can be down loaded or a user
     * can change it while the client is running.
     *
     * @return The transaction tax object.
     */
    public TransactionTaxIfc getInitialTransactionTax()
    {
        // Get the parameter manager
        ParameterManagerIfc pm = (ParameterManagerIfc) getBus().getManager(ParameterManagerIfc.TYPE);

        // create a TransactionTax object with a default rate of 0
        TransactionTaxIfc tax = DomainGateway.getFactory().getTransactionTaxInstance();
        tax.setDefaultRate(0.0);

        try
        {
            double dbl = pm.getDoubleValue(ParameterConstantsIfc.TAX_TaxRate).doubleValue();
            if (dbl >= 0 && dbl <= 100)
            {
                // set the transaction tax rate to the default local tax rate
                // (TaxRate from the properties file divided by 100)
                tax.setDefaultRate(dbl / 100);
            }
        }
        catch (Exception e)
        {
            logger.error(getName() + " cannot find tax default tax parameter.");
        }

        try
        {
            // Get the default tax rule and set it on the transaction tax object
            TaxRuleIfc rule = getDefaultTaxRule(pm);
            tax.setDefaultTaxRules(new TaxRuleIfc[] { rule });
            if (rule.getTaxCalculator() instanceof TaxRateCalculatorIfc)
            {
                TaxRateCalculatorIfc calculator = (TaxRateCalculatorIfc) rule.getTaxCalculator();
                tax.setDefaultRate(calculator.getTaxRate().doubleValue());
            }
        }
        catch (Exception e)
        {
            if (tax.getDefaultRate() == 0.0)
            {
                logger.error(getName() +
                        " cannot find tax rule matching default tax authority and group.  " +
                        "Default tax rate will be 0.0.", e);
            }
            else
            {
                logger.error(getName() + " cannot find tax rule matching default tax authority and group.  "
                        + "Default tax rate comes from the parameters and will be " + tax.getDefaultRate() + ".", e);
            }
        }

        return tax;
    }

    /**
     * This method gets the default tax rule. It is called every time a
     * transaction is initialized and the parameters are retrieved every time.
     * The parameters can be down loaded while the client is running. mapping
     * object.
     *
     * @return default rule
     * @throws Exception
     */
    protected TaxRuleIfc getDefaultTaxRule(ParameterManagerIfc pm) throws Exception
    {
        TaxRuleIfc defaultTaxRule = null;

        // Initialize the default tax authority and group IDs from parameters
        int defaultTaxAuthorityID = getDefaultTaxAuthorityID(pm);
        int defaultTaxGroupID = getDefaultTaxGroupID(pm);

        // Get the rules associated with the default tax group
        TaxRuleIfc taxRule = null;
        StoreStatusIfc status = getStoreStatus();
        Map<Integer,List<TaxRuleIfc>> ruleMapping = getTaxGroupTaxRulesMapping(status.getStore().getStoreID());
        List<TaxRuleIfc> taxRules = ruleMapping.get(defaultTaxGroupID);
        if (taxRules != null)
        {
            // Take the first rules that matches the default tax authority
            for (int i = 0; i < taxRules.size(); i++)
            {
                taxRule = taxRules.get(i);
                if (taxRule.getTaxAuthorityID() == defaultTaxAuthorityID)
                {
                    defaultTaxRule = taxRule;
                    break;
                }
            }
        }

        // The tax rule is still null throw an exception.
        if (defaultTaxRule == null)
        {
            throw new NullPointerException("Unable to determine default tax rate.");
        }

        return defaultTaxRule;
    }

    /**
     * Gets the default tax authority id from the parameter manager
     *
     * @param pm parameter manager
     * @return the default tax authority id
     */
    protected int getDefaultTaxAuthorityID(ParameterManagerIfc pm)
    {
        // Attempt to get the default tax authority id from the parameter
        // manager
        int taxAuthorityID = 0;
        try
        {
            taxAuthorityID = pm.getIntegerValue(ParameterConstantsIfc.TAX_DefaultTaxAuthorityID);
        }
        catch (ParameterException pe)
        {
            logger.error(pe.getMessage());
            throw new IllegalStateException(
                    "ParameterException for 'DefaultTaxAuthorityID' received in UtilityManager.getDefaultTaxAuthorityID(): "
                            + pe.getMessage());
        }

        return taxAuthorityID;
    }

    /**
     * Gets the default tax group id from the parameter manager
     *
     * @param pm parameter manager
     * @return the default tax authority id
     */
    protected int getDefaultTaxGroupID(ParameterManagerIfc pm)
    {
        // Attempt to get the default tax group id from the parameter manager
        int taxGroupID = 0;
        try
        {
            taxGroupID = pm.getIntegerValue("DefaultTaxGroupID");
        }
        catch (ParameterException pe)
        {
            logger.error(pe.getMessage());
            throw new IllegalStateException(
                    "ParameterException for 'DefaultTaxGroupID' received in UtilityManager.getDefaultTaxGroupID():"
                            + pe.getMessage());
        }

        return taxGroupID;
    }

    /**
     * Get the map of tax rules keyed by tax group. The rules are lazily
     * initialized if null. This manager caches the rules by store number to
     * speed up PLU operations.
     *
     * @param storeId the store id for the desired rule mappings.
     * @return map of tax groups with their tax rules or null if store status is
     *          not set.
     */
    public Map<Integer,List<TaxRuleIfc>> getTaxGroupTaxRulesMapping(String storeId)
    {
        synchronized (taxGroupTaxRulesMapping)
        {
            Map<Integer,List<TaxRuleIfc>> mapping = taxGroupTaxRulesMapping.get(storeId);
            if (mapping == null)
            {
                StoreStatusIfc status = getStoreStatus();
                if (status != null)
                {
                    StoreIfc si = status.getStore();
                    String storeID = si.getStoreID();
                    String state = si.getAddress().getState();
                    try
                    {
                        TaxTDO taxTDO = (TaxTDO)TDOFactory.create("tdo.utility.Tax");
                        mapping = taxTDO.createTaxGroupTaxRuleMappingForStore(storeID, state);
                        taxGroupTaxRulesMapping.put(storeId, mapping);
                    }
                    catch (Exception e)
                    {
                        logger.warn("Error intializing local store tax rules", e);
                    }
                }
            }
            return mapping;
        }
    }

    /**
     * Set the map of tax rules keyed by tax group. Set the rules beforehand to
     * prevent lazy initialization in {@link #getTaxGroupTaxRulesMapping()}.
     *
     * @param storeId the store id for the desired rule mappings.
     * @param taxRuleMapping map keyed with tax group, and have list of tax rules
     */
    public void setTaxGroupTaxRulesMapping(String storeId, Map<Integer,List<TaxRuleIfc>> taxRuleMapping)
    {
        synchronized (taxGroupTaxRulesMapping)
        {
            taxGroupTaxRulesMapping.put(storeId, taxRuleMapping);
        }
    }

    public static void setRestockingFeeOverriddenTransaction(boolean status)
    {
        restockingFeeOverriddenTransaction = status;
    }

    public boolean getRestockingFeeOverRiddenTransaction()
    {
        return restockingFeeOverriddenTransaction;
    }

    /**
     * This method gets the related item transacion info.
     *
     * @return Returns the relatedItemTransInfo.
     */
    public RelatedItemTransactionInfoIfc getRelatedItemTransInfo()
    {
        return relatedItemTransInfo;
    }

    /**
     * @param relatedItemTransInfo The relatedItemTransInfo to set.
     */
    public void setRelatedItemTransInfo(RelatedItemTransactionInfoIfc relatedItemTransInfo)
    {
        TransactionUtilityManager.relatedItemTransInfo = relatedItemTransInfo;
    }

    /**
     * Returns the store status from the cargo if available.
     *
     * @return
     * @see AbstractFinancialCargoIfc#getStoreStatus()
     */
    protected StoreStatusIfc getStoreStatus()
    {
        CargoIfc cargo = getBus().getCargo();
        if (cargo instanceof AbstractFinancialCargoIfc)
        {
            AbstractFinancialCargoIfc fCargo = (AbstractFinancialCargoIfc)cargo;
            return fCargo.getStoreStatus();
        }
        return null;
    }

    /**
     * Determine if transaction has any employee discounts applied to items
     * being sold. This method is used by the reciept generating logic to get
     * the employee ID and determine if an Employee discount receipt should be
     * printed.
     *
     * @param trans Sale Return Transaction with potential Employee discounts
     * @return null if no employee discount found
     */
    public String getEmployeeIDForEmployeeDiscountReceipt(SaleReturnTransactionIfc trans)
    {
        String foundEmployeeNumber = null;

        // Get the line items
        AbstractTransactionLineItemIfc[] lineItems = trans.getLineItems();

        // Iterate through the line items
        for (AbstractTransactionLineItemIfc lineItem : lineItems)
        {
            // If this is a sale item ..
            if (lineItem instanceof SaleReturnLineItemIfc && !((SaleReturnLineItemIfc) lineItem).isReturnLineItem())
            {
                // Get the discounts
                ItemDiscountStrategyIfc[] discounts = ((SaleReturnLineItemIfc) lineItem).getItemPrice()
                        .getItemDiscounts();

                // Iterate through the discounts
                for (ItemDiscountStrategyIfc discount : discounts)
                {
                    // If one is an employee discount ..
                    if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                    {
                        foundEmployeeNumber = discount.getDiscountEmployeeID();
                        break;
                    }
                }
            }

            // If an employee disount has been found, get out of the loop.
            if (!Util.isEmpty(foundEmployeeNumber))
            {
                break;
            }
        }
        return foundEmployeeNumber;
    }

    /**
     * Indexes transaction in journal file using string transaction ID.
     *
     * @param tid transaction ID
     */
    public void indexTransactionInJournal(String tid)
    {

        StringBuilder data = new StringBuilder();

        data.append(tid);
        data.append(" ");

        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        Date now = new Date();
        // use the same date format for all locales in jnlindex.dat (ej saved in
        // local filesystem)
        // and journal (jl_enr) table for ej saved in database so that ej search
        // will work for
        // all locales.
        String date = dateTimeService.formatDate(now, defaultLocale, "MM/dd/yyyy");
        String time = dateTimeService.formatTime(now, defaultLocale, "HH:mm");
        data.append(date).append(" ").append(time).append(" ");

        JournalManagerIfc jmi = (JournalManagerIfc)getBus().getManager(JournalManagerIfc.TYPE);

        if (jmi != null)
        {
            data.append(jmi.getCashierID());
            data.append(" ");
            data.append(jmi.getSalesAssociateID());

            data.append(" ");
            data.append(jmi.getRegisterID());

            jmi.index(tid, data.toString());
        }
    }

    /**
     * If we are in training mode, this method will check the
     * SendTrainingModeTransactionToJournal parameter to see if it set to Y. If
     * it is set to Y, the training mode transaction should be written to the
     * e-journal
     *
     * @param trainingModeOn - set to true for training mode
     * @return true if the transaction should be written to the e-journal
     */
    public boolean journalTransaction(boolean trainingModeOn)
    {
        boolean journalOn = true;
        // If in training mode then check to see if
        // SendTrainingModeTransactionsToJournal parameter
        // is set to Yes to journal training mode transactions
        if (trainingModeOn)
        {
            // get parameter manager
            UtilityIfc util = null;
            try
            {
                util = Utility.createInstance();
            }
            catch (ADOException e)
            {
                String message = "Configuration problem: could not instantiate UtilityIfc instance";
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
            journalOn = util.getParameterValue("SendTrainingModeTransactionsToJournal", "Y").equalsIgnoreCase("Y");
        }
        return (journalOn);
    }


    @Override
    public void cancelRegisterInprocessTransactions() throws DataException, ArchiveException 
    {
        // Basic implementation is a no op for the base manager
    }
    
    @Override
    public void updateInprocessTransaction(TransactionIfc tran, FinancialTotalsIfc totals, TillIfc till, 
            Map<String, Serializable> supporting) throws ArchiveException
    {
    	throw new ArchiveException(ArchiveException.CONFIG,"Unsupported Operation");
    }

    /**
     * Not supported in this class.  Please see {@link  oracle.retail.stores.pos.manager.utility.ArchivingTransactionUtilityManager}
     */
    @Override
    public String getArchiveName(TransactionIfc tran)
    {
        throw new RuntimeException("Not supported - see ArchivingTransactionUtilityManager");
    }
    
    
    /**
     * Please see {@link  oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc}
     */
    @Override
    public void setNextTransactionID(TransactionIfc tran, RegisterIfc register, EYSDate busDate)
            throws ArchiveException
    {
        // set workstation ID, business date, status, etc.
        tran.setWorkstation(register.getWorkstation());
        tran.setBusinessDay(busDate);
        tran.setTransactionSequenceNumber(register.getNextTransactionSequenceNumber());
        tran.buildTransactionID();
        try 
        {
            cancelRegisterInprocessTransactions();
        } 
        catch (DataException | ArchiveException e) 
        {
            // These exceptions are logged in the cancelRegisterInprocessTransaction method
            if (logger.isDebugEnabled())
                {
                logger.debug("ArchvingTransactionUtilityManager.initializeTransaaction() - exception during cancelInprocessTransactions()", e);
                }
        }
        
        
        if (logger.isInfoEnabled())
        {
            logger.info("ArchivingTransactionUtilityManger.initializeTransaction() - transaction id " + tran.getTransactionID());
        }
        
        // Add the transaction  to the inprocess archive
        try {
            
            RetailTransactionArchiveEntryIfc archive = new RetailTransactionArchiveEntry(getArchiveName(tran), tran, null, null, null,null);
            getArchiveManager().updateInprocessArchive(archive);
        } 
        catch (ArchiveException earc) 
        {
            logger.warn("RetailTransactionManager failed to store inprocess transaction " + tran.getTransactionID(), earc);
        }
    }
    
    // Perform a lazy load of the TransactionArchiveManager
    protected TransactionArchiveManagerIfc getArchiveManager() throws ArchiveException
    {
        if (archiveMgr == null)
        {
            archiveMgr = (TransactionArchiveManagerIfc)Gateway.getDispatcher().getManager(TransactionArchiveManagerIfc.TYPE);
            if (archiveMgr == null)
            {
                throw new ArchiveException(ArchiveException.CONFIG, "No Transaction Archive Manager registered on this tier");
            }
        }
        return archiveMgr;
    }
}
