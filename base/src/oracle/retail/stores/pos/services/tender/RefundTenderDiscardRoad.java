/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RefundTenderDiscardRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    03/26/10 - removing tabs
 *    acadar    03/26/10 - added road for resetting the tender refund letter
 *    acadar    03/26/10 - road for resetting the refund letter
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;


public class RefundTenderDiscardRoad extends LaneActionAdapter
{


    /**
     * Reset the refund tender letter in the cargo
     */
	public void traverse(BusIfc bus)
	{

	    TenderCargo cargo = (TenderCargo)bus.getCargo();
	    cargo.setRefundTenderLetter(null);
	}


}
