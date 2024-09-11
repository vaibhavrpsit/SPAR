/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveReportingPeriod.java /main/14 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/06/12 - prevent npe during businessDate conversion
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
 *    5    360Commerce 1.4         2/15/2008 4:50:58 PM   Owen D. Horne   CR
 *         29842:  Fiscal week number is now based on week 1 starting  January
 *          1st;  instead of using c.get(Calendar.WEEK_OF_YEAR), which reports
 *          some of the last days of the year as being part of week 1 (of the
 *         following year).  Reviewed by Michael Barnett.
 *    4    360Commerce 1.3         1/25/2006 4:11:23 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:21    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
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
 *    Rev 1.0   Aug 29 2003 15:32:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:48:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:24   msg
 * Initial revision.
 *
 *    Rev 1.4   14 Jan 2002 16:55:24   pdd
 * Added use of isSummary() in getTenderTypes().
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.3   08 Jan 2002 15:05:34   pdd
 * Restored getTenderTypes().
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.2   07 Jan 2002 10:47:28   pdd
 * Temporary move of getTenderTypes().
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.1   04 Jan 2002 10:34:02   pdd
 * Added getTenderTypes().
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.0   Sep 20 2001 15:57:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.ArrayList;
import java.util.Calendar;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

/**
 * This operation performs inserts into the business day, and reporting period
 * tables.
 * 
 * @version $Revision: /main/14 $
 */
