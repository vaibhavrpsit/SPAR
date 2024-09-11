/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ResetRestockingFeeAndSetNextItemRoad.java /main/14 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    jswan     07/21/10 - Merge change.
 *    jswan     07/20/10 - Fixed crash in restocking fee associated with
 *                         external order.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/03/25 15:07:15  baa
 *   @scr 3561 returns bug fixes
 *
 *   Revision 1.5  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.4  2004/03/22 06:17:50  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   05 Feb 2004 23:24:12   baa
 * Initial revision.
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
package oracle.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.utility.TransactionUtilityManager;

/**
 * This aisle is traversed when the user presses ACCEPT on the get Item
 * Information screen.
 */
public class ResetRestockingFeeAndSetNextItemRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -1440225895746341624L;

    /**
     * Gets the selected transacation index from the ui.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Increment the current item index.
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();

        // First Reset current item's restocking fee to 0.0
        cargo.getReturnItem().setRestockingFee(DomainGateway.getBaseCurrencyInstance());
        TransactionUtilityManager.setRestockingFeeOverriddenTransaction(true);

        // Set the last item returned index.
        cargo.setLastLineItemReturnedIndex(cargo.getCurrentItem());
        /* 
         * If the return is from an external order, set the return indicator for
         * the corresponding external order item to true; This tracks which
         * external / orders have been returned. This helps fulfill the
         * requirement that all external order items must be returned.
         */
        if (cargo.isExternalOrder())
        {
            cargo.setAssociatedExternalOrderItemReturnedStatus(cargo.getPLUItem().getReturnExternalOrderItem(), true);
        }

        // Set cargo to point to the next item on the list
        cargo.setCurrentItem(cargo.getCurrentItem() + 1);

        if (cargo.getReturnSaleLineItems() != null)
        {
            SaleReturnLineItemIfc item = cargo.getReturnSaleLineItems()[cargo.getCurrentItem()];
            cargo.setPLUItem(item.getPLUItem());
            cargo.setPrice(item.getSellingPrice());

            // The return item is not available for external orders; it is not
            // created until the item has actually been returned.
            if (!cargo.isExternalOrder())
            {
                cargo.getReturnItem().setItemQuantity(item.getQuantityReturnable());
            }
        }

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#toString()
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  SetNextReturnItemRoad (Revision " + ")" + hashCode());
        return (strResult);
    }
}