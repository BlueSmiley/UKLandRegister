package gui.primitive;

import java.util.ArrayList;
import java.util.HashMap;

import datatypes.UDim2;
import middleware.Main;

public class Screen implements Parent {
	private static final float[] ORIGIN = {0f, 0f};
	private static int globalId = 0;
	public final int ID;
	
	private Main applet;
	private float[][] clippingRegion = {{0,0},{Main.SCREEN[0],Main.SCREEN[1]}};
	private HashMap<Integer, ArrayList<GuiObject>> children = new HashMap<Integer, ArrayList<GuiObject>> ();
	
	private boolean canBeChanged = true;
	
	public Screen (Main applet) {
		clearAllChildren();
		
		this.applet = applet;
		ID = globalId++;
	}
	
	public void draw () {
		for (int z = children.size() - 1; z >= 0; z--) {
			for (GuiObject child: children.get(z)) {
				child.draw();
			}
		}
	}
	
	public void update () {
		for (int z = children.size() - 1; z >= 0; z--) {
			for (GuiObject child: children.get(z)) {
				child.update();
			}
		}
	}

	public boolean canBeChanged () {
		return canBeChanged;
	}
	
	public void setCanBeChanged(boolean canBeChanged) {
		this.canBeChanged = canBeChanged;
	}
	
	public void setClippingRegion (float[][] region) {
		this.clippingRegion = region;
	}
	
	public float[][] getClippingRegion () {
		return clippingRegion;
	}
	
	@Override
	public float[] origin() {
		return ORIGIN;
	}
	
	@Override
	public ArrayList<GuiObject> getChildren() {
		ArrayList<GuiObject> allChildren = new ArrayList<GuiObject> ();
		
		for (int z = 0; z < children.size(); z++) {
			allChildren.addAll(allChildren.size(), children.get(z));
		}
		
		return allChildren;
	}
	
	@Override
	public GuiObject addChild (GuiObject child) {
		children.get(child.getZIndex()).add(child);
		return child;
	}
	
	@Override
	public void removeChild (GuiObject child) {
		children.get(child.getZIndex()).remove(child);
	}
	
	@Override 
	public void clearAllChildren () {
		for (int i = 0; i < GuiObject.ZLEVELS; i++) {
			children.put(i,  new ArrayList<GuiObject> ());
		}
	}
	
	@Override
	public void clearAllChildren (int zIndex) {
		children.put(zIndex, new ArrayList<GuiObject> ());
	}
	
	@Override
	public Main getApplet () {
		return applet;
	}

	@Override
	public Screen getScreen () {
		return this;
	}
	
	@Override
	public void changeZIndex (GuiObject child, int newZIndex) {
		children.get(child.getZIndex()).remove(child);
		children.get(newZIndex).add(child);
	}
	
	@Override
	public UDim2 getSize() {
		return new UDim2 (0f, (float) middleware.Main.SCREEN[0], 0f, (float) middleware.Main.SCREEN[1]);
	}
}
