/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/CheckSerialNumberRequiredSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   12/22/10 - Fix to remove 'Enter serial number' screen when item
 *                         is scanned through UIN
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/11/10 - Added for External Order Integration.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;

//--------------------------------------------------------------------------
/**
    Checks for external order processing.
**/
//--------------------------------------------------------------------------
public class CheckSerialNumberRequiredSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3438708460234003773L;

    //----------------------------------------------------------------------
    /**
       Check for external order processing and mail letter.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();
        String letter = "NotRequired";
        if (cargo.getPLUItem().getItemClassification().isSerializedItem() && cargo.getItemSerial()==null)
        {
            letter = "Required";
        }
            
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

}
