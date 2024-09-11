/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registerclose/RegisterUpdateSite.java /main/12 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  03/06/09 - FIXED CLOSED REGISTER STATUS COMES AS RECONCILE WHEN
 *                         CHECKING THE STAUS
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         2/29/2008 5:28:13 AM   Chengegowda Venkatesh
 *          fix for CR : 30345
 *    5    360Commerce 1.4         1/10/2008 7:51:40 AM   Manas Sahu      Event
 *          Originator Changes
 *    4    360Commerce 1.3         1/7/2008 8:38:46 AM    Chengegowda Venkatesh
 *          Changes for AuditLog incorporation
 *    3    360Commerce 1.2         3/31/2005 4:29:38 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:39 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   09 Jan 2004 21:33:16   Tim Fritz
 * Set letter to Failure when a data exception is received.
 *
 *    Rev 1.0   Aug 29 2003 15:57:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 23 2003 06:50:06   jgs
 * Modified to delay end transaction journal entry.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 *
 *    Rev 1.1   May 13 2002 19:36:04   mpm
 * Added support for till open/close and register open/close transactions.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:30:02   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registerclose;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.RegisterEvent;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;

/**
 * The site updates the register to closed.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class RegisterUpdateSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Updates the register database entry to closed.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.SUCCESS;

        // get cargo reference
        RegisterCloseCargo cargo = (RegisterCloseCargo) bus.getCargo();

        // for Auditlogging
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
        RegisterEvent ev = (RegisterEvent)AuditLoggingUtils.createLogEvent(RegisterEvent.class, AuditLogEventEnum.REGISTER_CLOSE);

        RegisterOpenCloseTransactionIfc transaction =
          DomainGateway.getFactory().getRegisterOpenCloseTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_CLOSE_REGISTER);

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        utility.initializeTransaction(transaction);
        transaction.setTimestampEnd();
        transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);

        // clone register (reset cargo if update succeeds)
        RegisterIfc r = (RegisterIfc) cargo.getRegister().clone();
        r.setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);
        r.setCloseTime();
        r.setSignOffOperator(cargo.getOperator());
        transaction.setRegister(r);

        // set the status in the cargo to the record that was found
        cargo.setRegister(r);

        // for Auditlogging
		ev.setUserId(transaction.getCashier().getLoginID());
		ev.setStoreId(transaction.getFormattedStoreID());
        ev.setBusinessDate(transaction.getBusinessDay().dateValue());
		ev.setTransactionNumber(transaction.getTransactionID());
		ev.setRegisterNumber(transaction.getWorkstation().getWorkstationID());

    if (EventOriginatorInfoBean.getEventOriginator() != null)
    {
          ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
        }
        try
        {
            // The register close processing adds more information to the journal
            // after the transaction has been completed.  Setting this flag
            // to false allows a follow on site to finish up the journalling
            // for this transaction.
            utility.saveTransaction(transaction, false);

            // for Auditlogging
            cargo.setTransaction(transaction);
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
            	auditService.logStatusSuccess(ev);
            }

        }
        catch (DataException e)
        {
        	// for Auditlogging
        	if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
            	auditService.logStatusFailure(ev);
            }

        	letterName = CommonLetterIfc.FAILURE;
            logger.error( "" + e + "");
        }
        // mail appropriate letter
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
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