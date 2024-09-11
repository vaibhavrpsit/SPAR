/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/AlterationsErrorSite.java /main/7 2012/08/27 11:23:02 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - wptg - removed placeholder from key NoLinkedCustomer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         5/22/2008 6:00:55 AM   subramanyaprasad gv CR
 *       31423: Added new error site to fix the bug.
 * $
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class AlterationsErrorSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/7 $";
    /**
        no link customer screen name
    **/
    private static final String RESOURCE_ID = "NoLinkedCustomer";
   
    //--------------------------------------------------------------------------
    /**
       Show the 'no linked customer' error dialog. <P>
       @param the service bus
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the managers from the bus
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean", display the error dialog.
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(RESOURCE_ID);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);

        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Cancel");

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}

