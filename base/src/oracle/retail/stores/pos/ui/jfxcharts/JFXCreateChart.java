/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/jfxcharts/JFXCreateChart.java /main/1 2012/12/04 09:26:00 vbongu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vbongu      12/04/12 - jfxcreatechart.
* vbongu      12/04/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.ui.jfxcharts;

import java.math.BigDecimal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * Creates the JavaFX charts depending on the chart type
 * 
 * @author vbongu
 * @since 14.0
 */
public class JFXCreateChart extends Chart
{
    protected Chart chart;
    /** X-Axis label */
    protected String xAxisLabel = null;
    /** Y-Axis label */
    protected String yAxisLabel = null;

    public JFXCreateChart()
    {

    }

    /**
     * Creates the chart depending on the chart type from plaf and report
     * selected
     * 
     * @param dbReport the report type
     * @param chartType the chart type
     * @param xData the x-axis data
     * @param yData the y-axis data
     * @return chart
     */
    public Chart createChart(String dbReport, String chartType, String[] xData, BigDecimal[] yData)
    {

        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        xAxisLabel = utility.retrieveText(dbReport, BundleConstantsIfc.DASHBOARD_BUNDLE_NAME, "XAxisLabel",
                "XAxisLabel");
        yAxisLabel = utility.retrieveText(dbReport, BundleConstantsIfc.DASHBOARD_BUNDLE_NAME, "YAxisLabel",
                "YAxisLabel");
        if (JFXChartTypeConstantsIfc.BAR_CHART.equalsIgnoreCase(chartType))
        {
            chart = createBarChart(xData, yData);
        }
        else if (JFXChartTypeConstantsIfc.PIE_CHART.equalsIgnoreCase(chartType))
        {
            chart = createPieChart(xData, yData);
        }
        else if (JFXChartTypeConstantsIfc.AREA_CHART.equalsIgnoreCase(chartType))
        {
            chart = createAreaChart(xData, yData);
        }
        else if (JFXChartTypeConstantsIfc.LINE_CHART.equalsIgnoreCase(chartType))
        {
            chart = createLineChart(xData, yData);
        }
        else if (JFXChartTypeConstantsIfc.BUBBLE_CHART.equalsIgnoreCase(chartType))
        {
            chart = createBubbleChart(xData, yData);
        }
        else if (JFXChartTypeConstantsIfc.SCATTER_CHART.equalsIgnoreCase(chartType))
        {
            chart = createScatterChart(xData, yData);
        }

        return chart;

    }

    /**
     * Creates the bar chart
     * 
     * @param xData the x-axis data
     * @param yData the y-axis data
     * @return barChart
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BarChart<String, Number> createBarChart(String xData[], BigDecimal[] yData)
    {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
        XYChart.Series bar = new XYChart.Series();
        int i = 0;
        for (i = 0; i < xData.length; i++)
        {
            String x = xData[i];
            BigDecimal y = yData[i];

            bar.getData().add(getData(x, y));

        }
        barChart.setLegendVisible(false);
        barChart.getData().addAll(bar);

        // If CSS is not an option - remove after code review
        /*
         * String xyChart = UIFactory.getInstance()
         * .getUIProperties(LocaleMap.getLocale
         * (LocaleConstantsIfc.USER_INTERFACE)).getProperty("xyCharts"); for
         * (int j = 0; j < i; j++) {
         * barChart.getData().get(0).getData().get(j).getNode
         * ().setStyle("-fx-background-color: " + xyChart + ";"); }
         */

