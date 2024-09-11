/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/POSLogWriter.java /main/18 2014/05/16 16:58:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/16/14 - XbranchMerge cgreene_bug-18772577 from
 *                         rgbustores_14.0x_generic_branch
 *    cgreene   05/16/14 - prevent totals NPE during writing of ATG order
 *                         transaction
 *    arabalas  02/04/14 - released the stream handles
 *    mjwallac  01/09/14 - fix null dereferences
 *    cgreene   08/04/11 - added ability to stream poslog xml to a file
 *    cgreene   08/10/10 - added ability to check xml for wellformness
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mkutiana  02/19/10 - Cancelled Order Trans POSLogging issue
 *    abondala  01/03/10 - update header date
 *    mpbarnet  04/22/09 - In createXMLPOSLogFileName(), append the seconds and
 *                         milliseconds to prevent data loss from two
 *                         transaction files being created during the same
 *                         minute.
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         7/26/2007 11:41:33 AM  Ashok.Mondal    CR
 *       27858 :Displaying correct transaction amount value on POSLog for
 *       order transaction.
 *  4    360Commerce 1.3         4/25/2007 10:00:45 AM  Anda D. Cadar   I18N
 *       merge
 *  3    360Commerce 1.2         3/31/2005 4:29:26 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:24:16 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:13:18 PM  Robert Pearse
 *
 * Revision 1.12.2.3  2005/01/21 22:41:14  jdeleau
 * @scr 7888 merge Branch poslogconf into v700
 *
 * Revision 1.12.2.2.2.1  2005/01/20 16:37:23  jdeleau
 * @scr 7888 Various POSLog fixes from mwright
 *
 * Revision 1.12.2.1  2004/12/09 05:04:05  mwright
 * Changed to add v2.1 batch totals element before first transaction
 *
 * Revision 1.12  2004/10/01 20:53:44  cdb
 * @scr 7297 Updated to avoid duplicate batch element entries.
 *
 * Revision 1.11  2004/09/23 00:30:48  kmcbride
 * @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 * Revision 1.10  2004/06/24 09:15:09  mwright
 * POSLog v2.1 (second) merge with top of tree
 *
 * Revision 1.9.2.1  2004/06/15 06:32:52  mwright
 * Added facility to get the Document representing the POSLog. This is used in testing.
 *
 * Revision 1.9  2004/05/06 03:37:04  mwright
 * POSLog v2.1 merge with top of tree
 * Cope with possibility that v2.1 does not create batch totals
 *
 * Revision 1.8  2004/04/09 16:55:47  cdb
 * @scr 4302 Removed double semicolon warnings.
 *
 * Revision 1.7  2004/04/08 16:45:59  cdb
 * @scr 4204 Removing tabs - again.
 *
 * Revision 1.6  2004/04/08 06:34:18  smcgrigor
 * Merge of Kintore POSLog v2.1 code from branch to TopOfTree
 *
 * Revision 1.5.2.1  2004/03/17 04:13:49  mwright
 * Initial revision for POSLog v2.1
 *
 * Revision 1.5  2004/02/17 17:57:42  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.4  2004/02/17 16:18:51  rhafernik
 * @scr 0 log4j conversion
 *
 * Revision 1.3  2004/02/12 17:13:45  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 23:25:28  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:36:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 01 2003 14:09:26   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.1   Mar 28 2003 10:27:06   adc
 * Added the backup functionality
 * Resolution for 1913: T-Log files distribution using JMS
 *
 *    Rev 1.0   Jan 22 2003 10:04:58   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.transaction.TransactionLoggerIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.xml.InvalidXmlException;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLUtility;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class tests the creation of a TLog conforming to an IXRetail-like
 * format. This test simulates the activity in the job which will perform the
 * extraction of the transaction data, the creation of the actual XML and
 * updates of the transaction data to reflect the archival of the data.
 *
 * @version $Revision: /main/18 $
 */
