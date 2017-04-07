package gui.graph;

import java.text.DecimalFormat;

import datatypes.UDim2;
import gui.primitive.GuiObject;
import gui.primitive.Parent;
import processing.core.PApplet;
import processing.core.PFont;

public class Histograph extends Graphs
{
	private static final float ANIMATION_TIME = .5f;
	int[] barColors;
	float[] barHeights;
	float barWidth;
	float barHeightRatio;
	float labelSize;
	String[] leftLabels;
	String[] bottomLabels;

	int titleFontSize;
	int bottomLabelFontSize;
	int leftLabelFontSize;
	int colour;
	
	float maxValue;
	private int textColor;
	boolean defaultOrientation;
	private boolean startedDraw;
	private boolean tweening;
	private float[] truePosition;
	
	private String xLabel, yLabel;
	private PFont leftLabelFont;
	private PFont bottomLabelFont;
	private PFont titleFont;
	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#");

	public Histograph(Parent parent, UDim2 position, UDim2 size, int[] values, float labelSize, String[] bottomLabels,
			String title, int colour, String xLabel, String yLabel)
	{
		super(parent, position, size, values, title);
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.truePosition = getTruePosition();
		startedDraw = false;
		this.colour = colour;
		this.labelSize = labelSize;
		this.bottomLabels = bottomLabels;
		barWidth = (getWidth() - labelSize) / (this.values.length);
		this.maxValue = 0;
		//use floats or double maxValue for double precision during division in graduateValues function later
		for(float value : values)
		{
			if(value > this.maxValue) maxValue = value;
		}
		this.barHeightRatio = ((getHeight() - 2 * labelSize) / maxValue);
		barColors = new int[this.values.length];
		barHeights = new float[values.length];

		setBarColors(colour);
		resetBarHeights();
		graduateValues();

		//default values
		titleFontSize = 20;
		leftLabelFontSize = 16;
		bottomLabelFontSize = 16;

		titleFontSize = fitFont(titleFontSize, title, (getWidth() - labelSize) * labelSize/3);
		leftLabelFontSize = fitFont(leftLabelFontSize, findLargestLabel(leftLabels),
				((getHeight() - 2 * labelSize) / leftLabels.length) * (labelSize*2/3));
		bottomLabelFontSize = fitFont(bottomLabelFontSize, findLargestLabel(bottomLabels), 
				barWidth * (labelSize*2/3));
		textColor = 0xFF000000;
		defaultOrientation=true;
		//P2d renderer so create all fonts before for clean text
		leftLabelFont = applet.createFont("Calibri Bold", leftLabelFontSize);
		bottomLabelFont = applet.createFont("Calibri Bold", bottomLabelFontSize);
		titleFont = applet.createFont("Calibri Bold", titleFontSize);
		
	}
	
	public Histograph(Parent parent, UDim2 position, UDim2 size, int[] values, float labelSize, String[] bottomLabels,
			String title, int colour)
	{
		this(parent, position, size, values, labelSize, bottomLabels, title, colour, "", "");
	}
	
	public void setLeftLabelFont(PFont font)
	{
		this.leftLabelFont=font;
	}
	
	public void setBottomLabelFont(PFont font)
	{
		this.bottomLabelFont=font;
	}
	
	public void setTitleFont(PFont font)
	{
		this.titleFont=font;
	}

	public void alternateOrientation()
	{
		defaultOrientation=false;
	}
	
	public void defaultOrientation()
	{
		defaultOrientation=true;
	}
	
	public int[] getValues()
	{
		return this.values;
	}

	public int[] getBarColors()
	{
		return barColors;
	}

	public int getNumberOfBars()
	{
		return this.values.length;
	}

	public float getMaxValue()
	{
		return maxValue;
	}

	@Deprecated //use reset barheights instead
	public void reset()
	{
		for(int i = 0; i < barHeights.length; i++)
		{
			barHeights[i] = 0;
		}
	}

	public void displayBars()
	{	
		if (truePosition[0] != getTruePosition()[0] || truePosition[1] != getTruePosition()[1])
		{
			tweening = true;
			updatePosition();
		}
		else
			tweening = false;
		
		if (startedDraw || !tweening)
		{
			startedDraw = true;
			for(int i = 0; i < values.length; i++)
			{
				applet.fill(barColors[i]);
				float height = thisBarHeight(i);
				applet.rect((getX() + labelSize) + i * barWidth,
						(getY() + labelSize) + (getHeight() - 2 * labelSize) - height, barWidth, height);
			}
		}
	}

