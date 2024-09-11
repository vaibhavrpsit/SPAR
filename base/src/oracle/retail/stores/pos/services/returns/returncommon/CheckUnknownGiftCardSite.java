/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/CheckUnknownGiftCardSite.java /rgbustores_13.4x_generic_branch/5 2011/08/18 08:44:03 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    sgu       08/04/10 - update error dialogue message
 *    sgu       08/03/10 - reject a partially used gift card
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  06/23/09 - I18N of Giftcard status
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse
 *
 *   Revision 1.8  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.7  2004/07/15 19:28:17  lzhao
 *   @scr 6284: return gift card without receipt.
 *
 *   Revision 1.6  2004/03/23 18:42:20  baa
 *   @scr 3561 fix gifcard return bugs
 *
 *   Revision 1.5  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.4  2004/02/12 20:41:40  baa
 *   @scr 0 fixjavadoc
 *
 *   Revision 1.3  2004/02/12 16:51:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:05:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 28 2003 15:16:58   DCobb
 * Mail "Failure" letter if gift card is not valid for return.
 * Resolution for POS SCR-2548: G C Inquiry Screen - F12- Cancel and Esc/Undo should be enabled.
 *
 *    Rev 1.1   Sep 24 2002 14:09:32   baa
 * retrieve domain descriptor text from bundles
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:06:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:06   msg
 * Initial revision.
 *
 *    Rev 1.1   20 Feb 2002 14:20:56   cir
 * Send the letter Continue after the non returnable error screen
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 *
 *    Rev 1.0   Sep 21 2001 11:24:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/5 $
 * @deprecated in 13.4; this class is not used in any return tour script.
 */
public class CheckUnknownGiftCardSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1933197947111563165L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();

        // get the ui reference
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set PLU Item item info in model
        PLUItemIfc item = cargo.getPLUItem();

        //Indicates gift card cannot be returned
        boolean giftCardReturnInvalid = false;

        //The gift card
        GiftCardIfc giftCard = null;

        /* If the PLUItem is a GiftCardPLUItem check and if the current balance
         * is updated set the model item description for the gift card = the card
         *  number + expiry date and the price = gift card current balance
         */
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String errorMsg = "";
        if (item instanceof GiftCardPLUItemIfc)
        {
            GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)item;
            giftCard = giftCardPLUItem.getGiftCard();

            if (giftCard != null && !StatusCode.Active.equals(giftCard.getStatus()))
            {
            	giftCardReturnInvalid = true;
            	String responseCode = giftCard.getStatus().toString();
                errorMsg = utility.retrieveCommonText("GiftCardAuthResponseCode." + responseCode, responseCode);;

            }
            else if (giftCard != null && giftCard.getCurrentBalance().compareTo(giftCard.getInitialBalance()) != CurrencyIfc.EQUALS)
            {
            	giftCardReturnInvalid = true;
            	errorMsg = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC,
                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                          GiftCardConstantsIfc.ERROR_MODIFIED_TAG,
                          GiftCardConstantsIfc.ERROR_MODIFIED);
            }
        }
        if(giftCardReturnInvalid)
        {
            logger.warn(
            "The gift card status is in an unknown state.");
            //Set the dialog message argument with the status of giftcard.

            String [] msg = new String[1];
            msg[0] = errorMsg;
            // put up error dialog screen
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(GiftCardConstantsIfc.GIFT_CARD_RETURN_INVALID_DIALOG_ID);
            dialogModel.setArgs(msg);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
            dialogModel.setType(DialogScreensIfc.ERROR);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}
