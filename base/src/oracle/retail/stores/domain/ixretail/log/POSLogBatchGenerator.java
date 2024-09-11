/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/POSLogBatchGenerator.java /main/16 2013/05/02 16:49:47 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/31/11 - removed deprecated method
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ranojha   12/10/08 - Fixing performance defect using
 *                         maximumTransactionsToExport attribute
 *    mahising  11/25/08 - updated due to merge
 *    asinton   11/22/08 - Changes per review comments.
 *    asinton   11/21/08 - Moving configuration setting to export training mode
 *                         transactions via POSLog from parameters to store
 *                         server conduit.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuilder to StringBuilder
 *    5    360Commerce 1.4         11/9/2006 7:28:31 PM   Jack G. Swan
 *         Modifided for XML Data Replication and CTR.
 *    4    360Commerce 1.3         4/24/2006 5:51:32 PM   Charles D. Baker
 *         Merge of NEP62
 *    3    360Commerce 1.2         3/31/2005 4:29:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:13 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:09 PM  Robert Pearse
 *
 *   Revision 1.10.4.2  2004/11/18 01:18:37  csuehs
 *   @scr 7740 Add INFO level log messages
 *
 *   Revision 1.10.4.1  2004/10/20 13:11:57  kll
 *   @scr 7352: changing scope of some attributes to protected for the sake of extensibility
 *
 *   Revision 1.10  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *
 *   Revision 1.9  2004/06/03 14:47:36  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *
 *   Revision 1.8.2.2  2004/06/23 00:25:37  mwright
 *   Added facility (for testing) to extract the Document produced by the logwriter
 *
 *   Revision 1.8.2.1  2004/06/15 06:31:58  mwright
 *   Added method for generating batch to Document rather than file (used for testing)
 *
 *
 *   Revision 1.8  2004/04/17 19:51:19  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.7  2004/04/14 15:04:34  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:42  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:51  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:36:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   01 Jul 2003 14:33:34   jgs
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.arts.TransactionWriteNotQueuedDataTransaction;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * This is the class used to implement the task of exporting a TLog. A TLog is a
 * flat-file log of transactions. It is, in this implementation, exported in an
 * IXRetail-like format.
 * 
 * @version@ $Revision: /main/16 $
 */
