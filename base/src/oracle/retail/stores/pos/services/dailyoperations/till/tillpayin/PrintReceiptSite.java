/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/PrintReceiptSite.java /main/14 2013/12/23 09:58:42 bhsuthar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    bhsuthar  12/20/13 - Handling the printer offline
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
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
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Prints receipt after a till pay in.
 * 
 * @version $Revision: /main/14 $
 */
public class PrintReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4092347501492655496L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * parameter string for Till Payin Receipts
       @deprecated as of 13.1 use blueprint configuration instead
     */
    public static final String tillPayinReceiptCount = "NumberTillPayinReceipts";

    /**
     * till payin prompt tag
       @deprecated as of 13.1 use blueprint configuration instead
     */
    protected static final String TILL_PAYIN_TAG = "TillPayIn";

    /**
     * till payin prompt default text
       @deprecated as of 13.1 use blueprint configuration instead
     */
    protected static final String TILL_PAYIN_TEXT = "Till pay-in";

    /**
     * PrintReceiptSite
     */
    public static final String SITENAME = "PrintReceiptSite";

    /**
     * Pay-Out calls printReport to
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPayInCargo cargo = (TillPayInCargo)bus.getCargo();
        boolean mailLetter = true;
        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String argText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME, TILL_PAYIN_TAG, TILL_PAYIN_TEXT);

        pandrModel.setArguments(argText);

        baseModel.setPromptAndResponseModel(pandrModel);
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        baseModel.setStatusBeanModel(statusModel);

        // Side-effect: Allow the status model to dynamically compute a negative
        // till balance.
        statusModel.setRegister(cargo.getRegister());

        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, baseModel);

        try
        {
            ReceiptParameterBeanIfc bean = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
            bean.setDocumentType(ReceiptTypeConstantsIfc.TILLPAYIN);
            bean.setTransaction(cargo.getTransaction());

            PrintableDocumentManagerIfc printMgr = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                    PrintableDocumentManagerIfc.TYPE);
            printMgr.printReceipt((SessionBusIfc)bus, bean);
        }
        catch (PrintableDocumentException e)
        {
        	  logger.error("PrintReceipt exception");
              logger.error(e);
              logger.error(e.getNestedException());
              statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
              mailLetter = false;
              String msg[] = new String[1];
              msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                                                  BundleConstantsIfc.PRINTER_OFFLINE);

              DialogBeanModel model = new DialogBeanModel();
              model.setResourceID("RetryContinue");
              model.setType(DialogScreensIfc.RETRY_CONTINUE);
              model.setArgs(msg);
              ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
          }
          if (mailLetter)
          {
              bus.mail(letter, BusIfc.CURRENT);
          }
               
    }
}
