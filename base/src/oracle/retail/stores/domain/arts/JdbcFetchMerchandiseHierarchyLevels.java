/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFetchMerchandiseHierarchyLevels.java /main/11 2013/12/30 11:44:42 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/12/13 - formatting cleanup
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.ArrayList;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Fetches the all levels for an existing merchandise classification hierarchy
 * group from the MerchandiseHierarchy tables in the ARTS data model (Release
 * 4.0).
 * 
 * @version $Revision: /main/11 $
 */
public class JdbcFetchMerchandiseHierarchyLevels extends JdbcMerchandiseHierarchyDataOperation
{
    private static final long serialVersionUID = -1866373317372468318L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        // Figure out where we are
        String methodName = "JdbcFetchMerchandiseHierarchyGroup.execute()";
        if (logger.isDebugEnabled())
            logger.debug(methodName + "starting...");

        // Make sure we've got the right transaction....
        MerchandiseHierarchyDataTransaction transaction;
        try
        {
            transaction = (MerchandiseHierarchyDataTransaction)dataTransaction;
        }
        catch (ClassCastException ex)
        {
            throw new DataException(DataException.DATA_FORMAT, methodName + ": Invalid dataTransaction ("
                    + dataTransaction.getClass().getName() + ")");
        }

        ArrayList<MerchandiseHierarchyLevelIfc> result = doFetchAllLevels((JdbcDataConnection)dataConnection,
                transaction.paramHierarchyID,
                transaction.localeReq);

        transaction.setResult(result);

        // We're done
        if (logger.isDebugEnabled())
            logger.debug(methodName + " ended.");
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.arts.JdbcMerchandiseHierarchyDataOperation#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader("JdbcFetchMerchandiseHierarchyGroup", getRevisionNumber(),
                hashCode());
        return strResult.toString();
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }
}
