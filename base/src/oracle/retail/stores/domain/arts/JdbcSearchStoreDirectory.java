/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSearchStoreDirectory.java /main/16 2012/09/12 11:57:21 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 04/03/12 - removed deprecated methods
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    mkochu 01/23/09 - set country
 *    mkochu 11/04/08 - i18n changes for phone and postalcode fields
 *    ohorne 10/22/08 - I18N StoreInfo-related changes
 *    ohorne 10/20/08 - added support for LocaleRequestor and i8 tables
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:25 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:26:48    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:51     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:46  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:23  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:33:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:34   msg
 * Initial revision.
 *
 *    Rev 1.2   18 May 2002 17:15:46   sfl
 * Using upper case in LIKE qualifier to handle the matching search situations when database
 * sever to be configured either case-sensitive or case-insensitive.
 * Resolution for POS SCR-1666: Employee - Search by employee name cannot find existing employees
 *
 *    Rev 1.1   Mar 18 2002 22:49:08   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:50   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:52   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMapConstantsIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.domain.utility.StoreSearchCriteria;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;

//-------------------------------------------------------------------------
/**
    This class provides the methods needed to search the store directory. <P>
**/
//-------------------------------------------------------------------------
public class JdbcSearchStoreDirectory extends JdbcDataOperation
                                      implements ARTSDatabaseIfc
{                                                                               // begin class JdbcSearchStoreDirectory

    private static final long serialVersionUID = 4509352775930434592L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcSearchStoreDirectory.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/16 $";

    //---------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        logger.debug( "JdbcSearchStoreDirectory.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        StoreIfc[] retrievedStores = null;
        
        // attempt to search store directory
        try
        {
            // pull the data from the action object
            Object dataObject = action.getDataObject();
            if (dataObject != null && dataObject instanceof StoreSearchCriteria)
            {
                StoreSearchCriteria searchCriteria = (StoreSearchCriteria) dataObject;
                
                ARTSStore store = searchCriteria.getARTSStore();            
                LocaleRequestor localeRequestor = searchCriteria.getLocaleRequestor();
                retrievedStores = searchStoreDirectory(connection, store, localeRequestor);
            }
            else if (dataObject != null && dataObject instanceof ARTSStore)
            {
                //this block is deprecated as of Release 13.1
                ARTSStore store = (ARTSStore) dataObject;
                retrievedStores = searchStoreDirectory(connection, store, new LocaleRequestor(LocaleMap.getLocale(LocaleMapConstantsIfc.DEFAULT)));                
            }
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedStores);

        logger.debug( "JdbcSearchStoreDirectory.execute()");
    }

    /**
     * Returns the stores which match the store criteria.   This search is done
     * by joining the party, retail store and address tables.  If a store has
     * no entry in the address table, it will not be located.  <P>
     * @param dataConnection  connection to the db
     * @param store store on which the search is to be based
     * @return array of stores which match search criteria
     * @throws DataException upon error
     * @deprecated As of release 13.1 use  {@link #searchStoreDirectory(JdbcDataConnection, ARTSStore, LocaleRequestor)}
     */
    public StoreIfc[] searchStoreDirectory(JdbcDataConnection dataConnection,
                                           ARTSStore store)
        throws DataException
    {
        return searchStoreDirectory(dataConnection, store, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }
    
    /**
     * Returns the stores which match the store criteria.   This search is done
     * by joining the party, retail store and address tables.  If a store has
     * no entry in the address table, it will not be located.  <P>
     * @param dataConnection  connection to the db
     * @param store store on which the search is to be based
     * @return array of stores which match search criteria
     * @throws DataException upon error
     */
    public StoreIfc[] searchStoreDirectory(JdbcDataConnection dataConnection,
                                           ARTSStore store,
                                           LocaleRequestor localeRequestor)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // select rtl.id_str_rt, rtl.nm_loc, ads.a1_cnct, ads.a2_cnct, ads.ci_cnct, ads.st_cnct 
        //        ,ads.pc_cnct, ads.ta_cnct, ads.tl_cnct, ads.co_cnct
        //        ,,RSI8.lcl, RSI8.nm_loc
        // from pa_str_rtl rtl, lo_ads ads, pa_prty prty
        // where rtl.id_str_rt = RSI8.id_str_rt
        // and RSI8.lcl in ('en')
        // and rtl.id_prty = ads.id_prty
        // and rtl.id_prty = prty.id_prty
        // and prty.ty_prty = 'STORE';

        // define tables, aliases
        sql.addTable(TABLE_RETAIL_STORE, ALIAS_RETAIL_STORE);
        sql.addTable(TABLE_RETAIL_STORE_I8, ALIAS_RETAIL_STORE_I8);
        sql.addTable(TABLE_PARTY, ALIAS_PARTY);
        sql.addTable(TABLE_ADDRESS, ALIAS_ADDRESS);

        // add columns
        sql.addColumn(ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_1);
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_2);
        sql.addColumn(FIELD_CONTACT_CITY);
        sql.addColumn(FIELD_CONTACT_STATE);
        sql.addColumn(FIELD_CONTACT_POSTAL_CODE);
        sql.addColumn(FIELD_CONTACT_COUNTRY);
        sql.addColumn(FIELD_CONTACT_AREA_TELEPHONE_CODE);
        sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER);
        
        //localized fields
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE);
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_LOCATION_NAME);

        //add qualifiers for localization
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID);
        
        // add basic qualifiers to join by party ID on retail store table, address table and party table
        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = " +
                         ALIAS_ADDRESS      + "." + FIELD_PARTY_ID);
        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = " +
                         ALIAS_PARTY        + "." + FIELD_PARTY_ID);
        sql.addQualifier(ALIAS_PARTY        + "." + FIELD_PARTY_TYPE_CODE + " = '" + STORE_PARTY_TYPE + "'");

        // add optional qualifiers
        StoreIfc useStore = store.getPosStore();
        if (useStore != null)
        {                                                               // begin check store attributes for keys
            // use first address line
            AddressIfc address = useStore.getAddress();
            if (address != null)
            {                                                   // begin check address attributes for keys
                                // check city
                String city = address.getCity();
                if (city != null)
                {
                    if (city.length() > 0)
                    {
                        sql.addQualifier("UPPER(" + FIELD_CONTACT_CITY + ")" + " like " + "UPPER('" + city + "%')");
                    }
                }
                                // check state
                String state = address.getState();
                if (state != null)
                {
                    if (state.length() > 0)
                    {
                        sql.addQualifier("UPPER(" + FIELD_CONTACT_STATE + ") like UPPER('" + state + "%')");
                    }
                }
                                // check postalCode
                String postalCode = address.getPostalCode();
                if (postalCode != null)
                {
                    if (postalCode.length() > 0)
                    {
                        sql.addQualifier("UPPER(" + FIELD_CONTACT_POSTAL_CODE + ")" + " like UPPER('" + postalCode + "%')");
                    }
                }
            }                                                   // end check address attributes for keys
        }                                                               // end check store attributes for keys

        sql.addOrdering(ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID);
        
        StoreIfc[] list = null;
        try
        {
            dataConnection.execute(sql.getSQLString());
            list = parseResultSet(dataConnection, localeRequestor);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "searchStoreDirectory", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "searchStoreDirectory", e);
        }

        return(list);
    }

    /**
     * Parses result set and creates store records. <P>
     * @param dataConnection
     * @return array of StoreIfc objects
     * @throws SQLException thrown if result set cannot be parsed
     * @throws DataException thrown if no records in result set
     * @deprecated As of release 13.1 use  {@link #parseLocalizedNameResults(ResultSet, LocalizedTextIfc, int, int, LocaleRequestor)} 
     */
    protected StoreIfc[] parseResultSet(JdbcDataConnection dataConnection)
        throws SQLException, DataException
    {
        return parseResultSet(dataConnection, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }
    
    /**
     * Parses result set and creates store records. <P>
     * @param dataConnection
     * @param dataConnection the requested locales
     * @return array of StoreIfc objects
     * @throws SQLException thrown if result set cannot be parsed
     * @throws DataException thrown if no records in result set
     */
    protected StoreIfc[] parseResultSet(JdbcDataConnection dataConnection, LocaleRequestor localeRequestor)
        throws SQLException, DataException
    {
        Vector<StoreIfc> resultVector = new Vector<StoreIfc>();
        ResultSet rs = (ResultSet) dataConnection.getResult();
        StoreIfc[] arrayStores = null;
        
        if (rs != null)
        {
            StoreIfc store = null;
            AddressIfc address = null;
            PhoneIfc phone = null;
          
            String postalCode;
            String postalCodeExt;
            StringTokenizer tokenizer = null;
            
            while (rs.next())
            {
                String identifier = getSafeString(rs, 1);
                
                //the ResultSet, is sorted by storeID, and may contain rows for more than one store; 
                //however, since a store's localized data results in one row per locale each row may or 
                //may not be for the same store.
                //Therefore, we only instantiate a new Store when the storeID obtained from the resultSet 
                //doesn't match the current Store's identifier.
                if (store == null || !identifier.equals(store.getStoreID()))
                {
                    store = instantiateStoreIfc();
                    store.setStoreID(identifier);
                    
                    address = instantiateAddressIfc();
                    
                    //address line 1
                    String addressLine = getSafeString(rs, 2);
                    if (addressLine.length() > 0)
                    {
                        address.addAddressLine(addressLine);
                    }

                    //address line 2
                    addressLine = getSafeString(rs, 3);
                    if (addressLine.length() > 0)
                    {
                        address.addAddressLine(addressLine);
                    }
                    
                    address.setCity(getSafeString(rs, 4));
                    address.setState(getSafeString(rs, 5));
                    
                    // parse postal code
                    postalCode = getSafeString(rs, 6);
                    postalCodeExt = "";
                    address.setPostalCode(postalCode);
                    address.setPostalCodeExtension(postalCodeExt);
                    String country = getSafeString(rs, 7);
                    address.setCountry(country);
                    
                    // set address reference
                    store.setAddress(address);

                    // set phone
                    phone = instantiatePhoneIfc();
                    phone.setPhoneNumber(getSafeString(rs, 9));
                    phone.setCountry(country);
                    store.addPhone(phone);
                    
                    store.getLocalizedLocationNames().setDefaultLocale(localeRequestor.getDefaultLocale());
                    
                    // add store to vector
                    resultVector.addElement(store); 
                }

                //add locale and localized location name from resultSet to store's LocalizedLocationNames
                JdbcDataOperation.parseLocalizedNameResults(rs, store.getLocalizedLocationNames(), 10, 11);                           
            }

            // close result set
            rs.close();
        }

        // handle not found
        if (resultVector.isEmpty())
        {
            String msg = "JdbcSearchStoreDirectoryStatus: status not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }
        // add employee objects
        else
        {
            // copy vector elements to array
            arrayStores = new StoreIfc[resultVector.size()];
            resultVector.copyInto(arrayStores);
            if (logger.isInfoEnabled())
            {
                logger.info("Matches found:  " + resultVector.size());
            }                
        }

        return(arrayStores);
    }


    //---------------------------------------------------------------------
    /**
       Instantiates StoreIfc object. <P>
       @return StoreIfc object
    **/
    //---------------------------------------------------------------------
    public StoreIfc instantiateStoreIfc()
    {                                   // begin instantiateStoreIfc()
        return(DomainGateway.getFactory().getStoreInstance());
    }                                   // end instantiateStoreIfc()

    //---------------------------------------------------------------------
    /**
       Instantiates AddressIfc object. <P>
       @return AddressIfc object
    **/
    //---------------------------------------------------------------------
    public AddressIfc instantiateAddressIfc()
    {                                   // begin instantiateAddressIfc()
        return(DomainGateway.getFactory().getAddressInstance());
    }                                   // end instantiateAddressIfc()

    //---------------------------------------------------------------------
    /**
       Instantiates PhoneIfc object. <P>
       @return PhoneIfc object
    **/
    //---------------------------------------------------------------------
    public PhoneIfc instantiatePhoneIfc()
    {                                   // begin instantiatePhoneIfc()
        return(DomainGateway.getFactory().getPhoneInstance());
    }                                   // end instantiatePhoneIfc()

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}                                                                               // end class JdbcSearchStoreDirectory
