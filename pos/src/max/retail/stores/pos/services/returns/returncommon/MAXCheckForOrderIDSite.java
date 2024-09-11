/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  08 Nov, 2016              Nadia              MAX-StoreCredi_Return requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractFindTransactionCargo;

/**
 * This site action determines if an order id is available in the
 * AbstractFindTransactionCargo; It mails a "TransactionHasOrder" if one is
 * available and a "Continue" if not.
 * <p>
 * As of 14.0, if the cargo's transaction has the order marked as an ATG-ASA
 * order (i.e. started and maintained by the web commerce stack) then the order
 * does not need to be retrieved since ATG has not yet provided return web
 * services.
 * <p>
 * Ordinarily as simple switch like this would be a {@link TrafficLightIfc};
 * however, due to the complexity of the return tours in the order lookup must
 * be added, this simple site action reduces the number modifications required.
 */
@SuppressWarnings("serial")
public class MAXCheckForOrderIDSite extends PosSiteActionAdapter implements SiteActionIfc
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AbstractFindTransactionCargo cargo = (AbstractFindTransactionCargo)bus.getCargo();
        /*String letter = ReturnUtilities.TRANSACTION_HAS_ORDER;

        if (Util.isEmpty(cargo.getSelectedTransactionOrderID()) ||
                (cargo.getTransaction().isWebManagedOrder()))
        {
            letter = CommonLetterIfc.CONTINUE;
        }*/

        bus.mail(CommonLetterIfc.CONTINUE);
    }
}
