/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/StoreStatusUpdateSite.java /main/13 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    mchellap  12/06/11 - Fix store close status
 *    mchellap  12/06/11 - XbranchMerge mchellap_fix_pos_store_close from
 *                         rgbustores_13.4x_generic_branch
 *    mchellap  12/01/11 - Fixed store close status update
 *    mjwallac  07/27/11 - set store status to CLOSED not RECONCILED after EOD
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    atirkey   01/12/09 - Removed condition to enable logging of store open
 *
 * ===========================================================================
 * $Log:
 *7    360Commerce 1.6         3/12/2008 5:55:33 AM   Chengegowda Venkatesh For
 *      audit log changes
 *6    360Commerce 1.5         1/24/2008 2:43:00 AM   Chengegowda Venkatesh
 *     PABP 30 - Originating Point checkin for BO and CO audit Log
 *5    360Commerce 1.4         1/10/2008 7:46:01 AM   Manas Sahu      Event
 *     Originator changes
 *4    360Commerce 1.3         1/7/2008 8:39:44 AM    Chengegowda Venkatesh
 *     Changes for AuditLog incorporation
 *3    360Commerce 1.2         3/31/2005 4:30:14 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:25:37 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:14:32 PM  Robert Pearse   
 *
 Revision 1.4  2004/03/03 23:15:11  bwf
 @scr 0 Fixed CommonLetterIfc deprecations.
 *
 Revision 1.3  2004/02/12 16:49:37  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:46:17  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   May 22 2003 17:11:28   jgs
 * Modified to delay the end transaction journaling.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 * 
 *    Rev 1.1   May 09 2002 08:37:22   mpm
 * Modified to support new store open/close transaction.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:31:26   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

import java.util.Hashtable;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.StoreOpsEvent;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * The site updates the store status to closed.
 * 
 */
@SuppressWarnings("serial")
public class StoreStatusUpdateSite extends PosSiteActionAdapter
{


    /**
     * Updates the store status database entry to closed.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = null;

        // get cargo reference
        EndOfDayCargo cargo = (EndOfDayCargo) bus.getCargo();
        
        // for AuditLogging
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
        StoreOpsEvent ev = (StoreOpsEvent)AuditLoggingUtils.createLogEvent(StoreOpsEvent.class, AuditLogEventEnum.END_OF_DAY);
        
        // clone store status (reset cargo if update succeeds)
        StoreStatusIfc ss = (StoreStatusIfc) cargo.getStoreStatus().clone();
        ss.setStatus(AbstractFinancialEntityIfc.STORE_STATUS_CLOSED);
        ss.setSignOffOperator(cargo.getOperator());
        ss.setCloseTime();
        // set the status in the cargo to the record that was found
        cargo.setStoreStatus(ss);
        
        try
        {                               // begin update store status try block
            letter = new Letter(CommonLetterIfc.SUCCESS);
            
            // initialize store open/close transaction
            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            StoreOpenCloseTransactionIfc transaction =
                DomainGateway.getFactory().getStoreOpenCloseTransactionInstance();
            transaction.setTransactionType(TransactionIfc.TYPE_CLOSE_STORE);
            utility.initializeTransaction(transaction, -1);
            transaction.setTimestampEnd();
            transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
            
            // set store totals and count
            transaction.setStoreStatus(ss);
           
                transaction.setEndingSafeCount
                (cargo.getSafeTotals().getEndingSafeCount().getEntered());
            
            
            // The end of day processing adds more information to the journal
            // after the transaction has been completed.  Setting this flag
            // to false allows a follow on site to finish up the journalling
            // for this transaction.
            utility.saveTransaction(transaction, false);
            
            // set the status in the cargo to the record that was found
            cargo.setCommitFlag(true);
            cargo.setTransaction(transaction);
            
            // for Auditlogging
            ev.setUserId(transaction.getCashier().getLoginID());
            ev.setStoreId(transaction.getFormattedStoreID());
            ev.setBusinessDate(transaction.getBusinessDay().dateValue());
            ev.setTransactionNumber(transaction.getTransactionID());
            if(transaction.getTransactionIdentifier()!=null)
            {
            	ev.setRegisterID(transaction.getTransactionIdentifier().getWorkstationID());
            }
            
            if (cargo.getSafeCountType() == FinancialCountIfc.COUNT_TYPE_NONE)
            {
                ev.setOperatingFundAmount(AuditLoggerConstants.OPERATING_FUND_AMOUNT_EXPECTED);
            }
            else 
            {
                FinancialTotalsIfc fti = cargo.getSafeTotals();
                if(fti!=null)
                {
                    ReconcilableCountIfc rci = fti.getEndingSafeCount();
                    if(rci!=null)
                    {
                        FinancialCountIfc fci = rci.getEntered();
                        if(fci!=null)
                        {
                            CurrencyIfc ci = fci.getAmount();
                            if(ci!=null)
                            {
                                ev.setOperatingFundAmount(ci.getStringValue());
                            }
                            if(cargo.getSafeCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
                            {
                                Hashtable denominations = new Hashtable();
                                FinancialCountTenderItemIfc[] fcti = fci.getTenderItems();
                                for(int i=0;i<fcti.length;i++)
                                {
                                    denominations.put(fcti[i].getDescription(),String.valueOf(fcti[i].getNumberItemsIn()));
                                }
                                // Remove from hashmap the entry for total because
                    			// the total amount is log as a seperate field
                                denominations.remove(DomainGateway.getFactory()
                                             .getTenderTypeMapInstance()
                                             .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
                                ev.addDenomination(denominations,null);
                            }
                        }
                    }
                }
            }
            
           	ev.setEventOriginator("LookupStoreStatusSite.arrive");
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditService.logStatusSuccess(ev);
            }
        }                               
        // end update store status try block
        // catch problems on the lookup
        catch (DataException e)
        {
            // for Auditlogging
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditService.logStatusFailure(ev);
            }
            
            // set error code
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            // check for NO_DATA exception, which indicates store
            // was already closed
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                letter = new Letter("ClosedStoreError");
            }
            // if another error, handle it
            else
            {
                logger.error(
                        e.toString());
                letter = new Letter(CommonLetterIfc.FAILURE);
            }
        }

        // mail appropriate letter
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}