/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcertificate/CheckDiscountAppliedSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/27 23:13:36  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcertificate;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
    This site adds an item to the transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CheckDiscountAppliedSite extends SiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     
     This site displays the DiscountApplied dialog screen
     @param bus the bus arriving at this site
     **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        int[] buttons = new int[2];
        buttons[0] = DialogScreensIfc.BUTTON_NO;
        buttons[1] = DialogScreensIfc.BUTTON_YES;
        
        String [] letters = new String[2];
        letters[0] = "Success";
        letters[1] = "Discount";
        
        UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION, "DiscountApplied", null, buttons, letters);
    }
}
