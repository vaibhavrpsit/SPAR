/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/OverrideAuthorizationAisle.java /rgbustores_13.4x_generic_branch/4 2011/08/23 16:08:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/23/11 - rolled back ability to override declines
 *    sgu       08/12/11 - fix manager override case
 *    cgreene   07/28/11 - added support for manager override for card decline
 *    cgreene   07/28/11 - initial version
 *    blarsen   06/21/11 - Initial version. (To display, among probable future
 *                         cases, tender type code mismatches between
 *                         PinCommConfig.xml and PinCommCodes.properties.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc.AuthorizationMethod;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.AuthorizedTenderADOBuilderIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This aisle modifies the auth response to a manual approval.
 *
 * @author cgreene
 * @since 13.4
 */
@SuppressWarnings("serial")
public class OverrideAuthorizationAisle extends PosLaneActionAdapter
{
    /** constant for canceled by customer dialog name */
    public static final String CONFIGURATION_ERROR = "ConfigurationError";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();

        // Get the response to override
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();

        response.setResponseCode(ResponseCode.Approved);
        response.setAuthorizationMethod(AuthorizationMethod.Manual);
        response.setAuthorizationCode(CommonLetterIfc.OVERRIDE + ":" + cargo.getOperator().getEmployeeID());

        journalApproval(bus);
    }

    /**
     * Only journal for successful override.  We could have multiple
     * unsuccessful overrides and only want to journal when we have a resolution
     */
    protected void journalApproval(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        AuthorizedTenderADOBuilderIfc builder = (AuthorizedTenderADOBuilderIfc)BeanLocator.getBean(BeanLocator.APPLICATION_CONTEXT_KEY, AuthorizedTenderADOBuilderIfc.BEAN_KEY);
        // create a temporary ado in order to journal it.
        // TODO refactor when or if the ADO gets created in this tour.
        TenderADOIfc tender = builder.buildTenderADO(response);
        RegisterJournalIfc journal = getRegisterJournal();
        journal.journal(tender, JournalFamilyEnum.TENDER, JournalActionEnum.AUTHORIZATION);
    }

    protected static RegisterJournalIfc getRegisterJournal()
    {
        // journal the added tender
        JournalFactoryIfc jrnlFact = null;
        try
        {
            jrnlFact = JournalFactory.getInstance();
        }
        catch (ADOException e)
        {
            logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
        }
        return jrnlFact.getRegisterJournal();
    }

}
