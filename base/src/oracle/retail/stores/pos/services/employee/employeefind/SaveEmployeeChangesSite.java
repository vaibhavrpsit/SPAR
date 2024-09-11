/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/SaveEmployeeChangesSite.java /main/18 2013/10/15 14:16:22 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    hyin      02/25/11 - use utility method
 *    hyin      02/24/11 - add ej entry and audit log for fingerprint change
 *                         event
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *10   360Commerce 1.9         3/5/2008 8:48:53 AM    Chengegowda Venkatesh
 *     Fixed CR 30507
 *9    360Commerce 1.8         2/27/2008 4:44:14 AM   Chengegowda Venkatesh
 *     Rectified Audit Log errors
 *8    360Commerce 1.7         2/27/2008 4:05:54 AM   Chengegowda Venkatesh
 *     Rectified an error in Audit log
 *7    360Commerce 1.6         1/10/2008 7:59:11 AM   Manas Sahu      Event
 *     Originator Changes
 *6    360Commerce 1.5         1/7/2008 8:28:47 AM    Chengegowda Venkatesh
 *     PABP FR40 : Changes for AuditLog incorporation
 *5    360Commerce 1.4         9/29/2006 3:15:25 PM   Christian Greene Do not
 *     journal password changes
 *4    360Commerce 1.3         4/2/2006 11:56:16 PM   Dinesh Gautam   Added
 *     code for new fields �Employee login Id� & �Verify Password�
 *3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse
 *2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse
 *1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *
 Revision 1.10  2004/08/18 18:24:55  jdeleau
 @scr 6437 Correct typo (Change Password Changed to Password Change)
 *
 Revision 1.9  2004/08/18 18:23:15  jdeleau
 @scr 6437 Don't show passwords in e-journal
 *
 Revision 1.8  2004/06/03 14:47:44  epd
 @scr 5368 Update to use of DataTransactionFactory
 *
 Revision 1.7  2004/04/20 13:13:09  tmorris
 @scr 4332 -Sorted imports
 *
 Revision 1.6  2004/04/13 02:26:34  pkillick
 @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 Revision 1.5  2004/03/14 21:24:26  tfritz
 @scr 3884 - New Training Mode Functionality
 *
 Revision 1.4  2004/03/03 23:15:07  bwf
 @scr 0 Fixed CommonLetterIfc deprecations.
 *
 Revision 1.3  2004/02/12 16:50:18  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:39:47  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:59:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 22 2003 17:22:46   DCobb
 * Journal employee ID, status and preferred language changes.
 * Resolution for POS SCR-3217: Unable to change employee status from active to inactive
 *
 *    Rev 1.2   Apr 16 2003 19:18:18   baa
 * add status field
 * Resolution for POS SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer screen
 *
 *    Rev 1.1   Apr 11 2003 13:17:08   baa
 * remove usage of  deprecated EployeeIfc methods get/setName
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 *
 *    Rev 1.0   Apr 29 2002 15:23:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:32   msg
 * Initial revision.
 *
 *    Rev 1.3   11 Mar 2002 17:15:04   sfl
 * Moved the "Entering Employee" E-Journaling to the
 * higher level location at EmployeeLaunchSite so that
 * every activity to enter the Employee service will be
 * e-journaled.
 * Resolution for POS SCR-1524: Entering and Exiting Employee is not being written to the E Journal
 *
 *    Rev 1.2   27 Oct 2001 08:47:44   mpm
 * Merged additional changes from Virginia ABC demo.
 *
 *    Rev 1.0   Sep 21 2001 11:23:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import java.util.Locale;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.EmployeeEvent;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeWriteTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//------------------------------------------------------------------------------
/**
 *
 * The SaveEmployeeChanges site saves to the database any changes made by the
 * user.
 *
 */
//------------------------------------------------------------------------------
public class SaveEmployeeChangesSite extends PosSiteActionAdapter {

	public static final String SITENAME = "SaveEmployeeChangesSite";