        return barChart;
    }

    /**
     * Creates the area chart
     * 
     * @param xData the x-axis data
     * @param yData the y-axis data
     * @return areaChart
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AreaChart<String, Number> createAreaChart(String xData[], BigDecimal[] yData)
    {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        AreaChart<String, Number> areaChart = new AreaChart<String, Number>(xAxis, yAxis);

        XYChart.Series area = new XYChart.Series();
        for (int i = 0; i < xData.length; i++)
        {
            String x = xData[i];
            BigDecimal y = yData[i];

            area.getData().add(getData(x, y));
        }
        areaChart.setLegendVisible(false);
        areaChart.getData().addAll(area);
        return areaChart;
    }

    /**
     * Creates the line chart
     * 
     * @param xData the x-axis data
     * @param yData the y-axis data
     * @return lineChart
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public LineChart createLineChart(String xData[], BigDecimal[] yData)
    {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);

        LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

        XYChart.Series line = new XYChart.Series();
        for (int i = 0; i < xData.length; i++)
        {
            String x = xData[i];
            BigDecimal y = yData[i];

            line.getData().add(getData(x, y));
        }
        lineChart.setLegendVisible(false);
        lineChart.getData().addAll(line);
        return lineChart;

    }

    /**
     * Creates the bubble chart
     * 
     * @param xData the x-axis data
     * @param yData the y-axis data
     * @return bubbleChart
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BubbleChart createBubbleChart(String xData[], BigDecimal[] yData)
    {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);

        BubbleChart<String, Number> bubbleChart = new BubbleChart<String, Number>(xAxis, yAxis);

        XYChart.Series bubble = new XYChart.Series();
        for (int i = 0; i < xData.length; i++)
        {
            String x = xData[i];
            BigDecimal y = yData[i];

            bubble.getData().add(getData(x, y));
        }
        bubbleChart.setLegendVisible(false);
        bubbleChart.getData().addAll(bubble);
        return bubbleChart;

    }

    /**
     * Creates the scatter chart
     * 
     * @param xData the x-axis data
     * @param yData the y-axis data
     * @return scatterChart
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ScatterChart createScatterChart(String xData[], BigDecimal[] yData)
    {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);

        ScatterChart<String, Number> scatterChart = new ScatterChart<String, Number>(xAxis, yAxis);

        XYChart.Series scatter = new XYChart.Series();
        for (int i = 0; i < xData.length; i++)
        {
            String x = xData[i];
            BigDecimal y = yData[i];

            scatter.getData().add(getData(x, y));
        }
        scatterChart.setLegendVisible(false);
        scatterChart.getData().addAll(scatter);
        return scatterChart;

    }

    /**
     * Creates the pie chart
     * 
     * @param xData the x-axis data
     * @param yData the y-axis data
     * @return pieChart
     */
    public PieChart createPieChart(String xData[], BigDecimal[] yData)
    {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        final PieChart pieChart = new PieChart(pieChartData);
        int i;
        for (i = 0; i < xData.length; i++)
        {
            String x = xData[i];

            Double y = yData[i].doubleValue();

            pieChartData.add(getData(x, y));
        }
        pieChart.setLabelLineLength(10);

        // If CSS is not an option - remove after code review
        /*
         * for (int j = 1; j <= i; j++) { String pieColor =
         * UIFactory.getInstance()
         * .getUIProperties(LocaleMap.getLocale(LocaleConstantsIfc
         * .USER_INTERFACE)) .getProperty("pieChart" + j + ""); if (pieColor !=
         * null) { pieChart.getData().get(j -
         * 1).getNode().setStyle("-fx-background-color: " + pieColor + ";"); } }
         */

        return pieChart;
    }

    private XYChart.Data<String, BigDecimal> getData(String x, BigDecimal y)
    {
        XYChart.Data<String, BigDecimal> data = new XYChart.Data<String, BigDecimal>();
        data.setXValue(x);
        data.setYValue(y);
        return data;
    }

    private PieChart.Data getData(String x, double y)
    {
        PieChart.Data data = new PieChart.Data(x, y);
        data.setName(x);
        data.setPieValue(y);
        return data;
    }

    @Override
    protected void layoutChartChildren(double arg0, double arg1, double arg2, double arg3)
    {
        // TODO Auto-generated method stub

    }

}