public class POSLogBatchGenerator implements POSLogBatchGeneratorIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/16 $";
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(POSLogBatchGenerator.class);

    /**
     * count of records expected
     */
    protected int recordsExpected = 0;
    /**
     * count of records read
     */
    protected int recordsRead = 0;
    /**
     * count of records exported
     */
    protected int recordsExported = 0;
    /**
     * count of read failures
     */
    protected int readFailures = 0;
    /**
     * count of export failures
     */
    protected int exportFailures = 0;
    /**
     * count of records flagged as exported
     */
    protected int recordsFlagged = 0;
    /**
     * count of record-flagging failures
     */
    protected int markRecordsFailures = 0;
    /**
     * flag indicating that exported records are flagged
     */
    protected boolean exportedRecordsMarked = true;
    /**
     * flag indicating that flagging of exported records is to be reported
     */
    protected boolean reportExportedRecordsMarked = false;
    /**
     * indicator that no-records-read does not constitute failure
     */
    protected boolean noRecordsFoundOk = true;
    /**
     * The class accumulates the final result information in this buffer
     */
    protected StringBuilder resultMessage = new StringBuilder();
    /**
     * batch object
     */
    protected BatchTotalIfc batch = null;
    /**
     * data transaction used for reading transactions
     */
    protected TransactionReadDataTransaction readDataTransaction = null;
    /**
     * transaction list
     */
    protected POSLogTransactionEntryIfc[] transactionList = null;
    /**
     * flag indicating transactions which can't be read or processed are set
     * aside and not processed again
     */
    protected boolean setAsideFlag = true;
    /**
     * processing code for exception handling
     */
    protected int processingCode = 0;
    /**
     * processing code constant
     */
    protected static final int PROCESSING_CODE_XML_TLOG = 1;
    /**
     * business date being processed
     */
    protected EYSDate businessDate = null;
    /**
     * transaction ID string for transaction being processed
     */
    protected String transactionIDString = "";
    /**
     * export directory name
     */
    protected String exportDirectoryName = "";
    /**
     * the backup directory name for export
     */
    protected String backupExportDirectoryName = null;
    /**
     * Object that writes the Pos Log entries to persistence
     */
    protected POSLogWriterIfc logWriter = null;
    /**
     * If entries are queued, the name of the queue to which the PosLog will be
     * written
     */
    protected String queueName = null;
    /**
     * If entries are queued, The name of the host on which the queue resides.
     */
    protected String queueHostName = null;
    /**
     * Maximum number of transaction to export; -1 mean unlimited
     */
    protected int maximumTransactionsToExport;
    /**
     * Identifies which column is used as bacth identifier
     */
    protected int columnID = -1;
    /**
     * This flag is used to suppress the normal file writing, so we can get a
     * Document from the POSLogWriter instead of writing and then re-reading a
     * file. This is used for testing only. The default false value ensures that
     * normal operation is not disturbed.
     */
    protected boolean documentOutputMode = false;
    /**
     * Flag to indicate whether to transport training mode transactions to
     * POSLog.
     */
    protected boolean trainingTransactionsExportable = false;
    
    /**
     * The attribute which specifies whether to compress the 
     * File or not.
     */
    protected boolean compressed = false;

    /**
     * Default constructor
     */
    public POSLogBatchGenerator()
    {
        columnID = POSLogTransactionEntryIfc.USE_BATCH_ARCHIVE;
    }

    /**
     * Generates the XML POSLog batch from the database. Before calling the this
     * method the user class must take the following steps:
     * <P>
     * 1. Call setLogWriter(POSLogWriterIfc) 2. Call
     * setExportDirectoryName(pathName) if the batch is being written to a file.
     * 3. Call setBackupExportDirectoryName(pathName) if the batch is being
     * written to a backup file.
     * <P>
     */
    public boolean generateBatch()
    {
        boolean bOk = true;
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Starting POSLog batch generation");
            }
            // set up batch
            initializeBatch();
            logWriter.setExportDirectoryName(exportDirectoryName);
            logWriter.setQueueName(queueName);
            logWriter.setQueueHostName(queueHostName);

            // retrieve list of transactions
            POSLogTransactionEntryIfc[] transactions = null;
            try
            {
                transactions = retrieveTransactionList();
            }
            catch (DataException edata)
            {
                if (edata.getErrorCode() != DataException.NO_DATA)
                {
                    logger.fatal("Unable to retrieve transactions", edata);
                }
                bOk = false;
            }
            catch (Exception e)
            {
                logger.fatal("Unable to retrieve transactions", e);
                bOk = false;
            }

            if (logger.isDebugEnabled())
            {
                int count = 0;
                if (transactions != null)
                {
                    count = transactions.length;
                }
                logger.debug("Retrieved " + count + " transactions for POSLog batch");
            }
            if (bOk && recordsExpected > 0)
            {
                bOk = processTransactions(transactions);
            }
        }
        // catch any other exception that slipped through the cracks
        catch (Exception e)
        {
            handleExceptionMessaging("An unknown exception has occurred: ", e);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Completed batch generation for POSLog");
        }
        return bOk;
    }

    /**
     * Generates the data replication batch id for those customer record which
     * has to send at ORCO database. Actual implimentation of this method is in
     * DataReplicationBatchGenerator java class.
     */
    public boolean generateCustomerBatch()
    {
        return true;
    }

    /**
     * Extracts the document after the batch has been generated the normal way.
     * This is used in testing.
     */
    public Document getDocument()
    {
        return logWriter.getDocument();
    }

    /**
     * Generates the XML POSLog as a document (for testing). The caller must
     * first set the POSLogWriter.
     */
    public Document generateBatchToDocument()
    {
        documentOutputMode = true; // this stops file writing from taking place
        Document doc = null;
        boolean bOk = true;
        try
        {
            // set up batch
            initializeBatch();
            // logWriter.setExportDirectoryName(exportDirectoryName);
            // logWriter.setQueueName(queueName);
            // logWriter.setQueueHostName(queueHostName);

            // retrieve list of transactions
            POSLogTransactionEntryIfc[] transactions = null;
            try
            {
                transactions = retrieveTransactionList();
            }
            catch (Exception e)
            {
                bOk = false;
            }

            if (bOk && recordsExpected > 0)
            {
                bOk = processTransactions(transactions);
            }
        }
        // catch any other exception that slipped through the cracks
        catch (Exception e)
        {
            handleExceptionMessaging("An unknown exception has occurred: ", e);
        }

        if (bOk)
        {
            doc = logWriter.getDocument();
        }
        return doc;
    }

    /**
     * Completes the batch processing.
     * <P>
     */
    public void completeProcessing()
    {
        try
        {
            // initialize log writer
            logWriter.completeProcessing();
        }
        catch (Exception e)
        {
            handleExceptionMessaging("An unknown exception has occurred: ", e);
        }
    }

    /**
     * Initializes batch.
     */
    protected void initializeBatch()
    {
        batch = IXRetailGateway.getFactory().getBatchTotalInstance();
        batch.setStoreID(getStoreNumber());
        batch.buildBatchID();
        if (logger.isInfoEnabled())
            logger.info("Batch " + batch.getBatchID() + " initialized.");
        
        
        logWriter.setCompressed(compressed);
    }

    /**
     * Retrieves list of transactions from database.
     * 
     * @return array of transaction list entries
     * @exception DataException thrown if error occurred retrieving transaction
     *                list
     */
    protected POSLogTransactionEntryIfc[] retrieveTransactionList() throws DataException
    {
        // initialize read data transaction
        getReadDataTransaction();
        // read transaction list
        POSLogTransactionEntryIfc[] entries = null;
        try
        {
            if (logger.isInfoEnabled())
            {
                logger.info("Retrieving transactions not yet batched.");
            }
            // read entries
            entries = readDataTransaction.retrieveTransactionsNotInBatch(getStoreNumber(), columnID,
                    maximumTransactionsToExport);

            // set records expected
            recordsExpected = entries.length;
        }
        catch (DataException de)
        {
            // not-found is a an expected condition
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                resultMessage.append("No transactions were found to be logged.").append(Util.EOL);
            }
            else
            {
                handleExceptionMessaging("An error occurred reading the transaction list from the database", de);
                throw de;
            }
        }

        // log results
        if (logger.isInfoEnabled())
            logger.info("Records expected in t-log: " + Integer.toString(recordsExpected) + "");

        return (entries);
    }

    /**
     * Exports transactions in list to t-log and mark transactions accordingly.
     * 
     * @param transactions list of transaction entries
     * @return true if no error occurred, false otherwise
     */
    protected boolean processTransactions(POSLogTransactionEntryIfc[] transactions)
    {
        // initialize t-log facility
        boolean bOk = initializePOSLog();
        if (bOk)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Processing transactions for POSLog batch");
            }
            // export transactions
            for (int i = 0; i < transactions.length; i++)
            {
                exportTransaction(transactions[i]);
            }
            if (logger.isDebugEnabled())
            {
                logger.debug("Completed transaction export with " + recordsExported + " records exported");
            }

            // if any records to write, do it
            if (recordsExported > 0)
            {
                if (!documentOutputMode) // this is true of we only want a
                                         // Document, not a written file
                {
                    // write tlog
                    bOk = writePOSLogFile();
                    if (!bOk)
                    {
                        deletePOSLogFile();
                    }
                }
            }
            // mark transactions, if necessary
            if (bOk && exportedRecordsMarked)
            {
                // mark transactions
                bOk = markTransactions(transactions);
                if (!bOk && recordsExported > 0)
                {
                    deletePOSLogFile();
                }
            }
            // set error flag for read failures, if need be
            if (readFailures > 0)
            {
                bOk = false;
            }
        }
        return (bOk);
    }

    /**
     * Initialize T-Log, creating XML document and instantiating log objects.
     * 
     * @return true if no problems occurred, false otherwise
     */
    protected boolean initializePOSLog()
    {
        boolean bOk = true;
        try
        {
            logWriter.initializePOSLog(batch.getBatchID());
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Failed to initialize pos log writer", e);
            }
            StringBuilder messageString = new StringBuilder("POSLog (").append(((POSLogWriterException)e).getFileType())
                    .append(" file) cannot be initialized");
            handleExceptionMessaging(messageString.toString(), e);
            bOk = false;
        }

        return (bOk);
    }

    /**
     * Exports a transaction to t-log.
     * 
     * @param transaction transaction entry to be exported
     * @param logWriter log writer facility
     * @param doc XML document
     */
    protected void exportTransaction(POSLogTransactionEntryIfc transaction)
    {
        try
        {
            // read transaction from database
            TransactionIfc readTransaction = retrieveTransaction(transaction);
            if (readTransaction != null)
            {
                businessDate = readTransaction.getBusinessDay();
                transactionIDString = readTransaction.getTransactionID();
                if (logger.isInfoEnabled())
                    logger.info("Logging transaction [" + transactionIDString + "] business date [" + businessDate
                            + "]");
                // reset processing code in case exception occurs
                processingCode = PROCESSING_CODE_XML_TLOG;

                long timeMark = System.currentTimeMillis();

                // test to see if training mode transactions should be exported.
                boolean isTrainingMode = readTransaction.isTrainingMode();
                if (!isTrainingMode || (isTrainingMode && isTrainingTransactionsExportable()))
                {
                    logWriter.exportTransaction(readTransaction, batch);
                    recordsExported++;
                }

                logElapsedTime("Transaction translated to POSLog", timeMark);

                // set batch ID on transaction entry
                transaction.setBatchID(batch.getBatchID());
            }
        }
        catch (Exception e)
        {
            handleProcessingException(e);
            if (setAsideFlag)
            {
                transaction.setBatchID(POSLogTransactionEntryIfc.UNAVAILABLE_FOR_PROCESSING);
            }
        }
    }

    /**
     * Handle processing exception.
     * 
     * @param exception exception thrown by processing
     */
    protected void handleProcessingException(Exception e)
    {
        StringBuilder messageString = new StringBuilder("Transaction [").append(transactionIDString)
                .append("] business date [").append(businessDate).append("] cannot be translated into POSLog");
        if (e instanceof POSLogWriterException)
        {
            messageString.append(" ").append(((POSLogWriterException)e).getFileType()).append(" file");
        }
        handleExceptionMessaging(messageString.toString(), e);
        exportFailures++;

    }

    /**
     * Retrieves specified transaction.
     * 
     * @param transactionEntry transaction list entry
     * @return retrieved transaction
     */
    protected TransactionIfc retrieveTransaction(POSLogTransactionEntryIfc transactionEntry)
    {
        TransactionIfc transaction = null;
        // use default locale for POSLog
        LocaleRequestor localeReq = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
        try
        {
            getReadDataTransaction();
            transaction = readDataTransaction.readTransactionForBatch(transactionEntry.getTransactionID()
                    .getTransactionIDString(), transactionEntry.getBusinessDate(), localeReq);

            recordsRead++;
        }
        catch (Exception e)
        {
            logger.warn("Transaction [" + transactionEntry.getTransactionID().getTransactionIDString()
                    + "] business date [" + transactionEntry.getBusinessDate() + "] cannot be read from database.", e);

            readFailures++;
            // set batch indicator to set this record aside
            if (setAsideFlag)
            {
                transactionEntry.setBatchID(POSLogTransactionEntryIfc.UNAVAILABLE_FOR_PROCESSING);
            }
        }
        return (transaction);
    }

    /**
     * Marks transactions as logged.
     * 
     * @param transactions list of transaction entries
     * @return boolean error indicator
     */
    protected boolean markTransactions(POSLogTransactionEntryIfc[] transactions)
    {
        boolean noError = true;
        try
        {
            TransactionWriteNotQueuedDataTransaction dt = null;

            dt = (TransactionWriteNotQueuedDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION);

            Integer updateCount = null;
            if (columnID == POSLogTransactionEntryIfc.USE_BATCH_ARCHIVE)
            {
                updateCount = dt.updateTransactionBatchIDs(transactions);
            }
            else
            {
                updateCount = dt.updateTransactionTLogIDs(transactions);
            }

            recordsFlagged = updateCount.intValue();
        }
        catch (Exception e)
        {
            handleExceptionMessaging("An error occurred marking the transactions as processed", e);
            noError = false;
        }

        return (noError);
    }

    /**
     * Builds batch total object, appends batch total elements to document and
     * writes document to file.
     * 
     * @param logWriter log writer facility
     * @return true if no error occurred, false otherwise
     */
    protected boolean writePOSLogFile()
    {
        boolean bOk = logBatchTotals();
        if (bOk)
        {
            try
            {
                logWriter.writePOSLogFile(batch.getBatchID());

                resultMessage.append("XML t-log was written to ").append(logWriter.getOutputXMLFileName())
                        .append(".  ").append(Util.EOL);

                if (backupExportDirectoryName != null)
                {
                    logWriter.backupPOSLogFile(batch.getBatchID(), backupExportDirectoryName);
                    resultMessage.append("XML t-log was also written to the backup location ")
                            .append(logWriter.getBackupXMLFileName()).append(".  ").append(Util.EOL);
                }

            }
            catch (Exception e)
            {
                handleExceptionMessaging("An error occurred writing the t-log XML file", e);
                bOk = false;
                recordsExported = 0;
            }
        }

        return (bOk);
    }

    /**
     * Write batch totals element to document.
     * 
     * @param logWriter log writer facility
     * @return true if no error occurred, false otherwise
     */
    protected boolean logBatchTotals()
    {
        boolean bOk = true;
        try
        {
            logWriter.logBatchTotals(batch);
        }
        catch (Exception e)
        {
            handleExceptionMessaging("An error occurred creating the batch totals element", e);
            bOk = false;
        }

        return (bOk);
    }

    /**
     * Deletes t-log file. This is used if the transaction-mark operation fails.
     * 
     * @param logWriter log writer facility
     */
    protected void deletePOSLogFile()
    {
        try
        {
            logWriter.deletePOSLogFile();
            resultMessage.append("T-log file ").append(logWriter.getOutputXMLFileName()).append(" was deleted.  ")
                    .append(Util.EOL);

            resultMessage.append("The backup copy of the T-log file ").append(logWriter.getBackupXMLFileName())
                    .append(" was also deleted.  ").append(Util.EOL);

            recordsExported = 0;
        }
        catch (Exception e)
        {
            StringBuilder messageString = new StringBuilder("An error occurred deleting the XML file [").append(
                    logWriter.getOutputXMLFileName()).append("]");
            handleExceptionMessaging(messageString.toString(), e);
        }
    }

    /**
     * Returns instance of TransactionReadDataTransaction. If data transaction
     * has not been instantiated, the data transaction is instantiated.
     * 
     * @return read data transaction
     */
    protected TransactionReadDataTransaction getReadDataTransaction()
    {
        // perform lazy create of data transaction
        if (readDataTransaction == null)
        {

            readDataTransaction = (TransactionReadDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

        }
        return (readDataTransaction);
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("ExportTLogTask", getRevisionNumber(), hashCode());
        // add attributes to string
        strResult.append(Util.EOL);

        // pass back result
        return (strResult.toString());
    }

    /**
     * Handles exception messaging.
     * 
     * @param messageString error message string
     * @param ex exception
     */
    protected void handleExceptionMessaging(String messageString, Exception ex)
    {
        logger.error(messageString);
        logger.error("Error:  " + Util.throwableToString(ex) + "");
        resultMessage.append(messageString).append(":  ").append(getExceptionMessage(ex)).append(".").append(Util.EOL);
    }

    /**
     * Returns exception message. If exception message is empty (as in
     * NullPointerException), toString() is used.
     * 
     * @param ex exception
     * @return exception message string
     */
    protected String getExceptionMessage(Exception ex)
    {
        String msg = ex.getMessage();
        if (Util.isEmpty(msg))
        {
            msg = ex.toString();
        }
        return (msg);
    }

    /**
     * Logs elapsed time for a specified event and start time, and resets start
     * time.
     * 
     * @param desc description of event
     * @param startTime start time
     * @param stopTime stop time
     */
    protected void logElapsedTime(String desc, long startTime)
    {
        logElapsedTime(desc, startTime, System.currentTimeMillis());
        startTime = System.currentTimeMillis();
    }

    /**
     * Logs elapsed time for a specified event, stop time and start time.
     * 
     * @param desc description of event
     * @param startTime start time
     * @param stopTime stop time
     */
    protected void logElapsedTime(String desc, long startTime, long stopTime)
    {
        if (logger.isInfoEnabled())
            logger.info("" + desc + " in " + Long.toString(stopTime - startTime) + " ms");
    }

    /**
     * Gets the value of current store number
     * 
     * @return the label text
     */
    public String getStoreNumber()
    {
        String storeID = Gateway.getProperty("application", "StoreID", "0000");
        return storeID;
    }

    /**
     * Returns the backupExportDirectoryName.
     * 
     * @return String
     */
    public String getBackupExportDirectoryName()
    {
        return backupExportDirectoryName;
    }

    /**
     * Returns the exportDirectoryName.
     * 
     * @return String
     */
    public String getExportDirectoryName()
    {
        return exportDirectoryName;
    }

    /**
     * Returns the exportedRecordsMarked.
     * 
     * @return boolean
     */
    public boolean isExportedRecordsMarked()
    {
        return exportedRecordsMarked;
    }

    /**
     * Returns the exportFailures.
     * 
     * @return int
     */
    public int getExportFailures()
    {
        return exportFailures;
    }

    /**
     * Returns the markRecordsFailures.
     * 
     * @return int
     */
    public int getMarkRecordsFailures()
    {
        return markRecordsFailures;
    }

    /**
     * Returns the noRecordsFoundOk.
     * 
     * @return boolean
     */
    public boolean isNoRecordsFoundOk()
    {
        return noRecordsFoundOk;
    }

    /**
     * Returns the processingCode.
     * 
     * @return int
     */
    public int getProcessingCode()
    {
        return processingCode;
    }

    /**
     * Returns the readFailures.
     * 
     * @return int
     */
    public int getReadFailures()
    {
        return readFailures;
    }

    /**
     * Returns the recordsExpected.
     * 
     * @return int
     */
    public int getRecordsExpected()
    {
        return recordsExpected;
    }

    /**
     * Returns the recordsExported.
     * 
     * @return int
     */
    public int getRecordsExported()
    {
        return recordsExported;
    }

    /**
     * Returns the recordsFlagged.
     * 
     * @return int
     */
    public int getRecordsFlagged()
    {
        return recordsFlagged;
    }

    /**
     * Returns the recordsRead.
     * 
     * @return int
     */
    public int getRecordsRead()
    {
        return recordsRead;
    }

    /**
     * Returns the reportExportedRecordsMarked.
     * 
     * @return boolean
     */
    public boolean isReportExportedRecordsMarked()
    {
        return reportExportedRecordsMarked;
    }

    /**
     * Returns the resultMessage.
     * 
     * @return StringBuilder
     */
    public String getResultMessage()
    {
        return resultMessage.toString();
    }

    /**
     * Method setQueueName
     * 
     * @param queueName
     */
    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    /**
     * Method setQueueHostName
     * 
     * @param queueHostName
     */
    public void setQueueHostName(String queueHostName)
    {
        this.queueHostName = queueHostName;
    }

    /**
     * Sets the backupExportDirectoryName.
     * 
     * @param backupExportDirectoryName The backupExportDirectoryName to set
     */
    public void setBackupExportDirectoryName(String backupExportDirectoryName)
    {
        this.backupExportDirectoryName = backupExportDirectoryName;
    }

    /**
     * Sets the exportDirectoryName.
     * 
     * @param exportDirectoryName The exportDirectoryName to set
     */
    public void setExportDirectoryName(String exportDirectoryName)
    {
        this.exportDirectoryName = exportDirectoryName;
    }

    /**
     * Sets the logWriter.
     * 
     * @param logWriter The logWriter to set
     */

    public void setLogWriter(POSLogWriterIfc logWriter)
    {
        this.logWriter = logWriter;
    }

    /**
     * Sets maximumTransactionsToExport
     * 
     * @parm int
     */
    public void setMaximumTransactionsToExport(int maxTransactionsToExport)
    {
        this.maximumTransactionsToExport = maxTransactionsToExport;
    }

    /**
     * Converts and sets maximumTransactionsToExport
     * 
     * @parm String
     */
    public void setMaximumTransactionsToExport(String maxTransactionsToExport)
    {
        setMaximumTransactionsToExport(Integer.parseInt(maxTransactionsToExport));
    }

    /**
     * Sets the logWriter.
     * 
     * @param logWriter The logWriter to set
     */
    public void initialize()
    {
        recordsExpected = 0;
        recordsRead = 0;
        recordsExported = 0;
        readFailures = 0;
        exportFailures = 0;
        recordsFlagged = 0;
        exportedRecordsMarked = true;
        reportExportedRecordsMarked = false;
        markRecordsFailures = 0;
        noRecordsFoundOk = true;
        resultMessage = new StringBuilder();
        batch = null;
        readDataTransaction = null;
        transactionList = null;
        setAsideFlag = true;
        processingCode = 0;
        businessDate = null;
        transactionIDString = "";
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Return trainingTransactionsExportable
     * 
     * @return boolean
     */
    public boolean isTrainingTransactionsExportable()
    {
        return trainingTransactionsExportable;
    }

    /**
     * Set trainingTransactionsExportable
     * 
     * @param boolean
     */
    public void setTrainingTransactionsExportable(boolean trainingTransactionsExportable)
    {
        this.trainingTransactionsExportable = trainingTransactionsExportable;
    }
    
    /**
     * Method gets whether to compress the file or not.
     * 
     * @return boolean
     */
    public boolean isCompressed()
    {
        return compressed;
    }

    /**
     * Sets the compress attribute
     * 
     * @param boolean compressed flag
     */
    public void setCompressed(boolean value)
    {
        this.compressed = value;
    }    

}

