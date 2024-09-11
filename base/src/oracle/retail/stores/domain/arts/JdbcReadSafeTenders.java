/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadSafeTenders.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         7/3/2007 2:06:23 PM    Alan N. Sinton  CR
 *       27474 - Read store information even if store history table is empty.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Reads the safe tenders from the store database.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadSafeTenders extends JdbcReadStore
{
    /** Logger */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadSafeTenders.class);

    /**
     * Initiates the read of safe tenders from the database.
     * @param dt
     * @param dc
     * @param da
     * @throws DataException
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadSafeTenders.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        TenderDescriptorIfc[] tenderDescriptors = readSafeTenders(connection);
        /*
         * Send back the result
         */
        dataTransaction.setResult(tenderDescriptors);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadSafeTenders.execute()");
    }

}
