/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreSafeTotals.java /main/15 2013/10/28 09:04:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
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
 * 8    360Commerce 1.7         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *      changes to export and import POSLog.
 * 7    360Commerce 1.6         6/22/2007 4:58:27 PM   Alan N. Sinton  CR 27313
 *       - Added code to read the ID_CNY_ICD field from LE_HST_WS_TND and
 *      LE_HST_STR_TND tables.
 * 6    360Commerce 1.5         4/25/2007 10:01:13 AM  Anda D. Cadar   I18N
 *      merge
 * 5    360Commerce 1.4         4/24/2006 5:51:31 PM   Charles D. Baker Merge
 *      of NEP62
 * 4    360Commerce 1.3         1/25/2006 4:11:18 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 * 4    .v700     1.2.2.0     11/16/2005 16:27:33    Jason L. DeLeau 4215: Get
 *      rid of redundant ArtsDatabaseifc class
 * 3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *Revision 1.6  2004/03/12 04:31:57  rdunsmore
 *cahnged business day from DATE to CHAR
 *
 *Revision 1.5  2004/02/17 17:57:37  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.4  2004/02/17 16:18:46  rhafernik
 *@scr 0 log4j conversion
 *
 *Revision 1.3  2004/02/12 17:13:17  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 23:25:24  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 15 2003 17:25:50   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Sep 03 2002 15:43:20   baa
 * Externalize domain constants
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Jun 03 2002 16:38:16   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts/updates into the store safe tender history
 * 
 * @version $Revision: /main/15 $;
 */
