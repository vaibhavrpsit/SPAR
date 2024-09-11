/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/SetReturnExternalOrderItemRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.PLUCargoIfc;

public class SetReturnExternalOrderItemRoad extends PosLaneActionAdapter {

    /**
	 * serial version UID
	 */
	private static final long serialVersionUID = -1072867006745435871L;

	//----------------------------------------------------------------------
    /**
       Set the external order item to the cargo
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
    	PLUCargoIfc cargo = (PLUCargoIfc)bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();

        if (pluItem != null)
        {
            if (cargo.isExternalOrder())
            {
            	ExternalOrderItemIfc externalOrderItem = ((ReturnExternalOrderItemsCargoIfc)cargo).
                	getCurrentExternalOrderItemReturnStatusElement().getExternalOrderItem();
                pluItem.setReturnExternalOrderItem(externalOrderItem);
            }
        }
    }
}
