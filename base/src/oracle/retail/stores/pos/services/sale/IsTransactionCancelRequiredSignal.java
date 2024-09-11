/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/IsTransactionCancelRequiredSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    jswan     10/29/10 - Added to determine if there is a transaction that
 *                         should be canceled.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

import org.apache.log4j.Logger;

/**
 * This signal checks to see if the transaction should be canceled.
 */
public class IsTransactionCancelRequiredSignal implements TrafficLightIfc
{
    static final long serialVersionUID = 2657612323986955398L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(IsTransactionCancelRequiredSignal.class);

    /**
     * Checks to see if the transaction should be canceled; the criteria are:
     * 
     *  1. If the transaction is not null AND
     *  2. If there are no line items on the transaction
     *  
     * @return true if the transaction should be canceled.
     */
    public boolean roadClear(BusIfc bus)
    {
        boolean result = true;
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        if (cargo.getTransaction() == null)
        {
            result = false;
        }
        else
        if (cargo.getTransaction().getLineItems() != null && 
            cargo.getTransaction().getLineItems().length > 0)
        {
            result = false;
        }

        return result;
    }
}
