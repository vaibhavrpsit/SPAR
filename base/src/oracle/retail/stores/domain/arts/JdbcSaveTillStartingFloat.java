/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTillStartingFloat.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
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
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/24/2007 5:53:22 PM   Ashok.Mondal
 *         Update code to store currencyID in tender history table.
 *    5    360Commerce 1.4         4/25/2007 10:01:09 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:25 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:03    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.8  2004/07/30 21:05:23  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *
 *   Revision 1.7  2004/06/18 22:56:43  cdb
 *   @scr 4205 Corrected problems caused by searching financial counts
 *   by tender description rather than tender descriptor - which caused problems
 *   with foreign currencies.
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   May 22 2003 08:13:36   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    This operation saves the starting float for till in the
    workstation history tender table.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class JdbcSaveTillStartingFloat
extends JdbcSaveReportingPeriod
implements ARTSDatabaseIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 7241431811142754336L;
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveTillStartingFloat.class);
    /**
       revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Tender type map
     */
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTillStartingFloat.execute()");


        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be updated
        // in the database.
        RegisterIfc register = (RegisterIfc)action.getDataObject();

        saveTillStartingFloat(connection, register);


        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTillStartingFloat.execute()");
    }
    /**
        Inserts a record in the workstation tender history table.
        <P>
        @param  dataConnection  connection to the db
        @param  tenderItem      FinancialCountTenderItemIfc the type of tender
        @param  register        the register information

        @exception DataException upon error
     */
    public boolean insertStartingFloatWorkstationHistory(JdbcDataConnection dataConnection,
                                                 FinancialCountTenderItemIfc tenderItem,
                                                 RegisterIfc register
                                                 //String openAmount,
                                                 //String shortAmount,
                                                // String overAmount
                                                )
                                                 throws DataException
    {
        boolean returnCode = false;
        FinancialTotalsIfc totals = register.getTotals();
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLInsertStatement sql = new SQLInsertStatement();
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_TENDER_HISTORY);
        /*
         * Add columns and their values
         */
        EYSDate rp = getReportingPeriod(register);
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
        sql.addColumn(FIELD_FISCAL_YEAR, getFiscalYear(register.getBusinessDate()));
        sql.addColumn(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodType(rp));
        sql.addColumn(FIELD_REPORTING_PERIOD_ID, getReportingPeriodID(rp));
        sql.addColumn(FIELD_TENDER_TYPE_CODE, inQuotes(tenderType));
        sql.addColumn(FIELD_TENDER_SUBTYPE, inQuotes(emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE, inQuotes(tenderItem.getCurrencyCode()));
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, tenderItem.getCurrencyID());
        //-I18N
        sql.addColumn(FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT,
                      safeSQLCast(getTenderOverAmount(totals, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      safeSQLCast(getTenderOverCount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT,
                      safeSQLCast(getTenderShortAmount(totals, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      getTenderShortCount(totals, tenderDescriptor));

        sql.addColumn(FIELD_WORKSTATION_TENDER_OPEN_AMOUNT,
                      safeSQLCast(getTenderOpenAmount(totals, tenderDescriptor)));

        //sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT,
        //              safeSQLCast(getTenderCount(totals, tenderDesc)));
        //sql.addColumn(FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT,
        //              safeSQLCast(getTenderTotalAmount(totals, tenderDesc)));

        sql.addColumn(FIELD_WORKSTATION_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderBeginningCount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return(returnCode);
    }

    /**
        Adds to a record in the workstation history table.
        <P>
        @param  dataConnection  connection to the db
        @param  register        the register information
        @exception DataException upon error
     */
    public void saveTillStartingFloat(JdbcDataConnection dataConnection,
                                      RegisterIfc register)
                                      throws DataException

   {

       FinancialTotalsIfc totals = register.getTotals();
      // System.out.println("Totals " + totals);
       FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(totals.getCombinedCount());


       for (int i = 0; i < tenderTypes.length; ++i)
       {


          if (!addStartingFloatWorkstationHistory(dataConnection, tenderTypes[i], register))
          {
                insertStartingFloatWorkstationHistory(dataConnection,
                                                       tenderTypes[i],
                                                       register);

          }

          if (!addStartingFloatStoreHistory(dataConnection,  tenderTypes[i], register))
          {
              insertStartingFloatStoreHistory(dataConnection,
                                              tenderTypes[i],
                                              register);

          }
       }
    }


    /**
        Adds to a record in the workstation tender history table.
        <P>
        @param  dataConnection  connection to the db
        @param  tenderItem      the type of tender
        @param  register        the register information

        @exception DataException upon error
     */
    public boolean addStartingFloatWorkstationHistory(JdbcDataConnection dataConnection,
                                                      FinancialCountTenderItemIfc tenderItem,
                                                      RegisterIfc register)
                                                      throws DataException
    {
        boolean returnCode = false;
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        FinancialTotalsIfc totals = register.getTotals();
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLUpdateStatement sql = new SQLUpdateStatement();
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_TENDER_HISTORY);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderOverAmount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderShortAmount(totals, tenderDescriptor)));

       sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT + "+" + safeSQLCast(getTenderOverCount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT + "+" + safeSQLCast(getTenderShortCount(totals, tenderDescriptor)));


        sql.addColumn(FIELD_WORKSTATION_TENDER_OPEN_AMOUNT,
                      FIELD_WORKSTATION_TENDER_OPEN_AMOUNT + "+" + safeSQLCast(getTenderOpenAmount(totals, tenderDescriptor)));

        //sql.addColumn(FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT,
        //              FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderTotalAmount(totals, tenderDesc)));

        //sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT,
        //              FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT + "+" + safeSQLCast(getTenderCount(totals, tenderDesc)));

        sql.addColumn(FIELD_WORKSTATION_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderBeginningCount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        /*
         * Add Qualifiers
         */
        EYSDate rp = getReportingPeriod(register);
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        sql.addQualifier(FIELD_FISCAL_YEAR + " = " + getFiscalYear(register.getBusinessDate()));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE + " = " + getReportingPeriodType(rp));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " = " + getReportingPeriodID(rp));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + inQuotes(tenderType));
        sql.addQualifier(FIELD_TENDER_SUBTYPE + " = " + inQuotes(emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " + inQuotes(tenderItem.getCurrencyCode()));

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode=true;
        }
        return returnCode;
    }


         /**
        Inserts the financial tender totals into the store tender history
        table.
        <P>
        @param  dataConnection  connection to the db
        @param  register          The register
        @param  tenderItem      The type item
        @exception DataException thrown when an error occurs.
     */
    public boolean insertStartingFloatStoreHistory(JdbcDataConnection dataConnection,
                                                   FinancialCountTenderItemIfc tenderItem,
                                                   RegisterIfc register)
                                         throws DataException
    {
        boolean returnCode = false;

        FinancialTotalsIfc totals = register.getTotals();
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLInsertStatement sql = new SQLInsertStatement();
        /*
         * Define the table
         */
        sql.setTable(TABLE_STORE_TENDER_HISTORY);
        /*
         * Add Columns
         */
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
        sql.addColumn(FIELD_FISCAL_YEAR, getFiscalYear(register.getBusinessDate()));
        sql.addColumn(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodType(register.getBusinessDate()));
        sql.addColumn(FIELD_REPORTING_PERIOD_ID, getReportingPeriodID(register.getBusinessDate()));
        sql.addColumn(FIELD_TENDER_TYPE_CODE, inQuotes(tenderType));
        sql.addColumn(FIELD_TENDER_SUBTYPE, inQuotes(emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE, inQuotes(tenderItem.getCurrencyCode()));
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, tenderItem.getCurrencyID());
        //-I18N
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        /*
         * Add tender columns and their values
         */
        sql.addColumn(FIELD_STORE_TENDER_OVER_TOTAL_AMOUNT,
                      safeSQLCast(getTenderOverAmount(totals,tenderDescriptor)));

        sql.addColumn(FIELD_STORE_TENDER_SHORT_TOTAL_AMOUNT,
                      safeSQLCast(getTenderShortAmount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_STORE_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderBeginningCount(totals, tenderDescriptor)));

       //sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_COUNT,
                     // safeSQLCast(getTenderCount(totals, tenderDesc)));
                    // safeSQLCast(getTenderBeginningCount(totals, tenderDesc)));

        sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      safeSQLCast(getTenderOverCount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      safeSQLCast(getTenderShortCount(totals, tenderDescriptor)));


       //sql.addColumn(FIELD_STORE_TENDER_TOTAL_AMOUNT,
                      //safeSQLCast(getTenderTotalAmount(totals, tenderDesc)));
                     // safeSQLCast(getTenderOpenAmount(totals, tenderDesc)));

        sql.addColumn(FIELD_STORE_TENDER_OPEN_AMOUNT,
                      safeSQLCast(getTenderOpenAmount(totals, tenderDescriptor)));

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return(returnCode);

    }



    /**
        Adds the financial tender totals to the store tender history records.
        <P>
        @param  dataConnection  connection to the db
        @param  tenderItem      The tender item
        @param  register          The register
        @exception DataException thrown when an error occurs.
     */
    public boolean addStartingFloatStoreHistory(JdbcDataConnection dataConnection,
                                                FinancialCountTenderItemIfc tenderItem,
                                                RegisterIfc register)
                                      throws DataException
    {
        boolean returnCode = false;
        FinancialTotalsIfc totals = register.getTotals();

        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLUpdateStatement sql = new SQLUpdateStatement();
        /*
         * Define the table
         */
        sql.setTable(TABLE_STORE_TENDER_HISTORY);
        /*
         * Add columns and their values
         */

        sql.addColumn(FIELD_STORE_TENDER_OVER_TOTAL_AMOUNT,
                      FIELD_STORE_TENDER_OVER_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderOverAmount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_STORE_TENDER_SHORT_TOTAL_AMOUNT,
                      FIELD_STORE_TENDER_SHORT_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderShortAmount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_STORE_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      FIELD_STORE_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT + "+" + safeSQLCast(getTenderBeginningCount(totals, tenderDescriptor)));

       // sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_COUNT,
       //               FIELD_STORE_TOTAL_TENDER_MEDIA_COUNT + "+" + safeSQLCast(getTenderCount(totals, tenderDesc)));

        sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      FIELD_STORE_TOTAL_TENDER_MEDIA_OVER_COUNT + "+" + safeSQLCast(getTenderOverCount(totals, tenderDescriptor)));

        sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      FIELD_STORE_TOTAL_TENDER_MEDIA_SHORT_COUNT + "+" + safeSQLCast(getTenderShortCount(totals, tenderDescriptor)));

        //sql.addColumn(FIELD_STORE_TENDER_TOTAL_AMOUNT,
        //              FIELD_STORE_TENDER_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderTotalAmount(totals, tenderDesc)));

        sql.addColumn(FIELD_STORE_TENDER_OPEN_AMOUNT,
                      FIELD_STORE_TENDER_OPEN_AMOUNT + "+" + safeSQLCast(getTenderOpenAmount(totals, tenderDescriptor)));



        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_FISCAL_YEAR + " = " + getFiscalYear(register.getBusinessDate()));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE + " = " + getReportingPeriodType(register.getBusinessDate()));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " = " + getReportingPeriodID(register.getBusinessDate()));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + inQuotes(tenderType));
        sql.addQualifier(FIELD_TENDER_SUBTYPE + " = " + inQuotes(emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " + inQuotes(tenderItem.getCurrencyCode()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return returnCode;
    }

    /**
        Returns the reporting period
        <p>
        @param  register  The register object
        @return the reporting period
     */
    protected EYSDate getReportingPeriod(RegisterIfc register)
    {
        /*
         * Use the business date for store level stuff
         */
        return(register.getBusinessDate());
    }

    /**
        Returns the store id
        <p>
        @param  register  A register
        @return the store id
     */
    protected String getStoreID(RegisterIfc register)
    {
        return("'" + register.getWorkstation().getStoreID() + "'");
    }

    /**
        Returns the workstation id
        <p>
        @param  register  A register
        @return the workstation id
     */
    protected String getWorkstationID(RegisterIfc register)
    {
        return("'" + register.getWorkstation().getWorkstationID() + "'");
    }

    /**
        Returns the string representation of the start time
        <p>
        @param  register  A register
        @return the string representation of the start time
     */
    protected String getStartTimestamp(RegisterIfc register)
    {
        return(dateToSQLTimestampString(register.getOpenTime().dateValue()));
    }


    /**
        Returns the tender type
        <p>
        @param  tenderType  The type of tender
        @return the tender type
     */
    protected String getTenderType(String tenderType)
    {
        String value = tenderTypeMap.getCode(tenderTypeMap.getTypeFromDescriptor(tenderType));

        return("'" + value + "'");
    }

    /**
     Returns the tender over amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender over amount
     */
    protected String getTenderOverAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amount;

        if (expectedTender != null && enteredTender != null)
        {
            amount = enteredTender.getAmountTotal().subtract(expectedTender.getAmountTotal());
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                amount = expectedTender.getAmountTotal().negate();
            }
            else // both are null
            {
                amount = zero;
            }
        }
        else // expectedTender == null
        {
            amount = enteredTender.getAmountTotal();
        }

        if (amount.compareTo(zero) == CurrencyIfc.LESS_THAN)
        {
            amount = zero;
        }

        return(amount.getStringValue());

    }

    /**
     Returns the tender short amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender short amount
     */
    protected String getTenderShortAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amount;

        if (expectedTender != null && enteredTender != null)
        {
            amount = enteredTender.getAmountTotal().subtract(expectedTender.getAmountTotal());
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                amount = expectedTender.getAmountTotal().negate();
            }
            else // both are null
            {
                amount = zero;
            }
        }
        else // expectedTender == null
        {
            amount = enteredTender.getAmountTotal();
        }

        if (amount.compareTo(zero) == CurrencyIfc.LESS_THAN)
        {
            amount = amount.negate();
        }
        else
        {
            amount = zero;
        }

        return(amount.getStringValue());

    }

    /**
     Returns the tender count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender count
     */
    protected String getTenderCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = totals.getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsTotal());
        }

        return(value);
    }

    /**
     Returns the tender over count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender over count
     */
    protected String getTenderOverCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        int count;

        if (expectedTender != null && enteredTender != null)
        {
            count = enteredTender.getNumberItemsTotal() - expectedTender.getNumberItemsTotal();
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                count = - expectedTender.getNumberItemsTotal();
            }
            else // both are null
            {
                count = 0;
            }
        }
        else // expectedTender == null
        {
            count = enteredTender.getNumberItemsTotal();
        }

        if (count < 0)
        {
            count = 0;
        }

        return(String.valueOf(count));
    }

    /**
     Returns the tender short count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender short count
     */
    protected String getTenderShortCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        int count;

        if (expectedTender != null && enteredTender != null)
        {
            count = enteredTender.getNumberItemsTotal() - expectedTender.getNumberItemsTotal();
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                count = - expectedTender.getNumberItemsTotal();
            }
            else // both are null
            {
                count = 0;
            }
        }
        else // expectedTender == null
        {
            count = enteredTender.getNumberItemsTotal();
        }

        if (count < 0)
        {
            count = - count;
        }
        else
        {
            count = 0;
        }

        return(String.valueOf(count));
    }

    /**
     Returns the tender total amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender total amount
     */
    protected String getTenderTotalAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = totals.getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountTotal().getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender open amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender open amount
     */
    protected String getTenderOpenAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = totals.getStartingFloatCount().getEntered();

        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountIn().getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender beginning count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender beginning count
     */
    protected String getTenderBeginningCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = totals.getStartingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn());
        }

        return(value);
    }
}
