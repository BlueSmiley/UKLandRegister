package gui.primitive;

import java.util.ArrayList;

import datatypes.UDim2;
import middleware.Main;

public interface Parent {
	public float[] origin ();
	public UDim2 getSize ();
	
	public ArrayList<GuiObject> getChildren ();
	public GuiObject addChild (GuiObject child);
	public void removeChild (GuiObject child);
	public void clearAllChildren();
	public void clearAllChildren(int zIndex);
	
	public void changeZIndex (GuiObject child, int newZIndex);
	
	public Main getApplet ();
	public Screen getScreen ();
}