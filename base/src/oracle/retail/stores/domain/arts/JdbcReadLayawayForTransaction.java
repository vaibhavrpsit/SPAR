/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLayawayForTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/13/09 - make layaway location required; refactor the way we
 *                         handle layaway reason codes
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    sgu       10/30/08 - check in after refresh
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:16 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
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
 *    Rev 1.0   Jun 03 2002 16:37:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:00:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadLayawayForTransaction implements the layaway lookup JDBC data store
 * operation.
 *
 * @see oracle.retail.stores.domain.arts.JdbcReadLayaway
 */
public class JdbcReadLayawayForTransaction extends JdbcReadLayaway
    implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8447497825906253234L;
    /** The logger to which log messages will be sent. */
    private static Logger logger = Logger.getLogger(JdbcReadLayawayForTransaction.class);
    /** revision number of this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcReadLayawayForTransaction()
    {
        setName("JdbcReadLayawayForTransaction");
    }

    /**
     * Executes the SQL statements against the database.
     * <P>
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadLayawayForTransaction.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        TransactionIfc layawayTransaction = (TransactionIfc)action.getDataObject();

        LayawayIfc layaway = readLayawayForTransaction(connection, layawayTransaction, layawayTransaction.getLocaleRequestor());

        dataTransaction.setResult(layaway);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadLayawayForTransaction.execute");
    }

    /**
     * Selects a layaway from the layaway table.
     *
     * @param dataConnection a connection to the database
     * @param inputLayawayTransaction layaway transaction containing key values
     * @return selected layaway
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     * @deprecated as of 13.1. Use {@link #readLayaway(JdbcDataConnection, LayawayIfc, LocaleRequestor)}
     */
    protected LayawayIfc readLayawayForTransaction(JdbcDataConnection dataConnection,
            TransactionIfc inputLayawayTransaction) throws DataException
    {
        return readLayawayForTransaction(dataConnection,inputLayawayTransaction, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }

    /**
     * Selects a layaway from the layaway table.
     *
     * @param dataConnection a connection to the database
     * @param inputLayawayTransaction layaway transaction containing key values
     * @param localeRequestor
     * @return selected layaway
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected LayawayIfc readLayawayForTransaction(JdbcDataConnection dataConnection,
                                                        TransactionIfc inputLayawayTransaction,
                                                            LocaleRequestor localeRequestor) throws DataException
    { // begin readLayawayForTransaction()
        LayawayIfc layaway = null;
        // set search values in layaway object
        LayawayIfc inputLayaway = DomainGateway.getFactory().getLayawayInstance();
        inputLayaway.setInitialTransactionBusinessDate(inputLayawayTransaction.getBusinessDay());
        inputLayaway.setInitialTransactionID(inputLayawayTransaction.getTransactionIdentifier());
        inputLayaway.setTrainingMode(inputLayawayTransaction.isTrainingMode());

        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildLayawaySQLStatement(inputLayaway);
        try
        {
            ResultSet rs = execute(dataConnection, sql);
            layaway = parseLayawayResultSet(rs);
            readLocationCode(dataConnection, layaway, localeRequestor);
            layaway.setLocaleRequestor(inputLayawayTransaction.getLocaleRequestor());
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Layaway lookup");
            throw new DataException(DataException.SQL_ERROR, "Layaway lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Layaway lookup", e);
        }

        return (layaway);
    }

    /**
     * Adds qualifiers for layaway transaction lookup to SQL statement.
     *
     * @param sql SQLSelectStatement object
     * @param layaway input layaway
     */
    protected void addLayawayQualifierColumns(SQLSelectStatement sql, LayawayIfc layaway)
    { // begin addLayawayQualifierColumns()
        TransactionIDIfc initialTransactionID = layaway.getInitialTransactionID();
        // add training mode, layaway stuff
        sql.addQualifier(FIELD_TRAINING_MODE, makeStringFromBoolean(layaway.getTrainingMode()));
        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(initialTransactionID.getStoreID()));
        sql
                .addQualifier(FIELD_LAYAWAY_ORIGINAL_WORKSTATION_ID, makeSafeString(initialTransactionID
                        .getWorkstationID()));
        sql.addQualifier(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE, dateToSQLDateString(layaway
                .getInitialTransactionBusinessDate()));
        sql.addQualifier(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER, Long.toString(initialTransactionID
                .getSequenceNumber()));

    }
}