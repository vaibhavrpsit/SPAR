/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/SetNextReturnItemRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
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
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed to increment the current item.
**/
//--------------------------------------------------------------------------
public class SetNextReturnItemRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8083557010192273176L;

    //----------------------------------------------------------------------
    /**
        This aisle is traversed to increment the current item.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Increment the current item index.
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();
        cargo.setCurrentItem(cargo.getCurrentItem() + 1);
        if (cargo.getReturnSaleLineItems() != null)
        {
            SaleReturnLineItemIfc item = cargo.getReturnSaleLineItems()[cargo.getCurrentItem()];
            cargo.setPLUItem(item.getPLUItem());
            cargo.setPrice(item.getSellingPrice());
            cargo.getReturnItem().setItemQuantity(item.getQuantityReturnable());
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  SetNextReturnItemRoad (Revision " +
                                      ")" + hashCode());
        return(strResult);
    }                                   // end toString()
}
