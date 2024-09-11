/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/ModifyItemTaxOptionsSite.java /main/11 2011/02/16 09:13:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 * $
 * Revision 1.9.2.1  2004/10/22 21:27:23  jdeleau
 * @scr 7429 Move the TAX_ALREADY_APPLIED dialog to appear
 * after the override amount or override % dialog is selected.
 *
 * Revision 1.9  2004/07/09 22:36:17  jdeleau
 * @scr 5155
 *
 * Revision 1.8  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.7  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.6  2004/03/11 22:28:39  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.5  2004/03/11 00:32:01  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.3 2004/02/12 16:51:07 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:51:47 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 16:02:04 CSchellenger Initial revision.
 * 
 * Rev 1.0 Apr 29 2002 15:18:12 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 11:38:08 msg Initial revision.
 * 
 * Rev 1.2 Jan 19 2002 10:28:20 mpm Initial implementation of pluggable-look-and-feel user interface. Resolution for
 * POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 * Rev 1.1 08 Jan 2002 17:22:48 baa add tax override to flow when items are sent out of state Resolution for POS
 * SCR-520: Prepare Send code for review
 * 
 * Rev 1.0 Sep 21 2001 11:29:38 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Site for managing modify-item-tax options.
 * 
 * @version $Revision: /main/11 $
 */
public class ModifyItemTaxOptionsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2292637265032553042L;

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * This arrive shows the screen for all the options for ModifyItemTax.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // retrieve cargo
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        boolean taxExempt = false;
        TaxableLineItemIfc[] lineItems = cargo.getItems();
        for (int i = 0; i < lineItems.length; i++)
        {
            if (lineItems[i].getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                taxExempt = true;
                break;
            }
        }

        // Should never happen, tax button will be disabled
        // on a tax exempt transaction.  If it does happen
        // an error dialog will take the user back to the sale screen.
        if (taxExempt)
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("TaxExemptOverrideError");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.UNDO);
            uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            if (cargo.isSendOutOfArea())
            {
                //Skip menu options an show override tax screen
                bus.mail(new Letter("OverrideTaxRate"), BusIfc.CURRENT);
            }
            else
            {
                // Continue on to show the options on the tax menu
                bus.mail(new Letter(CommonLetterIfc.YES), BusIfc.CURRENT);
            }
        }
    }
}