/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationReferralSite.java /rgbustores_13.4x_generic_branch/4 2011/09/30 11:17:46 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/13/14 - deprecated because this class moved to referral subpackage
 *    asinton   09/29/11 - Added Card Type to the Call Referral Screen, made it
 *                         depended upon credit tender only.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    asinton   06/27/11 - Added new UI for Call Referral and application flow
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;

/**
 * This site shows the call referral screen
 * @author asinton
 * @since 13.4
 * @deprecated as of 14.1.  This class moved to {@link oracle.retail.stores.pos.services.tender.authorization.referral.AuthorizationReferralSite}.
 */
@SuppressWarnings("serial")
public class AuthorizationReferralSite extends PosSiteActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        AuthorizationCargo authorizationCargo = (AuthorizationCargo)bus.getCargo();
        CreditReferralBeanModel beanModel = new CreditReferralBeanModel();

        // get the most recently added response
        AuthorizeTransferResponseIfc response = null;
        if(authorizationCargo.getResponseList().size() > 0)
        {
            response = authorizationCargo.getResponseList().get(authorizationCargo.getResponseList().size() - 1);
        }
        if(response != null)
        {
            if(!Util.isEmpty(response.getResponseMessage()))
            {
                beanModel.setAuthResponse(response.getResponseMessage());
            }
        }
        ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        String parameter = "";
        try
        {
            parameter = ParameterConstantsIfc.TENDERAUTHORIZATION_CallReferralList;
            String[] callReferralList = parameterManager.getStringValues(parameter);
            beanModel.setCallReferralList(callReferralList);

            parameter = ParameterConstantsIfc.TENDERAUTHORIZATION_CreditCardTypes;
            String[] creditCardTypes = parameterManager.getStringValues(parameter);
            beanModel.setCreditCardTypes(creditCardTypes);
        }
        catch(ParameterException pe)
        {
            logger.warn("Couldn't retrieve parameter: " + parameter, pe);
        }

        beanModel.setTenderType(response.getTenderType());
        beanModel.setCardType(response.getTenderSubType());
        beanModel.setMerchantNumber(
                retrieveParameterValue(
                        parameterManager,
                        ParameterConstantsIfc.TENDERAUTHORIZATION_MerchantNumber,
                        ""));

        beanModel.setChargeAmount(response.getBaseAmount());
        ui.showScreen(POSUIManagerIfc.CREDIT_REFERRAL, beanModel);
    }

    /**
     * Retrieves the parameter value for the given parameterName.
     * @param parameterManager
     * @param parameterName
     * @param defaultValue
     * @return
     */
    protected String retrieveParameterValue(ParameterManagerIfc parameterManager, String parameterName, String defaultValue)
    {
        String parameter = defaultValue;
        try
        {
            parameter = parameterManager.getStringValue(parameterName);
        }
        catch(ParameterException pe)
        {
            logger.warn("Could not retrieve parameter for " + parameterName, pe);
        }
        return parameter;
    }
}
