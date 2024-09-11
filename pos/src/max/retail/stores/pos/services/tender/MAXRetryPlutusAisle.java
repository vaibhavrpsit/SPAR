/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.0  22/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle opens the cash drawer.

     @version $Revision: 1.1 $
**/
//--------------------------------------------------------------------------
public class MAXRetryPlutusAisle extends PosLaneActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5269768601051180276L;
	/**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 1.1 $";


    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // Initially set cash drawer status to ONLINE
        ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("PlutusAuthConnFail");
        model.setType(DialogScreensIfc.RETRY_CONTINUE);
        model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,"Retry");
        model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE,"Continue");
        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
