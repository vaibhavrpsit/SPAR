/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadItemInfo.java /main/36 2014/02/10 11:22:29 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/06/14 - Fortify Null Derefernce fix
 *    abondala  09/04/13 - initialize collections
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/15/11 - removed deprecated methods and changed static
 *                         methods to non-static
 *    mchellap  06/01/11 - Fixed item inquiry using manufacturer
 *    vtemker   05/31/11 - Fixed issue with item description search (with empty
 *                         item id).
 *    ohorne    03/18/11 - fix for wildcard ItemNumber search in
 *                         readItemInfo(..)
 *    ohorne    02/22/11 - ItemNumber can be ItemID or PosItemID
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       12/08/09 - rework PLURequestor to use EnumSet and rename
 *                         set/unsetRequestType to add/removeRequestType
 *    sgu       12/03/09 - Change references to use the new/improved PLU apis
 *    cgreene   10/12/09 - correct item search to use manufacturer id in
 *                         posidentity table
 *    cgreene   09/24/09 - fix manufacturer query to match only posident id_mf
 *                         to pa_mf.id_mf
 *    spurkaya  05/29/09 - Updated readItemInfo method to not use UPPER for non
 *                         wildcard search with item id for better performance
 *    sgu       03/17/09 - refresh to latest build
 *    sgu       03/17/09 - use LocaleUtilities.getLocaleFromString to convert
 *                         String to Locale object
 *    abondala  03/13/09 - updated query to fix item search
 *    deghosh   02/19/09 - Added Ordering based on Item Id for Item search
 *    abondala  02/18/09 - deprecated unused selectItemInfo methods
 *    abondala  02/17/09 - removed the stock item table and UOM table from the
 *                         joining clause. This prevents searching service
 *                         items
 *    cgreene   03/01/09 - upgrade to using prepared statements for PLU
 *    mchellap  01/05/09 - Department search field changes
 *    mchellap  12/23/08 - Using setDistinctFlag() instead of UNIQUE
 *    vikini    12/10/08 - changed the scope of ILRM Methods
 *    nkgautam  12/02/08 - Code Review changes for ILRM CR
 *    vikini    11/10/08 - Incorporating Code Review findings
 *    vikini    11/08/08 - Retreive Item Messages for Item Info Search Screen
 *    ddbaker   10/28/08 - Updates due to code review
 *    ddbaker   10/23/08 - Final updates for localized item description support
 *    abondala  10/16/08 - I18Ning manufacturer name
 *    abondala  10/14/08 - I18Ning manufacturer name
 *    ohorne    10/08/08 - deprecated methods per I18N Database Technical
 *                         Specification
 *    mchellap  09/30/08 - Updated copy right header
 *
 *   $Log:
 *    9    360Commerce 1.8         4/17/2008 4:14:39 PM   Charles D. Baker CR
 *         31384 - Updated to correct errors in handling of search criteria
 *         introduced by CR 31319. Code review by Alan Sinton.
 *    8    360Commerce 1.7         4/16/2008 7:24:55 AM   Manikandan Chellapan
 *         CR#31368 Fixed the item search sql
 *    7    360Commerce 1.6         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    6    360Commerce 1.5         11/15/2007 11:37:56 AM Christian Greene
 *         Belize merge - PLU changes
 *    5    360Commerce 1.4         1/25/2006 4:11:16 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:43:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
 *   $:
 *    5    .v710     1.2.2.0     9/21/2005 13:39:46     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    4    .v700     1.2.3.0     11/17/2005 16:10:47    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *   $
 *   Revision 1.5.4.1  2004/11/12 22:02:23  jdeleau
 *   @scr 2638 If a SKU has multiple PLU's, make sure all PLU's are correctly displayed
 *   on an item inquiry.
 *
 *   Revision 1.5  2004/06/28 19:44:56  lzhao
 *   @scr 5657: wild card search in item description.
 *
 *   Revision 1.4  2004/06/19 18:42:32  jdeleau
 *   @scr 5676 Retrieve tax rules when an item inquiry is done.
 *
 *   Revision 1.3  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.2  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Jan 26 2004 15:38:12   kll
 * protect the String prior to construction of qualifier
 * Resolution for 1900: Item Inquiry cannot handle single quote in item description
 *
 *    Rev 1.2   13 Jan 2004 14:36:38   Tim Fritz
 * Wild cards are already in the itemno variable, so do not add them again.
 *
 *    Rev 1.1   Sep 03 2003 16:21:38   mrm
 * DB2 support
 * Resolution for POS SCR-3357: Add support needed by RSS
 *
 *    Rev 1.0   Aug 29 2003 15:31:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Apr 11 2003 08:34:18   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.5   Feb 15 2003 17:25:46   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.4   Jan 29 2003 11:07:42   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.3   Jan 16 2003 09:19:16   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.2   Jan 15 2003 13:52:54   bwf
 * In readItemInfo, check if itemNo ends with %.  If it does then query where item number is exact otherwise check if like item number %.
 *
 *    Rev 1.1   Jan 06 2003 11:45:38   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:37:28   msg
 * Initial revision.
 *===========================================================================
 */

