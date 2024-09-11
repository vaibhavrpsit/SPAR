/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/setaccess/SetAccessUpdatesAisle.java /main/23 2013/03/22 16:31:09 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  03/19/13 - Restricting access point and role access based on
 *                         operators role
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    blarsen   03/06/12 - Getting appID from common Ifc.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    kelesika  10/06/10 - RoelEvent audit, store ID
 *    acadar    08/23/10 - changes for roles
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nkgautam  05/12/09 - EJ Changes to include space between function and
 *                         function value
 *    vchengeg  12/08/08 - EJ I18n formatting
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *      8    360Commerce 1.7         6/12/2008 11:27:43 AM  Manas Sahu      The
 *            Catch block in SetAccessUpdatesAisle to have register ID and
 *           User login ID for Audit event. But for User Login ID we need to
 *           pass the Operator from SecurityCargo to RoleMainCargo and then to
 *            SetAccessCargo. Code reviewed by Naveen
 *      7    360Commerce 1.6         6/7/2008 1:19:59 AM    Manikandan
 *           Chellapan CR31958 Audit log prefixed role features with
 *           application name
 *      6    360Commerce 1.5         2/28/2008 5:56:42 AM   Chengegowda
 *           Venkatesh Audit Log fixes
 *      5    360Commerce 1.4         1/10/2008 7:44:00 AM   Manas Sahu
 *           Event Originator changes
 *      4    360Commerce 1.3         1/7/2008 8:40:52 AM    Chengegowda
 *           Venkatesh Changes for AuditLog incorporation
 *      3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:25:14 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:14:11 PM  Robert Pearse
 *     $
 *     Revision 1.8.2.1  2004/10/15 18:50:28  kmcbride
 *     Merging in trunk changes that occurred during branching activity
 *
 *     Revision 1.9  2004/10/11 22:00:48  jdeleau
 *     @scr 7306 Fix roles not appearing after they are created
 *
 *     Revision 1.8  2004/06/03 14:47:45  epd
 *     @scr 5368 Update to use of DataTransactionFactory
 *
 *     Revision 1.7  2004/04/20 13:11:00  tmorris
 *     @scr 4332 -Sorted imports
 *
 *     Revision 1.6  2004/04/14 15:17:10  pkillick
 *     @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *     Revision 1.5  2004/03/14 21:19:34  tfritz
 *     @scr 3884 - New Training Mode Functionality
 *
 *     Revision 1.4  2004/03/03 23:15:15  bwf
 *     @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *     Revision 1.3  2004/02/12 16:48:59  mcs
 *     Forcing head revision
 *
 *     Revision 1.2  2004/02/11 21:36:54  rhafernik
 *     @scr 0 Log4J conversion and code cleanup
 *
 *     Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *     updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 03 2003 10:36:12   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 30 2002 16:33:14   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:37:52   msg
 * Initial revision.
 *
 *    Rev 1.3   04 Apr 2002 15:22:18   baa
 * Remove references to Rolefunction descriptor array and maximun number of role functions
 * Resolution for POS SCR-1565: Remove references to RoleFunctionIfc.Descriptor Security Service
 *
 *    Rev 1.2   21 Mar 2002 13:07:36   baa
 * remove extra line from journal
 * Resolution for POS SCR-512: Role Add in EJ prints double spaced
 *
 *    Rev 1.1   Mar 18 2002 23:07:18   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:16   msg
 * Initial revision.
 *
 *    Rev 1.2   03 Mar 2002 20:22:52   baa
 * role functions
 * Resolution for POS SCR-1483: Adding a new role to current employee then viewing role shows
 * role out of order & change in functions
 *
 *    Rev 1.1   23 Jan 2002 12:44:20   baa
 * security updates
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:12:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.setaccess;

// java imports
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.RoleEvent;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.RoleTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunction;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

// ------------------------------------------------------------------------------
/**
 * This road is used to save the access values set for the role function, into a
 * database.
 *
 */
// ------------------------------------------------------------------------------
public class SetAccessUpdatesAisle extends PosLaneActionAdapter {


	/**
	 * class name constant
	 */
	public static final String LANENAME = "SetAccessUpdatesAisle";

	/**
	 * the total number of characters in a row
	 */
	public static final int TOTAL_ROW_CHARACTERS = 40;

	/**
	 * the number of characters for the function title and access values for
	 * 'add role'
	 */
	public static final int ADD_ROLE_ROW_CHARACTERS = 36;

