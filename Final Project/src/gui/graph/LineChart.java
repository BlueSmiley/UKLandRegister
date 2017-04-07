package gui.graph;

import java.text.DecimalFormat;

import datatypes.UDim2;
import gui.primitive.GuiObject;
import gui.primitive.Parent;
import processing.core.PApplet;
import processing.core.PFont;

public class LineChart extends Graphs
{
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
	PApplet processing;
	private float growthPerFrame;
	private String xLabel, yLabel;
	private int pointSize;

	private PFont leftLabelFont;
	private PFont bottomLabelFont;
	private PFont titleFont;
	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#");

	public LineChart(Parent parent, UDim2 position, UDim2 size, int[] values, float labelSize, String[] bottomLabels,
			String title, String xLabel, String yLabel)
	{
		super(parent, position, size, values, title);
		MouseEnter.disconnectAll();
		MouseLeave.disconnectAll();    //dont want default functionality for these
		this.processing = parent.getApplet();    //processing functions and methods are used frequently

		this.labelSize = labelSize;
		this.bottomLabels = bottomLabels;
		this.title = title;
		this.values = values;
		this.pointSize = 7;
		lineFraction = new float[values.length - 1];
		for(int i = 0; i < lineFraction.length; i++)
		{
			lineFraction[i] = 0;
		}
		float totalFrames = ANIMATION_TIME * processing.frameRate;
		float framesPerSegment = totalFrames / values.length;
		growthPerFrame = 1 / framesPerSegment;

		lineWidth = (size.absoluteX() - labelSize) / (this.values.length - 1);
		this.maxValue = 0;
		//use floats or double maxValue for double precision during division in graduateValues function later
		for(float value : values)
		{
			if(value > this.maxValue) maxValue = value;
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

	public LineChart(Parent parent, UDim2 position, UDim2 size, int[] values, float labelSize, String[] bottomLabels,
			String title)
	{
		this(parent, position, size, values, labelSize, bottomLabels, title, "", "");
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

	public final String findLargestLabel(String[] labels)
	{
		String largestLabel = "";
		for(String nextLabel : labels)
			largestLabel = largestText(largestLabel, nextLabel);
		return largestLabel;
	}

	public int fitFont(int maxFontSize, String text, double area)
	{
		int fontSize = maxFontSize;
		processing.textSize(fontSize);
		while((processing.textAscent() + processing.textDescent()) * processing.textWidth(text) > area && fontSize > 1)
		{
			fontSize--;
			processing.textSize(fontSize);
		}
		return fontSize;
	}

	public String largestText(String firstText, String secondText)
	{
		if(processing.textWidth(firstText) > processing.textWidth(secondText)) return firstText;
		return secondText;
	}

	public void reset()
	{
		for(int i = 0; i < lineFraction.length; i++)
		{
			lineFraction[i] = 0;
		}
	}

	public void displayLines()
	{
		processing.stroke(lineColor);
		processing.strokeWeight(STROKE_SIZE);
		processing.fill(pointColor);
		float[] ordinates = getTruePosition();
		for(int i = - 1; i < values.length - 1; i++)
		{
			if(i >= 0)
			{
				if(i == 0) processing.ellipse((ordinates[0] + labelSize) + (i) * lineWidth,
						ordinates[1] + (size.absoluteY() - (values[i] * lineHeightRatio) - labelSize) - pointSize,
						pointSize, pointSize);
				if(i == 0 || lineFraction[i - 1] == 1)
				{
					processing.line((ordinates[0] + labelSize) + (i) * lineWidth,
							ordinates[1] + (size.absoluteY() - (values[i] * lineHeightRatio) - labelSize) - pointSize,
							(ordinates[0] + labelSize) + (i) * lineWidth + (lineWidth * (float) lineFraction[i]),
							ordinates[1] + (size.absoluteY() - labelSize - (values[i] * lineHeightRatio)
									- (values[i + 1] - values[i]) * lineHeightRatio * (float) lineFraction[i])
									- pointSize);
					if(lineFraction[i] < 1)
					{
						lineFraction[i] += growthPerFrame;
						if(lineFraction[i] > 1) lineFraction[i] = 1;
					}
					else processing.ellipse((ordinates[0] + labelSize) + (i + 1) * lineWidth,
							ordinates[1] + (size.absoluteY() - (values[i + 1] * lineHeightRatio) - labelSize)
									- pointSize, pointSize, pointSize);

				}
			}

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

	public void displayText()
	{
		float[] ordinates = getTruePosition();
		//processing.textSize(bottomLabelFontSize);
		applet.textFont(bottomLabelFont);
		processing.fill(textColor);
		processing.pushMatrix();
		processing.translate(
				ordinates[0] + labelSize - lineWidth / 2 + (processing.textDescent() + processing.textDescent()) / 2,
				ordinates[1] + size.absoluteY() - labelSize / 3);
		int paddingRequired = (int) (((labelSize * 2 / 3) - applet.textWidth(findLargestLabel(bottomLabels)))
				/ paddingLength);
		for(int i = 0; i < bottomLabels.length; i++)
		{
			processing.rotate(PApplet.HALF_PI * 3);
			String label = bottomLabels[i];
			if(paddingRequired > 0) label = String.format("%" + - paddingRequired + "s", label);
			processing.text(label, 0, 0, (labelSize * 2 / 3), lineWidth);
			processing.rotate(- PApplet.HALF_PI * 3);
			processing.translate((lineWidth), 0);
		}
		processing.popMatrix();

		//processing.textSize(leftLabelFontSize);
		applet.textFont(leftLabelFont);
		for(int j = 0; j < leftLabels.length; j++)
		{
			String label = leftLabels[j];
			int leftPaddingRequired = (int) (((labelSize * 2 / 3) - applet.textWidth(label)) / paddingLength);
			if(paddingRequired > 0) label = String.format("%" + leftPaddingRequired + "s", label);

			processing.text(label, ordinates[0] + labelSize / 3,
					ordinates[1] + labelSize + ((getHeight() - 2 * labelSize) / (leftLabels.length - 1)) * j
							- ((getHeight() - 2 * labelSize) / (leftLabels.length - 1)) / 2, (labelSize * 2 / 3),
					(size.absoluteX() - 2 * labelSize) / leftLabels.length);
		}

		//processing.textSize(titleFontSize);
		applet.textFont(titleFont);
		processing.text(title, ordinates[0] + labelSize, ordinates[1], size.absoluteX() - labelSize, labelSize);

		applet.text(xLabel, getX(), getY() + getHeight() - labelSize / 3, getWidth(), labelSize / 3);

		applet.pushMatrix();
		applet.translate(getX(), getY() + getHeight());
		applet.rotate(PApplet.HALF_PI * 3);
		applet.text(yLabel, 0, 0, getHeight(), labelSize / 3);
		applet.rotate(- PApplet.HALF_PI * 3);
		applet.popMatrix();
	}

	@Override public void draw(GuiObject self)
	{
		//wont call super as dont want to implement default drawing functionality
		float[] ordinates = getTruePosition();
		processing.fill(backgroundColor);
		processing.stroke(0);
		processing.rect(ordinates[0] + labelSize, ordinates[1] + labelSize, size.absoluteX() - labelSize,
				size.absoluteY() - 2 * labelSize);
		displayLines();
		displayText();
	}
}