package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import oracle.retail.stores.domain.stock.MessageDTO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * JdbcPLUOperation implements the price lookup JDBC data store operation.
 */
public class JdbcReadItemInfo extends JdbcPLUOperation
{
    private static final long serialVersionUID = 453278324824538585L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/36 $";

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(JdbcReadItemInfo.class);

    /**
     * The default selected value .
     */
    protected static String DEFAULT_SELECTED_VALUE = "-1";


    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
    	if (logger.isDebugEnabled()) logger.debug("Entering JdbcReadItemInfo.execute");

        JdbcDataConnection  connection  = (JdbcDataConnection)dataConnection;
        SearchCriteriaIfc   itemInfo    = (SearchCriteriaIfc)action.getDataObject();

        PLUItemIfc[]        items       = readItemInfo(connection, itemInfo);


        if(itemInfo.getGeoCode() == null)
        {
            JdbcReadNewTaxRules taxReader = new JdbcReadNewTaxRules();
            GeoCodeVO geoCodeVO =  taxReader.readGeoCodeFromStoreId(connection, itemInfo.getStoreNumber());
            assignTaxRules(connection, items, geoCodeVO.getGeoCode());
        }
        else
        {
            assignTaxRules(connection, items, itemInfo.getGeoCode());
        }

        // Search Item by any method, This call retrieves corresponding Item Messages and updates Item Object
        getItemMessages(connection, items);

        dataTransaction.setResult(items);

