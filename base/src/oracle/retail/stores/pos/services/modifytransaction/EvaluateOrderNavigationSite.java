/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - Refactor to restore Fulfillment main option flow.
 *    asinton   10/22/14 - Initial checkin
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.IsXChannelOrderSignal;

/**
 * Decides whether or not to go Order or Special Order by evaluation of 
 * configuration and cargo state.
 * 
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class EvaluateOrderNavigationSite extends PosSiteActionAdapter
{
    /** Order letter */
    public static String ORDER_LETTER = "Order";

    /** Special order letter */
    public static String SPECIAL_ORDER_LETTER = "SpecialOrder";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter = SPECIAL_ORDER_LETTER;
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        boolean isXChannel = Gateway.getBooleanProperty(IsXChannelOrderSignal.APPLICATION_PROPERTY_GROUP_NAME, IsXChannelOrderSignal.XCHANNEL_ENABLED, false);
        if(cargo.isFromFulfillment() || isXChannel)
        {
            letter = ORDER_LETTER;
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

}
