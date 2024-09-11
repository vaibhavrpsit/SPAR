/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/tdo/MainMenuTDO.java /main/20 2014/06/06 15:03:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  06/06/14 - move training mode from main screen to admin screen
 *    mjwallac  01/09/14 - fix null dereferences
 *    vbongu    11/30/12 - dashboard changes
 *    vbongu    11/26/12 - Dashboard changes
 *    npoola    08/10/10 - removed the training register object
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    03/22/10 - add additional check for drawer status before
 *                         setting the training mode to true
 *    abondala  01/03/10 - update header date
 *    blarsen   04/27/09 - Training button should be disabled when in reentry
 *                         mode. Changed logic for training button
 *                         enable/disable to work around bug in
 *                         setButtonEnable() method.
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         5/17/2007 5:31:59 PM   Michael P. Barnett
 *       Instantiate a MainMenuBeanModel for the main menu, which now includes
 *        a timer.
 *  3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:25 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:31 PM  Robert Pearse
 * $
 * Revision 1.20  2004/07/28 19:56:06  rsachdeva
 * @scr 5820 TransactionReentryMode Bundle Filename
 *
 * Revision 1.19  2004/07/23 22:17:25  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.18  2004/07/12 18:09:22  rsachdeva
 * @scr 3976 Online Listener was not being set in the correct tdo (Installer 4690 issues)
 *
 * Revision 1.17  2004/07/06 13:19:22  jriggins
 * @scr 5421 Removed unecessary import which will cause build problems under our Eclipse settings
 *
 * Revision 1.16  2004/07/02 20:00:37  dcobb
 * @scr 5503 Training Mode button should be disabled when store / register / till are not open.
 *
 * Revision 1.15  2004/05/20 22:54:58  cdb
 * @scr 4204 Removed tabs from code base again.
 *
 * Revision 1.14  2004/05/01 14:57:16  tfritz
 * @scr 4414 Disable Re-Entry button when in Training Mode
 *
 * Revision 1.13  2004/04/16 14:39:39  bjosserand
 * @scr 4093 Transaction Reentry
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main.tdo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AssociateProductivityIfc;
import oracle.retail.stores.domain.financial.DepartmentActivityIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TimeIntervalActivityIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataManager;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationAppSite;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.OnlineListener;
import oracle.retail.stores.pos.services.main.MainCargo;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.UITDOIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DashboardReportBeanModel;
import oracle.retail.stores.pos.ui.beans.MainMenuBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

import org.apache.log4j.Logger;

/**
 * @author rwh This class controls creation of the UI bean model for the main
 *         menu.
 */
public class MainMenuTDO extends TDOAdapter implements UITDOIfc
{
    protected final String TRANS_REENTRY_BUTTON_NAME = "TransReentry";
    protected final String MAIN_OPS_BUTTONS_ID = "MainOptionsButtonSpec";
    protected final String TRANS_REENTRY_ON_KEY = "TransReentryOn";
    protected final String TRANS_REENTRY_ON_DEFAULT_TEXT = "Re-entry On";
    protected final String TRANS_REENTRY_OFF_KEY = "TransReentryOff";
    protected final String TRANS_REENTRY_OFF_DEFAULT_TEXT = "Re-entry Off";

    /**
     * Number of seconds the Data Manager waits before attempting the next
     * transaction.
     */
    protected static final int TRANSACTION_INTERVAL = 5;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(MainMenuTDO.class);

    protected boolean enableDashboard = false;
    protected DashboardReportBeanModel drbModel;
    protected ParameterManagerIfc pm;

