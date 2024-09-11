/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdatePriceChangeItems.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:27 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:59    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:53     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:18   msg
 * Initial revision.
 *
 *    Rev 1.2   16 May 2002 23:42:34   adc
 * db2 fixes
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:49:58   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Updates the data for a temporary or a permanent price change.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public abstract class JdbcUpdatePriceChangeItems
    extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5923718307216676910L;
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
        ItemPriceMaintenanceEventIfc itemPriceMaintenance = (ItemPriceMaintenanceEventIfc) action.getDataObject();
        updatePriceChangeItems(connection, itemPriceMaintenance);
    }

    /**
        Updates the data for a temporary or a permanent price change. <P>
        @param dataConnection  connection to the db
        @param priceEvent The price event object
        @exception DataException upon error
     */
    protected void updatePriceChangeItems(JdbcDataConnection dataConnection,
                                          ItemPriceMaintenanceEventIfc itemPriceMaintenance)
        throws DataException
    {
        ArrayList<String> crtItemIDs = new ArrayList<String>();
        ArrayList<String> deleteIDs = new ArrayList<String>();
        ArrayList<String> createIDs = new ArrayList<String>();
        ArrayList<String> updateIDs = new ArrayList<String>();
        try
        {
            SQLSelectStatement sql = buildSelectPriceChangeItemsSQL(itemPriceMaintenance);
            dataConnection.execute(sql.getSQLString());
            parsePriceChangeItemsResultSet(dataConnection, crtItemIDs);

            findDeleteUpdateIDs(itemPriceMaintenance, crtItemIDs, deleteIDs, updateIDs);
            findCreateIDs(itemPriceMaintenance, crtItemIDs, createIDs);

            if (deleteIDs.size() > 0)
            {
                SQLDeleteStatement delSQL = buildDeletePriceChangeItemsSQL(itemPriceMaintenance,
                                                                                    deleteIDs);
                dataConnection.execute(delSQL.getSQLString());
            }

            for (int i = 0; i < createIDs.size(); i++)
            {
                SQLInsertStatement insSQL = buildCreatePriceChangeItemsSQL(itemPriceMaintenance,
                                             createIDs.get(i));
                dataConnection.execute(insSQL.getSQLString());
            }

            for (int i = 0; i < updateIDs.size(); i++)
            {
                SQLUpdateStatement updSQL = buildUpdatePriceChangeItemsSQL(itemPriceMaintenance,
                                             updateIDs.get(i));
                dataConnection.execute(updSQL.getSQLString());
            }
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "updatePriceChangeItems", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updatePriceChangeItems", e);
        }
    }

    /**
        Builds SQL statement for creating the Event table record. <P>
        @param priceEvent The price event object
        @exception SQLException thrown if error occurs
     */
    protected abstract SQLSelectStatement buildSelectPriceChangeItemsSQL(ItemPriceMaintenanceEventIfc searchObj)
        throws SQLException;

    /**
        Parses result set and builds a list of existing item IDs for this
        price event. <P>
        @param dataConnection data connection
        @exception SQLException thrown if result set cannot be parsed
        @exception DataException thrown if no records in result set
     */
    protected void parsePriceChangeItemsResultSet(DataConnectionIfc dataConnection,
                                                  List<String> crtItemIDs)
        throws DataException, SQLException
    {
        ResultSet rs = (ResultSet) dataConnection.getResult();
        if (rs != null)
        {
            while (rs.next())
            {
                String itemID = getSafeString(rs, 1);
                crtItemIDs.add(itemID);
            }
            rs.close();
        }
    }

    /**
        Determine the items that need to be deleted and the ones to
        be updated. <P>
        @param itemPriceMaintenance The price event object
        @param crtItemIDs array of the item IDs currently in the database
        @param deleteIDs array of the item IDs marked for deletion
        @param updateIDs array of the item IDs marked for update
     */
    public void findDeleteUpdateIDs(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                                        List<String> crtItemIDs,
                                        List<String> deleteIDs,
                                        List<String> updateIDs)
    {
        String crtID = null;

        for (int i = 0; i < crtItemIDs.size(); i++)
        {
            crtID = crtItemIDs.get(i);
            if (itemPriceMaintenance.findItem(crtID) != null)
            {
                updateIDs.add(crtID);
            }
            else
            {
                deleteIDs.add(crtID);
            }
        }
    }

    /**
        Determine the items that need to be created. <P>
        @param itemPriceMaintenance The price event object
        @param crtItemIDs array of the item IDs currently in the database
        @param createIDs array of the item IDs marked for creation
     */
    public void findCreateIDs(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                              List<String> crtItemIDs,
                              List<String> createIDs)
    {
        String crtID = null;
        boolean create = true;
        PriceChangeIfc[] pcItems = itemPriceMaintenance.getItems();

        for (int i = 0; i < pcItems.length; i++)
        {
            crtID = pcItems[i].getItem().getItemID();
            create = true;
            for (int j = 0; j < crtItemIDs.size(); j++)
            {
                if (crtID.equals(crtItemIDs.get(j)))
                {
                    create = false;
                    break;
                }
            }
            if (create)
            {
                createIDs.add(crtID);
            }
        }
    }

    /**
        Builds SQL statement for deleting items. <P>
        @param itemPriceMaintenance The price event object
        @param deleteIDs array of the item IDs marked for deletion
        @exception SQLException thrown if error occurs
     */
    public abstract SQLDeleteStatement
        buildDeletePriceChangeItemsSQL(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                                       List<String> deleteIDs);

    /**
        Create a list of item IDs. <P>
        @param deleteIDs array of the item IDs marked for deletion
        @exception SQLException thrown if error occurs
     */
    public String getItemIDs(List<String> deleteIDs)
    {
       // String result = (String) deleteIDs.get(0);
        StringBuffer resultBuf  = new StringBuffer();
        resultBuf.append("'" + deleteIDs.get(0)+ "'");
        for (int i = 1; i < deleteIDs.size(); i++)
        {
           // result =result + " , " + (String) deleteIDs.get(i);
            resultBuf.append(" ,'" + deleteIDs.get(i) + "'");
        }
    // System.out.println(resultBuf.toString());
        return(resultBuf.toString());
    }

    /**
        Builds SQL statement for creating an item. <P>
        @param itemPriceMaintenance The price event object
        @param itemID the ID of the item to be created
        @exception SQLException thrown if error occurs
     */
    public abstract SQLInsertStatement
        buildCreatePriceChangeItemsSQL(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                                       String itemID);

    /**
        Builds SQL statement for updating an item. <P>
        @param itemPriceMaintenance The price event object
        @param itemID the ID of the item to be updated
        exception SQLException thrown if error occurs
     */
    public abstract SQLUpdateStatement
        buildUpdatePriceChangeItemsSQL(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                                       String itemID);

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
        return(Util.classToStringHeader("JdbcUpdatePriceChangeItems",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }

}

