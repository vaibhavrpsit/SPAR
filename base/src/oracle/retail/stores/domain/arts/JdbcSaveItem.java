/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveItem.java /main/17 2012/04/05 16:32:22 sthallam Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    sthall 04/05/12 - Enhanced RPM Integration - Item Mod Classification
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    ddbake 10/22/08 - Updating to use localized item descriptoins
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      7    360Commerce 1.6         11/15/2007 11:37:56 AM Christian Greene
           Belize merge - PLU changes
      6    360Commerce 1.5         7/6/2007 8:36:29 AM    Christian Greene
           Remove reference to deleted ItemProduct table
      5    360Commerce 1.4         6/7/2007 12:47:14 PM   Jack G. Swan
           Changes from DB2 unittests.
      4    360Commerce 1.3         1/25/2006 4:11:21 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:02 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:28:22    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:48     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:02     Robert Pearse
     $
     Revision 1.8  2004/04/09 16:55:44  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.7  2004/03/15 15:15:16  baa
     @scr 3561 refactoring/ cleaning item size

     Revision 1.6  2004/02/17 22:08:46  epd
     @scr 0
     Updated to read/save the new size required flag

     Revision 1.5  2004/02/17 17:57:35  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:21  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Feb 06 2004 12:21:12   cdb
 * Added handling for Damage Disount Eligible items.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 15:32:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 06 2003 15:08:52   cdb
 * Added support for Merchandise Hierarchy Groups in Items.
 * Resolution for 2035: Ad Hoc Reporting Feature
 *
 *    Rev 1.0   Jun 03 2002 16:39:38   msg
 * Initial revision.
 *
 *    Rev 1.2   May 16 2002 15:16:44   mia
 * db2fixes
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:48:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:06   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 01 2002 13:54:04   cdb
 * Backed out previous changes, updated to use Special Order Eligible flag that was hiding in POS Identity table.
 * Resolution for Backoffice SCR-377: Changes due to requirements changes in Item Maintenance  Service.
 *
 *    Rev 1.1   Feb 01 2002 13:30:38   cdb
 * Added Special Order Eligible to support changes in Back Office.
 * Resolution for Backoffice SCR-377: Changes due to requirements changes in Item Maintenance  Service.
 *
 *    Rev 1.0   Sep 20 2001 15:57:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:06   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;
import java.util.List;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation inserts and updates the item table from the ItemIfc object.
 * 
 * @version $Revision: /main/17 $
 * @see oracle.retail.stores.domain.arts.ItemDataTransaction
 * @see oracle.retail.stores.domain.stock.ItemIfc
 */
