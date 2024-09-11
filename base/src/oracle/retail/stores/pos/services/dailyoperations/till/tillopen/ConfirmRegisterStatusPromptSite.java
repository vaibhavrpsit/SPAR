/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/ConfirmRegisterStatusPromptSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/24/10 - move strBusDate to last in dialog args
 *    cgreene   02/11/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import java.text.DateFormat;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site confirms that the user wants to open the till while offline. The
 * end user will be presented with the current register status and must confirm.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ConfirmRegisterStatusPromptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7881789455579243222L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Dialog prompt tag that will warn end user of register state.
     */
    public static final String DIALOG_PROMPT_TAG = "ConfirmOfflineRegisterStatus";

    /**
     * Confirms that the user wants to open the till.
     * 
     * @param bus the bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // get current business date and statuses
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        EYSDate businessDate = register.getBusinessDate();
        Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        String strBusDate = businessDate.toFormattedString(DateFormat.SHORT, locale);
        String registerStatus = utility.retrieveCommonText(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS
                [register.getStatus()]);
        String storeStatus = utility.retrieveCommonText(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS
                  [cargo.getStoreStatus().getStatus()]);

        // Display the confirmation screen
        DialogBeanModel model = new DialogBeanModel();
        String args[] = new String[] { registerStatus, storeStatus, strBusDate };
        model.setResourceID(DIALOG_PROMPT_TAG);
        model.setType(DialogScreensIfc.CONTINUE_CANCEL);
        model.setLocalButtonBeanModel(null);
        model.setArgs(args);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}