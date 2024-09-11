/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/InTrainingModeSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:26 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.2  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/04/07 17:50:56  tfritz
 *   @scr 3884 - Training Mode rework
 *
 *   Revision 1.1  2004/03/26 20:06:27  tfritz
 *   @scr 4136 - An error dialog now comes up when trying to open a store while in training mode
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// Foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal determines whether training mode is currently on.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class InTrainingModeSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8569349101547947266L;

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Returns true if we're in Training Mode.
        @return boolean
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean inTrainingMode = false;
        AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        
        if (register != null)
        {
            inTrainingMode = register.getWorkstation().isTrainingMode();
        }
        
        return (inTrainingMode);
    }
}
