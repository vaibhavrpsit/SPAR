/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ResetCurrentItemRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/02/27 01:43:29  baa
 *   @scr 3561 returns - selecting return items
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
    This aisle is traversed when the user presses ACCEPT on the get Item
    Information screen.
**/
//--------------------------------------------------------------------------
public class ResetCurrentItemRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -8773178909325243617L;

    //----------------------------------------------------------------------
    /**
       Gets the selected transaction index from the ui.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Increment the current item index.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();
        cargo.setCurrentItem(0);
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
