/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/EditCheckAisle.java /main/14 2013/04/05 16:39:03 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   04/05/13 - Reading temp employee valid days from text field
 *                         earlier it was combobox.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    glwang    12/01/08 - deprecated employee full name column
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         3/7/2008 3:23:20 AM    Chengegowda Venkatesh
 *          For Audit log fixes
 *    6    360Commerce 1.5         3/5/2008 2:54:53 PM    Anil Bondalapati
 *         updated to fix the display of storeID on the backoffice
 *    5    360Commerce 1.4         9/26/2006 9:20:51 AM   Christian Greene
 *         Removing setPassword, which will be in new aisle
 *    4    360Commerce 1.3         4/2/2006 11:53:42 PM   Dinesh Gautam   Added
 *          code for new fields ‘Employee login Id’ & ‘Verify Password’
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *
 *   Revision 1.9  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.8  2004/06/25 16:20:45  jeffp
 *   @scr 5738 - Added checks to see when editing a temporary employee
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/15 16:57:00  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/16 14:46:51  blj
 *   @scr - 3838 cleanup code
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:59:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 22 2003 17:26:32   DCobb
 * Use EmployeeCargo.getEmployeeStatusFromModel() method to set the employee status. Added check for status change.
 * Resolution for POS SCR-3217: Unable to change employee status from active to inactive
 *
 *    Rev 1.3   Apr 16 2003 19:18:18   baa
 * add status field
 * Resolution for POS SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer screen
 *
 *    Rev 1.2   Apr 11 2003 13:16:54   baa
 * remove usage of  deprecated EployeeIfc methods get/setName
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 *
 *    Rev 1.1   Dec 18 2002 17:40:20   baa
 * add employee preferred locale support
 * Resolution for POS SCR-1843: Multilanguage support
 *
 *    Rev 1.0   Apr 29 2002 15:23:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:20   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Oct 2001 08:45:34   mpm
 * Merged employee changes from Virginia ABC demonstration.
 *
 *    Rev 1.0   Sep 21 2001 11:23:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import java.util.Calendar;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.EmployeeEvent;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeFindForUpdateTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeMasterBeanModel;

/**
 * The EditCheckAisle is traversed when an Accept letter is received. This aisle
 * checks to see if the accepted employee was edited. If so it mails a Continue
 * letter so the name can be checked. If not it mails an Exit letter since the
 * service is finished.
 * 
 * @version $Revision: /main/14 $
 */
