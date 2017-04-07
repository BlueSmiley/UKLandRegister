package gui.map;

import database.Land;
import database.Queries;
import datatypes.UDim2;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.events.PanMapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import gui.events.Event;
import gui.primitive.*;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Map extends Container
{
    private static final int CLICK_PERIOD = 500;
    private static final int FOCUS_ZOOM = 10;
    private static final int DEFAULT_ZOOM = 6;
    private static final int MIN_ZOOM = 5;
    private static final int MAX_ZOOM = 17;
    private static final int MAX_PANNING_DISTANCE = 600;
    private static final Location CENTRE_LOCATION = new Location(54.00366, -2.547855f);
    private static final int NUMBER_OF_MARKERS = 2500;
    public final Event MouseButton1Click = new Event();
    private final String STREET_VIEW = "https://maps.googleapis.com/maps/api/streetview?size=400x400&location=%f,%f&key=AIzaSyAX82YR3E81QTIG27I4X5Lx8ry7xOLNKX0";
    protected boolean mouseDown = false;
    protected long mouseDownAt;
    private boolean zoomingIntoAddress = false;
    private EventDispatcher eventDispatcher;
    private boolean active = true;

    private UnfoldingMap map;
    private InfoWindow markerInfoWindow, mapInfoWindow;
    private InfoMarker hitMarker, previousHitMarker;
    private MarkerManager markerManager;

    private PFont infoWindowNormalFont, infoWindowTitleFont, defaultFont;
    private UDim2[] infoWindowPositions = {new UDim2(1f, 0, 0, 0), new UDim2(0.85f, 0f, 0f, 0f)};
    private UDim2[] markerInfoWindowPositions = {new UDim2(1f, 0, 0, 0.5f), new UDim2(0.85f, -1f, 0f, 0.5f)};
    private HashMap<String, String> info;
    private Queries queries;
    private ArrayList<Integer> listOfIDs;

    private PImage histogramIcon, linePlotIcon, pieChartIcon;

    public Map(Parent parent, UDim2 position, UDim2 size, Queries queries, ArrayList<Integer> listOfIDs)
    {
        super(parent, position, size);
        this.queries = queries;
        this.listOfIDs = listOfIDs;
        this.info = this.queries.getMapDisplayInfo(listOfIDs);
        setZIndex(9);

        map = new UnfoldingMap(applet, getTruePosition()[0], getTruePosition()[1], size.absoluteX(), size.absoluteY(),
                new OpenStreetMap.OpenStreetMapProvider());
        markerManager = new MarkerManager();
        eventDispatcher = MapUtils.createDefaultEventDispatcher(applet, map);

        map.zoomAndPanTo(DEFAULT_ZOOM, CENTRE_LOCATION);
        map.setZoomRange(MIN_ZOOM, MAX_ZOOM);
        map.setTweening(true);
        map.setPanningRestriction(CENTRE_LOCATION, MAX_PANNING_DISTANCE);

        markerManager.addMarkers(getMarkers(this.queries.getLandObjects(listOfIDs)));
        map.addMarkerManager(markerManager);

        histogramIcon = applet.loadImage("img/Histogram-Icon.png");
        linePlotIcon = applet.loadImage("img/LinePlot-Icon.png");
        pieChartIcon = applet.loadImage("img/PieChart-Icon.png");

        markerInfoWindow = new InfoWindow(parent, markerInfoWindowPositions[0], new UDim2(0.15f, 0, 1, -1));
        if (info != null)
        {
            mapInfoWindow = new InfoWindow(parent, infoWindowPositions[1], new UDim2(0.15f, 0, 1, 0), info);
            mapInfoWindow.setBorder(false);
        }

        this.MouseButton1Click.connect(() -> mapPressed());

        infoWindowTitleFont = applet.boldFont;
        infoWindowNormalFont = applet.defaultFont;
        defaultFont = applet.defaultFont;

    }

    public void updateMap(ArrayList<Integer> listOfIDs)
    {
        markerManager.clearMarkers();
        this.listOfIDs = listOfIDs;
        this.info = this.queries.getMapDisplayInfo(listOfIDs);
        this.mapInfoWindow.updateInfo(info);
        this.mapInfoWindow.graphScreen.changeList(listOfIDs);
        markerManager.addMarkers(getMarkers(queries.getLandObjects(listOfIDs)));
        if (listOfIDs.size() == 1)
        {
            zoomingIntoAddress = true;
            mapPressed();
        }
    }

    public void draw(GuiObject self)
    {
        map.draw();
        if (parent.getClass().getName().equals("gui.primitive.Screen"))
        {
            if (applet.frame.getCurrentScreen() == parent) setActive(true);
            else setActive(false);
        }
    }

    public void setActive(boolean active)
    {
        if (active != this.active)
        {
            this.active = active;
            if (active) listen();
            else mute();
        }
    }

    public void listen()
    {
        eventDispatcher.register(map, PanMapEvent.TYPE_PAN, map.getId());
        eventDispatcher.register(map, ZoomMapEvent.TYPE_ZOOM, map.getId());
    }

    public void mute()
    {
        eventDispatcher.unregister(map, PanMapEvent.TYPE_PAN, map.getId());
        eventDispatcher.unregister(map, ZoomMapEvent.TYPE_ZOOM, map.getId());
    }

    @Override
    public void update()
    {
        super.update();
        if (mouseInside)
        {
            boolean isMouseDown = applet.mousePressed;
            if (mouseDown != isMouseDown)
            {
                mouseDown = isMouseDown;
                if (mouseDown) mouseDownAt = System.currentTimeMillis();
                else if (System.currentTimeMillis() - mouseDownAt <= CLICK_PERIOD) MouseButton1Click.trigger();
            }
        }
    }

    public void mapPressed()
    {
        if (map != null)
        {
            if (zoomingIntoAddress)
                hitMarker = (InfoMarker) markerManager.getMarkers().get(0);
            else
                hitMarker = (InfoMarker) map.getFirstHitMarker(applet.mouseX, applet.mouseY);
            if (hitMarker != null)
            {
                if (map.getZoomLevel() < FOCUS_ZOOM) map.zoomAndPanTo(FOCUS_ZOOM, hitMarker.getLocation());
                else map.panTo(hitMarker.getLocation());
                hitMarker.setSelected(true);
                markerInfoWindow.setMarker(hitMarker);
                if (markerInfoWindow.getPosition() != markerInfoWindowPositions[1])
                    markerInfoWindow.tweenPosition(markerInfoWindowPositions[0], markerInfoWindowPositions[1], 0.4f);
                if (mapInfoWindow.getPosition() != infoWindowPositions[0])
                    mapInfoWindow.tweenPosition(infoWindowPositions[1], infoWindowPositions[0], 0.4f);
                if (previousHitMarker != null & previousHitMarker != hitMarker) previousHitMarker.setSelected(false);
                previousHitMarker = hitMarker;
                zoomingIntoAddress = false;
            } else if (previousHitMarker != null)
            {
                if (previousHitMarker.isSelected())
                {
                    markerInfoWindow.tweenPosition(markerInfoWindowPositions[1], markerInfoWindowPositions[0], 0.4f);
                    mapInfoWindow.tweenPosition(infoWindowPositions[0], infoWindowPositions[1], 0.4f);
                }
                previousHitMarker.setSelected(false);
            }
        }
    }

    public ArrayList<Marker> getMarkers(Land[] lands)
    {
        ArrayList<Marker> markers = new ArrayList<>();
        ArrayList<Land> landList = new ArrayList<>(Arrays.asList(lands));
        Collections.shuffle(landList);
        for (int i = 0; i < landList.size() && i < NUMBER_OF_MARKERS; i++)
        {
            SimplePointMarker simplePointMarker = null;
            String type = landList.get(i).getPropertyType();
            if (type.equals(Land.DETACHED))
            {
                simplePointMarker = new InfoMarker(
                        new Location(landList.get(i).getLatitude(), landList.get(i).getLongitude()),
                        landList.get(i).getProperties(), map, applet.color(138, 202, 76));
            } else if (type.equals(Land.SEMI_DETACHED))
            {
                simplePointMarker = new InfoMarker(
                        new Location(landList.get(i).getLatitude(), landList.get(i).getLongitude()),
                        landList.get(i).getProperties(), map, applet.color(185, 115, 210));
            } else if (type.equals(Land.TERRACED))
            {
                simplePointMarker = new InfoMarker(
                        new Location(landList.get(i).getLatitude(), landList.get(i).getLongitude()),
                        landList.get(i).getProperties(), map, applet.color(187, 146, 55));
            } else if (type.equals(Land.FLATS_MAISONETTES))
            {
                simplePointMarker = new InfoMarker(
                        new Location(landList.get(i).getLatitude(), landList.get(i).getLongitude()),
                        landList.get(i).getProperties(), map, applet.color(226, 98, 94));
            } else if (type.equals(Land.OTHER))
            {
                simplePointMarker = new InfoMarker(
                        new Location(landList.get(i).getLatitude(), landList.get(i).getLongitude()),
                        landList.get(i).getProperties(), map, applet.color(207, 162, 70));
            }

            simplePointMarker.setStrokeColor(255);
            markers.add(simplePointMarker);
        }
        return markers;
    }

    private class InfoWindow extends Container
    {
        private InfoMarker marker;
        private HashMap<String, String> info;
        private ImageLabel image;
        private GraphScreen graphScreen;

        public InfoWindow(Parent parent, UDim2 position, UDim2 size, HashMap<String, String> info)
        {
            super(parent, position, size);
            this.info = info;
            setZIndex(9);

            this.MouseLeave.connect(() -> listen());
            this.MouseEnter.connect(() -> mute());

            if (info != null)
            {
                graphScreen = new GraphScreen(applet, queries, listOfIDs, info, applet.frame.getCurrentScreen().ID);
                applet.frame.addScreen(graphScreen);

                TextButton barChartButton = new TextButton(this, new UDim2(0.1f, 0, 0.92f, 0f),
                        new UDim2(0.2f, 0, 0.05f, 0), "");
                barChartButton.setTextSize(18);
                barChartButton.setBackgroundColor(0x2F000000);
                barChartButton.setTextColor(applet.TEXT_COLOR);
                barChartButton.setBorder(true);

                ImageLabel barchartIcon = new ImageLabel(this, new UDim2(0.13f, 0, 0.9275f, 0f),
                        new UDim2(0.145f, 0, 0.0375f, 0), histogramIcon);
                barchartIcon.setBorder(false);
                barchartIcon.setBackgroundColor(applet.TRANSPARENT);
                barchartIcon.MouseMove.disconnectAll();
                barchartIcon.MouseLeave.disconnectAll();
                barchartIcon.MouseEnter.disconnectAll();
                barchartIcon.setZIndex(8);

                barChartButton.MouseButton1Click.connect(graphScreen.toggleBarChart);

                TextButton pieChartButton = new TextButton(this, new UDim2(0.4f, 0, 0.92f, 0f),
                        new UDim2(0.2f, 0, 0.05f, 0), "");

                pieChartButton.setBackgroundColor(0x2F000000);
                pieChartButton.setTextColor(applet.TEXT_COLOR);
                pieChartButton.setBorder(true);
                pieChartButton.setTextSize(18);

                ImageLabel piechartIcon = new ImageLabel(this, new UDim2(0.43f, 0, 0.9275f, 0f),
                        new UDim2(0.145f, 0, 0.0375f, 0), pieChartIcon);
                piechartIcon.setBorder(false);
                piechartIcon.setBackgroundColor(applet.TRANSPARENT);
                piechartIcon.MouseMove.disconnectAll();
                piechartIcon.MouseLeave.disconnectAll();
                piechartIcon.MouseEnter.disconnectAll();
                piechartIcon.setZIndex(8);

                pieChartButton.MouseButton1Click.connect(graphScreen.togglePieChart);

                TextButton lineChartButton = new TextButton(this, new UDim2(0.7f, 0, 0.92f, 0f),
                        new UDim2(0.2f, 0, 0.05f, 0), "");

                lineChartButton.setBackgroundColor(0x2F000000);
                lineChartButton.setTextColor(applet.TEXT_COLOR);
                lineChartButton.setBorder(true);
                lineChartButton.setTextSize(18);
                lineChartButton.setZIndex(7);

                ImageLabel linechartIcon = new ImageLabel(this, new UDim2(0.725f, 0, 0.925f, 0f),
                        new UDim2(0.15f, 0, 0.04f, 0), linePlotIcon);
                linechartIcon.setBorder(false);
                linechartIcon.setBackgroundColor(applet.TRANSPARENT);
                linechartIcon.MouseMove.disconnectAll();
                linechartIcon.MouseLeave.disconnectAll();
                linechartIcon.MouseEnter.disconnectAll();
                linechartIcon.setZIndex(8);

                lineChartButton.MouseButton1Click.connect(graphScreen.toggleLineChart);
            } else
            {
                image = new ImageLabel(this, new UDim2(0.025f, 0, 0.005f, 0), new UDim2(0.95f, 0, 0.2f, 0), null);
                image.setBorder(false);
                image.MouseMove.disconnectAll();
                image.MouseLeave.disconnectAll();
                image.MouseEnter.disconnectAll();
                image.setZIndex(8);
            }
            setBackgroundColor(applet.BACKGROUND_COLOR);
        }

        public InfoWindow(Parent parent, UDim2 position, UDim2 size)
        {
            this(parent, position, size, null);
        }

        public void updateInfo(HashMap info)
        {
            this.info = info;
            graphScreen.changeInfo(info);
        }

        public void setMarker(InfoMarker marker)
        {
            this.marker = marker;
            String url = String.format(STREET_VIEW, marker.getLocation().getLat(), marker.getLocation().getLon());
            image.setImage(applet.loadImage(url, "jpg"));
            setBorderColor(marker.getColor());
            setBorderThickness(1);
            setDynamicBorder(false);
            setBackgroundColor(applet.BACKGROUND_COLOR);
            info = (HashMap) marker.getProperties();
        }

        @Override
        public void draw(GuiObject self)
        {
            if (info != null)
            {
                applet.textAlign(PConstants.CENTER);
                applet.textAlign(PConstants.CENTER);
                applet.fill(backgroundColor);
                if (border)
                {
                    applet.stroke(borderColor);
                    applet.strokeWeight(borderThickness);
                } else applet.noStroke();

                applet.rect(position.absoluteX(), position.absoluteY(), size.absoluteX(), size.absoluteY());
                applet.fill(applet.TEXT_COLOR);
                float x = position.absoluteX() + (size.absoluteX() / 2);
                float y = position.absoluteY();

                float division = size.absoluteY() / ((2 * info.size() + ((marker != null) ? 7 : 3)));
                if (marker != null) y += (6 * division);
                for (Object label : info.keySet())
                {
                    applet.textSize(applet.TITLE_TEXT_SIZE);
                    applet.textFont(infoWindowTitleFont);
                    y += division;
                    applet.textSize(applet.NORMAL_TEXT_SIZE - 2);
                    applet.text((String) label, x, y);
                    applet.textFont(infoWindowNormalFont);
                    y += division;
                    applet.textSize(applet.NORMAL_TEXT_SIZE - 3);
                    applet.text(info.get(label), x, y);
                }
                applet.textFont(defaultFont);
            }
        }
    }

}
