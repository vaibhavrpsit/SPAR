/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

 $Log$
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.multiplemrp;

import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

// ------------------------------------------------------------------------------
/**
 *
 * @version $Revision: /rgbustores_12.0.9in_branch/1 $
 * @since 12.0.9IN
 */
// ------------------------------------------------------------------------------
public class MAXDetermineMultipleMRPSite extends PosSiteActionAdapter implements
	SiteActionIfc {
    static final long serialVersionUID = 8726461900194986465L;

    public static String PARAMETER_MULTIPLE_MRP = "Allow MRP Selection";

    public static String PICK_ONE_ITEM_MRP_PROMPT = "PickOneItemMRPPrompt";

    public static final String YES = "Y";

    public static final String NO = "N";

    // ----------------------------------------------------------------------
    /**
     * Checks the item to see if mmrp is enabled for item.If enabled gets
     * the list of mrp's from the database.
     * <P>
     *
     * @param bus
     *                Service Bus
     */
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus) {

	/*
	 * Grab the item from the cargo
	 */
	MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo) bus.getCargo();
	MAXPLUItemIfc pluItem = (MAXPLUItemIfc) ((MAXMultipleMRPCargo) cargo).getPLUItem();
	System.out.println("58 :"+pluItem.getPrice());
    String originLetter = cargo.getInitialOriginLetter();
	Letter letter = null;
	//System.out.println("MAXDetermineMultipleMRPSite :"+pluItem.toString());
	//logger.info("SAKSHI Frustrated: Setting changes for best deal, in if block: "+cargo.isApplyBestDeal());
    if(cargo.isApplyBestDeal() ||(originLetter != null && originLetter.equalsIgnoreCase("Tender")))
	{
		 bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
	}
	else
	{
	
			try {
			    pluItem = (MAXPLUItemIfc) ReflectionUtility.getAttribute(cargo,
				    "PLUItem");
			} catch (Exception e) {
			    logger.error(e.getMessage());
			}
		
			ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		
			if (pluItem.getRetailLessThanMRPFlag()
				&& showMultipleMRPScreen(pm, pluItem)) {
			    letter = new Letter(MAXMaximumRetailPriceChangeIfc.PICK_ONE_MRP);
			}
		
			else {
			    letter = new Letter(CommonLetterIfc.CONTINUE);
			}
		
			if (letter != null) {
			    bus.mail(letter, BusIfc.CURRENT);
			}
		}	
    }

    /**
     * This method checks whether the Item supports MultipleMRP and the
     * Multiple MRP Parameter is enabled
     *
     * @param pm
     * @param pluitem
     * @return
     */
    private boolean showMultipleMRPScreen(ParameterManagerIfc pm,
	    MAXPLUItemIfc pluItem) {

	boolean mmrpEnabled = false;
	boolean mmrpParameterEnabled = false;
	try {
	    String mmrpParameterValue = pm
		    .getStringValue(PARAMETER_MULTIPLE_MRP);
	    
	    if (mmrpParameterValue != null) {
		mmrpParameterEnabled = mmrpParameterValue.equals(YES) ? true
			: false;
	    }
	    if (mmrpParameterEnabled
		    && ((MAXPLUItemIfc) pluItem).getMultipleMaximumRetailPriceFlag()) {
		if (pluItem.hasInActiveMaximumRetailPriceChanges()) {
		    mmrpEnabled = true;
		} else if (pluItem.getActiveMaximumRetailPriceChanges().length > 1) {
		    mmrpEnabled = true;
		}
	    }
	   // System.out.println("mmrpParameterValue ::"+mmrpParameterValue);
	} catch (ParameterException pe) {
	    logger.error("Exceptiion while retrieving MMRPParameterEnabled"
		    + Util.throwableToString(pe) + "");
	}
	 //System.out.println("mmrpParameterValue ::"+pluItem.toString());
	return mmrpEnabled;

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

}
