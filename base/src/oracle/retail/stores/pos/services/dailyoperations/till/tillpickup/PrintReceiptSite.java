/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/PrintReceiptSite.java /main/17 2013/08/26 12:03:46 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  08/26/13 - fixed PromptandResponse panel display text
 *    rgour     06/17/13 - Putting the placeholder value for Pick up receipt
 *                         printing
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/20/09 - moved setting of receipt locale to spring context
 *    acadar    12/08/08 - fix for till pickup receipt
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Prints receipt after a till pickup.
 *
 * @version $Revision: /main/17 $
 **/
public class PrintReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4094310272765260984L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/17 $";
    
    /**
     * pickup text tag
     */
    protected static final String PICKUP_TEXT_TAG = "PickupText";
    
    /**
     * pick up text(with the dot)
     */
    protected static final String PICKUP_TEXT = "Pickup";


    /**
     * Collects the Till pickup count then calls printReport to print the count.
     * Displays a screen that printing is occurring. Catches device exceptions
     * and displays a dialog to allow the user to Retry or Cancel. If parameter
     * TillCountTillPickup is set to No then do not count the pickup amount,
     * otherwise count the pickup amount based upon this parameter's value
     * (Summary/Detail).
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = null;

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        StringBuffer argumentText = new StringBuffer();
        argumentText.append(
                utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                        BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME, PICKUP_TEXT_TAG, PICKUP_TEXT));
        pandrModel.setArguments(argumentText.toString());
        baseModel.setPromptAndResponseModel(pandrModel);
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        baseModel.setStatusBeanModel(statusModel);
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, baseModel);

        try
        {
            printReport(bus);
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        catch (PrintableDocumentException e)
        {
            // Update printer status
            logger.error(bus.getServiceName() + ": PrintReceipt exception ", e.getNestedException());
            statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                    BundleConstantsIfc.PRINTER_OFFLINE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            model.setStatusBeanModel(statusModel);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Print the report
     */
    private void printReport(BusIfc bus) throws PrintableDocumentException
    {
        TillPickupCargo cargo = (TillPickupCargo)bus.getCargo();

        ReceiptParameterBeanIfc bean = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
        TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        bean.setTransaction(cargo.getTransaction());
        bean.setDocumentType(ReceiptTypeConstantsIfc.TILLPICKUP);

        if (cargo.getPickupCountType() != FinancialCountIfc.COUNT_TYPE_NONE
                || cargo.getTenderName().equals(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)))
        {
            // Get financial count
            FinancialTotalsIfc fc = cargo.getRegister().getTillByID(cargo.getTillID()).getTotals();
            // Get all the pickups in this till
            ReconcilableCountIfc[] tillPickups = fc.getTillPickups();
            // This pickup is the last in the Pickups array
            FinancialCountIfc count = tillPickups[tillPickups.length - 1].getEntered();
            bean.setFinancialCount(count);
        }

        PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                PrintableDocumentManagerIfc.TYPE);
        pdm.printReceipt((SessionBusIfc)bus, bean);
    }
}
