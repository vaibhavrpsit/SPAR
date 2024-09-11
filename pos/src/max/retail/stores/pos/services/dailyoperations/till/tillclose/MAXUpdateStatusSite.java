/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/UpdateStatusSite.java /main/14 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * Rev 1.0	Aug 24,2016		Ashish Yadav Changes ofr code merging
 * Initial revision.
 * ===========================================================================
 */
package max.retail.stores.pos.services.dailyoperations.till.tillclose;

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
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillCloseCargo;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.UpdateStatusSite;
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
public class MAXUpdateStatusSite extends UpdateStatusSite
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

	// changes starts for Rev 1.0
	 boolean saveTransSuccess = true;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		// Chnages ends for rev 1.0
        // Local references to register and till.
        RegisterIfc register = cargo.getRegister();
        TillIfc till = register.getTillByID(cargo.getTillID());
        
        //boolean saveTransSuccess = true;
        //POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

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
