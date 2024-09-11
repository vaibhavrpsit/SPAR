/* ===========================================================================
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreInfo.java /main/13 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nganesh   03/17/09 - Handled DataException in db query methods
 *    miparek   02/17/09 - Modified sql querry to set Address line info to the
 *                         result obj
 *    mkochumm  01/23/09 - set country
 *    ohorne    10/22/08 - I18N StoreInfo-related changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.domain.store.Store;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//--------------------------------------------------------------------------
/**
 The JdbcReadStoreInfo defines methods that the application calls to
 fetch store contact information from store/offline database.
 @version $revision$
 **/
//--------------------------------------------------------------------------
public class JdbcReadStoreInfo extends JdbcDataOperation implements ARTSDatabaseIfc
{

    //The logger to which log messages will be sent.
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadStoreStatus.class);

    //revision number of this class
    public static String revisionNumber = "$Revision: /main/13 $";

    //serialVersionUID
    private static final long serialVersionUID = 7227539020081718033L;

    //Store ID constant
    private static String STORE_STRING = "STOREID";

    // ---------------------------------------------------------------------
    /**
     * Class constructor.
     */
    // ---------------------------------------------------------------------
    public JdbcReadStoreInfo()
    {
        super();
        setName("JdbcReadStoreInfo");
    }

    // ---------------------------------------------------------------------
    /**
     * Executes the SQL statements against the database.
     * <P>
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    // ---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        Object dataObject = action.getDataObject();

        if (dataObject instanceof ItemInquirySearchCriteriaIfc)
        {
            //Get the search criteria from action
            ItemInquirySearchCriteriaIfc criteria = (ItemInquirySearchCriteriaIfc) action.getDataObject();

            //Read the stores address information
            StoreIfc[] stores = readStoreContactInfo(dataConnection, criteria);

            //Send back the result
            dataTransaction.setResult(stores);
        }

        else
        {
            logger.error(getName() + "Unknown data object");
            throw new DataException("Unknown data object");
        }
    }

    // ---------------------------------------------------------------------
    /**
     * Returns the contact information for a store.
     * <P>
     *
     * @param dataConnection connection to the db
     * @param storeID the store id
     * @return status of store, or null if no store history records.
     * @exception DataException upon error
     */
    // ---------------------------------------------------------------------
    public StoreIfc[] readStoreContactInfo(DataConnectionIfc dataConnection, ItemInquirySearchCriteriaIfc criteria)
            throws DataException
    {
        // Set of store ids for search
        String[] storeIDs = criteria.getStoreIDs();

        // Construct the query
        SQLSelectStatement sql = buildStoreContactQuery(criteria.getLocaleRequestor());

        List <StoreIfc> stores = new ArrayList<StoreIfc>();

        ResultSet rs = null;

        try
        {
            for (int storesCounter = 0; storesCounter < storeIDs.length ; storesCounter++)
            {

                // Get the store instance
                StoreIfc store = DomainGateway.getFactory().getStoreInstance();

                // Set the store id
                store.setStoreID(storeIDs[storesCounter]);

                // Get the query
                String query = sql.getSQLString();

                // Replace the STORE_STRING with the actual store id
                query = query.replaceAll(STORE_STRING, makeSafeString(storeIDs[storesCounter]));

                // Execute the query
                dataConnection.execute(query);

                // Get the result set
                rs = (ResultSet) dataConnection.getResult();

                ArrayList <PhoneIfc> phoneList = new ArrayList<PhoneIfc>();

                // Set the store contact information
                if (rs != null)
                {

                    if (rs.next())
                    {
                        int index = 0;
                        Vector<String> addrLines = new Vector<String>(3);
                        addrLines.add(getSafeString(rs, ++index));
                        addrLines.add(getSafeString(rs, ++index));
                        addrLines.add(getSafeString(rs, ++index));

                        String city = getSafeString(rs, ++index);
                        String state = getSafeString(rs, ++index);
                        String postalCode = getSafeString(rs, ++index);
                        String country = getSafeString(rs, ++index);

                        @SuppressWarnings("unused")
                        // countryPhoneCode is not required.
                        String countryPhoneCode = getSafeString(rs, ++index);

                        /*String areaPhoneCode =*/ getSafeString(rs, ++index);
                        String localPhoneNumber = getSafeString(rs, ++index);
                        String geoCode = getSafeString(rs, ++index);

                        AddressIfc address = DomainGateway.getFactory().getAddressInstance();

                        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();

                        address.setLines(addrLines);
                        address.setCity(city);
                        address.setState(state);
                        address.setPostalCode(postalCode);
                        address.setCountry(country);
                        store.setAddress(address);
                        phone.setPhoneNumber(localPhoneNumber);
                        phone.setCountry(country);
                        phoneList.add(phone);

                        store.setGeoCode(geoCode);

                        ReadStoreSQL.parseStoreLocationResults(rs, store, ++index, ++index, criteria.getLocaleRequestor().getDefaultLocale());
                    }

                    PhoneIfc[] list = new PhoneIfc[phoneList.size()];
                    phoneList.toArray(list);
                    store.setPhones(list);

                    //Add the store to the result
                    stores.add(store);
                }
            }
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readStoreInformation", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreInformation", e);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException se)
                {
                    throw new DataException(DataException.SQL_ERROR, "error closing ResultSet", se);
                }
            }
        }

        StoreIfc[] result = new Store[stores.size()];

        //copy the result to store array
        stores.toArray(result);

        //return the result
        return result;
    }

    /**
     * Constructs the store contact information search query
     * @param localeRequestor LocaleRequestor containing all desired locales
     * @return SQLStatement The store contact search statement
     */
    private SQLSelectStatement buildStoreContactQuery(LocaleRequestor localeReq)
    {
        //Sql statement for db query
        SQLSelectStatement sql = new SQLSelectStatement();

        //Add the desired tables (and aliases)
        sql.setDistinctFlag(true);
        sql.addTable(TABLE_RETAIL_STORE, ALIAS_RETAIL_STORE);
        sql.addTable(TABLE_RETAIL_STORE_I8, ALIAS_RETAIL_STORE_I8);
        sql.addTable(TABLE_ADDRESS, ALIAS_ADDRESS);

        //Add desired columns
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_ADDRESS_LINE_1);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_ADDRESS_LINE_2);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_ADDRESS_LINE_3);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_CITY);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_STATE);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_POSTAL_CODE);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_COUNTRY);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_COUNTRY_TELEPHONE_CODE);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_AREA_TELEPHONE_CODE);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER);
        sql.addColumn(ALIAS_RETAIL_STORE + "." + FIELD_GEO_CODE);
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE);
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_LOCATION_NAME);

        //Add Qualifiers
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_ID + " = " + STORE_STRING);
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE + buildINClauseString(LocaleMap.getBestMatch("", localeReq.getLocales())));
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID);

        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = " + ALIAS_ADDRESS + "." + FIELD_PARTY_ID);

        /*
         * String sql = SELECT DISTINCT AD.A1_CNCT,AD.A2_CNCT,AD.A3_CNCT,AD.CI_CNCT, AD.ST_CNCT, AD.PC_CNCT, AD.CO_CNCT, AD.CC_CNCT, AD.TA_CNCT,
         *              + AD.TL_CNCT, RS.ID_CD_GEO, RSI8.LCL, RSI8.NM_LOC
         *              + FROM LO_ADS AD, PA_STR_RTL RS, PA_STR_RTL_I8 RSI8
         *              + WHERE RSI8.ID_STR_RT = ?
         *              + AND RSI8.LCL IN (?,?,?,n...)
         *              + AND RSI8.ID_STR_RT = RS.ID_STR_RT
         *              + AND RS.ID_PRTY = AD.ID_PRTY
         *
         */

        //Return the sql
        return sql;
    }

}