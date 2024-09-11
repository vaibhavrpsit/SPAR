/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadDepartment.java /main/17 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    cgreen 09/25/09 - XbranchMerge cgreene_bug-8931126 from
 *                      rgbustores_13.1x_branch
 *    cgreen 09/24/09 - refactor SQL statements up support preparedStatements
 *                      for updates and inserts to improve dept hist perf
 *    ohorne 03/13/09 - added support for localized department names
 *    cgreen 03/01/09 - upgrade to using prepared statements for PLU
 *    mipare 10/16/08 - dept list changes for localized desc
 *    mipare 10/16/08 - department description changes for localized text
 *    mipare 10/16/08 - dept list changes
 *    mipare 10/16/08 - dept list changes
 *    mipare 10/16/08 - dept list changes
 *    mipare 10/15/08 - changes for department list for locale requestor
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================
     $Log:
      9    360Commerce 1.8         8/15/2007 11:34:49 AM  Alan N. Sinton  CR
           28333 Netting out the restocking fees from the department sales
           report.
      8    360Commerce 1.7         7/26/2007 7:59:53 AM   Alan N. Sinton  CR
           27192 Make item lookup depend on department tax group ID if item's
           tax group ID is invalid.
      7    360Commerce 1.6         5/14/2007 6:08:34 PM   Sandy Gu
           update inclusive information in financial totals and history tables
      6    360Commerce 1.5         4/25/2007 10:01:14 AM  Anda D. Cadar   I18N
           merge
      5    360Commerce 1.4         1/25/2006 4:11:14 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:41:17 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/16/2005 16:28:09    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
     $
     Revision 1.8  2004/05/11 23:03:01  jdeleau
     @scr 4218 Backout recent changes to remove TransactionDiscounts,
     going to go a different route and remove the newly added
     voids and grosses instead.

     Revision 1.6  2004/04/09 16:55:44  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:22  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
     updating to pvcs 360store-current
 *
 *    Rev 1.0   Aug 29 2003 15:31:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:04   msg
 * Initial revision.
 *
 *    Rev 1.2   16 Apr 2002 13:16:32   sfl
 * Added gross value readings when read the POSDepartmentHistory table so that the
 * direct gross value can be read in to replace the original
 * reading the net value and then computing the gross value
 * approach.
 * Resolution for POS SCR-1579: Store gross figures in the DB (financials)
 *
 *    Rev 1.1   Mar 18 2002 22:47:20   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:33:32   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 15:58:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.DepartmentActivityIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Class that contains the database calls for reading departments.
 * 
 * @version $Revision: /main/17 $
 */
