/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/referral/CheckAccessForReferralRequiredSite.java /main/1 2012/09/12 11:57:17 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/21/12 - refactor referral into separate tour
 *    asinton   02/02/12 - implement security access checkpoint for call
 *                         referrals
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization.referral;

import java.util.Arrays;
import java.util.List;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.authorization.AuthorizationCargo;

/**
 * This site calculates if a security access check is required by parameter
 * settings for a call referral.
 * @author asinton
 * @since 13.4.1
 */
@SuppressWarnings("serial")
public class CheckAccessForReferralRequiredSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // calculate access privilege
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        TenderType tenderType = cargo.getCurrentResponse().getTenderType();
        String letter = CommonLetterIfc.CONTINUE;
        if(isAccessCheckRequired(tenderType, parameterManager))
        {
            letter = CommonLetterIfc.OVERRIDE;
            cargo.setAccessFunctionID(RoleFunctionIfc.OVERRIDE_CALL_REFERRALS);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Calculates if the parameter setting requires security access
     * @param tenderType
     * @return
     */
    protected boolean isAccessCheckRequired(TenderType tenderType, ParameterManagerIfc parameterManager)
    {
        // assume access check required is false
        boolean accessCheckRequired = false;
        Object[] values = new Object[0]; // so as not to return a null;

        try
        {
            values = parameterManager.getParameterValues(ParameterConstantsIfc.TENDERAUTHORIZATION_ManagerOverrideParameters);
        }
        catch (ParameterException e)
        {
            logger.error(e);
        }
        List<Object> declineOverrideSettings = Arrays.asList(values);
        String parameterValue = getParameterForTender(tenderType);

        if (declineOverrideSettings.contains(parameterValue))
        {
            // if access check required is present in override settings, then set to true
            accessCheckRequired = true;
        }
        return accessCheckRequired;
    }

    /**
     * Gets the parameter name for the given tender type.
     * @param tenderType
     * @return the parameter name for the given tender type.
     */
    protected String getParameterForTender(TenderType tenderType)
    {
        String parameterValue = "";
        if(tenderType == TenderType.CREDIT)
        {
            parameterValue = ParameterConstantsIfc.TENDERUATHORIZATION_VALUE_CreditCallReferralApprovalRequiresManagerOverride;
        }
        else if (tenderType == TenderType.CHECK)
        {
            parameterValue = ParameterConstantsIfc.TENDERUATHORIZATION_VALUE_CheckCallReferralApprovalRequiresManagerOverride;
        }
        else if (tenderType == TenderType.GIFT_CARD)
        {
            parameterValue = ParameterConstantsIfc.TENDERUATHORIZATION_VALUE_GiftCardCallReferralApprovalRequiresManagerOverride;
        }
        return parameterValue;
    }

}
