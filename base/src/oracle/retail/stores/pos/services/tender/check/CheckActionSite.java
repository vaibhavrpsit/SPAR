/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/CheckActionSite.java /main/13 2013/08/27 09:23:39 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     08/26/13 - Correct auth called for echeck when check auth
 *                         parameter is set to no.
 *    sgu       10/07/11 - have echeck support floor limit
 *    cgreene   07/12/11 - update generics
 *    blarsen   06/28/11 - RequestType was moved into the base class.
 *    blarsen   06/09/11 - Updated the renamed RequestType AuthorizeCheck.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    ohorne    05/12/11 - Added off-line floor limit logic
 *    ohorne    05/06/11 - Complete Re-write of class to support APF
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceRequestIfc.RequestType;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * Determines Check Authorization Action
 */
@SuppressWarnings("serial")
public class CheckActionSite extends PosSiteActionAdapter
{
    /**
     * Determines if Check Authorization is required
     **/
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            if (pm.getBooleanValue("E-CheckFunctionality") && pm.getBooleanValue("CheckAuthActive").booleanValue())
            {
                //echeck
                cargo.getTenderAttributes().put(TenderConstants.AUTH_REQUIRED, TenderConstants.TRUE);
                cargo.getTenderAttributes().put(TenderConstants.REQUEST_TYPE, RequestType.AuthorizeECheck);

                //set Conversion Code based on value of AuthorizationConversion parameter
                String authConversion = pm.getStringValue("AuthorizationConversion");
                if (authConversion.equals("ConversionOnly"))
                {
                    cargo.getTenderAttributes().put(TenderConstants.CONVERSION_CODE, AuthorizationConstantsIfc.CONVERSION_FLAG_CONVERSION_ONLY);
                }
                else if (authConversion.equals("VerificationWithConversion"))
                {
                    cargo.getTenderAttributes().put(TenderConstants.CONVERSION_CODE, AuthorizationConstantsIfc.CONVERSION_FLAG_VERIFICATION_WITH_CONVERSION);
                }
                else if (authConversion.equals("GuarenteeWithConversion"))
                {
                   cargo.getTenderAttributes().put(TenderConstants.CONVERSION_CODE, AuthorizationConstantsIfc.CONVERSION_FLAG_GUARANTEE_WITH_CONVERSION);
                }
            }
            else
            {
                //check
                cargo.getTenderAttributes().put(TenderConstants.REQUEST_TYPE, RequestType.AuthorizeCheck);

                if (pm.getBooleanValue("CheckAuthActive").booleanValue())
                {
                    //is tender amount greater than minimum check authorization limit amount?
                    CurrencyIfc limitAmount =
                        DomainGateway.getBaseCurrencyInstance(pm.getStringValue("MinimumCheckAuthorizationAmount"));

                    CurrencyIfc tenderAmount =
                        DomainGateway.getBaseCurrencyInstance((String)tenderAttributes.get(TenderConstants.AMOUNT));
                    if (tenderAmount.compareTo(limitAmount)>= CurrencyIfc.EQUALS)
                    {
                        //authorization is required
                        cargo.getTenderAttributes().put(TenderConstants.AUTH_REQUIRED, TenderConstants.TRUE);
                    }
                    else
                    {
                        //authorization not required
                        cargo.getTenderAttributes().put(TenderConstants.AUTH_REQUIRED, TenderConstants.FALSE);
                    }
                }
                else
                {
                    //authorization not required
                    cargo.getTenderAttributes().put(TenderConstants.AUTH_REQUIRED, TenderConstants.FALSE);
                }
            }

            //set Off-line Check Floor Limit
            if (cargo.getTenderAttributes().get(TenderConstants.AUTH_REQUIRED).equals(TenderConstants.TRUE))
            {
                CurrencyIfc floorLimitAmount =
                    DomainGateway.getBaseCurrencyInstance(pm.getStringValue("OfflineCheckFloorLimit"));
                cargo.getTenderAttributes().put(TenderConstants.FLOOR_LIMIT_AMOUNT, floorLimitAmount);
            }

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (ParameterException e)
        {
            logger.error("Exception while getting parameter", e);
            bus.mail(new Letter(CommonLetterIfc.FAILURE), BusIfc.CURRENT);
        }
    }
}