public abstract class JdbcSaveItem extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -4463459458471580457L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveItem.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/17 $";


    /**
     * Perform item update.
     * 
     * @param dataConnection JdbcDataConnection
     * @param item ItemIfc reference
     * @exception DataException thrown if error occurs
     */
    public void updateItem(JdbcDataConnection dataConnection,
            ItemIfc item) throws DataException
    {
        // build sql statement
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_ITEM);
        addUpdateColumns(item, sql);
        addUpdateQualifiers(item, sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error( e.toString());
            throw new DataException(DataException.UNKNOWN, "Item update", e);
        }

    }

    /**
        Perform item insert. <P>
        @param dataConnection JdbcDataConnection
        @param item ItemIfc reference
        @exception DataException thrown if error occurs
     */
    public void insertItem(JdbcDataConnection dataConnection,
            ItemIfc item) throws DataException
    {
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_ITEM);
        addInsertColumns(item, sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error( e.toString());
            throw new DataException(DataException.UNKNOWN, "Item insert", e);
        }

    }

    /**
        Add update columns. <P>
        @param ItemIfc item object
        @param sql SQLUpdateStatement
        @deprecated As of release 13.1
     */
    public void addUpdateColumns(ItemIfc item, SQLUpdatableStatementIfc sql)
    {
        ItemClassificationIfc classification = item.getItemClassification();
        sql.addColumn(FIELD_ITEM_DESCRIPTION, makeSafeString(item.getDescription(LocaleMap.getLocale(LocaleMap.DEFAULT))));
//        sql.addColumn(FIELD_ITEM_PRODUCT_ID,
//                makeSafeString(item.getProduct().getProductID()));
        sql.addColumn(FIELD_ITEM_SHORT_DESCRIPTION,
                makeSafeString(item.getShortDescription(LocaleMap.getLocale(LocaleMap.DEFAULT))));
        sql.addColumn(FIELD_TAX_GROUP_ID, Integer.toString(item.getTaxGroupID()));
        sql.addColumn(FIELD_ITEM_TAX_EXEMPT_CODE,
                makeStringFromBoolean(item.getTaxable()));
        sql.addColumn(FIELD_ITEM_SIZE_REQUIRED_FLAG,
                makeStringFromBoolean(item.isItemSizeRequired()));
        sql.addColumn(FIELD_POS_DEPARTMENT_ID,
                makeSafeString(item.getDepartment().getDepartmentID()));
        sql.addColumn(FIELD_ITEM_DISCOUNT_FLAG,
                makeStringFromBoolean(item.getDiscountEligible()));
        sql.addColumn(FIELD_ITEM_DAMAGE_DISCOUNT_FLAG,
                makeStringFromBoolean(item.getDamageDiscountEligible()));
        sql.addColumn(FIELD_ITEM_REGISTRY_FLAG,
                makeStringFromBoolean(classification.getRegistryEligible()));
        sql.addColumn(FIELD_MERCHANDISE_HIERARCHY_LEVEL_CODE,
                makeSafeString(item.getProductGroupID()));
        sql.addColumn(FIELD_ITEM_AUTHORIZED_FOR_SALE_FLAG,
                makeStringFromBoolean(classification.getAuthorizedForSale()));
        sql.addColumn(FIELD_ITEM_KIT_SET_CODE, JdbcDataOperation.inQuotes(classification.getItemKitSetCode()));
        sql.addColumn(FIELD_ITEM_SUBSTITUTE_IDENTIFIED_FLAG,
                makeStringFromBoolean(classification.getSubstituteItemAvailable()));
        sql.addColumn(FIELD_ITEM_TYPE_CODE,
                getTypeCodeStringFromCode(classification));
        sql.addColumn(FIELD_ITEM_ACTIVATION_REQUIRED_FLAG,
                makeStringFromBoolean(classification.getActivationRequired()));
        sql.addColumn(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(classification.getMerchandiseHierarchyGroup()));
        List<MerchandiseClassificationIfc> merchandiseClassifications =
                classification.getMerchandiseClassifications();
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                getSQLCurrentTimestampFunction());
    }

    /**
        Add insert columns. <P>
        @param ItemIfc item object
        @param sql SQLInsertStatement
     */
    public void addInsertColumns(ItemIfc item, SQLUpdatableStatementIfc sql)
    {
        sql.addColumn(FIELD_ITEM_ID, makeSafeString(item.getItemID()));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                getSQLCurrentTimestampFunction());
        addUpdateColumns(item, sql);
    }

    /**
        Adds update qualifier columns to SQL statement. <P>
        @param ItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addUpdateQualifiers(ItemIfc item, SQLUpdateStatement sql)
    {
        sql.addQualifier(FIELD_ITEM_ID, makeSafeString(item.getItemID()));
    }

    /**
        Returns item type classification string from code. <P>
        @param classification ItemClassificationIfc object
        @return SQL-ready string with ARTS item classification code
     */
    protected String getTypeCodeStringFromCode (ItemClassificationIfc classification)
    {
        String itemTypeString = null;
        switch (classification.getItemType())
        {
            case ItemClassificationConstantsIfc.TYPE_SERVICE:
                itemTypeString = ITEM_TYPE_SERVICE;
                break;
            case ItemClassificationConstantsIfc.TYPE_STORE_COUPON:
                itemTypeString = ITEM_TYPE_STORE_COUPON;
                break;
            case ItemClassificationConstantsIfc.TYPE_STOCK:
            default:
                itemTypeString = ITEM_TYPE_STOCK;
                break;
        }
        return("'" + itemTypeString + "'");
    }

    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcSaveItem", getRevisionNumber(),
                hashCode()).toString());
    }
}
