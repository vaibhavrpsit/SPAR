/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/SkipReceiptOptionsSignal.java /main/2 2012/11/30 15:36:48 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/30/12 - Added customer receipt preference
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;

/**
 * Road should only be clear if the cargo contains customer email address and
 * skip receipt option parameter is true.
 */
public class SkipReceiptOptionsSignal implements TrafficLightIfc
{

    private static final long serialVersionUID = 2370138321082361063L;

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle
     * .retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        boolean skipReceiptOptions = false;

        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        if (trans.getCustomer() != null
                && trans.getCustomer().getReceiptPreference() != ReceiptConstantsIfc.RECEIPT_PREFERENCE_UNKNOWN)
        {
            ParameterManagerIfc paramMgr = (ParameterManagerIfc) Gateway.getDispatcher().getManager(
                    ParameterManagerIfc.TYPE);
            // See if fiscal printing is enabled
            boolean isFiscalPrintingEnabled = Gateway.getBooleanProperty("application", "FiscalPrintingEnabled", false);
            try
            {
                skipReceiptOptions = paramMgr
                        .getBooleanValue(ParameterConstantsIfc.PRINTING_SkipReceiptPrintingOptions)
                        && !isFiscalPrintingEnabled;
            }
            catch (ParameterException e)
            {
                // Do nothing
            }
        }
        return skipReceiptOptions;
    }

}
