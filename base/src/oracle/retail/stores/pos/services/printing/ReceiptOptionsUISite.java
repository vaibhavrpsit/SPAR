/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/ReceiptOptionsUISite.java /main/10 2013/05/21 12:52:56 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/17/13 - add eReceipt support for ordres
 *    mchellap  09/21/12 - Disable Email button if fiscal printing is enabled
 *    blarsen   03/13/12 - Do not prompt w/ options if ereceipt is enabled and
 *                         the address is provided in cargo. MPOS puts address
 *                         provided by mobile device into the cargo.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     10/22/10 - Fixed issue with cancelling transaction on screen
 *                         timeout.
 *    rsnayak   10/07/10 - Bill Pay E-Receipt
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    arathore  02/02/09 - Removed Receipt Options for Layaway and Order
 *                         Transactions.
 *    arathore  11/17/08 - updated for ereceipt feature
 *    arathore  11/17/08 - Site to display receipt options.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 * Display the printing options screen if configured.
 *
 * @version $Revision: /main/10 $
 */
public class ReceiptOptionsUISite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7064815812151744479L;
    /** revision number */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * Display the printing options screen if configured.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);

        // See if printing is configured
        boolean printConfig = true;
        try
        {
            printConfig = pm.getBooleanValue(ParameterConstantsIfc.PRINTING_PrintReceipts);
        }
        catch (ParameterException pe)
        {
            logger.error("Could not determine print setting.", pe);
        }

        // print receipt if configured
        if (printConfig)
        {
            // See if eReceipt functionality is enabled.
            String ereceiptEnabled = cargo.getParameterValue(pm, ParameterConstantsIfc.PRINTING_eReceiptFunctionality);
            
            // See if fiscal printing is enabled
            boolean isFiscalPrintingEnabled = Gateway.getBooleanProperty("application", "FiscalPrintingEnabled", false);

            // if email provided in cargo, do not prompt
            if (ParameterConstantsIfc.YES.equals(ereceiptEnabled) && !Util.isEmpty(cargo.getEmailAddress()))
            {
                letter = new Letter(CommonLetterIfc.EMAIL);
            }
            else if (ParameterConstantsIfc.YES.equals(ereceiptEnabled)
                    && (cargo.getTransaction() instanceof SaleReturnTransactionIfc || cargo.getTransaction() instanceof BillPayTransactionIfc)
                    && !(cargo.getTransaction() instanceof LayawayTransactionIfc))
            {
                DataInputBeanModel model = new DataInputBeanModel();

                // Mail the "Print" letter when the screen times out rather than
                // the "Timeout" letter. This will print the receipt to the
                // physical printer and exit the printing service normally.
                model.getTimerModel().setActionName(CommonActionsIfc.PRINT);

                // get the POS UI manager
                POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

                // Disable Email button if fiscal printing is enabled
                if (isFiscalPrintingEnabled)
                {
                    NavigationButtonBeanModel nModel = model.getLocalButtonBeanModel();
                    if (nModel == null)
                    {
                        nModel = new NavigationButtonBeanModel();
                        model.setLocalButtonBeanModel(nModel);
                    }
                    nModel.setButtonEnabled("Email", false);
                }

                // show screen.
                uiManager.showScreen(POSUIManagerIfc.RECEIPT_OPTIONS_SCREEN, model);
                return;
            }
            else
            // mail the print letter if eReceipt is not enabled.
            {
                letter = new Letter(CommonLetterIfc.PRINT);
            }
        }

        //mail the letter
        bus.mail(letter, BusIfc.CURRENT);
    }
}
