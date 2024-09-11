/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ReadStoreSQL.java /main/14 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/12/12 - removed deprecated methods
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    ohorne 10/22/08 - I18N StoreInfo-related changes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:41 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:27:21    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:29:34     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:24:31     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:13:33     Robert Pearse
     $
     Revision 1.4  2004/09/23 00:30:50  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:33:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 21 2003 14:57:20   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:42:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:51:02   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:10:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:22   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.StoreIfc;

/**
 * This class contains static methods for building SQL for accessing the store
 * tables and then parsing the results.
 * <P>
 * Note that the current release of this class does not look up the store
 * address from the LO_ADS table. That is saved for a future exercise.
 * 
 * @version $Revision: /main/14 $
 */
public class ReadStoreSQL implements ARTSDatabaseIfc, Serializable
{
    private static final long serialVersionUID = -4280992527262979397L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Builds SQL for retrieving a store's localized location name. Specifically
     * this SQL will return the locale and location name for the specified
     * storeID and locales.
     * 
     * @param storeID the storeID
     * @param localeRequestor the locales
     * @return
     */
    public static SQLSelectStatement getStoreLocationSQL(String storeID, LocaleRequestor localeRequestor)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add table
        sql.addTable(TABLE_RETAIL_STORE_I8);

        // add columns
        sql.addColumn(FIELD_LOCALE);
        sql.addColumn(FIELD_RETAIL_STORE_LOCATION_NAME);

        // add qualifier
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + JdbcUtilities.inQuotes(storeID));
        sql.addQualifier(FIELD_LOCALE
                + JdbcDataOperation.buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        // pass back string
        return sql;
    }

    /**
     * Parses result set from localized store-location query. The query must
     * contain columns for locale and localized location name.
     * 
     * @param rs the resultSet
     * @param store the store object onto which localized location
     * @param lclIndex the index to obtain a String value from the resultSet
     *            representing the locale
     * @param nameIndex the index to obtain a String value from the resultSet
     *            representing the store location name for the locale
     * @param localeRequestor the locale requestor used in the query.
     * @return supplied store object loaded from resultSet with localized store
     *         location names
     * @throws SQLException
     */
    public static StoreIfc parseStoreLocationResults(ResultSet rs, StoreIfc store, int lclIndex, int nameIndex,
            Locale defaultLocale) throws SQLException
    {
        LocalizedTextIfc locationNames = DomainGateway.getFactory().getLocalizedText();
        locationNames.setDefaultLocale(defaultLocale);

        do
        {
            locationNames = JdbcDataOperation.parseLocalizedNameResults(rs, locationNames, lclIndex, nameIndex);
        } while (rs.next());

        store.setLocalizedLocationNames(locationNames);

        return store;
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