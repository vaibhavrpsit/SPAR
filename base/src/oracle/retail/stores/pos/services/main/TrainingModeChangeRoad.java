/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/TrainingModeChangeRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:30:33 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:26:19 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:15:11 PM  Robert Pearse   
 *
 *  Revision 1.1  2004/03/14 21:12:41  tfritz
 *  @scr 3884 - New Training Mode Functionality
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This road sets the function to override and the training request in the cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TrainingModeChangeRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Set the function ID and the training request type.
        @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        MainCargo cargo = (MainCargo) bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.TRAINING_MODE);
    }

}
