/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateStoreSafeTotals.java /main/14 2013/10/28 09:04:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    yiqzhao   03/08/10 - closeFundAmount can be negtive.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 8    360Commerce 1.7         5/30/2007 3:33:54 PM   Ashok.Mondal    Insert
 *      currencyID to store safe tender history table.
 * 7    360Commerce 1.6         4/25/2007 10:01:08 AM  Anda D. Cadar   I18N
 *      merge
 * 6    360Commerce 1.5         8/23/2006 2:49:49 PM   Brett J. Larsen CR 20740
 *       - oracle db port - changing date formatting to use db helper class
 * 5    360Commerce 1.4         4/24/2006 5:51:32 PM   Charles D. Baker Merge
 *      of NEP62
 * 4    360Commerce 1.3         1/25/2006 4:11:27 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse
 *:
 * 4    .v700     1.2.3.0     11/16/2005 16:28:14    Jason L. DeLeau 4215: Get
 *      rid of redundant ArtsDatabaseifc class
 * 3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:22:53     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *Revision 1.8  2004/08/02 18:14:04  dcobb
 *@scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *Code cleanup.
 *
 *Revision 1.7  2004/07/23 16:29:05  dcobb
 *@scr 6433 Till Loan not updating store safe tender history.
 *
 *Revision 1.6  2004/03/12 04:27:32  rdunsmore
 *changed business day from date to string
 *
 *Revision 1.5  2004/02/17 17:57:36  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.4  2004/02/17 16:18:46  rhafernik
 *@scr 0 log4j conversion
 *
 *Revision 1.3  2004/02/12 17:13:19  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 23:25:23  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Feb 15 2003 17:26:06   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.2   Sep 03 2002 15:43:20   baa
 * Externalize domain constants
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Jun 10 2002 11:14:58   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.StoreSafe;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts/updates into the store safe tender history
 * 
 * @version $Revision: /main/14 $;
 */
