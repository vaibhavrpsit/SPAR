/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSelectChecksFromTill.java /main/18 2012/05/21 15:50:19 cgreene Exp $
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
 *    abhayg    03/22/11 - added fix for CAD CHECK ISSUE
 *    abhayg    03/22/11 - XbranchMerge abhayg_bug-11841077 from main
 *    rsnayak   11/10/10 - Post voided cheque fix
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    7    360Commerce 1.6         5/6/2008 6:15:08 PM    Manas Sahu      The
 *         SQLSelectStatement addNotQualifier was not being used in the proper
 *          way. Where a NOT EXISTS was required a NOT was being used. the NOT
 *          qualifier clubs all the added qualifier as a single statement.
 *         Hence adding a NOT EXISTS clause so as not to select
 *         PostVoidTransactionTender types at the time of TillPickup. Code
 *         reviewed by Owen
 *    6    360Commerce 1.5         4/25/2007 10:01:08 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/25/2006 4:11:25 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:24 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:14    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:51     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
 *
 *   Revision 1.4  2004/09/09 14:06:27  kll
 *   @scr 7140: db2 syntax considerations
 *
 *   Revision 1.3  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.2  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Feb 10 2004 14:30:12   bwf
 * Refactor echeck.
 * 
 *    Rev 1.3   Jan 20 2004 17:00:58   kll
 * remove imports and unused variables
 * 
 *    Rev 1.2   Jan 20 2004 16:58:02   kll
 * Exclusion of ECheck tenders from till counts via a persistent boolean value 
 * Resolution for 3604: Pick-up Till Report does not distinguish between Checks and e-Checks and Total is incorrect
 * 
 *    Rev 1.1   Oct 02 2003 14:53:40   nrao
 * Replaced the subquery in sql query with NOT qualifier for compliance with MySQL standards.
 * Resolution for 3367: MySQL Migration
 * 
 *    Rev 1.0   Aug 29 2003 15:33:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jul 16 2003 15:09:14   DCobb
 * Use String.equals() method.
 * Resolution for POS SCR-3149: Register Reports- Pickup of US check is not on the reports
 * 
 *    Rev 1.2   Jun 23 2003 13:33:22   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 * 
 *    Rev 1.1   Jun 10 2002 11:14:58   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 * 
 *    Rev 1.2   Jun 07 2002 17:47:46   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 * 
 *    Rev 1.1   Mar 18 2002 22:49:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:08:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   11 Mar 2002 16:33:58   epd
 * Fixed Till check pickups where business date differs from system date
 * Resolution for POS SCR-1545: Till Pickup for Check only works on 1st till of the day
 * 
 *    Rev 1.0   Sep 20 2001 15:57:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:50   msg
 * header update
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
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class provides the methods needed to select Checks for pickup.
 * 
 * @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc
 * @version $Revision: /main/18 $
 */
