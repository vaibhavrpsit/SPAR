/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcBuildItemImportOperation.java /main/18 2014/02/05 15:03:13 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  02/04/14 - released the stream handles
 *    sthallam  04/05/12 - Enhanced RPM Integration - Item Mod Classification
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    ddbaker   11/17/08 - Verify closing result statement and prepared
 *                         statement in all cases.
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        11/15/2007 11:34:19 AM Christian Greene
 *         Belize merge - PLU changes
 *    10   360Commerce 1.9         7/6/2007 8:36:29 AM    Christian Greene
 *         Remove reference to deleted ItemProduct table
 *    9    360Commerce 1.8         6/1/2007 3:16:05 PM    Christian Greene
 *         Backing out PLU to pre-v1.0.0.414 version code
 *    8    360Commerce 1.7         5/31/2007 3:50:56 PM   Christian Greene
 *         delete code referencing removed table columns
 *    7    360Commerce 1.6         4/25/2007 10:01:18 AM  Anda D. Cadar   I18N
 *         merge
 *    6    360Commerce 1.5         5/30/2006 10:40:30 AM  Brett J. Larsen CR
 *         18490 - UDM - misc changes to AS_ITM* tables
 *    5    360Commerce 1.4         1/25/2006 4:11:06 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:15 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:52 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:05    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:36     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:35     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:52     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:30:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:35:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:46:24   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:06:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 05 2002 16:33:24   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 * 
 *    Rev 1.0   Sep 20 2001 15:58:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:34:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.stock.ProductGroupIfc;
import oracle.retail.stores.domain.stock.ProductIfc;
import oracle.retail.stores.domain.stock.StockItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
     This class creates test data for the item import task.
     @version $Revision: /main/18 $
