/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		10/08/2013		Initial Draft:	CR:Restriction required in POS to display no.
  of MRPs to the user. Also, parameter is required to control the no. of MRPs to be shown to user.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.multiplemrp;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import max.retail.stores.domain.event.MAXPriceChange;
import max.retail.stores.domain.event.MAXPriceChangeIfc;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXMaximumRetailPriceListBeanModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
 * This site displays the muliple active mrp for a user to select for an item
 *
 * @version $Revision: /rgbustores_12.0.9in_branch/1 $
 */
//------------------------------------------------------------------------------
public class MAXDisplayActiveMRPSite extends PosSiteActionAdapter implements
SiteActionIfc {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String INACTIVE_MRP_BUTTON = "Display Inactive MRPs";

    public static final String YES = "Y";

    public static final String NO = "N";

    /**
     * Constant for the Override button action.
     * <P>
     */
    // --------------------------------------------------------------------------
    private static final String ACTION_INACTIVE_MRP = "PickInactiveMRP";

    // --------------------------------------------------------------------------
    /**
     *
     *
     * @param bus
     *                the bus arriving at this site
     */
    // --------------------------------------------------------------------------
    public void arrive(BusIfc bus) {
	/*
	 * Grab the item from the cargo
	 */
    	    	
	MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo) bus.getCargo();
	MAXPLUItemIfc pluItem = (MAXPLUItemIfc) cargo.getPLUItem();
	//MAXPriceChangeIfc spclEmpDisc = new MAXPriceChange();
	//System.out.println("77 spclEmpDisc================="+pluItem.getSpclEmpDisc());
	//System.out.println(!(pluItem.getEmpID() && pluItem.getSpclEmpDisc().equalsIgnoreCase("SpecEmpDisc"))+"========================MAXDisplayActiveMRPSite");
	if(!(pluItem.getEmpID() && pluItem.getSpclEmpDisc().equalsIgnoreCase("SpecEmpDisc")))
	{
		//if((pluItem.getEmpID() && pluItem.getPrice().equals("0")) || !pluItem.getEmpID()){}
	NavigationButtonBeanModel localButton = new NavigationButtonBeanModel();

	ParameterManagerIfc pm = (ParameterManagerIfc) bus
	.getManager(ParameterManagerIfc.TYPE);
	boolean showInActiveButton = showInActiveMRPButton(pm);

	// If there is no inactive mrp's then
	if (showInActiveButton
		&& pluItem.getInActiveMaximumRetailPriceChanges() == null
		|| pluItem.getInActiveMaximumRetailPriceChanges().length == 0) { // Disable
	    // the
	    // button
	    localButton.setButtonEnabled(ACTION_INACTIVE_MRP, false);
	} else if (!showInActiveButton) {
	    // Disable the button
	    localButton.setButtonEnabled(ACTION_INACTIVE_MRP, false);
	} else {
	    localButton.setButtonEnabled(ACTION_INACTIVE_MRP, true);
	}

	Letter letter = null;
	if (pluItem != null) {

	    try {

		// Create the BeanModel
		MAXMaximumRetailPriceListBeanModel beanModel = new MAXMaximumRetailPriceListBeanModel();
		int multipleMRPCount = 0;
		try {
			multipleMRPCount = pm.getIntegerValue("MultipleMRPCount").intValue();
		} catch (ParameterException e) {
			multipleMRPCount = 10;
			e.printStackTrace();
		}
		
		
				MAXMaximumRetailPriceChangeIfc[] multiplePriceChange = pluItem
						.getActiveMaximumRetailPriceChanges();
		MAXMaximumRetailPriceChangeIfc[] mPrice = new MAXMaximumRetailPriceChangeIfc[multipleMRPCount];
				Set tmp = new LinkedHashSet();
				for (int i = 0; i < multiplePriceChange.length; i++) {
					tmp.add(multiplePriceChange[i]);
				}
				MAXMaximumRetailPriceChangeIfc[] mPriceModified = new MAXMaximumRetailPriceChangeIfc[tmp
						.size()];
				Iterator tempitr = tmp.iterator();
				int j = 0;
				while (tempitr.hasNext()) {
					mPriceModified[j] = (MAXMaximumRetailPriceChangeIfc) tempitr
							.next();
					j++;
				}
				if (mPriceModified.length >= multipleMRPCount) {
		for(int i = 0;i<multipleMRPCount;i++){
						mPrice[i] = mPriceModified[i];
		}
				}
			  pluItem.getActiveMaximumRetailPriceChanges();
//		beanModel.setItemMaximumRetailPriceList(pluItem
//				.getActiveMaximumRetailPriceChanges());
			  if(mPriceModified.length>=multipleMRPCount)
		beanModel.setItemMaximumRetailPriceList(mPrice);
			  else{
				  beanModel.setItemMaximumRetailPriceList(mPriceModified);
			  }
		// Construct the models
		PromptAndResponseModel pandrModel = new PromptAndResponseModel();
		pandrModel.setArguments(pluItem.getItemID());
		beanModel.setPromptAndResponseModel(pandrModel);
		beanModel.setLocalButtonBeanModel(localButton);

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
	}}
	else
	{
		Letter letter = new Letter(CommonLetterIfc.CONTINUE);
		bus.mail(letter, BusIfc.CURRENT);
	}
   }


    // --------------------------------------------------------------------------
    /**
     *
     *
     * @param bus
     *                the bus departing from this site
     */
    // --------------------------------------------------------------------------
    public void depart(BusIfc bus) {
    }

    // --------------------------------------------------------------------------
    /**
     *
     *
     * @param bus
     *                the bus undoing its actions
     */
    // --------------------------------------------------------------------------
    public void undo(BusIfc bus) {
    }

    // --------------------------------------------------------------------------
    /**
     *
     *
     * @param bus
     *                the bus being reset
     */
    // --------------------------------------------------------------------------
    public void reset(BusIfc bus) {
    }

    /**
     * This method checks whether the Inactive MMRP enabled parmater is set
     * to true or false
     *
     * @param pm
     *
     * @return
     */
    private boolean showInActiveMRPButton(ParameterManagerIfc pm) {

	boolean inActiveMRPButtonEnabled = false;

	try {
	    String inActiveMRPParameterValue = pm
	    .getStringValue(INACTIVE_MRP_BUTTON);
	    if (inActiveMRPParameterValue != null) {
		inActiveMRPButtonEnabled = inActiveMRPParameterValue
		.equals(YES) ? true : false;
	    }

	} catch (ParameterException pe) {
	    logger
	    .error("Exceptiion while retrieving inActiveMRPButtonEnabled parameter"
		    + Util.throwableToString(pe) + "");
	}
	return inActiveMRPButtonEnabled;

    }
}
