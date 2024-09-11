/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/SaveEmployeeSite.java /main/17 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    rrkohli   11/12/10 - added fix to display store id in audit log for add
 *                         employee event
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
 *    8    360Commerce 1.7         3/7/2008 3:21:55 AM    Chengegowda Venkatesh
 *          For Audit Log Fixes
 *    7    360Commerce 1.6         3/5/2008 8:48:04 AM    Chengegowda Venkatesh
 *          Fixed CR 30507
 *    6    360Commerce 1.5         2/27/2008 7:22:12 AM   Chengegowda Venkatesh
 *          For audit log fixes
 *    5    360Commerce 1.4         2/27/2008 7:17:55 AM   Chengegowda Venkatesh
 *          Added event log method for Add Employee
 *    4    360Commerce 1.3         9/27/2006 4:58:08 PM   Christian Greene Add
 *         functionality to display temp password after adding a standard
 *         employee
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *
 *   Revision 1.9  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/06/02 17:41:54  dfierling
 *   @scr 5272 - Altered program flow after Employee saved
 *
 *   Revision 1.7  2004/04/20 13:13:10  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.6  2004/04/13 02:26:35  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/03/14 21:24:26  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:24  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:59:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 08 2003 15:27:16   bwf
 * Write correct error message to journal.
 * Resolution for 3070: Referential Integrity Error received when adding an employee in POS prints to the ejournal.
 *
 *    Rev 1.0   Apr 29 2002 15:24:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:14   msg
 * Initial revision.
 *
 *    Rev 1.1   11 Mar 2002 17:14:00   sfl
 * Moved the "Entering Employee" E-Journaling to the
 * higher level location at EmployeeLaunchSite so that
 * every activity to enter the Employee service will be
 * e-journaled.
 * Resolution for POS SCR-1524: Entering and Exiting Employee is not being written to the E Journal
 *
 *    Rev 1.0   Sep 21 2001 11:23:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.EmployeeEvent;
import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.arts.EmployeeWriteTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;

/**
 * The SaveEmployee site is used to make the database call that saves the
 * employee.

 */
public class SaveEmployeeSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 536478069590499054L;

    public static final String SITENAME = "SaveEmployeeSite";

    /**
     * Writes the employee data to the database, journals the adding of the
     * employee, and logs database errors.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter result = new Letter(CommonLetterIfc.CONTINUE);
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();

        // get the Journal manager
        JournalManagerIfc jmi =
            (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

        EmployeeWriteTransaction empTransaction = null;
        empTransaction = (EmployeeWriteTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_WRITE_TRANSACTION);

        // for Audit Logging
        EmployeeEvent ev=null;
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
        if(EmployeeTypeEnum.STANDARD.equals(cargo.getAddEmployeeType()))
        {
        	ev = (EmployeeEvent)AuditLoggingUtils.createLogEvent(EmployeeEvent.class, AuditLogEventEnum.ADD_EMPLOYEE);
        }
        else
        {
        	ev = (EmployeeEvent)AuditLoggingUtils.createLogEvent(EmployeeEvent.class, AuditLogEventEnum.ADD_TEMPORARY_EMPLOYEE);
        }
        prepareEventToLog(cargo,ev);

        try
        {
           	// begin employee save try block
            if (!cargo.getRegister().getWorkstation().isTrainingMode())
            {
                empTransaction.insertEmployee(cargo.getEmployee());
            }

            // for Audit Logging
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
            	auditService.logStatusSuccess(ev);
            }

            // journal the save
            if (jmi != null)
            {
               // String journalString = "\nEntering Employee\n  Add Employee " +
               //     cargo.getEmployee().getLoginID() + "\nExiting Employee";
            	Object[] dataArgs = new Object[2];
        		dataArgs[0] = cargo.getEmployee().getLoginID();
                String journalString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ADD_EMPLOYEE_LABEL, dataArgs);

                jmi.journal(journalString);
            }
            else
            {
                logger.warn( "No journal manager found.");
            }

        }                               // end employee save try block
        catch (DataException de)
        {
            // log the error; set the error code in the cargo for future use.
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            cargo.setFatalError(true);
            result = new Letter(CommonLetterIfc.DB_ERROR);

            // for Audit Logging
            if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
            	auditService.logStatusFailure(ev);
            }

            // journal the database error status
            if (jmi != null)
            {
                jmi.journal(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SAVE_UNSUCCESSFUL_ERROR_CODE_LABEL, null)+ Util.EOL + de.getErrorCodeString());
            }
            else
            {
                logger.warn( "No journal manager found.");
            }

            logger.error("Employee error: ", de);
        }

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);
    }

    /**
     * This method populates the Employee event with all the information that
     * has to be logged
     * 
     * @return
     * @param ev <code>EmployeeEvent</code> is the event object
     * @param cargo <EmployeeCargo</code> is this site's cargo
     */
    private void prepareEventToLog(EmployeeCargo cargo,EmployeeEvent ev)
    {
    	EmployeeIfc employeeLoggedIn=null;
        String userId  = null;
        EmployeeTransaction empFetchTransaction = null;
        
        // get the needed locale
        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
                
        if (EventOriginatorInfoBean.getEventOriginator() != null)
        {
          ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
        }

        RegisterIfc  ri = cargo.getRegister();
        if(ri!=null)
        {
        	WorkstationIfc wi = ri.getWorkstation();
        	if(wi!=null)
        	{
        		ev.setEmployeeStoreNumber(wi.getStoreID());
        		ev.setRegisterNumber(wi.getWorkstationID());
        	}
        }
        EmployeeIfc addedEmployee = cargo.getEmployee();
        if(addedEmployee!=null)
        {
        	ev.setEmployeeID(addedEmployee.getEmployeeID());
        	ev.setFirstName(addedEmployee.getPersonName().getFirstName());
        	ev.setLastName(addedEmployee.getPersonName().getLastName());
        	ev.setMiddleName(addedEmployee.getPersonName().getMiddleName());
        	ev.setEmployeeLoginID(addedEmployee.getLoginID());
        	ev.setRoleName(addedEmployee.getRole().getTitle(journalLocale));
        	ev.setStoreId(addedEmployee.getStoreID());
            if(addedEmployee.getLoginStatus()-1 == EmployeeStatusEnum.ACTIVE.getValue())
        	{
        		ev.setEmployeeStatus(EmployeeStatusEnum.ACTIVE.getName());
        	}
        	else
        	{
        		ev.setEmployeeStatus(EmployeeStatusEnum.INACTIVE.getName());
        	}
            if(EmployeeTypeEnum.TEMPORARY.equals(cargo.getAddEmployeeType()))
            {
            	ev.setDaysValid(String.valueOf(addedEmployee.getDaysValid()));
            }
        }
        try
        {
        	empFetchTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
			employeeLoggedIn = empFetchTransaction.getEmployeeNumber(cargo.getOperatorID());
			if(employeeLoggedIn!=null)
			{
				userId = employeeLoggedIn.getLoginID();
	        	ev.setUserId(userId);
			}
		}
        catch (DataException e)
		{
			// Do Nothing Here
			// The code in the try block above is only for Auditlogging; On an exception,
			// one field UserId will not be Auditlogged. And the SaveEmployeeTransaction
			// is let to proceed uninterrupted by this AuditLog exception
		}
    }
}
