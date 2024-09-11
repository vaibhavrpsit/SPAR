/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcMerchandiseHierarchyDataOperation.java /main/54 2014/07/10 14:11:21 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       11/23/14 - retrieve child merchandise hierarchy node
 *    tksharma  11/05/14 - added method retrieveHierarchyInformation to set hierarchy information
 *    tksharma  08/20/14 - added join qualifier to subQuery to return correct 
 *                         results based on manufacturer
 *    abhinavs  08/05/14 - Retrieving I18N department description to fix
 *                         blank department on MPOS UI
 *    rabhawsa  07/10/14 - discountable and taxable is added to, used in query.
 *    mkutiana  02/06/14 - Fortify Null Derefernce fix
 *    vtemker   09/30/13 - EIT Defect 437 - Fix SQL query error in Inventory
 *                         Inquiry
 *    abondala  09/04/13 - initialize collections
 *    mchellap  09/13/12 - Removed JdbPLUOperation.selectKitComponents method
 *                         call
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/15/11 - removed deprecated methods and changed static
 *                         methods to non-static
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    rsnayak   07/13/11 - search by item id or pos itm id
 *    cgreene   01/10/11 - refactor blob helpers into one
 *    npoola    08/25/10 - passed the connection object to the
 *                         IdentifierService getNextID method to use right
 *                         connection
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    mchellap  03/11/10 - Fixed offline item image retrieval issue
 *    abondala  01/03/10 - update header date
 *    nganesh   04/24/09 - Removed Distinct clause and CO_UOM table from Item
 *                         Search query
 *    nganesh   04/24/09 - Removed Upper function from SQL queries while
 *                         searching for Item ID
 *    mchellap  04/22/09 - Fixed item images npe
 *    cgreene   03/30/09 - removed item name column from item image table
 *    cgreene   03/27/09 - remove thumbnail image column from database
 *    cgreene   03/19/09 - refactoring changes
 *    djenning  03/18/09 - merge
 *    djenning  03/18/09 - remove string version of currency from iteminfo and
 *                         iteminfoifc
 *    nganesh   03/17/09 - Handled DataException in db query methods
 *    sgu       03/17/09 - use LocaleUtilities.getLocaleFromString to convert
 *                         String to Locale object
 *    nkgautam  02/26/09 - Handled dataException for item image for offline
 *                         support
 *    djenning  02/26/09 - small logo on lower right corner is missing on some
 *                         screens and retrieval of manufacturer for inventory
 *                         was broken.
 *    mchellap  02/04/09 - Formatting and cleanup
 *    mchellap  02/02/09 - Fixed wildcard item search
 *    mchellap  12/30/08 - Fixed the item search by description query
 *    mchellap  12/23/08 - Using setDistinctFlag() instead of UNIQUE
 *    atirkey   12/16/08 - Item List fix
 *
 *    nkgautam  12/02/08 - Changes for ILRM
 *    atirkey   12/02/08 - Item Image CR
 *    akandru   10/31/08 - EJ Changes_I18n
 *    akandru   10/30/08 - EJ changes
 *    ddbaker   10/28/08 - Update for merge
 *    atirkey   10/27/08 - query changed for item image
 *    ranojha   10/23/08 - Fixed the localized text for ItemSize and Buttons
 *    ranojha   10/23/08 - Fixed UnitOfMeasure I18N changes
 *    sgu       10/22/08 - refresh to latest label
 *    sgu       10/22/08 - add I8 support for creation and removal of
 *                         merchandise hierarchy group and level
 *    sgu       10/21/08 - add locale support for merchandise hierarchy
 *    ranojha   10/21/08 - Code Review changes
 *    ranojha   10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    akandru   10/20/08 - EJ -- I18N
 *    akandru   10/20/08 -
 *    sgu       10/20/08 - add locale support merchandise hierarchy
 *    atirkey   10/17/08 -
 *    abondala  10/17/08 - I18Ning manufacturer name
 *    ddbaker   10/15/08 - Implementing I18N Item Description for ItemInfo
 *                         class.
 *    masahu    10/09/08 - change of column names for as_itm_img table for db2
 *                         compatibility
 *    masahu    10/09/08 -
 *    ohorne    10/08/08 - deprecated methods per I18N Database Technical
 *                         Specification
 *    atirkey   10/01/08 - merged for item images
 *    atirkey   10/01/08 -
 *    mchellap  09/30/08 - Updated copy right header
 *
 *  $Log:
 *   21   360Commerce 1.20        5/27/2008 6:51:15 AM   Naveen Ganesh   Converted
 *         the groupId variable type from int to String
 *   20   360Commerce 1.19        4/8/2008 5:13:08 AM    Manikandan Chellapan
 *        CR#30956 Fixed KitItem search performance issue
 *   19   360Commerce 1.18        3/6/2008 5:08:30 AM    Anil Kandru     The
 *        parent ID obtained has been processed properly to get all the possible
 *        children IDs.
 *   18   360Commerce 1.17        2/25/2008 4:04:53 AM   Manikandan Chellapan
 *        CR#30118 PSI: Fixed UOM display
 *   17   360Commerce 1.16        2/22/2008 5:03:01 AM   Naveen Ganesh   Changed
 *        queries to search items by Description and Manufacturer name using
 *        wildcard.
 *   16   360Commerce 1.15        2/21/2008 2:53:02 AM   Naveen Ganesh   Changed
 *        Queries to search items by Description and Manufacturer name
 *   15   360Commerce 1.14        2/19/2008 10:22:32 AM  Naveen Ganesh
 *        Manufacturer ID has been processed properly in case of multiple item
 *        results
 *   14   360Commerce 1.13        2/12/2008 3:37:49 AM   Manikandan Chellapan
 *        CR#30194 PSI: Kit Item price not displayed properly
 *   13   360Commerce 1.12        2/6/2008 10:32:14 PM   Manikandan Chellapan
 *        CR#30162 PSI:Manufacturer value not displayed
 *   12   360Commerce 1.11        1/17/2008 5:02:00 AM   Manikandan Chellapan PSI
 *        MerchandiseHierarchyTest Fixes
 *   11   360Commerce 1.10        1/9/2008 11:56:16 PM   Manikandan Chellapan
 *        CR29833 Using APIs to get item selling price and regular price
 *   10   360Commerce 1.9         11/22/2007 11:09:30 PM Naveen Ganesh   PSI Code
 *        checkin
 *   9    360Commerce 1.8         6/5/2007 12:07:12 PM   Christian Greene Groupd
 *        id is now a String. Fix merchandise hierarchy jdbc classes and test
 *        class.
 *   8    360Commerce 1.7         5/30/2007 7:32:19 AM   Manas Sahu      SIE
 *        Workshop Changes + ORPOS Changes + Item UPSERT changes + Miscellaneous
 *   7    360Commerce 1.6         9/29/2006 5:31:06 PM   Brendan W. Farrell Fix
 *        for oracle db.
 *   6    360Commerce 1.5         7/5/2006 5:37:19 PM    Brendan W. Farrell Fix
 *        POS Unit tests.
 *   5    360Commerce 1.4         5/30/2006 2:03:24 PM   Brett J. Larsen CR 18490
 *        - UDM - updating gen-key methods to use the standard CO_ID_GEN table
 *   4    360Commerce 1.3         1/25/2006 4:11:09 PM   Brett J. Larsen merge
 *        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *   3    360Commerce 1.2         3/31/2005 4:28:38 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:22:39 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse
 *  $:
 *  4    .v700     1.2.1.0     11/16/2005 16:27:14    Jason L. DeLeau 4215: Get
 *  rid of redundant ArtsDatabaseifc class
 *  3    360Commerce1.2         3/31/2005 15:28:38     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:22:39     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:11:55     Robert Pearse
 *  $
 *  Revision 1.8  2004/08/18 12:59:02  kll
 *  @scr 6644: update file header information
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.identifier.IdentifierConstantsIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceLocator;
import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.ItemInfo;
import oracle.retail.stores.domain.stock.ItemInfoIfc;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.MessageDTO;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyGroupIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelKey;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelKeyIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.util.DBUtils;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;

import org.apache.log4j.Logger;

/**
 * This parent class of the merchandise hierarchy data operations.
 *
 * @see oracle.retail.stores.domain.arts.JdbcFetchAllLevelItems $Revision:
 *      st_rgbu_mchellap_bug-7758797/1 $
 */