	// --------------------------------------------------------------------------
	/**
	 *
	 * The SaveEmployeeChanges site saves to the database any changes made by
	 * the user.
	 *
	 * @param bus
	 *            the bus arriving at this site
	 *
	 */
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {

		Letter result = null; // letter to send to move us along.

		EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
		EmployeeIfc currentEmployee = cargo.getEmployee();
		EmployeeIfc originalEmployee = cargo.getOriginalEmployee();

		// for Audit Logging
		EmployeeEvent ev = null;
		AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
		if (EmployeeTypeEnum.TEMPORARY.equals(currentEmployee.getType()))
		{
			ev = (EmployeeEvent) AuditLoggingUtils.createLogEvent(
					EmployeeEvent.class,
					AuditLogEventEnum.MODIFY_TEMPORARY_EMPLOYEE);
		}
		else
		{
			ev = (EmployeeEvent) AuditLoggingUtils.createLogEvent(
					EmployeeEvent.class, AuditLogEventEnum.MODIFY_EMPLOYEE);
		}
		prepareEventToLog(cargo, ev);

		// do the database save here
		EmployeeWriteTransaction empTransaction = null;
		empTransaction = (EmployeeWriteTransaction) DataTransactionFactory
		.create(DataTransactionKeys.EMPLOYEE_WRITE_TRANSACTION);

		try
		{
			// Update the employee.
			if (!cargo.getRegister().getWorkstation().isTrainingMode())
			{
				empTransaction.updateEmployee(currentEmployee);
			}

			// For audit logging
			if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
			{
				auditService.logStatusSuccess(ev);
			}

			// journal the save
            StringBuffer journalString = new StringBuffer();
            Object[] dataArgs = new Object[2];
            dataArgs[0] = cargo.getEmployee().getLoginID();
            journalString.append(I18NHelper.getString(
                    I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.FIND_EMPLOYEE_LABEL, dataArgs));
            
            // put all of the changes in a journal string.
            // Compare first names; journal if changed.
            String current = currentEmployee.getPersonName().getFirstName();
            String original = originalEmployee.getPersonName().getFirstName();
            if (!current.equals(original)) {
                journalString.append(Util.EOL);
                dataArgs[0] = original;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_FIRST_NAME_LABEL, dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = current;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.FIRST_NAME_LABEL, dataArgs));
            }

