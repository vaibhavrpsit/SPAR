/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSendEMessage.java /main/17 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   01/10/11 - refactor blob helpers into one
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/19/09 - change access to blob helper
 *    mchellap  12/11/08 - Using jdbc utility methods for timestamps
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         3/29/2008 9:50:08 AM   Dwight D. Jennings
 *         changing column holding content of the EMessage from varchar 250 to
 *          a blob.
 *
 *         Reviewed by Dan Baker. Luis approved the column type change.
 *    6    360Commerce 1.5         3/25/2008 9:33:20 AM   Dwight D. Jennings
 *         update the truncation of the e-mail message for the shortened
 *         column length. reviewed by Sandy Gu.
 *    5    360Commerce 1.4         6/9/2006 2:38:36 PM    Brett J. Larsen CR
 *         18490 - UDM
 *         FL_NM_.* changed to NM_.*
 *         timestamp fields in DO_EMSG renamed to ts_..._emsg for consistency
 *    4    360Commerce 1.3         1/25/2006 4:11:25 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse
 *:
 *    6    .v700     1.2.1.2     11/16/2005 16:25:57    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    5    .v700     1.2.1.1     11/8/2005 16:38:15     Rohit Sachdeva  6606:
 *         EMessage Text
 *    4    .v700     1.2.1.0     11/8/2005 15:25:40     Rohit Sachdeva  6606:
 *         EMessage Text
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:51     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 04 2003 10:39:36   bwf
 * Uncommented out code to send email to database.
 * Resolution for 2365: No email sent  out  when fill / pickup/ cancel  order
 *
 *    Rev 1.0   Jun 03 2002 16:40:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:18   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;

import org.apache.log4j.Logger;

/**
 * This operation inserts an emessage into the emessage table.
 */
public class JdbcSendEMessage extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1083312692779432025L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(JdbcSendEMessage.class);

    //----------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //----------------------------------------------------------------------
    public JdbcSendEMessage()
    {
        super();
        setName("JdbcSendEMessage");
    }

    //----------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction
       @param  dataConnection
       @param  action
    **/
    //----------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSendEMessage.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        // grab arguments and call SendEMessage()
        EMessageIfc emessage = (EMessageIfc)action.getDataObject();
        SendEMessage(connection, emessage);

        if (logger.isDebugEnabled()) logger.debug( "JdbcSendEMessage.execute");
    }


    //----------------------------------------------------------------------
    /**
       Creates the SQL statements against the database. Sends the emessage
       object based upon the emessage argument.
       <P>
       @param  dataConnection
       @param  emessage
    **/
    //----------------------------------------------------------------------
    public void SendEMessage(JdbcDataConnection connection,
                             EMessageIfc emessage) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcEMessageWriteDataTransaction.SendEMessage()");
        if (logger.isInfoEnabled()) logger.info("Sending EMessage: " + emessage + "");

        boolean insertFailed = true; // insert failed
        String sentStr = "" + EMessageIfc.MESSAGE_STATUS_SENT;

        while (insertFailed)
        {
           SQLInsertStatement sql = new SQLInsertStatement();

           // set table
           sql.setTable(TABLE_EMESSAGE);

           // add columns
           sql.addColumn(FIELD_EMESSAGE_SENT, getSQLCurrentTimestampFunction());
           // set update timestamp to current date
           sql.addColumn(FIELD_EMESSAGE_UPDATED, getSQLCurrentTimestampFunction());
           sql.addColumn(FIELD_ORDER_LOCATION, "'" + emessage.getShipToLocationName() + "'");
           sql.addColumn(FIELD_EMESSAGE_SOURCE, "'" + emessage.getSender() + "'");
           sql.addColumn(FIELD_EMESSAGE_SUBJECT, "'" + emessage.getSubject() + "'");
           sql.addColumn(FIELD_EMESSAGE_RECIPIENT, "'" + emessage.getRecipients()[0] + "'");
           sql.addColumn(FIELD_EMESSAGE_STATUS, sentStr);
           sql.addColumn(FIELD_ORDER_ID, "'" + emessage.getOrderID() + "'");
           sql.addColumn(FIELD_CUSTOMER_ID, "'" + emessage.getCustomerID() + "'");
           sql.addColumn(FIELD_CUSTOMER_NAME,  "'" + emessage.getCustomerName() + "'");
           sql.addColumn(FIELD_EMESSAGE_CREATED, dateToSQLTimestampFunction(emessage.getTimestampBegin()));
           sql.addColumn(FIELD_RETAIL_STORE_ID, "'" + emessage.getShipToStoreID() + "'");

           // randomly generate message id > 0 ?
           Random rand = new Random();
           int nextRand = rand.nextInt();
           if (nextRand < 0)
           {
               nextRand *= -1;
           }
           String randStr = "" + nextRand;

           // randomize the EMessageID
           sql.addColumn(FIELD_EMESSAGE_ID, "'" + randStr + "'");
           if (logger.isInfoEnabled()) logger.info( "MSG ID: " + randStr + "");

           try
           {
               connection.execute(sql.getSQLString());

               if (0 < connection.getUpdateCount())    // inserted ok
               {
                   insertFailed = false;
               }


               // save the blob.
               HashMap<String,Object> map = new HashMap<String,Object>(1);
               map.put(FIELD_EMESSAGE_ID, randStr);
               DatabaseBlobHelperIfc helper = DatabaseBlobHelperFactory.getInstance().
                           getDatabaseBlobHelper(connection.getConnection());
               if(helper != null)
               {

                   String msgText = (emessage.getMessageText()).trim();
            	   helper.updateBlob(connection.getConnection(),
                		   TABLE_EMESSAGE,
                           FIELD_EMESSAGE_TEXT,
                           msgText.getBytes(),
                           map);
               }
           }

           catch (DataException de)
           {
               logger.warn(de.toString());
               if (de.getErrorCode() == DataException.UNKNOWN)
               {
                   throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
               }
               
               throw de;
           }
           catch (SQLException se)
           {
               connection.logSQLException(se, "emessage table");
               throw new DataException(DataException.SQL_ERROR, "emessage table", se);
           }
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcEMessageWriteDataTransaction.SendEMessage()");
    }
}
