/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/StoreStatusIsStaleErrorAisle.java /main/6 2014/07/23 15:44:30 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/23/14 - Code review updates
 *    rhaight   07/15/14 - Support for cancelling offline store open
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/1/2008 2:30:37 PM    Deepti Sharma   CR
 *       31016 forward port from v12x -> trunk
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle displays the store status stale error dialog
    $Revision: /main/6 $
**/
//--------------------------------------------------------------------------
public class StoreStatusIsStaleErrorAisle extends LaneActionAdapter
{
    /** Serial Version ID */
	private static final long serialVersionUID = -1935527575902605119L;
	
	/**
     revision number of this class
     **/
    public static String revisionNumber = "$Revision: /main/6 $";
    //--------------------------------------------------------------------------
    /**
     Displays error message.<P>
     
     @since 14.1
     
     Stores a copy of the current store status to support offline 
     store open cancel operations
     
     @param bus the bus traversing this site
     **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
    	
    	// Get a copy of the initial store status incase the offline store open is cancelled
    	RegisterOpenCargo cargo = (RegisterOpenCargo)bus.getCargo();
    	StoreStatusIfc status = cargo.getStoreStatus();
    	cargo.setRollbackStoreStatus((StoreStatusIfc)status.clone());
    	
        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("StoreStatusStale");
        model.setType(DialogScreensIfc.YES_NO);
        model.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Cancel");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
     Returns the revision number of the class. <P>
     @return String representation of revision number
     **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