	/**
	 * the number of characters for the function title for modify role
	 */
	public static final int MODIFY_ROLE_TITLES_ROW = 39;

	/**
	 * the number of characters for the function access values for modify role
	 */
	public static final int MODIFY_ROLE_ACCESS_ROW = 35;

	Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

    //--------------------------------------------------------------------------
    /**
       Save the edited role to the database.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // letter to be send
        String letter = CommonLetterIfc.SUCCESS;
        // change Cargo type to SetAccessCargo
        SetAccessCargo cargo = (SetAccessCargo)bus.getCargo();

        // get the needed locale
        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

		// for Auditlogging
		RoleEvent ev = null;
		AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();

		// retrieve the modified role which is to be saved in the database
		RoleIfc role = cargo.getRoleSelected();
		role.setApplicationId(RoleIfc.POINT_OF_SALE);
		
		//RoleFunctionIfc[] function = role.getFunctions();
		RoleFunctionIfc[] function = cargo.getFilteredRoleFunctionsForRole();

		// must retrieve the String function access values from cargo
		String[] oldFunctionAccess = cargo.getOldFunctionAccess();
		String[] newFunctionAccess = cargo.getNewFunctionAccess();

        // retrieve all old and new function access String values
        // and store in alphabetic order by function title, in a map
        Collator collate = Collator.getInstance(journalLocale);
        TreeMap<String, String> mapOldFunction = new TreeMap<String, String>(collate);
        TreeMap<String, String> mapNewFunction  = new TreeMap<String, String>(collate);

        for (int i = 0; i < function.length; i++)
        {
            mapOldFunction.put(function[i].getLocalizedTitle(journalLocale), oldFunctionAccess[i]);
            mapNewFunction.put(function[i].getLocalizedTitle(journalLocale), newFunctionAccess[i]);
        }

        // retrieve the role name values
        String oldRoleName = cargo.getOldRoleTitle();
        String newRoleName = role.getTitle(journalLocale);

		// get the ID of the employee who made the change
		String employeeID = cargo.getEmployeeID();

		// get register from cargo
		RegisterIfc register = cargo.getRegister();
		boolean trainingModeOn = false;
		if (register != null) {
			trainingModeOn = register.getWorkstation().isTrainingMode();
		}

		// save the modified role in the database and
		// create the journal entry
		try {
			boolean newRole = cargo.getNewRole();

			RoleTransaction rt = null;

			rt = (RoleTransaction) DataTransactionFactory
					.create(DataTransactionKeys.ROLE_TRANSACTION);

			if (newRole == true) {

                TreeMap<Integer, RoleFunctionIfc> roleSetting  = role.getFunctionMap();
                Collection<RoleFunctionIfc> c = roleSetting.values();
                Iterator<RoleFunctionIfc> itr = c.iterator();
                ArrayList<String> roleFunctions = new ArrayList<String>();
                ArrayList<String> logRoleFunctions = new ArrayList<String>();
                while(itr.hasNext())
                {
                	RoleFunction roleFunction =  (RoleFunction)itr.next();
                	if(Boolean.TRUE.equals(roleFunction.getAccess()))
                	{
                        StringBuffer logRoleTitle = new StringBuffer();
                        roleFunctions.add(roleFunction.getLocalizedTitle(journalLocale));
                        // prefix application abbreviation before feature
                        logRoleTitle.append(UserAccessCargoIfc.STATIONARY_POS_APPLICATION_NAME)
                                 .append(" ")
                                 .append(roleFunction.getLocalizedTitle(journalLocale));
                        logRoleFunctions.add(logRoleTitle.toString());
                	}
                }

				// for Auditlogging
				ev = (RoleEvent) AuditLoggingUtils.createLogEvent(
						RoleEvent.class, AuditLogEventEnum.ADD_ROLE);
				ev.setRoleSetting(logRoleFunctions);

				if (!trainingModeOn) {
					rt.insertRole(role);
				}
				journalAddRole(bus, employeeID, newRoleName, mapNewFunction);
			} else {
				// for Auditlogging
				ev = (RoleEvent) AuditLoggingUtils.createLogEvent(
						RoleEvent.class, AuditLogEventEnum.EDIT_ROLE);

				if (!trainingModeOn) {
					rt.updateRole(role);
				}

				journalModifyRole(employeeID, oldRoleName, newRoleName,
						mapOldFunction, mapNewFunction);
			}

			// for Auditlogging
			ev.setRoleName(newRoleName);
			ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
			RegisterIfc ri = cargo.getRegister();
			if (ri != null) {
				WorkstationIfc wi = ri.getWorkstation();
				if (wi != null) {
					ev.setRegisterNumber(wi.getWorkstationID());
				}
			}

			ev.setUserId(cargo.getOperator().getLoginID());
            ev.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
			if (!CheckTrainingReentryMode
					.isTrainingRetryOn(cargo.getRegister())) {
				auditService.logStatusSuccess(ev);
			}

			boolean registerUpdated = updateRegister(register, role);

			if (registerUpdated) {
				letter = CommonLetterIfc.UPDATE_HARD_TOTALS;
			} else {
				letter = CommonLetterIfc.SUCCESS;
			}
		} catch (DataException e) {
			// for Auditlogging
			if (!CheckTrainingReentryMode
					.isTrainingRetryOn(cargo.getRegister())) {
				ev.setRoleName(newRoleName);
				ev.setEventOriginator(EventOriginatorInfoBean
						.getEventOriginator());
				RegisterIfc ri = cargo.getRegister();
				if (ri != null) {
					WorkstationIfc wi = ri.getWorkstation();
					if (wi != null) {
						ev.setRegisterNumber(wi.getWorkstationID());
					}
				}
				ev.setUserId(cargo.getOperator().getLoginID());
				auditService.logStatusFailure(ev);
			}

			cargo.setDataExceptionErrorCode(e.getErrorCode());
			letter = CommonLetterIfc.DB_ERROR;
		}

		bus.mail(new Letter(letter), BusIfc.CURRENT);
	}

	// --------------------------------------------------------------------------
	/**
	 * Change all cashiers that use this role.
	 *
	 * @param register
	 *            the register totals object; it contains a list of all logged
	 *            on cashiers.
	 * @param role
	 *            the role update by this service
	 */
	// --------------------------------------------------------------------------
	protected boolean updateRegister(RegisterIfc register, RoleIfc role) {
		EmployeeIfc[] cashierArray = register.getCashiers();
		boolean updated = false;

		for (int i = 0; i < cashierArray.length; i++) {
			if (role.getRoleID() == cashierArray[i].getRole().getRoleID()) {
				cashierArray[i].setRole(role);
				updated = true;
			}
		}

		return updated;
	}

