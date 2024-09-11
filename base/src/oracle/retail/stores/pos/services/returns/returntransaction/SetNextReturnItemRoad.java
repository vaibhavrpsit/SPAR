/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/SetNextReturnItemRoad.java /rgbustores_13.4x_generic_branch/2 2011/08/18 08:44:04 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    sgu       08/04/10 - redirect the return flow back to
 *                         DisplayExternalOrderDialogSite for any rejected
 *                         partially used gift card item
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse
 *
 *   Revision 1.2  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   26 Jan 2004 00:14:06   baa
 * continue return development
 *
 *    Rev 1.0   Aug 29 2003 16:06:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:04:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:25:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
 * This road sets up the cargo to process the next item in the list. If this
 * is an external order return, it marks the external order item as returned.
 **/
// --------------------------------------------------------------------------
public class SetNextReturnItemRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -8814468304058199449L;

    // ----------------------------------------------------------------------
    /**
       This method sets up the cargo to process the next item in the list. If this
       is an external order return, marks the external order as returned.
     * <P>
     *
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Increment the current item index.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        if (cargo.isExternalOrder() && cargo.getReturnItem() != null)
        {
            cargo.setAssociatedExternalOrderItemReturnedStatus(cargo.getPLUItem().getReturnExternalOrderItem(), true);
        }

        cargo.setCurrentItem(cargo.getCurrentItem() + 1);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of the object.
     * <P>
     *
     * @return String representation of object
     **/
    // ----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  SetNextReturnItemRoad (Revision " + ")" + hashCode());
        return (strResult);
    } // end toString()
}
