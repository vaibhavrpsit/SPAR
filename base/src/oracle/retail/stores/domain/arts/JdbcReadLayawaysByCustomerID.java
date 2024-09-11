/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLayawaysByCustomerID.java /main/17 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/16/13 - Modified to prevent retrieval of suspended Layaways
 *                         for payments or completion.
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    rrkohli   12/01/10 - fix to search suspended lawaway transaction by
 *                         customer info
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    sgu       10/30/08 - check in after refresh
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:16 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:16    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:44     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 15 2003 17:25:48   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Jan 28 2003 14:21:46   crain
 * Removed the status qualifier
 * Resolution for 1860: Layaway -- different results when searching for a completed layway by customer and by layaway number
 *
 *    Rev 1.0   Jun 03 2002 16:37:44   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads POS layaway summaries from a database, based on customer
 * id. It contains the methods that read the transaction tables in the database.
 * 
 * @version $Revision: /main/17 $
 */
public class JdbcReadLayawaysByCustomerID extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1102791496358852692L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadLayawaysByCustomerID.class);

    /**
     * Class constructor.
     */
    public JdbcReadLayawaysByCustomerID()
    {
        super();
        setName("JdbcReadLayawaysByCustomerID");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadLayawaysByCustomerID.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        /*
         * Send back the correct layaway summary (or lack thereof)
         */
        LayawaySummaryEntryIfc[] layawaySummaries;
        layawaySummaries = selectLayawaySummaries(connection, (LayawayIfc)action.getDataObject());

        dataTransaction.setResult(layawaySummaries);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadLayawaysByCustomerID.execute");
    }

    /**
        Selects layaway summaries from the layaway table.
        <P>
        @param  dataConnection      a connection to the database
        @param  customerID          the customer ID to search for layaway
        @exception  DataException thrown when an error occurs executing the
                                  SQL against the DataConnection, or when
                                  processing the ResultSet
     */
    public LayawaySummaryEntryIfc[] selectLayawaySummaries(JdbcDataConnection dataConnection,
                                            LayawayIfc inputLayaway)
                                            throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadLayawaysByCustomerID.selectLayaway()");

        LayawaySummaryEntryIfc[] layawaySummaryArray = null;

        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildLayawaySQLStatement(inputLayaway);

        Vector<LayawaySummaryEntryIfc> layawaySummaries = new Vector<LayawaySummaryEntryIfc>(2);
        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            /*
             * Grab the fields selected from the database
             */
            while (rs.next())
            {
                // Create a layaway summary based on the data retrieved
                // from the database
                LayawaySummaryEntryIfc layawaySummary
                    = instantiateLayawaySummaryEntry(inputLayaway);

                int index = 0;

                // Read the result set
                layawaySummary.setLayawayID(getSafeString(rs, ++index));
                layawaySummary.getInitialTransactionID().setStoreID(getSafeString(rs, ++index));
                layawaySummary.getInitialTransactionID().setWorkstationID(getSafeString(rs, ++index));
                layawaySummary.setInitialTransactionBusinessDate(getEYSDateFromString(rs, ++index));
                layawaySummary.getInitialTransactionID().setSequenceNumber(rs.getInt(++index));
                layawaySummary.setStatus(rs.getInt(++index));
                checkForExpiredLayaway(layawaySummary, dateToEYSDate(rs, ++index));
                layawaySummary.setExpirationDate(dateToEYSDate(rs, ++index));
                layawaySummary.setBalanceDue(getCurrencyFromDecimal(rs, ++index));

                layawaySummaries.addElement(layawaySummary);

            }
            rs.close();

            layawaySummaryArray = new LayawaySummaryEntryIfc[layawaySummaries.size()];
            layawaySummaries.copyInto(layawaySummaryArray);

        }
        catch (DataException de)
        {
            logger.warn( "Data exception retrieving LayawaySummaryEntry from Database" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "layaway table");
            throw new DataException(DataException.SQL_ERROR, "layaway table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "layaway table", e);
        }

        if (layawaySummaries.isEmpty())
        {
            logger.warn( "No layaways found");
            throw new DataException(DataException.NO_DATA, "No layaways found");
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadLayawaysByCustomerID.selectLayaway()");

        return(layawaySummaryArray);
    }

    /**
        Builds and returns SQL statement for retrieving a layaway. <P>
        @param layaway input layaway
        @return SQL select statement for retrieving a layaway
     */
    protected SQLSelectStatement buildLayawaySQLStatement(LayawayIfc layaway)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_LAYAWAY);
        // add select columns
        addLayawaySelectColumns(sql);
        // add qualifiers
        addLayawayQualifierColumns(sql,
                                   layaway);
        sql.addOrdering(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE + " DESC");

        return(sql);
    }


    /**
        Adds select columns for layaway lookup to SQL statement. <P>
        @param sql SQLSelectStatement object
     */
    protected void addLayawaySelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(FIELD_LAYAWAY_ID);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_LAYAWAY_STATUS);
        sql.addColumn(FIELD_LAYAWAY_GRACE_PERIOD);
        sql.addColumn(FIELD_LAYAWAY_EXPIRATION_DATE);
        sql.addColumn(FIELD_LAYAWAY_BALANCE_DUE);
    }

    /**
        Adds qualifiers for layaway lookup to SQL statement. <P>
        @param sql SQLSelectStatement object
        @param layaway input layaway
     */
    protected void addLayawayQualifierColumns(SQLSelectStatement sql,
                                              LayawayIfc layaway)
    {
        // add layaway ID to predicate
        sql.addQualifier(FIELD_CUSTOMER_ID,
                         makeSafeString(layaway.getCustomer().getCustomerID()));
        sql.addQualifier(FIELD_TRAINING_MODE,
                         makeStringFromBoolean(layaway.getTrainingMode()));
            sql.addQualifier("( " + FIELD_LAYAWAY_STATUS + " = " +
                         Integer.toString(LayawayConstantsIfc.STATUS_NEW) +
                         " or " +
                         FIELD_LAYAWAY_STATUS + " = " +
                         Integer.toString(LayawayConstantsIfc.STATUS_ACTIVE) +
                         " or " +
                         FIELD_LAYAWAY_STATUS + " = " +
                         Integer.toString(LayawayConstantsIfc.STATUS_EXPIRED) +
                         " )");
}

    /**
        Switches the status to Expired if the status is active and
        the grace period has passed. <P>
        @param summary LayawaySummaryEntryIfc to check
        @param gracePeriodDate Date of end of grace period

     */
    protected void checkForExpiredLayaway(LayawaySummaryEntryIfc summary,
                                          EYSDate gracePeriodDate)
    {
        // get current date and set to date-type-only
        EYSDate currentDate = DomainGateway.getFactory().getEYSDateInstance();
        currentDate.initialize(EYSDate.TYPE_DATE_ONLY);

        if ((summary.getStatus() == LayawayConstantsIfc.STATUS_ACTIVE ||
             summary.getStatus() == LayawayConstantsIfc.STATUS_NEW)   &&
            gracePeriodDate.before(currentDate))
        {
                summary.setStatus(LayawayConstantsIfc.STATUS_EXPIRED);
        }
        // Include if we want to change an expired back to active due
        // to changes in system time.
        else if (summary.getStatus() == LayawayConstantsIfc.STATUS_EXPIRED &&
                 gracePeriodDate.after(currentDate))
        {
            // summaries don't have previous status, so if it went from
            // new to expired, this isn't exactly accurate.
            summary.setStatus(LayawayConstantsIfc.STATUS_ACTIVE);
        }
    }

    /**
       Instantiates an object implementing the LayawaySummaryEntryIfc interface. <P>
       @return object implementing LayawaySummaryEntryIfc
    **/
        static protected LayawaySummaryEntryIfc instantiateLayawaySummaryEntry(LayawayIfc inputLayaway)
    {
        LayawaySummaryEntryIfc layawaySummary = DomainGateway.getFactory().getLayawaySummaryEntryInstance();
        TransactionIDIfc transactionID = instantiateTransactionID();
        layawaySummary.setInitialTransactionID(transactionID);
        layawaySummary.setLocaleRequestor(inputLayaway.getLocaleRequestor());

        return(layawaySummary);
    }

    /**
       Instantiates an object implementing the TransactionIDIfc interface. <P>
       @return object implementing TransactionIDIfc
    **/
        static protected TransactionIDIfc instantiateTransactionID()
    {
        return(DomainGateway.getFactory().getTransactionIDInstance());
    }
}
