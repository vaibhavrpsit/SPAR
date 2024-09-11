/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadReportingPeriod.java /main/15 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:44    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:45  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:50   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:00:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    Class that contains the database calls for reading
    reporting periods.
    <P>
    @version $Revision: /main/15 $
**/
public class JdbcReadReportingPeriod extends JdbcDataOperation
                                     implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadReportingPeriod.class);

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadReportingPeriod.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSReportingPeriod period = (ARTSReportingPeriod)action.getDataObject();
        ReportingPeriodIfc[] periods;
        periods = readReportingPeriod(connection,
                                      period.getStartDate(),
                                      period.getEndDate());

        /*
         * Send back the result
         */
        dataTransaction.setResult(periods);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadReportingPeriod.execute()");
    }

    /**
       Returns a list of business days between startDate and endDate
       inclusive.
       <p>
       @param  dataConnection  connection to the db
       @param  startDate       The beginning of the reporting period
       @param  endDate         The end of the reporting period
       @return the list of business days
       @exception DataException upon error
     */
    public ReportingPeriodIfc[] readReportingPeriod(JdbcDataConnection dataConnection,
                                                    EYSDate startDate,
                                                    EYSDate endDate)
        throws DataException
    {
        Vector periodVector = selectReportingPeriod(dataConnection, startDate, endDate);

        ReportingPeriodIfc[] periods = new ReportingPeriodIfc[periodVector.size()];
        periodVector.copyInto(periods);

        return(periods);
    }

    /**
       Reads a specific business day.  This method indicates
       whether or not the business day exists in the database.
       <p>
       @param  dataConnection  connection to the data source
       @param  businessDate    The business day to read
       @exception DataException upon error
    **/
        static public void selectBusinessDay(JdbcDataConnection dataConnection,
                                         EYSDate businessDate)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_FISCAL_WEEK_NUMBER);
        sql.addColumn(FIELD_FISCAL_DAY_NUMBER);
        sql.addColumn(FIELD_FISCAL_YEAR);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
                         + dateToSQLDateString(businessDate.dateValue()));

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                int fiscalWeekNumber = rs.getInt(++index);
                int fiscalDayNumber = rs.getInt(++index);
                String fiscalYear = getSafeString(rs, ++index);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "Business Day not found.");
            }

            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "Business Day table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Business Day table", e);
        }
    }

    /**
       Reads the Reporting Period table.
       <p>
       @param  dataConnection  connection to the db
       @param  startDate       The beginning of the reporting period
       @param  endDate         The end of the reporting period
       @return the list of business days
       @exception DataException upon error
     */
    public Vector selectReportingPeriod(JdbcDataConnection dataConnection,
                                        EYSDate startDate,
                                        EYSDate endDate)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID);
        sql.addColumn(ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addColumn(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);

        /*
         * Add Qualifier(s)
         */
        // Only business days
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + getReportingPeriodType(startDate));

        // between startDate and endDate, inclusive
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE
                         + " >= " + getBusinessDay(startDate));
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE
                         + " <= " + getBusinessDay(endDate));

        // join business day and reporting period tables
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_WEEK_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_DAY_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER);

        Vector periodVector = new Vector(2);
        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();
            while (rs.next())
            {
                int index = 0;
                int reportingPeriodID = rs.getInt(++index);
                String typeCode = getSafeString(rs, ++index);
                String fiscalYear = getSafeString(rs, ++index);

                /*
                 * Initialize Reporting Period object
                 */
                ReportingPeriodIfc period = instantiateReportingPeriod();
                period.setReportingPeriodID(String.valueOf(reportingPeriodID));
                period.setReportingPeriodType(getReportingPeriodType(typeCode));
                period.setFiscalYear(fiscalYear);

                periodVector.addElement(period);
            }
            if (periodVector.isEmpty())
            {
                throw new DataException(DataException.NO_DATA, "No Reporting Periods found.");
            }

            rs.close();
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "Reporting Period table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Reporting Period table", e);
        }

        return(periodVector);
    }

    /**
       Returns the reporting period type.
       <p>
       @param  typeCode    the reporting period type code
       @return the reporting period type
     */
    protected int getReportingPeriodType(String typeCode)
    {
        int code = ReportingPeriodIfc.TYPE_UNKNOWN;
        for (int i = 0; i < ReportingPeriodIfc.REPORTING_PERIOD_CODES.length; ++i)
        {
            if (ReportingPeriodIfc.REPORTING_PERIOD_CODES[i].equals(typeCode))
            {
                code = i;
                break;
            }
        }
        return(code);
    }

    /**
       Returns the reporting period type.
       <p>
       @param  businessDate     the business date
       @return the reporting period type
     */
    protected String getReportingPeriodType(EYSDate businessDate)
    {
        int code = ReportingPeriodIfc.TYPE_BUSINESS_DAY;
        return("'" + ReportingPeriodIfc.REPORTING_PERIOD_CODES[code] + "'");
    }

    /**
       Returns the reporting period id.
       <p>
       @param  businessDate     the business date
       @return the reporting period id
     */
    protected String getReportingPeriodID(EYSDate businessDate)
    {
        Calendar c = businessDate.calendarValue();
        return(String.valueOf(c.get(Calendar.DAY_OF_YEAR)));
    }

    /**
       Returns the fiscal year.  Assumes fiscal year and calendar year
       are the same.
       <p>
       @param  businessDate     the business date
       @return the fiscal year
     */
    protected String getFiscalYear(EYSDate businessDate)
    {
        return("'" + businessDate.getYear() + "'");
    }

    /**
       Returns the string representation of the business day
       <p>
       @param  businessDate     the business day
       @return the business day
     */
    protected String getBusinessDay(EYSDate businessDate)
    {
        return(dateToSQLDateString(businessDate.dateValue()));
    }

    /**
       Instantiates a Reporting Period object.
       <p>
       @return new Reporting Period object
     */
    protected ReportingPeriodIfc instantiateReportingPeriod()
    {
        return(DomainGateway.getFactory().getReportingPeriodInstance());
    }
}
