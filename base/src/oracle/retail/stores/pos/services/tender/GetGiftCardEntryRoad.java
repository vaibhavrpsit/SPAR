/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/GetGiftCardEntryRoad.java /rgbustores_13.4x_generic_branch/2 2011/09/23 14:12:53 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/23/11 - capture the entry method of the gift card number
 *    asinton   08/19/11 - Added configurable flow to prompt for gift card
 *                         number during tender.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import java.util.Map;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This road retrieves the entered gift card number which was entered
 * on the GiftCardEntryUISite.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GetGiftCardEntryRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = (POSBaseBeanModel)uiManager.getModel(POSUIManagerIfc.GIFT_CARD);
        PromptAndResponseModel promptAndResponseModel = beanModel.getPromptAndResponseModel();
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        Map<String, Object> tenderAttributes = tenderCargo.getTenderAttributes();
        EncipheredCardDataIfc cardData = null;
        EntryMethod entryMethod = EntryMethod.Manual;
        if(promptAndResponseModel.isSwiped())
        {
            cardData = promptAndResponseModel.getMSRModel().getEncipheredCardData();
            entryMethod = EntryMethod.Swipe;
        }
        else
        {
            if(promptAndResponseModel.isScanned())
            {
                entryMethod = EntryMethod.Scan;
            }
            byte[] cardNumber = promptAndResponseModel.getResponseText().getBytes();
            try
            {
                cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(cardNumber);
            }
            catch(EncryptionServiceException ese)
            {
                logger.error("Could not encrypt card number", ese);
            }
            finally
            {
                Util.flushByteArray(cardNumber);
            }
        }
        tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
        tenderAttributes.put(TenderConstants.ENTRY_METHOD, entryMethod);
    }

}
