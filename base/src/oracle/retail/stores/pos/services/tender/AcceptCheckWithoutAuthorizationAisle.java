/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/AcceptCheckWithoutAuthorizationAisle.java /main/2 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   07/12/11 - update generics
 *    ohorne    04/29/11 - create class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Collects User Entered Check Number and determine authorization status
 */
public class AcceptCheckWithoutAuthorizationAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    private static final long serialVersionUID = 3728765026831590271L;

    public static final String LANENAME = "AcceptCheckWithoutAuthorizationAisle";

    /**
     * Tender factory name
     */
    public static final String TENDER_FACTORY = "factory.tender";

    /**
     * Add tender to transaction when not calling Authorizer
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();

        HashMap<String, Object> tenderAttributes = tenderCargo.getTenderAttributes();

        // lookup Systematic Approval Authorization Code parameter value
        UtilityIfc util = getUtility();
        String authCode = util.getParameterValue("SystematicApprovalAuthCode", "");

        // configure tender attributes to indicate tender is considered
        // Authorized
        tenderAttributes.put(TenderConstants.AUTH_CODE, authCode);
        tenderAttributes.put(TenderConstants.AUTH_RESPONSE, "");
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CHECK);
        tenderAttributes.put(TenderConstants.AUTH_METHOD, AuthorizableTenderIfc.AUTHORIZATION_METHOD_SYSTEM);
        tenderAttributes.put(TenderConstants.AUTH_STATUS, AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
        tenderAttributes.put(TenderConstants.FINANCIAL_NETWORK_STATUS,
                AuthorizableTenderIfc.AUTHORIZATION_NETWORK_ONLINE);

        // add tender to transaction
        RetailTransactionADOIfc transaction = tenderCargo.getCurrentTransactionADO();
        try
        {
            TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory(TENDER_FACTORY);
            TenderADOIfc checkTenderADO = factory.createTender(tenderAttributes);

            transaction.addTender(checkTenderADO);
            tenderCargo.setLineDisplayTender(checkTenderADO);

            // journal check
            JournalFactoryIfc jrnlFact = JournalFactory.getInstance();
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(checkTenderADO, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
        }
        catch (TenderException te)
        {
            logger.error("TenderException caught while adding tender to the transaction", te);
        }
        catch (ADOException adoe)
        {
            logger.error("TenderException caught while adding tender to the transaction. Unable to obtain Factory",
                    adoe);
        }

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /**
     * Instantiates Utility
     * 
     * @return the utility instance
     */
    public UtilityIfc getUtility()
    {
        try
        {
            return Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
