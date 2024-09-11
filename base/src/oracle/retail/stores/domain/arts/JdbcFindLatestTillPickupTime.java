/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFindLatestTillPickupTime.java /main/13 2012/05/21 15:50:17 cgreene Exp $
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
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/16/2008 5:12:01 AM   Manas Sahu
 *         Instead of using NOT Clause for Select Query using the newly added
 *         NOT EXISTS clause. Code Reviewed by Naveen
 *    5    360Commerce 1.4         4/25/2007 10:01:17 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:07 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:54 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:55    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:54     Robert Pearse
 *
 *   Revision 1.5  2004/08/13 13:35:45  kll
 *   @scr 0: deprecation fixes
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 02 2003 14:50:30   nrao
 * Replaced the subquery in sql query with NOT qualifier for compliance with MySQL standards.
 * Resolution for 3367: MySQL Migration
 *
 *    Rev 1.0   Aug 29 2003 15:30:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 23 2003 13:33:22   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.0   Jun 03 2002 16:36:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:34   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class provides the methods needed to find the most recent till pickup.
 * 
 * @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc
 * @version $Revision: /main/13 $
 */
public class JdbcFindLatestTillPickupTime extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1561542335946179748L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    @SuppressWarnings("unchecked")
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // pull the data from the action object
        Map<String,Object> params = (Map<String,Object>)action.getDataObject();
        EYSDate retrievedPickupTime = null;

        try
        {
            retrievedPickupTime = findLatestTillPickupTime(connection, params);
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedPickupTime);
    }

    /**
        Returns the time of the most recent till pickup
        @param  dataConnection  connection to the db
        @param  The HashMap containing all the data for the WHERE clause of the SQL statement
        @exception DataException upon error
     */
    public EYSDate findLatestTillPickupTime(JdbcDataConnection dataConnection,
                                            Map<String,Object> params)
    throws DataException
    {
        EYSDate pickupTime = null;
        try
        {
            SQLSelectStatement sql = buildSQLStatement(params);
            dataConnection.execute(sql.getSQLString());
            pickupTime = parseResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "findLatestTillPickupTime", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "findLatestTillPickupTime", e);
        }

        return(pickupTime);
    }

    /**
        Builds SQL statement. <P>
        @return sql string
        @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildSQLStatement(Map<String,Object> params)
    throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        String tillID = (String)params.get("tillId");
        String storeID = (String)params.get("storeId");
        EYSDate businessDate = (EYSDate)params.get("businessDate");
        String tenderName = (String)params.get("tenderName");
        String tenderNationality = (String)params.get("tenderNationality");

        // define tables, aliases
        sql.addTable(TABLE_TRANSACTION);
        sql.addTable("tr_pkp_tnd");
//        sql.addTable(TABLE_POST_VOID_TRANSACTION);
        sql.setDistinctFlag(true);

        // add columns
        sql.addColumn(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_END_DATE_TIMESTAMP);

        // add qualifiers
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TENDER_REPOSITORY_ID + " = '" + tillID + "'");
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = '" + storeID + "'");
        sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + dateToSQLDateString(businessDate));
        sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_TENDER_TYPE_CODE + " = '" + tenderName + "'");
        sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_TENDER_PICKUP_TENDER_COUNTRY_CODE + " = '" + tenderNationality + "'");
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
        // replaced subquery with NOT qualifier for MySQL
        sql.addNotExistTable(TABLE_POST_VOID_TRANSACTION);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + TABLE_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
      
        sql.addOrdering(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_END_DATE_TIMESTAMP + " desc");

        return(sql);
    }

    /**
        Parses result set and creates an EYSDate object. <P>
        @param dataConnection data connection
        @return Array of TenderCheck objects
        @exception SQLException thrown if result set cannot be parsed
                @exception DataException thrown if no records in result set
     */
    protected EYSDate parseResultSet(JdbcDataConnection dataConnection)
    throws SQLException, DataException
    {
        EYSDate retrievedDate = null;
        ResultSet rs = (ResultSet) dataConnection.getResult();


        if (rs != null)
        {
            if (rs.next())
            {
                retrievedDate = convertResultSetEntry(rs);
            }
            // close result set
            rs.close();
        }

        return(retrievedDate);
    }

    /**
        Converts result set entry into a TenderCheckIfc object. <P>
        @param retrievedCheck TenderCheckIfc object
        @param rs ResultSet set at entry to be converted
        @return index of result set entries
        @exception SQLException thrown if error occurs
     */
    protected EYSDate convertResultSetEntry(ResultSet rs)
    throws SQLException, DataException
    {
        int index = 0;
        EYSDate retrievedDate = new EYSDate(timestampToDate(rs.getTimestamp(++index)));

        return(retrievedDate);
    }

    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
