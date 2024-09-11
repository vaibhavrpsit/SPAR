/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/IsCheckLessThanFloorLimitSignal.java /rgbustores_13.4x_generic_branch/1 2011/10/20 11:12:28 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * Determines if tender is a check and, if so, is the tendered amount 
 * less than the floor limit.
 * @author asinton
 * @since 13.4
 *
 */
@SuppressWarnings("serial")
public class IsCheckLessThanFloorLimitSignal implements TrafficLightIfc
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        boolean isCheckAndLessThanFloorLimit = false;
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        if(TenderType.CHECK.equals(response.getTenderType()))
        {
            if(response.getFloorLimit().compareTo(response.getBaseAmount()) == CurrencyIfc.GREATER_THAN)
            {
                isCheckAndLessThanFloorLimit = true;
            }
        }
        return isCheckAndLessThanFloorLimit;
    }

}
