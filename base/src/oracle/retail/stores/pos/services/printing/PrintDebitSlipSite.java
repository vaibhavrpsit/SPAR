/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrintDebitSlipSite.java /main/20 2014/03/28 15:36:35 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    crain     10/14/14 - Bug 19811946: remove unnecessary setTenderLineItems call that could inadvertently mutate
 *                         transaction object while being persisted in queue in other thread.
 *    ohorne    03/28/14 - only print store copy debit slip when printDebitSlip
 *                         parameter is true
 *    swbhaska  03/20/14 - setting the value of print to true when a recipt
 *                         will be printed
 *    yiqzhao   02/12/14 - Add isStoreCopyPrinted in PrintCargo and
 *                         PrintReceiptTourParameters and add
 *                         lastPrintedTransactionIdForStore in
 *                         StatefulServiceSessionConstants for handling Done
 *                         was clicked on Print Options screen and Print Last
 *                         Transaction is selected. MPOS should print origial
 *                         customer copy.
 *    mkutiana  05/23/13 - Preventing the container from bieng unlocked to stop
 *                         unwanted letters (doubleclick)
 *    blarsen   08/13/12 - Preventing paper receipt from printing when e-mail
 *                         MPOS API is called.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   03/09/09 - set receipt locale explicity since locale in map
 *                         changes depending on the customer loaded
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/24/2007 6:05:12 PM   Alan N. Sinton  CR
 *         27256 Store copy receipt to say "Store Copy".
 *    3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 09 2004 17:55:14   DCobb
 * Removed unused import statement.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 *
 *    Rev 1.1   Jan 08 2004 18:00:16   DCobb
 * Removed printer offline halt behavior.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 *
 *    Rev 1.0   Aug 29 2003 16:05:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   24 May 2002 18:54:40   vxs
 * Removed unncessary concatenations from log statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:07:30   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Mar 2002 11:35:12   jbp
 * indicate proper entry method for debits, credits, and checks
 * Resolution for POS SCR-776: Entry Method on receipt for Debit is A, should be S for Swipe
 *
 *    Rev 1.0   Mar 18 2002 11:44:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 10 2002 18:00:40   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:22:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderDebit;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
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

/**
 * Print credit signature slips if configured and needed.
 *
 */
public class PrintDebitSlipSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3619252291492212914L;

    public static final String SITENAME = "PrintDebitSlipSite";


    /**
     * If debit slips are configured and there are approved debit tenders, print
     * the slips for them.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        boolean sendMail = true;

        TenderableTransactionIfc trans = cargo.getTransaction();
        if(cargo.isPrintPaperReceipt() && trans.getTransactionType() != TransactionIfc.TYPE_VOID)
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            String debitSlipConfig = cargo.getParameterValue(pm, "PrintDebitSlip");

            // Get debit tenders
            try
            {
                TenderLineItemIfc[] tenders = trans.getTenderLineItems();
                for (int i = 0; i < tenders.length; i++)
                {
                    if (tenders[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_DEBIT)
                    {
                        TenderDebit td = (TenderDebit)tenders[i];
                        if (debitSlipConfig.equalsIgnoreCase("Y"))
                        {
                            td.setPrintDebitSlip(true);
                        }
                        else
                        {
                            td.setPrintDebitSlip(false);
                        }
                        // pass them to the POSDeviceActions object
                        ReceiptParameterBeanIfc bean = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
                        bean.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
                        bean.setTransaction(trans);
                        bean.setTender(td);
                        bean.setDocumentType(ReceiptTypeConstantsIfc.DEBIT_SLIP);
                        if ( !cargo.isStoreCopyPrinted() && td.isPrintDebitSlip() )
                        {
                            // print store copy
                            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
                            pdm.printReceipt((SessionBusIfc)bus, bean);
                            cargo.setReceiptPrinted(true);
                        }
                    }
                }

                // Update printer status
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
                                 POSUIManagerIfc.ONLINE, POSUIManagerIfc.DO_NOT_UNLOCK_CONTAINER);
            }
            catch (PrintableDocumentException e)
            {
                logger.warn(
                            "Unable to print debit slip. " + e.getMessage());

                // Update printer status
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
                                 POSUIManagerIfc.OFFLINE, POSUIManagerIfc.DO_NOT_UNLOCK_CONTAINER);

                if (e.getNestedException() != null)
                {
                    logger.warn("NestedException:\n" + Util.throwableToString(e.getNestedException()));
                }

                String msg[] = new String[1];
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline",
                                                    "Printer is offline.");

                    DialogBeanModel model = new DialogBeanModel();
                    model.setResourceID("RetryContinue");
                    model.setType(DialogScreensIfc.RETRY_CONTINUE);
                    model.setArgs(msg);

                    // display dialog
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

                sendMail = false;
            }
        }                                           // end Trans Void
        if (sendMail)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }
}
