package gui.primitive;

import java.util.HashMap;

import processing.core.PApplet;

public class Frame {
	private HashMap<Integer, Screen> screens = new HashMap <Integer, Screen> ();
	private Screen currentScreen;
	private int nextScreen = -1;
	private Screen backgroundScreen;
	private int backgroundColor = 0xff808080;
	
	private PApplet applet;
	
	public Frame (PApplet applet) {
		this.applet = applet;
	}
	
	public void draw () {
		if (nextScreen != -1) {
			if (currentScreen.canBeChanged()) {
				currentScreen = screens.get(nextScreen);
				nextScreen = -1;
			}
		}
		
		if (backgroundScreen == currentScreen && backgroundScreen != null) {
			backgroundScreen.update();
		} else {
			if (backgroundScreen != null) {
				backgroundScreen.draw();
			} else {
				applet.background(backgroundColor);
			}
			currentScreen.update();
		}
	}
	
	public Screen addScreen (Screen screen) {
		screens.put(screen.ID, screen);
		if (currentScreen == null)
			currentScreen = screen;
		return screen;
	}
	
	public void setCurrentScreen (int id) {
		nextScreen = id;
	}
	
	public Screen getCurrentScreen () {
		return currentScreen;
	}
	
	public Screen setBackgroundScreen (Screen screen) {
		return backgroundScreen = addScreen(screen);
	}
	
	public void setBackgroundColor (int color) {
		this.backgroundColor = color;
	}
	
	public int getBackgroundColor () {
		return this.backgroundColor;
	}
	
	public int getNextScreenId () {
		return nextScreen;
	}
}