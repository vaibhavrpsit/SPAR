/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadItemTypes.java /main/10 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mchellap  10/16/08 - Advance Item Inquiry
 *    mchellap  10/16/08 - Advance Item Inquiry
 *    mchellap  10/16/08 - Advanced Item Inquiry
 *
 *
 * ===========================================================================
 */

package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Class that contains the database calls for reading item types.
 */
public class JdbcReadItemTypes extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 2018948400332485812L;

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
        if (logger.isDebugEnabled()) logger.debug("Entering JdbcReadItemTypes.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        Object dataObject = action.getDataObject();

        if (dataObject != null && dataObject instanceof LocaleRequestor)
        {
            LocaleRequestor localeRequestor = (LocaleRequestor)dataObject;
            dataTransaction.setResult(readItemStyles(connection, localeRequestor));
        }
        else
        {
            logger.error("JdbcReadItemTypes.execute: Invalid search object");
            throw new DataException("Invalid search object");
        }

        if (logger.isDebugEnabled()) logger.debug( "Exiting JdbcReadItemTypes.execute()");
    }

    /**
        Returns a list of item styles.
        @param  dataConnection      connection to the db
        @return ItemStyleIfc[]     the list of item types
        @exception DataException upon error
     */
    public ItemTypeIfc[] readItemStyles(JdbcDataConnection dataConnection, LocaleRequestor localeRequestor)
                                                throws DataException
    {


        SQLSelectStatement sql = new SQLSelectStatement();

        ItemTypeIfc[] types = null;

        sql.addTable(TABLE_ITEM);
        // add columns
        sql.addColumn(FIELD_ITEM_TYPE_CODE);
        // add ordering
        sql.addOrdering(FIELD_ITEM_TYPE_CODE);
        // Set distinct flag to select unique item types
        sql.setDistinctFlag(true);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            Vector itemTypeList = new Vector<ItemTypeIfc>(4);

            while (rs.next())
            {
                int index = 0;
                ItemTypeIfc itemType = DomainGateway.getFactory().getItemTypeInstance();
                String type = getSafeString(rs,++index);
                itemType.setItemTypeID(type);
                itemType.setItemTypeName(type);
                itemTypeList.addElement(itemType);
            }
            rs.close();
            types = new ItemTypeIfc[itemTypeList.size()];
            itemTypeList.copyInto(types);

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
            throw new DataException(UNKNOWN, "selectItemTypeList", e);
        }

        return types;
    }
}
