/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/about/DisplayAboutVersionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         11/8/2006 8:53:28 AM   Keith L. Lesikar 
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.about;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class DisplayAboutVersionSite extends PosSiteActionAdapter 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	    revision number
	**/
	public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
	
	//--------------------------------------------------------------------------
	/**
	    DisplayAboutVersionSite
	**/
	//--------------------------------------------------------------------------
	public static final String SITENAME = "DisplayAboutVersionSite";
	//----------------------------------------------------------------------
    /**
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	if (logger.isDebugEnabled()) logger.debug(SITENAME + ".arrive starting...");

        //show screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel          pModel = new POSBaseBeanModel();
        ui.showScreen(POSUIManagerIfc.ABOUT_OPTIONS, pModel);

        if (logger.isDebugEnabled()) logger.debug(SITENAME + ".arrive ending...");
    }
}
