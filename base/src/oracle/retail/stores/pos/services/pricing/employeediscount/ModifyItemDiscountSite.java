/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/ModifyItemDiscountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/03/22 18:35:05  cdb
 *   @scr 3588 Corrected some javadoc
 *
 *   Revision 1.7  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.6  2004/03/03 21:03:45  cdb
 *   @scr 3588 Added employee transaction discount service.
 *
 *   Revision 1.5  2004/02/20 17:34:57  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.DiscountCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;


//------------------------------------------------------------------------------
/**
    Displays item discount by amount dialog.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ModifyItemDiscountSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
     *   Displays item discount by amount dialog.
     *   @param  bus BusIfc
     */
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.
            getManager(UIManagerIfc.TYPE);
        DiscountCargoIfc cargo = (DiscountCargoIfc)bus.getCargo();

        // check to see if this is an item discount by amount or percent
        // set argument text with localized text 
        if(cargo.getDiscountType() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
        {
            // show the discount amount entry screen
            ui.showScreen(POSUIManagerIfc.ENTER_EMPLOYEE_AMOUNT_DISCOUNT, new POSBaseBeanModel());
        }
        else
        {
            // show the discount percent entry screen
            ui.showScreen(POSUIManagerIfc.ENTER_EMPLOYEE_PERCENT_DISCOUNT, new POSBaseBeanModel());
        }

    }
}
