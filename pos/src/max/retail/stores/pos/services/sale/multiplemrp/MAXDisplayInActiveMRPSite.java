/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

     $Log$
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.multiplemrp;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXMaximumRetailPriceListBeanModel;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;


//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_12.0.9in_branch/1 $
**/
//------------------------------------------------------------------------------

public class MAXDisplayInActiveMRPSite extends PosSiteActionAdapter implements SiteActionIfc
{

    static final long serialVersionUID = 8726461900194986465L;

    private static final String ACTION_INACTIVE_MRP = "PickInactiveMRP";
    //--------------------------------------------------------------------------
    /**


            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
	/*
	 * Grab the item from the cargo
	 */
	MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo) bus.getCargo();
	PLUItemIfc pluItem = cargo.getPLUItem();
	Letter letter = null;
	if (pluItem != null) {

	    try {

		// Create the BeanModel
		MAXMaximumRetailPriceListBeanModel beanModel = new MAXMaximumRetailPriceListBeanModel();
		beanModel.setItemMaximumRetailPriceList(((MAXPLUItemIfc)pluItem).getInActiveMaximumRetailPriceChanges());
		NavigationButtonBeanModel localButton = new NavigationButtonBeanModel();
		localButton.setButtonEnabled(ACTION_INACTIVE_MRP, false);
		beanModel.setLocalButtonBeanModel(localButton);
		// Construct the models
		PromptAndResponseModel pandrModel = new PromptAndResponseModel();
		pandrModel.setArguments(pluItem.getItemID());
		beanModel.setPromptAndResponseModel(pandrModel);

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		ui.showScreen(MAXPOSUIManagerIfc.PICK_ONE_MRP, beanModel);
		ui.setModel(MAXPOSUIManagerIfc.PICK_ONE_MRP, beanModel);
		// letter = new Letter(CommonLetterIfc.CONTINUE);
	    } catch (Exception e) {
		logger.warn("Error: " + e.getMessage() + " \n " + e + "");
		letter = new Letter(CommonLetterIfc.CONTINUE);
	    }
	} else {
	    letter = new Letter(CommonLetterIfc.CONTINUE);
	}

	if (letter != null) {
	    bus.mail(letter, BusIfc.CURRENT);
	}
    }

    //--------------------------------------------------------------------------
    /**


            @param bus the bus departing from this site
    **/
    //--------------------------------------------------------------------------

    public void depart(BusIfc bus)
    {
    }


    //--------------------------------------------------------------------------
    /**


            @param bus the bus undoing its actions
    **/
    //--------------------------------------------------------------------------

    public void undo(BusIfc bus)
    {
    }

    //--------------------------------------------------------------------------
    /**


            @param bus the bus being reset
    **/
    //--------------------------------------------------------------------------

    public void reset(BusIfc bus)
    {
    }

}
