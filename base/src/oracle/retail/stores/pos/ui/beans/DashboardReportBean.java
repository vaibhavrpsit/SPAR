/* ===========================================================================  
* Copyright (c) 2012, 20012, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DashboardReportBean.java /main/8 2013/05/15 14:57:53 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/15/13 - set last refresh to just time
 *    vbongu    12/13/12 - add removeAll to messageListPanel
 *    cgreene   12/12/12 - lizily initialize graphPanel
 *    vbongu    11/30/12 - dashboard changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.reports.ReportTypeConstantsIfc;
import oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationAppSite;
import oracle.retail.stores.pos.ui.jfxcharts.JFXCreateChart;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * Bean for displaying the dashboard. Default behavior is to show the dashboard
 * and if disabled to show the logo.
 *
 * @author vbongu
 * @since 14.0
 */
public class DashboardReportBean extends BaseBeanAdapter
{
    private static final long serialVersionUID = 5011930841939464845L;
    /** The logger to which log messages will be sent */
    protected static final Logger logger = Logger.getLogger(DashboardReportBean.class);
    /** The default bean name. */
    public static final String BEAN_NAME = "DashboardReportBean";

    /** Used to configure component layout with uifactory. */
    public static final String DB_UI_PREFIX = "DashboardReport";
    public static final String DB_UI_LOGO = "DashboardReport.logoPanel";
    public static final String DB_UI_GRAPH = "DashboardReport.graphPanel";
    public static final String DB_UI_MESSAGE = "DashboardReport.messagePanel";

    /** Used to determine the chart type from property files */
    public static final String DB_RTYPE_APROD = "ChartType.associateProductivity";
    public static final String DB_RTYPE_HPROD = "ChartType.hourlyProductivity";
    public static final String DB_RTYPE_DSALES = "ChartType.departmentSales";

    /** Used to get the label text from locale specific prop files */
    public static final String REPORTTYPE_LABEL_SPEC = "ReportTypeSpec";
    public static final String DB_LABEL_SPEC = "DashboardReportSpec";

    /** Image names */
    public static final String DB_LOGO_MAIN = "mainLogo";
    public static final String DB_LOGO_SMALL = "dbSmallLogo";

    /**
     * Style sheet should be located at
     * "classpath://config/ui/jfxStyleSheet.css"
     */
    public static final String styleSheet = "/config/ui/jfxStyleSheet.css";

    /** The JavaFX Panel for displaying the charts */
    protected JFXPanel fxPanel;
    /** List of reports selected */
    protected String[] dashboardReports = null;
    /** List of messages to display */
    protected String[] dashboardMessages = null;

    /** JPanels for displaying dashboard */
    protected JPanel logoPanel;
    protected JPanel graphPanel;
    protected JPanel messagePanel;
    protected JPanel messageListPanel;

    /** Time at which the dashboard was last refreshed */
    protected EYSTime lastRefreshed;
    /** JLable to display last refreshed date */
    protected JLabel lastRefreshedLabel;
    /** Image to display when the dashboard is disabled */
    protected Image logoImage;

    protected UtilityManagerIfc utility;

    /**
     * Constructor
     */
    public DashboardReportBean()
    {
        UI_PREFIX = DB_UI_PREFIX;

    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure()
    {

        // Initialize the panel
        setName(BEAN_NAME);
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());

        utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);

        buildLogoPanel();
        buildMessagePanel();

