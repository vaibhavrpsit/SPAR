/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/registry/SetUpdatePolicySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:28  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:16:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   21 Jan 2002 17:51:20   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:30:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.registry;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SetUpdatePolicySite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8243449324804773166L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Checks to see if any items already have a gift registry associated with
     * them. If so it will show a dialog box to ask the user if they want to
     * update all the items with the new gift registry about to be entered. If
     * no items have a gift registry associated with them TRUE is set to update
     * all items and a Continue Letter is sent.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // retrieve cargo
        ModifyTransactionGiftRegistryCargo cargo = (ModifyTransactionGiftRegistryCargo)bus.getCargo();

        // Check to see if the employee has access to tax modifications
        if (cargo.getItemsModifiedFlag() == true)
        {
            // get the POS UI manager
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // show Yes/No UI to set policy
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("MultiGiftRegs");
            model.setType(DialogScreensIfc.CONFIRMATION);
            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
        }
        else
        {
            // set all items to the gift registry tath will be entered
            cargo.setUpdateAllItemsFlag(true);

            // send a Continue Letter to go to next site
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }
}