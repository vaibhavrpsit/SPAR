/*===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RefundToOriginalCardWithTokenSignal.java /rgbustores_13.4x_generic_branch/2 2011/10/17 14:55:57 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         09/08/11 - add house account as a refund tender
* sgu         09/01/11 - add a new signal
* sgu         09/01/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;

import org.apache.log4j.Logger;


public class RefundToOriginalCardWithTokenSignal implements TrafficLightIfc
{
    private static final long serialVersionUID = 879376702016873528L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(RefundToOriginalCardWithTokenSignal.class);

    public boolean roadClear(BusIfc bus)
    {
        logger.debug("RefundToOriginalCardWithTokenSignal.roadClear() - entry");

        boolean result = false;
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        String token = (String)cargo.getTenderAttributes().get(TenderConstants.ACCOUNT_NUMBER_TOKEN);

        if (cargo.refundToOriginalCard() && !Util.isEmpty(token))
        {
            result = true;
        }

        logger.debug("RefundToOriginalCardWithTokenSignal.roadClear() - exit");
        return (result);
    }
}

