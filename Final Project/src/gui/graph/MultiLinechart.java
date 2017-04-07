package gui.graph;

import java.text.DecimalFormat;

import datatypes.UDim2;
import gui.primitive.Container;
import gui.primitive.GuiObject;
import gui.primitive.Parent;
import processing.core.PApplet;
import processing.core.PFont;
//Testing RIP chart :P
public class MultiLinechart extends Graphs 
{
	private Key chartKey;
	private int[] lineColors;
	
	private static final float ANIMATION_TIME = 2.5f;
	private static final int STROKE_SIZE = 2;

	float lineWidth;
	float lineHeightRatio;
	float labelSize;
	String[] leftLabels;
	String[] bottomLabels;
	int titleFontSize;
	int bottomLabelFontSize;
	int leftLabelFontSize;
	int textColor;
	int lineColor;
	int pointColor;
	float maxValue;
	float[] lineFraction;
	private float growthPerFrame;
	private String xLabel, yLabel;
	private int pointSize;
	private int[][] values;

	private PFont leftLabelFont;
	private PFont bottomLabelFont;
	private PFont titleFont;
	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#");
	
	public MultiLinechart(Parent parent, UDim2 position, UDim2 size, float labelSize, String[] bottomLabels,
			String title, String xLabel, String yLabel,String[] keyLabels,int[] ...values)
	{
		super(parent, position, size, new int[0], title);
		lineColors = new int[values.length];
		chartKey = new Key(this,position,new UDim2(.25f,0f,.9f,0),keyLabels,lineColors);
		
		this.labelSize = labelSize;
		this.bottomLabels = bottomLabels;
		this.title = title;
		this.values = values;
		this.pointSize = 7;
		useLinearColours();
		
		lineFraction = new float[values[0].length - 1];
		for(int i = 0; i < lineFraction.length; i++)
		{
			lineFraction[i] = 0;
		}
		float totalFrames = ANIMATION_TIME * applet.frameRate;
		float framesPerSegment = totalFrames / values[0].length;
		growthPerFrame = 1 / framesPerSegment;

		lineWidth = (size.absoluteX()/2 - labelSize) / (this.values[0].length - 1);
		this.maxValue = 0;
		//use floats or double maxValue for double precision during division in graduateValues function later
		for(int[] line:values)
		{
			for(float value : line)
			{
				if(value > this.maxValue) maxValue = value;
			}
		}
		
		this.lineHeightRatio = ((size.absoluteY() - 2 * labelSize - 10) / maxValue);

		//default values
		titleFontSize = 20;
		leftLabelFontSize = 16;
		bottomLabelFontSize = 16;
		textColor = 0;
		lineColor = 0;
		pointColor = 0;

		titleFontSize = fitFont(titleFontSize, title, (size.absoluteX() - labelSize) * labelSize / 3);
		graduateValues(10);
		leftLabelFontSize = fitFont(leftLabelFontSize, findLargestLabel(leftLabels),
				((size.absoluteY() - 2 * labelSize) / leftLabels.length) * (labelSize * 2 / 3));
		bottomLabelFontSize = fitFont(bottomLabelFontSize, findLargestLabel(bottomLabels),
				lineWidth * (labelSize * 2 / 3));
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		//P2d renderer so create all fonts before for clean text
		leftLabelFont = applet.createFont("Calibri Bold", leftLabelFontSize);
		bottomLabelFont = applet.createFont("Calibri Bold", bottomLabelFontSize);
		titleFont = applet.createFont("Calibri Bold", titleFontSize);
		
	}
	
	public void draw(GuiObject self)
	{
		float[] ordinates = getTruePosition();
		applet.fill(backgroundColor);
		applet.stroke(0);
		applet.rect(ordinates[0] + labelSize+size.absoluteX()/2,
				ordinates[1] + labelSize, 
				(size.absoluteX()/2) - labelSize,
				size.absoluteY() - (2 * labelSize));
		displayText();
		displayLines();
	}
	
