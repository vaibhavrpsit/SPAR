/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/EnterPickupSummaryAmountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:02 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Feb 04 2004 18:35:10   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

//--------------------------------------------------------------------------
/**
     Displays the SUMMARY_COUNT_PICKUP screen.
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnterPickupSummaryAmountSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Display the SUMMARY_COUNT_PICKUP screen. 
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Set the model
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        DataInputBeanModel dModel = new DataInputBeanModel();
        if (cargo.getPickupAndLoanFromRegister() != null)
        {
            // populate the model
            dModel.setValue("amountField", cargo.getCurrentAmount().getStringValue());
            dModel.setValue("toRegisterField", cargo.getPickupAndLoanToRegister());
        }
        /*
         * Ask the UI Manager to display the screen
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE); 
        ui.showScreen(POSUIManagerIfc.SUMMARY_COUNT_PICKUP, dModel);
    }
}
