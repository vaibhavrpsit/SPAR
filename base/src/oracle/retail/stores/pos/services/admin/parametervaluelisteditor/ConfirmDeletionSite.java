/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/ConfirmDeletionSite.java /main/10 2011/02/16 09:13:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/07/19 17:58:52  dcobb
 *   @scr 6339 Confirmation message is incorrect when deleting "Check" from Tenders to Count at Till Reconcile parameter
 *
 *   Revision 1.3  2004/02/12 16:48:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:46   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:04   msg
 * Initial revision.
 * 
 *    Rev 1.2   10 Feb 2002 14:33:38   KAC
 * If there is nothing to delete, no confirmation dialog.
 * Resolution for POS SCR-1226: Update list parameter value editor per new requirements
 * 
 *    Rev 1.1   23 Jan 2002 13:05:58   KAC
 * Now confirms parameter value deletion instead of reason
 * code deletion.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * 
 *    Rev 1.0   22 Jan 2002 13:52:54   KAC
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site confirms that the user wants to delete.
 * 
 * @version $Revision: /main/10 $
 */
public class ConfirmDeletionSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 8067691018568327246L;

    public static final String SITENAME = "ConfirmDeletionSite";

    /**
     * Confirm that the user wants to delete.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ListEditorCargo cargo = (ListEditorCargo) bus.getCargo();

        // If a reason code is being deleted, confirm with the user
        if (cargo.getReasonCode() != null)
        {
            String name = "'" + cargo.getReasonCode().getReasonCodeName() + "' ";
            String[] args = { name };

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("DeleteParameterValue");
            model.setType(DialogScreensIfc.CONFIRMATION);
            model.setArgs(args);
            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
        }
        // If nothing is being deleted, ignore and continue
        else
        {
            bus.mail(new Letter(CommonLetterIfc.YES), BusIfc.CURRENT);
        }
    }

    /**
     * Do nothing
     * 
     * @param bus the bus departing from this site
     */
    @Override
    public void depart(BusIfc bus)
    {
    }

    /**
     * Do nothing.
     * 
     * @param bus the bus undoing its actions
     */
    @Override
    public void undo(BusIfc bus)
    {
    }

    /**
     * Do nothing.
     * 
     * @param bus the bus being reset
     */
    @Override
    public void reset(BusIfc bus)
    {
    }

}