	public void displayText()
	{
		float[] ordinates = getTruePosition();
		//processing.textSize(bottomLabelFontSize);
		applet.textFont(bottomLabelFont);
		applet.fill(textColor);
		applet.pushMatrix();
		applet.translate(
				ordinates[0] +size.absoluteX()/2+ labelSize - lineWidth / 2 + 
					(applet.textDescent() + applet.textDescent()) / 2,
				ordinates[1] + size.absoluteY() - labelSize / 3);
		int paddingRequired = (int) (((labelSize * 2 / 3) - applet.textWidth(findLargestLabel(bottomLabels)))
				/ paddingLength);
		for(int i = 0; i < bottomLabels.length; i++)
		{
			applet.rotate(PApplet.HALF_PI * 3);
			String label = bottomLabels[i];
			if(paddingRequired > 0) label = String.format("%" + - paddingRequired + "s", label);
			applet.text(label, 0, 0, (labelSize * 2 / 3), lineWidth);
			applet.rotate(- PApplet.HALF_PI * 3);
			applet.translate((lineWidth), 0);
		}
		applet.popMatrix();

		//processing.textSize(leftLabelFontSize);
		applet.textFont(leftLabelFont);
		for(int j = 0; j < leftLabels.length; j++)
		{
			String label = leftLabels[j];
			int leftPaddingRequired = (int) (((labelSize * 2 / 3) - applet.textWidth(label)) / paddingLength);
			if(paddingRequired > 0) label = String.format("%" + leftPaddingRequired + "s", label);

			applet.text(label, ordinates[0] + labelSize / 3 +size.absoluteX()/2,
					ordinates[1] + labelSize + ((getHeight() - 2 * labelSize) / (leftLabels.length - 1)) * j
							- ((getHeight() - 2 * labelSize) / (leftLabels.length - 1)) / 2, (labelSize * 2 / 3),
					(size.absoluteX() - 2 * labelSize) / leftLabels.length);
		}

		//processing.textSize(titleFontSize);
		applet.textFont(titleFont);
		applet.text(title, ordinates[0] + labelSize +size.absoluteX()/2,
				ordinates[1],
				size.absoluteX()/2 - labelSize,
				labelSize);

		applet.text(xLabel, 
				ordinates[0] +size.absoluteX()/2+labelSize, 
				ordinates[1] + getHeight() - labelSize / 3, 
				size.absoluteX()/2 - labelSize, 
				labelSize / 3);

		applet.pushMatrix();
		applet.translate(getX() +size.absoluteX()/2, getY() + getHeight());
		applet.rotate(PApplet.HALF_PI * 3);
		applet.text(yLabel, 0, 0, getHeight(), labelSize / 3);
		applet.rotate(- PApplet.HALF_PI * 3);
		applet.popMatrix();
	}
	
	public void displayLines()
	{
		applet.stroke(lineColor);
		applet.strokeWeight(STROKE_SIZE);
		float[] ordinates = getTruePosition();
		for(int j=0;j<values.length;j++)
		{
			applet.fill(lineColors[j]);
			applet.stroke(lineColors[j]);
			for(int i = - 1; i < values[j].length - 1; i++)
			{
				if(i >= 0)
				{
					if(i == 0) applet.ellipse((ordinates[0] + labelSize) + (i) * lineWidth+
								size.absoluteX()/2,
							ordinates[1] + (size.absoluteY() - 
									(values[j][i] * lineHeightRatio) - labelSize) - pointSize,
							pointSize, 
							pointSize);
					if(i == 0 || lineFraction[i - 1] == 1)
					{
						applet.line((ordinates[0] + labelSize) + (i) * lineWidth +size.absoluteX()/2,
								ordinates[1] + (size.absoluteY() - (values[j][i] * lineHeightRatio) - labelSize) - pointSize,
								(ordinates[0] + labelSize) + (i) * lineWidth + (lineWidth * (float) lineFraction[i])+size.absoluteX()/2,
								ordinates[1] + (size.absoluteY() - labelSize - (values[j][i] * lineHeightRatio)
										- (values[j][i + 1] - values[j][i]) * lineHeightRatio * (float) lineFraction[i])
										- pointSize);
						if(lineFraction[i] < 1)
						{
							lineFraction[i] += growthPerFrame;
							if(lineFraction[i] > 1) lineFraction[i] = 1;
						}
						else applet.ellipse((ordinates[0] + labelSize) + (i + 1) * lineWidth + size.absoluteX()/2,
								ordinates[1] + (size.absoluteY() - (values[j][i + 1] * lineHeightRatio) - labelSize)
								- pointSize, 
								pointSize, 
								pointSize);

					}
				}

			}
		}
	}
	
