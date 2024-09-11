/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreCredit.java /main/20 2013/12/20 10:27:43 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    jswan     03/27/13 - Fixed issue with reading/exporting to POSLog Store
 *                         Credit Tenders.
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       02/03/11 - check in all
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    cgreene   04/21/09 - correct code list used for id types
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason code
 *    abondala  11/05/08 - updated files related to the reason codes
 *                         CheckIDTypes and MailBankCheckIDTypes
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         1/28/2008 4:31:09 PM   Sandy Gu
 *         Export foreign currency id, code and exchange rate for store credit
 *          and gift certificate foreign tender.
 *    9    360Commerce 1.8         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    8    360Commerce 1.7         4/25/2007 10:01:13 AM  Anda D. Cadar   I18N
 *         merge
 *    7    360Commerce 1.6         5/12/2006 5:26:29 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    6    360Commerce 1.5         4/5/2006 6:00:11 AM    Akhilashwar K. Gupta
 *         CR-3861: As per BA decision, reverted back the changes done earlier
 *          to fix the CR i.e. addition of following 4 fields in Store Credit
 *         and related code:
 *         - RetailStoreID
 *         - WorkstationID
 *         - TransactionSequenceNumber
 *         - BusinessDayDate
 *    5    360Commerce 1.4         3/15/2006 11:48:16 PM  Akhilashwar K. Gupta
 *         CR-3861: Modified buildSQLStatement() and
 *         convertResultSetEntry() methods.
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *:
 *    5    .v700     1.2.1.1     11/17/2005 16:10:47    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    4    .v700     1.2.1.0     11/16/2005 16:28:08    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.5  2004/02/29 16:20:48  nrao
 *   Added new fields to the sql read statement for first name, last name and id type.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 10 2002 11:14:54   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.5   Jun 07 2002 17:47:40   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.4   Jun 03 2002 16:22:14   epd
 * Fixed again for use of Data Transaction within Data Operation
 * Resolution for Domain SCR-79: Fix incorrect usage of data operation
 *
 *    Rev 1.3   Jun 03 2002 12:30:26   epd
 * Fixed a JDBC data operation instantiating a new Data Transaction
 * Resolution for Domain SCR-79: Fix incorrect usage of data operation
 *
 *    Rev 1.2   May 12 2002 23:10:00   mhr
 * db2 fix
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:45:56   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:00   msg
 * Initial revision.
 *
 *    Rev 1.2   10 Jan 2002 10:54:00   vxs
 * added retrievedCredit.setTrainingMode() in ConvertResultSetEntry()
 * Resolution for POS SCR-596: Store Credit package training mode updates
 *
 *    Rev 1.1   09 Jan 2002 17:23:14   vxs
 * Store Credit training mode functionality in place.
 * Resolution for POS SCR-596: Store Credit package training mode updates
 *
 *    Rev 1.0   Sep 20 2001 15:59:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * This class provides the methods needed to read a Store Credit.
 *
 * @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc
 * @version $Revision: /main/20 $
 */
