/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/GiftCardInquirySite.java /rgbustores_13.4x_generic_branch/6 2011/08/18 08:44:04 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    npoola    10/22/10 - added a condition to make sure gift card initial
 *                         balance price is same as current price before return
 *    sgu       08/03/10 - reject a partially used gift card
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mpbarnet  03/17/10 - Checkin
 *    mpbarnet  03/17/10 - Use GiftCardUtilities method to generate dialog
 *                         rather than local method.
 *    mpbarnet  03/16/10 - In arrive(), display error dialog if no gift card
 *                         information is returned from the authorizer.
 *    dwfung    03/10/10 - Handling Training Mode Requests
 *    abondala  01/03/10 - update header date
 *    mchellap  06/23/09 - I18N of giftcard status
 *    mchellap  06/19/09 - I18N changes for gift card return error message
 *    mchellap  06/18/09 - Showing GiftCardReturnInvalid dialog for invalid
 *                         returns
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/18/2008 4:05:31 PM   Maisa De Camargo CR
 *         32088 - Fixed comparison between Currency objects.
 *    4    360Commerce 1.3         6/17/2008 5:15:01 PM   Maisa De Camargo CR
 *         32088 - Used GiftCards shouldn't be returned.
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse
 *
 *   Revision 1.7  2004/07/28 22:21:58  lzhao
 *   @scr 6592: change for fit ISD.
 *
 *   Revision 1.6  2004/07/07 18:17:16  blj
 *   @scr 5966 - resolution
 *
 *   Revision 1.5  2004/05/03 21:09:43  epd
 *   @scr 4264 Gift Cards now returnable, but still trying to activate.  I'm working on that next
 *
 *   Revision 1.4  2004/03/23 18:42:20  baa
 *   @scr 3561 fix gifcard return bugs
 *
 *   Revision 1.3  2004/03/22 06:17:50  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 *   Revision 1.2  2004/03/18 23:01:56  baa
 *   @scr 3561 returns fixes for gift card
 *
 *   Revision 1.1  2004/03/18 15:55:00  baa
 *   @scr 3561 Add changes to support giftcard returns
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   Feb 06 2004 16:43:22   lzhao
 * change display message for different request.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.5   Jan 30 2004 14:14:08   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Jan 09 2004 12:54:22   lzhao
 * set transaction back, remove comments, add date
 * Resolution for 3666: Eltronic Journal for Gift Card Issue  and Reload not Correct
 *
 *    Rev 1.3   Nov 26 2003 09:25:12   lzhao
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
 *    Rev 1.0   Aug 29 2003 15:59:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.8   Jun 25 2003 15:34:12   RSachdeva
 * REQUEST_SCREEN_NAME
 * Resolution for POS SCR-2902: Gift Card Request Screen has tags
 *
 *    Rev 1.7   Apr 22 2003 09:56:22   KLL
 * fixed inquiry request failure
 * Resolution for POS SCR-2164: Gift Card Inquiry Slip
 *
 *    Rev 1.6   Apr 16 2003 08:16:08   KLL
 * Prompt to print gift card inquiry slip
 * Resolution for POS SCR-2129: Gift Card - Print Balance- Missing Print Button
 *
 *    Rev 1.4   Jan 22 2003 13:29:08   bwf
 * Changed ERROR_UNKNOWN_CARD_TAG to the correct value.
 * Resolution for 1640: Gift Card Inquiry - Error message for unknown card says "invalid request"
 *
 *    Rev 1.3   Jan 16 2003 13:56:00   pjf
 * ISD integration updates.
 * Resolution for 1904: ISD Integration
 *
 *    Rev 1.2   Sep 26 2002 15:48:38   kmorneau
 * print an inquiry slip for customer
 * Resolution for 1816: Gift Card Balance Slip
 *
 *    Rev 1.1   Sep 25 2002 09:34:46   kmorneau
 * inquiry print
 * Resolution for 1816: Gift Card Balance Slip
 *
 *    Rev 1.0   Apr 29 2002 15:23:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:33:02   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 10 2002 18:00:24   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Mar 10 2002 11:06:52   mpm
 * Externalized text.
 *
 *    Rev 1.1   Mar 09 2002 18:36:38   mpm
 * Externalized text.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:20:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site send the gift card inquiry request.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/6 $
 * @deprecated in 13.4; this class is not referenced by any return tour scripts.
 */
@SuppressWarnings("serial")
public class GiftCardInquirySite extends PosSiteActionAdapter
{

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/6 $";
    /**
     * site name constant
     */
    public static final String SITENAME = "RequestCardInfoSite";

