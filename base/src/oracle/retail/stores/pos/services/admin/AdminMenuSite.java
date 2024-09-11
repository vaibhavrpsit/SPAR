/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/AdminMenuSite.java /main/20 2014/06/06 15:03:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  06/06/14 - move training mode from main screen to admin screen
 *    blarsen   01/29/14 - Disabling ReEntry Mode button based on
 *                         application.properties value. (AJB EMV payment
 *                         system does not support this feature.)
 *    mjwallac  05/02/12 - Fortify: fix redundant null checks, part 4
 *    mkutiana  09/28/11 - fixed bundle name for button label
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    mjwallac  08/08/11 - disallow reentry in training mode.
 *    ohorne    03/22/11 - set re-entry mode button text
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    jkoppolu  07/16/10 - Fix for Bug#9849659
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     9    360Commerce 1.8         6/7/2008 6:09:16 AM    Manikandan Chellapan
 *           CR#31924 Enabled audit logging for training and reentry login
 *          logout
 *     8    360Commerce 1.7         5/22/2008 7:02:56 AM   subramanyaprasad gv
 *          CR 31731: Changed the method call auditService.log(ev) to
 *          auditService.logStatusSuccess(ev). Code reviewed by Manikandan
 *          Chellapan.
 *     7    360Commerce 1.6         3/6/2008 5:41:14 AM    Chengegowda
 *          Venkatesh For CR 30275
 *     6    360Commerce 1.5         1/10/2008 7:38:01 AM   Manas Sahu
 *          Event originator changes
 *     5    360Commerce 1.4         1/7/2008 7:59:55 AM    Chengegowda
 *          Venkatesh Audit log changes
 *     5    I18N_P2    1.3.1.0     12/18/2007 3:09:14 PM  Sandy Gu        static
 *           text fix for POS
 *     4    360Commerce 1.3         11/22/2007 10:59:04 PM Naveen Ganesh   PSI
 *          Code checkin
 *     3    360Commerce 1.2         3/31/2005 4:27:11 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:19:33 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:09:25 PM  Robert Pearse
 *    $
 *    Revision 1.5.2.1  2004/10/20 13:14:27  kll
 *    @scr 7377: impart knowledge of the register object to the bean model
 *
 *    Revision 1.5  2004/07/22 21:22:06  kll
 *    @scr 5212: disable Queue capabilities while in Training Mode
 *
 *    Revision 1.4  2004/03/14 21:19:34  tfritz
 *    @scr 3884 - New Training Mode Functionality
 *
 *    Revision 1.3  2004/02/12 16:48:47  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:38:15  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 29 2003 13:41:50   bwf
 * Depending on parameter show correct buttons and screen.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 *
 *    Rev 1.0   Aug 29 2003 15:52:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 11 2003 13:16:54   baa
 * remove usage of  deprecated EployeeIfc methods get/setName
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 *
 *    Rev 1.0   Apr 29 2002 15:36:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:02:52   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:18:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:10:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.timer.TimeoutSettingsUtility;

/**
 * Administration screen with options.
 *
 * @version $Revision: /main/20 $
 */
public class AdminMenuSite extends PosSiteActionAdapter
{

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * site name constant
     **/
    public static final String SITENAME = "AdminMenuSite";


    protected final String TRANS_REENTRY_BUTTON_NAME = "TransReentry";

    protected final String TRANS_REENTRY_ON_KEY = "TransReentryOn";
    protected final String TRANS_REENTRY_ON_DEFAULT_TEXT = "Re-entry On";

    protected final String TRANS_REENTRY_OFF_KEY = "TransReentryOff";
    protected final String TRANS_REENTRY_OFF_DEFAULT_TEXT = "Re-entry Off";

    protected final String ADMIN_BUTTON_SPEC = "AdminOptionsButtonSpec";
    protected final String ADMIN_BUTTON_SPEC_NO_QUEUE = "AdminOptionsNoQueueButtonSpec";

    protected static final String POS_ENABLE_REENTRY = "ReentryModeButtonEnabled";
    
    /**
     * Training Off Label tag
     */
    public static final String TRAINING_OFF_LABEL_TAG = "TrainingOffLabel";
    /**
     * Training Off Label default text
     */
    public static final String TRAINING_OFF_LABEL_TEXT = "Training Off";
    /**
     * Training On Label tag
     */
    public static final String TRAINING_ON_LABEL_TAG = "TrainingOnLabel";
    /**
     * Training On Label default text
     */
    public static final String TRAINING_ON_LABEL_TEXT = "Training On";

