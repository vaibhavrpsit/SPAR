/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/manager/utility/ArchivingTransactionUtilityManager.java /main/9 2014/07/16 15:44:39 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    07/16/14 - fix for Fortify:Race Condition:Format Flaw
 *    rhaight   05/20/14 - Archive name independent of the transaction id
 *                         format settings in ORPOS
 *    rhaight   05/19/14 - Updated the Regex filter ending string
 *    rhaight   05/19/14 - Add filter expression generation from the
 *                         transaction id properties
 *    kavdurai  02/18/14 - getArchiveManager() function moved to base class
 *                         TransactionUtilityManager
 *    mjwallac  12/11/13 - Synchronized getArchiveName to avoid race condition
 *    asinton   11/15/13 - refactored to use TransactionUtilityManager to
 *                         obtain transactionArchiveName
 *    rhaight   11/13/13 - post review update
 *    rhaight   11/12/13 - Enhancements to support additional transaction
 *                         recovery
 *    cgreene   10/07/13 - set transaction number onto journal when canceling
 *    rhaight   09/24/13 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.manager.utility;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oracle.retail.stores.commerceservices.transaction.RetailTransactionCancelPayload;
import oracle.retail.stores.commerceservices.transaction.RetailTransactionCancelServiceException;
import oracle.retail.stores.commerceservices.transaction.RetailTransactionCancelServiceIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionWriteDataTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.archive.ArchiveException;
import oracle.retail.stores.pos.manager.archive.RetailTransactionArchiveEntry;
import oracle.retail.stores.pos.manager.archive.RetailTransactionArchiveEntryIfc;
import oracle.retail.stores.pos.manager.archive.TransactionArchiveManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.ui.timer.TimeoutSettingsUtility;

import org.apache.log4j.Logger;

/**
 * ArchivingTransactionUtilityManager extends the TransactionUtilityManager with
 * support for archiving inprocess transactions and verification of transaction
 * persistence. This manager delegates to the TransactionArchiveManager for the
 * storage and retrieval of the in session transaction.
 *
 * @author rhaight
 * @since 14.0
 */