public class JdbcReadStoreSafeTotals extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -9212073711505951679L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadStoreSafeTotals.class);

    /**
     * testing currency object with value of $0.00 Removed for Store Replatform
     * - NOT USED
     */
    // private final CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    /**
     * Class constructor.
     */
    public JdbcReadStoreSafeTotals()
    {
        super();
        setName("JdbcReadStoreSafeTotals");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreSafeTotals.execute() starts");


        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        StoreSafeIfc safe = (StoreSafeIfc)action.getDataObject();

        safe = readStoreSafeTenderHistory(connection, safe);

        /*
         * Send back the result
         */
        dataTransaction.setResult(safe);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreSafeTotals.execute()");
    }

    /**
        Reads the financial tender totals from the store tender history
        table.
        <P>
        @param  dataConnection  connection to the db
        @param  safe            The safe information
        @param  tenderType      The type of tender
        @exception DataException thrown when an error occurs.
     */
    public StoreSafeIfc readStoreSafeTenderHistory(JdbcDataConnection dataConnection,
                                           StoreSafeIfc safe)
    throws DataException
    {

        // Set all amounts and counts to zero.
        CurrencyIfc currentAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc openOperationalAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc closeOperationalAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc depositAmount = DomainGateway.getBaseCurrencyInstance();

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_STORE_SAFE_TENDER_HISTORY);

        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_SUBTYPE);
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N
        sql.addColumn(FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT);
        sql.addColumn(FIELD_STORE_SAFE_TENDER_OPEN_OPERATING_BALANCE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_SAFE_TENDER_CLOSE_OPERATING_BALANCE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_SAFE_TENDER_DEPOSIT_AMOUNT);


        // Add Qualifier(s)
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(safe.getStoreID()));
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + inQuotes(safe.getStoreSafeID()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + dateToSQLDateString(safe.getBusinessDay().dateValue())); // from JdbcDataOperations


        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String tenderTypeCode = getSafeString(rs, ++index);
                String tenderSubType = getSafeString(rs, ++index);
                String countryCode = getSafeString(rs, ++index);
                int currencyID = rs.getInt(++index); //I18N
                currentAmount = getCurrencyFromDecimal(rs, ++index, countryCode);
                openOperationalAmount = getCurrencyFromDecimal(rs, ++index, countryCode);
                closeOperationalAmount = getCurrencyFromDecimal(rs, ++index, countryCode);
                depositAmount = getCurrencyFromDecimal(rs, ++index, countryCode);

                /*
                 * Fill in the values
                 */
                TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
                descriptor.setCountryCode(countryCode);
                descriptor.setCurrencyID(currencyID); //I18N
                descriptor.setTenderSubType(tenderSubType);
                int intTenderType = getTenderType(tenderTypeCode);
                String tenderTypeDesc = tenderTypeMap.getDescriptor(intTenderType);
                descriptor.setTenderType(intTenderType);

                if (!countryCode.equals(DomainGateway.getBaseCurrencyType().getCountryCode()))
                {
                    tenderTypeDesc = DomainGateway.getAlternateCurrencyInstance(countryCode).getCurrencyCode() + "_" + tenderTypeDesc;
                }

                if (currentAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc currentSafeAmount = safe.getCurrentOperatingFunds();
                    FinancialCountTenderItemIfc fcti = DomainGateway.getFactory().getFinancialCountTenderItemInstance();
                    fcti.setDescription(tenderTypeDesc);
                    fcti.setTenderDescriptor(descriptor);
                    fcti.setSummaryDescription("");
                    fcti.setSummary(true);
                    fcti.setAmountIn(currentAmount);
                    currentSafeAmount.addTenderItem(fcti);
                    //System.out.println("FCTI\n" + fcti);
                }

                if (openOperationalAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc openSafeAmount = safe.getOpenOperatingFunds();
                    FinancialCountTenderItemIfc fcti = DomainGateway.getFactory().getFinancialCountTenderItemInstance();
                    fcti.setDescription(tenderTypeDesc);
                    fcti.setTenderDescriptor(descriptor);
                    fcti.setSummaryDescription("");
                    fcti.setSummary(true);
                    fcti.setAmountIn(openOperationalAmount);
                    openSafeAmount.addTenderItem(fcti);
                    //System.out.println("FCTI\n" + fcti);
                }

                if (closeOperationalAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc closeSafeAmount = safe.getCloseOperatingFunds();
                    FinancialCountTenderItemIfc fcti = DomainGateway.getFactory().getFinancialCountTenderItemInstance();
                    fcti.setDescription(tenderTypeDesc);
                    fcti.setTenderDescriptor(descriptor);
                    fcti.setSummaryDescription("");
                    fcti.setSummary(true);
                    fcti.setAmountIn(closeOperationalAmount);
                    closeSafeAmount.addTenderItem(fcti);
                    //System.out.println("FCTI\n" + fcti);
                }

                if (depositAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc depositCount = safe.getDepositCounts();
                    FinancialCountTenderItemIfc fcti = DomainGateway.getFactory().getFinancialCountTenderItemInstance();
                    fcti.setDescription(tenderTypeDesc);
                    fcti.setTenderDescriptor(descriptor);
                    fcti.setSummaryDescription("");
                    fcti.setSummary(true);
                    fcti.setAmountIn(depositAmount);
                    depositCount.addTenderItem(fcti);
                    //System.out.println("FCTI\n" + fcti);
                }

            }
            rs.close();

        }
        catch (SQLException se)
        {
            logger.error(
                         se.toString());
            throw new DataException(DataException.SQL_ERROR, "Read Store Safe Tender History", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "readStoreSafeTenderHistory", e);
        }

        //System.out.println("READ SAFE\n" + safe);
        return(safe);
    }

    /**
       Returns a database safe string for the store id.
       <p>
       @return the store id
     */
    protected String getStoreID(StoreIfc store)
    {
        return("'" + store.getStoreID() + "'");
    }

    /**
       Returns a database safe string for the store id.
       <p>
       @return the store id
     */
    protected String getStoreID(String store)
    {
        return("'" + store + "'");
    }


    /**
       Returns a database safe string for the business date
       <p>
       @return the business date
     */
    protected String getBusinessDate(EYSDate businessDate)
    {
        return(dateToSQLDateString(businessDate.dateValue()));
    }

    /**
       Instantiates a financial total object.
       <p>
       @return new FinancialTotalsIfc object
     */
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {
        return(DomainGateway.getFactory().getFinancialTotalsInstance());
    }

    /**
       Returns the tender type
       <p>
       @param  tenderTypeCode  The type of tender
       @return the tender type
     */
    protected String getTenderTypeDesc(String tenderTypeCode)
    {
        return tenderTypeMap.getDescriptor(tenderTypeMap.getTypeFromCode(tenderTypeCode));
    }


    /**
       Returns the tender type
       <p>
       @param tenderTypeCode String {@link TenderLineItemIfc}
       @return int tender type
     */
    protected int getTenderType(String tenderTypeCode)
    {
        int value = tenderTypeMap.getTypeFromCode(tenderTypeCode);

        if (value == -1)
        {
            value = TenderLineItemIfc.TENDER_TYPE_CASH;
        }

        return value;
    }

}
