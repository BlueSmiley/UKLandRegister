package gui.input;

import datatypes.UDim2;
import gui.primitive.GuiObject;
import gui.primitive.Parent;
import gui.primitive.TextButton;

public class CheckBox extends TextButton
{
	protected boolean clicked;
	protected int activeBackgroundColor;
	protected int inactiveBackgroundColor;
	
	public CheckBox(Parent parent, UDim2 position, UDim2 size, String text)
	{
		super(parent, position, size, text);
		clicked = false;
		MouseButton1Down.connect(()->clickReaction());
		activeBackgroundColor=0xFFAADAAA;
		inactiveBackgroundColor=getBackgroundColor();
	}
	
	protected void addClick()
	{
		clicked = true;
	}
	
	protected void removeClick()
	{
		clicked = false;
	}
	
	protected void toggleClick()
	{
		if (clicked) {
			clicked = false;
		}
		else {
			clicked = true;
		}
	}
	
	protected void clickReaction()
	{
		toggleClick();
	}
	
	public void setActiveBackgroundColor(int color)
	{
		activeBackgroundColor = color;
	}
	
	public void setInactiveBackgroundColor(int color)
	{
		inactiveBackgroundColor = color;
	}
	
	public void draw(GuiObject self)
	{
		if (clicked)
			setBackgroundColor(activeBackgroundColor);
		else
			setBackgroundColor(inactiveBackgroundColor);
		super.draw(self);
	}
	
	public boolean getValue () {
		return this.clicked; 
	}
	
}
