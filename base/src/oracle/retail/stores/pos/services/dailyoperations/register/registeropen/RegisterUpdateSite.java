/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/RegisterUpdateSite.java /main/13 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    mchellap  08/10/12 - Add fiscal printer support
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         1/10/2008 7:52:16 AM   Manas Sahu      Event
 *          Originator Changes
 *    5    360Commerce 1.4         1/7/2008 8:37:59 AM    Chengegowda Venkatesh
 *          Changes for AuditLog incorporation
 *    4    360Commerce 1.3         5/11/2007 4:35:13 PM   Peter J. Fierro
 *         Deprecate DomainInterfaceManager/Technician, refactor DomainGateway
 *          to not read/write xml type lists.
 *    3    360Commerce 1.2         3/31/2005 4:29:38 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:39 PM  Robert Pearse
 *
 *   Revision 1.10  2004/07/17 18:18:14  crain
 *   @scr 5872 Foreign Currency: Local file not updated when register is opened
 *
 *   Revision 1.9  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/05/27 18:19:47  jriggins
 *   @scr 5160 overloaded retrieveCurrencyTypesFromSource() and updateBackupCurrencySource() methods so that they can accept a TransactionIfc instance for help in determining when to journal
 *
 *   Revision 1.7  2004/05/19 20:02:11  jriggins
 *   @scr 5160 moved call to updateBackupCurrencySource() after transaction initialization so that it can show up in the e-journal
 *
 *   Revision 1.6  2004/05/18 21:27:32  jriggins
 *   @scr 5160 Added call to update the BackupCurrencyTypeList.xml file at register open
 *
 *   Revision 1.5  2004/04/19 18:48:57  awilliam
 *   @scr 4374 Reason Code featrure work
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:52  mcs
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
 *    Rev 1.0   Aug 29 2003 15:57:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   May 23 2003 06:52:36   jgs
 * Modified to delay the end of transaction journal entry.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 *
 *    Rev 1.2   24 Jun 2002 11:45:18   jbp
 * merge from 5.1 SCR 1726
 * Resolution for POS SCR-1726: Void - Void of new special order gets stuck in the queue in DB2
 *
 *    Rev 1.1   May 13 2002 19:36:04   mpm
 * Added support for till open/close and register open/close transactions.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:29:44   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.RegisterEvent;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;

/**
 * This site writes the register information and status to persistent storage.

 */
public class RegisterUpdateSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1786536211041321031L;


    /**
     * Writes register update to persistent storage.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // letter to be mailed
        String letterName = CommonLetterIfc.SUCCESS;

        // get cargo reference
        RegisterOpenCargo cargo = (RegisterOpenCargo) bus.getCargo();

        // get register (reset cargo if update succeeds) and set business date, status, open time
        RegisterIfc r = cargo.getRegister();
        StoreStatusIfc ss = cargo.getStoreStatus();

        // for Auditlogging
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
        RegisterEvent ev = (RegisterEvent)AuditLoggingUtils.createLogEvent(RegisterEvent.class, AuditLogEventEnum.REGISTER_OPEN);

        // If the business day changes, then start with a fresh register.
        if (!r.getBusinessDate().equals(ss.getBusinessDate()))
        {

            r.setBusinessDate(ss.getBusinessDate());
            if (logger.isInfoEnabled()) logger.info(
                        "Unique ID:[" + r.getCurrentUniqueID() + "]");
        }

        // check business date of store, register
        EYSDate storeBusinessDate = ss.getBusinessDate();
        if (logger.isInfoEnabled()) logger.info(
                    "Store business date: " + storeBusinessDate + "" +
                    "  register business date:  " + r.getBusinessDate() + "");

        // set status, open time
        r.setStatus(AbstractFinancialEntityIfc.STATUS_OPEN);
        r.setOpenTime();
        r.setSignOnOperator(cargo.getOperator());
        r.resetTotals();
        r.resetTills();

        // Set the opened register object into the hold register
        cargo.setHoldRegister((RegisterIfc) r.clone());

        // save the new status in the cargo
        cargo.setRegister(r);

        //
        // Create the Open Register Transaction
        //
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        RegisterOpenCloseTransactionIfc transaction =
            DomainGateway.getFactory().getRegisterOpenCloseTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_OPEN_REGISTER);
        utility.initializeTransaction(transaction, -1);

        transaction.setTimestampEnd();
        transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
        transaction.setRegister(r);

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
            // The register open processing adds more information to the journal
            // after the transaction has been completed. Setting this flag
            // to false allows a follow on site to finish up the journalling
            // for this transaction.
            utility.saveTransaction(transaction, false);
            cargo.setTransaction(transaction);
            //for Auditlogging
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditService.logStatusSuccess(ev);
            }
            
            boolean isFiscalPrintingEnabled = Gateway.getBooleanProperty("application", "FiscalPrintingEnabled", false);
            
            // Synchronize register and printer time
            if (isFiscalPrintingEnabled)
            {
                POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
                pda.setFiscalPrinterDate();
            }
        }
        catch (DataException e)
        {
            //for Auditlogging
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditService.logStatusFailure(ev);
            }
            logger.error(e);
        }
        catch (DeviceException e)
        {
            // Printer will continue to use its current date and time.
            logger.error("Unable to synchronize register and fiscal printer date and time", e);
        }

        // mail appropriate letter
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}