**/
public class JdbcBuildItemImportOperation extends    JdbcDataOperation 
                                          implements ARTSDatabaseIfc,
                                                     ProductGroupConstantsIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcBuildItemImportOperation.class);

    /**
       revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    public static final String SPACE  = " ";
    public static final String ITEM_FILE_NAME  = "itemimport.dat";
    public static final String TRUE   = "t";
    public static final String FALSE  = "f";

    /**
       Item constants
     */
    public static final int CLASSIFICATION_LENGTH               = 4;
    public static final int BUFFLEN                             = 229; // total record length
    public static final int OFFSET_CHANGE_TYPE                  = 0;
    public static final int OFFSET_ITEM_ID                      = 3;
    public static final int OFFSET_SELLING_PRICE                = 17;
    public static final int OFFSET_PERMANENT_PRICE              = 27;
    public static final int OFFSET_DEPARTMENT                   = 37;
    public static final int OFFSET_UOM_CODE                     = 47;
    public static final int OFFSET_SIZE                         = 67;
    public static final int OFFSET_COLOR                        = 77;
    public static final int OFFSET_STYLE                        = 87;
    public static final int OFFSET_LONG_DESCRIPTION             = 97;
    public static final int OFFSET_SHORT_DESCRIPTION            = 137;
    public static final int CLASSIFICATION1                     = 153;
    public static final int CLASSIFICATION2                     = 157;
    public static final int CLASSIFICATION3                     = 161;
    public static final int CLASSIFICATION4                     = 165;
    public static final int CLASSIFICATION5                     = 169;
    public static final int CLASSIFICATION6                     = 173;
    public static final int CLASSIFICATION7                     = 177;
    public static final int CLASSIFICATION8                     = 181;
    public static final int CLASSIFICATION9                     = 185;
    public static final int CLASSIFICATION10                    = 189;
    public static final int OFFSET_TAX_GROUP                    = 193;
    public static final int OFFSET_TAXABLE                      = 203;
    public static final int OFFSET_QUANTITY_MODIFIABLE          = 204;
    public static final int OFFSET_RETURN_ELIGIBLE              = 205;
    public static final int OFFSET_PRICE_ENTRY_REQUIRED         = 206;
    public static final int OFFSET_PRICE_MODIFIABLE             = 207;
    public static final int OFFSET_DISCOUNT_ELIGIBLE            = 208;
    public static final int OFFSET_REGISTRY_ELIGIBLE            = 209;
    public static final int OFFSET_AUTHORIZED_FOR_SALE          = 210;
    public static final int OFFSET_RESTOCKING_FEE_FLAG          = 211;
    public static final int OFFSET_SERIALIZED_ITEM_FLAG         = 212;
    public static final int OFFSET_ACTIVATION_REQUIRED          = 213;
    public static final int OFFSET_HIERARCHY_LEVEL              = 214;
    public static final int OFFSET_ITEM_TYPE                    = 218;
    public static final int OFFSET_COST                         = 219;

    public Locale itemImportLocale = null;

    /**
       Class constructor.
     */
    public JdbcBuildItemImportOperation()
    {
        itemImportLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcBuildItemImportOperation.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        /*
         * Send back the correct transaction (or lack thereof)
         */
        buildFlatFile(connection);

        if (logger.isDebugEnabled()) logger.debug( "JdbcBuildItemImportOperation.execute");
    }

    /**
       Selects items from the POS Identity and Item tables.
       <P>
       @param  dataConnection  a connection to the database
       @param  key             the item lookup key
       @return An array of PLUItems
       @exception  DataException thrown when an error occurs executing the
       SQL against the DataConnection, or when
       processing the ResultSet
     */
    protected void buildFlatFile(JdbcDataConnection dataConnection)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcPLUBuildOperation.buildFlatFile()");

        // build SQL statement, execute and parse
        String sqlString = buildItemsSQLStatement();
        int          itemCount = 0;
        
        // Open the file
        try(FileOutputStream os = new FileOutputStream(ITEM_FILE_NAME);)
        {
            // Execute the SQL statement.
            dataConnection.execute(sqlString);
            ResultSet rs = (ResultSet) dataConnection.getResult();
            
            // Process the result set.
            while(rs.next())
            {
                ItemIfc item = parseItemResultSet(rs);
                
                LocaleRequestor localeRequestor = new LocaleRequestor(itemImportLocale);
                applyLocaleDependentDescriptions(dataConnection, item, localeRequestor);

                // Write the record to disk;    
                String buffer = getItemBuffer(item);
                os.write(buffer.getBytes());

                itemCount++;
                if ((itemCount % 10) == 0)
                {
                    System.out.println("Items written to Item Import file = " + itemCount + ".");
                }
            }
            
            rs.close();
            if (itemCount == 0)
            {
                throw new DataException(DataException.NO_DATA, 
                    "No item was found processing the result set in JdbcBuildItemImportOperation.");
            }
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Item lookup");
            throw new DataException(DataException.SQL_ERROR, "Item lookup", se);
        }
        catch (java.io.IOException ioe)
        {
            logger.error(
                         "JdbcBuildItemImportOperation.writeRecordsToFile - error creating or writing to the PLU file.");
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Item lookup", e);
        }

        System.out.println("Total items written to Item Import file = " + itemCount + ".\n");
        if (logger.isDebugEnabled()) logger.debug( "JdbcBuildItemImportOperation.selectPLUItem()");
    }

    /**
        Builds and returns SQL statement for retrieving an item. <P>
        @return SQL select statement for retrieving and item
     */
    protected String buildItemsSQLStatement()
    {

        StringBuffer sql = new StringBuffer(1500);
        sql.append("SELECT  posid.id_itm_pos, posid.id_itm, \n");
        sql.append("        posid.fl_ky_prh_qty, posid.fl_rtn_prh, posid.qu_un_blk_mnm, \n");
        sql.append("        posid.qu_un_blk_mxm, posid.fl_cpn_alw_multy, posid.fl_entr_prc_rq, \n");
            sql.append("        posid.fl_cpn_elntc, posid.fl_cpn_rst, posid.fl_mdfr_rt_prc, \n");
            sql.append("        itm.id_gp_tx, itm.lu_exm_tx, itm.id_dpt_pos, \n");
            sql.append("        itm.fl_itm_dsc, itm.fl_itm_rgstry, itm.lu_hrc_mr_lv, itm.fl_azn_fr_sls, \n");
            sql.append("        itm.lu_kt_st, itm.fl_itm_sbst_idn, itm.ty_itm, \n");
            sql.append("        itm.id_itm_pdt\n");//, pdt.de_itm_pdt, pdt.id_mf,\n");
            sql.append("        itm.fl_actvn_rq,\n");
            sql.append("        sitm.lu_uom_sls, sitm.fl_vld_srz_itm, sitm.fl_fe_rstk, \n");
            sql.append("        sitm.ed_clr, sitm.ed_sz, sitm.lu_styl, sicbcb.cp_pnt_brk_bs_cst \n");

        sql.append("FROM        as_itm itm LEFT OUTER JOIN as_itm_spr spri ON itm.id_itm = spri.id_itm_mf_upc, \n");
            sql.append("        as_itm itm LEFT OUTER JOIN as_itm_stk sitm ON itm.id_itm = sitm.id_itm, \n");
            sql.append("        id_idn_ps posid, as_itm_rtl_str rsi, \n");//as_itm_pdt pdt, 
            sql.append("        as_itm_spr spri LEFT OUTER JOIN co_brk_spr_itm_bs sicbcb ON spri.id_itm_spr = sicbcb.id_itm_spr \n");

        sql.append("WHERE       posid.id_itm = itm.id_itm AND itm.id_itm = rsi.id_itm");//AND itm.id_itm_pdt = pdt.id_itm_pdt 
        
        return sql.toString();
    }

    /**
        Parse item result set and returns item object. <P>
        @param rs ResultSet object
        @return ItemIfc object
        @exception SQLException thrown if error SQL error occurs while parsing result set
        @exception DataException thrown if error other error occurs while parsing result set
     */
    protected ItemIfc parseItemResultSet(ResultSet rs) throws DataException, SQLException
    {
        
        ItemIfc item = null;

        int index = 0;
        String posItemID = getSafeString(rs, ++index);
        String itemID = getSafeString(rs, ++index);
//        CurrencyIfc retailPrice = getCurrencyFromDecimal(rs, ++index);
//        CurrencyIfc permanentPrice = getCurrencyFromDecimal(rs, ++index);
        boolean disableQuantityKey = getBooleanFromString(rs, ++index);
        boolean prohibitReturn = getBooleanFromString(rs, ++index);
        BigDecimal minimumSaleQuantity = getBigDecimal(rs, ++index);
        BigDecimal maximumSaleQuantity = getBigDecimal(rs, ++index);
        boolean multipleCouponsAllowed = getBooleanFromString(rs, ++index);
        boolean priceEntryRequired = getBooleanFromString(rs, ++index);
        boolean electronicCouponAvailable = getBooleanFromString(rs, ++index);
        boolean couponRestricted = getBooleanFromString(rs, ++index);
        boolean priceModifiable = getBooleanFromString(rs, ++index);
        int taxGroupID = rs.getInt(++index);
        boolean taxable = getBooleanFromString(rs, ++index);
        String deptID = getSafeString(rs, ++index);
        boolean discountable = getBooleanFromString(rs, ++index);
        boolean registryEligible = getBooleanFromString(rs, ++index);
        String productGroupID = rs.getString(++index);
        boolean saleable = getBooleanFromString(rs, ++index);
        int kitCode = rs.getInt(++index);
        boolean substituteAvailable = getBooleanFromString(rs, ++index);
        String itemType = rs.getString(++index);
        String productID = getSafeString(rs, ++index);
//        String productDescription = getSafeString(rs, ++index);
//        int productManufacturerID = rs.getInt(++index);
        boolean activationRequired = getBooleanFromString(rs, ++index);
        String uomCode = getSafeString(rs, ++index);
        boolean isSerializedItem = getBooleanFromString(rs, ++index);
        boolean restockingFeeFlag = getBooleanFromString(rs, ++index);
        String colorCode = getSafeString(rs, ++index);
        String sizeCode = getSafeString(rs, ++index);
        String styleCode = getSafeString(rs, ++index);
        CurrencyIfc cost = getCurrencyFromDecimal(rs, ++index);
        
        // instantiate proper item based on type code
        if (itemType.equals(ITEM_TYPE_STOCK))
        {
            item = DomainGateway.getFactory().getStockItemInstance();
        }
        else if (itemType.equals(ITEM_TYPE_SERVICE))
        {
            item = DomainGateway.getFactory().getItemInstance();
            item.getItemClassification().setItemType (
                    ItemClassificationConstantsIfc.TYPE_SERVICE);
        }
        else if (itemType.equals(ITEM_TYPE_STORE_COUPON))
        {
            item = DomainGateway.getFactory().getItemInstance();
            item.getItemClassification().setItemType (
                    ItemClassificationConstantsIfc.TYPE_STORE_COUPON);
        }
        else
        {
            item = DomainGateway.getFactory().getStockItemInstance();
        }

        // set the attributes
        item.setItemID(itemID);        
        
//        item.setSellingPrice(retailPrice);
//        item.setPermanentPrice(permanentPrice);
        item.setTaxable(taxable);
        item.setTaxGroupID(taxGroupID);
        // set department
        DepartmentIfc dept = item.getDepartment();
        dept.setDepartmentID(deptID);

        // set product
        ProductIfc product = item.getProduct();
        product.setProductID(productID);
//        product.setDescription(productDescription);
//        // at some point, we should look up the manufacturer data
//        product.getManufacturer().setManufacturerID(productManufacturerID);

        // Set classification attributes
        ItemClassificationIfc sc = item.getItemClassification();
        sc.setQuantityModifiable(!disableQuantityKey);
        sc.setReturnEligible(!prohibitReturn);
        sc.setPriceOverridable(priceModifiable);
        sc.setMinimumSaleQuantity(minimumSaleQuantity);
        sc.setMaximumSaleQuantity(maximumSaleQuantity);
        sc.setMultipleCouponEligible(multipleCouponsAllowed);
        sc.setPriceEntryRequired(priceEntryRequired);
        sc.setElectronicCouponAvailable(electronicCouponAvailable);
        sc.setCouponRestricted(couponRestricted);
        sc.setDiscountEligible(discountable);
        sc.setRegistryEligible(registryEligible);
        sc.setAuthorizedForSale(saleable);
        sc.setItemKitSetCode(kitCode);
        sc.setSubstituteItemAvailable(substituteAvailable);
        sc.setActivationRequired(activationRequired);

        ProductGroupIfc pg =
                DomainGateway.getFactory().getProductGroupInstance();
        if (productGroupID != null)
        {
            pg.setGroupID(productGroupID);
            pg.setDescription("None defined");
        }
        sc.setGroup(pg);

        if (item instanceof StockItemIfc)
        {
            // Set the unit of measure
            StockItemIfc sItem = (StockItemIfc)item;
            UnitOfMeasureIfc itemUOM = sItem.getUnitOfMeasure();
            itemUOM.setUnitID(uomCode);

            // get serialized item flag
            sItem.getItemClassification().setSerializedItem(isSerializedItem);
            sItem.getItemClassification().setRestockingFeeFlag(restockingFeeFlag);
            
            // get color, size, style codes
            if (colorCode.length() != 0)
            {
                sItem.getItemColor().setIdentifier(colorCode);
            }
            if (sizeCode.length() != 0)
            {
                sItem.getItemSize().setIdentifier(sizeCode);
            }
            if (styleCode.length() != 0)
            {
                sItem.getItemStyle().setIdentifier(styleCode);
            }
        }

        item.setItemCost(cost);
        
        return(item);

    }

    /**
     * Query for Locale Dependent Description
     *
     * @param dataConnection the data connection to use
     * @param pluItem the plu item
     * @param sqlLocale
     * @throws DataException
     * @throws SQLException
     */
    protected static void applyLocaleDependentDescriptions(JdbcDataConnection dataConnection, ItemIfc item,
            LocaleRequestor localeRequestor) throws DataException, SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // Table to select from
        sql.addTable(TABLE_ITEM_I8);

        // add column
        sql.addColumn(FIELD_LOCALE);
        sql.addColumn(FIELD_ITEM_DESCRIPTION);
        sql.addColumn(FIELD_ITEM_SHORT_DESCRIPTION);

        // add identifier qualifier
        sql.addQualifier(FIELD_ITEM_ID, inQuotes(item.getItemID()));

        //  add qualifier for locale
        sql.addQualifier(FIELD_LOCALE + " " + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        ResultSet rs = null;
        PreparedStatement ps = null;
        try
        {
            // execute sql
            Connection itemConnection = dataConnection.getConnection();
            ps = itemConnection.prepareStatement(sql.getSQLString());
            rs = (ResultSet)ps.executeQuery();
    
            Locale locale = null;
            // parse result set
            while (rs.next())
            {
                locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, 1));
                item.setDescription(locale, getSafeString(rs, 2));
                item.setShortDescription(locale, getSafeString(rs, 2));
            }
            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "JdbcBuildItemImportOperation.applyLocaleDependentDescriptions()", se);
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(ps);
        }
    }
    /**
       Gets the string to write to the import file
       <p>
       @param  item an ItemIfc object
     */
    protected String getItemBuffer(ItemIfc item)
    {
        // Initialize the buffer.
        StringBuffer record = new StringBuffer(BUFFLEN + 50);
        for (int offset = 0; offset < BUFFLEN; offset++)
        {
            record.insert(offset, SPACE);
        }

        // Place the data in the record.
        record.insert(OFFSET_CHANGE_TYPE, "ADD");
        record.insert(OFFSET_ITEM_ID, item.getItemID());
        record.insert(OFFSET_SELLING_PRICE, item.getSellingPrice().getStringValue());
        record.insert(OFFSET_PERMANENT_PRICE, item.getPermanentPrice().getStringValue());
        record.insert(OFFSET_DEPARTMENT, item.getDepartment().getDepartmentID());
        if (item instanceof StockItemIfc)
        {
            StockItemIfc sItem = (StockItemIfc)item;
            record.insert(OFFSET_UOM_CODE, sItem.getUnitOfMeasure().getUnitID());
            record.insert(OFFSET_SIZE, sItem.getItemSize().getIdentifier());
            record.insert(OFFSET_COLOR, sItem.getItemColor().getIdentifier());
            record.insert(OFFSET_STYLE, sItem.getItemStyle().getIdentifier());
        }
        String desc = item.getDescription(itemImportLocale);
        if (desc.length() > 40)
        {
            desc = desc.substring(0, 40);
        }
        record.insert(OFFSET_LONG_DESCRIPTION, desc);
 
        desc = item.getShortDescription(itemImportLocale);
        if (desc.length() > 16)
        {
            desc = desc.substring(0, 16);
        }
        record.insert(OFFSET_SHORT_DESCRIPTION, desc);

        if (item.getItemClassification().hasMerchandiseClassifications())
        {
            int offset = CLASSIFICATION1;
            Iterator iter = item.getItemClassification().getMerchandiseClassificationIterator();
            while (iter.hasNext())
            {
                MerchandiseClassificationIfc classification = (MerchandiseClassificationIfc) iter.next();
                record.insert(offset, classification.getIdentifier());
            }
            offset = offset + CLASSIFICATION_LENGTH;
        }
                
        record.insert(OFFSET_TAX_GROUP, item.getTaxGroupID());
        record.insert(OFFSET_TAXABLE, getBoolean(item.getTaxable()));
        ItemClassificationIfc sc = item.getItemClassification();
        record.insert(OFFSET_QUANTITY_MODIFIABLE, getBoolean(sc.isQuantityModifiable()));
        record.insert(OFFSET_RETURN_ELIGIBLE, getBoolean(sc.isReturnEligible()));
        record.insert(OFFSET_PRICE_ENTRY_REQUIRED, getBoolean(sc.isPriceEntryRequired()));
        record.insert(OFFSET_PRICE_MODIFIABLE, getBoolean(sc.isPriceOverridable()));
        record.insert(OFFSET_DISCOUNT_ELIGIBLE, getBoolean(sc.isDiscountEligible()));
        record.insert(OFFSET_REGISTRY_ELIGIBLE, getBoolean(sc.isRegistryEligible()));
        record.insert(OFFSET_AUTHORIZED_FOR_SALE, getBoolean(sc.isAuthorizedForSale()));
        record.insert(OFFSET_RESTOCKING_FEE_FLAG, getBoolean(sc.getRestockingFeeFlag()));
        record.insert(OFFSET_SERIALIZED_ITEM_FLAG, getBoolean(sc.isSerializedItem()));
        record.insert(OFFSET_ACTIVATION_REQUIRED, getBoolean(sc.isActivationRequired()));
        record.insert(OFFSET_HIERARCHY_LEVEL, sc.getGroup().getGroupID());
        record.insert(OFFSET_ITEM_TYPE, Integer.toString(item.getItemClassification().getItemType()));
        record.insert(OFFSET_COST, item.getItemCost().getStringValue());
    
        record.insert(BUFFLEN, "\n");
        return record.toString().substring(0, BUFFLEN+1);
    }

    /**
       Returns the appropriate string value for the given boolean.
       <p>
       @return the appropriate string value for the given boolean.
     */
    protected String getBoolean(boolean value)
    {
        String rc = FALSE;

        if (value)
        {
            rc = TRUE;
        }

        return(rc);
    }

    private static void closeResultSet(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                logger.error("", e);
            }
        }
    }

    private static void closeStatement(Statement s)
    {
        if (s != null)
        {
            try
            {
                s.close();
            }
            catch (SQLException e)
            {
                logger.error("", e);
            }
        }
    }

}

