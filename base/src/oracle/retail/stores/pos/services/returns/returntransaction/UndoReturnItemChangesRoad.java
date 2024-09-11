/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/UndoReturnItemChangesRoad.java /main/14 2012/10/29 12:55:22 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified use method on SaleReturnLineItemIfc rather
 *                         than calcualtating the value in the site.
 *                         OrderLineItem has its own implementation of this
 *                         method.
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:20 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:04:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:50   msg
 * Initial revision.
 * 
 *    Rev 1.3   22 Feb 2002 18:06:36   cir
 * Set the card number to empty string for non returnable gift card
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 *
 *    Rev 1.2   20 Feb 2002 16:35:32   cir
 * Check for non returnable gift card
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 *
 *    Rev 1.1   Feb 05 2002 16:43:26   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:25:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This aisle is traversed when the user presses UNDO on the get Item
 * Information screen.
 */
public class UndoReturnItemChangesRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 5481565291253545848L;

    /**
     * This aisle is traversed when the user undo on the get Item Information
     * screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Decrement the current item index.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();

        if (!cargo.isExternalOrder())
        {            
            // Undo the Quantity
            SaleReturnLineItemIfc item = cargo.getSaleLineItem();
            cargo.getReturnItem().setItemQuantity(item.getQuantityReturnable());
    
            int previousIndex = cargo.getCurrentItem() - 1;
            cargo.setCurrentItem(previousIndex);
        }
    }
}