    /**
     * Build UI bean model for main menu.
     * 
     * @param HashMap
     * @return MainMenuBeanModel
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        BusIfc bus = (BusIfc)attributeMap.get("BUS");

        MainCargo cargo = (MainCargo)bus.getCargo();
        boolean trainingModeOn = cargo.isTrainingMode();
        String storeID = cargo.getStoreStatus().getStore().getStoreID();
        StoreStatusIfc store = cargo.getStoreStatus();
        EYSDate startDate = store.getBusinessDate();
        EYSDate endDate = store.getBusinessDate();

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            enableDashboard = pm.getBooleanValue(ParameterConstantsIfc.BASE_DashboardReportEnabled);
        }
        catch (ParameterException pe)
        {
            logger.error("Error retrieving parameter value: " + pe);
        }
        // Ask the UI Manager to display the main menu
        MainMenuBeanModel pModel = new MainMenuBeanModel();

        StatusBeanModel sModel = new StatusBeanModel();
        drbModel = new DashboardReportBeanModel();
        sModel.setSalesAssociateName("");
        sModel.setCashierName("");
        checkAddOnlineListener(bus);
        RegisterADO registerADO = cargo.getRegisterADO();
        RegisterIfc reg = (RegisterIfc)registerADO.toLegacy();
        if (cargo.isTrainingMode())
        {
            reg.getWorkstation().setTrainingMode(true);
        }
        else
        {
            reg.getWorkstation().setTrainingMode(false);
        }
        boolean reentryMode = reg.getWorkstation().isTransReentryMode();

        NavigationButtonBeanModel localModel = getNavigationButtonBeanModel(utility, trainingModeOn, reentryMode);

        String transReentryText;
        if (reentryMode)
        {
            transReentryText = utility.retrieveText(MAIN_OPS_BUTTONS_ID, BundleConstantsIfc.MAIN_BUNDLE_NAME,
                    TRANS_REENTRY_OFF_KEY, TRANS_REENTRY_OFF_DEFAULT_TEXT);
        }
        else
        {
            transReentryText = utility.retrieveText(MAIN_OPS_BUTTONS_ID, BundleConstantsIfc.MAIN_BUNDLE_NAME,
                    TRANS_REENTRY_ON_KEY, TRANS_REENTRY_ON_DEFAULT_TEXT);
        }

        localModel.setButtonLabel(TRANS_REENTRY_BUTTON_NAME, transReentryText); // "TransReentry"

        // If training mode is turned on, then put Training Mode
        // indication in status panel. Otherwise, return status
        // to online/offline status.
        sModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);
        pModel.setInTraining(trainingModeOn);

        sModel.setRegisterId(reg.getWorkstation().getWorkstationID());
        pModel.setStatusBeanModel(sModel);
        pModel.setLocalButtonBeanModel(localModel);

        if (enableDashboard && BrowserFoundationAppSite.isJavaFXInstalled())
        {
            drbModel = getDashboardReportBeanModel(storeID, startDate, endDate);
        }
        pModel.setDashboardReportBeanModel(drbModel);

        return pModel;
    }

    /**
     * Returns a navigation button bean model for the main menu screen
     * 
     * @param utility UtilityManager to retrieve the training button text from
     * @param trainingModeOn true for training mode
     * @param transReentryMode true for transaction reentry mode
     * @return NavigationButtonBeanModel
     */
    protected NavigationButtonBeanModel getNavigationButtonBeanModel(UtilityManagerIfc utility, boolean trainingModeOn,
            boolean transReentryMode)
    {
        NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();

        if (trainingModeOn || transReentryMode)
        {
            localModel.setButtonEnabled(CommonActionsIfc.DAILY_OPS, false);
            localModel.setButtonEnabled(CommonActionsIfc.CLOCK, false);
            localModel.setButtonEnabled(CommonActionsIfc.SERVICE_ALERT, false);
            localModel.setButtonEnabled(CommonActionsIfc.ONLINE_OFFICE, false);
            localModel.setButtonEnabled(CommonActionsIfc.TRANS_REENTRY, false);
            localModel.setButtonEnabled(CommonActionsIfc.POS, true);
            localModel.setButtonEnabled(CommonActionsIfc.ADMINISTRATION, true);
        }
        else
        {
            localModel.setButtonEnabled(CommonActionsIfc.DAILY_OPS, true);
            localModel.setButtonEnabled(CommonActionsIfc.CLOCK, true);
            localModel.setButtonEnabled(CommonActionsIfc.SERVICE_ALERT, true);
            localModel.setButtonEnabled(CommonActionsIfc.ONLINE_OFFICE, true);
            localModel.setButtonEnabled(CommonActionsIfc.TRANS_REENTRY, true);
            localModel.setButtonEnabled(CommonActionsIfc.POS, true);
            localModel.setButtonEnabled(CommonActionsIfc.ADMINISTRATION, true);
        }

        return localModel;
    }