public class ArchivingTransactionUtilityManager extends
        TransactionUtilityManager implements TransactionUtilityManagerIfc {
    
    /** Log4J logger */
    protected static Logger logger = Logger.getLogger(ArchivingTransactionUtilityManager.class);
    
    /** Spring Bean Identifier for the Retail Transaction Cancel Service */
    protected static final String RETAIL_TRANSACTION_CANCEL_SERVICE_BEAN = "application_RetailTransactionCancel";

    /** Date Formatter for manager */
    protected DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    

    /** Default name of the data transaction queue for retail transactions */
    protected final static String DEFAULT_TRANSACTION_QUEUE = "TransactionQueue";

    /** Regular expression for the date and store section of transaction id */
    protected static final String REGEX_PREFIX = "\\d{8}"; 
    
    /** Regular expression for the sequence number section of transaction id */
    protected static final String REGEX_ENDING = "\\w*"; 
    
    /** Default store id format for archive names */
    protected static final String DEFAULT_STORE_ID_FORMAT = "000000";
    
    /** Default sequence id format for archive names */
    protected static final String DEFAULT_SEQUENCE_ID_FORMAT = "00000";
    
    /** Default register id format for archive names */
    protected static final String DEFAULT_REGISTER_ID_FORMAT = "0000"; 
    

    /** Store ID Format for archive names */
    protected String storeIDFormat = DEFAULT_STORE_ID_FORMAT;
    
    /** Register ID Format for archive names */
    protected String registerIDFormat = DEFAULT_REGISTER_ID_FORMAT;
    
    /** Sequence ID Format for archive names */
    protected String sequenceIDFormat = DEFAULT_SEQUENCE_ID_FORMAT;
    
    /** Formatter for Store IDs */
    protected DecimalFormat storeIDFormatter = null;
    
    /** Formatter for Register IDs */
    protected DecimalFormat registerIDFormatter = null;
    
    /** Formatter for sequence IDs */
    protected DecimalFormat sequenceIDFormatter = null;
    
    /** Service to perform the cancel transaction processing */
    protected RetailTransactionCancelServiceIfc txnCancelSvc = null;
    
    /** Flag to indicate the manager is shutting down */
    protected boolean shuttingDown = false;
    

    /**
     * Constructor for ArchivingTransactionUtilityManager
     */
    public ArchivingTransactionUtilityManager()
    {
        super();
        getCancelService();
    }
    
    
 
    
    @Override
    public void startUp()
    {
        super.startUp();
    }

    @Override
    public void shutdown()
    {
        super.shutdown();
    }
    
    @Override
    public void updateInprocessTransaction(TransactionIfc tran, FinancialTotalsIfc totals, TillIfc till, 
                                           Map<String, Serializable> supporting) throws ArchiveException
    {
        String arcName = getArchiveName(tran);
        TransactionArchiveManagerIfc arcMgr = getArchiveManager();
        
        RetailTransactionArchiveEntryIfc archive = 
                                 new RetailTransactionArchiveEntry(arcName,tran, totals, getRegister(), till, supporting);
        
        arcMgr.updateInprocessArchive(archive);
    }
    
    
    @Override
    public void initializeTransaction(TransactionIfc trans, long seq, String custID)
    {
     // Cancel any inprocess transactions before initializing new transaction
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
        
        super.initializeTransaction(trans, seq, custID);
        
        if (logger.isInfoEnabled())
        {
            logger.info("ArchivingTransactionUtilityManger.initializeTransaction() - transaction id " + trans.getTransactionID());
        }
        
        // Add the transaction  to the inprocess archive
        try {
            
            RetailTransactionArchiveEntryIfc archive = new RetailTransactionArchiveEntry(getArchiveName(trans), trans, null, null, null,null);
            getArchiveManager().updateInprocessArchive(archive);
        } 
        catch (ArchiveException earc) 
        {
            logger.warn("RetailTransactionManager failed to store inprocess transaction " + trans.getTransactionID(), earc);
        }
    }
    
    
    @Override
    public void saveTransaction(TransactionIfc trans, FinancialTotalsIfc totals, TillIfc till,
            RegisterIfc register, boolean journalEndOfTransaction) throws DataException
    {
        SaveTransactionOptions tranOpts = new SaveTransactionOptions();
        tranOpts.setAllowQueueing(true);
        tranOpts.setVerify(isVerifyEnabled());
        tranOpts.setDeleteInprocess(true);
        tranOpts.setArchiveName(getArchiveName(trans));
        tranOpts.setJournalAtEnd(journalEndOfTransaction);
        
        saveTransaction(trans, totals, till, register, tranOpts);
    }
    
    protected void saveTransaction(TransactionIfc trans, FinancialTotalsIfc totals, 
                                    TillIfc till, RegisterIfc register, SaveTransactionOptions options)
            throws DataException {
        
        // Provides additional control over the persistence of the retail transaction

        prePersistProcessing(trans, options);
        
        persistRetailTransaction(trans,totals, till, register, options);
        
        if (options.isJournalAtEnd())
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

        // Process the archive related options
        try
        {
            TransactionArchiveManagerIfc arcMgr = getArchiveManager();
            
            if (options.isDeleteInProcess())
            {
                arcMgr.deleteInprocessArchive(options.getArchiveName());
            }
            
            if (arcMgr.isVerifyEnabled() && options.isVerify())
            {
                RetailTransactionArchiveEntryIfc archive = new RetailTransactionArchiveEntry(options.getArchiveName(), trans, totals, register, till, null);
                arcMgr.archiveTransaction(archive);
            }
        }
        catch (ArchiveException earc)
        {
            throw new DataException(DataException.UNEXPECTED_ERROR, earc.getMessage(), earc);
        }
    }   
    
    protected void prePersistProcessing(TransactionIfc trans, SaveTransactionOptions options)
    {
        // This method provides a point to perform processing before a transaction is
        // persisted. 
        switch (trans.getTransactionType())
        {
            case TransactionIfc.TYPE_CLOSE_REGISTER:
            case TransactionIfc.TYPE_CLOSE_STORE:
            {
                // This is time to force verify all remaining transactions
                // for this register
                verifyRegisterTransactions();
                break;
            }
            default:
            {
                // no pre persist operations is default for most transactions
            }
            
        }
        
        
    }
    
    
    protected void persistRetailTransaction(TransactionIfc trans,
            FinancialTotalsIfc totals, TillIfc till, RegisterIfc register, SaveTransactionOptions opts) throws DataException
    {
        
        TransactionWriteDataTransaction dbTrans = getDBTransaction(trans, opts);
        
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
                sb.append("ExtTransactionUtilityManager.persistRetailTransaction() - before DM Call. Transaction ID: ");
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
        }
        catch (DataException e)
        {
            logger.error("ExtTransactionUtilityManager.persistRetailTransaction() - An error occurred saving the transaction: " + trans.getTransactionID(), e);
            throw e;
        }
        
    }
    
    protected TransactionWriteDataTransaction getDBTransaction(TransactionIfc trans, SaveTransactionOptions opts)
    {
        TransactionWriteDataTransaction dbTrans = null;
 
        if (!opts.isAllowQueueing())
        {
            // If queuing not allowed return the WRITE_NOT_QUEUED
            dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION);
        }
        else
        {
            // Provide the same logic as the TransactionUtilityManager class
            switch (trans.getTransactionType())
            {
                case TransactionIfc.TYPE_CLOSE_REGISTER:
                case TransactionIfc.TYPE_CLOSE_STORE:
                {
                    dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory
                            .create(DataTransactionKeys.TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION);
                    break;
                }
                default:
                {
                    dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory
                            .create(DataTransactionKeys.TRANSACTION_WRITE_DATA_TRANSACTION);
                }
            }
        }
        return dbTrans;      
        
    }
    
    
    protected void removeInprocessTransaction(TransactionIfc trans) throws ArchiveException
    {
        getArchiveManager().deleteInprocessArchive(getArchiveName(trans));
    }
    

    
    
    @Override
    public void cancelRegisterInprocessTransactions() throws DataException, ArchiveException
    {
        // Cancels all inprocess transactions for the current register
        
        List<String> inprocessArchives = getArchiveManager().getInprocessArchives(getRegisterFilter());
        RetailTransactionArchiveEntryIfc archive = null;
        
        for (String arcName : inprocessArchives)
        {
            archive = getArchiveManager().getInprocessArchive(arcName);
            
            // This is called to clear orphaned inprocess archives - result of an error
            logger.error("ExtTransactionUtilityManager.cancelRegisterInprocessTransactions() - cancelling archive " + arcName);
            
            cancelInprocessArchive(archive);
        }
    }


    protected List<String> verifyRegisterTransactions()
    {
        List<String> unverifieds =  new ArrayList<String>();
        try {
            unverifieds = getArchiveManager().verifyArchivedTransactions(getRegisterFilter());
            
            if (!unverifieds.isEmpty())
            {
                StringBuffer sb = new StringBuffer();
                for (String arcName : unverifieds)
                {
                    sb.append(arcName);
                    sb.append(" ");
                }
                logger.error("The following Transaction Archives were not verified: " + sb.toString()); 
            }
        } 
        catch (DataException | ArchiveException earc) {
            // exceptions are logged in TransactionArchiveManager - this process must continue
            logger.error("ArchivingTransactionUtilityManager.verifyRegisterTransactions() - exception verfifying transactions", earc);
        }
        return unverifieds;
    }

    
    protected boolean cancelInprocessArchive(RetailTransactionArchiveEntryIfc archive)
            throws DataException 
    {
        // Refactor for delegating the pre and post persistence cancel operations 
        if (logger.isInfoEnabled())
        {
            logger.info("cancelInprocessArchive for transaction id " + archive.getArchiveEntryName());
        }
        BusIfc bus = getBus();
        
        AbstractFinancialCargo cargo = (AbstractFinancialCargo)bus.getCargo();
        
        RetailTransactionCancelPayload payload = new RetailTransactionCancelPayload();
        
        payload.setRetailTransaction(archive.getTransaction());
        payload.setRegister(cargo.getRegister());
        payload.setTill(cargo.getRegister().getCurrentTill());
        payload.setOperator(cargo.getOperator());
        payload.setSupporting(archive.getSupporting());
        
        
        try 
        {
            
            payload = getCancelService().prePersistCancelProcessing(bus, payload);
            
            if (payload.isPersistTransaction())
            {
                // Pass the transaction, a null totals object, the till,
                // register, and false indicating that the transaction has
                // not completed its journalling yet.
             
                
                SaveTransactionOptions opts = new SaveTransactionOptions();
                
                opts.setArchiveName(archive.getArchiveEntryName());
                opts.setVerify(isVerifyEnabled());
                opts.setAllowQueueing(true);
                opts.setDeleteInprocess(true);
                opts.setJournalAtEnd(false);
                
                saveTransaction(payload.getRetailTransaction(), null, payload.getTill(), payload.getRegister(), opts);

                payload.getRetailTransaction().setCanceledTransactionSaved(true);
            }

            if (payload.isPostPersistProcessing())
            {
                payload = getCancelService().postPersistCancelProcessing(bus, payload);
            }
        } 
        catch (DataException eData)
        {
            payload.setCancelled(false);
            throw eData;
        }
        catch (RetailTransactionCancelServiceException e) {
             logger.error("Failed to cancel retail transaction", e);
        }

        return payload.isCancelled();
    }  

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc#getArchiveName(oracle.retail.stores.domain.transaction.TransactionIfc)
     */
    @Override
    public synchronized String getArchiveName(TransactionIfc tran)
    {
        // Generate an archive name independent of the setting for transaction
        // id format
        StringBuffer sb = new StringBuffer();
        
        sb.append(getArchiveDate(tran));
        sb.append(getArchiveStoreID(tran));
        sb.append(getArchiveRegisterID(tran));
        sb.append(getArchiveSequenceID(tran));
        
        return sb.toString();
    }
    
    protected RegisterIfc getRegister()
    {
        AbstractFinancialCargo cargo = (AbstractFinancialCargo)getBus().getCargo();
        return cargo.getRegister();
    }
    
    protected EmployeeIfc getOperator()
    {
        AbstractFinancialCargo cargo = (AbstractFinancialCargo)getBus().getCargo();
        return cargo.getOperator();
        
    }
    
    protected String getRegisterFilter()
    {
        // Build a regular expression string to identify the archives associated
        // with the store and register combination
        
        int store = Integer.parseInt(getStoreStatus().getStore().getStoreID());
        int reg = Integer.parseInt(getRegister().getWorkstation().getWorkstationID());
        
        String filter = REGEX_PREFIX + getStoreIDFormatter().format(store) 
                + getRegisterIDFormatter().format(reg) + REGEX_ENDING;
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Register Filter Pattern: " + filter);
        }
        return filter;
    }
    
    protected boolean isVerifyEnabled()
    {
        boolean verifyEnabled = false;
        try
        {
            verifyEnabled = getArchiveManager().isVerifyEnabled();
        }
        catch (ArchiveException earc)
        {
            // disable verification options
        }
        return verifyEnabled;
    }
  
    protected RetailTransactionCancelServiceIfc getCancelService()
    {
        // Perform a lazy load of cancel retail transaction service
        if (txnCancelSvc == null)
        {
            txnCancelSvc = (RetailTransactionCancelServiceIfc)BeanLocator.getApplicationBean(RETAIL_TRANSACTION_CANCEL_SERVICE_BEAN);
        }
       
        return txnCancelSvc;
    }
    
    // Utility methods to generate an archive name based on the underlying
    // transaction date, store number, register number, and sequence number, 
    // independent of the formatting options chosen
    
    protected String getArchiveDate(TransactionIfc tran)
    {
        // synchronized block avoids race condition in java.text.Format.format()
        synchronized (dateFormat)
        {
            return dateFormat.format(tran.getBusinessDay().toDate());
        }
    }
    
    protected String getArchiveStoreID(TransactionIfc tran)
    {
        int storeId = Integer.parseInt(tran.getFormattedStoreID());
        return getStoreIDFormatter().format(storeId);
    }
    
    protected String getArchiveRegisterID(TransactionIfc tran)
    {
        int regId = Integer.parseInt(tran.getFormattedWorkstationID());
        return getRegisterIDFormatter().format(regId);
    }
    
    protected String getArchiveSequenceID(TransactionIfc tran)
    {
        int seqNbr = Integer.parseInt(tran.getFormattedTransactionSequenceNumber());
        return getSequenceIDFormatter().format(seqNbr);
    }
    
    protected DecimalFormat getStoreIDFormatter()
    {
        // lazy load
        if (storeIDFormatter == null)
        {
            storeIDFormatter = new DecimalFormat(storeIDFormat);
        }
        return storeIDFormatter;
    }
    
    protected DecimalFormat getRegisterIDFormatter()
    {
        // lazy load
        if (registerIDFormatter == null)
        {
            registerIDFormatter = new DecimalFormat(registerIDFormat);
        }
        return registerIDFormatter;
    }
    
    protected DecimalFormat getSequenceIDFormatter()
    {
        // lazy load
        if (sequenceIDFormatter == null)
        {
            sequenceIDFormatter = new DecimalFormat(sequenceIDFormat);
        }
        return sequenceIDFormatter;
    }
    
    /**
     * Sets the Store ID Format String used for generating archive names
     * @param format
     */
    public void setStoreIDFormat(String format)
    {
        storeIDFormat = format;
    }
    
    /**
     * Sets the Register ID Format used for generating archive names
     * @param format
     */
    public void setRegisterIDFormat(String format)
    {
        registerIDFormat = format;
    }
    
    /**
     * Sets the sequence ID format used for generating archive names
     * @param format
     */
    public void setSequenceIDFormat(String format)
    {
        sequenceIDFormat = format;
    }
}