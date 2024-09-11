/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/UpdateStatusSite.java /main/14 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *6    360Commerce 1.5         3/6/2008 1:27:18 PM    Chengegowda Venkatesh For
 *      Audit Log
 *5    360Commerce 1.4         1/10/2008 7:53:48 AM   Manas Sahu      Event
 *     Originator Changes
 *4    360Commerce 1.3         1/7/2008 8:36:36 AM    Chengegowda Venkatesh
 *     Changes for AuditLog incorporation
 *3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
 *1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *
 Revision 1.12  2004/07/23 16:19:51  dcobb
 @scr 6428 suspend/resume till - can't resume till after different user closes their till
 Update the currentTillID for the register before the save operations.
 *
 Revision 1.11  2004/06/30 18:18:00  dcobb
 @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 Revision 1.10  2004/06/30 00:21:24  dcobb
 @scr 5165 - Allowed to reconcile till when database is offline.
 @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 Revision 1.9  2004/06/25 22:33:10  dcobb
 @scr 4205 Feature Enhancement: Till Options
 Addjouranling for foreign currency count.
 *
 Revision 1.8  2004/06/24 21:40:03  dcobb
 @scr 5263 - Can't resume suspended till.
 If the till has been removed from the drawer, set the till status to floating and update the current till for the register.
 *
 Revision 1.7  2004/06/24 17:18:58  dcobb
 @scr 5263 - Can't resume suspended till.
 Backed out khassen changes.
 *
 Revision 1.6  2004/06/23 14:24:07  khassen
 @scr 5263 - Updated register to reflect current status of till.
 *
 Revision 1.5  2004/05/17 20:23:42  dcobb
 @scr 4204 Feature Enhancement: Till Options
 Added RemoveTillRoad to tillreconcile. Drawer status is set in RemoveTillRoad.
 *
 Revision 1.4  2004/02/27 18:38:57  tfritz
 @scr 0 - Changed location of the CommonLetterIfc class
 *
 Revision 1.3  2004/02/12 16:49:58  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:47:18  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:57:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   Mar 28 2003 11:21:22   sfl
 * Removed the line 355 because the extra spaces are not needed.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.6   Jan 21 2003 17:41:50   sfl
 * Reduced 4 spaces for displaying payment amount in EJ.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.5   Jan 21 2003 17:35:26   sfl
 * Adjust the print format based on EJ requirements.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.4   Jan 10 2003 13:35:36   sfl
 * Shorted the printed float amount to be two digits after
 * decimal point.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.3   Sep 03 2002 16:03:42   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   May 14 2002 11:34:10   mpm
 * Corrected defect in close-not-reconciled flow.
 * Resolution for POS SCR-1641: Till Options - Choosing not to reconcile at the workstation crashes the system
 *
 *    Rev 1.1   May 13 2002 19:36:04   mpm
 * Added support for till open/close and register open/close transactions.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:28:44   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

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
 * Updates the till status, totals, operator, and closing time. Updates the
 * database with this till information.
 * 
 * @version $Revision: /main/14 $
 */