	/**
	 * Create the journal Add Role entry
	 *
	 * @param roleTitle
	 *            is the role for which the journal entry is created
	 * @param row
	 *            is the array of alphabetic function titles
	 * @param functionTitle
	 *            is the array of Titles
	 * @param functionAccess
	 *            is the array of Access values
	 */
	protected void journalAddRole(BusIfc bus, String employeeID, String roleName,
			TreeMap map)

	{
		JournalManagerIfc journalIfc = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

		StringBuffer journalText = new StringBuffer();
		StringBuffer rowText = null;

		// begin the journal text for a new role being added
		Object[] dataArgs = new Object[2];
		journalText.append(Util.EOL);
		dataArgs[0] = employeeID;
		journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.EMPLOYEE_ID_LABEL, dataArgs));

		journalText.append(Util.EOL);
		journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.ADD_ROLE_LABEL, null));

		journalText.append(Util.EOL);
		dataArgs[0] = roleName;
		journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.ROLE_NAME_LABEL, dataArgs));

		// add the newline, function Title and white spaces
		// for each row
		Iterator accessMap = map.keySet().iterator();
		while (accessMap.hasNext()) {
			String key = (String) accessMap.next();
			String access = (String) (map.get(key));
			// rowText = new StringBuffer(TOTAL_ROW_CHARACTERS);
			rowText = new StringBuffer();

			// add the function Title
			rowText.append("\n  ").append(key);

			// add the white spaces
			rowText.append(" ");

			// add the function Access value
			rowText.append(access);

			// append each line of role function values
			// to the journal
			journalText.append(rowText);
		}

		// create the actual 'AddRole' journal entry
		journalIfc.journal("", "", journalText.toString());
	}

	// --------------------------------------------------------------------------
	/**
	 * Create the journal Modify Role entry
	 *
	 * @param oldName
	 *            old role name value
	 * @param newName
	 *            new role name value
	 * @param oldAccess
	 *            is the array of old Access values
	 * @param newAccess
	 *            is the array of new Access values
	 */
	// --------------------------------------------------------------------------
	protected void journalModifyRole(String employeeID, String oldName,
			String newName, TreeMap mapOld, TreeMap mapNew) {
		JournalManagerIfc journalIfc = (JournalManagerIfc) Gateway
				.getDispatcher().getManager(JournalManagerIfc.TYPE);

		StringBuffer journalText = new StringBuffer();
		StringBuffer rowText = null;

		int whiteSpaceOld = 0;
		int whiteSpaceNew = 0;

		// this journal entry consists of an oldValue String array
		// where the first old Value is the old role title and
		// the next old values are modified access values
		String strOldValue = "Old Value";
		int strLength = strOldValue.length();
		String strNewValue = "New Value";
		String newValue = "";

		// create the journal text string for "MODIFY ROLE"
		Object[] dataArgs = new Object[2];
		journalText.append(Util.EOL);
		dataArgs[0] = employeeID;
		journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.EMPLOYEE_ID_LABEL, dataArgs));

		journalText.append(Util.EOL);
		journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.MODIFY_ROLE_LABEL, null));

		journalText.append(Util.EOL);
		journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.ROLE_NAME_LABEL, new Object[]{""}));

		// rowText = new StringBuffer(TOTAL_ROW_CHARACTERS);
		rowText = new StringBuffer();

		rowText.append(Util.EOL);
		dataArgs[0] = oldName;
		rowText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.OLD_VALUE_LABEL, dataArgs));

		// add the white spaces for the RoleName old value
		/*
		 * whiteSpaceOld = (MODIFY_ROLE_TITLES_ROW - strLength -
		 * oldName.length()); for (int n = 0; n <= whiteSpaceOld; n++) {
		 * rowText.append(" "); }
		 */
		// rowText.append(oldName);
		journalText.append(rowText);

		// rowText = new StringBuffer(TOTAL_ROW_CHARACTERS);
		rowText = new StringBuffer();
		rowText.append(Util.EOL);
		dataArgs[0] = newName;
		rowText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.NEW_VALUE_LABEL, dataArgs));

		// add the white spaces for the RoleName new value
		/*
		 * whiteSpaceNew = (MODIFY_ROLE_TITLES_ROW - strLength -
		 * newName.length()); for (int n = 0; n <= whiteSpaceNew; n++) {
		 * rowText.append(" "); }
		 */
		// rowText.append(newName);
		journalText.append(rowText);

		// *** Add each row of text for 'Modify Role' values ***//
		// add the newline, function Access and white spaces
		// for each row

		Iterator accessMap = mapOld.keySet().iterator();
		while (accessMap.hasNext()) {
			String key = (String) accessMap.next();
			String access = (String) mapOld.get(key);

			newValue = (String) mapNew.get(key);

			if (access.equals((String) mapNew.get(key)) == false) {
				// rowText = new StringBuffer(TOTAL_ROW_CHARACTERS);
				rowText = new StringBuffer();

				// add the function Title
				rowText.append("\n").append(key);
				// add the 'Old Value' text
				rowText.append(Util.EOL);
				dataArgs[0] = access;
				rowText.append(I18NHelper.getString(
						I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.OLD_VALUE_LABEL, dataArgs));
				// add the white spaces
				/*
				 * whiteSpaceOld = (MODIFY_ROLE_ACCESS_ROW - strLength -
				 * access.length()); for (int n = 0; n <= whiteSpaceOld; n++) {
				 * rowText.append(" "); }
				 */
				// add the old Access value
				// rowText.append(access);
				// add the 'New Value' text
				rowText.append(Util.EOL);
				dataArgs[0] = newValue;
				rowText.append(I18NHelper.getString(
						I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.NEW_VALUE_LABEL, dataArgs));

				// add the white spaces
				/*
				 * whiteSpaceNew = (MODIFY_ROLE_ACCESS_ROW - strLength -
				 * newValue.length()); for (int n = 0; n <= whiteSpaceNew; n++) {
				 * rowText.append(" "); }
				 */
				// add the new Access value
				// rowText.append(newValue);
				// append each line of role function values
				// to the journal
				journalText.append(rowText);
			}
		}

		journalText.append(Util.EOL);
		// create the actual 'ModifyRole' journal entry
		journalIfc.journal("", "", journalText.toString());
	}

	// ---------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * <P>
	 *
	 * @return String representation of object
	 */
	// ---------------------------------------------------------------------
	public String toString() {
		// result string
		String strResult = new String("Class: " + LANENAME + " (Revision "
				+ getRevisionNumber() + ")" + hashCode());

		// pass back result
		return (strResult);
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves the Team Connection revision number.
	 * <P>
	 *
	 * @return String representation of revision number
	 */
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		// return string
		return (revisionNumber);
	}
}
