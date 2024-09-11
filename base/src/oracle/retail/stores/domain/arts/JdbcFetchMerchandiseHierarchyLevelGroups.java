/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFetchMerchandiseHierarchyLevelGroups.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharm   11/11/14 - modified to fetch groups at a hierarchy level
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.util.ArrayList;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyGroupIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

// --------------------------------------------------------------------------------
/**
 * The JdbcFetchMerchandiseHierarchyLevelGroups defines method that the
 * application calls to get groups at a given merchandise hierarchy level.
 *
 * @version $revision$
 */
// --------------------------------------------------------------------------------
public class JdbcFetchMerchandiseHierarchyLevelGroups extends JdbcMerchandiseHierarchyDataOperation
{

    //revision number of this class
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //serialVersionUID
    private static final long serialVersionUID = 4462487424591269241L;

    //  ---------------------------------------------------------------------
    /**
       Default constructor.
    **/
    // ---------------------------------------------------------------------
    public JdbcFetchMerchandiseHierarchyLevelGroups()
    {
        super();
        setName("JdbcFetchMerchandiseHierarchyLevelGroups");
    }


    // --------------------------------------------------------------------------------
    /**
     * Executes the requested SQL statments against the database.
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */

    // --------------------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {

        //Set the method name for logging
        String methodName = "JdbcFetchMerchandiseHierarchyLevelGroups.execute()";

        if (logger.isDebugEnabled())
        {
            logger.debug(methodName);
        }

        //Make sure the the transaction is a merchandise data transaction
        MerchandiseHierarchyDataTransaction transaction;
        try
        {
            transaction = (MerchandiseHierarchyDataTransaction) dataTransaction;
        }
        catch (ClassCastException ex)
        {
            throw new DataException(DataException.DATA_FORMAT, methodName + ": Invalid dataTransaction ("
                    + dataTransaction.getClass().getName() + ")");
        }

        //Fetch the groups for a given level
        ArrayList<MerchandiseHierarchyGroupIfc> list = doFetchLevelGroupIDs((JdbcDataConnection) dataConnection,
                transaction.paramHierarchyID, transaction.paramLevelID, transaction.localeReq);

        //Set the result to transaction
        transaction.setResult(list);

        // We're done
        if (logger.isDebugEnabled())
        {
            logger.debug(methodName);
        }
    }

    // --------------------------------------------------------------------------------
    /**
     * String identification of this class.
     * <p>
     *
     * @return A string identifying the class and revision number.
     */
    // --------------------------------------------------------------------------------
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader("JdbcFetchChildrenMerchandiseHierarchyGroups",
                getRevisionNumber(), hashCode());
        return strResult.toString();
    }

    // --------------------------------------------------------------------------------
    /**
     * Retrieves the source-code-control system revision number.
     * <P>
     *
     * @return String representation of revision number
     */
    // --------------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }
}
