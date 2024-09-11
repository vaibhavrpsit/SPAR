/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/damagediscount/DamageDiscountOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:39 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:26 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/02/25 22:51:41  dcobb
 *   @scr 3870 Feature Enhancement: Damage Discounts
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.damagediscount;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractDiscountOptionsSite;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays damage discount options.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DamageDiscountOptionsSite extends AbstractDiscountOptionsSite
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the damage discount options
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        PricingCargo pricingCargo = (PricingCargo)bus.getCargo();

        ListBeanModel model = getModifyItemBeanModel(pricingCargo.getItems());

        configureLocalButtons(model, pricingCargo);

        ui.showScreen(POSUIManagerIfc.DAMAGE_DISCOUNT_OPTIONS, model);
    }

}
