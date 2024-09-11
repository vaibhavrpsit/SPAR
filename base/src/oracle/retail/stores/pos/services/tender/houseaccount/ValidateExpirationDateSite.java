/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/houseaccount/ValidateExpirationDateSite.java /main/2 2012/08/27 11:23:06 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - removed placeholder from key ExpiredCardError
 *    cgreene   07/12/11 - update generics
 *    ohorne    06/22/11 - set encrypted expiration
 *    ohorne    06/21/11 - changed SUCCESS letter to CONTINUE
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
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This class will validate the card's expiration date.  If
 * card is expired a dialog is displayed.
 */
@SuppressWarnings("serial")
public class ValidateExpirationDateSite extends PosSiteActionAdapter
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
        String expirationDate = (String)tenderAttributes.get(TenderConstants.EXPIRATION_DATE);
        
        //date attribute is no longer needed because valid date is stored in EncipheredCardDataIfc 
        tenderAttributes.remove(TenderConstants.EXPIRATION_DATE);
        try
        {
            validateExpirationDate(expirationDate);

            //set card data with date
            EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
            if (cardData != null)
            {
                cardData.setEncryptedExpirationDate(expirationDate);
            }
            else
            {
                String message = "EncipheredCardData not found.  This should not happen";
                logger.error(message);
                throw new RuntimeException(message);
            }
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

            if (error == TenderErrorCodeEnum.EXPIRED)
            {
                cargo.setPreTenderMSRModel(null);
                showExpiredCardDialog(utility, ui, bus);
                return;
            }
            else
            {
                String message = "Unhandled exception.  This should not happen";
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
        }

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /**
     * Shows the expired card dialog screen.
     */
    protected void showExpiredCardDialog(UtilityManagerIfc utility,POSUIManagerIfc ui,BusIfc bus)
    {
        // Display error message
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ExpiredCardError");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);

        String titleTag = "ExpiredCreditCardTitle";
        dialogModel.setTitleTag(titleTag);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    /**
     * This method validates the expiration date.
     * @param expirationDate
     * @throws TenderException if not a valid expiration date
     */
    protected void validateExpirationDate(String expirationDate)
    throws TenderException
    {
        if(expirationDate == null)
        {
            throw new TenderException("Expiration Date must be present",TenderErrorCodeEnum.INVALID_EXPIRATION_DATE);
        }

        UtilityIfc util = getUtility();
        util.validateExpirationDate(expirationDate);
    }
    
    /**
     * Returns the appropriate UtilityIfc implementation
     * @return
     */
    protected UtilityIfc getUtility()
    {
        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            logger.error(e);
            throw new RuntimeException("Configuration problem: could not create instance of UtilityIfc");
        }
        return util;
    }
}
