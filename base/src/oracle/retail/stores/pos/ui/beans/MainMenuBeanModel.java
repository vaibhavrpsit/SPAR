/* ===========================================================================
* Copyright (c) 2007, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MainMenuBeanModel.java /main/11 2014/07/08 11:41:53 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/08/14 - refactor default timer model to default to 15
 *                         minutes timeout and be able to find parametermanager
 *                         from dispatcher
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.timer.ScreenTimeoutIfc;
import oracle.retail.stores.pos.ui.timer.TimerModelIfc;

/**
 * This is the bean model used by the SaleBean.
 * 
 * @version $Revision: /main/11 $
 * @see oracle.retail.stores.pos.ui.beans.SaleBean
 */
public class MainMenuBeanModel extends POSBaseBeanModel implements ScreenTimeoutIfc
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    private static final long serialVersionUID = 7387604598852350309L;

    /** Contains the model for the dashboard report bean */
    protected DashboardReportBeanModel dashboardReportBeanModel = null;

    /**
     * Default constructor.
     */
    public MainMenuBeanModel()
    {
        // turn off timeout during main menu
        timerModel.setTimerEnabled(false);
    }

    /**
     * Sets the timerModel to be used in this class, the default value is the
     * DefaultTimerModel class.
     * 
     * @param timerModel TimerModel to use
     */
    public void setTimerModel(TimerModelIfc timerModel)
    {
        this.timerModel = timerModel;
    }

    /**
     * Return the timerModel this LineItemsModel is using
     * 
     * @return timerModel
     */
    public TimerModelIfc getTimerModel()
    {
        return timerModel;
    }

    /**
     * Gets the Dashboard Report bean Model
     * 
     * @return the dashboardReportBeanModel
     */
    public DashboardReportBeanModel getDashboardReportBeanModel()
    {
        return dashboardReportBeanModel;
    }

    /**
     * Sets the DashboardReport bean model
     * 
     * @param value the dashboardReportBeanModel
     */
    public void setDashboardReportBeanModel(DashboardReportBeanModel value)
    {
        this.dashboardReportBeanModel = value;
    }
}