public class POSLogWriter implements POSLogWriterIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -8425306184778515823L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(POSLogWriter.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/18 $";

    /**
     * transaction log
     */
    protected LogIfc tLog = null;

    /**
     * transaction logger
     */
    protected TransactionLoggerIfc tLogger = null;

    /**
     * document element
     */
    protected Document doc = null;

    /**
     * root element
     */
    protected Element rootElement = null;

    /**
     * first child element element
     */
    protected Element firstChildElement = null;

    /**
     * output file name
     */
    protected String outputXMLFileName = null;

    /**
     * export directory name
     */
    protected String exportDirectoryName = "";

    /**
     * file name prefix
     */
    protected String fileNamePrefix = DEFAULT_XML_FILE_NAME_PREFIX;

    /**
     * XML file name suffix
     */
    protected String xmlFileNameSuffix = DEFAULT_XML_FILE_NAME_EXTENSION;

    /**
     * backup file name
     */
    protected String backupXMLFileName = null;

    /**
     * Whether or not use a temp file when creating the string to send.
     */
    private boolean useTempFile;
    
    /** attribute to assess whether to compress message or not. */
    protected boolean compressed = false;    

    /**
     * Constructs POSLogWriter object.
     */
    public POSLogWriter()
    {
    }

    /**
     * Initializes XML TLog.
     *
     * @param batchID batch identifier
     * @exception Exception is thrown if error occurs creating document
     */
    public void initializePOSLog(String batchID) throws POSLogWriterException
    {
        rootElement = null;
        firstChildElement = null;
        try
        {
            tLog = IXRetailGateway.getFactory().getLogInstance();
            tLogger = IXRetailGateway.getFactory().getTransactionLoggerInstance();
            doc = XMLUtility.createDocument(tLog.getLogRootElement(), true);
            rootElement = doc.getDocumentElement();
            // add attributes to root element
            tLog.addAttributes(rootElement);
        }
        catch (Exception e)
        {
            logger.error("An error occurred during initialization.", e);
            throw new POSLogWriterException(e);
        }
    }

    /**
     * Processes a transaction.
     *
     * @param transaction transaction object read from database
     * @param batchTotal batch object
     * @exception XMLConversionException is thrown if error occurs
     */
    public void exportTransaction(TransactionIfc transaction, BatchTotalIfc batchTotal) throws Exception
    {
        try
        {
            // assume only one transaction
            Element transactionElement = tLogger.createTransactionElement(transaction, doc);

            performAdditionalProcessing(transaction); // stub in this version

            // if either effort fails, we won't write append element and update totals
            if (transactionElement != null)
            {
                if (firstChildElement == null)
                {
                    firstChildElement = transactionElement;
                }
                rootElement.appendChild(transactionElement);
                CurrencyIfc transactionTotal = DomainGateway.getBaseCurrencyInstance();

                // if not tenderable leave total to zero.
                // if transaction is tenderable, get total
                if (transaction instanceof TenderableTransactionIfc)
                {
                    // CR 27858 :In case of order transaction we could do the partial payment
                    // So take the payment amount as the transaction total instead of grand total
                    // value for all the line items in the order
                    if (transaction instanceof OrderTransactionIfc &&
                            transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED &&
                            transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
                    {
                        if (((OrderTransactionIfc) transaction).getPayment() != null)
                        {
                            transactionTotal = ((OrderTransactionIfc) transaction).getPayment().getPaymentAmount();
                        }
                        else
                        {
                            logger.info("OrderTransaction did not have a payment specified for its transactionTotal.");
                            transactionTotal = ((TenderableTransactionIfc) transaction).getTransactionTotals().getGrandTotal();
                        }
                    }
                    else
                    {
                        transactionTotal = ((TenderableTransactionIfc) transaction).getTransactionTotals().getGrandTotal();
                    }
                }
                // add to batch
                batchTotal.addTransaction(transaction.getTimestampEnd(), transactionTotal);
            }
        }
        catch (Exception e)
        {
            // if this is a log-writer exception, toss it
            if (e instanceof POSLogWriterException)
            {
                throw e;
            }

            // if not, wrap it and toss it.
            logger.error("A translation error has occurred.", e);
            throw new POSLogWriterException(e);
        }

    }

    /**
     * Perform additional transaction processing. This method is provided to
     * facilitate extensibility.
     * <P>
     *
     * @param transaction transaction object
     */
    public void performAdditionalProcessing(TransactionIfc transaction) throws Exception
    {
    }

    /**
     * Logs batch totals.
     * <P>
     *
     * @param totals batch totals
     * @exception Exception thrown if error occurs
     */
    public void logBatchTotals(BatchTotalIfc totals) throws Exception
    {
        totals.setBatchCompleteTimestamp();
        LogBatchTotalIfc logBatch = IXRetailGateway.getFactory().getLogBatchTotalInstance();
        Element batchElement = logBatch.createBatchTotalElement(totals, doc);

        if (batchElement != null)
        {
            // v1.0 has batch at end of document, v2.1 has it at the head
            if (batchElement.getNodeName().equals("POS360Batch"))
            {
                rootElement.appendChild(batchElement);
            }
            else
            {
                try
                {
                    rootElement.insertBefore(batchElement, firstChildElement);
                }
                catch (Exception e)
                {
                    // if we have a problem pre-pending the batch element, just carry on
                    logger.error("Igonoring problem pre-pending batch total: " + Util.throwableToString(e));
                }
                finally
                {
                    firstChildElement = null;
                }
            }
        }
    }

    /**
     * Write TLog file(s).
     *
     * @param batchID batch identifier
     * @exception Exception thrown if error occurs
     */
    public void writePOSLogFile(String batchID) throws Exception
    {
        outputXMLFileName = createXMLPOSLogFileName(batchID, exportDirectoryName);
        XMLUtility.buildXMLFile(doc, outputXMLFileName);
    }

    /**
     * Write TLog file(s) to a backup location.
     *
     * @param batchID batch identifier
     * @param directoryName the backup location
     * @exception IOException thrown if error occurs
     */
    public void backupPOSLogFile(String batchID, String directoryName) throws IOException
    {
        backupXMLFileName = createXMLPOSLogFileName(batchID, directoryName);

        try (FileInputStream fis = new FileInputStream(outputXMLFileName);)
        {
            int bytesAvailable = fis.available();
            if (bytesAvailable > 0)
            {
                byte[] data = new byte[bytesAvailable];
                fis.read(data);

                try (FileOutputStream fos = new FileOutputStream(backupXMLFileName);)
                {
                    fos.write(data);
                }
            }
        }
    }

    /**
     * Creates and returns file name for t-log file.
     *
     * @param batchID batch identifier
     * @return t-log file name
     */
    protected String createXMLPOSLogFileName(String batchID)
    {
        return createXMLPOSLogFileName(exportDirectoryName, batchID);
    }

    /**
     * Creates and returns file name for t-log file.
     *
     * @param batchID batch identifier
     * @param directoryName the directory where the file will reside
     * @return t-log file name
     */
    protected String createXMLPOSLogFileName(String batchID, String directoryName)
    {

        StringBuilder fileName = null;
        if (Util.isEmpty(directoryName))
        {
            fileName = new StringBuilder();
        }
        else
        {
            fileName = new StringBuilder();
            fileName.append(directoryName);
            fileName.append(System.getProperty("file.separator"));
        }

        fileName.append(fileNamePrefix);
        fileName.append(batchID);
        fileName.append("-");
        EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
        SimpleDateFormat format = new SimpleDateFormat("ssSSS");
        fileName.append(format.format(date.dateValue()));
        fileName.append(xmlFileNameSuffix);

        return (fileName.toString());

    }

    /**
     * Deletes t-log file. This is used if the transaction-mark operation fails.
     */
    public void deletePOSLogFile() throws Exception
    {
        File outputFile = new File(outputXMLFileName);
        outputFile.delete();

        // delete the backup copy also
        outputFile = new File(backupXMLFileName);
        outputFile.delete();
    }

    /**
     * Does any end-of-process cleanup.
     *
     * @exception exception thrown if error occurs
     */
    public void completeProcessing() throws Exception
    {
    }

    /**
     * Returns output XML file name.
     *
     * @return output XML file name
     */
    public String getOutputXMLFileName()
    {
        return outputXMLFileName;
    }

    /**
     * Returns backup XML file name.
     *
     * @return backup XML file name
     */
    public String getBackupXMLFileName()
    {
        return backupXMLFileName;
    }

    /**
     * Returns export directory name.
     *
     * @return export directory name
     */
    public String getExportDirectoryName()
    {
        return exportDirectoryName;
    }

    /**
     * Sets export directory name.
     *
     * @param value export directory name
     */
    public void setExportDirectoryName(String value)
    {
        exportDirectoryName = value;
    }

    /**
     * Method setQueueName; this class does not write to a queue, so this method
     * is empty.
     *
     * @param queueName
     */
    public void setQueueName(String queueName)
    {
    }

    /**
     * Method setQueueHostName; this class does not write to a queue, so this
     * method is empty.
     *
     * @param queueHostName
     */
    public void setQueueHostName(String queueHostName)
    {
    }

    /**
     * @return the useTempFile
     */
    public boolean isUseTempFile()
    {
        return useTempFile;
    }

    /**
     * @param useTempFile the useTempFile to set
     */
    public void setUseTempFile(boolean useTempFile)
    {
        this.useTempFile = useTempFile;
    }

    /**
     * Returns the document that was built up by the exportTransaction() method
     */
    public Document getDocument()
    {
        return doc;
    }
    
    /**
     * Method gets whether to compress the message or not.
     * @return boolean Flag whether the file is compressed.
     */
    public boolean isCompressed()
    {
        return this.compressed;
    }

    /**
     * Sets the compress attribute
     * @param  boolean compressed flag value
     */
    public void setCompressed(boolean value)
    {
        this.compressed = value;
    }    

    /**
     * @return
     * @throws InvalidXmlException 
     */
    protected String getXMLStringFromDocument() throws InvalidXmlException, IOException
    {
        if (isUseTempFile())
        {
            File tempFile = File.createTempFile("PosLog", null);
            // write the temp file
            XMLUtility.buildXMLFile(doc, tempFile.getPath());
            // delete the dom object to free up some memory
            doc = null;
            // read the file
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile)))
            {
                StringBuilder builder = new StringBuilder((int)tempFile.length());
                String readln = reader.readLine();
                while (readln != null)
                {
                    builder.append(readln);
                    readln = reader.readLine();
                }
                return builder.toString();
            }
        }

        // not using temp file, just read the string into memory.
        return XMLUtility.buildXMLString(doc);
    }

}
