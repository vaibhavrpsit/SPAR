/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/AboutLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *  1    360Commerce 1.0         11/8/2006 8:53:28 AM   Keith L. Lesikar 
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoLaunchShuttle;

public class AboutLaunchShuttle extends UserAccessCargoLaunchShuttle 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 
	    The logger to which log messages will be sent.
	**/
	protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.AboutLaunchShuttle.class);
	// Class name
	public static final String SHUTTLENAME = "AboutLaunchShuttle";
	
	//--------------------------------------------------------------------------
	/**
	   Load register from admin cargo
	
	   @param bus the bus being loaded
	**/
	//--------------------------------------------------------------------------
	public void load(BusIfc bus)
	{
	    super.load(bus);
	}
	
	//--------------------------------------------------------------------------
	/**
	   Unload register from reset hard totals cargo
	
	   @param bus the bus being unloaded
	**/
	//--------------------------------------------------------------------------
	public void unload(BusIfc bus)
	{
	    super.unload(bus);
	    AdminCargo cargo = (AdminCargo) bus.getCargo();
	    // cargo.setManualReset(true);
	}
}
