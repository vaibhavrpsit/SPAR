/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ReturnSuccessProcessingRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
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
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:55 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   05 Feb 2004 23:24:12   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:06:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 10 2003 17:38:12   sfl
 * Coment out the duplicate partial return processing.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:04:32   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:14   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.0   Mar 18 2002 11:46:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:43:24   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:25:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// java imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This road set the the transfer flag to true and clears the selected list.
**/
//--------------------------------------------------------------------------
public class ReturnSuccessProcessingRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 935449649015972925L;
    /**
       lane name constant
    **/
    public static final String LANENAME = "ReturnSuccessProcessingRoad";

    //----------------------------------------------------------------------
    /**
       Gets the selected transacation index from the ui.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // Every thing is good to go.
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        cargo.setTransferCargo(true);

        // Set the last item returned index.
        cargo.setLastLineItemReturnedIndex(cargo.getCurrentItem());
        // If the return is from an external order, set the return indicator for the
        // corresponding external order item to true; This tracks which external 
        // orders have been returned.  This helps fulfill the requirement that all
        // external order items must be returned.
        if (cargo.isExternalOrder())
        {
            cargo.setAssociatedExternalOrderItemReturnedStatus(cargo.getPLUItem().getReturnExternalOrderItem(),
                    true);
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
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append(LANENAME)
            .append(") @").append(hashCode());
        return(strResult.toString());
    }                                   // end toString()
}
