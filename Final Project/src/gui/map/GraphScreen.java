package gui.map;

import database.DiscreteData;
import database.Land;
import database.Queries;
import datatypes.UDim2;
import gui.events.Event;
import gui.graph.Histograph;
import gui.graph.LineChart;
import gui.graph.PieChart;
import gui.primitive.Container;
import gui.primitive.Screen;
import gui.primitive.TextButton;
import middleware.Main;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphScreen extends Screen
{
    public final UDim2[] CONTAINER_POSITIONS = {new UDim2(-0.6f, 0, 0.05f, 0), new UDim2(0f, 0f, 0.05f, 0f)};
    private final UDim2 CHART_POSITION = new UDim2(0.1f, 0, 0.05f, 0);
    private final UDim2 PIE_CHART_POSITION = new UDim2(0.1f, 0, 0.135f, 0);
    private final UDim2 CHART_SIZE = new UDim2(0.8f, 0, 0.85f, 0);
    private final UDim2 PIE_CHART_SIZE = new UDim2(0.725f, 0, 0.725f, 0);
    private Container container;
    private Histograph priceHistograph, monthHistograph, dateHistograph = null;
    private PieChart typePieChart, conditionPieChart = null;
    private LineChart monthLineChart, monthAverageLineChart, dateLineChart, dateAverageLineChart = null;
    private Queries queries;
    private ArrayList<Integer> listOfIDs;
    Event.EventOperation togglePieChart = () ->
    {
        if (container != null && container.getPosition() == CONTAINER_POSITIONS[0])
        {
            setupPieCharts();
            getApplet().frame.setCurrentScreen(this.ID);
            container.tweenPosition(CONTAINER_POSITIONS[1], 0.4f);

            TextButton propertyTypeButton = new TextButton(container, new UDim2(0.1f, 0f, 0.925f, 0f),
                    new UDim2(0.35f, 0, 0.04f, 0), "Types of Property Sold");
            propertyTypeButton.setTextSize(14);
            propertyTypeButton.setBackgroundColor(0x2F000000);
            propertyTypeButton.setTextColor(255);
            propertyTypeButton.setBorder(true);
            propertyTypeButton.setZIndex(1);

            Event.EventOperation showPropertyGraph = () ->
            {
                conditionPieChart.setVisible(false);
                typePieChart.setVisible(true);
                typePieChart.reset();
            };

            propertyTypeButton.MouseButton1Click.connect(showPropertyGraph);
            propertyTypeButton.MouseButton1Click.trigger();

            TextButton conditionButton = new TextButton(container, new UDim2(0.55f, 0f, 0.925f, 0f),
                    new UDim2(0.35f, 0, 0.04f, 0), "Condition of Property Sold");
            conditionButton.setTextSize(14);
            conditionButton.setBackgroundColor(0x2F000000);
            conditionButton.setTextColor(255);
            conditionButton.setBorder(true);
            conditionButton.setZIndex(1);

            Event.EventOperation showConditionGraph = () ->
            {
                conditionPieChart.setVisible(true);
                typePieChart.setVisible(false);
                conditionPieChart.reset();
            };

            conditionButton.MouseButton1Click.connect(showConditionGraph);
        }
    };
    Event.EventOperation toggleLineChart = () ->
    {
        if (container != null && container.getPosition() == CONTAINER_POSITIONS[0])
        {
            setupLineCharts();
            getApplet().frame.setCurrentScreen(this.ID);
            container.tweenPosition(CONTAINER_POSITIONS[1], 0.4f);

            TextButton monthDataButton = new TextButton(container, new UDim2(0.15f, 0f, 0.925f, 0f),
                    new UDim2(0.15f, 0, 0.04f, 0), "Sales By Month");
            monthDataButton.setTextSize(14);
            monthDataButton.setBackgroundColor(0x2F000000);
            monthDataButton.setTextColor(255);
            monthDataButton.setBorder(true);
            monthDataButton.setZIndex(1);

            Event.EventOperation showMonthGraph = () ->
            {
                monthAverageLineChart.setVisible(false);
                dateAverageLineChart.setVisible(false);
                dateLineChart.setVisible(false);
                monthLineChart.setVisible(true);
                monthLineChart.reset();
            };

            monthDataButton.MouseButton1Click.connect(showMonthGraph);
            monthDataButton.MouseButton1Click.trigger();

            TextButton monthAverageButton = new TextButton(container, new UDim2(0.35f, 0f, 0.925f, 0f),
                    new UDim2(0.15f, 0, 0.04f, 0), "Price by Month");
            monthAverageButton.setTextSize(14);
            monthAverageButton.setBackgroundColor(0x2F000000);
            monthAverageButton.setTextColor(255);
            monthAverageButton.setBorder(true);
            monthAverageButton.setZIndex(1);

            Event.EventOperation showMonthAverageGraph = () ->
            {
                monthAverageLineChart.setVisible(true);
                dateAverageLineChart.setVisible(false);
                dateLineChart.setVisible(false);
                monthLineChart.setVisible(false);
                monthAverageLineChart.reset();
            };

            monthAverageButton.MouseButton1Click.connect(showMonthAverageGraph);

            TextButton dateButton = new TextButton(container, new UDim2(0.55f, 0f, 0.925f, 0f),
                    new UDim2(0.15f, 0, 0.04f, 0), "Sales by Date");
            dateButton.setTextSize(14);
            dateButton.setBackgroundColor(0x2F000000);
            dateButton.setTextColor(255);
            dateButton.setBorder(true);
            dateButton.setZIndex(1);

            Event.EventOperation showDateGraph = () ->
            {
                monthAverageLineChart.setVisible(false);
                dateAverageLineChart.setVisible(false);
                dateLineChart.setVisible(true);
                monthLineChart.setVisible(false);
                dateLineChart.reset();
            };

            dateButton.MouseButton1Click.connect(showDateGraph);

            TextButton dateAverageButton = new TextButton(container, new UDim2(0.75f, 0f, 0.925f, 0f),
                    new UDim2(0.15f, 0, 0.04f, 0), "Price by Date");
            dateAverageButton.setTextSize(14);
            dateAverageButton.setBackgroundColor(0x2F000000);
            dateAverageButton.setTextColor(255);
            dateAverageButton.setBorder(true);
            dateAverageButton.setZIndex(1);

            Event.EventOperation showDateAverageGraph = () ->
            {
                monthAverageLineChart.setVisible(false);
                dateAverageLineChart.setVisible(true);
                dateLineChart.setVisible(false);
                monthLineChart.setVisible(false);
                dateAverageLineChart.reset();
            };

            dateAverageButton.MouseButton1Click.connect(showDateAverageGraph);
        }
    };
    private boolean entered;
    private HashMap<String, String> info;
    private DiscreteData priceData, monthSalesData, typeData, dateData, propertyData, monthAverageData, dateAverageData;
    public Event.EventOperation toggleBarChart = () ->
    {
        if (container != null && container.getPosition() == CONTAINER_POSITIONS[0])
        {
            setupHistographs();
            getApplet().frame.setCurrentScreen(this.ID);
            container.tweenPosition(CONTAINER_POSITIONS[1], 0.4f);

            TextButton priceButton = new TextButton(container, new UDim2(0.1f, 0f, 0.925f, 0f),
                    new UDim2(0.2f, 0, 0.04f, 0), "Price");
            priceButton.setTextSize(14);
            priceButton.setBackgroundColor(0x2F000000);
            priceButton.setTextColor(255);
            priceButton.setBorder(true);
            priceButton.setZIndex(1);

            Event.EventOperation showPriceGraph = () ->
            {
                dateHistograph.setVisible(false);
                monthHistograph.setVisible(false);
                priceHistograph.setVisible(true);
                priceHistograph.resetBarHeights();
            };

            priceButton.MouseButton1Click.connect(showPriceGraph);
            priceButton.MouseButton1Click.trigger();

            TextButton monthSalesButton = new TextButton(container, new UDim2(0.4f, 0f, 0.925f, 0f),
                    new UDim2(0.2f, 0, 0.04f, 0), "Month");
            monthSalesButton.setTextSize(14);
            monthSalesButton.setBackgroundColor(0x2F000000);
            monthSalesButton.setTextColor(255);
            monthSalesButton.setBorder(true);
            monthSalesButton.setZIndex(1);

            Event.EventOperation showPropertyGraph = () ->
            {
                dateHistograph.setVisible(false);
                monthHistograph.setVisible(true);
                priceHistograph.setVisible(false);
                monthHistograph.resetBarHeights();
            };

            monthSalesButton.MouseButton1Click.connect(showPropertyGraph);

            TextButton dateSalesButton = new TextButton(container, new UDim2(0.7f, 0f, 0.925f, 0f),
                    new UDim2(0.2f, 0, 0.04f, 0), "Time");
            dateSalesButton.setTextSize(14);
            dateSalesButton.setBackgroundColor(0x2F000000);
            dateSalesButton.setTextColor(255);
            dateSalesButton.setBorder(true);
            dateSalesButton.setZIndex(1);

            Event.EventOperation showConditionGraph = () ->
            {
                dateHistograph.setVisible(true);
                monthHistograph.setVisible(false);
                priceHistograph.setVisible(false);
                dateHistograph.resetBarHeights();
            };

            dateSalesButton.MouseButton1Click.connect(showConditionGraph);
        }
    };

    public GraphScreen(Main applet, Queries queries, ArrayList<Integer> listOfIDs, HashMap info, int mainID)
    {
        super(applet);
        this.queries = queries;
        this.listOfIDs = listOfIDs;
        this.info = info;
        container = new Container(this, CONTAINER_POSITIONS[0], new UDim2(0.6f, 0, 0.9f, 0));
        container.setBackgroundColor(applet.BACKGROUND_COLOR);
        container.setBorder(false);
        entered = false;

        Event.EventOperation closeGraphScreen = () ->
        {
            if (entered)
            {
                container.clearAllChildren();

                container.tweenPosition(CONTAINER_POSITIONS[0], 0.4f);
                applet.frame.setCurrentScreen(mainID);
                entered = false;
            }
        };

        container.MouseEnter.connect(() -> entered = true);
        container.MouseLeave.connect(closeGraphScreen);
    }

    public void changeList(ArrayList<Integer> listOfIDs)
    {
        this.listOfIDs = listOfIDs;
        container.clearAllChildren();
        priceData = null;
        monthSalesData = null;
        typeData = null;
        dateData = null;
        propertyData = null;
        monthAverageData = null;
        dateAverageData = null;
    }

    public void changeInfo(HashMap info)
    {
        this.info = info;
    }

    public void setupHistographs()
    {
        if (priceData == null)
            priceData = queries.priceDiscreteData(listOfIDs, Integer.parseInt(info.get("Minimum Price").replaceFirst(".", "")),
                    Integer.parseInt(info.get("Maximum Price").replaceFirst(".", "")), 30);
        priceHistograph = new Histograph(container, CHART_POSITION, CHART_SIZE,
                priceData.getData(), 120, priceData.getLabels(), priceData.getTitle(), 0xcF317873, "Price (�)", "Number of Sales");
        priceHistograph.setBackgroundColor(0x3FFFFFFF);
        priceHistograph.setVisible(false);
        priceHistograph.setTextColor(0xFFFFFFFF);

        if (monthSalesData == null)
            monthSalesData = queries.monthSalesDiscreteData(listOfIDs);
        monthHistograph = new Histograph(container, CHART_POSITION, CHART_SIZE,
                monthSalesData.getData(), 120, monthSalesData.getLabels(), monthSalesData.getTitle(), 0xcF809f39, "Month", "Number of Sales");
        monthHistograph.setBackgroundColor(0x3FFFFFFF);
        monthHistograph.setVisible(false);
        monthHistograph.setTextColor(0xFFFFFFFF);

        if (dateData == null)
            dateData = queries.dateAmountDiscreteData(listOfIDs, 20);
        dateHistograph = new Histograph(container, CHART_POSITION, CHART_SIZE,
                dateData.getData(), 120, dateData.getLabels(), dateData.getTitle(), 0xcFFF2622, "Date", "Number of Sales");
        dateHistograph.setBackgroundColor(0x3FFFFFFF);
        dateHistograph.setVisible(false);
        dateHistograph.setTextColor(0xFFFFFFFF);

    }

    public void setupPieCharts()
    {
        if (propertyData == null)
            propertyData = queries.propertyTypeDiscreteData(listOfIDs,
                    new String[]{Land.TERRACED, Land.SEMI_DETACHED, Land.DETACHED, Land.FLATS_MAISONETTES, Land.OTHER});
        typePieChart = new PieChart(container, PIE_CHART_POSITION, PIE_CHART_SIZE,
                propertyData.getData(), 225, propertyData.getLabels(), "Type of Property");

        typePieChart.setBackgroundColor(0x3FFFFFFF);
        typePieChart.setVisible(false);
        typePieChart.setTextColor(0xFFFFFFFF);
        typePieChart.setFontSize(12);
        typePieChart.setLabelBackgroundColor(0x3F000000);

        if (typeData == null)
            typeData = queries.conditionDiscreteData(listOfIDs);
        conditionPieChart = new PieChart(container, PIE_CHART_POSITION, PIE_CHART_SIZE,
                typeData.getData(), 200, typeData.getLabels(), "Condition of Property");
        conditionPieChart.setBackgroundColor(0x3FFFFFFF);
        conditionPieChart.setVisible(false);
        conditionPieChart.setTextColor(0xFFFFFFFF);
        conditionPieChart.setFontSize(12);
        conditionPieChart.setLabelBackgroundColor(0x3F000000);
    }

    public void setupLineCharts()
    {
        if (monthSalesData == null)
            monthSalesData = queries.monthSalesDiscreteData(listOfIDs);
        monthLineChart = new LineChart(container, CHART_POSITION, CHART_SIZE,
                monthSalesData.getData(), 120, monthSalesData.getLabels(), monthSalesData.getTitle(),"Month", "Number of Sales");
        monthLineChart.setBackgroundColor(0x3FFFFFFF);
        monthLineChart.setVisible(false);
        monthLineChart.setTextColor(0xFFFFFFFF);
        monthLineChart.setLineColor(0xFFFFFFFF);
        monthLineChart.setPointColor(0);

        if (monthAverageData == null)
            monthAverageData = queries.monthAverageDiscretData(listOfIDs);
        monthAverageLineChart = new LineChart(container, CHART_POSITION, CHART_SIZE,
                monthAverageData.getData(), 120, monthAverageData.getLabels(), monthAverageData.getTitle(), "Month", "Average Price(�)");
        monthAverageLineChart.setBackgroundColor(0x3FFFFFFF);
        monthAverageLineChart.setVisible(false);
        monthAverageLineChart.setTextColor(0xFFFFFFFF);
        monthAverageLineChart.setLineColor(0xFFFFFFFF);
        monthAverageLineChart.setPointColor(0);

        if (dateData == null)
            dateData = queries.dateAmountDiscreteData(listOfIDs, 10);
        dateLineChart = new LineChart(container, CHART_POSITION, CHART_SIZE,
                dateData.getData(), 120, dateData.getLabels(), dateData.getTitle(),"Date", "Number of Sales");
        dateLineChart.setBackgroundColor(0x3FFFFFFF);
        dateLineChart.setVisible(false);
        dateLineChart.setTextColor(0xFFFFFFFF);
        dateLineChart.setLineColor(0xFFFFFFFF);
        dateLineChart.setPointColor(0);

        if (dateAverageData == null)
            dateAverageData = queries.dateAverageDiscreteData(listOfIDs, 10);
        dateAverageLineChart = new LineChart(container, CHART_POSITION, CHART_SIZE,
                dateAverageData.getData(), 120, dateAverageData.getLabels(), dateAverageData.getTitle(), "Date", "Average Price(�)");
        dateAverageLineChart.setBackgroundColor(0x3FFFFFFF);
        dateAverageLineChart.setVisible(false);
        dateAverageLineChart.setTextColor(0xFFFFFFFF);
        dateAverageLineChart.setLineColor(0xFFFFFFFF);
        dateAverageLineChart.setPointColor(0);

    }

}