public class JdbcReadDepartment extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1539481804675459959L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadDepartment.class);

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadDepartment.execute()");

        /* getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        DepartmentIfc dept = (DepartmentIfc)action.getDataObject();
        String deptID = dept.getDepartmentID();
        if (deptID != null && deptID.length() > 0)
        {
            dept = selectDepartmentByDeptID(connection, dept.getDepartmentID(),new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
        }
        else
        {
            dept = selectDepartmentByName(connection, dept.getLocalizedDescriptions().getText());
        }

        /*
         * Send back the result
         */
        dataTransaction.setResult(dept);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadDepartment.execute()");
    }

    /**
     * Returns a department by its name.
     * 
     * @param dataConnection connection to the db
     * @param departmentName The name of the department to return
     * @return department
     * @exception DataException upon error
     * @deprecated As of release 13.1
     */
    public DepartmentIfc selectDepartmentByName(JdbcDataConnection dataConnection,
                                                String departmentName)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables
         */
        sql.addTable(TABLE_POS_DEPARTMENT, ALIAS_POS_DEPARTMENT);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_POS_DEPARTMENT_ID);
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME);
        sql.addColumn(FIELD_TAX_GROUP_ID);    // CR 27192

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(new SQLParameterValue(FIELD_POS_DEPARTMENT_NAME, departmentName));

        DepartmentIfc dept = instantiateDepartment();
        try
        {
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String deptID = getSafeString(rs, ++index);
                String deptName = getSafeString(rs, ++index);
                int taxGroupID = rs.getInt(++index);

                /*
                 * Fill in the department object
                 */
                dept.setDepartmentID(deptID);
                dept.setDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),deptName);
                dept.setTaxGroupID(taxGroupID);
            }
            else
            {
                throw new DataException(NO_DATA, "Department Not Found");
            }

            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectDepartmentByName", se);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectDepartmentByName", e);
        }

        return(dept);
    }

    /**
     * Returns a department by its ID.
     * 
     * @param dataConnection connection to the db
     * @param departmentName The ID of the department to return
     * @return department
     * @exception DataException upon error
     * @deprecated As of 13.1 Use selectDepartmentByDeptID
     */
    public DepartmentIfc selectDepartmentByID(JdbcDataConnection dataConnection,
                                              String departmentID)
        throws DataException
    {
    	return selectDepartmentByDeptID(dataConnection, departmentID, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }

    /**
     * Returns a department by its ID.
     * 
     * @param dataConnection connection to the db
     * @param departmentName The ID of the department to return
     * @param LocaleRequestor
     * @return department
     * @exception DataException upon error
     */
    public DepartmentIfc selectDepartmentByDeptID(JdbcDataConnection dataConnection,
                                              String departmentID, LocaleRequestor localeRequestor)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables
         */
        sql.addTable(TABLE_POS_DEPARTMENT, ALIAS_POS_DEPARTMENT);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_POS_DEPARTMENT_ID);
        sql.addColumn(FIELD_TAX_GROUP_ID);    // CR 27192

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(new SQLParameterValue(FIELD_POS_DEPARTMENT_ID, departmentID));
        DepartmentIfc dept = instantiateDepartment();
        try
        {
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String deptID = getSafeString(rs, ++index);
                int taxGroupID = rs.getInt(++index);

                /*
                 * Fill in the department object
                 */
                dept.setDepartmentID(deptID);
                dept.setTaxGroupID(taxGroupID);
            }
            else
            {
                throw new DataException(NO_DATA, "Department Not Found");
            }

            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectDepartmentByID", se);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectDepartmentByID", e);
        }
        readI8DeptDescription(dataConnection,dept,localeRequestor);
        return(dept);
    }

    /**
     * read the dept description from i18 table
     * 
     * @param connection
     * @param dept
     * @param localeRequestor
     * @throws DataException
     */
    protected void readI8DeptDescription(JdbcDataConnection connection,
    									DepartmentIfc dept,
    									LocaleRequestor localeRequestor) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // Table to select from
        sql.addTable(TABLE_POS_DEPARTMENT_I8);

        // add column
        sql.addColumn(FIELD_LOCALE);
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME);


        // add identifier qualifier
        sql.addQualifier(new SQLParameterValue(FIELD_POS_DEPARTMENT_ID, dept.getDepartmentID()));

        //  add qualifier for locale
        sql.addQualifier(FIELD_LOCALE + " " + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        try
        {
            // execute sql
            String sqlString = sql.getSQLString();
            connection.execute(sqlString, sql.getParameterValues());
            ResultSet rs = (ResultSet)connection.getResult();
            
            //set dept with new localizedDescriptions
            LocalizedTextIfc localizedDescriptions = DomainGateway.getFactory().getLocalizedText();
            localizedDescriptions.setDefaultLocale(localeRequestor.getDefaultLocale());
            dept.setLocalizedDescriptions(localizedDescriptions);
            
            // parse result set
            Locale locale = null;
            while (rs.next())
            {
                locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, 1));
                dept.setDescription(locale, getSafeString(rs, 2));
            }
            rs.close();
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "readI8DeptDescription");
            throw new DataException(DataException.SQL_ERROR, "readDeptDesc", se);
        }
        catch (DataException de)
        {
            // not found is regarded to be Ok here
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readDeptDesc", e);
        }

    }

    /**
     * Returns a list of department history records.
     * <p>
     * NOTE: All of the reporting periods must be of the same type.
     * 
     * @param dataConnection connection to the db
     * @param reportingPeriods The list of reporting periods to include
     * @return List of department totals
     * @exception DataException upon error
     * @deprecated As of release 13.1 use {@link #selectDepartmentHistory(JdbcDataConnection, ReportingPeriodIfc[], LocaleRequestor)}
     */
    public Vector<DepartmentActivityIfc> selectDepartmentHistory(JdbcDataConnection dataConnection,
                                          ReportingPeriodIfc[] reportingPeriods)
        throws DataException
    {
    	return selectDepartmentHistory(dataConnection, reportingPeriods, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }
    
    
    /**
     * Returns a list of department history records.
     * <p>
     * NOTE: All of the reporting periods must be of the same type.
     * 
     * @param dataConnection connection to the db
     * @param reportingPeriods The list of reporting periods to include
     * @param locales the locales of department names
     * @return List of department totals
     * @exception DataException upon error
     */
    public Vector<DepartmentActivityIfc> selectDepartmentHistory(JdbcDataConnection dataConnection,
                                          ReportingPeriodIfc[] reportingPeriods,
                                          LocaleRequestor locales)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_POS_DEPARTMENT_HISTORY, ALIAS_POS_DEPARTMENT_HISTORY);
        sql.addTable(TABLE_POS_DEPARTMENT, ALIAS_POS_DEPARTMENT);

        /*
         * Add desired columns
         */
        sql.addColumn(ALIAS_POS_DEPARTMENT + "." + FIELD_POS_DEPARTMENT_ID);

        // These are in the same order as Store History
        sql.addColumn(FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT);
        sql.addColumn(FIELD_POS_DEPT_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_MARKDOWN_COUNT);
        sql.addColumn(FIELD_POS_DEPT_MARKDOWN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT);
        sql.addColumn(FIELD_POS_DEPT_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_POST_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_POS_DEPT_POST_TRANSACTION_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_LINE_ITEM_VOID_COUNT);
        sql.addColumn(FIELD_POS_DEPT_LINE_ITEM_VOID_TOTAL_AMOUNT);

        // Additional fields not defined in ARTS
        sql.addColumn(FIELD_POS_DEPT_RETURN_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_NONTAXABLE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_POS_DEPT_RETURN_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_NONTAXABLE_RETURN_COUNT);
        sql.addColumn(FIELD_POS_DEPT_RETURN_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_RETURN_COUNT);
        sql.addColumn(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_COUNT);
        sql.addColumn(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_COUNT);
        sql.addColumn(FIELD_POS_DEPT_ITEM_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_ITEM_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_RESTOCKING_FEE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_POS_DEPT_TOTAL_RESTOCKING_FEE_COUNT);

        // Add Qualifier(s)

        // Join PosDepartment and PosDepartmentHistory
        sql.addJoinQualifier(ALIAS_POS_DEPARTMENT_HISTORY, FIELD_POS_DEPARTMENT_ID,
                ALIAS_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID);

        if (reportingPeriods.length > 0)
        {
            String fy = reportingPeriods[0].getFiscalYear();
            StringBuffer fyList = new StringBuffer("'" + fy + "'");
            StringBuffer idList = new StringBuffer();
            for (int i = 0; i < reportingPeriods.length; ++i)
            {
                if (!fy.equals(reportingPeriods[i].getFiscalYear()))
                {
                    fy = reportingPeriods[i].getFiscalYear();
                    fyList.append(", '" + fy + "'");
                }

                if (i > 0)
                {
                    idList.append(", ");
                }

                idList.append(reportingPeriods[i].getReportingPeriodID());
            }

            int type = reportingPeriods[0].getReportingPeriodType();
            sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, ReportingPeriodIfc.REPORTING_PERIOD_CODES[type]));
            sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " in (" + idList + ")");
            sql.addQualifier(FIELD_FISCAL_YEAR + " in (" + fyList + ")");
        }

        /*
         * Add Ordering(s)
         */
        // No special order needed.

        Vector<DepartmentActivityIfc> activityVector = new Vector<DepartmentActivityIfc>(4);
        try
        {
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String deptID = getSafeString(rs, ++index);
                int taxExemptTransactionCount = rs.getInt(++index);
                CurrencyIfc netTaxExemptAmount = getCurrencyFromDecimal(rs, ++index);
                int markdownCount = rs.getInt(++index);
                CurrencyIfc markdownAmount = getCurrencyFromDecimal(rs, ++index);
                int miscDiscountCount = rs.getInt(++index);
                CurrencyIfc miscDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                int postVoidCount = rs.getInt(++index);
                CurrencyIfc postVoidAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemVoidCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemVoidAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc returnTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc returnInclusiveTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemNontaxableCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemNontaxableReturnAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemNontaxableReturnCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemTaxExemptReturnAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemTaxExemptReturnCount = getBigDecimal(rs, ++index);
                CurrencyIfc grossTaxableItemSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableItemSalesCount = getBigDecimal(rs, ++index);
                CurrencyIfc grossTaxableItemReturnsAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableItemReturnsCount = getBigDecimal(rs, ++index);
                CurrencyIfc taxableItemSalesTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc taxableItemSalesInclusiveTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc restockingFeesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal restockingFeesCount = getBigDecimal(rs, ++index);

                /*
                 * Department
                 */
                DepartmentIfc dept = instantiateDepartment();
                dept.setDepartmentID(deptID);

                /*
                 * DepartmentActivity
                 */
                DepartmentActivityIfc activity = instantiateDepartmentActivity();
                activity.setDepartment(dept);

                /*
                 * FinancialTotals
                 */
                FinancialTotalsIfc totals = instantiateFinancialTotals();

                // Item Sales
                totals.setAmountGrossTaxableItemSales(grossTaxableItemSalesAmount);
                totals.setUnitsGrossTaxableItemSales(grossTaxableItemSalesCount);
                totals.setAmountGrossNonTaxableItemSales(itemNontaxableAmount);
                totals.setUnitsGrossNonTaxableItemSales(itemNontaxableCount);
                totals.setAmountGrossTaxExemptItemSales(netTaxExemptAmount);
                totals.setUnitsGrossTaxExemptItemSales(BigDecimal.valueOf(taxExemptTransactionCount));

                // Item Returns
                totals.setAmountGrossTaxableItemReturns(grossTaxableItemReturnsAmount);
                totals.setUnitsGrossTaxableItemReturns(grossTaxableItemReturnsCount);
                totals.setAmountGrossNonTaxableItemReturns(itemNontaxableReturnAmount);
                totals.setUnitsGrossNonTaxableItemReturns(itemNontaxableReturnCount);
                totals.setAmountGrossTaxExemptItemReturns(itemTaxExemptReturnAmount);
                totals.setUnitsGrossTaxExemptItemReturns(itemTaxExemptReturnCount);
                
                // Tax
                totals.setAmountTaxItemSales(taxableItemSalesTaxAmount);
                totals.setAmountInclusiveTaxItemSales(taxableItemSalesInclusiveTaxAmount);
                totals.setAmountTaxItemReturns(returnTaxAmount);
                totals.setAmountInclusiveTaxItemReturns(returnInclusiveTaxAmount);
                // Misc
                totals.setAmountTransactionDiscounts(miscDiscountAmount);
                totals.setNumberTransactionDiscounts(miscDiscountCount);
                totals.setAmountItemDiscounts(markdownAmount);
                totals.setNumberItemDiscounts(markdownCount);
                totals.setAmountPostVoids(postVoidAmount);
                totals.setNumberPostVoids(postVoidCount);
                totals.setAmountLineVoids(itemVoidAmount);
                totals.setUnitsLineVoids(itemVoidCount);
                totals.setAmountRestockingFees(restockingFeesAmount);
                totals.setUnitsRestockingFees(restockingFeesCount);

                activity.setTotals(totals);
                activityVector.addElement(activity);
            }

            if (activityVector.isEmpty())
            {
                throw new DataException(NO_DATA, "No Activity records");
            }

            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectDepartmentHistory", se);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectDepartmentHistory", e);
        }

        //fetch localized names for departments. 
        for (DepartmentActivityIfc deptActivity : activityVector) 
        {
        	readI8DeptDescription(dataConnection, deptActivity.getDepartment(), locales);	
		}
        
        return(activityVector);
    }

    /**
     * Instantiates a Department object.
     * 
     * @return new Department object
     */
    protected DepartmentIfc instantiateDepartment()
    {
        return (DomainGateway.getFactory().getDepartmentInstance());
    }

    /**
     * Instantiates a Department Activity object.
     * 
     * @return new Department Activity object
     */
    protected DepartmentActivityIfc instantiateDepartmentActivity()
    {
        return (DomainGateway.getFactory().getDepartmentActivityInstance());
    }

    /**
     * Instantiates a Financial Totals object.
     * 
     * @return new Financial Totals object
     */
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {
        return (DomainGateway.getFactory().getFinancialTotalsInstance());
    }
}