        add(logoPanel, BorderLayout.PAGE_START);
        add(messagePanel, BorderLayout.PAGE_END);
    }

    /**
     * Builds the logo panel with label and icon
     */
    protected void buildLogoPanel()
    {
        logoPanel = new JPanel(new BorderLayout());
        JLabel dbLabel = null;
        Icon icon = new ImageIcon(UIFactory.getInstance().getImage(DB_LOGO_SMALL, dbLabel));
        String labelText = utility.retrieveText(DB_LABEL_SPEC, BundleConstantsIfc.DASHBOARD_BUNDLE_NAME, "dashboardLabel",
                "STORE DASHBOARD");
        dbLabel = uiFactory.createLabel("dashboardLabel", labelText, icon, DB_UI_LOGO);
        uiFactory.configureUIComponent(logoPanel, DB_UI_LOGO);
        logoPanel.add(dbLabel, BorderLayout.WEST);
    }

    /**
     * Builds the graph panel with JavaFX charts and label for last refreshed
     * date
     */
    protected void buildGraphPanel()
    {
        graphPanel = new JPanel(new BorderLayout());
        uiFactory.configureUIComponent(graphPanel, DB_UI_GRAPH);

        if (BrowserFoundationAppSite.isJavaFXInstalled())
        {
            fxPanel = new JFXPanel();
            fxPanel.setLayout(new BorderLayout());
            uiFactory.configureUIComponent(fxPanel, DB_UI_GRAPH);

            lastRefreshedLabel = new JLabel();
            fxPanel.add(lastRefreshedLabel, BorderLayout.PAGE_END);
            graphPanel.add(fxPanel, BorderLayout.CENTER);

            // refresh graphPanel when ctrl+R is pressed
            KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
            graphPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "RefreshPanel");
            @SuppressWarnings("serial")
            Action ctrlRKeyPressed = new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    updateBean();
                }
            };
            graphPanel.getActionMap().put("RefreshPanel", ctrlRKeyPressed);
        }
    }

    /**
     * Builds the message panel with message label and list of messages
     */
    protected void buildMessagePanel()
    {
        messagePanel = new JPanel(new BorderLayout());
        uiFactory.configureUIComponent(messagePanel, DB_UI_MESSAGE);
        String labelText = utility.retrieveText(DB_LABEL_SPEC, BundleConstantsIfc.DASHBOARD_BUNDLE_NAME,
                "dashboardMessagesLabel", "MESSAGES: ");
        JLabel messagesLabel = uiFactory.createLabel("dashboardMessagesLabel", labelText, null, DB_UI_MESSAGE);

        messagePanel.add(messagesLabel, BorderLayout.WEST);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setModel()
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set DashboardReportBeanModel to null");
        }
        if (model instanceof MainMenuBeanModel)
        {
            MainMenuBeanModel pModel = (MainMenuBeanModel)model;
            beanModel = pModel.getDashboardReportBeanModel();
            updateBean();
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateBean()
     */
    @Override
    public void updateBean()
    {
        // lazily create JavaFX bean to avoid any errors due to JavaFX not installed.
        if (graphPanel == null)
        {
            buildGraphPanel();
            add(graphPanel, BorderLayout.CENTER);
        }

        lastRefreshed = DomainGateway.getFactory().getEYSTimeInstance();
        String lastRefreshedDate = lastRefreshed.toFormattedString(getLocale());
        lastRefreshedLabel.setText(utility.retrieveText(DB_LABEL_SPEC, BundleConstantsIfc.DASHBOARD_BUNDLE_NAME,
                "dashboardLastRefreshLabel", "Last Refreshed: ") + lastRefreshedDate);

        if (beanModel instanceof DashboardReportBeanModel)
        {
            DashboardReportBeanModel model = (DashboardReportBeanModel)beanModel;
            dashboardReports = model.getDashboardReportsList();
            dashboardMessages = model.getDashboardMessages();
        }
        if (dashboardMessages != null)
        {
            if(messageListPanel != null)
            {
                messageListPanel.removeAll();
            }
            // add the messages to the list
            buildMessageListPanel();
        }
        if (dashboardReports != null)
        {
            initFxComponents();
        }
    }

    /**
     * Builds the message list and adds it to the message panel
     */
    protected void buildMessageListPanel()
    {
        messageListPanel = new JPanel();
        uiFactory.configureUIComponent(messageListPanel, DB_UI_MESSAGE);
        messageListPanel.setLayout(new GridLayout(0, 1));
        JLabel[] msgLabels = new JLabel[dashboardMessages.length];

            for (int i = 0; i < dashboardMessages.length; i++)
            {
                msgLabels[i] = new JLabel(dashboardMessages[i]);
                messageListPanel.add(msgLabels[i]);
            }


        messagePanel.add(messageListPanel, BorderLayout.CENTER);
    }

    /**
     * Initialize JavaFX components by calling createScene
     */
    private void initFxComponents()
    {
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                createScene();
            }
        });
    }

    /**
     * Creates the scene with gridpane and charts to display
     */
    protected void createScene()
    {
        GridPane grid = new GridPane();

        // Always maintains two rows and two columns independent of the size of
        // screen
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(column1, column2);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        grid.getRowConstraints().addAll(row1, row2);

        Scene scene = new Scene(grid);

        ArrayList<String> reportTypes = new ArrayList<String>();

        int columnWidth = 1;
        int rowWidth = 1;
        int[] gridConstraints;

        JFXCreateChart chart = new JFXCreateChart();
        Chart finalChart = null;
        int dbReportsLength = dashboardReports.length;
        Arrays.sort(dashboardReports);
        int reportIndex;

        for (String dbReport : dashboardReports)
        {
            if (beanModel instanceof DashboardReportBeanModel)
            {
                DashboardReportBeanModel model = (DashboardReportBeanModel)beanModel;
                reportTypes.add(dbReport);
                reportIndex = reportTypes.indexOf(dbReport);

                if (ReportTypeConstantsIfc.HOURLYPROD_REPORT.equalsIgnoreCase(dbReport))
                {

                    String chartType = UIFactory.getInstance()
                            .getUIProperties(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE))
                            .getProperty(DB_RTYPE_HPROD);
                    EYSDate[] transactionDateTime = model.getHprHourlyTime();
                    int length = transactionDateTime.length;
                    String[] transactionHour = new String[length];
                    for (int i = 0; i < length; i++)
                    {
                        transactionHour[i] = transactionDateTime[i].toFormattedString("HH:mm");
                    }

                    finalChart = chart.createChart(dbReport, chartType, transactionHour, model.getHprHourlySales());

                    String titleLabel = utility.retrieveText(REPORTTYPE_LABEL_SPEC,
                            BundleConstantsIfc.DASHBOARD_BUNDLE_NAME, "HourlyProductivityLabel",
                            "HourlyProductivityLabel");
                    finalChart.setTitle(titleLabel);

                }

                else if (ReportTypeConstantsIfc.ASSOCPRODUCTIVITY_REPORT.equalsIgnoreCase(dbReport))
                {
                    String chartType = UIFactory.getInstance()
                            .getUIProperties(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE))
                            .getProperty(DB_RTYPE_APROD);

                    finalChart = chart.createChart(dbReport, chartType, model.getAprAssociateName(),
                            model.getAprNetAmount());
                    String titleLabel = utility.retrieveText(REPORTTYPE_LABEL_SPEC,
                            BundleConstantsIfc.DASHBOARD_BUNDLE_NAME, "AssociateProductivityLabel",
                            "AssociateProductivityLabel");
                    finalChart.setTitle(titleLabel);

                }

                else if (ReportTypeConstantsIfc.DEPARTMENTSALES_REPORT.equalsIgnoreCase(dbReport))
                {

                    String chartType = UIFactory.getInstance()
                            .getUIProperties(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE))
                            .getProperty(DB_RTYPE_DSALES);

                    finalChart = chart.createChart(dbReport, chartType, model.getDsrDepartmentName(),
                            model.getDsrNetAmount());
                    String titleLabel = utility.retrieveText(REPORTTYPE_LABEL_SPEC,
                            BundleConstantsIfc.DASHBOARD_BUNDLE_NAME, "DepartmentSalesLabel", "DepartmentSalesLabel");
                    finalChart.setTitle(titleLabel);

                }

                if (finalChart != null)
                {
                    gridConstraints = getReportLocation(reportIndex, columnWidth, rowWidth);
                    grid.add(finalChart, gridConstraints[0], gridConstraints[1]);
                    setSpan(finalChart, dbReportsLength, reportTypes.indexOf(dbReport));
                }
            }
        }

        scene.getStylesheets().add(DashboardReportBean.class.getResource(styleSheet).toExternalForm());
        fxPanel.setScene(scene);

    }

    /**
     * Get the grid constraints for each report
     *
     * @param position the position of the report
     * @param columnWidth width of the column in the grid
     * @param rowWidth width of the row in the grid
     * @return gridConstraints
     */
    protected int[] getReportLocation(int position, int columnWidth, int rowWidth)
    {
        int rowIndex = 0;
        int columnIndex = 0;
        if ((position % 2) == 0)
        {
            rowIndex = position / 2;
            columnIndex = 0;
        }
        else if ((position % 2) != 0)
        {
            rowIndex = ((position - 1) / 2);
            columnIndex = columnWidth;
        }

        int[] gridConstraints = { columnIndex, rowIndex };
        return gridConstraints;
    }

    /**
     * Sets the span for the row and column depending on the number of reports
     *
     * @param finalChart the JavaFX chart
     * @param dbReportsLength the number of reports
     * @param dbReportIndex the index of the report
     */
    protected void setSpan(Chart finalChart, int dbReportsLength, int dbReportIndex)
    {
        if (dbReportsLength == 2)
        {
            GridPane.setRowSpan(finalChart, 2);
        }
        if (dbReportsLength == 1 && finalChart != null)
        {
            GridPane.setRowSpan(finalChart, 2);
            GridPane.setColumnSpan(finalChart, 2);
        }
        if (dbReportsLength == 3 && finalChart != null && dbReportIndex == 1)
        {
            GridPane.setRowSpan(finalChart, 2);
        }
    }

}
