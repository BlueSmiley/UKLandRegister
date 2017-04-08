package middleware;

import processing.core.PApplet;
import processing.core.PFont;
import datatypes.UDim2;
import gui.events.Event.EventOperation;
import gui.input.*;
import gui.map.Map;
import gui.primitive.*;
import database.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends PApplet {
	public static final int[] SCREEN = {1920, 1080};
	
	public final Frame frame = new Frame (this);
	
	public static final String ANY = "Any";
	
	public PFont defaultFont;
	public PFont boldFont;
	public final int NORMAL_TEXT_SIZE = 20;
	public final int TITLE_TEXT_SIZE  = 36;
	public final int TEXT_COLOR		  = 0xffffffff;
	public final int BACKGROUND_COLOR = 0xa0000000;
	public final int FOREGROUND_COLOR = 0x80808080;
	public final int TRANSPARENT	  = 0x00ffffff;
	public final int POPUP_COLOR	  = 0xe0808080;
	
	public static final int MIN = 0;
	public static final int MAX = 2500000;
	
	protected Map map;
	public Queries queries = new Queries(new DBConnection());
	protected ArrayList<Integer> allIDs = queries.getAllIDs();
	
	public static void main(String[] args) {
		PApplet.main("middleware.Main");
	}

	public void settings () {
		fullScreen();
		size(SCREEN[0], SCREEN[1], P2D);
	}
	
	public void setup () {
		defaultFont = loadFont("gui/Fonts/Calibri-100.vlw");
		boldFont = loadFont("gui/Fonts/Calibri-Bold-100.vlw");
		imageMode(CORNERS);
		frameRate(60);
		
		// Creating the background screen
		Screen backgroundScreen = frame.addScreen(new Screen (this));
		frame.setBackgroundScreen(backgroundScreen);
		ArrayList<Integer> initialQuery = queries.filterPriceRange(100000, Integer.MAX_VALUE);
		map = new Map (backgroundScreen, new UDim2 (), new UDim2 (1, 0, 1, 0), queries, initialQuery);
		Container leftTrigger = new Container (backgroundScreen, new UDim2 (0.15f, 0, 1, 0));
		leftTrigger.setBorder(false);
		leftTrigger.setBackgroundColor(TRANSPARENT);
		
		// Creating the search property sale screen
		Screen userQueryScreen = frame.addScreen(new Screen(this));
		Container userQueryContainer = new Container (userQueryScreen, new UDim2 (0.5f, 0, 1, 0));
		UDim2[] userQueryContainerPositions = {
				new UDim2 (0, -(userQueryContainer.getSize().absoluteX()), 0, 0),
				new UDim2 ()
		};
		userQueryContainer.setPosition(userQueryContainerPositions[0]);
		userQueryContainer.setBorder(false);
		userQueryContainer.setBackgroundColor(BACKGROUND_COLOR);
		userQueryContainer.setClipDescendants(true);
		
		EventOperation toggleUserQueryScreen = () -> {
			boolean userQueryScreenActive = frame.getCurrentScreen() == userQueryScreen;
			int id = userQueryScreenActive ? backgroundScreen.ID : userQueryScreen.ID;
			int p = userQueryScreenActive ? 1 : 0;
			float duration = 0.2f;
			
			frame.setCurrentScreen(id);
			userQueryContainer.tweenPosition(userQueryContainerPositions[p], userQueryContainerPositions[1-p], duration);
		};
		
		leftTrigger.MouseMove.connect(toggleUserQueryScreen);
		userQueryContainer.MouseLeave.connect(toggleUserQueryScreen);
		
		float posY = 0;
		float size = 0.1f;
		TextLabel userQueryScreenTitle = new TextLabel (userQueryContainer, new UDim2 (1, 0, size, 0), "Search Property Sales");
		userQueryScreenTitle.setFont(boldFont);
		userQueryScreenTitle.setTextSize(TITLE_TEXT_SIZE);
	
		final float offset = 0.015f;
		posY += size;
		size = 0.075f;
		Container addressSearchContainer = new Container (userQueryContainer, new UDim2 (0.1f, 0, 0.1f, 0), new UDim2 (0.8f, 0, 0.075f, 0));
		addressSearchContainer.setBorder(false);
		addressSearchContainer.setBackgroundColor(TRANSPARENT);
		addressSearchContainer.setZIndex(0);
		TextLabel addressSearchLabel = new TextLabel (addressSearchContainer, new UDim2 (1, 0, 0.5f, 0), "Search by address");
		addressSearchLabel.setHorizontalAlign(LEFT);
		addressSearchLabel.setFont(boldFont);
		addressSearchLabel.setTextSize(NORMAL_TEXT_SIZE);
		AdvancedDropdown addressSearch = new AdvancedDropdown (addressSearchContainer, new UDim2 (0, 0, 0.5f, 0), new UDim2 (1, 0, 3f, 0));
		addressSearch.addOption(queries.getRandomAddresses(100));
		addressSearch.Output.connect(()->{
			String output = addressSearch.getOutput();
			map.updateMap(queries.filterByAddress(allIDs, output));
		});
		
		posY += size + offset;
		size = 0;
		Container horizontalLine = new Container (userQueryContainer, new UDim2 (0, 0, posY, 0), new UDim2 (1, 0, size, 1));
		horizontalLine.setBorder(false);
		
		posY += size + offset;
		size = 0.2f;
		Container countySearchContainer = new Container (userQueryContainer, new UDim2 (0.1f, 0, posY, 0), new UDim2 (0.8f, 0, size, 0));
		countySearchContainer.setBackgroundColor(TRANSPARENT);
		countySearchContainer.setBorder(false);
		TextLabel countySearchLabel = new TextLabel (countySearchContainer, new UDim2 (1, 0, 0.2f, 0), "Search by County");
		countySearchLabel.setFont(boldFont);
		countySearchLabel.setTextSize(NORMAL_TEXT_SIZE);
		InputList currentSelection = new InputList (countySearchContainer, new UDim2 (0.53125f, 0, 0.2f, 0), new UDim2 (0.46785f, 0, 0.8f, 0));
		currentSelection.setBorder(false);
		currentSelection.changeContentBackground(FOREGROUND_COLOR);
		AdvancedDropdown countySelect = new AdvancedDropdown (countySearchContainer, new UDim2 (0, 0, 0.2f, 0), new UDim2 (0.46785f, 0, 0.8f, 0));
		countySelect.addOption(queries.getAllCounties());
		countySelect.addOption("ALL");
		countySelect.Output.connect(()->{
			if (countySelect.getOutput().equals("ALL")) {
				for (String output: countySelect.getAllOptions()) {
					if (!output.equals("ALL") && !output.equals("B") && !output.equals("")) {
						currentSelection.addInput(output);
					}
				}
			} else
				currentSelection.addInput(countySelect.getOutput());
		});

		posY += size + offset;
		size = 0.1f;
		Container priceSearchContainer = new Container (userQueryContainer, new UDim2 (0.1f, 0, posY, 0), new UDim2 (0.8f, 0, size, 0));
		priceSearchContainer.setBackgroundColor(TRANSPARENT);
		priceSearchContainer.setBorder(false);
		TextLabel priceSearchLabel = new TextLabel (priceSearchContainer, new UDim2 (1, 0, 0.5f, 0), "Search by Price");
		priceSearchLabel.setFont(boldFont);
		priceSearchLabel.setTextSize(NORMAL_TEXT_SIZE);
		DoubleSlider priceSelection = new DoubleSlider (priceSearchContainer, new UDim2 (0, 0, 0.5f, 0), new UDim2 (1, 0, 0.1f, 0), MIN, MAX, "Price");
		TextLabel minPriceLabel = new TextLabel (priceSearchContainer, new UDim2 (0.25f, 0, 0.5f, 0), MIN + "");
		minPriceLabel.setHorizontalAlign(LEFT);
		TextLabel maxPriceLabel = new TextLabel (priceSearchContainer, new UDim2 (0.75f, 0, 0, 0), new UDim2 (0.25f, 0, 0.5f, 0), MAX + "");
		maxPriceLabel.setHorizontalAlign(RIGHT);
		TextBox minPrice = new TextBox (priceSearchContainer, new UDim2 (0, 0, 0.66f, 0), new UDim2 (0.46785f, 0, 0.35f, 0), "\\d");
		minPrice.setBackgroundColor(FOREGROUND_COLOR);
		minPrice.setText(""+MIN);
		TextBox maxPrice = new TextBox (priceSearchContainer, new UDim2 (0.53125f, 0, 0.66f, 0), new UDim2 (0.46785f, 0, 0.35f, 0), "\\d");
		maxPrice.setBackgroundColor(FOREGROUND_COLOR);
		maxPrice.setText(""+MAX);
		new Container (priceSearchContainer, new UDim2 (0.48370f, 0, 0.75f, 0), new UDim2 (0.03170f, 0, 0.1f, 0));
		minPrice.Output.connect(() -> {
			if (!minPrice.getText().equals(""))
				priceSelection.setSlider1Value(Integer.parseInt(minPrice.getText()));
		});
		maxPrice.Output.connect(() -> {
			if (!maxPrice.getText().equals(""))
				priceSelection.setSlider2Value(Integer.parseInt(maxPrice.getText()));
		});
		priceSelection.Slider1Changed.connect(() -> {
			int[] prices = priceSelection.getSliderValues();
			minPrice.setText(prices[0]+"");
		});
		priceSelection.Slider2Change.connect(() -> {
			int[] prices = priceSelection.getSliderValues();
			maxPrice.setText(prices[1]+"");
		}); 
		
		posY += size + offset;
		size = 0.075f;
		Container dateSearchContainer = new Container (userQueryContainer, new UDim2 (0.1f, 0, posY, 0), new UDim2 (0.8f, 0, size, 0));
		dateSearchContainer.setBackgroundColor(TRANSPARENT);
		dateSearchContainer.setBorder(false);
		TextLabel dateInputLabel = new TextLabel (dateSearchContainer, new UDim2 (1, 0, 0.5f, 0), "Search by Date");
		dateInputLabel.setFont(boldFont);
		dateInputLabel.setTextSize(NORMAL_TEXT_SIZE);
		TextButton earliestDateInput = new TextBox (dateSearchContainer, new UDim2 (0, 0, 0.5f, 0), new UDim2 (0.46785f, 0, 0.5f, 0), "[\\d/]");
		earliestDateInput.setBackgroundColor(FOREGROUND_COLOR);
		earliestDateInput.setTextSize(NORMAL_TEXT_SIZE);
		earliestDateInput.setFont(defaultFont);
		new Container (dateSearchContainer, new UDim2 (0.48370f, 0, 0.7f, 0), new UDim2 (0.03170f, 0, 0.1f, 0));
		TextButton latestDateInput = new TextBox (dateSearchContainer, new UDim2 (0.53125f, 0, 0.5f, 0), new UDim2 (0.46785f, 0, 0.5f, 0), "[\\d/]");
		latestDateInput.setBackgroundColor(FOREGROUND_COLOR);
		latestDateInput.setTextSize(NORMAL_TEXT_SIZE);
		latestDateInput.setFont(defaultFont);
		
		posY += size + offset;
		CheckboxList propertySearchContainer = new CheckboxList (userQueryContainer, new UDim2 (0.1f, 0, posY, 0), new UDim2 (0.375f, 0, 0.325f, 0));
		propertySearchContainer.setBorder(false);
		propertySearchContainer.setBackgroundColor(TRANSPARENT);
		String[] types = {Land.DETACHED, Land.SEMI_DETACHED, Land.TERRACED, Land.FLATS_MAISONETTES, Land.OTHER};
		for (String type: types) {
			propertySearchContainer.addOption(type);
		}
		
		CheckboxList conditionSearchContainer = new CheckboxList (userQueryContainer, new UDim2 (0.525f, 0, posY, 0), new UDim2 (0.375f, 0, 0.162f, 0));
		conditionSearchContainer.setBorder(false);
		conditionSearchContainer.setBackgroundColor(TRANSPARENT);
		for (String condition: new String[] {Land.ESTABLISHED, Land.NEWLY_BUILT}) {
			conditionSearchContainer.addOption(condition);
		}
		
		TextButton searchButton = new TextButton (userQueryContainer, new UDim2 (0.525f, 0, 0.9f, 0), new UDim2 (0.375f, 0, 0.05f, 0), "Search");
		searchButton.setTextSize(TITLE_TEXT_SIZE);
		searchButton.setFont(boldFont);
		searchButton.setBackgroundColor(FOREGROUND_COLOR);
		searchButton.MouseButton1Click.connect(()->{
			HashMap<String, Boolean> typeFilter = propertySearchContainer.getAllInputs();
			ArrayList<String> toFilterTypes = new ArrayList<>();
			for (String key: typeFilter.keySet()) {
				if (typeFilter.get(key)) {
					toFilterTypes.add(key);
				}
			}
			ArrayList<Integer> filtered = queries.filterPropertyType(toFilterTypes.toArray(new String[toFilterTypes.size()]));
			
			HashMap<String, Boolean> conditionFilter = conditionSearchContainer.getAllInputs();
			ArrayList<String> toFilterConditions = new ArrayList<>();
			for (String key: conditionFilter.keySet()) {
				if (conditionFilter.get(key)) {
					toFilterConditions.add(key);
				}
			}

			filtered = queries.filterCondition(filtered, toFilterConditions.toArray(new String[toFilterConditions.size()]));
			
			filtered = queries.filterByCounty(filtered, currentSelection.getAllInputs());
			
			int[] priceRange = priceSelection.getSliderValues();
			filtered = queries.filterPriceRange(filtered, priceRange[0], priceRange[1]);
			
			String earlyDate = queries.isValidDate(earliestDateInput.getText()) ? earliestDateInput.getText() : "01/01/1990";
			String laterDate = queries.isValidDate(latestDateInput.getText()) ? latestDateInput.getText() : "31/12/2022";
			filtered = queries.filterDate(filtered, earlyDate, laterDate);
			
			map.updateMap(filtered);
			currentSelection.clearInputs();
		});
	}
	
	public void draw () {
		background(200);
		frame.draw();
	}
}
