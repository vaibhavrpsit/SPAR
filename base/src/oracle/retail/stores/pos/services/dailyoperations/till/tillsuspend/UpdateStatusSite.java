/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillsuspend/UpdateStatusSite.java /main/14 2012/09/12 11:57:09 blarsen Exp $
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
 *    nkgautam  04/09/09 - EJ Fix for space required between strings
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         2/29/2008 5:28:13 AM   Chengegowda Venkatesh
 *          fix for CR : 30345
 *    8    360Commerce 1.7         1/18/2008 3:59:18 AM   Chengegowda Venkatesh
 *          PABP 30 - Originating Point checkin for BO and CO audit Log
 *    7    360Commerce 1.6         1/10/2008 7:56:06 AM   Manas Sahu      Event
 *          Originator Changes
 *    6    360Commerce 1.5         1/7/2008 8:29:41 AM    Chengegowda Venkatesh
 *          PABP FR40 : Changes for AuditLog incorporation
 *    5    360Commerce 1.4         12/19/2007 9:16:28 AM  Manikandan Chellapan
 *         Reverting back to previous version
 *    4    360Commerce 1.3         12/19/2007 8:14:56 AM  Manikandan Chellapan
 *         PAPB FR27 Bulk Checkin -2
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *
 *   Revision 1.12  2004/08/19 20:13:37  dcobb
 *   @scr 6845 Till suspend / resume need to set transaction status to completed.
 *   Code review modifications.
 *
 *   Revision 1.11  2004/08/18 23:12:35  dcobb
 *   @scr 6845 Till suspend / resume need to set transaction status to completed.
 *
 *   Revision 1.10  2004/08/17 23:41:07  dcobb
 *   @scr 6845 Till suspend / resume need to set transaction status to completed.
 *
 *   Revision 1.9  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.7  2004/04/14 18:02:56  pkillick
 *   @scr 4332 -Fixed missing imports
 *
 *   Revision 1.6  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/03/19 21:48:41  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications. Further abstractions.
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.8   May 05 2003 14:52:14   RSachdeva
 * Till Id Comparison Check
 * Resolution for POS SCR-2271: POS is crashed at till reconcile in below case.
 *
 *    Rev 1.7   Feb 17 2003 15:43:30   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.6   Feb 12 2003 18:49:40   DCobb
 * Don't remove till from register; set floating till indicator instead.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.5   Jan 15 2003 12:41:02   DCobb
 * Check for null.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.4   Jan 10 2003 16:51:38   DCobb
 * Set current till ID before removing till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.3   Jan 06 2003 12:16:46   DCobb
 * Set the register's current till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.2   Dec 20 2002 11:32:06   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.1   May 13 2002 19:36:08   mpm
 * Added support for till open/close and register open/close transactions.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:25:40   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillsuspend;

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
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Updates till status.
 * 
 * @version $Revision: /main/14 $
 */
@SuppressWarnings("serial")
public class UpdateStatusSite extends PosSiteActionAdapter
{
    public static final String SITENAME = "UpdateStatusSite";
    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Updates till status.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        TillSuspendCargo cargo = (TillSuspendCargo) bus.getCargo();

        Letter letter = new Letter (CommonLetterIfc.SUCCESS);
        
    	boolean saveTransSuccess = true;
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Set till status to SUSPENDED
        String tillID = "";

        tillID = cargo.getTill().getTillID();

        cargo.getRegister().getTillByID(tillID).
          setStatus(AbstractFinancialEntityIfc.STATUS_SUSPENDED);

        // Create the Suspend Till Transaction
        TillOpenCloseTransactionIfc transaction =
            DomainGateway.getFactory().getTillOpenCloseTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_SUSPEND_TILL);
        utility.initializeTransaction(transaction, -1);
        transaction.setTimestampEnd();

        // get the Journal manager
        JournalManagerIfc jmi =
            (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        // journal the till status
        if (jmi != null)
        {
            StringBuffer sb = new StringBuffer();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_SUSPEND_LABEL, null));
            sb.append(Util.EOL);
            Object[] dataArgs = new Object[2];
			dataArgs[0] = cargo.getRegister().getCurrentTillID();
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.TILL_ID_LABEL, dataArgs));
			 sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_TAG_LABEL, null)).append(" ");
            if (cargo.getRegister()
                     .getDrawer(DrawerIfc.DRAWER_PRIMARY)
                     .getDrawerStatus() == AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED)
            {
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NOT_LABEL, null)).append(" ");
            }
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REMOVED_LABEL, null)).append(" ");
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
    		TillOpenEvent ev = (TillOpenEvent)AuditLoggingUtils.createLogEvent(TillOpenEvent.class, AuditLogEventEnum.TILL_SUSPEND);
    		ev.setUserId(transaction.getCashier().getLoginID());
    		ev.setStoreId(transaction.getFormattedStoreID());
    		ev.setBusinessDate(transaction.getBusinessDay().dateValue());
    		ev.setRegisterID(transaction.getWorkstation().getWorkstationID());
    		ev.setTillID(transaction.getTillID());
    		ev.setOperatorID(transaction.getCashier().getEmployeeID());
    		ev.setEventOriginator("LookupStoreStatusSite.arrive");

        try
        {
            //get register from cargo
            RegisterIfc register = cargo.getRegister();

            //get till from register
            TillIfc till = register.getTillByID(tillID);

            //save current register accountability in the register
            till.setRegisterAccountability(register.getAccountability());

            // Check if till has been removed from drawer. Till is not
            // floating if it hasn't been removed
            if (register.getDrawer(DrawerIfc.DRAWER_PRIMARY)
                .getDrawerStatus() == AbstractStatusEntityIfc.DRAWER_STATUS_UNOCCUPIED)
            {
                till.setTillType(AbstractStatusEntityIfc.TILL_TYPE_FLOATING);
            }
            else
            {
                till.setTillType(AbstractStatusEntityIfc.TILL_TYPE_STATIONARY);
            }

            // If till has been removed from drawer, update currentTillID
            if (till.getTillType() == AbstractStatusEntityIfc.TILL_TYPE_FLOATING)
            {
                // Update current till to indicate an empty drawer
                TillIfc currentTill = register.getCurrentTill();
                if (currentTill != null) {
                    String currentTillID = currentTill.getTillID();
                    if (currentTillID != null)
                    {
                        if (currentTillID.equalsIgnoreCase(tillID))
                        {
                            register.setCurrentTillID("");
                        }
                    }
                }
                register.setFloatingTill(true);
            }

            transaction.setRegister(cargo.getRegister());
            transaction.setTill(cargo.getRegister().getTillByID(tillID));
            // Save this transaction seperately since it doesn't determine
            // the success or failure of suspending the till
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
            saveTransSuccess = false;
            UtilityManagerIfc utili = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = utili.createErrorDialogBeanModel(e);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
        }
        if (saveTransSuccess)
        {
        	bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
