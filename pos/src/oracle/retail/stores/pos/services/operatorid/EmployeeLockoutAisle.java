/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/EmployeeLockoutAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rsnayak   08/05/10 - Employee Lock out reason fix
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     5    360Commerce 1.4         4/22/2008 6:32:27 AM   Anil Kandru     The
 *          lock out reason is modified
 *     4    360Commerce 1.3         4/18/2008 2:35:16 AM   Anil Kandru     The
 *          lockout reason has been changed.
 *     3    360Commerce 1.2         4/1/2008 5:08:57 AM    Anil Kandru     The
 *          lock out reason is logged.
 *     2    360Commerce 1.1         3/7/2008 4:47:52 AM    Anil Kandru
 *          Lock_Out Event logging has been done properly.
 *     1    360Commerce 1.0         10/6/2006 4:02:39 PM   Rohit Sachdeva  
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle is traversed when Lock Out Happens. It prevents the User to go
 * forward.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EmployeeLockoutAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 143645898545L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * resource ids
     */
    private static final String EMPLOYEE_LOCKOUT = "EmployeeLockout";

    /**
     * database offline resource id
     */
    public static final String DATABASE_ERROR_RESOURCE_ID = "DatabaseError";

    /**
     * Displays the error screen to prevent user to go forward.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        DialogBeanModel dialogModel = new DialogBeanModel();
        if (cargo.isLockOut())
        {
            if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
                UserEvent ev = (UserEvent) AuditLoggingUtils.createLogEvent(UserEvent.class, AuditLogEventEnum.LOCKOUT);
                ev.setStoreId(Gateway.getProperty("application", "StoreID", ""));
                RegisterIfc ri = cargo.getRegister();
                if (ri != null)
                {
                    WorkstationIfc wi = ri.getWorkstation();
                    if (wi != null)
                    {
                        ev.setRegisterNumber(wi.getWorkstationID());
                    }
                }
                ev.setUserId(cargo.getEmployeeID());
                if (EventOriginatorInfoBean.getEventOriginator() != null)
                    ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());

                if (cargo.isPasswordExpired())
                {
                    ev.setLockoutReason(AuditLoggerConstants.EXPIRED_PASSWORD);
                }
                else
                {
                    ev.setLockoutReason(cargo.getFailedLoginAttempts()
                            + AuditLoggerConstants.EMP_LOGIN_LOCKOUT_REASON_4 + AuditLoggerConstants.EXPIRED_PASSWORD);
                }
                auditService.logStatusSuccess(ev);
            }
            dialogModel.setResourceID(EMPLOYEE_LOCKOUT);
        }
        else
        {
            String args[] = new String[1];
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            args[0] = utility.getErrorCodeString(cargo.getErrorType());
            dialogModel.setArgs(args);
            dialogModel.setResourceID(DATABASE_ERROR_RESOURCE_ID);
        }
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  EmployeeLockoutAisle (Revision " + getRevisionNumber() + ")"
                + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