    /**
     * Checks if Online Listener has to be added
     * 
     * @param bus Bus reference
     */
    protected void checkAddOnlineListener(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MainCargo cargo = (MainCargo)bus.getCargo();
        if (!cargo.getOnlineListenerHasBeenSet())
        {
            DataManagerIfc dm = (DataManagerIfc)bus.getManager(DataManagerIfc.TYPE);
            if (dm != null)
            {
                OnlineListener ol = new OnlineListener(ui);
                dm.addOnlineListener(ol);
                if (dm instanceof DataManager)
                {
                    ((DataManager)dm).setQueueMonitorInterval(TRANSACTION_INTERVAL);
                }
                cargo.setOnlineListenerHasBeenSet(true);
            }
        }
    }

    /**
     * Sets the report data on the DashboardReportBean model
     * 
     * @param storeId ID of the store
     * @param startDate Start date of the store
     * @param endDate End date of the store
     * @return DashboardReportBeanModel
     */
    protected DashboardReportBeanModel getDashboardReportBeanModel(String storeID, EYSDate startDate, EYSDate endDate)
    {

        FinancialTotalsDataTransaction ftdt = null;

        ftdt = (FinancialTotalsDataTransaction)DataTransactionFactory
                .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

        setDashboardReportsList();
        setDashboardMessagesList();
        setAssociateProductivityReportInfo(ftdt, storeID, startDate, endDate);
        setDepartmentSalesReportInfo(ftdt, storeID, startDate, endDate);
        setHourlyProductivityReportInfo(ftdt, storeID, startDate, endDate);

        return drbModel;
    }

    /**
     * Sets the dashboard message list on the model
     */
    protected void setDashboardMessagesList()
    {
        try
        {
            // convert serializable[] to string[]
            Serializable[] messages = pm.getParameterValues(ParameterConstantsIfc.BASE_DashboardMessages);
            String[] dashboardMessages = new String[messages.length];
            for (int i = 0; i < messages.length; i++)
            {
                dashboardMessages[i] = (String)messages[i];
            }
            drbModel.setDashboardMessages(dashboardMessages);
        }
        catch (ParameterException e)
        {
            logger.error("parameter not found to display messages on the dashboard");
        }

    }

    /**
     * Sets the report list from parameter
     */
    protected void setDashboardReportsList()
    {
        try
        {
            // convert serializable[] to string[]
            Serializable[] values = pm.getParameterValues(ParameterConstantsIfc.BASE_DashboardReports);
            String[] dashboardReports = new String[values.length];
            for (int i = 0; i < values.length; i++)
            {
                dashboardReports[i] = (String)values[i];
            }
            drbModel.setDashboardReportsList(dashboardReports);
        }
        catch (ParameterException e)
        {
            logger.error("parameter not found for displaying dashboard");
        }

    }

    /**
     * Sets Associate productivity report information
     * 
     * @param drbModel DashboardReportBeanModel
     * @param ftdt FinancialTotalsDataTransaction
     * @param storeId ID of the store
     * @param startDate Start date of the store
     * @param endDate End date of the store
     */
    protected void setAssociateProductivityReportInfo(FinancialTotalsDataTransaction ftdt, String storeID,
            EYSDate startDate, EYSDate endDate)
    {
        AssociateProductivityIfc[] associate = null;
        int associateLength = 0;
        try
        {
            associate = ftdt.readAssociateProductivity(storeID, startDate, endDate);
            associateLength = associate.length;
        }
        catch (DataException e)
        {
            if(e.getErrorCode() == DataException.NO_DATA)
            {
                logger.warn("AssociateProductivityReport will be empty in the dashboard.");
            }
            else
            {
                logger.error("Exception occured when reading the data for AssociateProductivityReport",e);
            }
        }

        String[] associateName = new String[associateLength];
        BigDecimal[] amount = new BigDecimal[associateLength];
        if (associate != null)
        {
            for (int i = associateLength - 1; i >= 0; i--)
            {
                CurrencyIfc netAmount = associate[i].getNetAmount();
                amount[i] = netAmount.getDecimalValue();
                associateName[i] = associate[i].getAssociate().getPersonName().getFirstName(); 
            }
        }
        drbModel.setAprNetAmount(amount);
        drbModel.setAprAssociateName(associateName);

    }