public class JdbcSelectChecksFromTill extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 3825187532499153156L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

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
        TenderCheckIfc[] retrievedChecks = null;

        try
        {
            retrievedChecks = selectChecksFromTill(connection, params);
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedChecks);
    }

    /**
        Returns the list of checks that need to be picked up from the till
        @param  dataConnection  connection to the db
        @param  The HashMap containing all the data for the WHERE clause of the SQL statement
        @exception DataException upon error
     */
    public TenderCheckIfc[] selectChecksFromTill(JdbcDataConnection dataConnection,
                                                 HashMap params)
    throws DataException
    {

        TenderCheckIfc[] checks = null;
        try
        {
        	int lastTillPickUpTranId=getLastTillPickUpTranSaction(dataConnection,params);
            SQLSelectStatement sql = buildSQLStatement(params,lastTillPickUpTranId);
            dataConnection.execute(sql.getSQLString());
            checks = parseResultSet(dataConnection, params);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectChecksFromTill", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectChecksFromTill", e);
        }

        return(checks);
    }

    /**
        Builds SQL statement. <P>
        @return sql string
        @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildSQLStatement(HashMap params,int lastTillPickUpTranId)
                                 throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        String tillId = (String)params.get("tillId");
        String storeId = (String)params.get("storeId");
        String tenderNationality = (String)params.get("tenderNationality");
        EYSDate businessDate = (EYSDate)params.get("businessDate");
        EYSDate pickupTime = (EYSDate)params.get("pickupTime");
        Boolean checkFilter = (Boolean)params.get("checkFilter");


        // define tables, aliases
        sql.addTable(TABLE_TRANSACTION);
        sql.addTable(TABLE_TENDER_LINE_ITEM);
        sql.addTable(TABLE_CHECK_TENDER_LINE_ITEM);
//        sql.addTable(TABLE_POST_VOID_TRANSACTION);
        sql.setDistinctFlag(true);

        // add columns
        sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT);
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
                             + " = 0.00");
        }
        if (pickupTime != null)
        {
            sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_BEGIN_DATE_TIMESTAMP + " > " + dateToSQLTimestampFunction(pickupTime));
        }
        // do NOT include eChecks, only deposited checks
        if (checkFilter.booleanValue())
        {    
            sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_TENDER_TYPE_CODE + " = '" + 
                 DomainGateway.getFactory().getTenderTypeMapInstance().getCode(TenderLineItemConstantsIfc.TENDER_TYPE_CHECK) + "'");
        }
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + TABLE_CHECK_TENDER_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_CHECK_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = " + TABLE_CHECK_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_CHECK_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_CHECK_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);
        // Separate out Post Voided Cheques
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_STATUS_CODE + " != '8'");
        // replaced subquery with NOT qualifier for MySQL
        sql.addNotExistTable(TABLE_POST_VOID_TRANSACTION);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + TABLE_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + TABLE_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + TABLE_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addNotExistQualifier(TABLE_POST_VOID_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + TABLE_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(TABLE_TRANSACTION+ "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " > "+ lastTillPickUpTranId);
        return(sql);
    }

    /**
        Parses result set and creates an array of TenderCheck objects. <P>
        @param dataConnection data connection
        @return Array of TenderCheck objects
        @exception SQLException thrown if result set cannot be parsed
                @exception DataException thrown if no records in result set
     */
    protected TenderCheckIfc[] parseResultSet(JdbcDataConnection dataConnection, HashMap params)
    throws SQLException, DataException
    {
        String tenderNationality = (String)params.get("tenderNationality");
        ResultSet rs = (ResultSet) dataConnection.getResult();

        Vector checkVec = new Vector();
        if (rs != null)
        {
            while (rs.next())
            {
                TenderCheckIfc retrievedCheck = instantiateTenderCheckIfc();
                convertResultSetEntry(retrievedCheck,
                                      rs,
                                      tenderNationality);
                checkVec.add(retrievedCheck);
            }
            // close result set
            rs.close();
        }

        TenderCheckIfc[] checks = null;
        if (checkVec.size() > 0)
        {
            checks = new TenderCheckIfc[checkVec.size()];
            checkVec.toArray(checks);
        }
        else
        {
            checks = new TenderCheckIfc[0];
        }
        
        return(checks);
    }
    
    /**
    Returns the Last Till PickUp Transaction Id
    @param  dataConnection  connection to the db
    @param  The HashMap containing all the data for the WHERE clause of the SQL statement
    @exception DataException upon error
     */
    
    protected int getLastTillPickUpTranSaction(JdbcDataConnection dataConnection,HashMap params) 
    throws DataException
	{
    	int  lastTillPickUpTranId = 0;
	    try
	      {
	          SQLSelectStatement sql = buildSQLStatementForLastPickUp(params);
	          dataConnection.execute(sql.getSQLString());
	          lastTillPickUpTranId = parseResultSetForLastTillPickUp(dataConnection);
	      }
	      catch (SQLException se)
	      {
	          throw new DataException(DataException.SQL_ERROR, "selectLastTillPickUpTransactionId", se);
	      }
	      catch (DataException de)
	      {
	          throw de;
	      }
	      catch (Exception e)
	      {
	          throw new DataException(DataException.UNKNOWN, "selectLastTillPickUpTransactionId", e);
	      }

	      return(lastTillPickUpTranId);
	
	    }

    /**
    Parses result set and get last Till PickUp Transaction Id <P>
    @param dataConnection data connection
    @return Last Till Pickup Transaction Id
    @exception SQLException thrown if result set cannot be parsed
           
    */
    
    protected int parseResultSetForLastTillPickUp(JdbcDataConnection dataConnection) throws DataException, SQLException 
    {
    	ResultSet rs = (ResultSet) dataConnection.getResult();

    	int  lastTillPickUpTranId = 0;
    	if (rs != null)
    	{
    		if(rs.next())
    		{
    			lastTillPickUpTranId=rs.getInt(1);
	        }
       	
    		rs.close();
    	}

    	return lastTillPickUpTranId;
    }

    
    /**
    Builds SQL statement. <P>
    @return sql string
    @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildSQLStatementForLastPickUp(HashMap params)   throws SQLException 
    {
	   SQLSelectStatement sql = new SQLSelectStatement();

       String tillId = (String)params.get("tillId");
       String storeId = (String)params.get("storeId");
       String tenderNationality = (String)params.get("tenderNationality");
       EYSDate businessDate = (EYSDate)params.get("businessDate");
       EYSDate pickupTime = (EYSDate)params.get("pickupTime");
       Boolean checkFilter = (Boolean)params.get("checkFilter");
       
       // define tables, aliases
       sql.addTable(TABLE_TENDER_PICKUP_TRANSACTION);
       sql.addMaxFunction(FIELD_TRANSACTION_SEQUENCE_NUMBER);
       
       // add qualifiers
       //sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TENDER_REPOSITORY_ID + " = '" + tillId + "'");
       sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = '" + storeId + "'");
       sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_TENDER_REPOSITORY_ID + " = '" + tillId + "'");
       sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + dateToSQLDateString(businessDate));
       sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "."
               + FIELD_TENDER_PICKUP_TENDER_COUNTRY_CODE
               + " = '" + tenderNationality + "'");
       if (checkFilter.booleanValue())
       {    
           sql.addQualifier(TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_TENDER_TYPE_CODE,inQuotes(DomainGateway.getFactory().getTenderTypeMapInstance().getCode(TenderLineItemConstantsIfc.TENDER_TYPE_CHECK)));
       }
       
       return (sql);
       
       
}

    /**
        Converts result set entry into a TenderCheckIfc object. <P>
        @param retrievedCheck TenderCheckIfc object
        @param rs ResultSet set at entry to be converted
        @return index of result set entries
        @exception SQLException thrown if error occurs
     */
    protected int convertResultSetEntry(TenderCheckIfc retrievedCheck,
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
            Instantiates TenderCheckIfc object. <P>
            @return TenderCheckIfc object
     */
    public TenderCheckIfc instantiateTenderCheckIfc()
    {
        return(DomainGateway.getFactory().getTenderCheckInstance());
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
