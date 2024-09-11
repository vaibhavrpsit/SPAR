/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/ChangeBusinessDayPromptSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/24/10 - rename to ChangeBusinessDay
 *    cgreene   02/11/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site prompts the end user with whether or not they want to change the
 * business date (by setting the store to closed and performing a store open).
 * This doesn't need to prompted if the system is online.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ChangeBusinessDayPromptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7881789455579243222L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Dialog prompt tag that will warn end user of register state.
     */
    public static final String DIALOG_PROMPT_TAG = "ChangeBusinessDay";

    /**
     * Confirms that the user wants to open the till.
     * 
     * @param bus the bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Display the confirmation screen
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(DIALOG_PROMPT_TAG);
        model.setType(DialogScreensIfc.YES_NO);
        model.setLocalButtonBeanModel(null);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}