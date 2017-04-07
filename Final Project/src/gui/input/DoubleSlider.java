package gui.input;

import datatypes.UDim2;
import gui.events.Event;
import gui.primitive.GuiObject;
import gui.primitive.Parent;

public class DoubleSlider extends Slider {
	
	private float minSlider1Position;
	private float maxSlider1Position;
	private float minSlider2Position;
	private float maxSlider2Position;
	private float slider1Position;
	private float slider2Position;
	private boolean slider1Selected;
	private boolean slider2Selected;
	private float graduation;
	private int slider1Value;
	private int slider2Value;
	
	public final Event Slider1Changed = new Event ();
	public final Event Slider2Change = new Event();
	
	public DoubleSlider(Parent parent, UDim2 position, UDim2 size, int minValue, int maxValue, String label)
	{
		super(parent, position, size, minValue, maxValue, label);
		minSlider1Position = coordinates[0];
		slider1Position = minSlider1Position;
		slider2Position = minSlider1Position + size.absoluteX();
		
		maxSlider1Position = slider2Position;
		minSlider2Position = slider1Position;
		maxSlider2Position = minSlider1Position + size.absoluteX();
		slider1Value = MIN_VALUE;
		slider2Value = MAX_VALUE;
		
		graduation = (maxSlider2Position - minSlider1Position)/(float)(MAX_VALUE-MIN_VALUE);
	}
	
	private void updatePosition()
	{
		float lastMinSlider1Position = minSlider1Position;
		coordinates = getTruePosition();
		minSlider1Position = coordinates[0];
		slider1Position+= minSlider1Position - lastMinSlider1Position;
		slider2Position+= minSlider1Position - lastMinSlider1Position;
		
		maxSlider1Position = slider2Position;
		minSlider2Position = slider1Position;
		maxSlider2Position = minSlider1Position + size.absoluteX();
		
		graduation = (maxSlider2Position - minSlider1Position)/(float)(MAX_VALUE-MIN_VALUE);
	}
	
	private void moveSliders()
	{
		updatePosition();
		if (!applet.mousePressed)
		{
			slider1Selected = false;
			slider2Selected = false;
		}
		
		if (!slider2Selected && (slider1Selected || slider1ClickedOn())) {
			slider1Position = applet.mouseX;
			Slider1Changed.trigger();
		}
		if (!slider1Selected && (slider2Selected || slider2ClickedOn())) {
			slider2Position = applet.mouseX;
			Slider2Change.trigger();
		}
		
		if (slider1Position > maxSlider1Position)
			slider1Position = maxSlider1Position;
		else if (slider1Position < minSlider1Position)
			slider1Position = minSlider1Position;
		
		if (slider2Position > maxSlider2Position)
			slider2Position = maxSlider2Position;
		else if (slider2Position < minSlider2Position)
			slider2Position = minSlider2Position;
		
		slider1Value = (int)Math.round(((slider1Position - minSlider1Position)/graduation) + MIN_VALUE);
		slider2Value = (int)Math.round(((slider2Position - minSlider1Position)/graduation) + MIN_VALUE);
	}
	
	private boolean slider1ClickedOn()
	{
		boolean mouseInside = Math.sqrt(Math.pow(applet.mouseX - slider1Position, 2) 
				+ Math.pow(applet.mouseY - coordinates[1] + size.absoluteY()/2, 2)) < 15;
		slider1Selected = mouseInside && applet.mousePressed && applet.mouseX < maxSlider1Position;
		return slider1Selected;
	}
	
	private boolean slider2ClickedOn()
	{
		boolean mouseInside = Math.sqrt(Math.pow(applet.mouseX - slider2Position, 2) 
				+ Math.pow(applet.mouseY - coordinates[1] + size.absoluteY()/2, 2)) < 15;
		slider2Selected = mouseInside && applet.mousePressed && applet.mouseX > minSlider2Position;
		return slider2Selected;
	}
	
	public int[] getSliderValues()
	{
		return new int[] {slider1Value, slider2Value};
	}
	
	public void setSlider1Value(int newValue)
	{
		slider1Value = newValue;
		if (slider1Value > MAX_VALUE)
			slider1Value = MAX_VALUE;
		else if (slider1Value < MIN_VALUE)
			slider1Value = MIN_VALUE;
		slider1Position = (graduation * newValue) + minSlider1Position;
	}
	
	public void setSlider2Value(int newValue)
	{
		slider2Value = newValue;
		if (slider2Value > MAX_VALUE)
			slider2Value = MAX_VALUE;
		else if (slider2Value < MIN_VALUE)
			slider2Value = MIN_VALUE;
		slider2Position = (graduation * newValue) + minSlider1Position;
	}
	
	public void setSliderValue(int newValue){}
	
	public void draw(GuiObject self)
	{
		moveSliders();
		applet.noStroke();
		applet.fill(SLIDER_BAR_COLOUR);
		applet.rect(minSlider1Position, coordinates[1], size.absoluteX(), size.absoluteY(), 10, 10, 10, 10);
		
		applet.fill(SLIDER_OUTER_COLOUR);
		applet.ellipse(slider1Position, coordinates[1] + size.absoluteY()/2, SLIDER_SIZE, SLIDER_SIZE);
		applet.fill(SLIDER_INNER_COLOUR);
		applet.ellipse(slider1Position, coordinates[1] + size.absoluteY()/2, SLIDER_SIZE/2, SLIDER_SIZE/2);
		applet.fill(SLIDER_OUTER_COLOUR);
		applet.ellipse(slider2Position, coordinates[1] + size.absoluteY()/2, SLIDER_SIZE, SLIDER_SIZE);
		applet.fill(SLIDER_INNER_COLOUR);
		applet.ellipse(slider2Position, coordinates[1] + size.absoluteY()/2, SLIDER_SIZE/2, SLIDER_SIZE/2);
		
		//applet.fill(0xFF111111);
		//applet.text(getSliderValues()[0], coordinates[0] - 50, coordinates[1] + size.absoluteY()/2);
		//applet.text(getSliderValues()[1], coordinates[0] + size.absoluteX() + 50, coordinates[1] + size.absoluteY()/2);
		//applet.text(label, coordinates[0]+ size.absoluteX()/2, coordinates[1] - size.absoluteY());
	}
}
