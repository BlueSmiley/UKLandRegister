package gui.graph;

import datatypes.UDim2;
import gui.primitive.GuiObject;
import gui.primitive.Parent;
import processing.core.PApplet;
import processing.core.PFont;

public class PieChart extends Graphs
{
	private float sum;
	private float centerX;
	private float centerY;
	private int[] pieColors;
	private float[] arcValues;
	private float diameter;
	private float labelWidth, labelHeight;
	private int fontSize;
	private String[] labels;
	private boolean tweening;
	private boolean completedDraw, startedDraw;
	private int textColor;
	private int labelBackgroundColor;
	private boolean[] completedArc;
	private PFont labelFont, titleFont;

	public PieChart(Parent parent, UDim2 position, UDim2 size, int[] values, float labelWidth, String[] labels,
			String title)
	{
		super(parent, position, size, values, title);
		this.labels = labels;
		this.labelWidth = labelWidth;
		this.labelHeight = getHeight() / values.length;
		this.diameter = getHeight();
		this.values = values;
		tweening = false;
		completedDraw = false;
		startedDraw = false;
		sum = 0;
		for(float height : values)
			sum += height;

		centerX = getX() + diameter / 2 + labelWidth;
		centerY = getY() + diameter / 2;
		setBackgroundColor(0xFFFFFFFF);
		pieColors = new int[values.length];
		arcValues = new float[values.length];
		completedArc = new boolean[values.length];
		for(int i = 0; i < values.length; i++)
		{
			arcValues[i] = 0;
			completedArc[i] = false;
		}
		this.labelBackgroundColor = 0x0F000000;
		this.fontSize = 14;
		this.textColor = 0xFFFFFFFF;
		fontSize = fitFont(fontSize, findLargestLabel(labels), ((labelWidth - 10) / 2) * labelHeight);
		titleFont = applet.createFont("Calibri Bold", 20);
		labelFont = applet.createFont("Calibri Bold", fontSize);
		useLinearColours();
	}

	public PieChart(Parent parent, UDim2 position, UDim2 size, int[] values, float labelWidth, String[] labels)
	{
		this(parent, position, size, values, labelWidth, labels, "");
	}

	public void useLinearColours()
	{
		for(int i = 0; i < values.length; i++)
		{
			int redC = (int) PApplet.map(i, 0, values.length-1, 255, 0);
			int greenC = (int) PApplet.map(i, 0, values.length-1, 255, 0);
			int blueC = (int) PApplet.map(i, 0, values.length-1, 0, 255);
			pieColors[i] = applet.color(redC, greenC, blueC);
		}
	}

	public void useRandomColours()
	{
		for(int i = 0; i < values.length; i++)
		{
			pieColors[i] = applet.color(applet.random(256), applet.random(256), applet.random(256));
		}
	}

	public int[] getPieColors()
	{
		return this.pieColors;
	}

	public void setPieColors(int[] colors)
	{
		if(pieColors.length == colors.length) this.pieColors = colors;
	}

	public void setTextColor(int color)
	{
		this.textColor = color;
	}

	public void setLabelBackgroundColor(int color)
	{
		this.labelBackgroundColor = color;
	}

	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}

	public void displayPie()
	{
		float offset = 0;
		if(startedDraw || !tweening || completedDraw)
		{
			startedDraw = true;
			for(int i = 0; i < values.length; i++)
			{
				applet.fill(pieColors[i]);
				float maxThisArc = (values[i] / sum) * PApplet.TWO_PI;

				float maxLastArc = 0;
				if(i != 0)
				{
					maxLastArc = (values[i - 1] / sum) * PApplet.TWO_PI;
				}

				if(i == 0) // only take into account whether arc is max
					completedArc[i] = arcValues[i] == maxThisArc;
				else 	// take into account whether last arc is complete too
					completedArc[i] = completedArc[i - 1] && arcValues[i] == maxThisArc;

				if(i == 0) // always draw first arc
				{
					applet.arc(centerX, centerY, diameter, diameter, offset, offset + currentArc(i), PApplet.PIE);
				}
				else if(arcValues[i - 1] == maxLastArc && completedArc[i - 1]) //draw arc once last one has completed
				{
					applet.arc(centerX, centerY, diameter, diameter, offset, offset + currentArc(i), PApplet.PIE);
				}
				offset += maxThisArc;
			}
		}
		completedDraw = completedArc[values.length - 1];
	}

	public void displayText()
	{
		applet.noStroke();
		applet.fill(labelBackgroundColor);
		applet.rect(getX(), getY(), labelWidth - 10, getHeight());
		applet.textSize(fontSize * 2);
		applet.fill(textColor);
		applet.textFont(titleFont);
		applet.text(title, centerX, centerY - diameter / 2 - 50);
		applet.textSize(fontSize);
		applet.textFont(labelFont);
		for(int i = 0; i < pieColors.length && i < labels.length; i++)
		{
			applet.fill(textColor);
			applet.text(labels[i], getX(), getY() + i * labelHeight, (labelWidth - 10) / 2, labelHeight);
			applet.fill(pieColors[i]);
			applet.rect(getX() + (labelWidth / 2), getY() + i * labelHeight + labelHeight / 4, (labelWidth / 2) - 20,
					labelHeight / 2);
		}
	}

	private float currentArc(int i)
	{
		float maxArc = (values[i] / sum) * PApplet.TWO_PI;

		if(arcValues[i] < maxArc) 
			arcValues[i] += 1f / PApplet.PI;
		if(arcValues[i] > maxArc) 
			arcValues[i] = maxArc;
		return arcValues[i];
	}

	public void reset()
	{
		startedDraw = false;
		completedDraw = false;
		for(int i = 0; i < arcValues.length; i++)
		{
			arcValues[i] = 0;
			completedArc[i] = false;
		}
	}

	private void updatePosition()
	{
		float lastCenterX = centerX;
		float lastCenterY = centerY;

		centerX = getX() + diameter / 2 + labelWidth;
		centerY = getY() + diameter / 2;

		if(lastCenterX != centerX || lastCenterY != centerY) 
			tweening = true;
		else 
		 	tweening = false;
	}

	public void draw(GuiObject self)
	{
		if(visible)
		{
			updatePosition();
			applet.stroke(255);
			displayText();
			displayPie();
		}
	}
	
	public int[] getValues()
	{
		return values;
	}
	
	public boolean isTweening()
	{
		return tweening;
	}
	
	public boolean completedDrawing()
	{
		return completedDraw;
	}
	
	public String[] getLabels()
	{
		return labels;
	}
	
	public float[] getCenter()
	{
		return new float[] {centerX, centerY};
	}
}

