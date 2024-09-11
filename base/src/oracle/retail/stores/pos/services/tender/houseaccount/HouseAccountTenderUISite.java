/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/houseaccount/HouseAccountTenderUISite.java /rgbustores_13.4x_generic_branch/6 2011/07/12 15:58:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   07/06/11 - update entry method enum
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    ohorne    06/15/11 - set ENTRY_METHOD
 *    ohorne    06/01/11 - created class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.houseaccount;

import java.util.HashMap;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Puts up UI prompting the user to enter a card number  
 */
@SuppressWarnings("serial")
public class HouseAccountTenderUISite extends PosSiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo(); 
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
        if (cardData == null)
        {
            //capture card data
            MSRModel msr = cargo.getPreTenderMSRModel();
            if (msr != null)
            {
                //swiped swiped
                if(logger.isDebugEnabled())
                {
                    logger.debug("MSRModel: "+ msr);
                }
                tenderAttributes.put(TenderConstants.MSR_MODEL, msr);

                // remove manually entered number
                tenderAttributes.remove(TenderConstants.NUMBER);
                tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, msr.getEncipheredCardData());
                bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
            }
            else
            {    
                // put up UI asking for Card number
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.HOUSE_ACCOUNT, new POSBaseBeanModel());
            }
        }
        else
        {
            //Instant Credit Enrolled, so no further validation required
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT))
        {
            // Get information from UI
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel();
            PromptAndResponseModel parModel = model.getPromptAndResponseModel();
            HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
            if (parModel.isSwiped())
            {
                
                // is swiped save the whole MSR model.
                tenderAttributes.put(TenderConstants.MSR_MODEL, parModel.getMSRModel());
                // save the EncipheredCardData in the tender attributes
                EncipheredCardDataIfc cardData = parModel.getMSRModel().getEncipheredCardData();
                tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Swipe);
            }
            else if (cargo.getPreTenderMSRModel() == null)
            {
                // if manually entered, we only have the card number.
                // Converting bytes to string so that it continues to work as currently constituted; 
                // This byte array will be encrypted and then stored in the tender attributes. 
                byte[] number = model.getPromptAndResponseModel().getResponseBytes();
                // encrypt the card number
                try
                {
                    EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(number);
                    tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                    tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
                }
                catch(EncryptionServiceException e)
                {
                	logger.error("unable to encrypt the text", e);
                }
                finally
                {
                    // Overwrite the array to remove the value from memory
                    Util.flushByteArray(number);
                }

                // remove MSR (may have been previously swiped)
                tenderAttributes.remove(TenderConstants.MSR_MODEL);
            }
            // set pre tender msr model to null
            cargo.setPreTenderMSRModel(null);
        }
    }
}