        if (logger.isDebugEnabled()) logger.debug("Exiting JdbcReadItemInfo.execute");
    }

    /**
     * Reads items from the POS Identity and Item tables.
     *
     * @param dataConnection a connection to the database
     * @param info the item lookup key
     * @return An array of PLUItems
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public PLUItemIfc[] readItemInfo(JdbcDataConnection dataConnection, SearchCriteriaIfc info)
                                        throws DataException
    {
        String itemDesc = info.getDescription();
        String itemTypeCode = info.getItemTypeCode();
        String itemUOMCode = info.getItemUOMCode();
        String itemStyleCode = info.getItemStyleCode();
        String itemColorCode = info.getItemColorCode();
        String itemSizeCode = info.getItemSizeCode();

        LocaleRequestor localeRequestor = info.getLocaleRequestor();

        if(itemDesc != null)
        {
            itemDesc = protectString(itemDesc);
        }
        String itemDept = info.getDepartmentID();
        String storeID  = info.getStoreNumber();
        int maxMatches = info.getMaximumMatches();
        String itemManufacurer = info.getManufacturer();
        if(itemManufacurer != null)
        {
        	itemManufacurer = protectString(itemManufacurer);
        }

        String qualifier = null;

        // keep track of just searching by an item identifier (e.g. itemID, posItemID, or both) and store id
        boolean searchingByItemAndStore = false;

        if (info.isSearchItemByItemNumber() && !StringUtils.isEmpty(info.getItemNumber()))
        {
            String itemNo = protectString(info.getItemNumber()); // protect any single quotation marks
            if (itemNo.indexOf('%') > -1)
            {
                qualifier = "(" + ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID + " LIKE UPPER(" + inQuotes(itemNo) + ")"
                      + " OR " +  ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " LIKE UPPER(" + inQuotes(itemNo) + "))";
            }
            else
            {
                qualifier = "(" + ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID + " = " + inQuotes(itemNo)
                                + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = " + inQuotes(itemNo) + ")";
                searchingByItemAndStore = true;
            }
        }
        else if (info.isSearchItemByPosItemID() && !StringUtils.isEmpty(info.getPosItemID()))
        {
            String posItemID = protectString(info.getPosItemID()); // protect any single quotation marks
            if (posItemID.indexOf('%') > -1)
            {
                qualifier = "UPPER(" + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + ")" + " LIKE " + "UPPER(" + inQuotes(posItemID) + ")";
            }
            else
            {
                qualifier = ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = " + inQuotes(posItemID);
                searchingByItemAndStore = true;
            }
        }
        else if (info.isSearchItemByItemID() && !StringUtils.isEmpty(info.getItemID()))
        {
            String itemID = protectString(info.getItemID()); // protect any single quotation marks
            if (itemID.indexOf('%') > -1)
            {
                qualifier = "UPPER(" + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + ")" + " LIKE " + "UPPER(" + inQuotes(itemID) + ")";
            }
            else
            {
                qualifier = ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = " + inQuotes(itemID);
                searchingByItemAndStore = true;
            }
        }

        if (itemDesc != null)
        {
            Set<Locale> bestMatches = LocaleMap.getBestMatch("", localeRequestor.getLocales());
            String descLocaleQualifier = null;
            if (itemDesc.indexOf('%') > -1 )
            {
                descLocaleQualifier =
                    "UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ")" + " LIKE " + "UPPER('" + itemDesc + "')";
            }
            else
            {
            	descLocaleQualifier =
                    "UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ")" + " LIKE " + "UPPER('%" + itemDesc + "%')";
            }
            if (info.getSearchLocale() != null)
            {
                descLocaleQualifier = descLocaleQualifier + " AND " +  ALIAS_ITEM_I8 + "." + FIELD_LOCALE + " = '" + LocaleMap.getBestMatch(info.getSearchLocale()).toString() + "'";
            }
            else
            {
                descLocaleQualifier = descLocaleQualifier + " AND " +  ALIAS_ITEM_I8 + "." + FIELD_LOCALE + " " +  JdbcDataOperation.buildINClauseString(bestMatches);
            }

            if (qualifier != null)
            {
                    //Using Locale Table to search description
                    qualifier = qualifier + " AND " + descLocaleQualifier;
            }
            else
            {
                    //Using Locale Table to search description
                    qualifier = descLocaleQualifier;
            }
        }

        //  search by manufacturer
        if (itemManufacurer != null)
        {
            Set<Locale> bestMatches = LocaleMap.getBestMatch("", localeRequestor.getLocales());
            String manufLocaleQualifier = null;
            if (itemManufacurer.indexOf('%') > -1)
            {
                manufLocaleQualifier =
                    "UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME + ")" + " LIKE " + "UPPER('" + itemManufacurer + "')";
            }
            else
            {
            	manufLocaleQualifier =
                    "UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME + ")" + " LIKE " + "UPPER('%" + itemManufacurer + "%')";
            }
            if (info.getSearchLocale() != null)
            {
                manufLocaleQualifier = manufLocaleQualifier + " AND " +  ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE + " = '" + LocaleMap.getBestMatch(info.getSearchLocale()).toString() + "'";
            }
            else
            {
                manufLocaleQualifier = manufLocaleQualifier + " AND " +  ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE + " " +  JdbcDataOperation.buildINClauseString(bestMatches);
            }
            if (qualifier != null)
            {
                // Using Locale Table to search manufacturer name
                qualifier = qualifier + " AND " + manufLocaleQualifier;
            }
            else
            {
                // Using Locale Table to search manufacturer name
                qualifier = manufLocaleQualifier;
            }
        }
        // End addition
        if (itemDept != null &&
            !itemDept.equals("-1"))
        {
            qualifier = qualifier + " AND " + ALIAS_ITEM + "." + FIELD_POS_DEPARTMENT_ID + " = " + inQuotes(itemDept);
        }

        if(itemTypeCode != null && !itemTypeCode.equals(DEFAULT_SELECTED_VALUE))
        {
            qualifier = qualifier + " AND " + ALIAS_ITEM + "." + FIELD_ITEM_TYPE_CODE + " = " + inQuotes(itemTypeCode);
        }

        if(itemUOMCode != null && !itemUOMCode.equals(DEFAULT_SELECTED_VALUE))
        {
            qualifier = qualifier + " AND " + ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_CODE + " = " + inQuotes(itemUOMCode);
        }
        if(itemStyleCode != null && !itemStyleCode.equals(DEFAULT_SELECTED_VALUE))
        {
            qualifier = qualifier + " AND " + ALIAS_STOCK_ITEM + "." + FIELD_STYLE_CODE + " = " + inQuotes(itemStyleCode);
        }
        if(itemColorCode != null && !itemColorCode.equals(DEFAULT_SELECTED_VALUE))
        {
            qualifier = qualifier + " AND " + ALIAS_STOCK_ITEM + "." + FIELD_COLOR_CODE + " = " + inQuotes(itemColorCode);
        }
        if(itemSizeCode != null && !itemSizeCode.equals(DEFAULT_SELECTED_VALUE))
        {
            qualifier = qualifier + " AND " + ALIAS_STOCK_ITEM + "." + FIELD_SIZE_CODE + " = " + inQuotes(itemSizeCode);
        }

        if (storeID != null )
        {
            qualifier =  qualifier + " AND " + ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID + " = " + makeSafeString(storeID);
            searchingByItemAndStore = searchingByItemAndStore & true;
        }
        else
        {
            searchingByItemAndStore = false;
        }

        PLUItemIfc[] items = null;
        boolean usePlanogramID = info.isUsePlanogramID();

        // construct a PLU requestor
        PLURequestor pluRequestor = new PLURequestor();
        if(itemManufacurer == null)
        {
        	pluRequestor.removeRequestType(PLURequestor.RequestType.Manufacturer);
        }
        if(!usePlanogramID)
        {
        	pluRequestor.removeRequestType(PLURequestor.RequestType.Planogram);
        }

        if (searchingByItemAndStore)
        {
            if (info.isSearchItemByItemNumber())
            {
                items = readPLUItemByItemNumber(dataConnection, info.getItemNumber(), storeID, false, pluRequestor, localeRequestor);
            }
            else if(info.isSearchItemByItemID())
            {
                items = readPLUItemByItemID(dataConnection, info.getItemID(), storeID, false, pluRequestor, localeRequestor);
            }
            else if (info.isSearchItemByPosItemID())
            {
                items = readPLUItemByPosItemID(dataConnection, info.getPosItemID(), storeID, false, pluRequestor, localeRequestor);
            }
        }
        else
        {
            items = selectItemInfo(dataConnection,
                    qualifier,
                    maxMatches,
                    pluRequestor,
                    localeRequestor);
        }

        items = readRelatedItems(dataConnection, items, storeID);

        return items;
    }

    /**
     * Selects items from the POS Identity, Stock Item and Item tables.
     *
     * @param dataConnection a connection to the database
     * @param qualifier a qualifier for item lookup
     * @param maxMatches maximum number of results to return
     * @param pluRequestor plu information to include
     * @param sqlLocale locale being used in SQL Query
     * @return An array of PLUItems
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public PLUItemIfc[] selectItemInfo(JdbcDataConnection dataConnection,
    		String qualifier,
    		int maxMatches,
    		PLURequestor pluRequestor,
    		LocaleRequestor localeRequestor)
    throws DataException
    {
    	SQLSelectStatement sql = new SQLSelectStatement();

    	// add tables
    	sql.addTable(TABLE_POS_IDENTITY, ALIAS_POS_IDENTITY);
    	sql.addTable(TABLE_ITEM, ALIAS_ITEM);
    	// add table for manufacturer
    	boolean isManufacturerSearch = (qualifier.indexOf(FIELD_ITEM_MANUFACTURER_NAME) > -1);
    	if (isManufacturerSearch)
    	{
    		sql.addTable(TABLE_ITEM_MANUFACTURER, ALIAS_ITEM_MANUFACTURER);

    	}

    	// Set distinct flag to true
    	sql.setDistinctFlag(true);
    	// add columns
    	sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID);//FIELD_ITEM_ID);
    	sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID);

    	// add qualifiers
    	if (isManufacturerSearch)
    	{
    		// need pos identities which are manufactured by this manu
    		sql.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_MANUFACTURER_ID +
    				" = " + ALIAS_ITEM_MANUFACTURER + "." + FIELD_ITEM_MANUFACTURER_ID);
    	}
    	else
    	{
    		sql.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID +
    				" = " + ALIAS_ITEM + "." + FIELD_ITEM_ID);
    	}

    	if(qualifier.indexOf(FIELD_STYLE_CODE) > -1 || qualifier.indexOf(FIELD_COLOR_CODE) > -1
    			|| qualifier.indexOf(FIELD_SIZE_CODE) > -1 || qualifier.indexOf(FIELD_UNIT_OF_MEASURE_CODE) > -1)
    	{
    		sql.addTable(TABLE_STOCK_ITEM, ALIAS_STOCK_ITEM);
    		sql.addTable(TABLE_UNIT_OF_MEASURE, ALIAS_UNIT_OF_MEASURE);
    		sql.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_ITEM_ID + " = " + ALIAS_ITEM + "." + FIELD_ITEM_ID + " AND " +
    				ALIAS_STOCK_ITEM + "." + FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE+ " = " + ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_CODE);
    	}


    	// Using Locale Table for description search
    	sql.addTable(TABLE_ITEM_I8, ALIAS_ITEM_I8);
    	if(qualifier.indexOf(FIELD_ITEM_MANUFACTURER_NAME) > -1)
    	{
    		sql.addTable(TABLE_ITEM_MANUFACTURER_I18N, ALIAS_ITEM_MANUFACTURER_I18N);
    	}

    	// Add qualifier for Locale Table
    	sql.addQualifier(ALIAS_ITEM_I8 + "." + FIELD_ITEM_ID + " = " + ALIAS_ITEM + "." + FIELD_ITEM_ID);
    	if(qualifier.indexOf(FIELD_ITEM_MANUFACTURER_NAME) > -1)
    	{
    		sql.addQualifier(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_ID + " = " + ALIAS_ITEM_MANUFACTURER
    				+ "." + FIELD_ITEM_MANUFACTURER_ID);
    	}

    	// use the parameterized qualifier as well
    	sql.addQualifier(qualifier);
    	sql.addOrdering(ALIAS_POS_IDENTITY, FIELD_POS_ITEM_ID);

    	// perform the query
    	ArrayList<String> results = new ArrayList<String>();
    	try
    	{
    		dataConnection.execute(sql.getSQLString());

    		ResultSet rs = (ResultSet)dataConnection.getResult();

    		while (rs.next())
    		{
    			int index = 0;
    			String itemID = getSafeString(rs, ++index);
    			String storeID = getSafeString(rs,++index);
    			String result = itemID + ","+storeID;
    			results.add(result);
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
    		dataConnection.logSQLException(se, "ReadItemInfo");
    		throw new DataException(DataException.SQL_ERROR, "ReadItemInfo", se);
    	}
    	catch (Exception e)
    	{
    		throw new DataException(DataException.UNKNOWN, "ReadItemInfo", e);
    	}

    	if (results.isEmpty())
    	{
    		throw new DataException(DataException.NO_DATA,
    		"No PLU was found processing the result set in JdbcReadItemInfo.");
    	}

    	// see if data read exceeds maximum matches parameter
    	if ((maxMatches > 0) && (results.size() > maxMatches))
    	{
    		throw new DataException(DataException.RESULT_SET_SIZE,
    		"Too many records were found processing the result set in JdbcReadItemInfo.");
    	}

    	// for each selected item id, read the PLUItem information
        ArrayList<PLUItemIfc> items = new ArrayList<PLUItemIfc>();
        Iterator<String> i = results.iterator();
        while (i.hasNext())
    	{
    		String result = i.next();
    		String itemID = null;
    		String storeID = null;
    		StringTokenizer strTk = new StringTokenizer(result,",");
    		while(strTk.hasMoreTokens())
    		{
    			itemID = strTk.nextToken();
    			storeID = strTk.nextToken();
    		}
    		PLUItemIfc item = readPLUItem(dataConnection, itemID, storeID, false, pluRequestor, localeRequestor)[0];
    		items.add(item);
    	}

    	// convert results to array and return
    	PLUItemIfc[] itemArray = new PLUItemIfc[items.size()];
    	items.toArray(itemArray);
    	return(itemArray);
    }

    /**
     * Searches the protectString for a single quote then adds another single
     * quote to protect it.
     *
     * @param protectString the string to protect
     * @return the string with any single quotation marks protected
     */
    static public String protectString(String protectString)
    {
       StringBuilder buf = new StringBuilder(protectString);
        int count = 0;
       for (int i = 0; i < buf.length(); ++i)
       {
           switch (buf.charAt(i))
           {
                case '\'':  // Single Quote
                buf.insert(i, '\''); // add another
                   i++;
                   break;

                case '\\':  // backslash character
                // Escape the backslash character
                count = i++;
                buf = jdbcHelperClass.backSlashChar(count, buf);
                break;

           }
       }
       return(buf.toString());
    }

    /**
     * Retrieves Item Messages per Item from the DB and sets
     * it into the PLU Item Object
     *
     * @return void
     * @param connection
     * @param items
     * @throws DataException
     */
    public void getItemMessages(JdbcDataConnection connection, PLUItemIfc[] items)
    {
    	if(items != null)
    	{
    		for(int itemCtr = 0 ; itemCtr < items.length ; itemCtr++)
    		{
    			PLUItemIfc item = items[itemCtr];
    			getItemLevelMessages(connection, item);
    		}
    	}
	}

    /**
     * Method which gets the ILRM Message for a Given Item
     *
     * The Catch block simply prints the exception caused during execution
     * as the requirement is to just print the error not propogate it
     *
     * @param dataConnection
     * @param item
     * @throws DataException
     */
	public void getItemLevelMessages(JdbcDataConnection dataConnection , PLUItemIfc item)
	{
		if (item != null)
        {
            SQLSelectStatement sql = new SQLSelectStatement();
            MessageDTO mdto = new MessageDTO();
            List<MessageDTO> messageList = new ArrayList<MessageDTO>();
            Map<String, List<MessageDTO>> messagesMap = new HashMap<String, List<MessageDTO>>(1);

            // add tables
            sql.addTable(TABLE_ITEM_MESSAGE_ASSOCIATION);
            sql.addTable(TABLE_ASSET_MESSAGES);
            sql.addTable(TABLE_ASSET_MESSAGES_I18N);


            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION,FIELD_MESSAGE_TYPE);
            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION,FIELD_MESSAGE_CODE_ID);
            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION,FIELD_MESSAGE_TRANSACTION_TYPE);
            sql.addColumn(TABLE_ASSET_MESSAGES_I18N,FIELD_LOCALE);
            sql.addColumn(TABLE_ASSET_MESSAGES_I18N,FIELD_MESSAGE_DESCRIPTION);
            // add columns from related item association


            // add qualifiers //TODO change ITEM_ID below to the IFC name
            sql.addQualifier(TABLE_ITEM_MESSAGE_ASSOCIATION,FIELD_ITEM_ID, "'" + item.getItemID() + "'");
            sql.addJoinQualifier(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES, FIELD_MESSAGE_CODE_ID);
            sql.addJoinQualifier(TABLE_ASSET_MESSAGES, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES_I18N, FIELD_MESSAGE_CODE_ID);
            // price info exists in the store server.

            sql.addOrdering(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TRANSACTION_TYPE);

            try
            {
                String str = sql.getSQLString();
                String transactionType = null;
                String messageType = null;
                logger.debug(str);
                // execute the query and get the result set
                dataConnection.execute(sql.getSQLString());
                ResultSet rs = (ResultSet)dataConnection.getResult();

                while (rs.next())
                {
                	if(transactionType != null && !transactionType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE)))
                	{
                		messageList.add(mdto);
                		messagesMap.put(transactionType, messageList);
                		messageList = null;
                		messageType = null;
                		messageList = new ArrayList<MessageDTO>();
                	}

                	if( messageType!= null && messageType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TYPE)))
                	{
                		mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rs.getString(FIELD_LOCALE)), rs.getString(FIELD_MESSAGE_DESCRIPTION));
                		continue;
                	}else if(messageType != null && !messageType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TYPE))){
                		messageList.add(mdto);
                	}

                	messageType = rs.getString(FIELD_MESSAGE_TYPE);

                	mdto = new MessageDTO();
                	mdto.setDefaultItemMessage(rs.getString(FIELD_MESSAGE_DESCRIPTION));
                	mdto.setItemMessageCodeID(rs.getString(FIELD_MESSAGE_CODE_ID));
                	mdto.setItemMessageTransactionType(rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE));
                	mdto.setItemMessageType(messageType);
                	mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rs.getString(FIELD_LOCALE)), rs.getString(FIELD_MESSAGE_DESCRIPTION));

                	logger.info(mdto.toString());
                	transactionType = rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE);
                }
                messageList.add(mdto);
                messagesMap.put(transactionType, messageList);
                item.setAllItemLevelMessages(messagesMap);
            }
            catch (DataException de)
            {
                logger.error(de.toString());
            }
            catch (SQLException se)
            {
               logger.error(se);
            }
            catch (Exception e)
            {
                logger.error("Unexpected exception in readItemMessage " + e);
            }
        }
	}
}
