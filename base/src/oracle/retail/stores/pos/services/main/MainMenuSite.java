/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/MainMenuSite.java /main/25 2014/06/06 15:03:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  06/06/14 - move training mode from main screen to admin screen
 *    abondala  05/30/14 - notifications UI related changes
 *    rabhawsa  06/04/13 - clear any existing operator.
 *    vbongu    11/30/12 - dashboard changes
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    icole     08/12/11 - Ensure CPOI is cleared at startup and after a
 *                         timeout. HPQC 550
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   02/12/09 - Added LookAndFeel support by Locale
 *    ddbaker   12/30/08 - reset the locale (after logging out) to the default
 *                         locale before showing main options screen.
 *    mchellap  11/21/08 - Merge
 *    mchellap  11/21/08 - Renamed TransactionStatus to TransactionStatusBean
 *    mchellap  11/20/08 - Changes for code review comments
 *    mchellap  11/20/08 - Updating transaction status in arrive method
 *    mkochumm  11/11/08 - forward port bugdb 7314992
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         1/10/2008 7:34:19 AM   Manas Sahu      Event
 *       originator changes
 *  6    360Commerce 1.5         1/10/2008 4:35:58 AM   Naveen Ganesh   Removed
 *        unnecessary imports
 *
 *  5    360Commerce 1.4         11/22/2007 10:59:00 PM Naveen Ganesh   PSI
 *       Code checkin
 *  4    360Commerce 1.3         5/17/2007 5:31:04 PM   Michael P. Barnett
 *       Added a timer to the main menu site.  Also implemented depart() to
 *       disable the UI controls if IDDI timeout occured.
 *  3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:24 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:31 PM  Robert Pearse
 * $
 * Revision 1.8.2.2  2004/10/20 13:14:29  kll
 * @scr 7377: impart knowledge of the register object to the bean model
 *
 * Revision 1.8.2.1  2004/10/18 19:34:37  jdeleau
 * @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 * Revision 1.8  2004/07/16 18:03:36  bvanschyndel
 * @scr 5996 intialized customer name so customer name is updated and doesn't retain previous value
 *
 * Revision 1.7  2004/07/14 00:54:27  rzurga
 * @scr 5955 Pressing escape from sell item does not display the 360 logo on the CPOI
 *
 * Now the main menu site sends a logo screen to CPOI.
 *
 * Revision 1.6  2004/04/05 22:17:05  dcobb
 * @scr 3946 - KeyboardLight error appears on Windows log
 *
 * Revision 1.5  2004/03/16 18:30:42  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.4  2004/02/25 23:14:06  bjosserand
 * @scr 0 Main Refactoring
 * Revision 1.3 2004/02/12 16:48:05 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:24:06 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.2 Jan 13 2004 13:22:00 bjosserand code review
 *
 * Rev 1.1 Dec 26 2003 10:30:04 bjosserand Remove debug output.
 *
 * Rev 1.0 Dec 16 2003 17:00:12 bjosserand Initial revision.
 *
 * Rev 1.0 Aug 29 2003 16:01:04 CSchellenger Initial revision.
 *
 * Rev 1.3 29 Jul 2003 03:26:22 baa training mode background
 *
 * Rev 1.2 May 23 2003 07:11:16 jgs Modified to initialized the JournalManager's copy of the transaction id, if it did
 * not have one. Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 *
 * Rev 1.1 Apr 04 2003 17:39:18 sfl Plugged in the local store tax rule data read for storing them in the memory.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 * Rev 1.0 Apr 29 2002 15:19:36 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:36:02 msg Initial revision.
 *
 * Rev 1.1 26 Feb 2002 13:54:08 epd Cashier and Sales associate names now blanked on this screen Resolution for POS
 * SCR-957: User name stays displayed in status on Main if Admin logged on first
 *
 * Rev 1.0 Sep 21 2001 11:22:04 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import java.util.HashMap;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationAppSite;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.UITDOIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BrowserBeanModel;
import oracle.retail.stores.pos.ui.beans.MainMenuBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;
import oracle.retail.stores.pos.ui.timer.TimeoutSettingsUtility;

import org.apache.log4j.Logger;

/**
 * This site displays the application main menu.
 * 
 * @version $Revision: /main/25 $
 */
public class MainMenuSite extends PosSiteActionAdapter
{

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/25 $";

    /**
     * serial version UID to prevent compile warnings
     */
    public static final long serialVersionUID = -1L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(MainMenuSite.class);

    /**
     * Number of seconds the Data Manager waits before attempting the next
     * transaction.
     */
    public static final int TRANSACTION_INTERVAL = 5;

    /**
     * Parameter for to check if InventoryInquiry is supported or not.
     **/
    public static final String PSI_ENABLED_PROPERTY = "PSIEnabled";

    /** Check if JavaFX is installed */
    protected static Boolean javaFXInstalled;

