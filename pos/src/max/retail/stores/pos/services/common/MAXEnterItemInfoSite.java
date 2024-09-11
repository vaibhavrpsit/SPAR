/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.1	 May 04, 2017		    Kritica Agarwal  GST Changes
 * Rev 1.0   Feb 07,2017    		Ashish Yadav     Changes for Item not found bug
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.EnterItemInfoSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site displays the ITEM_NOT_FOUND screen to allow the user to enter the
 * required information for the item.
 * 
 * @version $Revision: /main/23 $
 */
public class MAXEnterItemInfoSite extends EnterItemInfoSite
{
    private static final long serialVersionUID = -25975074343L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/23 $";

        /**
     * Displays the ITEM_NOT_FOUND form to allow the user to enter the required
     * information for the item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        
    	//Change for rev 1.1 :Starts
        if(bus.getCurrentLetter().getName().equals("HSNnotFound")){
      	// initialize model bean
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("ITEM_HSN_NOT_FOUND");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.INVALID);
            // display dialog
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }else{
      	//Change for rev 1.1 :Ends
           // initialize model bean
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("ITEM_NOT_FOUND");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.INVALID);
            // display dialog
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        
    }

     
}
