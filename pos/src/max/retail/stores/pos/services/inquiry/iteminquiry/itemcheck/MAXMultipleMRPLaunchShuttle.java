/*===========================================================================
 * Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Log:
 *
 *
 * ===========================================================================
 */

package max.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import max.retail.stores.pos.services.sale.multiplemrp.MAXMultipleMRPCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * @author vichandr
 *
 */
public class MAXMultipleMRPLaunchShuttle implements ShuttleIfc {
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";
    
	public static final String APPLY_BEST_DEAL = "ApplyDiscounts";
//	protected static Logger logger = Logger.getLogger(com.max.pos.services.inquiry.iteminquiry.itemcheck.MAXMultipleMRPLaunchShuttle.class);

    /**
     * MultipleMRP cargo
     *//*
    MAXMultipleMRPCargo mCargo = null;

    *//**
     * The line item to change multiple mrp
     *//*
    protected PLUItemIfc item = null;
    protected boolean isApplyBestDeal = false;
    protected String initialLetter = null;
    
     * (non-Javadoc)
     *
     * @see com.extendyourstore.foundation.tour.ifc.ShuttleIfc#load(com.extendyourstore.foundation.tour.ifc.BusIfc)
     
    public void load(BusIfc bus) {
	// retrieve cargo from the parent
	MAXItemInquiryCargo cargo = (MAXItemInquiryCargo) bus.getCargo();
	item = cargo.getPLUItem();
	isApplyBestDeal = cargo.isApplyBestDeal();
	initialLetter = cargo.getInitialOriginLetter();

    }

    
     * (non-Javadoc)
     *
     * @see com.extendyourstore.foundation.tour.ifc.ShuttleIfc#unload(com.extendyourstore.foundation.tour.ifc.BusIfc)
     
    public void unload(BusIfc bus) {
	// TODO Auto-generated method stub
	// get cargo reference and set attributes
	MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo) bus.getCargo();
	cargo.setApplyBestDeal(isApplyBestDeal);
	cargo.setInitialOriginLetter(initialLetter);
	cargo.setPLUItem(item);

    }
    
*/
    protected MAXItemInquiryCargo cargo = null;
    public void load(BusIfc bus)
    {
    	cargo = (MAXItemInquiryCargo)bus.getCargo();
    }
    public void unload(BusIfc bus)
    {
    	MAXMultipleMRPCargo mcargo = (MAXMultipleMRPCargo)bus.getCargo();
    	mcargo.setPLUItem(cargo.getPLUItem());
    	mcargo.setApplyBestDeal(cargo.isApplyBestDeal());
    	mcargo.setInitialOriginLetter(cargo.getInitialOriginLetter());
    //	logger.info("SAKSHI Frustrated: Setting changes for best deal, in if block: "+cargo.isApplyBestDeal());
    	if(cargo.getInitialOriginLetter() != null && cargo.getInitialOriginLetter().equalsIgnoreCase(APPLY_BEST_DEAL) || mcargo.isApplyBestDeal())
		{
    		mcargo.setApplyBestDeal(true);	
		}
    }

}
