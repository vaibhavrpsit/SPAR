/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateKitSetCode.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
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
      4    360Commerce 1.3         1/25/2006 4:11:26 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:27:52    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:52     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:37  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:24  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:33:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:08   msg
 * Initial revision.
 *
 *    Rev 1.2   May 16 2002 15:16:46   mia
 * db2fixes
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:49:44   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Jan 15 2002 09:47:58   cdb
 * Initial revision.
 * Resolution for Backoffice SCR-197: Kit item transaction can not  be found
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
    This operation updates the item table from the ItemIfc object. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.domain.arts.ItemDataTransaction
    @see oracle.retail.stores.domain.stock.ItemIfc
    @see oracle.retail.stores.domain.arts.JdbcSaveItem
    @deprecated As of release 13.1.
**/
public class JdbcUpdateKitSetCode
extends JdbcSaveItem
implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateKitSetCode.class);

    /**
       revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       Class constructor.
     */
    public JdbcUpdateKitSetCode()
    {
        setName("JdbcUpdateKitSetCode");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateKitSetCode.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        KitComponentIfc[] itemKitComponents = (KitComponentIfc[]) action.getDataObject();
        updateKitSetCode(connection, itemKitComponents);

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateKitSetCode.execute()");
    }

    /**
        Perform item update. <P>
        @param dataConnection JdbcDataConnection
        @param item ItemIfc reference
        @exception DataException thrown if error occurs
     */
    public void updateKitSetCode(JdbcDataConnection dataConnection,
            KitComponentIfc[] itemKitComponents) throws DataException
    {
        try
        {
            for (int x = 0; x < itemKitComponents.length; x++)
            {
                // build sql statement
                SQLUpdateStatement sql = new SQLUpdateStatement();
                // add table, columns, qualifiers
                sql.setTable(TABLE_ITEM);
                addUpdateColumns(itemKitComponents[x], sql);
                addUpdateQualifiers(itemKitComponents[x], sql);
                // execute statement
                dataConnection.execute(sql.getSQLString());
            }
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error( e.toString());
            throw new DataException(DataException.UNKNOWN, "KitSetCode update", e);
        }

    }

    /**
        Add update columns. <P>
        @param ItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addUpdateColumns(KitComponentIfc itemKitComponent, SQLUpdatableStatementIfc sql)
    {

        sql.addColumn(FIELD_ITEM_KIT_SET_CODE,
            this.inQuotes(itemKitComponent.getItemClassification().getItemKitSetCode()));
    }

    /**
        Adds update qualifier columns to SQL statement. <P>
        @param ItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addUpdateQualifiers(KitComponentIfc itemKitComponent, SQLUpdateStatement sql)
    {
        sql.addQualifier(FIELD_ITEM_ID, makeSafeString(itemKitComponent.getItemID()));
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
        return(Util.classToStringHeader("JdbcUpdateKitSetCode",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

