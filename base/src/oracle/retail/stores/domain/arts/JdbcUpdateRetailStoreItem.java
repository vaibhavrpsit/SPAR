/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateRetailStoreItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
      4    360Commerce 1.3         1/25/2006 4:11:27 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:27:59    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:53     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:37  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:25  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:33:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:50:04   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:40   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation updates the retail stock item table from the ItemIfc object.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.ItemDataTransaction
 * @see oracle.retail.stores.domain.stock.ItemIfc
 * @see oracle.retail.stores.domain.arts.JdbcSaveRetailStoreItem
 * @deprecated As of release 13.1.
 */
public class JdbcUpdateRetailStoreItem extends JdbcSaveRetailStoreItem implements ARTSDatabaseIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateRetailStoreItem.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcUpdateRetailStoreItem()
    {
        setName("JdbcUpdateRetailStoreItem");
    }

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateRetailStoreItem.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ItemIfc item = (ItemIfc) action.getDataObject();
        updateRetailStoreItem(connection, item);

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateRetailStoreItem.execute()");
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
        return(Util.classToStringHeader("JdbcUpdateRetailStoreItem",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

