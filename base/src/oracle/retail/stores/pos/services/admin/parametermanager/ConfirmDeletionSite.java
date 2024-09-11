/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/ConfirmDeletionSite.java /main/10 2011/02/16 09:13:25 cgreene Exp $
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
 *   Revision 1.4  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:33  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:39:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:04:04   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   08 Feb 2002 17:30:34   KAC
 * Initial revision.
 * Resolution for POS SCR-1176: Update "list from list" parameter editing
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ListFromListParameterBeanModel;

/**
 * This site informs the user that the new value he attempted to give to the
 * reason code is illegitimate.
 * 
 * @version $Revision: /main/10 $
 */
public class ConfirmDeletionSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3164422689207163286L;

    public static final String SITENAME = "ConfirmDeletionSite";

    /**
     * Inform the user that the new value he attempted to give to the reason
     * code is illegitimate.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        ListFromListParameterBeanModel beanModel =
            (ListFromListParameterBeanModel)cargo.getParameter();
        String names = beanModel.getPotentialDeletes();

        // If there is something to delete, confirm the deletion
        if ((names != null) && !"".equals(names))
        {
            DialogBeanModel model = new DialogBeanModel();
            String[] args = { names };
            model.setResourceID("DeleteParameterValue");
            model.setType(DialogScreensIfc.CONFIRMATION);
            model.setArgs(args);
            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
        }
        // If there is nothing to delete, just proceed
        else
        {
            bus.mail(new Letter(CommonLetterIfc.YES), BusIfc.CURRENT);
        }
    }

    /**
     * Log the departure.
     * 
     * @param bus the bus departing from this site
     */
    @Override
    public void depart(BusIfc bus)
    {
    }

    /**
     * Log the undo.
     * 
     * @param bus the bus undoing its actions
     */
    @Override
    public void undo(BusIfc bus)
    {
    }

    /**
     * Log the reset.
     * 
     * @param bus the bus being reset
     */
    @Override
    public void reset(BusIfc bus)
    {
    }
}