/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/PrintReceiptSite.java /main/15 2013/12/23 09:58:43 bhsuthar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    bhsuthar  12/20/13 - Handling the printer offline issue to for prompt
 *                         retry-continue screen when the printer is offline
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/25/08 - switch to specific payroll doc type
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/07/22 04:56:57  khassen
 *   @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
 *   Adding database fields, print and reprint receipt functionality to reflect
 *   persistence of additional data in transaction.
 *
 *   Revision 1.2  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.1  2004/03/12 18:19:23  khassen
 *   @scr 0 Till Pay In/Out use case
 *
 *   Revision 1.4  2004/03/03 23:15:16  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 06 2003 13:08:04   RSachdeva
 * Prompt  Message
 * Resolution for POS SCR-2227: Till Pay In/Out Report Print Message should have what kind of report is printing
 * 
 *    Rev 1.2   Mar 05 2003 20:44:42   KLL
 * integration of code review results
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.1   Jan 03 2003 08:38:20   KLL
 * Parameter control for number of receipts
 *
 *    Rev 1.0   Apr 29 2002 15:26:42   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:24   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 12 2002 14:09:28   mpm
 * Externalized text in receipts and documents.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   08 Feb 2002 15:26:40   epd
 * added message
 * Resolution for POS SCR-705: Till pay out - system should display a message indicating printing is in progress
 *
 *    Rev 1.1   26 Oct 2001 15:07:34   jbp
 * Implement new reciept methodology
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 *    Rev 1.0   Sep 21 2001 11:19:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
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
 * Prints the receipt for this transaction.
 * 
 * @author khassen
 */
public class PrintReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 3502499324109240723L;

    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
     * @deprecated as of 13.1 use blueprint configuration instead
     */
    public static final String tillPayrollPayoutReceiptCount = "NumberTillPayrollPayoutReceipts";
    /**
     * @deprecated as of 13.1 use blueprint configuration instead
     */
    protected static String TILL_PAYROLL_PAYOUT_TAG = "TillPayrollPayOut";
    /**
     * @deprecated as of 13.1 use blueprint configuration instead
     */
    protected static String TILL_PAYROLL_PAYOUT_TEXT = "Till payroll pay out";

    /**
     * arrive method.
     * @param bus the bus arriving at this site.
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPayrollPayOutCargo cargo = (TillPayrollPayOutCargo) bus.getCargo();
        boolean mailLetter = true;
        Letter letter = new Letter (CommonLetterIfc.SUCCESS);

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String argText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                              BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                                              TILL_PAYROLL_PAYOUT_TAG,
                                              TILL_PAYROLL_PAYOUT_TEXT);

        pandrModel.setArguments(argText);
        
        baseModel.setPromptAndResponseModel(pandrModel);
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        baseModel.setStatusBeanModel(statusModel);
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, baseModel);

        try
        {
            ReceiptParameterBeanIfc bean = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
            bean.setTransaction(cargo.getTransaction());
            bean.setDocumentType(ReceiptTypeConstantsIfc.TILLPAYOUT_PAYROLL);
            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
            pdm.printReceipt((SessionBusIfc)bus, bean);
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
