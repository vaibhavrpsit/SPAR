/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/ResetCargoRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:50:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   28 Nov 2001 16:58:36   baa
 * Initial revision.
 * Resolution for POS SCR-306: 'Cancel' on 'Item Inventory' does not return to calling service
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry;

// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This road is traveled when the user selects an item
    It stores the item  in the cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ResetCargoRoad extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Stores the item info and dept list  in the cargo.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        InquiryOptionsCargo cargo = (InquiryOptionsCargo)bus.getCargo();

        // Reset Cargo
        cargo.setModifiedFlag(false);

    }
}
