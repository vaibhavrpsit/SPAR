/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ResetItemSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nganesh   04/16/09 - Handled null condition check for transaction and
 *                         lineItems to avoid nullpointer exception
 *    jswan     04/14/09 - Fixed header - code review.
 *    jswan     04/14/09 - Modified to fix conflict between multi quantity
 *                         items and items that have been marked for Pickup or
 *                         Delivery.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This road is traversed when the user presses the
    Item key from the SELL_ITEM screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ResetItemSelectedRoad extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -1164233805245483555L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Reset the selected for item modify data member on all the sale return
       line items.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        SaleCargoIfc cargo                       = (SaleCargoIfc)bus.getCargo();


        SaleReturnTransactionIfc transaction     = cargo.getTransaction();

        if(transaction == null)
        {
        	return;
        }
        // Reset item modify selected flag on all sale return line items.
        AbstractTransactionLineItemIfc[] lineItems = transaction.getItemContainerProxy().getLineItems();

        if(lineItems == null)
        {
        	return;
        }

        for(int i = 0; i < lineItems.length; i++)
        {
            if (lineItems[i] instanceof SaleReturnLineItemIfc)
            {
                ((SaleReturnLineItemIfc)lineItems[i]).setSelectedForItemModification(false);
            }
        }
    }
}
