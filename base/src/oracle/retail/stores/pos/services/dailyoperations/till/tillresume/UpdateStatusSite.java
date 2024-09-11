/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/UpdateStatusSite.java /main/15 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    deghosh   11/25/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         5/22/2008 6:39:22 AM   subramanyaprasad gv
 *         CR 31729: Changed the audit log event from TILL_RETRIEVE to
 *         TILL_RESUME. Code reviewed by Manikandan Chellappan.
 *    9    360Commerce 1.8         2/29/2008 5:28:13 AM   Chengegowda Venkatesh
 *          fix for CR : 30345
 *    8    360Commerce 1.7         1/18/2008 3:58:43 AM   Chengegowda Venkatesh
 *          PABP 30 - Originating Point checkin for BO and CO audit Log
 *    7    360Commerce 1.6         1/10/2008 7:55:40 AM   Manas Sahu      Event
 *          Originator Changes
 *    6    360Commerce 1.5         1/7/2008 8:30:24 AM    Chengegowda Venkatesh
 *          PABP FR40 : Changes for AuditLog incorporation
 *    5    360Commerce 1.4         12/19/2007 9:16:16 AM  Manikandan Chellapan
 *         Reverting back to previous version
 *    4    360Commerce 1.3         12/19/2007 8:14:31 AM  Manikandan Chellapan
 *         PAPB FR27 Bulk Checkin -2
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *
 *   Revision 1.9  2004/08/19 20:13:37  dcobb
 *   @scr 6845 Till suspend / resume need to set transaction status to completed.
 *   Code review modifications.
 *
 *   Revision 1.8  2004/08/18 23:12:34  dcobb
 *   @scr 6845 Till suspend / resume need to set transaction status to completed.
 *
 *   Revision 1.7  2004/08/17 23:40:45  dcobb
 *   @scr 6845 Till suspend / resume need to set transaction status to completed.
 *
 *   Revision 1.6  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:50:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Feb 18 2003 14:52:24   DCobb
 * Moved update register & drawer status to TransactionWriteDataTransaction
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.4   Feb 17 2003 15:43:30   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.3   Feb 12 2003 18:48:06   DCobb
 * Add till to register, add cashier to till (moved from TillEnteredAisle) and update status of register in the database.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.2   Jan 06 2003 12:14:56   DCobb
 * Set till type to stationary.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.1   May 13 2002 19:36:06   mpm
 * Added support for till open/close and register open/close transactions.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:26:04   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.TillOpenEvent;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;

/**
 * Updates till status.
 * 
 * @version $Revision: /main/15 $
 */
@SuppressWarnings("serial")
public class UpdateStatusSite extends PosSiteActionAdapter
{
    public static final String SITENAME = "UpdateStatusSite";
    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Updates till status.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        TillResumeCargo cargo = (TillResumeCargo) bus.getCargo();
        
        boolean saveTransSuccess = true;
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        Letter letter = new Letter (TillLetterIfc.SUCCESS);

        RegisterIfc register = cargo.getRegister();

        // Add till to register if not already in register's list
        TillIfc till = cargo.getTill();
        String tillID = till.getTillID();
        if (register.getTillByID(tillID) == null)
        {
            register.addTill(till);
        }
        till = register.getTillByID(tillID);

        // Add cashier to till
        till.addCashier(cargo.getOperator());

        // Set till status to OPEN
        till.setStatus(AbstractFinancialEntityIfc.STATUS_OPEN);
        // Set till type to stationary
        till.setTillType(AbstractStatusEntityIfc.TILL_TYPE_STATIONARY);
        // Set current till
        register.setCurrentTillID(tillID);

        // set Register - drawer status back to OCCUPIED
        register.getDrawer(DrawerIfc.DRAWER_PRIMARY)
             .setDrawerStatus(AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED, tillID);

        /*
         * Create the Resume Till Transaction
         */
        TillOpenCloseTransactionIfc transaction =
            DomainGateway.getFactory().getTillOpenCloseTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_RESUME_TILL);
        utility.initializeTransaction(transaction, -1);
        transaction.setTimestampEnd();

        // get the Journal manager
        JournalManagerIfc jmi =
            (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        // journal the till status
        if (jmi != null)
        {
            StringBuffer sb = new StringBuffer();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_RESUME_LABEL, null)).append(Util.EOL);
            Object[] dataArgs = new Object[2];
            dataArgs[0] = register.getCurrentTillID();
            sb.append(I18NHelper
					.getString(I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.TILL_ID_LABEL,
							dataArgs)).append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_INSERTED_LABEL, null));
            jmi.journal(cargo.getOperator().getEmployeeID(),
                        transaction.getTransactionID(),
                        sb.toString());
        }
        else
        {
            logger.warn( "No journal manager found.");
        }

        // for Auditlogging
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
    		TillOpenEvent ev = (TillOpenEvent)AuditLoggingUtils.createLogEvent(TillOpenEvent.class, AuditLogEventEnum.TILL_RESUME);
    		ev.setUserId(transaction.getCashier().getLoginID());
    		ev.setStoreId(transaction.getFormattedStoreID());
    		ev.setBusinessDate(transaction.getBusinessDay().dateValue());
    		ev.setRegisterID(transaction.getWorkstation().getWorkstationID());
    		ev.setTillID(transaction.getTillID());
    		ev.setOperatorID(transaction.getCashier().getEmployeeID());
    		ev.setEventOriginator("LookupStoreStatusSite.arrive");
        try
        {
            // set register and till on transaction
            transaction.setRegister(register);
            transaction.setTill(register.getTillByID(tillID));
            transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
            utility.saveTransaction(transaction);

            // for Auditlogging
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

            logger.error( "" + e + "");
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = util.createErrorDialogBeanModel(e);
            //display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
            saveTransSuccess = false;

        }
        if (saveTransSuccess)
        {
        	bus.mail(letter, BusIfc.CURRENT);
        }

    }

}