public class JdbcUpdateStoreSafeTotals extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -2072501093725811202L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateStoreSafeTotals.class);

    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    /**
     * Class constructor.
     */
    public JdbcUpdateStoreSafeTotals()
    {
        super();
        setName("JdbcUpdateStoreSafeTotals");
    }

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateStoreSafeTotals.execute() starts");


        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        StoreSafe safe = (StoreSafe)action.getDataObject();

        updateStoreSafeTotals(connection, safe);

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateStoreSafeTotals.execute()");
    }

    /**
     * Updates the store tender history table.
     * 
     * @param dataConnection connection to the db
     * @param safe The safe information
     * @exception DataException thrown when an error occurs.
     */
    public void updateStoreSafeTotals(JdbcDataConnection dataConnection,StoreSafeIfc safe)
    throws DataException
    {
        /*
         * Walk through each tender item and save it to the Store safe tender History table
         */
        TenderDescriptorIfc[] tenderTypes = safe.getValidTenderDescList();
        for (int i = 0; i < tenderTypes.length; ++i)
        {
           updateStoreSafeTenderHistory(dataConnection, safe, tenderTypes[i]);
        }
    }

    /**
     * Inserts the financial tender totals into the store tender history table.
     * 
     * @param dataConnection connection to the db
     * @param safe The safe information
     * @param tenderType The type of tender
     * @exception DataException thrown when an error occurs.
     */
    public void updateStoreSafeTenderHistory(JdbcDataConnection dataConnection,
                                             StoreSafeIfc safe,
                                             TenderDescriptorIfc tenderType)
    throws DataException
    {
        boolean updateRow = false;

        String countryCode  = tenderType.getCountryCode();
        String tenderDesc = tenderTypeMap.getDescriptor(tenderType.getTenderType());

        if (!countryCode.equals(DomainGateway.getBaseCurrencyType().getCountryCode()))
        {
           tenderDesc = DomainGateway.getAlternateCurrencyInstance(countryCode).getCurrencyCode() + "_" +  tenderDesc;
        }
        String tenderTypeCode = tenderTypeMap.getCode(tenderType.getTenderType());


        // starting float at till open
        CurrencyIfc openFloatAmount = DomainGateway.getBaseCurrencyInstance();
        // ending float at till close
        CurrencyIfc closeFloatAmount = DomainGateway.getBaseCurrencyInstance();
        // closing funds at till close
        CurrencyIfc closeFundAmount = DomainGateway.getBaseCurrencyInstance();
        // open float at store open
        CurrencyIfc openOperationalAmount = DomainGateway.getBaseCurrencyInstance();
        // close float at store close
        CurrencyIfc closeOperationalAmount = DomainGateway.getBaseCurrencyInstance();
        // till pickup amount
        CurrencyIfc pickupAmount = DomainGateway.getBaseCurrencyInstance();
        // till loan amount
        CurrencyIfc loanAmount     = DomainGateway.getBaseCurrencyInstance();
        // amount for deposit
        CurrencyIfc depositAmount = DomainGateway.getBaseCurrencyInstance();

        // get counts from safe
        FinancialCountTenderItemIfc tenderItem =
            safe.getDepositCounts().getTenderItem(tenderType,false);

        if (tenderItem != null)
        {
            updateRow = true;
            depositAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = safe.getLoanCounts().getSummaryTenderItemByDescriptor(tenderType);

        if (tenderItem != null)
        {
            updateRow = true;
            loanAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = safe.getPickupCounts().getSummaryTenderItemByDescriptor(tenderType);

        if (tenderItem != null)
        {
            updateRow = true;
            pickupAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = safe.getOpenTillCounts().getSummaryTenderItemByDescriptor(tenderType);

        if (tenderItem != null)
        {
            updateRow = true;
            openFloatAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = safe.getCloseTillCounts().getSummaryTenderItemByDescriptor(tenderType);

        if (tenderItem != null)
        {
            updateRow = true;
            closeFloatAmount = tenderItem.getAmountOut().abs();
            closeFundAmount = tenderItem.getAmountIn();

        }

        tenderItem = safe.getOpenOperatingFunds().getSummaryTenderItemByDescriptor(tenderType);

        if (tenderItem != null)
        {
            updateRow = true;
            openOperationalAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = safe.getCloseOperatingFunds().getSummaryTenderItemByDescriptor(tenderType);

        if (tenderItem != null)
        {
            updateRow = true;
            closeOperationalAmount = tenderItem.getAmountTotal().abs();
        }


        // Write data if there is any
        if (updateRow)
        {
              SQLUpdateStatement sql = new SQLUpdateStatement();
              sql.setTable(TABLE_STORE_SAFE_TENDER_HISTORY);

              sql.addColumn(FIELD_RETAIL_STORE_ID,
                  JdbcDataOperation.makeSafeString(safe.getStoreID()));
              sql.addColumn(FIELD_TENDER_REPOSITORY_ID,
                  JdbcDataOperation.makeSafeString(safe.getStoreSafeID()));
              sql.addColumn(FIELD_TENDER_TYPE_CODE,
                  JdbcDataOperation.makeSafeString(tenderTypeCode));
              sql.addColumn(FIELD_BUSINESS_DAY_DATE,
                  JdbcDataOperation.dateToSQLDateString(safe.getBusinessDay().dateValue())); // from JdbcDataOperations
              //+I18N
              sql.addColumn(FIELD_CURRENCY_ID, tenderType.getCurrencyID());
              //-I18N

              switch(safe.getOperationType())
              {
                case StoreSafeIfc.OPEN:
                {
                    sql.addColumn(FIELD_STORE_SAFE_TENDER_TILL_OPEN_FLOAT_TOTAL_AMOUNT,
                                  FIELD_STORE_SAFE_TENDER_TILL_OPEN_FLOAT_TOTAL_AMOUNT + "+" + safeSQLCast(openFloatAmount.getStringValue()));
                    break;
                }
                case StoreSafeIfc.CLOSE:
                {
                    sql.addColumn(FIELD_STORE_SAFE_TENDER_TILL_CLOSE_FLOAT_TOTAL_AMOUNT,
                                  FIELD_STORE_SAFE_TENDER_TILL_CLOSE_FLOAT_TOTAL_AMOUNT + "+" + safeSQLCast(closeFloatAmount.getStringValue()));

                    sql.addColumn(FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT,
                                  FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT + "+" + safeSQLCast(closeFundAmount.getStringValue()));

                    break;
                }
                case StoreSafeIfc.LOAN:
                {
                    sql.addColumn(FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT,
                                  FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT + "-" + safeSQLCast(loanAmount.getStringValue()));

                    break;
                }
                case StoreSafeIfc.PICKUP:
                {
                    sql.addColumn(FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT,
                                  FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT + "+" + safeSQLCast(pickupAmount.getStringValue()));

                    break;
                }
                case StoreSafeIfc.DEPOSIT:
                { // deposit
                    sql.addColumn(FIELD_STORE_SAFE_TENDER_DEPOSIT_AMOUNT,
                                  FIELD_STORE_SAFE_TENDER_DEPOSIT_AMOUNT + "+" + safeSQLCast(depositAmount.getStringValue()));

                    sql.addColumn(FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT,
                                  FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT + "-" + safeSQLCast(depositAmount.getStringValue()));
                    break;
                }
                default:
                {
                    break;
                }
              }


             // store safe operational balance at open or close
              CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
              if (openOperationalAmount.compareTo(zero) == CurrencyIfc.GREATER_THAN)
              {
                  sql.addColumn(FIELD_STORE_SAFE_TENDER_OPEN_OPERATING_BALANCE_TOTAL_AMOUNT,
                                safeSQLCast(openOperationalAmount.getStringValue()));
              }
              if (closeOperationalAmount.compareTo(zero) == CurrencyIfc.GREATER_THAN)
              {
                  sql.addColumn(FIELD_STORE_SAFE_TENDER_CLOSE_OPERATING_BALANCE_TOTAL_AMOUNT,
                                safeSQLCast(closeOperationalAmount.getStringValue()));
              }


              sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

              // Add Qualifier(s)
              sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + JdbcDataOperation.makeSafeString(safe.getStoreID()));
              sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + JdbcDataOperation.makeSafeString(safe.getStoreSafeID()));
              sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + JdbcDataOperation.makeSafeString(tenderTypeCode));
              sql.addQualifier(FIELD_TENDER_SUBTYPE + " = " +
                            JdbcDataOperation.makeSafeString(emptyStringToSpaceString(tenderType.getTenderSubType())));
              sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " +
                            JdbcDataOperation.makeSafeString(tenderType.getCountryCode()));

              sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + JdbcDataOperation.dateToSQLDateString(safe.getBusinessDay().dateValue())); // from JdbcDataOperations

              dataConnection.execute(sql.getSQLString());
              if (dataConnection.getUpdateCount() == 0)
              {
                  // in the beginning current total equals 0
                  CurrencyIfc currentAmount = DomainGateway.getBaseCurrencyInstance();

                  // Make the necessary adjustments before inserting the new data
                  currentAmount = currentAmount.add(pickupAmount).add(closeFundAmount);
                  currentAmount = currentAmount.subtract(loanAmount).subtract(depositAmount);
                  insertStoreSafeTenderHistory(dataConnection,
                                               safe,
                                               tenderType,
                                               currentAmount,
                                               openOperationalAmount,
                                               closeOperationalAmount,
                                               openFloatAmount,
                                               closeFloatAmount,
                                               depositAmount);
              }
        }// end if(writeRow)
    }

    /**
     * Inserts the financial tender totals into the store tender history table.
     * 
     * @param dataConnection connection to the db
     * @param safe The safe information
     * @param tenderType The type of tender
     * @exception DataException thrown when an error occurs.
     */
    public void insertStoreSafeTenderHistory(JdbcDataConnection dataConnection,
                                             StoreSafeIfc safe,
                                             TenderDescriptorIfc tenderType,
                                             CurrencyIfc currentAmount,
                                             CurrencyIfc openOperationalAmount,
                                             CurrencyIfc closeOperationalAmount,
                                             CurrencyIfc openFloatAmount,
                                             CurrencyIfc closeFloatAmount,
                                             CurrencyIfc depositAmount)
    throws DataException
    {
        String countryCode  = tenderType.getCountryCode();
        String tenderDesc = tenderTypeMap.getDescriptor(tenderType.getTenderType());

        if (!countryCode.equals(DomainGateway.getBaseCurrencyType().getCountryCode()))
        {
           tenderDesc = CountryCodeMap.getCountryDescriptor(countryCode) + " " + tenderDesc;
        }
        String tenderTypeCode = tenderTypeMap.getCode(tenderType.getTenderType());

        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_STORE_SAFE_TENDER_HISTORY);
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(safe.getStoreID()));
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID, makeSafeString(safe.getStoreSafeID()));
        sql.addColumn(FIELD_TENDER_TYPE_CODE,
                            JdbcDataOperation.makeSafeString(tenderTypeCode));
        sql.addColumn(FIELD_TENDER_SUBTYPE,
                            JdbcDataOperation.makeSafeString(emptyStringToSpaceString(tenderType.getTenderSubType())));
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE,
                            JdbcDataOperation.makeSafeString(tenderType.getCountryCode()));
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, tenderType.getCurrencyID());
        //-I18N
        sql.addColumn(FIELD_BUSINESS_DAY_DATE,
                JdbcDataOperation.dateToSQLDateString(safe.getBusinessDay().dateValue())); // from JdbcDataOperations

        // Available funds for deposit: pickups -loans + closing funds for tills(do not include ending float))
        sql.addColumn(FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT, currentAmount.getStringValue());

        // store safe operational balance at open
        sql.addColumn(FIELD_STORE_SAFE_TENDER_OPEN_OPERATING_BALANCE_TOTAL_AMOUNT, openOperationalAmount.getStringValue());

        // store safe operational balance at close
        sql.addColumn(FIELD_STORE_SAFE_TENDER_CLOSE_OPERATING_BALANCE_TOTAL_AMOUNT, closeOperationalAmount.getStringValue());

        // starting float for till
        sql.addColumn(FIELD_STORE_SAFE_TENDER_TILL_OPEN_FLOAT_TOTAL_AMOUNT, openFloatAmount.getStringValue());

        // ending float for till
        sql.addColumn(FIELD_STORE_SAFE_TENDER_TILL_CLOSE_FLOAT_TOTAL_AMOUNT, closeFloatAmount.getStringValue());

        // deposited funds
        sql.addColumn(FIELD_STORE_SAFE_TENDER_DEPOSIT_AMOUNT, depositAmount.getStringValue());

        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        dataConnection.execute(sql.getSQLString());
      }
}
