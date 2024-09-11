/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/JMSPOSLogWriter.java /main/12 2013/05/02 16:49:46 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/04/11 - added ability to stream poslog xml to a file
 *    cgreene   08/10/10 - added ability to check xml for wellformness
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:56 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:08 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:30:48  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/02/17 17:57:39  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.3  2004/02/17 16:18:51  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.2  2004/02/12 17:13:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   01 Jul 2003 14:33:00   jgs
 * Initial revision.
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

import java.io.IOException;

import javax.jms.DeliveryMode;
import javax.jms.ObjectMessage;

import oracle.retail.stores.compression.CompressionUtility;
import oracle.retail.stores.foundation.comm.jms.JMSBroker;
import oracle.retail.stores.foundation.comm.jms.JMSQueueSender;

/**
 * This class tests the creation of a TLog conforming to an IXRetail-like
 * format. This test simulates the activity in the job which will perform the
 * extraction of the transaction data, the creation of the actual XML and
 * updates of the transaction data to reflect the archival of the data.
 * 
 * @version $Revision: /main/12 $
 */
public class JMSPOSLogWriter extends POSLogWriter
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 5819494604466312719L;

    protected static final long MILLISECONDS_PER_HOUR = 1000 * 60 * 60;

    /**
     * The name of the queue to which the PosLog will be written
     */
    private String queueName = null;

    /**
     * The name of the host on which the queue resides.
     */
    private String queueHostName = null;

    /**
     * The JMS sender object that writes entries to the queue.
     */
    private JMSQueueSender sender = null;

    /**
     * Constructs POSLogWriter object.
     */
    public JMSPOSLogWriter()
    {
    }

    /**
     * Write TLog file(s).
     * 
     * @param batchID batch identifier
     * @exception Exception thrown if error occurs
     */
    @Override
    public void writePOSLogFile(String batchID) throws Exception
    {
        // create and configure the sender if not done already
        if (sender == null)
        {
            if (queueHostName == null)
            {
                sender = JMSBroker.getInstance().getQueueSender(queueName);
            }
            else
            {
                sender = JMSBroker.getInstance(queueHostName).getQueueSender(queueName);
            }
            sender.setDeliveryMode(DeliveryMode.PERSISTENT);
            sender.setTimeToLive(24 * 7 * MILLISECONDS_PER_HOUR);
        }
        
        // Get the string to write to the queue
        String xmlString = getXMLStringFromDocument();
        ObjectMessage msg = null;
        if (isCompressed() == true)
        {
            byte [] xmlBytes = CompressionUtility.compressForTransport(xmlString);
            msg = sender.createObjectMessage(xmlBytes);
            msg.setStringProperty(COMPRESSION_FLAG, "Y");
        }
        else
        // Write the log string to the queue.
        {
            msg = sender.createObjectMessage(xmlString);
        }
       
        sender.send(msg);
    }

    /**
     * Write TLog file(s) to a backup location.
     * 
     * @param batchID batch identifier
     * @param directoryName the backup location
     * @exception IOException thrown if error occurs
     */
    @Override
    public void backupPOSLogFile(String batchID, String directoryName) throws IOException
    {
    }

    /**
     * Deletes t-log file. This is used if the transaction-mark operation fails.
     */
    @Override
    public void deletePOSLogFile() throws Exception
    {
    }

    /**
     * Creates and returns file name for t-log file.
     * 
     * @param batchID batch identifier
     * @return t-log file name
     */
    @Override
    protected String createXMLPOSLogFileName(String batchID)
    {
        return null;
    }

    /**
     * Creates and returns file name for t-log file.
     * 
     * @param batchID batch identifier
     * @param directoryName the directory where the file will reside
     * @return t-log file name
     */
    @Override
    protected String createXMLPOSLogFileName(String batchID, String directoryName)
    {
        return null;
    }

    /**
     * Method setQueueName
     * 
     * @param queueName
     */
    @Override
    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    /**
     * Method setQueueHostName
     * 
     * @param queueHostName
     */
    @Override
    public void setQueueHostName(String queueHostName)
    {
        this.queueHostName = queueHostName;
    }
    
   
}