    /**
     * Displays admin options menu.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // cause timeout to drill back to main menu screen
        TimeoutSettingsUtility.setTransactionActive(true);
        // get references to ui, model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel pModel = new POSBaseBeanModel();
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

        // get register from cargo
        AdminCargo cargo = (AdminCargo) bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        boolean trainingModeOn = cargo.isTrainingMode();
        if (cargo.isTrainingMode())
        {
            register.getWorkstation().setTrainingMode(true);
        }
        else
        {
            register.getWorkstation().setTrainingMode(false);
        }

        // Only enable the reset button if session is closed
        boolean enableReset = ((register != null) && (!trainingModeOn) && (register.getStatus() == AbstractFinancialEntityIfc.STATUS_CLOSED || register
                .getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED));

        // Send enable to the reset button on the Local Navigation button bean.
        nModel.setButtonEnabled(CommonActionsIfc.RESET, enableReset);

        //set appropriate re-entry button text
        String transReentryText = null;
        if (register != null && register.getWorkstation().isTransReentryMode())
        {
            transReentryText =
                utility.retrieveText(
                    ADMIN_BUTTON_SPEC,
                    BundleConstantsIfc.ADMIN_BUNDLE_NAME,
                    TRANS_REENTRY_OFF_KEY,
                    TRANS_REENTRY_OFF_DEFAULT_TEXT);
        }
        else
        {
            transReentryText =
                utility.retrieveText(
                    ADMIN_BUTTON_SPEC,
                    BundleConstantsIfc.ADMIN_BUNDLE_NAME,
                    TRANS_REENTRY_ON_KEY,
                    TRANS_REENTRY_ON_DEFAULT_TEXT);
        }
        nModel.setButtonLabel(TRANS_REENTRY_BUTTON_NAME, transReentryText);

        // Disable transaction reentry button if in training mode or if feature is disable
        boolean enableReentryModeButton = Gateway.getBooleanProperty(Gateway.APPLICATION_PROPERTIES_GROUP, POS_ENABLE_REENTRY, true);
        nModel.setButtonEnabled(CommonActionsIfc.TRANS_REENTRY, enableReentryModeButton && !trainingModeOn);
        
        // Training Mode is enabled only if the store / register / till are open
        boolean enableTrainingModeButton = false;

        if (register.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
        {
            TillIfc till = register.getCurrentTill();
            if (till != null)
            {
                if (till.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
                {
                    int drawerStatus = register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getDrawerStatus();
                    if (drawerStatus == AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED)
                    {
                        enableTrainingModeButton = true;
                    }
                }
            }
        }
        // disable training mode if in transaction reentry mode
        if (enableTrainingModeButton)
        {
            if (register.getWorkstation().isTransReentryMode())
            {
                enableTrainingModeButton = false;
            }
        }

        String trainingButtonText = null;
        if (trainingModeOn)
        {
            trainingButtonText = utility.retrieveText("Common", BundleConstantsIfc.POS_BUNDLE_NAME,
                    TRAINING_OFF_LABEL_TAG, TRAINING_OFF_LABEL_TEXT);
        }
        else
        {

            trainingButtonText = utility.retrieveText("Common", BundleConstantsIfc.POS_BUNDLE_NAME,
                    TRAINING_ON_LABEL_TAG, TRAINING_ON_LABEL_TEXT);
        }
        nModel.setButtonEnabled(CommonActionsIfc.TRAINING_ON_OFF, trainingButtonText, enableTrainingModeButton);

        // get queue allowed flag
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        boolean queueFlag = false;
        try
        {
            queueFlag = "Y".equalsIgnoreCase(pm.getStringValue(ParameterConstantsIfc.QUEUE_ClearQueue));
        }
        catch (ParameterException pe)
        {
            logger.error("Could not retrieve setting for Queue Parameter");
        }

        // determine whether or not to display queue buttonH
        String screenName = null;
        if (queueFlag && !trainingModeOn)
        {
            screenName = POSUIManagerIfc.ADMIN_OPTIONS;
        }
        else
        {
            screenName = POSUIManagerIfc.ADMIN_OPTIONS_NO_QUEUE;
        }

        pModel.setLocalButtonBeanModel(nModel);
        pModel.setInTraining(trainingModeOn);

        // setting Cashier Name and Sales Associate Name on the Status Region.
        EmployeeIfc operator = cargo.getOperator();
        if (operator != null)
        {
            StatusBeanModel sModel = new StatusBeanModel();
            sModel.setCashierName(operator.getPersonName().getFirstLastName());
            sModel.setSalesAssociateName(operator.getPersonName().getFirstLastName());
            sModel.setRegister(register);
            // If training mode is turned on, then put Training Mode
            // indication in status panel. Otherwise, return status
            // to online/offline status.
            sModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);
            pModel.setStatusBeanModel(sModel);
        }
        else
        {
            logger.error("No Operator in the cargo!");
        }

        // show the UI to choose admin options
        ui.showScreen(screenName, pModel);

    } // end arrive()

    /**
     * Depart
     *
     * @param bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        if (letter instanceof ButtonPressedLetter) // Is ButtonPressedLetter
        {
            String letterName = letter.getName();
            AdminCargo cargo = (AdminCargo) bus.getCargo();
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
                ev.setStatus(AuditLoggerConstants.SUCCESS);
                ev.setEventOriginator("AdminMenuSite.depart");
                auditService.logStatusSuccess(ev);
            }
        }
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