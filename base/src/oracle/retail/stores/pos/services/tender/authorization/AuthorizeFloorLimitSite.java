/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizeFloorLimitSite.java /main/2 2012/01/12 15:53:44 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton      01/12/12 - XbranchMerge asinton_bug-13558615 from
 *                            rgbustores_13.4x_generic_branch
 *    asinton      01/11/12 - Use the FloorLimit ResponseCode to determine if
 *                            we should set some values in the response object.
 *    asinton      09/27/11 - removed unnecessary import
 *    sgu          08/29/11 - add auditlog events for authorizable tenders
 *    ohorne       08/09/11 - APF:foreign currency support
 *    asinton      06/29/11 - Refactored to use EntryMethod and
 *                            AuthorizationMethod enums.
 *    cgreene      05/27/11 - move auth response objects into domain
 *    cgreene      05/20/11 - implemented enums for reponse code and giftcard
 *                            status code
 *    ohorne       05/11/11 - created class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc.AuthorizationMethod;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site configures Off-line responses within floor limit for
 * systematic authorization.
 *
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizeFloorLimitSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        try
        {
            //look-up Systematic Approval Authorization Code Parameter value
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            String authCode = pm.getStringValue("SystematicApprovalAuthCode");

            //configure response objects for Systematic Authorization Approval
            AuthorizationCargo authorizationCargo = (AuthorizationCargo)bus.getCargo();
            AuthorizeTransferResponseIfc response = authorizationCargo.getCurrentResponse();

            if (ResponseCode.FloorLimit.equals(response.getResponseCode()))
            {
                logger.debug("Configuring response for systematic authorization: tenderType: " +  response.getTenderType() + ", " + response.getBaseAmount());
                response.setAuthorizationCode(authCode);
                response.setResponseMessage("");
                response.setResponseCode(ResponseCode.ApprovedFloorLimit);
                response.setAuthorizationMethod(AuthorizationMethod.Automatic);
                response.setFinancialNetworkStatus(AuthorizationConstantsIfc.PAYMENT_APPLICATION_OFFLINE);
            }

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (ParameterException e)
        {
            logger.error("Unable to lookup SystematicApprovalAuthCode parameter value", e);
            bus.mail(new Letter(CommonLetterIfc.FAILURE), BusIfc.CURRENT);
        }
    }
}
