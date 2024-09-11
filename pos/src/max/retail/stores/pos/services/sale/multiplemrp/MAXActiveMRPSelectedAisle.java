/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

     $Log$
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.multiplemrp;

import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXMaximumRetailPriceListBeanModel;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_12.0.9in_branch/1 $
**/
//------------------------------------------------------------------------------

public class MAXActiveMRPSelectedAisle extends LaneActionAdapter implements LaneActionIfc
{

    private static final long serialVersionUID = 53454353451L;

    //--------------------------------------------------------------------------
    /**


            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

	/*
         * Grab the item from the cargo
         */
	MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo) bus.getCargo();
	PLUItemIfc pluItem = null;

	try {
	    pluItem = (PLUItemIfc) ReflectionUtility.getAttribute(cargo,
		    "PLUItem");
	} catch (Exception e) {
	    System.err
		    .println("Exception while readign the PLUItem Attribute: "
			    + e);
	    e.printStackTrace();
	}
	POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
	MAXMaximumRetailPriceListBeanModel beanModel = (MAXMaximumRetailPriceListBeanModel) ui
		.getModel(MAXPOSUIManagerIfc.PICK_ONE_MRP);
	MAXMaximumRetailPriceChangeIfc maximumRetailPriceChange = beanModel
		.getSelectedItem();
	// Update the PLUItem with the MRP value of the selected
        // MaximumRetailPriceChange
	((MAXPLUItemIfc)pluItem).setMaximumRetailPrice(maximumRetailPriceChange
		.getMaximumRetailPrice());
	cargo.setPLUItem(pluItem);
	bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);

    }


    //--------------------------------------------------------------------------
    /**


            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void backup(BusIfc bus, SnapshotIfc snapshot)
    {
    }

}