    /**
     * Displays the main menu.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // When the application comes to main menu, there won't be any open
        // transaction
        TimeoutSettingsUtility.setTransactionActive(false);

        MainMenuBeanModel pModel;

        // if we're on a 4690, turn off the keyboard wait light
        String os = System.getProperty("os.name");
        if ((os != null) && (os.indexOf("4690") > -1))
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
            try
            {
                // Start the keyboard WAIT light extinguisher.
                // It will start the thread that will turn off the
                // light at regular intervals as defined in posdevices.xml
                pda.turnWaitLightOff();
            }
            catch (DeviceException de)
            {
                logger.error("Keyboard Wait Light Clear Error");
            }
        }
        MainCargo mainCargo = (MainCargo)bus.getCargo();
        WorkstationIfc workstation = mainCargo.getRegister().getWorkstation();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
        UserAccessCargoIfc accessCargo = (UserAccessCargoIfc) bus.getCargo();
        paymentManager.clearSwipeAheadData(workstation);
        paymentManager.showLogo(workstation);
        // Get the ui manager from the bus.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // At the Main Options screen, no employees are logged in - so use
        // the applications default locale.
        UIUtilities.setUILocaleForEmployee(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE), ui);
        
        accessCargo.setOperator(null);
        try
        {
            UITDOIfc tdo = (UITDOIfc)TDOFactory.create("tdo.main.mainmenu");
            HashMap<String, Object> attributes = new HashMap<String, Object>(1);
            attributes.put("BUS", bus);
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            boolean enableDashboard = pm.getBooleanValue(ParameterConstantsIfc.BASE_DashboardReportEnabled);
            pModel = (MainMenuBeanModel)tdo.buildBeanModel(attributes);

            pModel.getStatusBeanModel().setCustomerName("");
            // Check that a licensed 3rd party browser is available for this
            // installation
            BrowserBeanModel bbModel = new BrowserBeanModel();
            if (!bbModel.isInstalled())
            {
                pModel.getLocalButtonBeanModel().setButtonEnabled(CommonActionsIfc.ONLINE_OFFICE, false);
            }
            MainCargo cargo = (MainCargo)bus.getCargo();
            pModel.getStatusBeanModel().setRegister(cargo.getRegister());
            pModel.setTimerModel(new DefaultTimerModel(bus, false, ParameterConstantsIfc.BASE_IDDITimeoutInterval));

            // check for Inventory Inquiry access
            if (!inventoryInquirySupported(bus))
            {
                pModel.getLocalButtonBeanModel().setButtonEnabled(CommonActionsIfc.INVENTORY, false);
            }
            else
            {
                pModel.getLocalButtonBeanModel().setButtonEnabled(CommonActionsIfc.INVENTORY, true);
            }
            
            boolean enableNotifications = pm.getBooleanValue(ParameterConstantsIfc.NOTIFICATIONS_RegisterRetrieveNotifications);
            pModel.getLocalButtonBeanModel().setButtonEnabled(CommonActionsIfc.MESSAGES, enableNotifications);
            
            EventOriginatorInfoBean.setEventOriginator("MainMenuSite.arrive");

            if (enableDashboard && BrowserFoundationAppSite.isJavaFXInstalled())
            {
                // show screen with dashboard
                ui.showScreen(POSUIManagerIfc.MAIN_OPTIONS_DASHBOARD, pModel);
            }
            else
            {
                // show screen with logo
                ui.showScreen(POSUIManagerIfc.MAIN_OPTIONS, pModel);
            }

        }
        catch (Exception e)
        {
            logger.error("Unable to create tdo.main.mainmenu.", e);
            e.printStackTrace();
            System.out.println(e);
        }

        // clear line display device of leftover tender information
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        try
        {
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display.", e);
        }

        // set flag indicating this is POS ???
    }

    /**
     * If departing the site because of IDDI timeout, then disable UI controls
     * before importing available datasets.
     * 
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        String currentLetter = bus.getCurrentLetter().getName();

        if (currentLetter.equals(CommonLetterIfc.UNDO))
        {

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // freeze UI
            NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
            localModel.setButtonEnabled(CommonActionsIfc.DAILY_OPS, false);
            localModel.setButtonEnabled(CommonActionsIfc.POS, false);
            localModel.setButtonEnabled(CommonActionsIfc.ADMINISTRATION, false);
            localModel.setButtonEnabled(CommonActionsIfc.CLOCK, false);
            localModel.setButtonEnabled(CommonActionsIfc.SERVICE_ALERT, false);
            localModel.setButtonEnabled(CommonActionsIfc.ONLINE_OFFICE, false);
            localModel.setButtonEnabled(CommonActionsIfc.TRANS_REENTRY, false);
            localModel.setButtonEnabled(CommonActionsIfc.INVENTORY, false);

            NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
            globalModel.setButtonEnabled(CommonActionsIfc.HELP, false);

            POSBaseBeanModel pModel = new POSBaseBeanModel();

            pModel.setGlobalButtonBeanModel(globalModel);
            pModel.setLocalButtonBeanModel(localModel);

            ui.setModel(POSUIManagerIfc.MAIN_OPTIONS, pModel);
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
    
    public boolean inventoryInquirySupported(BusIfc bus)
    {
        boolean supported = false;

        try
        {
            MainCargo cargo = (MainCargo)bus.getCargo();

            // 1. Check whether Inventory Inquiry is Enabled or not
            Boolean enabledFlag = new Boolean(Gateway.getProperty("application", PSI_ENABLED_PROPERTY, "false"));

            // 2. Check whether the Reentry option is on or off
            boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();

            if (enabledFlag.booleanValue() && !isReentryMode)
            {
                supported = true;
            }
        }
        catch (Exception e)
        {
            logger.warn("Error while getting Inventory Inquiry Supported Flags");
            supported = false;
        }

        return supported;
    }    
}