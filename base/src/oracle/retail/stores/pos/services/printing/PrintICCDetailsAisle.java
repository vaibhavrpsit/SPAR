/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrintICCDetailsAisle.java /main/7 2014/03/27 18:11:42 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   03/27/14 - Preventing duplicate ICC Details receipts for MPOS.
 *    blarsen   03/26/14 - Error letter not handled when print fails. Changing
 *                         to Failure letter which is handled.
 *    swbhaska  03/20/14 - setting the value of print to true when a recipt
 *                         will be printed
 *    yiqzhao   02/11/14 - MPOS does not use ReprintReceiptCargo. Change
 *                         checking reprint from by cargo class to reprint
 *                         flag.
 *    blarsen   08/13/12 - Preventing paper receipt from printing when e-mail
 *                         MPOS API is called.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/15/11 - do not print ICC if reprinting
 *    cgreene   06/28/11 - rename hashed credit card field to token
 *    cgreene   06/03/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Print the ICC Details receipt and send the "Ok" letter.
 *
 */
public class PrintICCDetailsAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1146256229885027972L;

    public static final String LANENAME = "PrintICCDetailsAisle";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        String letter = CommonLetterIfc.OK;

        // only receipt if transaction is not a void and we're not re-printing.
        if (!cargo.isStoreCopyPrinted() &&
                        cargo.isPrintPaperReceipt() &&
                        trans.getTransactionType() != TransactionIfc.TYPE_VOID &&
                                (!cargo.isDuplicateReceipt()) )
        {
            try
            {
                printICCDetails(bus, trans);
            }
            catch (PrintableDocumentException e)
            {
                logger.error("Unable to print ICC details receipt.", e);
                cargo.setPrinterError(e);
                letter = CommonLetterIfc.FAILURE;
            }
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /**
     * @param bus
     * @param trans
     * @throws PrintableDocumentException
     */
    protected void printICCDetails(BusIfc bus, TenderableTransactionIfc trans)
        throws PrintableDocumentException
    {
    	PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderLineItemIfc[] tenders = trans.getTenderLineItems();
        for (int i = 0; i < tenders.length; i++)
        {
            if (tenders[i] instanceof TenderChargeIfc &&
               ((TenderChargeIfc)tenders[i]).getICCDetails() != null)
            {
                ReceiptParameterBeanIfc receipt = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
                receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
                receipt.setTransaction(trans);
                receipt.setTender(tenders[i]);
                receipt.setDocumentType(ReceiptTypeConstantsIfc.ICC_DETAILS);

                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
                pdm.printReceipt((SessionBusIfc)bus, receipt);
                cargo.setReceiptPrinted(true);
            }
        }
    }
}
