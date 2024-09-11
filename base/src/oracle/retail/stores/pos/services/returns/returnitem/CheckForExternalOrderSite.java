/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/CheckForExternalOrderSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/14/10 - fix identation
 *    sgu       09/14/10 - increment the current index for the first item
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warnings caused by generics.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/13/10 - Added site to check for External Order.
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
public class CheckForExternalOrderSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -2181843395687659684L;

    //----------------------------------------------------------------------
    /**
       Check for external order processing and mail letter.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ReturnItemCargoIfc cargo   = (ReturnItemCargoIfc)bus.getCargo();
        String letter;

        // Set the current starting point for processing returnSaleItems
        // array.
        int currentIndex = -1;
        if (cargo.getReturnSaleLineItems() != null)
        {
            currentIndex = cargo.getReturnSaleLineItems().length -1;
        }
        cargo.setLastLineItemReturnedIndex(currentIndex);

        // move the index to the new item to be added
        cargo.setCurrentItem(currentIndex + 1);

        // Determine whether to process items as external order items.
        if (cargo.isExternalOrder())
        {
            letter = "ExternalOrder";
        }
        else
        {
            letter = "Next";
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