abstract public class JdbcMerchandiseHierarchyDataOperation extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5095343071851488958L;

    protected static final Logger logger = Logger.getLogger(JdbcMerchandiseHierarchyDataOperation.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/54 $";

    /**
     * Kit actual price coloumn alias
     */

    protected static final String ALIAS_KIT_ACTUAL_PRICE = "PRC_ACT_KT";

    /**
     * Kit current price coloumn alias
     */
    protected static final String ALIAS_KIT_CURRNT_PRICE = "PRC_CRT_KT";

    /**
     * Limit for DB IN clause
     */
    public static int IN_CLAUSE_MAX = 1000;

    // /
    // / ID Generation methods
    // /

    /**
     * @deprecated as of 14.1. Use {@link UIConstantsIfc#UI_WILD_CARD} instead.
     */
    public static String UI_WILD_CARD = "*";

    /**
     * @deprecated as of 14.1. Use {@link DBConstantsIfc#DB_WILD_CARD} instead.
     */
    public static String DB_WILD_CARD = "%";

    /**
     * generateFunctionID()
     */
    protected int generateFunctionID(JdbcDataConnection connection) throws DataException
    {
        return IdentifierServiceLocator.getIdentifierService().getNextID(IdentifierConstantsIfc.COUNTER_MERCHANDISE_HIERARCHY_FUNCTION);
    }

    /**
     * generateLevelKey()
     */
    protected MerchandiseHierarchyLevelKeyIfc generateLevelKey(JdbcDataConnection connection, int hierarchyID)
            throws DataException
    {
        int levelID = IdentifierServiceLocator.getIdentifierService().getNextID(IdentifierConstantsIfc.COUNTER_MERCHANDISE_HIERARCHY_LEVEL);

        return DomainGateway.getFactory().getMerchandiseHierarchyLevelKeyInstance().initialize(hierarchyID, levelID);
    }

    /**
     * generateGroupID()
     */
    protected String generateGroupID(JdbcDataConnection connection) throws DataException
    {
        int id = IdentifierServiceLocator.getIdentifierService().getNextID(connection.getConnection(), IdentifierConstantsIfc.COUNTER_MERCHANDISE_HIERARCHY_GROUP);
        return String.valueOf(id);
    }

    /**
     * Actual work method - Go fetch all levels
     */
    protected ArrayList<MerchandiseHierarchyLevelIfc> doFetchAllLevels(JdbcDataConnection connection, int hierarchyID, LocaleRequestor localeReq)
            throws DataException
    {

        // Figure out where we are for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchAllLevels()";

        ArrayList<MerchandiseHierarchyLevelIfc> result = new ArrayList<MerchandiseHierarchyLevelIfc>(7);
        ArrayList<Integer> parents = new ArrayList<Integer>(7);

        try
        {
            // Make the SQL statement
            SQLSelectStatement stmt = new SQLSelectStatement();
            stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_LEVEL_V4, ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4);
            stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8);

            stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4, FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID);
            stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_LEVEL_NAME);
            stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4, FIELD_PARENT_MERCHANDISE_HIERARCHY_LEVEL_ID);
            stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_LOCALE);

            stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4, FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID,
                    ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID);
            stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4, FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID,
                    ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID);
            stmt.addQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4, FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID,
                    hierarchyID);

            Set<Locale> bestMatches = LocaleMap.getBestMatch(null, localeReq.getLocales());
            stmt.addQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8 + "." + FIELD_LOCALE + " "
                    + buildINClauseString(bestMatches));

            stmt.addOrdering(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4, FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID);

            /*
             * String sql = "select ID_MRHRC_LV, NM_MRHRC_LV, ID_MRHRC_LV_PRNT" + "
             * from CO_MRHRC_LV " + " where ID_MRHRC_FNC = ?";
             */

            // Populate & Execute
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // Populate the result
            MerchandiseHierarchyLevelIfc merchandiseHierarchyLevel = null;
            while (rs.next())
            {
                int index = 0;
                int levelId = rs.getInt(++index);
                String levelName = rs.getString(++index);
                int parentLevelId = rs.getInt(++index);
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));

                MerchandiseHierarchyLevelKeyIfc levelKey = DomainGateway.getFactory()
                        .getMerchandiseHierarchyLevelKeyInstance().initialize(hierarchyID, levelId);

                // Make & Populate the levelKey
                if ((merchandiseHierarchyLevel == null) || (!merchandiseHierarchyLevel.getLevelKey().equals(levelKey)))
                {

                    LocalizedTextIfc localizedLevelNames = DomainGateway.getFactory().getLocalizedText();
                    merchandiseHierarchyLevel = DomainGateway.getFactory().getMerchandiseHierarchyLevelInstance()
                            .initialize(levelKey, localizedLevelNames);
                    result.add(merchandiseHierarchyLevel);
                    parents.add(Integer.valueOf(parentLevelId));
                }

                merchandiseHierarchyLevel.setLevelName(locale, levelName);

            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }

        // Loop through the list of levels setting up child & parent pointers
        for (int i = 0; i < result.size(); i++)
        {

            // We're looking for this level's child.
            MerchandiseHierarchyLevelIfc level = result.get(i);

            for (int j = 0; j < parents.size(); j++)
            {

                // Is this the parent we're looking for ??
                if (parents.get(j).intValue() == level.getLevelKey().getLevelID())
                {

                    MerchandiseHierarchyLevelIfc child = result.get(j);

                    // Drop recursive ptr for rootLevel
                    if (!child.getLevelKey().equals(level.getLevelKey()))
                    {
                        child.setParentLevel(level);
                        level.setChildLevel(child);
                        break;
                    }
                }
            }
        }

        // we're done
        return result;
    }

    /**
     * doFetchChildrenGroupIDs()
     */
    protected ArrayList<String> doFetchChildrenGroupIDs(JdbcDataConnection connection, int hierarchyID,
            String parentID) throws DataException
    {
        // Figure out what our name is for exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchChildrenGroupIDs()";

        ArrayList<String> result = null;

        // Make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();

        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);

        stmt.addColumn(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);

        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);
        stmt.addQualifier(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(parentID));
        stmt.addQualifier(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID + " <> "
                + FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);

        stmt.addOrdering(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);
        /*
         * String sql = "select ID_MRHRC_GP_CHLD" + " from ST_ASCTN_MRHRC" + "
         * where ID_MRHRC_FNC = ?" + " and ID_MRHRC_GP_PRNT = ?" + " and
         * ID_MRHRC_GP_PRNT <> ID_MRHRC_GP_CHLD" + " order by ID_MRHRC_GP_CHLD";
         */
        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // Build a list of ChildIDs
            result = new ArrayList<String>(10);
            while (rs.next())
                result.add(rs.getString(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID));
            rs.close();
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
        return result;
    }

    protected ArrayList<MerchandiseHierarchyGroupIfc> doFetchChildrenGroupIDs(JdbcDataConnection connection, int hierarchyID,
            String levelID, String parentID, LocaleRequestor localeReq) throws DataException
    {
        // Figure out what our name is for exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchChildrenGroupIDs()";

        ArrayList<MerchandiseHierarchyGroupIfc> result = null;

        // Make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();

        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION, ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_GROUP, ALIAS_MERCHANDISE_HIERARCHY_GROUP);
        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_GROUP_I8, ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8);

        stmt.addColumn(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);
        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_NAME);
        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_LOCALE);

        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID, levelID);
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);
        stmt.addQualifier(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(parentID));
        stmt.addQualifier(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID + " <> "
                + FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);

        Set<Locale> bestMatches = LocaleMap.getBestMatch(null, localeReq.getLocales());
        stmt.addQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8 + "." + FIELD_LOCALE + " "
                + buildINClauseString(bestMatches));

        stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID,
                ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION, FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);
        stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID,
                ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);

        stmt.addOrdering(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);

        /*
         * String sql = "select ID_MRHRC_GP_CHLD" + " from ST_ASCTN_MRHRC" + "
         * where ID_MRHRC_FNC = ?" + " and ID_MRHRC_GP_PRNT = ?" + " and
         * ID_MRHRC_GP_PRNT <> ID_MRHRC_GP_CHLD" + " order by ID_MRHRC_GP_CHLD";
         */

        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // Build a list of ChildIDs
            result = new ArrayList<MerchandiseHierarchyGroupIfc>(10);
            MerchandiseHierarchyGroupIfc merchandiseHierarchyGroup = null;
            while (rs.next())
            {
                int index = 0;
                String groupID = getSafeString(rs, ++index);
                String groupName = getSafeString(rs, ++index);
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));

                if ((merchandiseHierarchyGroup == null) || (!merchandiseHierarchyGroup.getID().equals(groupID)))
                {
                    LocalizedTextIfc localizedGroupNames = DomainGateway.getFactory().getLocalizedText();
                    merchandiseHierarchyGroup = DomainGateway.getFactory().getMerchandiseHierarchyGroupInstance()
                            .initialize(
                                    groupID,
                                    localizedGroupNames,
                                    new MerchandiseHierarchyLevelKey().initialize(hierarchyID, Integer
                                            .parseInt(levelID)));
                    result.add(merchandiseHierarchyGroup);
                }
                merchandiseHierarchyGroup.setGroupName(locale, groupName);
            }
            rs.close();
        }

        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
        return result;
    }

    /**
     * doFetchChildrenGroupIDs()
     * @deprecated since 14.1
     */
    protected ArrayList<MerchandiseHierarchyGroupIfc> doFetchLevelChildrenGroupIDs(JdbcDataConnection connection, int hierarchyID,
            String levelID, LocaleRequestor localeReq) throws DataException
    {
        // Figure out what our name is for exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchChildrenGroupIDs()";

        // Make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();

        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION, ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_GROUP, ALIAS_MERCHANDISE_HIERARCHY_GROUP);
        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_GROUP_I8, ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8);

        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION, FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);
        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_NAME);
        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_LOCALE);

        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID, levelID);

        Set<Locale> bestMatches = LocaleMap.getBestMatch(null, localeReq.getLocales());
        stmt.addQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8 + "." + FIELD_LOCALE + " "
                + buildINClauseString(bestMatches));

        stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION, FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID,
                ALIAS_MERCHANDISE_HIERARCHY_GROUP, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);
        stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID,
                ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);

        stmt.addOrdering(ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION, FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);

        /*
         * SELECT ID_MRHRC_GP_CHLD FROM ST_ASCTN_MRHRC + WHERE ID_MRHRC_FNC = ? +
         * AND ID_MRHRC_LV = ?
         */

        ArrayList<MerchandiseHierarchyGroupIfc> result = null;
        MerchandiseHierarchyGroupIfc merchandiseHierarchyGroup = null;
        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // Build a list of ChildIDs
            result = new ArrayList<MerchandiseHierarchyGroupIfc>(10);
            while (rs.next())
            {
                int index = 0;
                String groupID = getSafeString(rs, ++index);
                String groupName = getSafeString(rs, ++index);
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));

                if ((merchandiseHierarchyGroup == null) || !merchandiseHierarchyGroup.getID().equals(groupID))
                {
                    LocalizedTextIfc localizedGroupNames = DomainGateway.getFactory().getLocalizedText();
                    merchandiseHierarchyGroup = DomainGateway.getFactory().getMerchandiseHierarchyGroupInstance()
                            .initialize(
                                    groupID,
                                    localizedGroupNames,
                                    new MerchandiseHierarchyLevelKey().initialize(hierarchyID, Integer
                                            .parseInt(levelID)));
                    result.add(merchandiseHierarchyGroup);
                }
                merchandiseHierarchyGroup.setGroupName(locale, groupName);
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
        return result;
    }
    
    /**
     * Returns the parent groups at a particular level in a particular hierarchy
     * 
     * @param connection
     * @param hierarchyID
     * @param levelID
     * @param localeReq
     * @return
     * @throws DataException
     * @since 14.1
     */
    protected ArrayList<MerchandiseHierarchyGroupIfc> doFetchLevelGroupIDs(JdbcDataConnection connection, int hierarchyID,
            String levelID, LocaleRequestor localeReq) throws DataException
    {
        // Figure out what our name is for exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchLevelGroupIDs()";

        // Make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();

        stmt.setDistinctFlag(true);
        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION, ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_GROUP, ALIAS_MERCHANDISE_HIERARCHY_GROUP);
        stmt.addTable(TABLE_MERCHANDISE_HIERARCHY_GROUP_I8, ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8);

        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION, FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID);
        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_NAME);
        stmt.addColumn(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_LOCALE);

        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID, levelID);

        Set<Locale> bestMatches = LocaleMap.getBestMatch(null, localeReq.getLocales());
        stmt.addQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8 + "." + FIELD_LOCALE + " "
                + buildINClauseString(bestMatches));

        stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION, FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID,
                ALIAS_MERCHANDISE_HIERARCHY_GROUP, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);
        stmt.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID,
                ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);

        stmt.addOrdering(ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION, FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID);

        /*
         * SELECT ID_MRHRC_GP_PRNT FROM ST_ASCTN_MRHRC + WHERE ID_MRHRC_FNC = ?
         * + AND ID_MRHRC_LV = ?
         */

        ArrayList<MerchandiseHierarchyGroupIfc> result = null;
        MerchandiseHierarchyGroupIfc merchandiseHierarchyGroup = null;
        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            // Build a list of GroupIDs
            result = new ArrayList<MerchandiseHierarchyGroupIfc>(10);
            while (rs.next())
            {
                int index = 0;
                String groupID = getSafeString(rs, ++index);
                String groupName = getSafeString(rs, ++index);
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));

                if ((merchandiseHierarchyGroup == null) || !merchandiseHierarchyGroup.getID().equals(groupID))
                {
                    LocalizedTextIfc localizedGroupNames = DomainGateway.getFactory().getLocalizedText();
                    merchandiseHierarchyGroup = DomainGateway
                            .getFactory()
                            .getMerchandiseHierarchyGroupInstance()
                            .initialize(
                                    groupID,
                                    localizedGroupNames,
                                    new MerchandiseHierarchyLevelKey().initialize(hierarchyID,
                                            Integer.parseInt(levelID)));
                    result.add(merchandiseHierarchyGroup);
                }
                merchandiseHierarchyGroup.setGroupName(locale, groupName);
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
        return result;
    }


    /**
     * doFetchParentGroupIDs() - actual work method
     */
    protected ArrayList<String> doFetchParentGroupIDs(JdbcDataConnection connection, String groupID,
            int hierarchyID) throws DataException
    {
        // Figure out what our name is for exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchParentGroupIDs()";

        // This'll be a small list
        ArrayList<String> result = new ArrayList<String>(4);

        // Make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();

        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);

        stmt.addColumn(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID);

        stmt.addQualifier(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(groupID));
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);

        /*
         * String sql = "select ID_MRHRC_GP_PRNT" + " from ST_ASCTN_MRHRC" + "
         * where ID_MRHRC_GP_CHLD = ?" + " and ID_MRHRC_FNC = ?";
         */
        try
        {

            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // Loop through the resultList reading parentGroupIDs
            while (rs.next())
            {
                String temp = rs.getString(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID);
                if (temp != null)
                    result.add(temp);
            }

            // We're finished
            rs.close();
            return result;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * doFetchItemMerchGroupID() - actual work method
     */
    protected String doFetchItemMerchGroupID(JdbcDataConnection connection, String itemID) throws DataException
    {
        // Figure out what our name is for exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchItemMerchGroupID()";

        // Maybe make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_ITEM);
        stmt.addColumn(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);
        stmt.addQualifier(FIELD_ITEM_ID, makeSafeString(itemID));

        /*
         * String sql = "select ID_MRHRC_GP" + " from AS_ITM" + " where ID_ITM =
         * ?";
         */
        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // We've got the initial group
            rs.next();
            String result = rs.getString(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);
            rs.close();

            return result;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * doRemoveHierarchyAssociations() - actual work method
     */
    protected void doRemoveHierarchyAssociations(JdbcDataConnection connection, int hierarchyID) throws DataException
    {
        // figure out where we are
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doRemoveHierarchyAssociations()";

        // Make the SQL statement
        SQLDeleteStatement stmt = new SQLDeleteStatement();
        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);

        try
        {
            connection.execute(stmt.getSQLString());
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * doRemoveGroupAssociations() - actual work method
     */
    protected void doRemoveGroupAssociations(JdbcDataConnection connection, int hierarchyID, String groupID)
            throws DataException
    {
        // figure out where we are
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doRemoveGroupAssociations()";

        // Make the SQL statement
        SQLDeleteStatement stmt = new SQLDeleteStatement();
        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);
        stmt.addQualifier(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(groupID));

        try
        {
            connection.execute(stmt.getSQLString());
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * doRemoveGroupAssociation() - actual work method
     */
    protected void doRemoveGroupAssociation(JdbcDataConnection connection, int hierarchyID, String parentID,
            String childID) throws DataException
    {
        // figure out where we are
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doRemoveGroupAssociation()";

        // Make the SQL statement
        SQLDeleteStatement stmt = new SQLDeleteStatement();
        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);
        stmt.addQualifier(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(parentID));
        stmt.addQualifier(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(childID));

        try
        {
            connection.execute(stmt.getSQLString());
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * doAssociateGroups - actual work function
     */
    protected void doAssociateGroups(JdbcDataConnection connection, String childGroupID, String parentGroupID,
            MerchandiseHierarchyLevelKeyIfc parentLevelKey) throws DataException
    {
        // figure out where we are
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doAssociateGroups()";

        // Make the SQL Statement
        SQLInsertStatement stmt = new SQLInsertStatement();
        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addColumn(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(childGroupID));
        stmt.addColumn(FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(parentGroupID));
        stmt.addColumn(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, parentLevelKey.getHierarchyID());
        stmt.addColumn(FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID, parentLevelKey.getLevelID());

        // Don't forget the timestamps
        stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        try
        {
            connection.execute(stmt.getSQLString());
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName + ".association", e);
        }
    }

    /**
     * doUpdateLevelParent() - actual work method
     */
    protected int doCountGroups(JdbcDataConnection connection, int hierarchyID) throws DataException
    {
        // Figure out the method name for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doCountGroups()";

        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addColumn("count(" + FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID + ")");
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, hierarchyID);
        stmt.addQualifier(" not " + FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID,
                FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);
        /*
         * String sql = "select count(ID_MRHRC_GP_CHLD)" + " from
         * ST_ASCTN_MRHRC" + " where ID_MRHRC_FNC = ?" + " and not
         * ID_MRHRC_GP_PRNT = ID_MRHRC_GP_CHLD";
         */

        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            rs.next();
            int result = rs.getInt(1);

            rs.close();
            return result;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * doFetchItemIDs() - Get the list of Items that're in the given list of
     * MerchandiseHierarchy groups.
     */
    protected ArrayList<Integer> doFetchItemIDs(JdbcDataConnection connection, ArrayList<String> groupIDs)
            throws DataException
    {
        // Figure out the method name for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doFetchItemIDs()";

        // convert group ids to set string
        String set = convertToSetString(groupIDs);

        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_ITEM);
        stmt.addColumn(FIELD_ITEM_ID);
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID + " in " + set);

        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // Make & Populate the result list of ItemIDs
            ArrayList<Integer> result = new ArrayList<Integer>(10);
            while (rs.next())
            {
                result.add(Integer.valueOf(rs.getInt(1)));
            }

            // We're done here
            return result;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * Are any Items directly in that merchandise hierarchy group?
     */
    protected int doCountItemIDs(JdbcDataConnection connection, String groupID) throws DataException
    {
        // Figure out the method name for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doCountItemIDs()";

        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_ITEM);
        stmt.addColumn("count(" + FIELD_ITEM_ID + ")");
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(groupID));

        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            rs.next();
            int result = rs.getInt(1);

            rs.close();
            return result;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * Are any MerchandiseHierarchyGroups in this level? A level has member
     * groups if there's an association to a group in the level's parentLevel
     */
    protected int doCountGroups(JdbcDataConnection connection, MerchandiseHierarchyLevelKeyIfc levelKey)
            throws DataException
    {
        // Figure out the method name for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.doCountGroups(levelKey)";

        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_MERCHANDISE_HIERARCHY_ASSOCIATION);
        stmt.addColumn("count(" + FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID + ")");
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, levelKey.getHierarchyID());
        stmt.addQualifier(FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID, levelKey.getLevelID());
        stmt.addQualifier(FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID + " <> "
                + FIELD_PARENT_MERCHANDISE_HIERARCHY_GROUP_ID);

        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            rs.next();
            int result = rs.getInt(1);

            rs.close();
            return result;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }
    }

    /**
     * Retrieves item price information for the item
     *
     * @param connection The database connection
     * @param criteria The search criteria
     * @return ItemInfoIfc The item information
     */
    protected ItemInfoIfc doFetchItemPriceInfo(JdbcDataConnection connection, ItemInquirySearchCriteriaIfc criteria)
            throws DataException
    {

        // Item information object
        ItemInfoIfc itemInfo = DomainGateway.getFactory().getItemInfoInstance();

        // set items actual price
        if (criteria.getKitHeaderCode() == ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER)
        {
            // set the price for kit item
            itemInfo.setActualPrice(DomainGateway.getBaseCurrencyInstance(fetchKitActualPrice(connection, criteria.getItemID(), criteria.getStoreNumber())));
        }
        else
        {
            // not a kit item
            itemInfo.setActualPrice(DomainGateway.getBaseCurrencyInstance(fetchItemActualPrice(connection, criteria)));
        }

        // set the items promotion end date
        itemInfo.setPromotionEndDate(fetchPromotionEndDate(connection, criteria));

        return itemInfo;

    }

    /**
     * Retrieves item information for the items belogs to a group and its
     * chidren
     *
     * @param parentGrpIDs The parent group ids for search
     * @param connection The database connection
     * @return ItemInfo[] item information
     */
    protected ItemInfo[] doFetchAllLevelItems(JdbcDataConnection connection, ItemInquirySearchCriteriaIfc criteria)
            throws DataException
    {
        // Make & Populate the result list of Items
        List<ItemInfoIfc> results = new ArrayList<ItemInfoIfc>();

        // Item information bean
        ItemInfoIfc info;

        // Maximum matches for the serach
        int maxMatches = criteria.getMaximumMatches();

        // Get the parent group ids
        ArrayList<String> parentGrpIDs = criteria.getParentIDs();

        // Variable to identify kit items
        int kitHeaderCode = -1;

        // Sorted map which holds the result
        TreeMap<String, ItemInfoIfc> itemMap = new TreeMap<String, ItemInfoIfc>();

        String itemID = null;

        // Item Manufacturer ID
        String mfID = null;
        
        //Department ID
        String deptID = null;
        
        //Merchandise Hierarchy Group ID
        String mrhrcID = null;
        
        ResultSet rs = null;

        // Get the store number from search criteria
        String storeNumber = criteria.getStoreNumber();

        // Get the query for item search
        String query = buildItemSearchQuery(connection, parentGrpIDs, criteria);

        logger.info(query.toString());

        try
        {
            // execute the query and get result set
            connection.execute(query);
            rs = (ResultSet) connection.getResult();

            int index;

            while (rs.next())
            {
                index = 0;
                kitHeaderCode = -1;

                // Get ItemInfo object
                info = DomainGateway.getFactory().getItemInfoInstance();

                // Get item id
                itemID = getSafeString(rs, ++index);
                info.setItemID(itemID);

                // Get kit header code
                kitHeaderCode = rs.getInt(++index);
                info.setKitHeaderCode(kitHeaderCode);

                info.setUnitOfMeasureID(getSafeString(rs, ++index));

                // Get the manufacturer ID
                mfID = getSafeString(rs, ++index);
                info.setManufacturerID(mfID);

                // Get the department ID
                deptID = getSafeString(rs, ++index);
                info.setDepartmentID(deptID);
                
                //Get the merchandise hierarchy group ID
                mrhrcID = getSafeString(rs, ++index);
                
                
                
                // retrieve localized manufacturer name unconditionally
                retrieveManufacturerName(connection, criteria, info);
                
                //Retrieving I18N department description for non null deptID
                retrieveDepartmentDescription(connection, deptID, info);
                
                //Retrieving parent Groups in the hierarchy
                retrieveHierarchyInformation(connection, mrhrcID, criteria.getHierarchyID(), info);
                
                // add item object to result
                itemMap.put(itemID, info);
            }
            // see if data read exceeds maximum matches parameter
            if ((maxMatches > 0) && (itemMap.size() > maxMatches))
            {
                throw new DataException(DataException.RESULT_SET_SIZE,
                        "Too many records were found processing the result set in JdbcFetchAllLevelItems.");
            }
            // Get the item ids from the sorted map
            Set<String> key = itemMap.keySet();

            Iterator<String> iterator = key.iterator();

            /*
             * Iterate through the sorted map and add the result to an array
             * list, set current and regular prices of the item with promotion
             * end date.
             */
            while (iterator.hasNext())
            {
                itemID = String.valueOf(iterator.next());

                // get the item from map
                ItemInfoIfc item = itemMap.get(itemID);

                // Get item's description
                item = fetchItemDescriptions(connection, item, criteria.getLocaleRequestor());

                // set current,actual price and promotion end date
                item = fetchCurrentAndRegularPrice(connection, item, storeNumber, criteria.getLocaleRequestor());

                if (item.getManufacturerName() == null && item.getManufacturerID() != null
                        && !Util.isEmpty(item.getManufacturerID()))
                    setManufacturerName(connection, item.getManufacturerID(), criteria.getLocaleRequestor(), item);

                // set Unit of measure for the item
                if (item.getUnitOfMeasureID() != null && !Util.isEmpty(item.getUnitOfMeasureID()))
                    setUnitOfMeasure(connection, item.getUnitOfMeasureID(), criteria.getLocaleRequestor(), item);

                // get the item image
                getItemImage(connection, item);

                // get the item level messages
                getItemLevelMessages(connection, item);

                // add the item to result
                results.add(item);
            }
        }
        catch (DataException dataException)
        {
            throw dataException;
        }
        catch (Exception e)
        {
            logger.error(DataException.MSG_UNKNOWN+" "+ e.getMessage());
        }
        finally
        {
            DBUtils.getInstance().closeResultSet(rs);
        }
        if (results.isEmpty())
        {
            throw new DataException(DataException.NO_DATA,
                    "No PLU was found processing the result set in JdbcFetchAllLevelItems.");
        }

        // return the result
        return results.toArray(new ItemInfo[results.size()]);
    }

    /**
     * Builds the SQL Statement to select I18N department description
     *
     * @param codeList
     * @param entry
     * @return
     * @throws SQLException
     * @since 14.1
     */
    private SQLSelectStatement buildDepartmentDescriptionQuery(String deptID) throws DataException
    {
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_POS_DEPARTMENT_I8);

        sql.addColumn(FIELD_POS_DEPARTMENT_NAME);

        sql.addQualifier(FIELD_LOCALE, makeSafeString(LocaleMap.getBestMatch(userLocale).toString()));
        sql.addQualifier(FIELD_POS_DEPARTMENT_ID, makeSafeString(deptID));

        return sql;
    }
    
    /**
     * builds the query to get the ancestors for a merchandise hierarchy group
     * in the merchandise hierarchy
     * 
     * @param mrhrcID
     * @param functionID
     * @return
     * @throws DataException
     * @since 14.1
     */
    private SQLSelectStatement buildMerchandiseHierarchyQuery(String mrhrcID, int functionID) throws DataException
    {
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.setDistinctFlag(true);
        sql.addTable(TABLE_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8);
        sql.addTable(TABLE_MERCHANDISE_HIERARCHY_GROUP_I8, ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8);
        String nestedQuery = "(SELECT hry.ID_MRHRC_GP_CHLD, hry.ID_MRHRC_LV FROM  ST_ASCTN_MRHRC hry WHERE hry.ID_MRHRC_FNC = "
                + functionID
                + " START WITH hry.ID_MRHRC_GP_CHLD = "
                + makeSafeString(mrhrcID)
                + " CONNECT BY NOCYCLE PRIOR hry.ID_MRHRC_GP_PRNT = hry.ID_MRHRC_GP_CHLD)";
        sql.addTable(nestedQuery, ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION);

        sql.addColumn(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_NAME);
        sql.addColumn(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID);
        sql.addColumn(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_LEVEL_NAME);
        sql.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID, ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION,
                FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID+"+1");
        sql.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_LOCALE,
                ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_LOCALE);

        sql.addQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_FUNCTION_ID, functionID);
        sql.addQualifier(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_LOCALE,
                makeSafeString(LocaleMap.getBestMatch(userLocale).toString()));
        sql.addJoinQualifier(ALIAS_MERCHANDISE_HIERARCHY_GROUP_I8, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID, ALIAS_MERCHANDISE_HIERARCHY_ASSOCIATION,
        		FIELD_CHILD_MERCHANDISE_HIERARCHY_GROUP_ID);
        sql.addOrdering(ALIAS_MERCHANDISE_HIERARCHY_LEVEL_V4_I8, FIELD_MERCHANDISE_HIERARCHY_LEVEL_ID);

        return sql;
    }
    
    /**
     * Retrieves an item's localized description
     *
     * @param connection The database connection
     * @param item The item
     * @param localeRequestor The locales to search
     */
    private ItemInfoIfc fetchItemDescriptions(JdbcDataConnection connection, ItemInfoIfc item,
            LocaleRequestor localeRequestor) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // Table to select from
        sql.addTable(TABLE_ITEM_I8);

        // add column
        sql.addColumn(FIELD_LOCALE);
        sql.addColumn(FIELD_ITEM_DESCRIPTION);

        // add identifier qualifier
        sql.addQualifier(FIELD_ITEM_ID, inQuotes(item.getItemID()));

        // add qualifier for locale
        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        try
        {
            // execute sql
            String sqlString = sql.getSQLString();
            connection.execute(sqlString);
            ResultSet rs = (ResultSet) connection.getResult();

            Locale locale = null;
            // parse result set
            while (rs.next())
            {
                locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, 1));
                item.setItemDescription(locale, getSafeString(rs, 2));
            }
            rs.close();
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "fetchItemDescriptions");
            throw new DataException(DataException.SQL_ERROR, "fetchItemDescriptions", se);
        }
        catch (DataException de)
        {
            // not found is regarded to be Ok here
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "fetchItemDescriptions", e);
        }
        return item;
    }

    /**
     * Sets item's Unit of measure
     *
     * @param connection The database connection
     * @param uomID The unit of measure's ID
     * @param localeRequestor The LocaleRequestor object.
     * @param item The ItemInfo object
     * @return String The Unit of measure
     */
    private String setUnitOfMeasure(JdbcDataConnection connection, String uomID, LocaleRequestor localeRequestor,
            ItemInfoIfc item) throws DataException
    {
        String methodName = "JdbcMerchandiseHierarchyDataOperation.setUnitOfMeasure()";

        // UOM
        String uom = "";

        SQLSelectStatement stmt = new SQLSelectStatement();

        // Add UOM Name coloumn
        stmt.addColumn(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_LOCALE);
        stmt.addColumn(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_UNIT_OF_MEASURE_NAME);

        // Add UOM I18N table
        stmt.addTable(TABLE_UNIT_OF_MEASURE_I8, ALIAS_UNIT_OF_MEASURE_I8);
        // add qualifier for locale
        stmt.addQualifier(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        // Add UOM ID where clause
        stmt.addQualifier(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_UNIT_OF_MEASURE_CODE, makeSafeString(uomID));

        // Get Unit of measure
        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet result = (ResultSet) connection.getResult();

            while (result.next())
            {
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(result, 1));
                uom = result.getString(FIELD_UNIT_OF_MEASURE_NAME);
                item.setUnitOfMeasure(locale, uom);
            }

            result.close();

        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }

        // Return UOM
        return uom;
    }

    /**
     * Retrieve item manufacturer's name by manufacturer id
     *
     * @param connection The database connection
     * @param mfID The manufacturer ID
     * @param salLocale Store's database locale
     * @return String The manufacturer's name
     */
    private void setManufacturerName(JdbcDataConnection connection, String mfID, LocaleRequestor sqlLocale,
            ItemInfoIfc item) throws DataException
    {

        String methodName = "JdbcMerchandiseHierarchyDataOperation.getManufacturerName()";

        SQLSelectStatement stmt = new SQLSelectStatement();

        // add columns
        stmt.addColumn(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE);
        stmt.addColumn(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME);

        // Add manufacturer I18N table
        stmt.addTable(TABLE_ITEM_MANUFACTURER_I18N, ALIAS_ITEM_MANUFACTURER_I18N);

        // add qualifier for locale
        Set<Locale> bestMatches = LocaleMap.getBestMatch("", sqlLocale.getLocales());
        stmt.addQualifier(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE + " "
                + JdbcDataOperation.buildINClauseString(bestMatches));

        // Add manufacturer ID where clause
        stmt.addQualifier(FIELD_ITEM_MANUFACTURER_ID, mfID);

        // Get Manufacturer Name
        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet result = (ResultSet) connection.getResult();

            while (result.next())
            {
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(result, 1));
                String manufacturer = getSafeString(result, 2);
                item.setManufacturerName(locale, manufacturer);
            }

            result.close();

        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }

    }

    /**
     * Retrieves an item's price details
     *
     * @param connection The database connection
     * @param itemID The item number
     * @param storeNumber The store number
     * @return ItemInfoIfc The item information
     */
    private ItemInfoIfc fetchCurrentAndRegularPrice(JdbcDataConnection connection, ItemInfoIfc item,
            String storeNumber, LocaleRequestor localeRequestor) throws DataException
    {
        // formatting currency in ItemInfo as a string using the default locale
        Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);

        // Selling price of the item
        CurrencyIfc currentPrice = null;

        // Permanent price of the item
        CurrencyIfc actualPrice = null;

        // Get price change jdbc instance
        JdbcSelectPriceChange priceChange = new JdbcSelectPriceChange();

        // Build pluItem using itemID and StoreID
        PLUItemIfc pluItem = buildPLUItem(item.getItemID(), item.getKitHeaderCode(), storeNumber);

        // Temporary price change events
        PriceChangeIfc[] tempPriceChanges = null;

        if (item.getKitHeaderCode() != ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER)
        {
            // Get temporary price change events for the item
            tempPriceChanges = priceChange.readTemporaryPriceChanges(connection, pluItem, buildCalendar());

            // Get permanent price change events for the item
            PriceChangeIfc[] permanentPriceChanges = priceChange.readPermanentPriceChanges(connection, pluItem,
                    buildCalendar());

            // Set the price change events to plu item
            pluItem.setTemporaryPriceChanges(tempPriceChanges);
            pluItem.setPermanentPriceChanges(permanentPriceChanges);

            currentPrice = pluItem.getPrice();
            actualPrice = pluItem.getPermanentPrice();
        }

        // Set the current price of the item
        item.setCurrentPrice(currentPrice);

        // Set the actual price of the item
        item.setActualPrice(actualPrice);

        // If there are any promotions, get the promotion end date
        if (tempPriceChanges != null && tempPriceChanges.length >= 1)
        {
            item.setPromotionEndDate(pluItem.getTemporaryPriceChanges()[0].getPriceMaintenanceEvent()
                    .getExpirationDateTimestamp().toFormattedString(defaultLocale));
        }

        // Return the item info
        return item;

    }

    /**
     * Retrieves kit item's actual price
     *
     * @param connection The database connection
     * @param itemID The item number
     * @param storeNumber The store number
     * @return String The actual price of kit item
     */
    private String fetchKitActualPrice(JdbcDataConnection connection, String itemID, String storeNumber)
            throws DataException
    {

        // Figure out the method name for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.getKitActualPrice()";

        String actualPrice = "";

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_RETAIL_STORE_ITEM, ALIAS_RETAIL_STORE_ITEM);
        sql.addTable(TABLE_ITEM_COLLECTION, ALIAS_ITEM_COLLECTION);

        sql.addColumn("SUM(" + FIELD_ITEM_PER_ASSEMBLY_COUNT + "*" + FIELD_PERMANENT_SALE_UNIT_RETAIL_PRICE_AMOUNT
                + ") " + ALIAS_KIT_ACTUAL_PRICE);

        sql.addQualifier(ALIAS_ITEM_COLLECTION + "." + FIELD_ITEM_ID + "=" + makeSafeString(itemID));
        sql.addQualifier(FIELD_RETAIL_STORE_ID + "=" + makeSafeString(storeNumber));
        sql.addJoinQualifier(ALIAS_RETAIL_STORE_ITEM + "." + FIELD_ITEM_ID, ALIAS_ITEM_COLLECTION + "."
                + FIELD_ITEM_COLLECTION_MEMBER_COLLECTION);

        try
        {
            // System.out.println("Kit Regular price " + sql.getSQLString());

            connection.execute(sql.getSQLString());
            ResultSet result = (ResultSet) connection.getResult();

            if (result.next())
            {
                actualPrice = result.getString(ALIAS_KIT_ACTUAL_PRICE);
            }

            result.close();

            // Return actual price
            return actualPrice;

        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }

    }

    /**
     * Retrieves item's actual price
     *
     * @param connection The database connection
     * @param criteria The search criteria
     * @return String The actual price of an item
     */
    protected String fetchItemActualPrice(JdbcDataConnection connection, ItemInquirySearchCriteriaIfc criteria)
            throws DataException
    {

        // Figure out the method name for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.getActualPrice()";

        String actualPrice = "";

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_RETAIL_STORE_ITEM, ALIAS_RETAIL_STORE_ITEM);

        sql.addColumn(FIELD_PERMANENT_SALE_UNIT_RETAIL_PRICE_AMOUNT);

        sql.addQualifier(FIELD_ITEM_ID + "=" + makeSafeString(criteria.getItemID()));
        sql.addQualifier(FIELD_RETAIL_STORE_ID + "=" + makeSafeString(criteria.getStoreNumber()));

        /*
         * Select AS_ITM_RTL_STR.RP_PR_SLS + FROM AS_ITM_RTL_STR + WHERE
         * ID_STR_RT = ? and ID_ITM = ?
         */

        try
        {

            // System.out.println("actual price query" + sql.getSQLString());
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            if (rs.next())
            {
                actualPrice = rs.getString(FIELD_PERMANENT_SALE_UNIT_RETAIL_PRICE_AMOUNT);
            }

            rs.close();

            // return item's actual price
            return actualPrice;

        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }

    }

    /**
     * Retrieves item's promotion end date
     *
     * @param connection The database connection
     * @param criteria The search criteria
     * @return String The promotion end date
     */
    protected String fetchPromotionEndDate(JdbcDataConnection connection, ItemInquirySearchCriteriaIfc criteria)
            throws DataException
    {
        // Figure out the method name for Exception reporting
        String methodName = "JdbcMerchandiseHierarchyDataOperation.getPromotionEndDate()";

        SQLSelectStatement sql = new SQLSelectStatement();

        String promoEndDate = "";

        // add promotion tables
        sql.addTable(TABLE_PRICE_DERIVATION_RULE, ALIAS_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY);

        // add end date coloumn
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE);

        // add qualifiers
        sql.addQualifier(ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID + " = "
                + makeSafeString(criteria.getStoreNumber()));
        sql.addQualifier(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_RETAIL_STORE_ID + " = "
                + makeSafeString(criteria.getStoreNumber()));
        sql.addQualifier(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_ITEM_ID + "="
                + makeSafeString(criteria.getItemID()));

        sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID,
                ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_PRICE_DERIVATION_RULE_ID);

        sql.addQualifier("UPPER(" + FIELD_PRICE_DERIVATION_RULE_STATUS_CODE + ") = 'ACTIVE'");
        sql.addQualifier(currentTimestampRangeCheckingString(FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE,
                FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE));

        /*
         * slqString = SELECT DC_RU_PRDV_EF endDate + FROM
         * RU_PRDV,CO_EL_PRDV_ITM where CO_EL_PRDV_ITM.ID_STR_RT = ? + AND
         * CO_EL_PRDV_ITM.ID_ITM = ? + AND RU_PRDV.ID_RU_PRDV =
         * CO_EL_PRDV_ITM.ID_RU_PRDV + AND RU_PRDV.ID_STR_RT= ? + AND SC_RU_PRDV =
         * 'Active' + AND DC_RU_PRDV_EP <= SYSDATE and DC_RU_PRDV_EF >= SYSDATE
         */

        try
        {
            connection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet) connection.getResult();

            if (rs.next())
            {
                promoEndDate = rs.getString(1);
            }

            rs.close();

            // return promotion end date
            return promoEndDate;

        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, methodName, e);
        }

    }

    /**
     * Builds query for avanced item search
     *
     * @param connection The database connection
     * @param parentGrpIDs The parent group ids
     * @param criteria The search criteria
     * @return String The item search query
     */
    protected String buildItemSearchQuery(JdbcDataConnection connection, ArrayList<String> parentGrpIDs,
            ItemInquirySearchCriteriaIfc criteria) throws DataException
    {

        int inListStart;
        int inListEnd;
        boolean isAvailable = true;

        SQLSelectStatement stmt;

        // query buffer
        StringBuilder query = new StringBuilder();

        // buffer for temporary query
        StringBuilder tempBuffer = new StringBuilder();

        if (criteria.isSerachItemAtRootLevel())
        {

            stmt = buildSubQuery(criteria);

            query.append(stmt.getSQLString());

        }

        else
        {
            // list of group ids
            List<String> childrenList;
            List<String> finalList = new ArrayList<String>();

            for (Iterator<String> iterator = parentGrpIDs.iterator(); iterator.hasNext();)
            {

                String parentID = iterator.next();
                // Include the parent group id for the search
                finalList.add(parentID);
                // Get the direct children group ids for a parent
                childrenList = doFetchChildrenGroupIDs(connection, criteria.getHierarchyID(), parentID);
                while (!childrenList.isEmpty())
                {
                    List<String> grandChildrenList = new ArrayList<String>();

                    // Loop through the list of groupIDs getting the actual
                    // groups
                    for (String childID : childrenList)
                    {
                        grandChildrenList
                                .addAll(doFetchChildrenGroupIDs(connection, criteria.getHierarchyID(), childID));
                        finalList.add(childID);
                    }
                    childrenList = grandChildrenList;
                }

                stmt = buildSubQuery(criteria);

                if (parentGrpIDs.size() > 0)
                {
                    int listSize = finalList.size();

                    /*
                     * Check if number of groups exceeds database in clause
                     * limit,if exceeds break up the in clause
                     */
                    if (finalList.size() > IN_CLAUSE_MAX)
                    {
                        inListStart = 0;
                        inListEnd = IN_CLAUSE_MAX;

                        // Iterate through the group ids and add to in class
                        while (isAvailable)
                        {
                            tempBuffer.append(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID + " IN "
                                    + convertToSetString(finalList.subList(inListStart, inListEnd)));
                            inListStart = inListEnd;

                            if ((inListEnd + IN_CLAUSE_MAX) > listSize)
                            {
                                inListEnd = listSize;
                                isAvailable = false;
                            }
                            else
                            {
                                tempBuffer.append(" OR ");
                                inListEnd += IN_CLAUSE_MAX;
                            }

                        }

                        stmt.addQualifier(tempBuffer.toString());
                    }
                    else
                    {
                        stmt
                                .addQualifier(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID + " IN "
                                        + convertToSetString(finalList));
                    }
                }

                query.append(stmt.getSQLString());

                if (iterator.hasNext())
                {
                    query.append(" UNION ALL ");
                }

            }

        }

        // the result will be stored in a TreeMap, so ORDER BY is commented
        // query.append(" ORDER BY 1");

        /*
         * SELECT ITM.ID_ITM, DE_ITM, RP_SLS_POS_CRT, NM_MF FROM AS_ITM ITM,
         * ID_IDN_PS POSID, PA_MF MF WHERE POSID.ID_ITM = ITM.ID_ITM AND
         * POSID.ID_ITM LIKE ? AND UPPER(DE_ITM) LIKE UPPER(?) AND UPPER(NM_MF)
         * LIKE UPPER(?) AND MF.ID_MF = ITM.ID_MF AND ID_MRHRC_GP IN (?) UNION
         * ALL SELECT ITM.ID_ITM ...... ORDER BY 1
         */

        return query.toString();
    }

    /**
     * Builds sub-query for avanced item search
     *
     * @param criteria The search criteria
     * @return String The item search query
     */
    private SQLSelectStatement buildSubQuery(ItemInquirySearchCriteriaIfc criteria) throws DataException
    {

        // get the sql locale from criteria
        LocaleRequestor localeRequestor = criteria.getLocaleRequestor();

        String itemTypeCode = criteria.getItemTypeCode();
        String itemUOMCode = criteria.getItemUOMCode();
        String itemStyleCode = criteria.getItemStyleCode();
        String itemColorCode = criteria.getItemColorCode();
        String itemSizeCode = criteria.getItemSizeCode();

        SQLSelectStatement stmt = new SQLSelectStatement();

        // add tables
        stmt.addTable(TABLE_POS_IDENTITY, ALIAS_POS_IDENTITY);
        stmt.addTable(TABLE_ITEM, ALIAS_ITEM);
        stmt.addTable(TABLE_STOCK_ITEM, ALIAS_STOCK_ITEM);


        // add coloumns
        stmt.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_ID);
        stmt.addColumn(FIELD_ITEM_KIT_SET_CODE);
        stmt.addColumn(FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE);
        stmt.addColumn(ALIAS_ITEM, FIELD_ITEM_MANUFACTURER_ID);
        stmt.addColumn(ALIAS_ITEM, FIELD_POS_DEPARTMENT_ID);
        stmt.addColumn(ALIAS_ITEM, FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);

        stmt.addJoinQualifier(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, ALIAS_ITEM, FIELD_ITEM_ID);
        stmt.addJoinQualifier(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, ALIAS_STOCK_ITEM, FIELD_ITEM_ID);

        // add item id qualifier
        if (criteria.getItemID() != null)
        {
            String itemID = criteria.getItemID();
            if (itemID.indexOf(UI_WILD_CARD) != -1 || itemID.indexOf(DB_WILD_CARD) != -1)
            {
                stmt.addQualifier("(" + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID +" LIKE "
                        + makeSafeString(replaceStar(itemID)) + " OR " + ALIAS_ITEM + "." + FIELD_ITEM_ID + " LIKE" + makeSafeString(replaceStar(itemID))+ ")");
            }
            else
            {
                stmt.addQualifier("(" + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = "
                        + makeSafeString(itemID) + " OR " + ALIAS_ITEM + "." + FIELD_ITEM_ID + " = " + makeSafeString(replaceStar(itemID))+ ")");

            } 
        }

        Set<Locale> bestMatches = LocaleMap.getBestMatch("", localeRequestor.getLocales());

        // add item description qualifier
        if (criteria.getDescription() != null)
        {
            String itemDescription = criteria.getDescription();

            stmt.addTable(TABLE_ITEM_I8, ALIAS_ITEM_I8);

            stmt.addJoinQualifier(ALIAS_ITEM, FIELD_ITEM_ID, ALIAS_ITEM_I8, FIELD_ITEM_ID);

            if (itemDescription.indexOf(UI_WILD_CARD) != -1 || itemDescription.indexOf(DB_WILD_CARD) != -1)
            {
                stmt.addQualifier("UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ") LIKE UPPER("
                        + makeSafeString(replaceStar(itemDescription)) + ")");
            }
            else
            {

                stmt.addQualifier("UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ") LIKE UPPER("
                        + makeSafeString(DB_WILD_CARD + itemDescription + DB_WILD_CARD) + ")");
            }

            if (criteria.getSearchLocale() != null)
            {
                stmt.addQualifier(ALIAS_ITEM_I8 + "." + FIELD_LOCALE + " = '"
                        + LocaleMap.getBestMatch(criteria.getSearchLocale()).toString() + "'");
            }
            else
            {
                stmt.addQualifier(ALIAS_ITEM_I8 + "." + FIELD_LOCALE + " "
                        + JdbcDataOperation.buildINClauseString(bestMatches));
            }
        }

        // add store number qualifier
        if (criteria.getStoreNumber() != null)
        {
            stmt.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID + " = "
                    + makeSafeString(criteria.getStoreNumber()));
        }

        if (itemTypeCode != null && !itemTypeCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_ITEM + "." + FIELD_ITEM_TYPE_CODE + " = " + inQuotes(itemTypeCode));
        }

        if (itemUOMCode != null && !itemUOMCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE + " = "
                    + inQuotes(itemUOMCode));
        }
        if (itemStyleCode != null && !itemStyleCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_STYLE_CODE + " = " + inQuotes(itemStyleCode));
        }
        if (itemColorCode != null && !itemColorCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_COLOR_CODE + " = " + inQuotes(itemColorCode));
        }
        if (itemSizeCode != null && !itemSizeCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_SIZE_CODE + " = " + inQuotes(itemSizeCode));
        }
        
        // if search by manufacturer enabled, add manufacturer qualifier
        if (criteria.getManufacturer() != null)
        {

            String manufacturerName = criteria.getManufacturer();
            // Get Manufacturer Name if searching by manufacturer id
            stmt.addTable(TABLE_ITEM_MANUFACTURER, ALIAS_ITEM_MANUFACTURER);
            stmt.addTable(TABLE_ITEM_MANUFACTURER_I18N, ALIAS_ITEM_MANUFACTURER_I18N);
            
            stmt.addJoinQualifier(ALIAS_ITEM_MANUFACTURER, FIELD_ITEM_MANUFACTURER_ID, ALIAS_ITEM_MANUFACTURER_I18N,
                    FIELD_ITEM_MANUFACTURER_ID);

            if (manufacturerName.indexOf(UI_WILD_CARD) != -1 || manufacturerName.indexOf(DB_WILD_CARD) != -1)
            {
                stmt.addQualifier("UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME
                        + ") LIKE UPPER(" + makeSafeString(replaceStar(manufacturerName)) + ")");
            }
            else
            {
                stmt.addQualifier("UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME
                        + ") LIKE UPPER(" + makeSafeString(DB_WILD_CARD + manufacturerName + DB_WILD_CARD) + ")");

            }

            if (criteria.getSearchLocale() != null)
            {
                stmt.addQualifier(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE + " = '"
                        + LocaleMap.getBestMatch(criteria.getSearchLocale()).toString() + "'");
            }
            else
            {
                stmt.addQualifier(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE + " "
                        + JdbcDataOperation.buildINClauseString(bestMatches));
            }

            stmt.addJoinQualifier(ALIAS_ITEM_MANUFACTURER, FIELD_ITEM_MANUFACTURER_ID, ALIAS_ITEM,
                    FIELD_ITEM_MANUFACTURER_ID);
        }
        
        if (criteria.isSearchItemByDiscountable())
        {
            int discountable = criteria.isDiscountable() ? 1 : 0;
            stmt.addQualifier(ALIAS_ITEM + "." + FIELD_ITEM_DISCOUNT_FLAG + " = " + discountable);
        }

        if (criteria.isSearchItemByTaxable())
        {
            int taxable = criteria.isTaxable() ? 1 : 0;
            stmt.addQualifier(ALIAS_ITEM + "." + FIELD_ITEM_TAX_EXEMPT_CODE + " = " + taxable);
        }
        
        return stmt;

    }

    /**
     * Builds sub-query for avanced item manufacturer search
     *
     * @param criteria The search criteria
     * @return String The item search query
     */
    private String buildItemManufacturerSearchQuery(ItemInquirySearchCriteriaIfc criteria) throws DataException
    {

        // get the sql locale from criteria
        LocaleRequestor localeRequestor = criteria.getLocaleRequestor();

        // query buffer
        StringBuilder query = new StringBuilder();

        String itemTypeCode = criteria.getItemTypeCode();
        String itemUOMCode = criteria.getItemUOMCode();
        String itemStyleCode = criteria.getItemStyleCode();
        String itemColorCode = criteria.getItemColorCode();
        String itemSizeCode = criteria.getItemSizeCode();

        SQLSelectStatement stmt = new SQLSelectStatement();

        // add tables
        stmt.addTable(TABLE_POS_IDENTITY, ALIAS_POS_IDENTITY);
        stmt.addTable(TABLE_ITEM, ALIAS_ITEM);
        stmt.addTable(TABLE_STOCK_ITEM, ALIAS_STOCK_ITEM);
        stmt.addTable(TABLE_ITEM_MANUFACTURER, ALIAS_ITEM_MANUFACTURER);
        stmt.addTable(TABLE_ITEM_MANUFACTURER_I18N, ALIAS_ITEM_MANUFACTURER_I18N);

        // Set distinct flag to true
        stmt.setDistinctFlag(true);
        // add coloumns
        stmt.addColumn(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE);
        stmt.addColumn(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME);

        stmt.addJoinQualifier(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, ALIAS_ITEM, FIELD_ITEM_ID);
        stmt.addJoinQualifier(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, ALIAS_STOCK_ITEM, FIELD_ITEM_ID);

        // add item id qualifier
        if (criteria.getItemID() != null)
        {
            String itemID = criteria.getItemID();
            if (itemID.indexOf(UI_WILD_CARD) != -1)
            {
                stmt.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " LIKE "
                        + makeSafeString(replaceStar(itemID)) );
            }
            else
            {
                stmt.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = "
                        + makeSafeString(itemID));

            }
        }

        // add store number qualifier
        if (criteria.getStoreNumber() != null)
        {
            stmt.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID + " = "
                    + makeSafeString(criteria.getStoreNumber()));
        }

        if (itemTypeCode != null && !itemTypeCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_ITEM + "." + FIELD_ITEM_TYPE_CODE + " = " + inQuotes(itemTypeCode));
        }

        if (itemUOMCode != null && !itemUOMCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE + " = "
                    + inQuotes(itemUOMCode));
        }
        if (itemStyleCode != null && !itemStyleCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_STYLE_CODE + " = " + inQuotes(itemStyleCode));
        }
        if (itemColorCode != null && !itemColorCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_COLOR_CODE + " = " + inQuotes(itemColorCode));
        }
        if (itemSizeCode != null && !itemSizeCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_SIZE_CODE + " = " + inQuotes(itemSizeCode));
        }

        // if search by manufacturer enabled, add manufacturer qualifier
        if (criteria.getManufacturer() != null)
        {

            String manufacturerName = criteria.getManufacturer();
            // Get Manufacturer Name if searching by manufacturer id

            stmt.addTable(TABLE_ITEM_MANUFACTURER_I18N, ALIAS_ITEM_MANUFACTURER_I18N);
            if (manufacturerName.indexOf(UI_WILD_CARD) != -1)
            {
                stmt.addQualifier("UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME
                        + ") LIKE UPPER(" + makeSafeString(replaceStar(manufacturerName)) + ")");
            }
            else
            {
                stmt.addQualifier("UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME
                        + ") LIKE UPPER(" + makeSafeString(DB_WILD_CARD + manufacturerName + DB_WILD_CARD) + ")");

            }

            Set<Locale> bestMatches = LocaleMap.getBestMatch("", localeRequestor.getLocales());

            stmt.addQualifier(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_LOCALE + " "
                    + JdbcDataOperation.buildINClauseString(bestMatches));

        }

        stmt.addJoinQualifier(ALIAS_ITEM_MANUFACTURER, FIELD_ITEM_MANUFACTURER_ID, ALIAS_ITEM,
                FIELD_ITEM_MANUFACTURER_ID);
        stmt.addJoinQualifier(ALIAS_ITEM_MANUFACTURER_I18N, FIELD_ITEM_MANUFACTURER_ID, ALIAS_ITEM,
                FIELD_ITEM_MANUFACTURER_ID);

        query.append(stmt.getSQLString());

        return query.toString();

    }

    /**
     * Builds sub-query for avanced item description search
     *
     * @param criteria The search criteria
     * @return String The item search query
     * @deprecated since 14.1 as it is not being used
     */
    private String buildItemDescriptionSearchQuery(ItemInquirySearchCriteriaIfc criteria) throws DataException
    {

        // get the sql locale from criteria
        LocaleRequestor localeRequestor = criteria.getLocaleRequestor();

        // query buffer
        StringBuilder query = new StringBuilder();

        String itemTypeCode = criteria.getItemTypeCode();
        String itemUOMCode = criteria.getItemUOMCode();
        String itemStyleCode = criteria.getItemStyleCode();
        String itemColorCode = criteria.getItemColorCode();
        String itemSizeCode = criteria.getItemSizeCode();

        SQLSelectStatement stmt = new SQLSelectStatement();

        // add tables
        stmt.addTable(TABLE_POS_IDENTITY, ALIAS_POS_IDENTITY);
        stmt.addTable(TABLE_ITEM, ALIAS_ITEM);
        stmt.addTable(TABLE_STOCK_ITEM, ALIAS_STOCK_ITEM);
        stmt.addTable(TABLE_ITEM_I8, ALIAS_ITEM_I8);

        stmt.setDistinctFlag(true);
        // add coloumns
        stmt.addColumn(ALIAS_ITEM_I8 + "." + FIELD_LOCALE);
        stmt.addColumn(ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION);

        stmt.addJoinQualifier(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, ALIAS_ITEM, FIELD_ITEM_ID);
        stmt.addJoinQualifier(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, ALIAS_STOCK_ITEM, FIELD_ITEM_ID);

        // add item id qualifier
        if (criteria.getItemID() != null)
        {
            String itemID = criteria.getItemID();
            if (itemID.indexOf(UI_WILD_CARD) != -1)
            {
                stmt.addQualifier( ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " LIKE "
                        + makeSafeString(replaceStar(itemID)));
            }
            else
            {
                stmt.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = "
                        + makeSafeString(itemID));

            }
        }

        // add store number qualifier
        if (criteria.getStoreNumber() != null)
        {
            stmt.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID + " = "
                    + makeSafeString(criteria.getStoreNumber()));
        }

        if (itemTypeCode != null && !itemTypeCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_ITEM + "." + FIELD_ITEM_TYPE_CODE + " = " + inQuotes(itemTypeCode));
        }

        if (itemUOMCode != null && !itemUOMCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE + " = "
                    + inQuotes(itemUOMCode));
        }
        if (itemStyleCode != null && !itemStyleCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_STYLE_CODE + " = " + inQuotes(itemStyleCode));
        }
        if (itemColorCode != null && !itemColorCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_COLOR_CODE + " = " + inQuotes(itemColorCode));
        }
        if (itemSizeCode != null && !itemSizeCode.equals("-1"))
        {
            stmt.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_SIZE_CODE + " = " + inQuotes(itemSizeCode));
        }

        // add item description qualifier
        if (criteria.getDescription() != null)
        {
            String itemDescription = criteria.getDescription();

            if (itemDescription.indexOf(UI_WILD_CARD) != -1)
            {
                stmt.addQualifier("UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ") LIKE UPPER("
                        + makeSafeString(replaceStar(itemDescription)) + ")");
            }
            else
            {

                stmt.addQualifier("UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ") LIKE UPPER("
                        + makeSafeString(DB_WILD_CARD + itemDescription + DB_WILD_CARD) + ")");
            }

            Set<Locale> bestMatches = LocaleMap.getBestMatch("", localeRequestor.getLocales());

            stmt.addQualifier(ALIAS_ITEM_I8 + "." + FIELD_LOCALE + " "
                    + JdbcDataOperation.buildINClauseString(bestMatches));
        }

        stmt.addJoinQualifier(ALIAS_ITEM_I8, FIELD_ITEM_ID, ALIAS_ITEM, FIELD_ITEM_ID);

        query.append(stmt.getSQLString());

        return query.toString();

    }

    /**
     * Returns default display string.
     * <P>
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return "JdbcMerchandiseHierarchyDataOperation (Revision " + getRevisionNumber() + ")";
    }

    /**
     * Retrieves the source-code-control system revision number.
     * <P>
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    private String convertToSetString(List<String> groupIDs)
    {
        StringBuilder buff = new StringBuilder("(");
        for (Iterator<String> iter = groupIDs.iterator(); iter.hasNext();)
        {
            buff.append(makeSafeString(iter.next().toString()));
            if (iter.hasNext())
                buff.append(",");
        }
        buff.append(")");
        return buff.toString();
    }

    /**
     * Replaces '*' to '%'
     *
     * @param string oldtext
     * @return String modified String
     */
    protected String replaceStar(String oldtext)
    {

        return replaceStar(oldtext, UI_WILD_CARD.charAt(UI_WILD_CARD.length() - 1), DB_WILD_CARD.charAt(UI_WILD_CARD
                .length() - 1));
    }

    /**
     * Replaces '%' to '*'
     *
     * @param string oldtext
     * @param char dbWildCard
     * @param char uiWildCard
     * @return String modified string
     */
    private String replaceStar(String oldtext, char dbWildCard, char uiWildCard)
    {
        // new string after wild card symbol replac3
        String newtext = null;

        if (oldtext != null && !(oldtext.length() == 0))
        {
            newtext = oldtext.replace(dbWildCard, uiWildCard);
        }

        return newtext;
    }

    /**
     * Returns the PLUItem object
     *
     * @param itemId The ItemID of the object
     * @param storeId The storeId for the item
     * @return PLUItem representation of the item
     */
    private PLUItemIfc buildPLUItem(String itemId, int kitHeaderCode, String storeId)
    {
        PLUItemIfc pluItem;

        if (kitHeaderCode == ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER)
        {
            pluItem = DomainGateway.getFactory().getItemKitInstance();
        }
        else
        {
            pluItem = DomainGateway.getFactory().getPLUItemInstance();
        }

        pluItem.setItemID(itemId);
        pluItem.setStoreID(storeId);
        return pluItem;
    }

    /**
     * Returns the Calendar with current system time
     */
    private Calendar buildCalendar()
    {
        java.util.Date date = new Date(System.currentTimeMillis());
        Calendar when = Calendar.getInstance();
        when.setTime(date);
        return when;
    }

    /**
     * Method which gets the ILRM Message for a Given Item The Catch block
     * simply prints the exception caused during execution as the requirement is
     * to just print the error not propogate it
     *
     * @param dataConnection
     * @param item
     * @throws DataException
     */
    private void getItemLevelMessages(JdbcDataConnection dataConnection, ItemInfoIfc item)
    {
        if (item != null)
        {
            SQLSelectStatement sql = new SQLSelectStatement();
            // PreparedStatement stmtILRM= null;
            // Connection ilrmConnection = dataConnection.getConnection();
            MessageDTO mdto = new MessageDTO();;
            List<MessageDTO> messageList = new ArrayList<MessageDTO>();
            Map<String, List<MessageDTO>> messagesMap = new HashMap<String, List<MessageDTO>>(1);
            ResultSet rsILRM = null;

            // add tables
            sql.addTable(TABLE_ITEM_MESSAGE_ASSOCIATION);
            sql.addTable(TABLE_ASSET_MESSAGES);
            sql.addTable(TABLE_ASSET_MESSAGES_I18N);

            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TYPE);
            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_CODE_ID);
            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TRANSACTION_TYPE);
            sql.addColumn(TABLE_ASSET_MESSAGES_I18N, FIELD_LOCALE);
            sql.addColumn(TABLE_ASSET_MESSAGES_I18N, FIELD_MESSAGE_DESCRIPTION);
            // add columns from related item association

            // add qualifiers //TODO change ITEM_ID below to the IFC name
            sql.addQualifier(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_ITEM_ID, "'" + item.getItemID() + "'");
            sql.addJoinQualifier(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES,
                    FIELD_MESSAGE_CODE_ID);
            sql.addJoinQualifier(TABLE_ASSET_MESSAGES, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES_I18N,
                    FIELD_MESSAGE_CODE_ID);
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
                rsILRM = (ResultSet) dataConnection.getResult();

                while (rsILRM.next())
                {
                    if (transactionType != null
                            && !transactionType.equalsIgnoreCase(rsILRM.getString(FIELD_MESSAGE_TRANSACTION_TYPE)))
                    {
                        messageList.add(mdto);
                        messagesMap.put(transactionType, messageList);
                        messageList = null;
                        messageType = null;
                        messageList = new ArrayList<MessageDTO>();
                    }

                    if (messageType != null && messageType.equalsIgnoreCase(rsILRM.getString(FIELD_MESSAGE_TYPE)))
                    {
                        mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rsILRM.getString(FIELD_LOCALE)), rsILRM
                                .getString(FIELD_MESSAGE_DESCRIPTION));
                        continue;
                    }
                    else if (messageType != null && !messageType.equalsIgnoreCase(rsILRM.getString(FIELD_MESSAGE_TYPE)))
                    {
                        messageList.add(mdto);
                    }

                    messageType = rsILRM.getString(FIELD_MESSAGE_TYPE);

                    mdto = new MessageDTO();
                    mdto.setDefaultItemMessage(rsILRM.getString(FIELD_MESSAGE_DESCRIPTION));
                    mdto.setItemMessageCodeID(rsILRM.getString(FIELD_MESSAGE_CODE_ID));
                    mdto.setItemMessageTransactionType(rsILRM.getString(FIELD_MESSAGE_TRANSACTION_TYPE));
                    mdto.setItemMessageType(messageType);
                    mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rsILRM.getString(FIELD_LOCALE)), rsILRM
                            .getString(FIELD_MESSAGE_DESCRIPTION));

                    logger.info(mdto.toString());
                    transactionType = rsILRM.getString(FIELD_MESSAGE_TRANSACTION_TYPE);
                }
                messageList.add(mdto);
                messagesMap.put(transactionType, messageList);
                item.setAllItemLevelMessages(messagesMap);
                rsILRM.close();

            }
            catch (DataException de)
            {
                logger.error("" + de + "");
            }
            catch (SQLException se)
            {
                logger.error(se);
            }
            catch (Exception e)
            {
                logger.error("Unexpected exception in readRelatedItems " + e);
            }

        }
    }

    /**
     * Method which gets the item image for a Given Item
     *
     * @param dataConnection
     * @param item
     * @throws DataException
     */
    private void getItemImage(JdbcDataConnection connection, ItemInfoIfc item)
        throws DataException
    {
        // get images
        PreparedStatement stmt = null;
        ResultSet imageResults = null;
        ItemImageIfc itemImage = DomainGateway.getFactory().getItemImageInstance();

        try
        {
            SQLSelectStatement imgStmt = new SQLSelectStatement();
            Connection descConnection = connection.getConnection();
            imgStmt.addTable(TABLE_ITEM_IMAGE, ALIAS_IMAGE);
            imgStmt.addColumn(ALIAS_IMAGE, FIELD_ITEM_IMAGE_LOCATION);
            imgStmt.addColumn(ALIAS_IMAGE, FIELD_ITEM_IMAGE_BLOB);
            imgStmt.addQualifier(FIELD_ITEM_ID, makeSafeString(item.getItemID()));

            stmt = descConnection.prepareStatement(imgStmt.getSQLString());
            imageResults = stmt.executeQuery();

            while (imageResults.next())
            {
                itemImage.setImageLocation(imageResults.getString(1));
                DatabaseBlobHelperIfc blob = DatabaseBlobHelperFactory.getInstance().getDatabaseBlobHelper(connection.getConnection());
                itemImage.setImageBlob(blob.loadBlob(imageResults, FIELD_ITEM_IMAGE_BLOB));
            }

        }

        catch (SQLException se)
        {
            logger.warn(se.toString());
        }
        finally
        {
            item.setItemImage(itemImage);
            DBUtils.getInstance().closeResultSet(imageResults);
            DBUtils.getInstance().closeStatement(stmt);
        }
    }
    
    /**
     * This method retrieves the localized manufacturer name for
     * for a search criteria and set into the {@link ItemInfo} object
     * @throws DataException 
     * @since 14.1
     */
    protected void retrieveManufacturerName(JdbcDataConnection connection, ItemInquirySearchCriteriaIfc criteria, ItemInfoIfc info) throws DataException
    {
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        // retrieve localized manufacturer name unconditionally
        try
        {
            // Get manufacturer's name
            String manufacturerQuery = buildItemManufacturerSearchQuery(criteria);
            Connection manfConnection = connection.getConnection();

            ps = manfConnection.prepareStatement(manufacturerQuery);
            resultSet = ps.executeQuery();;

            while (resultSet.next())
            {
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(resultSet, 1));
                String mfname = getSafeString(resultSet, 2);
                info.setManufacturerName(locale, mfname);

            }
            resultSet.close();
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            logger.error(DataException.MSG_SQL_ERROR + "while retrieving the localized Manufacturer name");
         }
        catch (Exception e)
        {
            logger.error(DataException.MSG_UNKNOWN +  "while retrieving the localized Manufacturer name");
        }
        finally
        {
            DBUtils.getInstance().closeResultSet(resultSet);
            DBUtils.getInstance().closeStatement(ps);
        }

    }
    
    /**
     * This method retrieves the localized
     * department description for the item and the locale
     * specified in the search criteria and set into the {@link ItemInfo} object
     * @throws DataException 
     * @since 14.1
     */
    protected void retrieveDepartmentDescription(JdbcDataConnection connection, String deptID, ItemInfoIfc info) throws DataException
    {
        // Retrieving I18N department description for non null deptID
        if (deptID != null)
        {
            ResultSet deptResultSet = null;
            PreparedStatement ps = null;
            try
            {
                String departmentI8NQuery = buildDepartmentDescriptionQuery(deptID).getSQLString();
                Connection deptI8NConnection = connection.getConnection();
                ps = deptI8NConnection.prepareStatement(departmentI8NQuery);
                deptResultSet = ps.executeQuery();

                while (deptResultSet.next())
                {
                    String deptI8NDescription = getSafeString(deptResultSet, 1);
                    info.setDepartmentDescription(deptI8NDescription);

                }
                deptResultSet.close();
            }
            catch (DataException de)
            {
                logger.warn(de.toString());
                throw de;
            }
            catch (SQLException se)
            {
                logger.error(DataException.MSG_SQL_ERROR + "while retrieving the localized Department description for a given "+
                "department Id "   +   deptID + " and UI locale");
            }
            catch (Exception e)
            {
                logger.error(DataException.MSG_UNKNOWN + "while retrieving the localized Department description for a given "+
                        "department Id "   +  deptID + " and UI locale");
            }
            finally
            {
                DBUtils.getInstance().closeResultSet(deptResultSet);
                DBUtils.getInstance().closeStatement(ps);
            }
        }
        else
        {
            info.setDepartmentDescription("");
            logger.info("department id is null. Setting department description to blank");
        }
    }
    
    /**
     * sets the hierarchy information for an item
     * 
     * @param connection
     * @param mrhrcID
     * @param functionID
     * @param info
     * @throws DataException
     * @since 14.1
     */
    protected void retrieveHierarchyInformation(JdbcDataConnection connection, String mrhrcID, int functionID,
            ItemInfoIfc info) throws DataException
    {
        List<String[]> parentGroups = new ArrayList<String[]>();
        if (mrhrcID != null)
        {
            ResultSet mrhrcResultSet = null;
            PreparedStatement ps = null;
            try
            {
                String mrhrcQuery = buildMerchandiseHierarchyQuery(mrhrcID, functionID).getSQLString();
                Connection mrhrcConnection = connection.getConnection();
                ps = mrhrcConnection.prepareStatement(mrhrcQuery);
                mrhrcResultSet = ps.executeQuery();

                while (mrhrcResultSet.next())
                {
                    String groupName = getSafeString(mrhrcResultSet, 1);
                    BigDecimal level = getBigDecimal(mrhrcResultSet, 2);
                    String levelName = getSafeString(mrhrcResultSet, 3);
                    parentGroups.add(new String[] { groupName, level.toString(), levelName });

                }
                info.setParentGroups(parentGroups);
                mrhrcResultSet.close();
            }
            catch (DataException de)
            {
                logger.warn(de.toString());
                throw de;
            }
            catch (SQLException se)
            {
                logger.error(DataException.MSG_SQL_ERROR + "while retrieving the parent groups for a given "
                        + "merchandise hierarchy group Id " + mrhrcID + " and UI locale");
            }
            catch (Exception e)
            {
                logger.error(DataException.MSG_UNKNOWN + "hile retrieving the parent groups for a given "
                        + "merchandise hierarchy group Id " + mrhrcID + " and UI locale");
            }
            finally
            {
                DBUtils.getInstance().closeResultSet(mrhrcResultSet);
                DBUtils.getInstance().closeStatement(ps);
            }
        }
        else
        {
            info.setParentGroups(null);
            logger.info("Merchandise Hierarchy group id is null. Setting parent groups as null");
        }
    }
}
