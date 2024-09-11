/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadRegionDistrict.java /main/12 2011/12/05 12:16:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
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

import oracle.retail.stores.common.utility.LocaleMapConstantsIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * Reads the store information per the given store ID.
 * 
 * $Revision: /main/12 $
 */
public class JdbcReadRegionDistrict extends JdbcReadStore
{
    private static final long serialVersionUID = -5983646883076485729L;

    /** Logger */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadRegionDistrict.class);

    /**
     * Execute method for initiating the data read.
     * @param dt
     * @param dc
     * @param da
     * @throws DataException
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction) throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        StoreIfc store = null;
        
        // Navigate the input object to obtain values that will be inserted
        // into the database.
        if (dataAction.getDataObject() instanceof StringSearchCriteria)
        {
           StringSearchCriteria criteria = (StringSearchCriteria) dataAction.getDataObject();
           store = DomainGateway.getFactory().getStoreInstance();
           store.setStoreID(criteria.getIdentifier());
           store = readRegionDistrict(connection, store, criteria.getLocaleRequestor());
        }
        else if (dataAction.getDataObject() instanceof StoreIfc)
        {
            //this block deprecated as of Release 13.1
            store = (StoreIfc) dataAction.getDataObject();
            store = readRegionDistrict(connection, store, new LocaleRequestor(LocaleMap.getLocale(LocaleMapConstantsIfc.DEFAULT)));
        }
        else 
        {
            logger.error("JdbcReadRegionDistrict.execute: Invalid search object");
            throw new DataException("Invalid search object");
        }
 
        /*
         * Send back the result
         */
        dataTransaction.setResult(store);
    }

}
