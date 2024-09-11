/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/DataReplicationDataTransaction.java /main/11 2011/01/31 14:17:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         11/9/2006 6:42:34 PM   Jack G. Swan
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.xmlreplication.result.EntityIfc;

/**
 * This class is the data transaction used to retrieve a data replication
 * entity.
 * 
 * @version@ $Revision: /main/11 $
 */
public class DataReplicationDataTransaction extends DataTransaction
    implements DataReplicationDataTransactionIfc
{
    private static final long serialVersionUID = -3739709994908315980L;

    /**
     * The default constructor.
     */
    public DataReplicationDataTransaction()
    {
        super("DataReplicationDataTransaction");
    }

    /**
     * This method retrieves a data replication entity.
     * 
     * @param DataReplicationSearchCriteria
     * @return EntityIfc
     */
    public EntityIfc getDataReplicationBatch(DataReplicationSearchCriteria criteria) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(criteria);
        dataAction.setDataOperationName("ReadDataReplicationEntity");
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        EntityIfc entity = (EntityIfc) getDataManager().execute(this);
        return entity;
    }

    /**
     * This method retrieves a data replication entity for customer.
     * 
     * @param DataReplicationSearchCriteria
     * @return EntityIfc
     */
    public EntityIfc getCustomerDataReplicationBatch(DataReplicationSearchCriteria criteria) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(criteria);
        dataAction.setDataOperationName("ReadCustomerDataReplicationEntity");
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        EntityIfc entity = (EntityIfc) getDataManager().execute(this);
        return entity;
    }
}
