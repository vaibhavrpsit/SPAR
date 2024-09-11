/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 * This site displays the ITEM_VOIDSCREEN screen.
 *
 * @version $Revision: 1.1 $
 */
//--------------------------------------------------------------------------
public class MAXItemListScreenSite extends PosSiteActionAdapter
{
        
/**
	 * 
	 */
	private static final long serialVersionUID = 7862876883399248159L;

	//  ----------------------------------------------------------------------
    /**
     * Displays the LINEITEM_VOID screen.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	//Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel parModel =
            ((POSBaseBeanModel) ui.getModel(MAXPOSUIManagerIfc.LINEITEM_VOID_LIST)).getPromptAndResponseModel();
        
        if (parModel != null ) parModel.setResponseText("");
        ui.showScreen(MAXPOSUIManagerIfc.LINEITEM_VOID_LIST);
      //HCUtility.playScannerTone(HCUtilityIfc.SCAN_AND_VOID_PROMPT);
        
    }
    
    public void depart(BusIfc bus)
    {
       
    }
}