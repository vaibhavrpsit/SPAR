/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadInstantCredit.java /main/14 2012/05/21 15:50:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     7    360Commerce 1.6         4/24/2008 11:22:22 PM  Manikandan Chellapan
 *           CR#30328 Updated copyright header
 *     6    360Commerce 1.5         4/22/2008 5:23:38 AM   Manikandan Chellapan
 *           CR#30328 Added operator id coloumn to the existing query
 *     5    360Commerce 1.4         6/8/2006 6:11:44 PM    Brett J. Larsen CR
 *          18490 - UDM - InstantCredit AuthorizationResponseCode changed to a
 *           String
 *     4    360Commerce 1.3         1/25/2006 4:11:15 PM   Brett J. Larsen
 *          merge 7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *     3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
 *    $:
 *     4    .v700     1.2.1.0     11/16/2005 16:27:09    Jason L. DeLeau 4215:
 *          Get rid of redundant ArtsDatabaseifc class
 *     3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *     2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *     1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *    $
 *    Revision 1.6  2004/04/09 16:55:46  cdb
 *    @scr 4302 Removed double semicolon warnings.
 *
 *     Revision 1.5  2004/02/17 17:57:37  bwf
 *    @scr 0 Organize imports.
 *
 *    Revision 1.4  2004/02/17 16:18:46  rhafernik
 *    @scr 0 log4j conversion
 *
 *    Revision 1.3  2004/02/12 17:13:17  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 23:25:23  bwf
 *    @scr 0 Organize imports.
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *    updating to pvcs 360store-current
 *
 *    Rev 1.0   Dec 31 2003 10:48:10   nrao Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * JdbcReadInstantCredit retrieves information from the instant credit table
 */
public class JdbcReadInstantCredit extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -7245774900666281112L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadInstantCredit.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Class constructor.
     */
    public JdbcReadInstantCredit()
    {
    }

    /**
     * Executes the SQL statements against the database.
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
            logger.debug("JdbcReadInstantCredit.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // look up the instant credit
        InstantCreditTransactionIfc ic = (InstantCreditTransactionIfc)action.getDataObject();
        InstantCreditIfc instantCredit = readInstantCredit(connection, ic);
        dataTransaction.setResult(instantCredit);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadInstantCredit.execute");
    }

    /**
     * Selects a row from the instant credit table.
     * 
     * @param dataConnection a connection to the database
     * @param ic InstantCreditTransactionIfc
     * @return InstantCreditIfc
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected InstantCreditIfc readInstantCredit(JdbcDataConnection dataConnection, InstantCreditTransactionIfc ic)
            throws DataException
    {
        InstantCreditIfc credit = null;
        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildInstantCreditSQLStatement(ic);
        try
        {
            ResultSet rs = execute(dataConnection, sql);
            credit = parseInstantCreditResultSet(rs);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Instant Credit lookup");
            throw new DataException(DataException.SQL_ERROR, "Instant Credit lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Instant Credit lookup", e);
        }

        return (credit);
    }

    /**
     * Builds and returns SQL statement for retrieving an instant credit.
     * 
     * @param iCredit InstantCreditTransactionIfc
     * @return SQL select statement for retrieving an instant credit
     */
    protected SQLSelectStatement buildInstantCreditSQLStatement(InstantCreditTransactionIfc iCredit)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_INSTANT_CREDIT);
        // add select columns
        addInstantCreditSelectColumns(sql);
        // add qualifiers
        addInstantCreditQualifierColumns(sql, iCredit);
        return (sql);
    }

    /**
     * Adds select columns for instant credit lookup to SQL statement.
     * 
     * @param sql SQLSelectStatement object
     */
    protected void addInstantCreditSelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(FIELD_AUTHORIZATION_RESPONSE);
        sql.addColumn(FIELD_EMPLOYEE_ID);
        sql.addColumn(FIELD_OPERATOR_ID);
    }

    /**
     * Adds qualifiers for instant credit lookup to SQL statement.
     * 
     * @param sql SQLSelectStatement object
     * @param iCredit instantCreditTransactionIfc
     */
    protected void addInstantCreditQualifierColumns(SQLSelectStatement sql, InstantCreditTransactionIfc iCredit)
    {
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(iCredit.getFormattedStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(iCredit.getFormattedWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(iCredit.getTransactionSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(iCredit.getBusinessDay()));
    }

    /**
     * Parse instant credit result set and returns instant Credit object.
     * 
     * @param rs ResultSet object
     * @return InstantCreditIfc
     * @exception thrown if error parsing result set
     */
    protected InstantCreditIfc parseInstantCreditResultSet(ResultSet rs) throws DataException, SQLException
    {
        InstantCreditIfc credit = null;
        try
        {
            if (rs.next())
            {
                credit = DomainGateway.getFactory().getInstantCreditInstance();
                credit.setInstantCreditSalesAssociate(DomainGateway.getFactory().getEmployeeInstance());
                int index = 0;
                // parse result set fields
                int code = Integer.parseInt(getSafeString(rs, ++index));
                credit.setApprovalStatus(InstantCreditApprovalStatus.getByCode(code));
                credit.getInstantCreditSalesAssociate().setEmployeeID(getSafeString(rs, ++index));
                credit.getInstantCreditSalesAssociate().setAlternateID((getSafeString(rs, ++index)));
            }
            rs.close();
        }

        catch (SQLException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Instant Credit lookup", e);
        }

        if (credit == null)
        {
            throw new DataException(DataException.NO_DATA,
                    "No instant credit was found processing the result set in JdbcReadInstantCredit.");
        }

        // close result set
        rs.close();

        return (credit);

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
    protected ResultSet execute(JdbcDataConnection dataConnection, SQLSelectStatement sql) throws DataException
    {
        ResultSet rs;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet)dataConnection.getResult();
        return rs;
    }
}
