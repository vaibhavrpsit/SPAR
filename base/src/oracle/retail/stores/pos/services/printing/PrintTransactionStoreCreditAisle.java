/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrintTransactionStoreCreditAisle.java /main/19 2014/03/20 18:37:50 swbhaska Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    swbhaska  03/20/14 - setting the value of print to true when a recipt
 *                         will be printed
 *    tksharma  12/23/13 - Carry the value isDuplicateReceipt from the Cargo to
 *                         ReceiptParameterBeanIfc
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   03/09/09 - set receipt locale explicity since locale in map
 *                         changes depending on the customer loaded
 *    jswan     01/29/09 - Modified to correct issues with printing store
 *                         credit.
 *    cgreene   12/12/08 - log errors instead of warning when print fails
 *    cgreene   11/17/08 - switch to print blueprints
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/08/23 16:16:00  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.4  2004/06/24 16:58:21  dfierling
 *   @scr 5815 - updated to handle LocalizedDeviceException error class.
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
 *    Rev 1.1   Jan 08 2004 14:43:26   DCobb
 * Made printer offline behavior = proceed.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 * 
 *    Rev 1.0   Aug 29 2003 16:05:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   24 May 2002 18:54:42   vxs
 * Removed unncessary concatenations from log statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:07:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:40   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 12 2002 14:09:40   mpm
 * Externalized text in receipts and documents.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Mar 10 2002 18:01:12   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Nov 16 2001 09:13:46   blj
 * Changed design so that gift receipts are printed from the print transaction receipt aisle.
 * Resolution for POS SCR-236: 230
 *
 *    Rev 1.2   Nov 15 2001 10:29:24   blj
 * updated to print gift receipts last.
 * Resolution for POS SCR-236: 230
 *
 *    Rev 1.1   26 Oct 2001 14:57:46   jbp
 * Implement new reciept printing methodology
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 *    Rev 1.0   Sep 21 2001 11:22:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Print the receipt.
 * 
 * @version $Revision: /main/19 $
 */
public class PrintTransactionStoreCreditAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1146256229885027971L;

    public static final String LANENAME = "PrintTransactionStoreCreditAisle";

    /**
     * Print the receipt and send a letter
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        String letter = "ExitPrinting";
        boolean printStoreCredit = true;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // print the store credit if the store is not using pre-printed credits
            printStoreCredit = !pm.getBooleanValue(ParameterConstantsIfc.TENDER_PrePrintedStoreCredit).booleanValue();
        }
        catch (ParameterException e)
        {
            logger.warn("Unable to determine PrePrintedStoreCredit parameter", e);
        }

        // only print store credit if transaction is not a void
        if (printStoreCredit && trans.getTransactionType() != TransactionIfc.TYPE_VOID)
        {
            try
            {
                printStoreCredits(bus, trans);
            }
            catch (PrintableDocumentException e)
            {
                logger.error("Unable to print store credit receipt.", e);
                cargo.setPrinterError(e);
                letter = CommonLetterIfc.ERROR;
            }
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /**
     * @param bus
     * @param trans
     * @throws PrintableDocumentException
     */
    protected void printStoreCredits(BusIfc bus, TenderableTransactionIfc trans)
        throws PrintableDocumentException
    {
        TenderLineItemIfc[] tenders = trans.getTenderLineItems();
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        boolean isDuplicateReceipt = cargo.isDuplicateReceipt(); 
        for (int i = 0; i < tenders.length; i++)
        {
            if (tenders[i] instanceof TenderStoreCreditIfc && 
               ((TenderStoreCreditIfc)tenders[i]).isIssued())
            {
                ReceiptParameterBeanIfc receipt = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
                receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
                receipt.setTransaction(trans);
                receipt.setTender(tenders[i]);
                receipt.setDocumentType(ReceiptTypeConstantsIfc.STORE_CREDIT);
                receipt.setDuplicateReceipt(isDuplicateReceipt);

                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
                pdm.printReceipt((SessionBusIfc)bus, receipt);  
                cargo.setReceiptPrinted(true);               
            }
        }
    }
}
