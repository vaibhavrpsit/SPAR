/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/clockentry/EnterReasonCodeSite.java /main/18 2012/10/16 17:37:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/10/12 - Popup menu implementation
 *    npoola    09/02/10 - set the register to the status bean model
 *    jkoppolu  07/16/10 - Fix for Bug#9849659
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   11/05/08 - I18N Reason Codes - It is not necessary to pass the
 *                         bestMatch to the model.inject
 *    mdecama   11/05/08 - I18N Reason Code - Refactored the EmployeeClockEntry
 *                         reason field.
 *    mdecama   11/04/08 - I18N - Fixed the way to retrieve a locale
 *    mdecama   10/20/08 - Refactored Dropdowns to use the new
 *                         CodeListManagerIfc
     $Log:
      4    360Commerce1.3         3/30/2007 4:56:29 AM   Michael Boyd    CR
           26172 - v8x merge to trunk

           4    .v8x      1.2.1.0     3/12/2007 2:25:49 PM   Maisa De Camargo
           Updated Reason Code logic to use the Default Settings.
      3    360Commerce1.2         4/1/2005 2:58:03 AM    Robert Pearse
      2    360Commerce1.1         3/10/2005 9:51:26 PM   Robert Pearse
      1    360Commerce1.0         2/11/2005 11:40:54 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:50:15  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:49:15  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:59:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 24 2003 12:42:14   HDyer
 * Fixed deprecation warning.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   07 May 2002 15:24:18   dfh
 * updates for adding clock entry type -
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 *
 *    Rev 1.0   Apr 29 2002 15:24:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:31:54   msg
 * Initial revision.
 *
 *    Rev 1.1   26 Feb 2002 14:26:24   epd
 * added logic to put cashier name in status panel
 * Resolution for POS SCR-958: Wrong user displays on Clock in/ Out screen.
 *
 *    Rev 1.0   28 Oct 2001 17:55:50   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.employee.clockentry;

import java.util.Locale;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.CodeListManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeClockEntryBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Displays the screen for entering the time entry type code and reason code.
 * 
 * @version $Revision: /main/18 $
 */
public class EnterReasonCodeSite extends PosSiteActionAdapter
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 2305246674983257663L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * Displays the screen for entering the time entry type code and reason
     * code.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ClockEntryCargo cargo = (ClockEntryCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // build ui string to display the entry type code In/Out values
        String[] inout = new String[2];
        inout[0] = cargo.getTypeCodeInString();
        inout[1] = cargo.getTypeCodeOutString();

        // construct bean model and populate
        EmployeeClockEntryBeanModel beanModel = new EmployeeClockEntryBeanModel();
        beanModel.setCurrentEntry();
        beanModel.setLastEntry(cargo.getLastEntry());
        // set entry type codes strings
        beanModel.setTypeCodes(inout);

        EmployeeClockEntryIfc employeeClockEntry = cargo.getClockEntry();

        if (employeeClockEntry != null) // set up last entry
        {
            int typeCode = cargo.getClockEntry().getTypeCode();
            beanModel.setTypeCode(typeCode);
            beanModel.setTypeCodeString(inout[typeCode]);
            beanModel.setEntryTypeLabelString(cargo.getEntryTypeString());
        }
        else
        {
            beanModel.setTypeCode(-1); // no last entry
        }

        // set up reason codes
        CodeListIfc reasonCodes = getReasonCodes(bus);
        cargo.setReasonCodes(reasonCodes);

        String selectedReasonCode = CodeConstantsIfc.CODE_UNDEFINED;
        if (employeeClockEntry != null)
        {
            if (employeeClockEntry.getReason() != null)
                selectedReasonCode = employeeClockEntry.getReason().getCode();
        }

        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        beanModel.inject(reasonCodes, selectedReasonCode, lcl);

        // setting Cashier Name and Sales Associate Name on the Status Region.
        EmployeeIfc operator = cargo.getClockingEmployee();
        if (operator != null)
        {
            StatusBeanModel sModel = new StatusBeanModel();
            sModel.setCashierName(operator.getPersonName().getFirstLastName());
            sModel.setSalesAssociateName("");
            // If training mode is turned on, then put Training Mode
            // indication in status panel. Otherwise, return status
            // to online/offline status.
            boolean trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
            sModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);
            sModel.setRegister(cargo.getRegister());
            beanModel.setStatusBeanModel(sModel);
        }
        else
        {
            logger.error("No Operator in the cargo!");
        }

        // display bean
        ui.showScreen(POSUIManagerIfc.EMPLOYEE_CLOCK_ENTRY, beanModel);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        if (letter instanceof ButtonPressedLetter)
        {
            String letterName = letter.getName();
            ClockEntryCargo cargo = (ClockEntryCargo) bus.getCargo();
            if (letterName != null && letterName.equals(CommonLetterIfc.UNDO))
            {
                // Audit Logging UserEvent for user logout
                AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
                UserEvent ev = (UserEvent) AuditLoggingUtils.createLogEvent(UserEvent.class, AuditLogEventEnum.LOG_OUT);
                RegisterIfc ri = cargo.getRegister();
                if (ri != null)
                {
                    WorkstationIfc wi = ri.getWorkstation();
                    if (wi != null)
                    {
                        ev.setRegisterNumber(wi.getWorkstationID());
                    }
                }
                ev.setStoreId(cargo.getOperator().getStoreID());
                ev.setUserId(cargo.getOperator().getLoginID());
                ev.setEventOriginator("EnterReasonCodeSite.depart");
                auditService.logStatusSuccess(ev);
            }
        }
    }

    /**
     * Retrieves the CodeList for TimeKeeping
     * 
     * @param bus
     * @return
     */
    protected CodeListIfc getReasonCodes(BusIfc bus)
    {
        CodeListIfc reasonCodes = null;
        ClockEntryCargo cargo = (ClockEntryCargo) bus.getCargo();

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        CodeListManagerIfc codeListManager = (CodeListManagerIfc) bus.getManager(CodeListManagerIfc.TYPE);
        CodeListSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeListSearchCriteriaInstance();
        criteria.setStoreID(cargo.getOperator().getStoreID());
        criteria.setListID(CodeConstantsIfc.CODE_LIST_TIMEKEEPING_REASON_CODES);
        criteria.setLocaleRequestor(utility.getRequestLocales());

        reasonCodes = codeListManager.getCodeList(criteria);

        return reasonCodes;
    }

}