	public void useLinearColours()
	{
		
		for(int i = 0; i < values.length; i++)
		{
			int redC = (int) PApplet.map(i, 0, values.length-1, 255, 0);
			int greenC = (int) PApplet.map(i, 0, values.length-1, 255, 0);
			int blueC = (int) PApplet.map(i, 0, values.length-1, 0, 255);
			lineColors[i] = applet.color(redC, greenC, blueC);
		}
	}
	
	public void setBottomLabelFontSize(int bottomLabelFontSize)
	{
		this.bottomLabelFontSize = bottomLabelFontSize;
	}

	public void setLeftLabelFontSize(int leftLabelFontSize)
	{
		this.leftLabelFontSize = leftLabelFontSize;
	}

	public void setTextColor(int textColor)
	{
		this.textColor = textColor;
	}

	public void setLineColor(int lineColor)
	{
		this.lineColor = lineColor;
	}

	public void setPointColor(int pointColor)
	{
		this.pointColor = pointColor;
	}
	
	public void setLeftLabelFont(PFont font)
	{
		this.leftLabelFont = font;
	}

	public void setBottomLabelFont(PFont font)
	{
		this.bottomLabelFont = font;
	}

	public void setTitleFont(PFont font)
	{
		this.titleFont = font;
	}

	private final void graduateValues(int graduations)    //used in constructor only so final
	{
		leftLabels = new String[graduations];
		for(int i = 0; i < leftLabels.length; i++)
		{
			leftLabels[i] =NUMBER_FORMAT.format((maxValue - (maxValue / (leftLabels.length - 1)) * i)) + "";
		}
	}
	
	public void reset()
	{
		for(int i = 0; i < lineFraction.length; i++)
		{
			lineFraction[i] = 0;
		}
	}
	
	private class Key extends Container
	{
		String[] labels;
		int[] colors;
		private int labelBackgroundColor;
		private int textColor;
		private int fontSize;
		private float labelHeight;
		
		Key(Parent parent,UDim2 position,UDim2 size,String[] labels,int[] colors)
		{
			super(parent,position,size);
			this.labels=labels;
			this.colors=colors;
			this.labelBackgroundColor = 0x0F000000;
			this.textColor = 0xFFFFFFFF;
			this.labelHeight = size.absoluteY() / labels.length;
			fontSize = fitFont(14, findLargestLabel(labels), (size.absoluteX() / 2) * labelHeight);
		}
		
		public void draw(GuiObject self)
		{
			applet.noStroke();
			applet.fill(labelBackgroundColor);
			float[] ordinates = getTruePosition();
			applet.rect(ordinates[0], ordinates[1], size.absoluteX(), size.absoluteY());
			applet.textSize(fontSize);
			//displayTextVertically(labels,new UDim2(1f,size.absoluteX()/2,1f,0),new UDim2(.25f,0,1f,0));
			for(int i = 0; i < colors.length && i < labels.length; i++)
			{
				applet.fill(textColor);
				applet.text(labels[i], ordinates[0] + size.absoluteX()/2, ordinates[1] + i*labelHeight, 
						((size.absoluteX())/2) , labelHeight);
				applet.fill(colors[i]);
				applet.rect(ordinates[0] + 10, 	//raw offset left= 10
						ordinates[1] + i*labelHeight + labelHeight/4, //raw offset to position=labelheight/4
						(size.absoluteX()/2) - 20,	//20 = accomodate for offset left and right
						labelHeight/2);
			}
		}
	}
}
