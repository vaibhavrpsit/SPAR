/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadItemCost.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 10:01:14 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:16 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:48    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:42   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 20 2002 15:03:54   cdb
 * Corrected defect in ReadItemCost transaction - The result was being set in a "remote" object, so in an n-tiered environment, the original object was never updated.
 * Resolution for Backoffice SCR-586: Item's cost is not saved in db.
 *
 *    Rev 1.0   Sep 20 2001 15:58:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
    Reads the item cost for a given item ID. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class JdbcReadItemCost
    extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
        revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Executes the SQL statements against the database.
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
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        ItemIfc itemObj = (ItemIfc) action.getDataObject();
        readItemCost(connection, itemObj);
        dataTransaction.setResult(itemObj.getItemCost());
    }

    /**
        Reads the item cost from the database. <P>
        @param dataConnection  connection to the database
        @param itemObj The item object
        @exception DataException upon error
     */
    protected void readItemCost(JdbcDataConnection dataConnection,
                                ItemIfc itemObj)
        throws DataException
    {
        try
        {
            SQLSelectStatement sql = buildSelectItemCostSQL(itemObj);
            dataConnection.execute(sql.getSQLString());
            CurrencyIfc cost = parseItemCostResultSet(dataConnection);
            itemObj.setItemCost(cost);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readItemCost", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readItemCost", e);
        }
    }

    /**
        Builds SQL statement for retrieving the cost for a given
        item ID. <P>
        @param itemObj The item object
        @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildSelectItemCostSQL(ItemIfc itemObj)
        throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_SUPPLIER_ITEM, ALIAS_SUPPLIER_ITEM);
        sql.addTable(TABLE_SUPPLIER_ITEM_CATALOG_BASE_COST_BREAK, ALIAS_SUPPLIER_ITEM_CATALOG_BASE_COST_BREAK);

        // add columns
        sql.addColumn(ALIAS_SUPPLIER_ITEM_CATALOG_BASE_COST_BREAK + "." +
                      FIELD_SPR_ITM_CTLG_BASE_CST_BRK_CST_PER_UT_AMOUNT);

        // add qualifiers
        sql.addQualifier(ALIAS_SUPPLIER_ITEM + "." + FIELD_SUPPLIER_ITEM_MANUFACTURER_UPC_ITEM_ID +
                         " = '" + itemObj.getItemID() + "'");

        // add join qualifiers
        sql.addJoinQualifier(ALIAS_SUPPLIER_ITEM, FIELD_SUPPLIER_ITEM_SUPPLIER_ITEM_ID,
                             ALIAS_SUPPLIER_ITEM_CATALOG_BASE_COST_BREAK, FIELD_SPR_ITM_CTLG_BASE_CST_BRK_SPR_ITEM_ID);

        return(sql);
    }

    /**
        Parse the result set and get the cost. <P>
        @param dataConnection Connection to the database
        @exception DataException thrown if error occurs
        @exception SQLException thrown if error occurs
     */
    protected CurrencyIfc parseItemCostResultSet(DataConnectionIfc dataConnection)
        throws DataException, SQLException
    {
        CurrencyIfc cost = null;
        ResultSet rs = (ResultSet) dataConnection.getResult();
        if ((rs != null) && rs.next())
        {
            cost = getCurrencyFromDecimal(rs, 1);
            rs.close();
        }
        else
        {
            throw new DataException(DataException.NO_DATA,
                "No cost found for this item.");
        }

        return(cost);
    }

    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
     */
    protected String getRevisionNumber()
    {
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcReadItemCost",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }

}

