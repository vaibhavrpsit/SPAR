/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/SaveParamSite.java /main/18 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale when displaying currency
 *    abondala  01/03/10 - update header date
 *    vchengeg  02/26/09 - I18Ned the parameter value also for EJournal.
 *    vchengeg  12/16/08 - ej defect fixes
 *    vchengeg  12/04/08 - Fix for EJ Internationalization bug : 7588280
 *    vchengeg  12/04/08 - Fix for EJ Internationalization bug : 7588280
 *
 * ===========================================================================
 * $Log:
 9    360Commerce 1.8         6/7/2008 5:30:58 AM    Anil Kandru     Till ID
 logged as per the description.
 8    360Commerce 1.7         3/11/2008 12:10:40 AM  Chengegowda Venkatesh For
 Audit logging
 7    360Commerce 1.6         3/3/2008 2:30:38 AM    Manas Sahu      For CR #
 30277.
 6    360Commerce 1.5         1/10/2008 7:38:42 AM   Manas Sahu      Event
 originator changes
 5    360Commerce 1.4         1/7/2008 8:25:52 AM    Chengegowda Venkatesh
 PABP FR40 : Changes for AuditLog incorporation
 4    360Commerce 1.3         7/20/2007 9:58:32 AM   Anda D. Cadar   I18N
 changes for currency type parameters
 3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *
 Revision 1.4  2004/09/23 00:07:14  kmcbride
 @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 Revision 1.3  2004/02/12 16:48:50  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:35:34  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:52:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:39:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:05:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:19:52   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Feb 2002 10:41:58   KAC
 * Now journals the parameter change.
 * Resolution for POS SCR-1316: Changing parameter values does not journal
 *
 *    Rev 1.1   10 Dec 2001 13:21:18   KAC
 * Revised to work at register level instead of corporate/store.
 * Remove locationLevel and store dependencies.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 *
 *    Rev 1.0   Sep 21 2001 11:11:44   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import java.io.Serializable;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.ParameterEvent;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ReasonCodeValue;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CurrencyParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.ParametersCommon;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.RetailParameter;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Save the modified parameter.
 * 
 * @version $Revision: /main/18 $
 **/