public class EditCheckAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -4121205366136344371L;
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * The EditCheckAisle is traversed when an Accept letter is received. This
     * aisle checks to see if the accepted employee was edited. If so it mails a
     * Continue letter so the name can be checked. If not it mails an Exit
     * letter since the service is finished.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {

        Letter result = new Letter(CommonLetterIfc.CONTINUE);
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        EmployeeMasterBeanModel model = null;

        if(EmployeeTypeEnum.TEMPORARY.equals(cargo.getEmployee().getType()))
        {
            model = (EmployeeMasterBeanModel)ui.getModel(POSUIManagerIfc.EMPLOYEE_MASTER_TEMP);
        }
        else
        {
            model = (EmployeeMasterBeanModel)ui.getModel(POSUIManagerIfc.EMPLOYEE_MASTER);
        }

        // get and the id info from the model
        EmployeeIfc employee = cargo.getEmployee();

        // get the name from the model
        employee.getPersonName().setFirstName(model.getFirstName());
        employee.getPersonName().setMiddleName(model.getMiddleName());
        employee.getPersonName().setLastName(model.getLastName());
        employee.setEmployeeID(model.getIDNumber());
        employee.setLoginID(model.getLoginIDNumber());
        employee.setLoginStatus(EmployeeCargo.getEmployeeStatusFromModel(util, model));
        employee.setPreferredLocale(LocaleMap.getSupportedLocales()[model.getSelectedLanguage()]);

        // add valid days to employee if temporary employee
        if (EmployeeTypeEnum.TEMPORARY.equals(cargo.getEmployee().getType()))
        {
            int newDaysValidValue = model.getDaysValidValue();
            employee.setDaysValid(newDaysValidValue);
            if (cargo.getOriginalEmployee().getDaysValid() != newDaysValidValue)
            {
                // Use the days valid setting to calculate the expiration date
                EYSDate expirationDate = new EYSDate();
                expirationDate.add(Calendar.DATE, newDaysValidValue);
                employee.setExpirationDate(expirationDate);
            }
        }

        cargo.setEmployee(employee);

        //get and set the role
        RoleIfc[] roles = cargo.getRoles();
        cargo.getEmployee().setRole(roles[model.getSelectedRole()]);

        // if there have been no changes to the Employee
        if (cargo.getEmployee().equals(cargo.getOriginalEmployee()))
        {
            // For audit logging
			if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
			{
        		logAuditEvent(cargo);
			}

        	//mail letter to go to the name validation service
             result = new Letter(EmployeeCargo.EXIT);
        }

        else
        {
            // Did employee status change?
            if (cargo.getEmployee().getLoginStatus() != cargo.getOriginalEmployee().getLoginStatus())
            {
                result = new Letter(EmployeeCargo.STATUS_CHANGE);
            }
            // did any piece of the name change ?
            else if (!((cargo.getEmployee().getPersonName().getFirstName().equals
                  (cargo.getOriginalEmployee().getPersonName().getFirstName())) &&
                  (cargo.getEmployee().getPersonName().getMiddleName().equals
                  (cargo.getOriginalEmployee().getPersonName().getMiddleName()))&&
                  (cargo.getEmployee().getPreferredLocale().equals
                  (cargo.getOriginalEmployee().getPreferredLocale()))&&
                  (cargo.getEmployee().getPersonName().getLastName().equals
                  (cargo.getOriginalEmployee().getPersonName().getLastName()))))
            {
                // look for duplicate name in the database (employee table)
                EmployeeFindForUpdateTransaction empTransaction = null;

                empTransaction = (EmployeeFindForUpdateTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_FIND_FOR_UPDATE_TRANSACTION);

                EmployeeIfc employeeDuplicate = null;

                try
                {   // check for name already in DB employee table
                    PersonNameIfc name = cargo.getEmployee().getPersonName();
                    employeeDuplicate = empTransaction.getEmployeeName(name);
                   // any duplicates found ?
                    if (employeeDuplicate != null)
                    {
                        // a duplicate employee was found
                        // check if the employee was is the original one
                        if (!Util.isObjectEqual(employeeDuplicate.getPersonName(),cargo.getOriginalEmployee().getPersonName()))
                           result = new Letter(EmployeeCargo.DUPLICATE_NAME);
                    }
                }   // end employee read try block

                catch (DataException de)    // handle data base exceptions
                {
                    // if no matches were found, that's ok - continue
                    if (de.getErrorCode() != DataException.NO_DATA)
                    {
                        cargo.setDataExceptionErrorCode(de.getErrorCode());
                        cargo.setFatalError(true);
                        result = new Letter(CommonLetterIfc.DB_ERROR);

                        logger.error(
                                     "getEmployeeName DB error: " + de.getMessage() + "");
                    }
                }    // end database error catch
            }  // end - if name changed
        }  // end - changes to employee

        bus.mail(result, BusIfc.CURRENT);

    }

	/**
	 * This method logs the Modify Employee Information event
	 * @param cargo EmployeeCargo is this site's cargo
	 */
    private void logAuditEvent(EmployeeCargo cargo)
    {
		AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
		EmployeeEvent ev = (EmployeeEvent) AuditLoggingUtils.createLogEvent(
				EmployeeEvent.class,
				AuditLogEventEnum.MODIFY_EMPLOYEE);

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
		EmployeeIfc originalEmployee = cargo.getOriginalEmployee();
		if (originalEmployee != null)
		{
			ev.setEmployeeID(originalEmployee.getEmployeeID());
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
		ev.setEventOriginator("EditSelectedEmployeeSite.arrive");

		auditService.logStatusSuccess(ev);
    }
}

