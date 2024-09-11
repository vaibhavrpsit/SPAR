/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveAllStores.java /main/15 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/12/12 - removed deprecated methods
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    ohorne 10/22/08 - I18N StoreInfo-related changes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:26:07    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:37  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:25  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:32:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   24 Jun 2002 10:58:10   adc
 * Additional changes for receiving
 * Resolution for Backoffice SCR-1167: Updates to receiving
 *
 *    Rev 1.0   Jun 03 2002 16:38:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Feb 04 2002 15:21:20   cdb
 * Initial revision.
 * Resolution for Backoffice SCR-446: Update Inventory service based on new requirements.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleMapConstantsIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class provides the methods needed to search the store directory.
 * 
 * @version $Revision: /main/15 $
 */
public class JdbcRetrieveAllStores extends JdbcDataOperation implements ARTSDatabaseIfc
{

    private static final long serialVersionUID = -2728496995957613437L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcRetrieveAllStores.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

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

        StoreIfc[] retrievedStores = null;

        try
        {
            // attempt to search store directory
            Object dataObject = action.getDataObject();
            if (dataObject instanceof LocaleRequestor)
            {
                // attempt to search store directory for requested locales
                LocaleRequestor localeRequestor = (LocaleRequestor) dataObject;
                retrievedStores = searchStoreDirectory(connection, localeRequestor);
            }
            else 
            {
                // this block is deprecated as of Release 13.1
                retrievedStores = searchStoreDirectory(connection, new LocaleRequestor(LocaleMap.getLocale(LocaleMapConstantsIfc.DEFAULT)));    
            }
            
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedStores);
    }

    /**
     * @param dataConnection connection to the db
     * @param localeRequestor the requested locales
     * @return array of stores which match search criteria
     * @throws DataException upon error
     */
    public StoreIfc[] searchStoreDirectory(JdbcDataConnection dataConnection, LocaleRequestor localeRequestor)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // define tables, aliases
        sql.addTable(TABLE_RETAIL_STORE, ALIAS_RETAIL_STORE);
        sql.addTable(TABLE_RETAIL_STORE_I8, ALIAS_RETAIL_STORE_I8);
        sql.addTable(TABLE_PARTY, ALIAS_PARTY);
        sql.addTable(TABLE_ADDRESS, ALIAS_ADDRESS);

        // add columns
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_CONTACT_STATE);
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE);
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_LOCATION_NAME);

 
        // add basic qualifiers to join by party ID on retail store table, address table and party table
        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = " + ALIAS_ADDRESS + "." + FIELD_PARTY_ID);
        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = " + ALIAS_PARTY   + "." + FIELD_PARTY_ID);
        sql.addQualifier(ALIAS_PARTY        + "." + FIELD_PARTY_TYPE_CODE + " = '" + STORE_PARTY_TYPE + "'");

        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));
 
        // add ordering
        sql.addOrdering(FIELD_RETAIL_STORE_ID);
        
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
     * Parses result set and creates store records.
     *
     * @param dataConnection data connection
     * @return array of StoreIfc objects
     * @throws SQLException thrown if result set cannot be parsed
     * @throws DataException thrown if no records in result set
     */
    protected StoreIfc[] parseResultSet(JdbcDataConnection dataConnection, LocaleRequestor localeRequestor)
        throws SQLException, DataException
    {
        Vector<StoreIfc> resultVector = new Vector<StoreIfc>();
        StoreIfc store = null;
        StoreIfc[] arrayStores = null;

        ResultSet rs = (ResultSet) dataConnection.getResult();
        if (rs != null)
        {
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

                    AddressIfc address = instantiateAddressIfc();
                    address.setState(getSafeString(rs, 2));

                    // set address reference
                    store.setAddress(address);

                    store.getLocalizedLocationNames().setDefaultLocale(localeRequestor.getDefaultLocale());
                    
                    // add store to vector
                    resultVector.addElement(store); 
                }
               
                //add locale and localized location name from resultSet to store's LocalizedLocationNames
                JdbcDataOperation.parseLocalizedNameResults(rs, store.getLocalizedLocationNames(), 3, 4);
            }

            // close result set
            rs.close();
        }
        
        // handle not found
        if (resultVector.isEmpty())
        {
            String msg = "JdbcRetrieveAllStoresStatus: status not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }
        // add employee objects
        // copy vector elements to array
        arrayStores = new StoreIfc[resultVector.size()];
        resultVector.copyInto(arrayStores);

        if (logger.isInfoEnabled())
        {
            logger.info("Matches found:  " + resultVector.size());
        }

        return(arrayStores);
    }

    /**
     * Instantiates StoreIfc object.
     * 
     * @return StoreIfc object
     */
    public StoreIfc instantiateStoreIfc()
    {
        return (DomainGateway.getFactory().getStoreInstance());
    }

    /**
     * Instantiates AddressIfc object.
     * 
     * @return AddressIfc object
     */
    public AddressIfc instantiateAddressIfc()
    {
        return (DomainGateway.getFactory().getAddressInstance());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