public class JdbcReadStoreCredit extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -7403140197056222724L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     * @deprecated This class is no longer called from the data technician.  It is only called
     * from the JdbcReadTransaction class. 
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
    	if (logger.isDebugEnabled()) logger.debug("Entering JdbcReadCurrencyType.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // pull the data from the action object
        StoreCreditIfc inputsc = (StoreCreditIfc)action.getDataObject();
        StoreCreditIfc retrievedCredit = null;

        try
        {
            retrievedCredit = readStoreCredit(connection, inputsc);
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedCredit);


        if (logger.isDebugEnabled()) logger.debug("Exiting JdbcReadCurrencyType.execute()");
    }

    /**
        Returns the store credit instance which matches the
        specified store credit id.  This retrieves the
        store credit data from DO_CR_STR.
        @param  dataConnection  connection to the db
        @exception DataException upon error
        @deprecated in 14.0 use readStoreCredit(JdbcDataConnection dataConnection, StoreCreditIfc inputsc, String storeID)
     */
    public StoreCreditIfc readStoreCredit(JdbcDataConnection dataConnection,
                                      StoreCreditIfc inputsc)
                                      throws DataException
    {
        return readStoreCredit(dataConnection, inputsc, "");
    }

    /**
        Returns the store credit instance which matches the
        specified store credit id.  This retrieves the
        store credit data from DO_CR_STR.
        @param  dataConnection  connection to the db
        @param inputsc  Store Credit object
        @param storeID  Store Identifier
        @return
        @exception DataException upon error
     */
    public StoreCreditIfc readStoreCredit(JdbcDataConnection dataConnection,
                                      StoreCreditIfc inputsc, String storeID)
                                      throws DataException
    {

        StoreCreditIfc retrievedCredit = null;
        try
        {
            SQLSelectStatement sql = buildSQLStatement(inputsc);
            dataConnection.execute(sql.getSQLString());
            retrievedCredit = parseResultSet(dataConnection, storeID);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readStoreCredit", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreCredit", e);
        }

        return(retrievedCredit);
    }

    /**
        Builds SQL statement for specified Store Credit ID. <P>
        @param countryCode country code
        @return sql string
        @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildSQLStatement(StoreCreditIfc inputsc)
                                 throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // define tables, aliases
        sql.addTable(TABLE_STORE_CREDIT);

        // add columns
        sql.addColumn(FIELD_STORE_CREDIT_ID);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N
        sql.addColumn(FIELD_STORE_CREDIT_BALANCE);
        sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE);
        sql.addColumn(FIELD_STORE_CREDIT_STATUS);
        sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_BUSINESS_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_ID_TYPE);
        sql.addColumn(FIELD_STORE_CREDIT_TRAINING_FLAG);

        // add qualifiers
        sql.addQualifier("UPPER(" + TABLE_STORE_CREDIT + "." + FIELD_STORE_CREDIT_ID + ") = UPPER('" + inputsc.getDocumentID() + "')");
        // match training mode
        sql.addQualifier(TABLE_STORE_CREDIT + "." + FIELD_STORE_CREDIT_TRAINING_FLAG + " = "
                        + ((inputsc.isTrainingMode())?"'1'":"'0'") );
        return(sql);
    }

    /**
        Parses result set and creates a StoreCreditIfc object. <P>
        @param dataConnection data connection
        @param storeID  Store Identifier
        @return StoreCreditIfc object
        @exception SQLException thrown if result set cannot be parsed
        @exception DataException thrown if no records in result set
        @deprecated in 14.0 use arseResultSet(JdbcDataConnection dataConnection, String storeID)
     */
    protected StoreCreditIfc parseResultSet(JdbcDataConnection dataConnection)
                              throws SQLException, DataException
    {
        return parseResultSet(dataConnection, "");
    }
    
    /**
        Parses result set and creates a StoreCreditIfc object. <P>
        @param dataConnection data connection
        @return StoreCreditIfc object
        @exception SQLException thrown if result set cannot be parsed
        @exception DataException thrown if no records in result set
     */
    protected StoreCreditIfc parseResultSet(JdbcDataConnection dataConnection, String storeID)
                              throws SQLException, DataException
    {
        StoreCreditIfc retrievedCredit = null;
        ResultSet rs = (ResultSet) dataConnection.getResult();

        if (rs != null && rs.next())
        {
            retrievedCredit = instantiateStoreCreditIfc();
            convertResultSetEntry(retrievedCredit,
                                  rs);

            // close result set
            rs.close();
        }

        if (retrievedCredit != null)
        {
            retrievedCredit.setPersonalIDType(getLocalizedReasonCode(dataConnection,
            		storeID,
            		retrievedCredit.getPersonalIDType().getCode(),
                    CodeConstantsIfc.CODE_LIST_MAIL_BANK_CHECK_ID_TYPES,
                    getLocaleRequestor()));

        }
        return(retrievedCredit);
    }

    /**
        Converts result set entry into a StoreCreditIfc object. <P>
        @param retrievedCredit StoreCreditIfc object
        @param rs ResultSet set at entry to be converted
        @return index of result set entries
        @exception SQLException thrown if error occurs
     */
    protected int convertResultSetEntry(StoreCreditIfc retrievedCredit,
                                        ResultSet rs)
                                        throws SQLException, DataException
    {
        int index = 0;
        retrievedCredit.setStoreCreditID(getSafeString(rs, ++index));
        retrievedCredit.setCurrencyID(Integer.parseInt(getSafeString(rs, ++index)));//I18N
        retrievedCredit.setAmount(getCurrencyFromDecimal(rs, ++index));
        try
        {
            retrievedCredit.setExpirationDate(dateToEYSDate(rs, ++index));
        }
        catch (Exception e)
        {
            // Date may be null
            retrievedCredit.setExpirationDate(null);
        }
        retrievedCredit.setStatus(getSafeString(rs, ++index));
        retrievedCredit.setFirstName(getSafeString(rs, ++index));
        retrievedCredit.setLastName(getSafeString(rs, ++index));
        retrievedCredit.setBusinessName(getSafeString(rs, ++index));
        retrievedCredit.getPersonalIDType().setCode(getSafeString(rs, ++index));
        retrievedCredit.setTrainingMode(getBooleanFromString(rs, ++index));
        return(index);
    }

    /**
            Instantiates StoreCreditIfc object. <P>
            @return StoreCreditIfc object
     */
    public StoreCreditIfc instantiateStoreCreditIfc()
    {
            return DomainGateway.getFactory().getStoreCreditInstance();
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

    /**
     * Gets the localized reason code for a transaction
     * @param connection
     * @param storeId
     * @param reasonCode
     * @param codeListType
     * @param locale
     * @return LocalizedCodeIfc
     */
    protected LocalizedCodeIfc getLocalizedReasonCode(JdbcDataConnection connection, String storeId, String reasonCode, String codeListType, LocaleRequestor locale)
    {
        LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();


        if(!reasonCode.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            // Read Localized Reason Code
            CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
            criteria.setStoreID(storeId);
            criteria.setListID(codeListType);
            criteria.setLocaleRequestor(locale);
            criteria.setCode(reasonCode);
            localizedReasonCode = getLocalizedReasonCode(connection, criteria);
        }
        else
        {
            localizedReasonCode.setCode(reasonCode);
        }
        return localizedReasonCode;
    }

    /**
     * Retrieves a localized reason code.
     *
     * @param dataConnection
     * @param criteria
     * @return Localized Code object
     */
    protected LocalizedCodeIfc getLocalizedReasonCode (JdbcDataConnection dataConnection, CodeSearchCriteriaIfc criteria)
    {
        LocalizedCodeIfc localizedCode = null;
        JdbcReadCodeList readCodeList = new JdbcReadCodeList();
        try
        {
            localizedCode = readCodeList.readCode(dataConnection, criteria);
        }
        catch (DataException e)
        {
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                localizedCode = DomainGateway.getFactory().getLocalizedCode();
                localizedCode.setCode(criteria.getCode());
            }
        }
        return localizedCode;
    }


    public LocaleRequestor getLocaleRequestor()
    {
        LocaleRequestor localeRequestor = null;

        Locale[] supportedLocales = LocaleMap.getSupportedLocales();
        Locale defaultLocaleMatch = LocaleMap.getBestMatch(LocaleMap.getLocale(LocaleMap.DEFAULT));
        int defaultIndex = 0;
        for (int i = 0; i < supportedLocales.length; i++)
        {
            if (defaultLocaleMatch.equals(supportedLocales[i]))
            {
                defaultIndex = i;
                break;
            }
        }
        localeRequestor = new LocaleRequestor(supportedLocales, defaultIndex);
        return localeRequestor;
    }

}
