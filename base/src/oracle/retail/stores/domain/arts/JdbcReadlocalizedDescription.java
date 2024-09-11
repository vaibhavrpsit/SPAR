/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadlocalizedDescription.java /main/1 2012/09/17 15:27:12 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/13/12 - Added to support deprecation of JdbcPLUOperation.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.util.DBUtils;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This abstract class provides the extending class the ability to read the 
 * localized item descriptions based on the Item ID.
 * 
 * @version $Revision: /main/1 $
 */
public abstract class JdbcReadlocalizedDescription extends JdbcDataOperation
                                 implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1517489066721519116L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadlocalizedDescription.class);

    /**
     * Class constructor.
     */
    public JdbcReadlocalizedDescription()
    {
        super();
    }

    /**
     * Retrieves the localized descriptions based on the Item Id stored in
     * pluItem.getItemID.  The pluItem object can be a real PLU Item or dummy.
     * In either case it provides the mechanism for transporting the Item ID
     * to this method and for transporting the short and long localized descriptions
     * back to the caller.
     * 
     * @param dataConnection a connection to the database
     * @param pluItem a real or dummy item data container
     * @param localeRequestor
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public void readLocalizedItemDescriptions(JdbcDataConnection dataConnection,
            PLUItemIfc pluItem, LocaleRequestor localeRequestor)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadlocalizedDescription.readLocalizedItemDescriptions()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // Table to select from
        sql.addTable(TABLE_ITEM_I8);

        // add column
        sql.addColumn(FIELD_LOCALE);
        sql.addColumn(FIELD_ITEM_DESCRIPTION);
        sql.addColumn(FIELD_ITEM_SHORT_DESCRIPTION);

        // add identifier qualifier
        sql.addQualifier(new SQLParameterValue(FIELD_ITEM_ID, pluItem.getItemID()));

        // add qualifier for locale
        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        ResultSet rs = null;
        try
        {
            // execute sql
            String sqlString = sql.getSQLString();
            dataConnection.execute(sqlString, sql.getParameterValues());
            rs = (ResultSet)dataConnection.getResult();

            Locale bestMatchingDefaultLocale = LocaleMap.getBestMatch(localeRequestor.getDefaultLocale());
            Locale locale = null;
            // parse result set
            while (rs.next())
            {
                locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, 1));
                pluItem.setDescription(locale, getSafeString(rs, 2));
                pluItem.setShortDescription(locale, getSafeString(rs, 3));
                if (locale.equals(bestMatchingDefaultLocale))
                {
                    pluItem.getLocalizedDescriptions().setDefaultLocale(bestMatchingDefaultLocale);
                    pluItem.getShortLocalizedDescriptions().setDefaultLocale(bestMatchingDefaultLocale);
                }

            }
            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "JdbcReadlocalizedDescription.readLocalizedItemDescriptions()", se);
        }
        finally
        {
            DBUtils.getInstance().closeResultSet(rs);
        }

    }
}
