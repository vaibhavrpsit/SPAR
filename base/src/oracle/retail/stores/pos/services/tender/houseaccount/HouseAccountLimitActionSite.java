/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/houseaccount/HouseAccountLimitActionSite.java /rgbustores_13.4x_generic_branch/4 2011/07/12 15:58:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    ohorne    06/24/11 - use of utility.isOvertenderAllowed(..)
 *    blarsen   06/22/11 - Refactored isOverTenderNotAllowed() to use new param
 *                         constant and getUtility() method
 *    ohorne    06/06/11 - created class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.houseaccount;

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
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


/**
 * Determines if Amount exceeds Limits
 */
@SuppressWarnings("serial")
public class HouseAccountLimitActionSite extends PosSiteActionAdapter
{
    /**
     * Validates tender amount is appropriate for credit over tender configuration
     * @param bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get tender attributes from cargo and add tender type
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        
        // Use transaction to validate limits for House Account 
        UtilityIfc utility = Utility.getUtil();
        if (!utility.isOvertenderAllowed(ParameterConstantsIfc.TENDER_TendersNotAllowedForOvertender_HouseAccount))
        {
            //over tender not allowed. Test if over tendering...
            String amountString =(String)tenderAttributes.get(TenderConstants.AMOUNT);
            CurrencyIfc tenderAmount = DomainGateway.getBaseCurrencyInstance(amountString);
            CurrencyIfc balanceDue = cargo.getCurrentTransactionADO().getBalanceDue();
            if (tenderAmount.compareTo(balanceDue) == CurrencyIfc.GREATER_THAN)
            {
                // display error message
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("OvertenderNotAllowed");
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
                
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                return;
            }
        }
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

}
