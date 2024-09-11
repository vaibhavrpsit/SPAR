/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/EnterCustomerTaxIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:28 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.customer.lookup;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

// --------------------------------------------------------------------------
/**
 * Site to show the screen to input tax ID.
 */
// --------------------------------------------------------------------------
public class EnterCustomerTaxIDSite extends PosSiteActionAdapter
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Displays the FIND_CUSTOMER_BY_TAX_ID screen. <p>
        @param bus  the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.FIND_CUSTOMER_BY_TAX_ID, new POSBaseBeanModel());
    }
}