	private float thisBarHeight(int barIndex)
	{
		double maxHeight = values[barIndex] * barHeightRatio;//getY() + (getHeight() - values[barIndex]*barHeightRatio);
		if(barHeights[barIndex] < maxHeight)
		{
			barHeights[barIndex] += maxHeight / (applet.frameRate*ANIMATION_TIME);
		}
		if(barHeights[barIndex] > (getHeight() - 2 * labelSize)) 
			barHeights[barIndex] = (getHeight() - 2 * labelSize);
		//System.out.println(maxHeight + ":" + getHeight() + ":" + barHeights[barIndex]);
		return barHeights[barIndex];
	}

	private final void graduateValues()    //used in constructor only so final
	{
		leftLabels = new String[10];
		for(int i = 0; i < leftLabels.length; i++)
		{
			leftLabels[i] =NUMBER_FORMAT.format((maxValue - (maxValue / (leftLabels.length - 1)) * i)) + "";
		}
	}

	public final void setBarColors(int newColour)
	{
		int[] shadesOfColour = {this.colour, brightenColour(this.colour)};
		for(int i = 0, shadeI = 0; i < values.length; i++, shadeI++)
		{
			if (shadeI >= shadesOfColour.length)
				shadeI -= shadesOfColour.length;
			barColors[i] = shadesOfColour[shadeI];
		}
	}
	
	public final void resetBarHeights()
	{
		startedDraw = false;
		for(int i = 0; i < values.length; i++)
		{
			barHeights[i] = 0;
		}
	}

	public final void setTextColor(int color)
	{
		this.textColor = color;
	}
	
	private void updatePosition()
	{
		truePosition = getTruePosition();
	}

	@Override public void draw(GuiObject self)
	{
		if(visible)
		{
			applet.fill(getBackgroundColor());
			applet.stroke(0);
			applet.rect(super.getX() + labelSize, super.getY() + labelSize, super.getWidth() - labelSize,
					super.getHeight() - 2 * labelSize);
			displayBars();

			applet.fill(textColor);
			//applet.textFont(font);
			//applet.textSize(bottomLabelFontSize);
			applet.textFont(bottomLabelFont);
			if(defaultOrientation)
			{
				applet.pushMatrix();
				applet.translate(getX() + labelSize + barWidth / 2 + (applet.textDescent() + applet.textDescent())/2,
						getY() + getHeight()-labelSize/3);
				int paddingRequired = (int) (((labelSize*2/3) - applet.textWidth(findLargestLabel(bottomLabels))) / paddingLength);
				for(int i = 0; i < bottomLabels.length; i++)
				{
					applet.rotate(PApplet.HALF_PI*3);
					String label = bottomLabels[i];
					if(paddingRequired > 0) 
						label = String.format("%" + - paddingRequired + "s", label);
					applet.text(label, 0, - barWidth / 2, (labelSize*2/3), barWidth);
					applet.rotate(- PApplet.HALF_PI*3);
					applet.translate((barWidth), 0);
				}
				applet.popMatrix();
			}
			else
			{
				displayTextHorizontally(bottomLabels,new UDim2(0,labelSize,0,getHeight()-labelSize),
						new UDim2(0,getWidth()-labelSize,0,labelSize));
			}

			//applet.textSize(leftLabelFontSize);
			applet.textFont(leftLabelFont);
			for(int j = 0; j < leftLabels.length; j++)
			{
				String label = leftLabels[j];
				int paddingRequired = (int) (((labelSize*2/3) - applet.textWidth(label)) / paddingLength);
				if(paddingRequired > 0) 
					label = String.format("%" + paddingRequired + "s", label);

				applet.text(label, 
						getX()+(labelSize/3),
						getY() + labelSize + ((getHeight() - 2 * labelSize) / (leftLabels.length - 1)) * j
							- ((getHeight() - 2 * labelSize) / (leftLabels.length - 1)) / 2, 
						(labelSize*2/3),
						(getHeight() - 2 * labelSize) / leftLabels.length);
			}

			//applet.textSize(titleFontSize);
			applet.textFont(titleFont);
			applet.text(title, getX() + labelSize, getY(), getWidth() - labelSize, labelSize);
			
			applet.text(xLabel, getX(),getY() + getHeight()-labelSize/3,getWidth(),labelSize/3);
			
			applet.pushMatrix();
			applet.translate(getX(),getY()+getHeight());
			applet.rotate(PApplet.HALF_PI*3);
			applet.text(yLabel, 0, 0,getHeight(),labelSize/3);
			applet.rotate(-PApplet.HALF_PI*3);
			applet.popMatrix();
		}
	}
}

