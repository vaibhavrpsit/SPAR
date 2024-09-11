/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/IsNotGiftReceiptItem.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:18 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/09/23 00:07:14  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;


//------------------------------------------------------------------------------
/**
     
    @version $KW=@(#); $Ver=010601:3; $EKW;
**/
//------------------------------------------------------------------------------

public class IsNotGiftReceiptItem implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7644804704110159837L;


    //--------------------------------------------------------------------------
    /**
            roadClear determines whether it is safe for the bus to proceed

            @param bus the bus trying to proceed
            @return true if safe; false otherwise
    **/
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();

        return (!cargo.isGiftReceiptSelected());
    }
}