    /**
     * Request the card info from the external processor.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get transaction objects
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();
        GiftCardIfc giftCard = null;
        if (cargo.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)cargo.getPLUItem();
            giftCard = giftCardPLUItem.getGiftCard();
        }

        if (giftCard != null)
        {
//            PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
//            // get the response
//            ActivationRequest request = createInquiryRequest(giftCard);
//            if (cargo.getOriginalTransaction() != null)
//            {
//                request.setTrainingMode(cargo.getOriginalTransaction().isTrainingMode());
//            }
//            request.setStoreID(Gateway.getProperty("application", "StoreID", ""));
//            AuthorizeResponseIfc response = paymentManager.authorize(request);
//
//            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
//            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
//            if (response != null)
//            {
//
//                String responseCode = response.getResponseCode().toString();
//
//                // display dialog
//                if (responseCode.equals(AuthorizationConstantsIfc.TIMEOUT))
//                {
//                    cargo.setReturnItem(null);
//                    String errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC,
//                            BundleConstantsIfc.DIALOG_BUNDLE_NAME, GiftCardConstantsIfc.ERROR_TIMEOUT_TAG,
//                            GiftCardConstantsIfc.ERROR_TIMEOUT);
//                    showErrorDialog(ui, errorMessage, GiftCardConstantsIfc.GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID);
//
//                }
//                else if (responseCode.equals(AuthorizationConstantsIfc.OFFLINE))
//                {
//                    cargo.setReturnItem(null);
//                    String errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC,
//                            BundleConstantsIfc.DIALOG_BUNDLE_NAME, GiftCardConstantsIfc.ERROR_OFFLINE_TAG,
//                            GiftCardConstantsIfc.ERROR_OFFLINE);
//                    showErrorDialog(ui, errorMessage, GiftCardConstantsIfc.GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID);
//
//                }
//                else if (responseCode.equals(AuthorizationConstantsIfc.DECLINED)
//                        && response.getGiftCard().getStatus().equals(AuthorizationConstantsIfc.INACTIVE))
//                {
//                    cargo.setReturnItem(null);
//                    DialogBeanModel dialogModel = GiftCardUtilities.createReturnErrorDialogModel();
//                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
//                }
//                else
//                {
//                    // Check if giftcard is valid for return:
//                    // 1. Card hasn't been used
//                    // 2. status is active
//                    if (response.getGiftCard() != null)
//                    {
//                        giftCard = response.getGiftCard();
//
//                        GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)cargo.getPLUItem();
//
//                        if (giftCard.getCurrentBalance().compareTo(giftCard.getInitialBalance()) != CurrencyIfc.EQUALS
//                                || !giftCard.getStatus().equals(AuthorizationConstantsIfc.ACTIVE)
//                                || giftCardPLUItem.getPrice().compareTo(giftCard.getInitialBalance()) != CurrencyIfc.EQUALS)
//                        {
//                            cargo.setReturnItem(null);
//                            String errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC,
//                                    BundleConstantsIfc.DIALOG_BUNDLE_NAME, GiftCardConstantsIfc.ERROR_MODIFIED_TAG,
//                                    GiftCardConstantsIfc.ERROR_MODIFIED);
//                            showErrorDialog(ui, errorMessage, GiftCardConstantsIfc.GIFT_CARD_RETURN_INVALID_DIALOG_ID);
//                            return;
//                        }
//                        else
//                        {
//                            giftCardPLUItem.setGiftCard(giftCard);
//                            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
//                        }
//                    }
//                }
//
//            }
//            else
//            {
//                // show unknown error
//                cargo.setReturnItem(null);
//                String status = giftCard.getStatus().toString();
//                String errorMessage = utility.retrieveCommonText("GiftCardAuthResponseCode." + status, status);
//                showErrorDialog(ui, errorMessage, "GiftCardReturnInvalid");
//            }
        }
    }

    /**
     * Show error message
     * 
     * @param ui UI Manager
     * @param errorCode String
     */
    protected void showErrorDialog(POSUIManagerIfc ui, String errorCode, String resourceID)
    {
        String args[] = { errorCode };
        DialogBeanModel dialogModel = new DialogBeanModel();
        if (resourceID == null)
        {
            resourceID = GiftCardConstantsIfc.GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID;
        }
        dialogModel.setResourceID(resourceID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CONTINUE);
        if (errorCode != null)
        {
            dialogModel.setArgs(args);
        }
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}