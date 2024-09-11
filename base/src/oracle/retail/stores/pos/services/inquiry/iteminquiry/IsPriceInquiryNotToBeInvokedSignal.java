/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/IsPriceInquiryNotToBeInvokedSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  09/30/08 - Added generated serialVersionUID
 *    mchellap  09/29/08 - QW-IIMO Updates for code review comments
 *    mchellap  09/19/08 - QW-IIMO
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal checks to skip price inquiry screen
**/
//--------------------------------------------------------------------------
public class IsPriceInquiryNotToBeInvokedSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    private static final long serialVersionUID = 1621973331149131898L;

    //----------------------------------------------------------------------
    /**
        Checks to make sure that price inquiry is to be skipped
        @return boolean true if the  price inquiry is to be skipped false otherwise.
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean result = false;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.isSkipPriceInquiryFlag())
        {
           result = true;
        }
        return(result);
    }
}
