/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxToggleInvalidValidationSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:50 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:45 PM  Robert Pearse   
 * $
 * Revision 1.1  2004/05/07 20:09:09  dcobb
 * @scr 4654 Added On/Off with Ineligible Items alternate flow.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
 * Determines if any items in the selection list are invalid for tax toggle.
 * <P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxToggleInvalidValidationSite extends PosSiteActionAdapter
{
    /** Revision Number for this class. */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * Determines if any items in the selection list are invalid for tax 
     * toggle. The Success letter is mailed if all items are valid; otherwise
     * a disalog message is displayed.
     * <P> 
     * @param bus  The Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get cargo handle
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ItemTaxModControllerIfc cntl = cargo.getController();

        try
        {
            cntl.validateItemsForToggle((SaleReturnLineItemIfc[])cargo.getItems());

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TaxWarningException tw)
        {
            cargo.displayDialog(ui, tw.getErrorTextResourceName(), DialogScreensIfc.CONTINUE_CANCEL);
        }
    }

}
