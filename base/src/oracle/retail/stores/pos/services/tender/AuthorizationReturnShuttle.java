/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/AuthorizationReturnShuttle.java /main/6 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    vineesin  02/17/14 - Added Authorization Reference to Audit Log
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    tzgarba   10/10/13 - Set audit event originator for tender authorization
 *                         events
 *    asinton   08/02/12 - Call referral refactor
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/27/11 - move auth response objects into domain
 *    ohorne    05/09/11 - added journaling
 *    asinton   03/31/11 - adding authorization builder utility.
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/21/11 - creating new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;
import java.util.List;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.AuthorizableTenderEvent;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.AuthorizedTenderADOBuilderIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.authorization.AuthorizationCargo;

import org.apache.log4j.Logger;

/**
 * Receives the responses from the Tender Authorization service.
 *
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizationReturnShuttle extends FinancialCargoShuttle
{
    /** Logger */
    public static final Logger logger = Logger.getLogger(AuthorizationReturnShuttle.class);

    /** handle to the list of response objects */
    protected List<AuthorizeTransferResponseIfc> responseList;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        // save the response list from the authorization cargo
        AuthorizationCargo authorizationCargo = (AuthorizationCargo)bus.getCargo();
        responseList = authorizationCargo.getResponseList();
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        // convert the responses into tenders
        AuthorizedTenderADOBuilderIfc builder = (AuthorizedTenderADOBuilderIfc)BeanLocator.getBean(BeanLocator.APPLICATION_CONTEXT_KEY, AuthorizedTenderADOBuilderIfc.BEAN_KEY);
        List<TenderADOIfc> tenders = builder.buildTenderADOs(responseList);

        // get local cargo and transaction
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        RetailTransactionADOIfc transaction = tenderCargo.getCurrentTransactionADO();

        // iterate through the tenders and add them to the transaction and the cargo's
        RegisterJournalIfc registerJournal = getRegisterJournal();
        for(TenderADOIfc tender : tenders)
        {
            try
            {
                Integer status =  (Integer) tender.getTenderAttributes().get(TenderConstants.AUTH_STATUS);
                if (status != null)
                {
                    EventOriginatorInfoBean.setEventOriginator("AuthorizationReturnShuttle.unload");
                    switch (status.intValue())
                    {
                        case AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED:

                            //add to transaction and journal
                            tender.setDirtyFlag(true);
                            transaction.addTender(tender);
                            tenderCargo.setLineDisplayTender(tender);

                            registerJournal.journal(tender, JournalFamilyEnum.TENDER, JournalActionEnum.AUTHORIZATION);

                            // add audit log event
                            addAuditLogEvent(bus, tender, AuthorizableTenderEvent.AuthorizationStatus.APPROVED);
                            break;

                        default:

                            //journal only
                            registerJournal.journal(tender, JournalFamilyEnum.TENDER, JournalActionEnum.AUTHORIZATION_DECLINED);

                            // add audit log event
                            addAuditLogEvent(bus, tender, AuthorizableTenderEvent.AuthorizationStatus.DECLINED);
                            break;
                    }
                }
            }
            catch (TenderException te)
            {
                logger.error("TenderException caught while adding tender to the transaction", te);
            }
        }
    }

    /**
     * Get Register journal
     * @return the RegisterJournal
     */
    protected RegisterJournalIfc getRegisterJournal()
    {
        RegisterJournalIfc registerJournal = null;
        try
        {
            JournalFactoryIfc jrnlFact = JournalFactory.getInstance();
            registerJournal = jrnlFact.getRegisterJournal();
        }
        catch (ADOException e)
        {
            logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
        }
        return registerJournal;
    }

    /**
     * Add auditlog entry for tender authorization
     * @param bus the bus
     * @param tender the tender
     * @param eventAuthStatus authorization status approved or declined
     */
    protected void addAuditLogEvent(BusIfc bus, TenderADOIfc tender, AuthorizableTenderEvent.AuthorizationStatus authStatus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        if (!CheckTrainingReentryMode.isTrainingOn(cargo.getRegister()))
        {
            HashMap<String,Object> tenderAttributes = tender.getTenderAttributes();
            AuthorizableTenderEvent event = (AuthorizableTenderEvent) AuditLoggingUtils.createLogEvent(
                    AuthorizableTenderEvent.class, AuditLogEventEnum.TRANSACTION_TENDERED_WITH_AUTHORIZABLE_TENDER);

            event.setStoreId(cargo.getRegister().getWorkstation().getStoreID());
            event.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
            event.setTillID(cargo.getRegister().getCurrentTillID());
            event.setUserId(cargo.getOperator().getLoginID());
            if (tenderAttributes.get(TenderConstants.TENDER_TYPE) != null)
            {
                TenderTypeEnum tenderType = (TenderTypeEnum)tenderAttributes.get(TenderConstants.TENDER_TYPE);
                event.setTenderType(tenderType.toString());
            }
            if (tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA) != null)
            {
                EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
                event.setCardNumber(cardData.getMaskedAcctNumber());
                event.setCardType(cardData.getCardName());
            }
            if (tenderAttributes.get(TenderConstants.ENTRY_METHOD) != null)
            {
                EntryMethod entryMethod = (EntryMethod)tenderAttributes.get(TenderConstants.ENTRY_METHOD);
                event.setEntryMethod(entryMethod.toString());
                event.setMsrSwipeIndicator(EntryMethod.Swipe.equals(entryMethod));
            }

            if (tenderAttributes.get(TenderConstants.AUTH_AMOUNT) != null)
            {
                String amount = (String)tenderAttributes.get(TenderConstants.AUTH_AMOUNT);
                event.setAmount(amount);
            }

            event.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
            event.setAuthStatus(authStatus);

            if (tenderAttributes.get(TenderConstants.AUTH_RESPONSE) != null)
            {
                event.setAuthReference((String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE));
            }

            // log the event
            AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
            auditService.logStatusSuccess(event);
        }
    }
}