@SuppressWarnings("serial")
public class UpdateStatusSite extends PosSiteActionAdapter
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Updates the till status by setting the status to reconciled, setting the
     * closing time, setting the operator, and updating the database with this
     * tills totals.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        TillCloseCargo cargo = (TillCloseCargo) bus.getCargo();

        // Local references to register and till.
        RegisterIfc register = cargo.getRegister();
        TillIfc till = register.getTillByID(cargo.getTillID());
        
        boolean saveTransSuccess = true;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // create close till transaction
        TillOpenCloseTransactionIfc transaction =
            DomainGateway.getFactory().getTillOpenCloseTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_CLOSE_TILL);
        utility.initializeTransaction(transaction);
        transaction.setTimestampEnd();

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

        if (till.getTillType() == AbstractStatusEntityIfc.TILL_TYPE_FLOATING)
        {
            // Update current till to indicate an empty drawer
            TillIfc currentTill = register.getCurrentTill();
            if (currentTill != null) {
                String currentTillID = currentTill.getTillID();
                if (currentTillID != null)
                {
                    if (currentTillID.equalsIgnoreCase(till.getTillID()))
                    {
                        register.setCurrentTillID("");
                    }
                }
            }
            register.setFloatingTill(true);
        }
        register.setTillClose(true);
        register.setClosingTillID(till.getTillID());
        // Mark the status of the till based on flag in cargo
        till.setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);
        till.setCloseTime();
        till.setSignOffOperator(cargo.getOperator());

        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)
        bus.getManager(JournalManagerIfc.TYPE);

        // journal and save the updated statuses and till close transactions.
        journalTillClose(till, transaction, cargo, jmi);

        // for Auditlogging
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
        TillOpenEvent ev = (TillOpenEvent)AuditLoggingUtils.createLogEvent(TillOpenEvent.class, AuditLogEventEnum.TILL_CLOSE);
        ev.setUserId(transaction.getCashier().getLoginID());
        ev.setStoreId(transaction.getFormattedStoreID());
        ev.setBusinessDate(transaction.getBusinessDay().dateValue());
        ev.setRegisterID(transaction.getWorkstation().getWorkstationID());
        ev.setTillID(transaction.getTillID());
        ev.setOperatorID(transaction.getCashier().getEmployeeID());
        ev.setEventOriginator("LookupStoreStatusSite.arrive");

        // save all common data.
        // Note: These are saved after till totals so that the totals go first into the transaction queue
        try
        {
            transaction.setRegister(register);
            transaction.setTill(till);
            // save the till close transaction
            utility.saveTransaction(transaction);

            //for Auditlogging
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditService.logStatusSuccess(ev);
            }
        }
        catch (DataException e)
        {
            //for Auditlogging
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditService.logStatusFailure(ev);
            }
            logger.error(e.toString());
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = util.createErrorDialogBeanModel(e);
            //display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
            saveTransSuccess = false;
        }
        if (saveTransSuccess)
        {
        	bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }

    //---------------------------------------------------------------------
    /**
     Journals the till close. <P>
     @param till till object
     @param transaction TillOpenCloseTransaction object
     @param cargo cargo object
     @param jmi JournalManagerIfc object
     **/
    //---------------------------------------------------------------------
    protected void journalTillClose(TillIfc till,
            TillOpenCloseTransactionIfc transaction,
            TillCloseCargo cargo,
            JournalManagerIfc jmi)
    {                                   // begin journalTillClose()
        // journal the till status
        if (jmi != null)
        {
            StringBuffer sb = new StringBuffer();
            // Default journal Title, TillID for Parameter
            // TillCloseCountFloat = 'No', 'Summary' or 'Detail'.
			sb.append(
					I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.CLOSE_TILL_LABEL, null))
					.append(Util.EOL).append(Util.EOL);
			Object[] dataArgs = new Object[2];
			dataArgs[0] = cargo.getTillID();
			sb
					.append(
							I18NHelper
									.getString(I18NConstantsIfc.EJOURNAL_TYPE,
											JournalConstantsIfc.TILL_ID_LABEL,
											dataArgs))
					.append(Util.EOL)
					.append(
							I18NHelper.getString(
									I18NConstantsIfc.EJOURNAL_TYPE,
									JournalConstantsIfc.STATUS_LABEL, null))
					.append(
							I18NHelper
									.getString(
											I18NConstantsIfc.EJOURNAL_TYPE,
											"JournalEntry."
													+ AbstractStatusEntityIfc.STATUS_DESCRIPTORS[till
															.getStatus()]
													+ "Label", null)).append(
							Util.EOL);

			jmi.journal(cargo.getOperator().getEmployeeID(), transaction
					.getTransactionID(), sb.toString());
        }
    }                                   // end journalTillClose()

}
