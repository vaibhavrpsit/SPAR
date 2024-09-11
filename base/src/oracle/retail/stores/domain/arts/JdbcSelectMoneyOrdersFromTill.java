/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSelectMoneyOrdersFromTill.java /main/13 2012/05/21 15:50:19 cgreene Exp $
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
 *    7    360Commerce 1.6         5/16/2008 5:12:01 AM   Manas Sahu
 *         Instead of using NOT Clause for Select Query using the newly added
 *         NOT EXISTS clause. Code Reviewed by Naveen
 *    6    360Commerce 1.5         4/25/2007 10:01:08 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/25/2006 4:11:25 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:24 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 *   $:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:40    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:51     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
 *   $
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 07 2003 15:23:00   blj
 * cleaned up code and added javadoc
 * 
 *    Rev 1.0   Nov 04 2003 17:15:54   blj
 * Initial revision.
 *  
 *    Rev 1.0   Sep 20 2001 15:57:12   msg
 * Initial revision.
 * 
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class provides the methods needed to select money orders for pickup.
 * 
 * @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc
 * @version $Revision: /main/13 $
 */
public class JdbcSelectMoneyOrdersFromTill extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -6308161944473753295L;
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
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // pull the data from the action object
        HashMap params = (HashMap)action.getDataObject();
        TenderMoneyOrderIfc[] retrievedChecks = null;

        try
        {
            retrievedChecks = selectMoneyOrdersFromTill(connection, params);
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedChecks);
    }

    /**
        Returns the list of money orders that need to be picked up from the till
        @param  dataConnection  connection to the db
        @param  The HashMap containing all the data for the WHERE clause of the SQL statement
        @exception DataException upon error
     */
    public TenderMoneyOrderIfc[] selectMoneyOrdersFromTill(JdbcDataConnection dataConnection,
                                                 HashMap params)
    throws DataException
    {

        TenderMoneyOrderIfc[] checks = null;
        try
        {
            SQLSelectStatement sql = buildSQLStatement(params);
            dataConnection.execute(sql.getSQLString());
            checks = parseResultSet(dataConnection, params);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectMoneyOrdersFromTill", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectMoneyOrdersFromTill", e);
        }

        return(checks);
    }

    /**
        Builds SQL statement. <P>
        @return sql string
        @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildSQLStatement(HashMap params)
                                 throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        String tillId = (String)params.get("tillId");
        String storeId = (String)params.get("storeId");
        String tenderNationality = (String)params.get("tenderNationality");
        EYSDate businessDate = (EYSDate)params.get("businessDate");
        EYSDate pickupTime = (EYSDate)params.get("pickupTime");

        // define tables, aliases
        sql.addTable(TABLE_TRANSACTION);
        sql.addTable(TABLE_TENDER_LINE_ITEM);
        sql.addTable(TABLE_MONEY_ORDER_TENDER_LINE_ITEM);
//        sql.addTable(TABLE_POST_VOID_TRANSACTION);
        sql.setDistinctFlag(true);

        // add columns
        sql.addColumn(TABLE_TENDER_LINE_ITEM + "." + FIELD_TENDER_LINE_ITEM_AMOUNT);
        sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_AMOUNT_TENDERED);

        // add qualifiers
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TENDER_REPOSITORY_ID + " = '" + tillId + "'");
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = '" + storeId + "'");
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = " + dateToSQLDateString(businessDate));
        // tender nationality
        if (!tenderNationality.equals(DomainGateway.getBaseCurrencyInstance().getCountryCode()))
        {
            sql.addQualifier(TABLE_TENDER_LINE_ITEM + "."
                             + FIELD_TENDER_FOREIGN_CURRENCY_COUNTRY_CODE
                             + " = '" + tenderNationality + "'");
        }
        else
        {
            sql.addQualifier(TABLE_TENDER_LINE_ITEM + "."
                             + FIELD_TENDER_FOREIGN_CURRENCY_AMOUNT_TENDERED
                             + " = '0.00'");
        }
        if (pickupTime != null)
        {
            sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_BEGIN_DATE_TIMESTAMP + " > " + dateToSQLTimestampFunction(pickupTime));
        }
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_MONEY_ORDER_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = " + TABLE_MONEY_ORDER_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_MONEY_ORDER_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + TABLE_MONEY_ORDER_TENDER_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_MONEY_ORDER_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);
        // replaced subquery with NOT qualifier for MySQL
        sql.addNotExistTable(TABLE_POST_VOID_TRANSACTION);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + TABLE_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
      
        return(sql);
    }

    /**
        Parses result set and creates an array of TenderMoneyOrder objects. <P>
        @param dataConnection data connection
        @return Array of TenderMoneyOrder objects
        @exception SQLException thrown if result set cannot be parsed
                @exception DataException thrown if no records in result set
     */
    protected TenderMoneyOrderIfc[] parseResultSet(JdbcDataConnection dataConnection, HashMap params)
    throws SQLException, DataException
    {
        String tenderNationality = (String)params.get("tenderNationality");
        ResultSet rs = (ResultSet) dataConnection.getResult();
        int recordsFound = 0;

        Vector checkVec = new Vector();
        if (rs != null)
        {
            while (rs.next())
            {
                TenderMoneyOrderIfc retrievedCheck = instantiateTenderMoneyOrderIfc();
                convertResultSetEntry(retrievedCheck,
                                      rs,
                                      tenderNationality);
                checkVec.add(retrievedCheck);
            }
            // close result set
            rs.close();
        }

        TenderMoneyOrderIfc[] checks = null;
        if (checkVec.size() > 0)
        {
            checks = new TenderMoneyOrderIfc[checkVec.size()];
            checkVec.toArray(checks);
        }
        else
        {
            checks = new TenderMoneyOrderIfc[0];
        }
        
        return(checks);
    }

    /**
        Converts result set entry into a TenderMoneyOrderIfc object. <P>
        @param retrievedCheck TenderMoneyOrderIfc object
        @param rs ResultSet set at entry to be converted
        @return index of result set entries
        @exception SQLException thrown if error occurs
     */
    protected int convertResultSetEntry(TenderMoneyOrderIfc retrievedCheck,
                                        ResultSet rs,
                                        String tenderNationality)
                                        throws SQLException, DataException
    {
        int index = 0;
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
        retrievedCheck.setAmountTender(amount);

        if (!tenderNationality.equals(DomainGateway.getBaseCurrencyInstance().getCountryCode()))
        {
            CurrencyTypeIfc[] type = DomainGateway.getAlternateCurrencyTypes();
            CurrencyIfc alternateAmount = null;
            alternateAmount = DomainGateway.getAlternateCurrencyInstance(type[0].getCountryCode());
            BigDecimal amt = new BigDecimal(getSafeString(rs, ++index));
            alternateAmount.setDecimalValue(amt);
            ((TenderAlternateCurrencyIfc)retrievedCheck).setAlternateCurrencyTendered(alternateAmount);
        }
        else
        {
            index++;
        }

        return(index);
    }

    /**
            Instantiates TenderMoneyOrderIfc object. <P>
            @return TenderMoneyOrderIfc object
     */
    public TenderMoneyOrderIfc instantiateTenderMoneyOrderIfc()
    {
        return(DomainGateway.getFactory().getTenderMoneyOrderInstance());
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
