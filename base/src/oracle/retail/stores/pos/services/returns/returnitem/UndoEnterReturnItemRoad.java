/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/UndoEnterReturnItemRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
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
 *    Rev 1.1   Feb 09 2004 10:36:58   baa
 * return - item not found
 * 
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 25 2002 15:06:16   blj
 * remove comments from this file.
 * Resolution for POS SCR-923: Started return by gift receipt.  Esc to do the return by item-got gift rec item screen
 * 
 *    Rev 1.0   Feb 25 2002 14:56:30   blj
 * Initial revision.
 * 
 *    Rev 1.2   Feb 05 2002 16:43:20   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.1   Dec 10 2001 17:23:40   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
    
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class UndoEnterReturnItemRoad extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -8245888511838168141L;

    //--------------------------------------------------------------------------
    /**
             
        This road set gift receipt flag to false.                   
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        cargo.setTransferCargo(false);
        
        // If this is an External Order return...
        if (cargo.isExternalOrder() && cargo.getReturnSaleLineItems() != null)
        {
            int removeIndex       = cargo.getLastLineItemReturnedIndex() + 1;
            int returnItemsLength = cargo.getReturnSaleLineItems().length;
            // Remove all item that have not completed the return process.  This
            // is necessary due to the fact that his tour reentrant for external orders.
            while(returnItemsLength > removeIndex)
            {
                ReturnItemIfc returnItem = cargo.getReturnItems()[removeIndex];
                cargo.removeReturnItem(returnItem);
                SaleReturnLineItemIfc saleReturnLineItem = cargo.getReturnSaleLineItems()[removeIndex];
                cargo.removeReturnSaleLineItem(saleReturnLineItem);
                if (cargo.getReturnSaleLineItems() == null)
                {
                    returnItemsLength = 0;
                }
                else
                {
                    returnItemsLength = cargo.getReturnSaleLineItems().length;
                }
            }
            
            if (returnItemsLength > 0)
            {
                cargo.setTransferCargo(true);
            }
        }
    }

}
