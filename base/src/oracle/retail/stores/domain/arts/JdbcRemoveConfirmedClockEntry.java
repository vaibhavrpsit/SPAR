/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRemoveConfirmedClockEntry.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcRemoveConfirmedClockEntry removes a clock entry pair from the DB.
 * 
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcRemoveConfirmedClockEntry extends JdbcDataOperation
{
    private static final long serialVersionUID = -5267577581326162375L;
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcRemoveConfirmedClockEntry.class);
    /**
        revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Class constructor.
     */
    public JdbcRemoveConfirmedClockEntry()
    {
    }

    /**
        Executes the SQL statements against the database. <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadEmployeeClockEntries.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        EmployeeClockEntryPairIfc pair = 
            (EmployeeClockEntryPairIfc) action.getDataObject();
        removeClockEntry(connection, pair);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadEmployeeClockEntries.execute");
    }

    /**
        Removes the selected Clock Entry pair from the DB.
        @param  dataConnection  a connection to the database
        @param  pair the pair to remove
        @exception  DataException thrown when an error occurs executing the
        SQL against the DataConnection, or when processing the ResultSet
     */
    protected void removeClockEntry(JdbcDataConnection dataConnection,
                                    EmployeeClockEntryPairIfc pair)
        throws DataException
    {
        execute(dataConnection, buildClockEntrySQLStatement(pair));
    }

    protected SQLStatementIfc buildClockEntrySQLStatement(EmployeeClockEntryPairIfc pair)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(ARTSDatabaseIfc.TABLE_EMPLOYEE_CONFIRMED_CLOCK_ENTRY);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_IS_DELETED,
                      "'1'");
        sql.addQualifier(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_ID + " = " + pair.getPairID());

        return(sql);
    }

    /**
     * Executes the SQL Statement.
     * 
     * @param dataConnection a connection to the database
     * @param sql the SQl statement
     * @return result set
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected ResultSet execute(JdbcDataConnection dataConnection,
                SQLStatementIfc sql) throws DataException
    {

        ResultSet rs = null;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet) dataConnection.getResult();
        return rs;
    }
}
