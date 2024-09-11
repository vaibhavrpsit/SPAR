/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadScanSheet.java /main/2 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abonda 09/04/13 - initialize collections
 *    jkoppo 03/09/11 - I18N changes.
 *    jkoppo 03/07/11 - Modified the code to take care of the case, where there
 *                      are no scan sheet items configured.
 *    jkoppo 03/07/11 - Modified scan sheet table and column names
 *    jkoppo 03/04/11 - Modified to fetch the image urls.
 *    jkoppo 03/02/11 - New jdbc look up class for scan sheet transaction
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.ScanSheet;
import oracle.retail.stores.domain.stock.ScanSheetComponent;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;

public class JdbcReadScanSheet extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8720913220048424694L;

    public void execute(DataTransactionIfc dt, DataConnectionIfc dc, DataActionIfc da) throws DataException
    {
        // All items list
        ArrayList<ScanSheetComponent> itemList = fetchAllScanSheetComponents(dc, (String) da.getDataObject());
        ScanSheet ss = null;
        HashMap<String, String> categoryParentMap = new HashMap<String, String>(1);
        HashMap<String, String> categoryDescMap = new HashMap<String, String>(1);
        // Stand alone components go into this list
        ArrayList<ScanSheetComponent> scItemList = new ArrayList<ScanSheetComponent>();
        // Components belonging to a particular cateogry are added to this map
        HashMap<String, ArrayList<ScanSheetComponent>> categoryMap = new HashMap<String, ArrayList<ScanSheetComponent>>(1);
        for (ScanSheetComponent ssComponent : itemList)
        {
            /*
             * prepare the arraylist and the hashmap of componenents belonging
             * to a category
             */
            if (!ssComponent.hasParent())
            {
                scItemList.add(ssComponent);
            }
            else
            {
                String parentCategory = ssComponent.getParentCategoryID();
                if (categoryMap.containsKey(parentCategory))
                {
                    categoryMap.get(parentCategory).add(ssComponent);
                }
                else
                {
                    ArrayList<ScanSheetComponent> al = new ArrayList<ScanSheetComponent>();
                    al.add(ssComponent);
                    categoryMap.put(parentCategory, al);
                }
                // Put parent name into the categoryParentMap
                if (ssComponent.isCategory())
                {
                    categoryParentMap.put(ssComponent.getCategoryID(), ssComponent.getParentCategoryID());
                }
            }
            if (ssComponent.isCategory())
            {
                categoryDescMap.put(ssComponent.getCategoryID(), ssComponent.getDesc());
            }
        }
        ss = new ScanSheet(scItemList, categoryMap, categoryParentMap, categoryDescMap);
        dt.setResult(ss);
    }

    private ArrayList<ScanSheetComponent> fetchAllScanSheetComponents(DataConnectionIfc dc, String locale)
            throws DataException
    {
        // build sql for the scan sheet table
        SQLSelectStatement scsql = this.buildScanSheetSQL();
        HashMap<Integer, ScanSheetComponent> scMap = new HashMap<Integer, ScanSheetComponent>(1);
        HashMap<String, ScanSheetComponent> itmMap = new HashMap<String, ScanSheetComponent>(1);
        JdbcDataConnection connection = (JdbcDataConnection) dc;
        ArrayList<ScanSheetComponent> itemList = new ArrayList<ScanSheetComponent>();
        // Where clause to fetch the category names and images
        StringBuilder wClause = new StringBuilder();
        // Where clasue to fetch descriptions for items from as_itm_i8
        StringBuilder iwClause = new StringBuilder();
        try
        {
            String sqlString = scsql.getSQLString();
            connection.execute(sqlString);
            ResultSet rs = (ResultSet) connection.getResult();
            /*
             * Gather scan sheet items from the result set
             */
            while (rs.next())
            {
                int componentID = rs.getInt(1);
                String itemID = getSafeString(rs, 2);
                String categoryID = getSafeString(rs, 3);
                int sequence = rs.getInt(4);
                boolean isCategory = (getSafeString(rs, 5).equals("C")) ? true : false;
                String parentCategoryID = getSafeString(rs, 6);
                ScanSheetComponent scComponent = new ScanSheetComponent(componentID, itemID, categoryID, ""
                        + componentID, sequence, isCategory, parentCategoryID, null, null);
                itemList.add(scComponent);
                scMap.put(componentID, scComponent);
                wClause.append("'" + componentID + "',");
                if (!scComponent.isCategory())
                {
                    String itmId = scComponent.getItemID();
                    if (!Util.isEmpty(itmId))
                    {
                        iwClause.append("'" + itmId + "',");
                        itmMap.put(itmId, scComponent);
                    }
                }
            }
            rs.close();
            if (wClause.length() != 0)
            {
                this.setCategoryDescriptions(scMap, wClause.toString(), locale, dc);
            }
            if (iwClause.length() != 0)
            {
                String whereClause = iwClause.toString();
                this.setItemDescriptions(itmMap, whereClause, locale, dc);
                this.setItemImages(itmMap, whereClause, locale, dc);
            }
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "ScanSheet lookup");
            throw new DataException(DataException.SQL_ERROR, "ScanSheet lookup", se);
        }
        return itemList;
    }

    private void setItemImages(HashMap<String, ScanSheetComponent> itmMap, String whereClause, String locale,
            DataConnectionIfc dc) throws DataException
    {
        SQLSelectStatement descSql = this.buildItemImageSQL(whereClause, locale);
        JdbcDataConnection connection = (JdbcDataConnection) dc;
        String sqlString = descSql.getSQLString();
        connection.execute(sqlString);
        ResultSet rs = (ResultSet) connection.getResult();
        try
        {
            while (rs.next())
            {
                String itemID = getSafeString(rs, 1);
                ScanSheetComponent scComponent = itmMap.get(itemID);
                byte[] image = scComponent.getImage();
                if(image == null)
                {
                   image  = new byte[0] ;
                }
                
                if (scComponent.getImage() == null && Util.isEmpty(scComponent.getImageLocation()))
                {
                    image = DatabaseBlobHelperFactory.getInstance()
                            .getDatabaseBlobHelper(connection.getConnection())
                            .loadBlob(rs, ARTSDatabaseIfc.FIELD_ITEM_IMAGE_BLOB);
                    
                    if (image.length == 0)
                    {
                        scComponent.setImageLocation(getSafeString(rs, 2));
                    }
                    else
                    {
                        scComponent.setImage(image);
                    }
                }
            }
            rs.close();
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "ScanSheet lookup");
            throw new DataException(DataException.SQL_ERROR, "ScanSheet lookup", se);
        }
    }

    private SQLSelectStatement buildItemImageSQL(String whereClause, String locale)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_ITEM_IMAGE, ALIAS_IMAGE);
        sql.addColumn(ALIAS_IMAGE + "." + FIELD_ITEM_ID);
        sql.addColumn(ALIAS_IMAGE + "." + FIELD_ITEM_IMAGE_LOCATION);
        sql.addColumn(ALIAS_IMAGE + "." + FIELD_ITEM_IMAGE_BLOB);
        sql.addQualifier(ARTSDatabaseIfc.ALIAS_IMAGE + "." + ARTSDatabaseIfc.FIELD_ITEM_ID + " IN("
                + whereClause.substring(0, whereClause.length() - 1) + " )");
        return sql;
    }

    private void setItemDescriptions(HashMap<String, ScanSheetComponent> itmMap, String iwClause, String locale,
            DataConnectionIfc dc) throws DataException
    {
        // build sql
        SQLSelectStatement descSql = this.buildItemDescI18NSQL(iwClause, locale);
        JdbcDataConnection connection = (JdbcDataConnection) dc;
        String sqlString = descSql.getSQLString();
        connection.execute(sqlString);
        ResultSet rs = (ResultSet) connection.getResult();
        try
        {
            while (rs.next())
            {
                itmMap.get(getSafeString(rs, 1)).setDesc(getSafeString(rs, 2));
            }
            rs.close();
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "ScanSheet lookup");
            throw new DataException(DataException.SQL_ERROR, "ScanSheet lookup", se);
        }
    }

    private SQLSelectStatement buildItemDescI18NSQL(String iwClause, String locale)
    {
        SQLSelectStatement scsql = new SQLSelectStatement();
        // add table with aliases
        scsql.addTable(ARTSDatabaseIfc.TABLE_ITEM_I8, ARTSDatabaseIfc.ALIAS_ITEM_I8);
        // add columns
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_ITEM_I8 + "." + ARTSDatabaseIfc.FIELD_ITEM_ID);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_ITEM_I8 + "." + ARTSDatabaseIfc.FIELD_ITEM_SHORT_DESCRIPTION);
        scsql.addQualifier(ARTSDatabaseIfc.ALIAS_ITEM_I8 + "." + ARTSDatabaseIfc.FIELD_ITEM_ID + " IN("
                + iwClause.substring(0, iwClause.length() - 1) + " )");
        scsql.addQualifier(ARTSDatabaseIfc.ALIAS_ITEM_I8 + "." + ARTSDatabaseIfc.FIELD_LOCALE + " ='" + locale + "'");
        return scsql;
    }

    private void setCategoryDescriptions(HashMap<Integer, ScanSheetComponent> scMap, String wClause, String locale,
            DataConnectionIfc dc) throws DataException
    {
        // build sql
        SQLSelectStatement scsql = this.buildScanSheetI18NSQL(wClause, locale);
        JdbcDataConnection connection = (JdbcDataConnection) dc;
        String sqlString = scsql.getSQLString();
        connection.execute(sqlString);
        ResultSet rs = (ResultSet) connection.getResult();
        try
        {
            while (rs.next())
            {
                ScanSheetComponent scComp = scMap.get(rs.getInt(1));
                if (scComp.isCategory())
                {
                    scComp.setDesc(getSafeString(rs, 2));
                }
                byte[] image = DatabaseBlobHelperFactory.getInstance()
                        .getDatabaseBlobHelper(connection.getConnection())
                        .loadBlob(rs, ARTSDatabaseIfc.FIELD_SCAN_SHEET_I8_DO_SC_COM_IMG);
                if (image.length == 0)
                {
                    scComp.setImageLocation(getSafeString(rs, 4));
                }
                else
                {
                    scComp.setImage(image);
                }
            }
            rs.close();
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "ScanSheet lookup");
            throw new DataException(DataException.SQL_ERROR, "ScanSheet lookup", se);
        }
    }

    private SQLSelectStatement buildScanSheetI18NSQL(String wClause, String locale)
    {
        SQLSelectStatement scsql = new SQLSelectStatement();
        // add table with aliases
        scsql.addTable(ARTSDatabaseIfc.TABLE_SCAN_SHEET_I8, ARTSDatabaseIfc.ALIAS_SCAN_SHEET_I8);
        // add columns
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET_I8 + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_I8_ID_SC_SHT_COM);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET_I8 + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_I8_NM_CTGY);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET_I8 + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_I8_DO_SC_COM_IMG);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET_I8 + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_I8_COM_IMG_LOC);
        scsql.addQualifier(ARTSDatabaseIfc.ALIAS_SCAN_SHEET_I8 + "."
                + ARTSDatabaseIfc.FIELD_SCAN_SHEET_I8_ID_SC_SHT_COM + " IN("
                + wClause.substring(0, wClause.length() - 1) + " )");
        scsql.addQualifier(ARTSDatabaseIfc.ALIAS_SCAN_SHEET_I8 + "." + ARTSDatabaseIfc.FIELD_LOCALE + " ='" + locale
                + "'");
        return scsql;
    }

    private SQLSelectStatement buildScanSheetSQL()
    {
        SQLSelectStatement scsql = new SQLSelectStatement();
        // add table with aliases
        scsql.addTable(ARTSDatabaseIfc.TABLE_SCAN_SHEET, ARTSDatabaseIfc.ALIAS_SCAN_SHEET);
        // add columns
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_ID_COM);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_ID_ITM);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_ID_CTGY);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_AI_ORD);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_TY_COM);
        scsql.addColumn(ARTSDatabaseIfc.ALIAS_SCAN_SHEET + "." + ARTSDatabaseIfc.FIELD_SCAN_SHEET_ID_CTGY_PRNT);
        return scsql;
    }
}