public abstract class JdbcSaveReportingPeriod extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -7279019305196359919L;
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcSaveReportingPeriod.class);

    /**
     * Class constructor.
     */
    public JdbcSaveReportingPeriod()
    {
        setName("JdbcSaveReportingPeriod");
    }

    /**
     * Creates the records needed for a new business day.
     * 
     * @param dataConnection connection to the db
     * @param businessDate the business date
     * @exception DataException upon error
     */
    public void createBusinessDay(JdbcDataConnection dataConnection, EYSDate businessDate) throws DataException
    {
        try
        {
            /*
             * See if the business day already exists before trying to add it.
             */
            JdbcReadReportingPeriod.selectBusinessDay(dataConnection, businessDate);
        }
        catch (DataException e)
        {
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                /*
                 * Business Day has not been created yet
                 */
                insertReportingPeriod(dataConnection, businessDate);
                insertBusinessDay(dataConnection, businessDate);
            }
            else
            {
                throw e;
            }
        }
    }

    /**
     * Adds a reporting period.
     * 
     * @param dataConnection connection to the db
     * @param businessDate the business date
     * @exception DataException upon error
     */
    public void insertReportingPeriod(JdbcDataConnection dataConnection, EYSDate businessDate) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        sql.setTable(TABLE_REPORTING_PERIOD);

        sql.addColumn(FIELD_FISCAL_YEAR, getFiscalYear(businessDate));
        sql.addColumn(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodType(businessDate));
        sql.addColumn(FIELD_REPORTING_PERIOD_ID, getReportingPeriodID(businessDate));
        sql.addColumn(FIELD_FISCAL_DAY_NUMBER, getFiscalWeekDay(businessDate));
        sql.addColumn(FIELD_FISCAL_WEEK_NUMBER, getFiscalWeek(businessDate));
        sql.addColumn(FIELD_FISCAL_MONTH, getFiscalMonth(businessDate));
        sql.addColumn(FIELD_FISCAL_QUARTER_ID, getFiscalQuarter(businessDate));
        sql.addColumn(FIELD_FISCAL_PERIOD_NAME, getReportingPeriodName(businessDate));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "Reporting Period", e);
        }
    }

    /**
     * Adds a business day record.
     * 
     * @param dataConnection connection to the db
     * @param businessDate the business day
     * @return true if successful
     * @exception DataException upon error
     */
    public void insertBusinessDay(JdbcDataConnection dataConnection, EYSDate businessDate) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        sql.setTable(TABLE_BUSINESS_DAY);

        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(businessDate));
        sql.addColumn(FIELD_FISCAL_YEAR, getFiscalYear(businessDate));
        sql.addColumn(FIELD_FISCAL_DAY_NUMBER, getFiscalWeekDay(businessDate));
        sql.addColumn(FIELD_FISCAL_WEEK_NUMBER, getFiscalWeek(businessDate));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "Business Day", e);
        }
    }

    /**
     * Returns the fiscal year. Assumes fiscal year and calendar year are the
     * same.
     * 
     * @param businessDate the business date
     * @return the fiscal year
     */
    protected String getFiscalYear(EYSDate businessDate)
    {
        return ("'" + businessDate.getYear() + "'");
    }

    /**
     * Returns the reporting period type.
     * 
     * @param businessDate the business date
     * @return the reporting period type
     */
    protected String getReportingPeriodType(EYSDate businessDate)
    {
        int code = ReportingPeriodIfc.TYPE_BUSINESS_DAY;
        return ("'" + ReportingPeriodIfc.REPORTING_PERIOD_CODES[code] + "'");
    }

    /**
     * Returns the reporting period id.
     * 
     * @param businessDate the business date
     * @return the reporting period id
     */
    protected String getReportingPeriodID(EYSDate businessDate)
    {
        Calendar c = businessDate.calendarValue();
        return (String.valueOf(c.get(Calendar.DAY_OF_YEAR)));
    }

    /**
     * Returns the fiscal week day.
     * 
     * @param businessDate the business date
     * @return the fiscal week day
     */
    protected String getFiscalWeekDay(EYSDate businessDate)
    {
        Calendar c = businessDate.calendarValue();
        return ("'" + c.get(Calendar.DAY_OF_WEEK) + "'");
    }

    /**
     * Returns the fiscal week. Assumes fiscal year and calendar year are the
     * same.
     * 
     * @param businessDate the business date
     * @return the fiscal week
     */
    protected String getFiscalWeek(EYSDate businessDate)
    {
        return ("'" + businessDate.weekOfYear() + "'");
    }

    /**
     * Returns the fiscal month. Assumes fiscal year and calendar year are the
     * same.
     * 
     * @param businessDate the business date
     * @return the fiscal month
     */
    protected String getFiscalMonth(EYSDate businessDate)
    {
        return ("'" + businessDate.getMonth() + "'");
    }

    /**
     * Returns the fiscal quarter. Assumes fiscal year and calendar year are the
     * same.
     * 
     * @param businessDate the business date
     * @return the fiscal quarter
     */
    protected String getFiscalQuarter(EYSDate businessDate)
    {
        int quarter = -1;

        int month = businessDate.getMonth();
        if (month < 3)
        {
            quarter = 0;
        }
        else if (month < 6)
        {
            quarter = 1;
        }
        else if (month < 9)
        {
            quarter = 2;
        }
        else
        {
            quarter = 3;
        }

        return ("'" + quarter + "'");
    }

    /**
     * Returns the string representation of the business day
     * 
     * @param businessDate the business day
     * @return the business day
     */
    protected String getBusinessDay(EYSDate businessDate)
    {
        return (businessDate != null)? dateToSQLDateString(businessDate.dateValue()) : null;
    }

    /**
     * Returns the name of the reporting period
     * 
     * @param businessDate the business day
     * @return the name of the reporting period
     */
    protected String getReportingPeriodName(EYSDate businessDate)
    {
        int code = ReportingPeriodIfc.TYPE_BUSINESS_DAY;
        return ("'" + ReportingPeriodIfc.REPORTING_PERIOD_DESCRIPTORS[code] + "'");
    }

    /**
     * Creates a list of the tenders that exist in both expected and entered
     * counts.
     * 
     * @param combined contains the lists of entered and expected tender items
     * @return FinancialCountTenderItemIfc[]
     */
    protected FinancialCountTenderItemIfc[] getTenderTypes(ReconcilableCountIfc combined)
    {
        // Get the lists of entered and expected tender items.
        ArrayList<FinancialCountTenderItemIfc> list = new ArrayList<FinancialCountTenderItemIfc>();
        FinancialCountTenderItemIfc[] expected = combined.getExpected().getTenderItems();
        FinancialCountTenderItemIfc[] entered = combined.getEntered().getTenderItems();

        // Add all the summary expected tender items to the list.
        if (expected != null)
        {
            for (int i = 0; i < expected.length; ++i)
            {
                if (expected[i].isSummary())
                {
                    list.add(expected[i]);
                }
            }
        }

        // Add all the summary, entered tender items to the list
        // which were not already in the expected list.
        if (entered != null)
        {
            for (int i = 0; i < entered.length; ++i)
            {
                if (entered[i].isSummary() && !listContainsItem(list, entered[i]))
                {
                    list.add(entered[i]);
                }
            }
        }

        FinancialCountTenderItemIfc[] tenderTypes = new FinancialCountTenderItemIfc[list.size()];
        list.toArray(tenderTypes);
        return tenderTypes;
    }

    /**
     * Determines if the list contains a match for the item based on tender type
     * and currency code.
     * 
     * @param list ArrayList of FinancialCountTenderItemIfc
     * @param item FinancialCountTenderItemIfc
     * @return boolean true if the item has a match in the list
     */
    protected boolean listContainsItem(ArrayList<FinancialCountTenderItemIfc> list, FinancialCountTenderItemIfc item)
    {
        boolean contains = false;
        FinancialCountTenderItemIfc listItem = null;

        for (int i = 0; i < list.size(); i++)
        {
            listItem = list.get(i);

            if (listItem.getTenderDescriptor().equals(item.getTenderDescriptor()))
            {
                contains = true;
                i = list.size();
            }
        }
        return contains;
    }
}
