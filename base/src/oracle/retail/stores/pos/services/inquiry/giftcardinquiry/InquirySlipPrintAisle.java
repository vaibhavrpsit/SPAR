/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftcardinquiry/InquirySlipPrintAisle.java /rgbustores_13.4x_generic_branch/4 2011/07/26 16:57:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    asinton   04/25/11 - Refactored giftcard inquiry for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  06/23/09 - I18N of Giftcard status
 *    glwang    02/06/09 - add isTrainingMode into
 *                         PrintableDocumentParameterBeanIfc
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         3/20/2008 12:59:20 AM  Manikandan Chellapan
 *       CR#30967 Modified code show truncated card number.
 *  4    360Commerce 1.3         1/22/2006 11:45:09 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:24 PM  Robert Pearse
 * $
 * Revision 1.2  2004/06/25 21:46:51  lzhao
 * @scr 5453: show item number automatically.
 *
 * Revision 1.1  2004/04/07 21:10:08  lzhao
 * @scr 3872: gift card redeem and revise gift card activation
 *
 * Revision 1.3  2004/02/12 16:50:22  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:51:11  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Dec 08 2003 09:28:08   lzhao
 * remove expiration date
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Nov 26 2003 09:25:14   lzhao
 * cleanup, use the methods in gift card utility.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Nov 21 2003 15:01:00   lzhao
 * using GiftCardUtilities
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Nov 07 2003 16:50:24   lzhao
 * add new inquiry multple time and print offline features.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Aug 29 2003 15:59:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 16 2003 08:16:08   KLL
 * Prompt to print gift card inquiry slip
 * Resolution for POS SCR-2129: Gift Card - Print Balance- Missing Print Button
 *
 *    Rev 1.0   Sep 26 2002 15:50:34   kmorneau
 * Initial revision.
 * Resolution for 1816: Gift Card Balance Slip
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.giftcardinquiry;

import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.GiftCardInquirySlip;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

/**
 * Print the gift card inquiry slip for the customer.
 *
 * @since 5.2.0
 */
public class InquirySlipPrintAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7037066928600760998L;

    /**
     * site name
     */
    protected static final String LANENAME = "InquirySlipPrintAisle";

    /**
     * Printer offline dialog ID
     */
    public static final String RETRY_CONTINUE_TAG = "RetryContinue";

    /**
     * Printer offline dialog message tag
     */
    public static final String PRINTER_OFFLINE_TAG = "RetryContinue.PrinterOffline";

    /**
     * Printer offline default dialog message
     */
    protected static final String PRINTER_OFFLINE_TEXT = "Printer is offline.";

    /**
     * Print the gift card inquiry slip for the customer.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        InquiryCargo cargo = (InquiryCargo)bus.getCargo();
        try
        {
            GiftCardIfc giftCard = cargo.getGiftCard();
            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
            GiftCardInquirySlip inquirySlip = (GiftCardInquirySlip)pdm.getParameterBeanInstance(ReceiptTypeConstantsIfc.GIFTCARD_INQUIRY);
            inquirySlip.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
            inquirySlip.setGiftCard(giftCard);
            pdm.printReceipt((SessionBusIfc)bus, inquirySlip);

            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            GiftCardBeanModel model = null;
            if (!(ui.getModel() instanceof GiftCardBeanModel))
            {
                /*
                 * Calling from retry, ui.getModel is not GiftCardBeanModel. Get
                 * card information from cargo
                 */
                model = new GiftCardBeanModel();
                model.setOpenAmount(giftCard.getOpenAmount());
                model.setGiftCardNumber(giftCard.getEncipheredCardData().getTruncatedAcctNumber());
                model.setGiftCardInitialBalance(new BigDecimal(giftCard.getInitialBalance().getStringValue()));
                String giftCardAmountLabel = utility.retrieveText(GiftCardConstantsIfc.GIFT_CARD_INQUIRY_SPEC_TAG,
                        BundleConstantsIfc.GIFTCARD_BUNDLE_NAME, GiftCardConstantsIfc.REMAINING_BALANCE_LABEL_TAG,
                        GiftCardConstantsIfc.REMAINING_BALANCE_LABEL_TEXT);
                model.setGiftCardAmountLabel(giftCardAmountLabel);
                model.setGiftCardAmount(new BigDecimal(giftCard.getCurrentBalance().getStringValue()));
                model.setGiftCardStatus(giftCard.getStatus());
            }
            else
            {
                model = (GiftCardBeanModel)ui.getModel();
            }
            // Display the screen
            ui.showScreen(POSUIManagerIfc.GIFT_CARD_INQUIRY, model);
        }
        catch (PrintableDocumentException e)
        {
            logger.warn(bus.getServiceName() + ": Unable to print gift card inquiry slip: " + e.getMessage());

            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);

            if (e.getCause() != null)
            {
                logger.warn(bus.getServiceName() + ": NestedException:\n"
                        + Util.throwableToString(e.getCause()));
            }

            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText(PRINTER_OFFLINE_TAG, PRINTER_OFFLINE_TEXT);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(RETRY_CONTINUE_TAG);
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "ExitPrint");
            model.setArgs(msg);

            // display retry/cancel dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
    }
}
