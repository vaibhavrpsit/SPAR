/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DashboardReportBeanModel.java /main/2 2012/12/04 12:59:53 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * vbongu      11/30/12 - dashboard changes
 * vbongu      11/26/12 - Dashboard changes
 * vbongu      11/08/12 - initial version
 * vbongu      11/08/12 - Creation
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.EYSDate;

/**
 * This model is used by DashboardReportBean
 * 
 * @author vbongu
 * @since 14.0
 */
public class DashboardReportBeanModel extends POSBaseBeanModel
{

    private static final long serialVersionUID = 1L;

    protected String[] aprAssociateName = null;
    protected BigDecimal[] aprNetAmount = null;
    protected String[] dsrDepartmentName = null;
    protected BigDecimal[] dsrNetAmount = null;
    protected EYSDate[] hprHourlyTime = null;
    protected BigDecimal[] hprHourlySales = null;
    protected String[] dashboardReports;
    protected String[] dashboardMessages;

    protected boolean enableDashboard = false;

    public DashboardReportBeanModel()
    {
        // Auto-generated constructor stub
    }

    /**
     * Gets the Associate name for associate productivity report
     * 
     * @return aprAssociateName
     */
    public String[] getAprAssociateName()
    {
        return aprAssociateName;
    }

    /**
     * Sets the Associate name for associate productivity report
     * 
     * @param aprAssociateName
     */
    public void setAprAssociateName(String[] aprAssociateName)
    {
        this.aprAssociateName = aprAssociateName;
    }

    /**
     * Gets the net amount for associate productivity report
     * 
     * @return aprNetAmount
     */
    public BigDecimal[] getAprNetAmount()
    {
        return aprNetAmount;
    }

    /**
     * Sets the net amount for associate productivity report
     * 
     * @param aprNetAmount
     */
    public void setAprNetAmount(BigDecimal[] aprNetAmount)
    {
        this.aprNetAmount = aprNetAmount;
    }

    /**
     * Gets the department name for department sales report
     * 
     * @return dsrDepartmentName
     */
    public String[] getDsrDepartmentName()
    {
        return dsrDepartmentName;
    }

    /**
     * Sets the department name for department sales report
     * 
     * @param dsrDepartmentName
     */
    public void setDsrDepartmentName(String[] dsrDepartmentName)
    {
        this.dsrDepartmentName = dsrDepartmentName;
    }

    /**
     * Gets the net amount for department sales report
     * 
     * @return aprAssociateName
     */
    public BigDecimal[] getDsrNetAmount()
    {
        return dsrNetAmount;
    }

    /**
     * Sets the net amount for department sales report
     * 
     * @param dsrNetAmount
     */
    public void setDsrNetAmount(BigDecimal[] dsrNetAmount)
    {
        this.dsrNetAmount = dsrNetAmount;
    }

    /**
     * Gets the hourly sales time for hourly sales report
     * 
     * @return hprHourlyTime
     */
    public EYSDate[] getHprHourlyTime()
    {
        return hprHourlyTime;
    }

    /**
     * Sets the hourly sales time for hourly sales report
     * 
     * @param hourlyTime
     */
    public void setHprHourlyTime(EYSDate[] hourlyTime)
    {
        this.hprHourlyTime = hourlyTime;
    }

    /**
     * Gets the hourly sales for hourly sales report
     * 
     * @return hprHourlySales
     */
    public BigDecimal[] getHprHourlySales()
    {
        return hprHourlySales;
    }

    /**
     * Sets the hourly sales for hourly sales report
     * 
     * @param hprHourlySales
     */
    public void setHprHourlySales(BigDecimal[] hprHourlySales)
    {
        this.hprHourlySales = hprHourlySales;
    }

    public void setEnableDashboard(boolean enableDashboard)
    {
        this.enableDashboard = enableDashboard;
    }

    public boolean isEnableDashboard()
    {
        return enableDashboard;
    }

    /**
     * gets the report list
     * 
     * @return
     */
    public String[] getDashboardReportsList()
    {
        return dashboardReports;
    }

    /**
     * sets the reports list
     * 
     * @param dashboardReports
     */
    public void setDashboardReportsList(String[] dashboardReports)
    {
        this.dashboardReports = dashboardReports;
    }

    /**
     * gets the db messages
     * 
     * @return
     */
    public String[] getDashboardMessages()
    {
        return dashboardMessages;
    }

    /**
     * sets the dashboard messages
     * 
     * @param dashboardMessages
     */
    public void setDashboardMessages(String[] dashboardMessages)
    {
        this.dashboardMessages = dashboardMessages;
    }

    /**
     * Converts to a String representing the data in this Object
     * 
     * @return String representing the data in this Object
     **/
    public String toString()
    {
        StringBuilder buff = new StringBuilder();
        buff.append("DashboardReportBeanModel").append("\n");
        return (buff.toString());
    }

}
