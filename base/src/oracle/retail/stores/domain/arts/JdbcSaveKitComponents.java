/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveKitComponents.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    ohorne 10/07/08 - Deprecated unused classes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:22 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:26:13    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:44  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:22  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:32:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:39:50   msg
 * Initial revision.
 *
 *    Rev 1.2   May 16 2002 15:16:46   mia
 * db2fixes
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:48:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Nov 06 2001 12:04:44   cdb
 * Initial revision.
 * Resolution for Backoffice SCR-18: Item Kit Feature
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
    This operation inserts and updates the item collection table from the
    ItemKitIfc object. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.domain.arts.ItemDataTransaction
    @see oracle.retail.stores.domain.stock.ItemKitIfc
    @see oracle.retail.stores.domain.stock.KitComponentIfc
    @deprecated As of release 13.1.
**/
public class JdbcSaveKitComponents extends JdbcDataOperation
                                   implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveKitComponents.class);

    /**
       revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       Class constructor.
     */
    public JdbcSaveKitComponents()
    {
        setName("JdbcSaveKitComponents");
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
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcSaveKitComponents.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ItemKitIfc item = (ItemKitIfc) action.getDataObject();
        updateKitComponents(connection, item);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSaveKitComponents.execute()");
    }

    /**
        Removed pre-existing Kit Components. <P>
        @param dataConnection JdbcDataConnection
        @param itemKit ItemKitIfc reference
        @exception DataException thrown if error occurs
     */
    public void removeKitComponents(JdbcDataConnection dataConnection,
            ItemKitIfc itemKit) throws DataException
    {
        // build sql statement
        SQLDeleteStatement sql = new SQLDeleteStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_ITEM_COLLECTION);
        sql.addQualifier(FIELD_ITEM_COLLECTION_ID,
                         makeSafeString(itemKit.getItemID()));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(
                         de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(
                         e.toString());
            throw new DataException(DataException.UNKNOWN,
                                    "Remove Kit Components", e);
        }
    }

    /**
        Perform update of kit components associated with an item kit. <P>
        @param dataConnection JdbcDataConnection
        @param itemKit ItemKitIfc reference
        @exception DataException thrown if error occurs
     */
    public void updateKitComponents(JdbcDataConnection dataConnection,
            ItemKitIfc itemKit) throws DataException
    {
        removeKitComponents(dataConnection, itemKit);
        insertKitComponents(dataConnection, itemKit);
    }

    /**
        Perform kit component insert. <P>
        @param dataConnection JdbcDataConnection
        @param itemKit ItemKitIfc reference
        @exception DataException thrown if error occurs
     */
    public void insertKitComponents(JdbcDataConnection dataConnection,
                                    ItemKitIfc itemKit) throws DataException
    {
        KitComponentIfc[] components = itemKit.getComponentItems();
        for (int x = 0; x < components.length; x++)
        {
            // build sql statement
            SQLInsertStatement sql = new SQLInsertStatement();
            // add table, columns, qualifiers
            sql.setTable(TABLE_ITEM_COLLECTION);
            addInsertColumns(components[x], sql);
            // execute statement
            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(
                             de.toString());
                throw de;
            }
            catch (Exception e)
            {
                logger.error(
                             e.toString());
                throw new DataException(DataException.UNKNOWN,
                                        "Insert kit components", e);
            }
        }

    }

    /**
        Add insert columns. <P>
        @param ItemIfc item object
        @param sql SQLInsertStatement
     */
    public void addInsertColumns(KitComponentIfc component,
                                 SQLUpdatableStatementIfc sql)
    {

        //FIELD_ITEM_COLLECTION_ID                  ItemCollectionID
        //FIELD_ITEM_ID                             Collection
        //FIELD_ITEM_COLLECTION_MEMBER_COLLECTION   MemberCollection
        //FIELD_ITEM_COLLECTION_TYPE_CODE           TypeCode
        //FIELD_ITEM_PER_ASSEMBLY_COUNT             PerAssemblyCount
        //                                          BlendPercent
        //FIELD_RECORD_CREATION_TIMESTAMP           RecordCreationTimestamp
        //FIELD_RECORD_LAST_MODIFIED_TIMESTAMP      RecordLastModifiedTimestamp

        sql.addColumn(FIELD_ITEM_COLLECTION_ID,
                      makeSafeString(component.getItemKitID()));
        sql.addColumn(FIELD_ITEM_ID,
                      makeSafeString(component.getItemKitID()));
        sql.addColumn(FIELD_ITEM_COLLECTION_MEMBER_COLLECTION,
                      makeSafeString(component.getItemID()));
        sql.addColumn(FIELD_ITEM_COLLECTION_TYPE_CODE,
                      inQuotes(component.getItemClassification().getItemKitSetCode()));
        sql.addColumn(FIELD_ITEM_PER_ASSEMBLY_COUNT,
                      component.getQuantity().toString());
        //sql.addColumn(PE_BLN, component.? - future requirement? ());

        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                getSQLCurrentTimestampFunction());
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
        return(Util.classToStringHeader("JdbcSaveKitComponents",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}
