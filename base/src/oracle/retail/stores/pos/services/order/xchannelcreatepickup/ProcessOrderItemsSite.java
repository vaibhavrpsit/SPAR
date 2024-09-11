/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/ProcessOrderItemsSite.java /main/3 2013/08/27 14:46:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  08/25/13 - Xchannel Inventory lookup enhancement phase I
 *    jswan     05/14/12 - Modified to fix issue with split of multi-quantity
 *                         line items.
 *    jswan     04/18/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//------------------------------------------------------------------------------
/**
    This class determines if all the selected line items have been process by
    the tour.
    @version $Revision: /main/3 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ProcessOrderItemsSite extends PosSiteActionAdapter
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";

    //--------------------------------------------------------------------------
    /**
        This class determines if all the selected line items have been process by
        the tour.
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        int diffIndex=cargo.getLineItemsBucket().size();
        
        if (cargo.getLineItemIndex() > diffIndex - 1)
        {
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        else
        {
            bus.mail(CommonLetterIfc.NEXT, BusIfc.CURRENT);
        }
    }
}
