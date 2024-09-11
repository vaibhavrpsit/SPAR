/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/CustomerUndoRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

//foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


//------------------------------------------------------------------------------
/**
    Resets data for the Undo road.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CustomerUndoRoad extends PosLaneActionAdapter
{
    /**
        lane name constant
    **/
    public static final String LANENAME = "CustomerUndoRoad";

    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Sets customer to null     
     * @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();

        //set the customer to null
        if (cargo.getCustomer() != null) 
        {
            cargo.setCustomer(null);
        }
    }
}
