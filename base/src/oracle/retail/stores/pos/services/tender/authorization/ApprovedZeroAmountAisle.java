/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/ApprovedZeroAmountAisle.java /main/1 2014/01/28 09:08:59 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   01/28/14 - fixed handling of approvals for gift card tender for
 *                         zero amount and calcalation of remaining balance
 *    asinton   01/23/14 - fixed handling of approvals for gift card tender for
 *                         zero amount and calcalation of remaining balance
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.activation.ActivationDeclinedAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Aisle for handling a zero amount approval response.
 * @since 14.0.1
 */
@SuppressWarnings("serial")
public class ApprovedZeroAmountAisle extends PosLaneActionAdapter
{
    /**
     * Constant value for the GiftCardZeroBalance resource ID.
     */
    public static final String GIFT_CARD_ZERO_BALANCE_DIALOG = ActivationDeclinedAisle.GIFT_CARD_ZERO_BALANCE_DIALOG;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();

        String cardLastFour = getGiftCardLastFour(response);
        String[] dialogArgs = {cardLastFour};

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(GIFT_CARD_ZERO_BALANCE_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Returns the last four digits of the gift card number.
     * @param response the <code>AuthorizeTransferResponseIfc</code> instance to retrieve the card number from
     * @return the last four digits of the gift card number.
     */
    protected String getGiftCardLastFour(AuthorizeTransferResponseIfc response)
    {
        String cardLastFour = response.getMaskedAccountNumber();
        if(cardLastFour != null && cardLastFour.length() > 4)
        {
            cardLastFour = cardLastFour.substring(cardLastFour.length() - 4);
        }
        else
        {
            cardLastFour = "";
        }
        return cardLastFour;
    }

}
