/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxOverrideValidationSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
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
 *  2    360Commerce 1.1         3/10/2005 10:25:48 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:43 PM  Robert Pearse   
 * $
 * Revision 1.8.2.1  2004/10/22 21:27:23  jdeleau
 * @scr 7429 Move the TAX_ALREADY_APPLIED dialog to appear
 * after the override amount or override % dialog is selected.
 *
 * Revision 1.8  2004/07/09 22:36:17  jdeleau
 * @scr 5155
 *
 * Revision 1.7  2004/05/07 20:09:09  dcobb
 * @scr 4654 Added On/Off with Ineligible Items alternate flow.
 *
 * Revision 1.6  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/03/11 00:32:01  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/09 16:45:14  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.3  2004/03/08 23:37:03  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.2  2004/03/08 21:07:52  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.1  2004/03/07 18:44:11  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.1  2004/03/05 22:57:24  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
 * Site used for processing data from tax rate override screen.
 * <P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxOverrideValidationSite extends PosSiteActionAdapter
{

    /** Revision Number for this class. */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * This site is executed when the override tax rate or amount button is pressed.
     * <P>
     * @param bus  The service bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get cargo handle
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();
        ItemTaxModControllerIfc cntl = cargo.getController();

        try
        {
            cntl.validateOverride(cargo.getItems());
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TaxWarningException te)
        {
            // get the POS UI manager
            POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(te.getErrorTextResourceName());
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

}
