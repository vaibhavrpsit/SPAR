/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/GiftCardTenderActionSite.java /rgbustores_13.4x_generic_branch/2 2011/07/12 15:58:33 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    blarsen   06/22/11 - Initial version. Prevents overtendering.
 * 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


@SuppressWarnings("serial")
public class GiftCardTenderActionSite extends PosSiteActionAdapter
{

    /**
     * This site checks for overtender.
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        
        UtilityIfc utility = Utility.getUtil();

        if (!utility.isOvertenderAllowed(ParameterConstantsIfc.TENDER_TendersNotAllowedForOvertender_CreditDebitGiftCard))
        {
            String amountString =(String)tenderAttributes.get(TenderConstants.AMOUNT);
            CurrencyIfc tenderAmount = DomainGateway.getBaseCurrencyInstance(amountString);
            CurrencyIfc balanceDue = cargo.getCurrentTransactionADO().getBalanceDue();
            if (tenderAmount.compareTo(balanceDue) == CurrencyIfc.GREATER_THAN)
            {
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("OvertenderNotAllowed");
                dialogModel.setType(DialogScreensIfc.ERROR);
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.FAILURE);
                // display dialog
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                return;
            }
        }

        Letter letter = new Letter(CommonLetterIfc.SUCCESS);
        bus.mail(letter, BusIfc.CURRENT);
    }

}
