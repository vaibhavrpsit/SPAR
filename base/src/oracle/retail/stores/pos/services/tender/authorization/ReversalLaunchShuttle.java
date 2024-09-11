/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/ReversalLaunchShuttle.java /main/5 2013/11/21 18:12:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/21/13 - removed unused imports
 *    asinton   11/15/13 - refactored to use TransactionUtilityManager to
 *                         obtain transactionArchiveName
 *    asinton   11/14/13 - added code to carry the transaction archive name to
 *                         reversal and authorization services for transaction
 *                         archival to support potential reversal of pending
 *                         authorizations in the case of application crash
 *    blarsen   08/08/13 - Disambiguate POS transactoinID from payment service
 *                         transactionID
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    blarsen   07/22/11 - Initial version.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.manager.payment.ReversalRequestIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.tender.reversal.ReversalCargo;

/**
 * Shuttle the information required by the Reversal Service
 */
public class ReversalLaunchShuttle implements ShuttleIfc
{
    private static final long serialVersionUID = -155925180621171325L;

    protected AuthorizationCargo callingCargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        callingCargo = (AuthorizationCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        ReversalCargo childCargo = (ReversalCargo)bus.getCargo();
        List<ReversalRequestIfc> requestList = new ArrayList<ReversalRequestIfc>();

        ReversalRequestIfc reversalRequest = ReversalCargo.buildRequest(
                        callingCargo.getCurrentRequest(),
                        callingCargo.getCurrentResponse(),
                        callingCargo.getCurrentTransactionADO() != null? callingCargo.getCurrentTransactionADO().getTransactionID() : "");

        requestList.add(reversalRequest);
        childCargo.setRequestList(requestList);
        TransactionUtilityManagerIfc transactionUtilityManager = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        TransactionIfc transaction = (TransactionIfc)callingCargo.getCurrentTransactionADO().toLegacy();
        String transactionArchiveName = transactionUtilityManager.getArchiveName(transaction);
        childCargo.setTransactionArchiveName(transactionArchiveName);
    }

}