            // Compare middle names; journal if changed.
            current = currentEmployee.getPersonName().getMiddleName();
            original = originalEmployee.getPersonName().getMiddleName();
            if (!current.equals(original)) {
                journalString.append(Util.EOL);
                dataArgs[0] = original;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_MIDDLE_NAME_LABEL, dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = current;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.MIDDLE_NAME_LABEL, dataArgs));

            }

            // Compare last names; journal if changed.
            current = currentEmployee.getPersonName().getLastName();
            original = originalEmployee.getPersonName().getLastName();
            if (!current.equals(original)) {
                journalString.append(Util.EOL);
                dataArgs[0] = original;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_LAST_NAME_LABEL, dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = current;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.LAST_NAME_LABEL, dataArgs));
            }

            // Compare employee ids; journal if changed
            current = currentEmployee.getEmployeeID();
            original = originalEmployee.getEmployeeID();
            if (!current.equals(original)) {
                journalString.append(Util.EOL);
                dataArgs[0] = original;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_EMPLOYEE_ID_LABEL, dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = current;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.EMPLOYEE_ID_LABEL, dataArgs));
            }

            // Compare employee login ids; journal if changed
            current = currentEmployee.getLoginID();
            original = originalEmployee.getLoginID();
            if (!current.equals(original)) {
                journalString.append(Util.EOL);
                dataArgs[0] = original;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_EMPLOYEE_LOGIN_ID_LABEL,
                        dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = current;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.EMPLOYEE_LOGIN_ID_LABEL, dataArgs));
            }

            if (!current.equals(original)) {
                journalString.append(Util.EOL);
                dataArgs[0] = original;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_GOVERNMENT_ID_NUMBER_LABEL,
                        dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = current;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.GOVERNMENT_ID_NUMBER_LABEL,
                        dataArgs));
            }

            // Compare role ids; journal role title if changed.
            int currentId = currentEmployee.getRole().getRoleID();
            int originalId = originalEmployee.getRole().getRoleID();
            if (currentId != originalId) {
                // get the needed locale
                Locale journalLocale = LocaleMap
                        .getLocale(LocaleConstantsIfc.JOURNAL);

                journalString.append(Util.EOL);
                dataArgs[0] = originalEmployee.getRole()
                        .getTitle(journalLocale);
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_ROLE_LABEL, dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = cargo.getEmployee().getRole().getTitle(
                        journalLocale);
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.ROLE_LABEL, dataArgs));
            }

            // Compare status; journal if changed
            current = currentEmployee.loginStatusToString();
            original = originalEmployee.loginStatusToString();
            if (!current.equals(original)) {
                journalString.append(Util.EOL);
                dataArgs[0] = original;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_STATUS_LABEL, dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = current;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.STATUS_ID_LABEL, dataArgs));
            }

            // Compare preferred language; journal if changed
            Locale currentLocale = currentEmployee.getPreferredLocale();
            Locale originalLocale = originalEmployee.getPreferredLocale();
            if (!currentLocale.equals(originalLocale)) {
                journalString.append(Util.EOL);
                dataArgs[0] = originalLocale;
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_PREFERRED_LANGUAGE_LABEL,
                        dataArgs));
                journalString.append(Util.EOL);
                dataArgs[0] = currentLocale;
                journalString
                        .append(I18NHelper.getString(
                                I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.PREFERRED_LANGUAGE_LABEL,
                                dataArgs));
            }

            // if a new fingerprint has been enrolled
            if (cargo.isEnrolledNewFingerprint()) {

                journalString.append(Util.EOL);
                journalString.append(I18NHelper.getString(
                        I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.EMPLOYEE_FINGERPRINT_CHANGED,
                        dataArgs));
                EmployeeFindUtility.logAuditEvent(true, cargo,
                        AuditLogEventEnum.ENROLL_EMPLOYEE_FINGERPRINT,
                        "SaveEmployeeChangeSite.arrive");

                cargo.setEnrolledNewFingerprint(false);
            }

            // journalString += ("Exiting Employee");

            EmployeeFindUtility.logEJEntry(bus, journalString.toString());

			// If this employee is a logged on cashier, update the cashier in
			// the
			// register.
			EmployeeIfc employee = cargo.getEmployee();
			EmployeeIfc cashier = cargo.getRegister().getCashierByID(
					employee.getEmployeeID());
			if (cashier != null)
			{
				result = new Letter(CommonLetterIfc.UPDATE_HARD_TOTALS);
				cashier.setRole(employee.getRole());
			}
			else
			{
				result = new Letter(CommonLetterIfc.CONTINUE);
			}

		}
		catch (DataException de)
		{
			// log the error; set the error code in the cargo for future use.
			cargo.setDataExceptionErrorCode(de.getErrorCode());

			cargo.setFatalError(true);
			result = new Letter(CommonLetterIfc.DB_ERROR);

			// For audit logging
			if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
			{
				auditService.logStatusFailure(ev);
			}

			// journal the database error status
			EmployeeFindUtility.logEJEntry(bus, I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SAVE_UNSUCCESSFUL_LABEL, null) + Util.EOL);

			logger.error("Employee error: " + de.getMessage() + "");
		}

		// Mail the appropriate result to continue
		bus.mail(result, BusIfc.CURRENT);

	}

	// --------------------------------------------------------------------------
	/**
	 * This method populates the Employee event with all the information that
	 * has to be logged
	 *
	 * @return
	 * @param ev
	 *            <code>EmployeeEvent</code> is the event object
	 * @param cargo
	 *            <EmployeeCargo</code> is this site's cargo
	 */
	// --------------------------------------------------------------------------
	private void prepareEventToLog(EmployeeCargo cargo, EmployeeEvent ev)
	{
		RegisterIfc ri = cargo.getRegister();
		if (ri != null)
		{
			WorkstationIfc wi = ri.getWorkstation();
			if (wi != null)
			{
				ev.setStoreId(wi.getStoreID());
				ev.setRegisterNumber(wi.getWorkstationID());
			}
		}
		EmployeeIfc ei = cargo.getEmployee();
		{
			if (ei != null)
			{
				ev.setEmployeeID(cargo.getEmployee().getEmployeeID());
			}
		}
		StoreStatusIfc ssi = cargo.getStoreStatus();
		if (ssi != null)
		{
			EmployeeIfc eis = ssi.getSignOnOperator();
			if (eis != null)
			{
				ev.setUserId(eis.getLoginID());
			}
		}

		if (EventOriginatorInfoBean.getEventOriginator() != null)
		{
			ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
		}
	}

} // end class SaveEmployeeChangesSite