public class SaveParamSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID. 
    static final long serialVersionUID = -4647163535599616671L;

    public static final String SITENAME = "SaveParamSite";

    protected static final String EOL = System.getProperty("line.separator");

    /**
     * status constant descriptors
     **/
    public static String[] STATUS_DESCRIPTORS = { "Closed", "Open", "Reconciled", "Suspended" };

    /**
     * Try to save the parameter values. Send a letter indicating whether the
     * save was successful.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        String group = cargo.getParameterGroup();
        Vector parameters = cargo.getParameters();
		String alternative = cargo.getAlternative();

		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);

		// for auditloggging
		ParameterEvent ev = (ParameterEvent) AuditLoggingUtils.createLogEvent(
				ParameterEvent.class, AuditLogEventEnum.MODIFY_APP_PARAMETER);
		AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();

		populateAuditEvent(cargo, ev);

		// If save succeeds, mail a letter saying so
		try {
			pm.saveParameters(ParameterCargo.REGISTER, alternative, group,
					parameters);
			journalParameterChange(bus, cargo);

			// for auditloggging
			if (!CheckTrainingReentryMode
					.isTrainingRetryOn(cargo.getRegister())) {
				auditService.logStatusSuccess(ev);
			}

			bus.mail(new Letter(ParametersCommon.SAVE_SUCCEEDED),
					BusIfc.CURRENT);
		}
		catch (Exception e)
		{
	        // If something went wrong in the save, mail a failure letter
			// for auditloggging
			if (!CheckTrainingReentryMode
					.isTrainingRetryOn(cargo.getRegister())) {
				auditService.logStatusFailure(ev);
			}
			logger.error(e);
			bus.mail(new Letter(ParametersCommon.SAVE_FAILED), BusIfc.CURRENT);
        }
    }

    /**
     * Load the event object used for Audit logging with all the information
     * that has to be logged.
     * 
     * @param cargo the cargo entering this site
     * @param ev the event to be loaded
     */
    private void populateAuditEvent(ParameterCargo cargo, ParameterEvent ev)
    {
		ev.setParameterGroup(cargo.getParameterGroup());

		EmployeeIfc operator = cargo.getOperator();
		if (operator != null) {
			ev.setStoreId(operator.getStoreID());
			ev.setUserId(operator.getLoginID());
		}

		RegisterIfc ri = cargo.getRegister();
		if (ri != null) {
			WorkstationIfc wi = ri.getWorkstation();
			if (wi != null) {
				ev.setRegisterId(wi.getWorkstationID());
			}

			TillIfc currentTill = ri.getCurrentTill();
			if (currentTill == null) {
				TillIfc[] tills = ri.getTills();
				if (tills != null) {
					int i = 0;
					for (; i < tills.length; i++) {
						if (tills[i].isSuspended()) {
							ev.setTillId(STATUS_DESCRIPTORS[tills[i]
									.getStatus()]);
							break;
						}
					}
					if (i == tills.length)
						ev.setTillId(STATUS_DESCRIPTORS[0]);
				}
			} else
				ev.setTillId(currentTill.getTillID());

		}

		if (EventOriginatorInfoBean.getEventOriginator() != null) {
			ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
		}
		RetailParameter rp = cargo.getParameter();
		if (rp != null) {
			ev.setParameterName(rp.getParameterName());
		}
	}

    /**
     * Write the new and old values of the parameter to the journal.
     * 
     * @param cargo the ParameterCargo containing the parameter information
     */
    protected void journalParameterChange(BusIfc bus, ParameterCargo cargo)
    {
        // Make the tender summary journal entry
		JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
		RetailParameter parameter = cargo.getParameter();
		String paramName = null;
		CurrencyServiceIfc currencyService = CurrencyServiceLocator
				.getCurrencyService();
		Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
		// Get the parameter name
		if (parameter != null) {
			paramName = parameter.getParameterName();
		}
		// For historical reasons, some parameters are
		// ReasonCodeGroupBeanModels instead of RetailParameters.
		else {
			ReasonCodeGroupBeanModel reasonCodeGroupBeanModel = cargo
					.getReasonCodeGroupBeanModel();
			if (reasonCodeGroupBeanModel != null) {
				paramName = reasonCodeGroupBeanModel.getGroupName();
			}
		}

		StringBuilder entry = new StringBuilder();
		Object[] dataArgs = new Object[2];
		dataArgs[0] = UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,paramName,paramName);
		entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.PARAMETER_EDITED_LABEL, dataArgs));

		entry.append(Util.EOL);

		// Get the previous value(s)

		Serializable[] oldValues = cargo.getOldValues();

		// If there are values, print them
		if (oldValues != null) {
			entry.append(I18NHelper.getString(
					I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.PREVIOUS_VALUE_LABEL, null));
			// Print one value per line
			for (int i = 0; i < oldValues.length; i++) {
				// I18N change - use proper formatting for currencies
				String oldVal = oldValues[i].toString();
				if (parameter != null
						&& parameter instanceof CurrencyParameterBeanModel) {
					oldVal = currencyService.formatCurrency(oldVal, defaultLocale);
				}
				entry.append(UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,oldVal,oldVal));
				entry.append(Util.EOL);
			}
		} // if values exist
		entry.append(Util.EOL);

		// Get the new value(s)

		Serializable[] newValues = cargo.getNewValuesForParameter(paramName);

		// If there are values, print them
		if (newValues != null) {
			entry.append(I18NHelper.getString(
					I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.NEW_VALUE_LABEL, new Object[]{""}));
			// Print one value per line
			for (int i = 0; i < newValues.length; i++) {
				if (newValues[i] instanceof ReasonCodeValue) {
					entry.append(((ReasonCodeValue) newValues[i]).getValue());
				} else {
					//                  I18N change - use proper formatting for currencies
					String newVal = newValues[i].toString();
					if (parameter != null
							&& parameter instanceof CurrencyParameterBeanModel) {
						newVal = currencyService.formatCurrency(newVal, defaultLocale);
					}
					entry.append(UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,newVal,newVal));

				}
				entry.append(Util.EOL);
			}
		} // if values exist
		entry.append(Util.EOL);

		journal.journal(entry.toString());
	}
}