/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/ReversalLaunchShuttle.java /main/3 2013/11/15 17:35:54 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/15/13 - refactored to use TransactionUtilityManager to
 *                         obtain transactionArchiveName
 *    asinton   11/14/13 - added code to carry the transaction archive name to
 *                         reversal and authorization services for transaction
 *                         archival to support potential reversal of pending
 *                         authorizations in the case of application crash
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import oracle.retail.stores.domain.manager.payment.ReversalRequestIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.tender.reversal.ReversalCargo;


/**
 * Shuttle the information required by the Reversal Service
 */
public class ReversalLaunchShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8017900063503934565L;

    /**
     * Handle to the sale cargo
     */
    protected SaleCargoIfc callingCargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        callingCargo = (SaleCargoIfc)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        ReversalCargo childCargo = (ReversalCargo)bus.getCargo();

        SaleReturnTransactionIfc saleReturnTransaction = callingCargo.getTransaction();
        List<ReversalRequestIfc> requestList = null;
        requestList = ReversalCargo.buildRequestList(
                        callingCargo.getRegister().getWorkstation(),
                        saleReturnTransaction.getTransactionType(),
                        callingCargo.getCurrentTransactionADO());
        childCargo.setRequestList(requestList);
        TransactionUtilityManagerIfc transactionUtilityManager = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        String transactionArchiveName = transactionUtilityManager.getArchiveName(saleReturnTransaction);
        childCargo.setTransactionArchiveName(transactionArchiveName);
    }

}
