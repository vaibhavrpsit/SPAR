/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/ProrateAmountDiscountAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:29 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/03/22 18:35:05  cdb
 *   @scr 3588 Corrected some javadoc
 *
 *   Revision 1.4  2004/03/16 18:30:46  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.3  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.2  2004/02/19 20:08:49  cdb
 *   @scr 3588 Corrected behavior problem when non-prorated discounts are done.
 *
 *   Revision 1.1  2004/02/16 21:24:10  cdb
 *   @scr 3588 Added checking of prorate parameter and
 *   showing appropriate dialogs accordingly.
 *
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle reads in the employee number.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ProrateAmountDiscountAisle extends LaneActionAdapter
{
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
     constant for error dialog screen
     **/
    public static final String PRORATE_ERROR = "ProrateError";

    //----------------------------------------------------------------------
    /**
     *   Reads in the employee number from the UI. <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {        
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        
        cargo.setProrateDiscountByAmount(true);
        
        if (containsSellReturnItems(cargo.getItems()))
        {
            cargo.setContainsSellAndReturnItems(true);
            POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            showProrateWarningDialog(ui);
        }
        else
        {    
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    //----------------------------------------------------------------------
    /**
     *   Displays the discount already applied confirmation screen. <P>
     *   @param  ui       The POSUIManager
     */
    //----------------------------------------------------------------------
    protected void showProrateWarningDialog(POSUIManagerIfc ui)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(PRORATE_ERROR);
        dialogModel.setType(DialogScreensIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns true if the Discount Already Applied Dialog should be displayed.
     *   @param lineItems The array of Sale Return Line Items to search
     *   @return true if dialog should be displayed, false otherwise
     */
    //----------------------------------------------------------------------
    private boolean containsSellReturnItems(SaleReturnLineItemIfc[] lineItems)
    {
        boolean containsSellItems = false;
        boolean containsReturnItems = false;
        
        for (int x = 0; x < lineItems.length; x++)
        {
            if (lineItems[x].isReturnLineItem())
            {
                containsReturnItems = true;
            }
            else if (lineItems[x].isSaleLineItem())
            {
                containsSellItems = true;
            }

            if (containsReturnItems && containsSellItems)
            {
                break;
            }
        }
        
        return (containsReturnItems && containsSellItems);
    }

}
