/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/cancel/CancelOrderTenderReturnItemAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:34 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         2/22/2008 10:32:00 AM  Pardee Chhabra  CR
 *       30191: Tender refund options are not displayed as per specification
 *       for Special Order Cancel feature.
 * $
 * Revision 1.5  2004/03/19 19:13:57  epd
 * @scr 3561 fixed non-returnable items staying selected
 *
 * Revision 1.4  2004/03/05 21:46:58  epd
 * @scr 3561 Updates to implement select highest price item
 *
 * Revision 1.3  2004/03/05 16:01:17  epd
 * @scr 3561 code reformatting and slight refactoring
 *
 * Revision 1.2  2004/02/27 01:43:29  baa
 * @scr 3561 returns - selecting return items
 *
 * Revision 1.1  2004/02/24 22:08:14  baa
 * @scr 3561 continue returns dev
 *
 * Revision 1.2  2004/02/24 15:15:34  baa
 * @scr 3561 returns enter item
 *
 * Revision 1.1  2004/02/23 13:54:52  baa
 * @scr 3561 Return Enhancements to support item size
 * Revision 1.1 2004/02/19 15:37:27 baa @scr 3561
 * returns
 * 
 * Revision 1.1 2004/02/18 20:36:20 baa @scr 3561 Returns changes to support size
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.cancel;

// java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

// --------------------------------------------------------------------------
/**
 * This aisle gets reset the return item info into order transaction.
 * <p>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
// --------------------------------------------------------------------------
public class CancelOrderTenderReturnItemAisle extends PosLaneActionAdapter {

	/**
	 * revision number supplied by Team Connection
	 */
	public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	/**
	 * This aisle gets reset the return item info into order transaction.
	 * 
	 */
	public void traverse(BusIfc bus) {
		
		PickupOrderCargo cargo = (PickupOrderCargo) bus.getCargo();
		
		SaleReturnTransactionIfc transaction = cargo.getTransaction();
		transaction.setReturnTenderElements(null);
		AbstractTransactionLineItemIfc[] lineItems=transaction.getLineItems();
		if(lineItems != null && lineItems.length>0){
			int numItems = lineItems.length;
	        for (int i = 0; i < numItems; i++)
            {
                if (lineItems[i] instanceof SaleReturnLineItemIfc)
                {
                    ((SaleReturnLineItemIfc) lineItems[i]).setReturnItem(null);
                    
                }
            }
		}
		bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
	}

}// ends class
