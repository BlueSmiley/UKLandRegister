package gui.primitive;

import datatypes.UDim2;
import gui.events.Event;

public class Container extends GuiObject {

	// Drawing properties
	protected int backgroundColor = 0xffffffff;
	
	protected boolean border 	 = true;
	protected boolean dynamicBorder = true;
	protected int inactiveBorderColor  = 0xff000000;
	protected int activeBorderColor = 0xffffffff;
	protected int borderColor = inactiveBorderColor;
	protected int borderThickness = 1;

	// Events
	protected boolean mouseInside = false;
	protected float[] lastMouse = {0,0};
	public final Event MouseEnter = new Event ();
	public final Event MouseLeave = new Event ();
	public final Event MouseMove  = new Event ();
	
	public Container (Parent parent, UDim2 position, UDim2 size) {
		super(parent, position, size);
		MouseEnter.connect(() -> borderColor = dynamicBorder ? activeBorderColor : borderColor);
		MouseLeave.connect(() -> borderColor = inactiveBorderColor);
	}

	public Container (Parent parent, UDim2 size) {
		this(parent, new UDim2 (), size);
	}
	
	@Override
	public void update () {
		if (visible) {
			super.update();
			if (!tweening && applet.frame.getNextScreenId() == -1) {
				float lowerBoundX = getTruePosition()[0];
				float upperBoundX = lowerBoundX + size.absoluteX();
				float lowerBoundY = getTruePosition()[1];
				float upperBoundY = lowerBoundY + size.absoluteY();
				
				float mouseX = applet.mouseX;
				float mouseY = applet.mouseY;
				if (mouseInside && mouseX < lowerBoundX || mouseX > upperBoundX
				 || mouseY < lowerBoundY || mouseY > upperBoundY) {
					mouseInside = false;
					MouseLeave.trigger();
				} else if (mouseInside &&mouseX != lastMouse[0] || mouseY != lastMouse[1]) {
					MouseMove.trigger();
					lastMouse = new float[] {mouseX, mouseY};
				} else if (!mouseInside && mouseX > lowerBoundX && mouseX < upperBoundX
				       && mouseY > lowerBoundY && mouseY < upperBoundY) {
					mouseInside = true;
					MouseEnter.trigger();
				}
			}
		}
	}
	
	@Override
	public void draw(GuiObject self) {
		float [] drawPosition = getTruePosition();
		
		if (border) {
			applet.stroke(borderColor);
			applet.strokeWeight(borderThickness);
		} else {
			applet.noStroke();
		}
		
		applet.fill(backgroundColor);
		applet.rect(drawPosition[0], drawPosition[1], size.absoluteX(), size.absoluteY());
	}
	
	public void setBackgroundColor (int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public int getBackgroundColor () {
		return backgroundColor;
	}
	
	
	public void setBorder (boolean border) {
		this.border = border;
	}
	
	public boolean getBorder () {
		return border;
	}
	
	public void setBorderColor (int color) {
		this.borderColor = color;
		this.inactiveBorderColor = color;
	}
	
	public int getBorderColor () {
		return borderColor;
	}
	
	public void setActiveBorderColor (int color) {
		this.activeBorderColor = color;
	}
	
	public int getActiveBorderColor () {
		return this.activeBorderColor;
	}
	
	public void setDynamicBorder (boolean dynamicBorder) {
		this.dynamicBorder = dynamicBorder;
	}
	
	public boolean getDynamicBorder () {
		return this.dynamicBorder;
	}
	
	public void setBorderThickness (int thickness) {
		this.borderThickness = thickness < 1 ? 1 : thickness;
	}
	
	public int getBorderThickness () {
		return borderThickness;
	}
}
