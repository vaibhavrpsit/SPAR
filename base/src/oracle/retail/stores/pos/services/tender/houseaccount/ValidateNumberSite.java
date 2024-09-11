/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/houseaccount/ValidateNumberSite.java /rgbustores_13.4x_generic_branch/2 2011/07/12 15:58:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    ohorne    06/02/11 - created
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.houseaccount;

import java.util.HashMap;

import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This class will validate the card number is a House Account pattern. 
 */
@SuppressWarnings("serial")
public class ValidateNumberSite extends PosSiteActionAdapter
{

    /**
     * This method creates a tender to check the expiration date.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {      
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
        if (isHouseAccountNumber(bus, cardData))
        {
            bus.mail(new Letter(CommonLetterIfc.VALID), BusIfc.CURRENT);
        }
        else
        {
            tenderAttributes.remove(TenderConstants.ENCIPHERED_CARD_DATA);
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            showUnknownCardDialog(ui);
        }
    }

    /**
     * Returns true if card data is for a house account 
     */
    protected boolean isHouseAccountNumber(BusIfc bus, EncipheredCardDataIfc cardData)
    {
        if (cardData != null)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            CreditTypeEnum type = utility.determineCreditType(cardData);
            return CreditTypeEnum.HOUSECARD.equals(type); 
        }
        return false;
    }

    /**
     * Shows the unknown card dialog screen.
     */
    protected void showUnknownCardDialog(POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("UnknownCreditCard");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.RETRY);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.INVALID);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
