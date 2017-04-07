package gui.input;

import datatypes.UDim2;
import gui.primitive.GuiObject;
import gui.primitive.Parent;
import gui.primitive.TextButton;

public class Slider extends TextButton{
	protected static final int SLIDER_SIZE = 20;
	protected static final int SLIDER_OUTER_COLOUR = 0xFFBBBBBB;
	protected static final int SLIDER_INNER_COLOUR = 0xFF666666;
	protected static final int SLIDER_BAR_COLOUR = 0xEE6F6F6F;
	private float minSliderPosition;
	private float maxSliderPosition;
	protected final int MIN_VALUE;
	protected final int MAX_VALUE;
	private float sliderPosition;
	private int sliderValue;
	protected String label;
	protected boolean selected;
	protected float[] coordinates;
	private float graduation;
	
	public Slider(Parent parent, UDim2 position, UDim2 size, int minValue, int maxValue, String label)
	{
		super(parent, position, size, label);
		coordinates = getTruePosition();
		minSliderPosition = coordinates[0];
		maxSliderPosition = minSliderPosition + size.absoluteX();
		this.MIN_VALUE = minValue;
		this.MAX_VALUE = maxValue;
		this.label = label;
		sliderPosition = minSliderPosition;
		sliderValue = MIN_VALUE;
		graduation = (maxSliderPosition - minSliderPosition)/(float)(MAX_VALUE-MIN_VALUE);
	}
	
	private void moveSlider()
	{
		updatePosition();
		if (!applet.mousePressed)
			selected = false;
		
		if (selected || clickedOn())
			sliderPosition = applet.mouseX;
		
		if (sliderPosition > maxSliderPosition)
			sliderPosition = maxSliderPosition;
		else if (sliderPosition < minSliderPosition)
			sliderPosition = minSliderPosition;
		
		sliderValue = (int)Math.round(((sliderPosition - minSliderPosition)/graduation) + MIN_VALUE);
	}
	
	private void updatePosition()
	{
		float lastMinSliderPosition = minSliderPosition;
		coordinates = getTruePosition();
		minSliderPosition = coordinates[0];
		maxSliderPosition = minSliderPosition + size.absoluteX();
		sliderPosition+= minSliderPosition - lastMinSliderPosition;
		
		graduation = (maxSliderPosition - minSliderPosition)/(float)(MAX_VALUE-MIN_VALUE);
	}
	
	public boolean clickedOn()
	{
		selected = mouseInside && applet.mousePressed;
		return selected;
	}
	
	public int getSliderValue()
	{
		return sliderValue;
	}
	
	public void setSliderValue(int newValue)
	{
		if (newValue >= MIN_VALUE && newValue <= MAX_VALUE)
		{
			sliderValue = newValue;
			sliderPosition = (graduation * sliderValue) + minSliderPosition;
		}
	}
	
	public void draw(GuiObject self)
	{
		moveSlider();
		applet.noStroke();
		applet.fill(SLIDER_BAR_COLOUR);
		applet.rect(minSliderPosition, coordinates[1], size.absoluteX(), size.absoluteY(), 10, 10, 10, 10);
		
		applet.fill(SLIDER_OUTER_COLOUR);
		applet.ellipse(sliderPosition, coordinates[1] + size.absoluteY()/2, SLIDER_SIZE, SLIDER_SIZE);
		applet.fill(SLIDER_INNER_COLOUR);
		applet.ellipse(sliderPosition, coordinates[1] + size.absoluteY()/2, SLIDER_SIZE/2, SLIDER_SIZE/2);
		
		//applet.fill(0xFF111111);
		//applet.text(getSliderValue(), coordinates[0] - 50, coordinates[1] + size.absoluteY()/2);
		//applet.text(label, coordinates[0]+ size.absoluteX()/2, coordinates[1] - size.absoluteY());
	}
}