    /**
     * Sets the Department sales report information
     * 
     * @param drbModel DashboardReportBeanModel
     * @param ftdt FinancialTotalsDataTransaction
     * @param storeId ID of the store
     * @param startDate Start date of the store
     * @param endDate End date of the store
     */
    protected void setDepartmentSalesReportInfo(FinancialTotalsDataTransaction ftdt, String storeID, EYSDate startDate,
            EYSDate endDate)
    {
        DepartmentActivityIfc[] departments = null;
        int departmentLength = 0;

        LocaleRequestor locales = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.REPORTS));
        try
        {
            departments = ftdt.readDepartmentTotals(storeID, startDate, endDate, locales);
            departmentLength = departments.length;
        }
        catch (DataException e)
        {
            if(e.getErrorCode() == DataException.NO_DATA)
            {
                logger.warn("Department sales report will be empty in the dashboard.");
            }
            else
            {
                logger.error("Exception occured when reading the data for Department sales report",e);
            }
        }
        String[] departmentname = new String[departmentLength];
        BigDecimal[] amount = new BigDecimal[departmentLength];

        if (departments != null)
        {
            for (int i = departmentLength - 1; i >= 0; i--)
            {
                FinancialTotalsIfc ft = departments[i].getTotals();
                departmentname[i] = departments[i].getDepartment().getDescription(getDefaultLocale());
                CurrencyIfc netAmountSales = DomainGateway.getBaseCurrencyInstance();
                netAmountSales = ft.getAmountNetItemSalesMinusRestockingFees();
                amount[i] = netAmountSales.getDecimalValue();
            }
        }
        drbModel.setDsrDepartmentName(departmentname);
        drbModel.setDsrNetAmount(amount);

    }

    /**
     * Sets the Hourly productivity report information
     * 
     * @param drbModel DashboardReportBeanModel
     * @param ftdt FinancialTotalsDataTransaction
     * @param storeId ID of the store
     * @param startDate Start date of the store
     * @param endDate End date of the store
     */
    private void setHourlyProductivityReportInfo(FinancialTotalsDataTransaction ftdt, String storeID,
            EYSDate startDate, EYSDate endDate)
    {
        TimeIntervalActivityIfc[] intervals = null;
        int intervalsLength = 0;
        try
        {
            intervals = ftdt.readTimeIntervalTotals(storeID, startDate, endDate);
            intervalsLength = intervals.length;
        }
        catch (DataException e)
        {
            if(e.getErrorCode() == DataException.NO_DATA)
            {
                logger.warn("Hourly productivity report will be empty in the dashboard.");
            }
            else
            {
                logger.error("Exception occured when reading the data for Hourly productivity report", e);
            }            
        }

        BigDecimal[] hourlySales = new BigDecimal[intervalsLength];
        EYSDate[] hourlyTime = new EYSDate[intervalsLength];

        if (intervals != null)
        {
            for (int i = 0; i < intervalsLength; i++)
            {
    
                EYSDate startTime = intervals[i].getStartTime();
                hourlyTime[i] = startTime;
                FinancialTotalsIfc totalSale = intervals[i].getTotals();
                CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
                amount = totalSale.getAmountNetTransactionSales();
                hourlySales[i] = amount.getDecimalValue();
            }
        }

        drbModel.setHprHourlySales(hourlySales);
        drbModel.setHprHourlyTime(hourlyTime);
    }

    /**
     * Gets the locale for the user interface subsystem properties object.
     * 
     * @returns locale for the user interface subsystem
     */
    public static Locale getDefaultLocale()
    {
        return (LocaleMap.getLocale(LocaleMap.DEFAULT));
    }

}
