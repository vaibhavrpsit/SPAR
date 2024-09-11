/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ProcessOrderSendPackagesSite.java /rgbustores_13.4x_generic_branch/1 2011/05/11 16:05:18 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.externalorder.processorder;

import java.util.List;

import oracle.retail.stores.domain.externalorder.ExternalOrderSendPackageItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

public class ProcessOrderSendPackagesSite extends PosSiteActionAdapter
{
    /**
	 *
	 */
	private static final long serialVersionUID = 8762562494570097122L;

	/**
     * For each order item marked for sale calls the sell order item station
     * @param BusIfc
     */
    public void arrive(BusIfc bus)
    {
    	ProcessOrderCargo cargo = (ProcessOrderCargo)bus.getCargo();
    	Letter letter = new Letter(CommonLetterIfc.NEXT);
    	int nextPackage = cargo.getNextSendPackage();
        try
        {
        	List<ExternalOrderSendPackageItemIfc> sendPackageItems = cargo.getExternalOrderSendPackageItems();
            cargo.setCurrentExternalOrderSendPackage(sendPackageItems.get(nextPackage++));
            cargo.setNextSendPackage(nextPackage);
         }
         catch (IndexOutOfBoundsException ie)
         {
                //we finished processing all the items, move one to do additional validation
                letter = new Letter(CommonLetterIfc.DONE);
                resetCargoData(cargo);
         }

         // go to item validation station
         bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Resets the data in the cargo
     */
    private void resetCargoData(ProcessOrderCargo cargo)
    {
        cargo.setCurrentExternalOrderSendPackage(null);
        cargo.setNextSendPackage(0);
    }
}
