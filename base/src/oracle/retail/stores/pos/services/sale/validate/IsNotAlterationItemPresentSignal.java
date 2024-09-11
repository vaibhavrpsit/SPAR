/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/IsNotAlterationItemPresentSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
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
 *  1    360Commerce 1.0         5/22/2008 6:01:36 AM   subramanyaprasad gv CR
 *       31423: Added new signal IsNotAlterationItemPresentSignal to fix the
 *       bug. 
 * $
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

public class IsNotAlterationItemPresentSignal implements TrafficLightIfc  
{
    /**
		revision number
    **/
	public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	
    //--------------------------------------------------------------------------
    /**
		Checks if alteration item is not present
        <P>
        @return true if alteration item is not present, false otherwise.
    **/
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        boolean alterationItemNotPresent = true;
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc  transaction = cargo.getTransaction();
        AbstractTransactionLineItemIfc[] lineItems = null;
        
        if (transaction != null)
        {
            lineItems = transaction.getLineItems();
            
            if (lineItems != null && lineItems.length > 0)
            {
                for (int i=0; i<lineItems.length; i++)
                {
                    if (((SaleReturnLineItemIfc)lineItems[i]).getAlterationItemFlag())
                    {
                    	alterationItemNotPresent = false;
                         break;
                    }
                }
            }
        }
	return alterationItemNotPresent;
    }
}

