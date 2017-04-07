package gui.graph;

import datatypes.UDim2;
import gui.primitive.Container;
import gui.primitive.Parent;
import processing.core.PApplet;

public abstract class Graphs extends Container
{
	protected int[] values;
	protected String title;
	PApplet applet;
	protected final float paddingLength;
	
	public Graphs(Parent parent, UDim2 position, UDim2 size, int[] values, String title)
	{
		super(parent,position,size);
		applet = parent.getApplet();
		MouseEnter.disconnectAll();
		MouseLeave.disconnectAll();
		this.values = values;
		this.title = title;
		paddingLength = applet.textWidth(" ");
	}
	
	public float getX()
	{
		float[] ordinates = getTruePosition();
		return ordinates[0];
	}
	
	public float getY()
	{
		float[] ordinates = getTruePosition();
		return ordinates[1];
	}
	
	public float getWidth()
	{
		return size.absoluteX();
	}
	
	public float getHeight()
	{
		return size.absoluteY();
	}
	
	public String findLargestLabel(String[] labels)
	{
		String largestLabel = "";
		for(String nextLabel:labels)
			largestLabel = largestText(largestLabel,nextLabel);
		return largestLabel;
	}

	public int fitFont(int maxFontSize,String text,double area)
	{
		int fontSize = maxFontSize;
		applet.textSize(fontSize);
		while((applet.textAscent()+applet.textDescent())*
				applet.textWidth(text)>area && fontSize>1)
		{
			fontSize--;
			applet.textSize(fontSize);
		}
		return fontSize;
	}

	public String largestText(String firstText,String secondText)
	{
		if(applet.textWidth(firstText)>applet.textWidth(secondText))
			return firstText;
		return secondText;
	}
	
	public void resetArray(float[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			array[i]=0;
		}
	}
	
	public void resetArray(int[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			array[i]=0;
		}
	}
	
	public void resetArray(double[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			array[i]=0;
		}
	}
	
	public void displayRotatedTextHorizontally(String[] text,UDim2 position,UDim2 size)
	{
		float[] origin = origin();
		float[] ordinates = {origin[0] + position.absoluteX(), origin[1] + position.absoluteY()};
		float labelWidth = size.absoluteX()/text.length;
		float labelHeight = size.absoluteY();
		
		applet.pushMatrix();
		applet.translate(ordinates[0],ordinates[1]);
		for(int i=0;i<text.length;i++)
		{
			applet.translate((labelWidth),0);
			applet.rotate(PApplet.HALF_PI);
			String label = text[i];
			
			int paddingRequired = (int)((labelHeight-applet.textWidth(label))/paddingLength);
			label = String.format("%" + -paddingRequired + "s", label);
			
			applet.text(label,0,0,labelHeight,labelWidth);
			applet.rotate(-PApplet.HALF_PI);
		}
		applet.popMatrix();	
	}
	
	public void displayRotatedTextVertically(String[] text,UDim2 position,UDim2 size)
	{
		float[] origin = origin();
		float[] ordinates = {origin[0] + position.absoluteX(), origin[1] + position.absoluteY()};
		float labelWidth = size.absoluteX()/text.length;
		float labelHeight = size.absoluteY();
		
		applet.pushMatrix();
		applet.translate(ordinates[0],ordinates[1]);
		for(int i=0;i<text.length;i++)
		{
			applet.translate(0,labelHeight);
			applet.rotate(PApplet.HALF_PI);
			String label = text[i];
			
			applet.text(label,0,0,labelHeight,labelWidth);
			applet.rotate(-PApplet.HALF_PI);
		}
		applet.popMatrix();	
	}
	
	public void displayTextVertically(String[] text,UDim2 position,UDim2 size)
	{
		float[] origin = origin();
		float[] ordinates = {origin[0] + position.absoluteX(), origin[1] + position.absoluteY()};
		float labelWidth = size.absoluteX()/text.length;
		float labelHeight = size.absoluteY();
		
		applet.pushMatrix();
		applet.translate(ordinates[0],ordinates[1]);
		for(int j=0;j<text.length;j++)
		{
			applet.translate(0,labelHeight);
			String label = text[j];
			int paddingRequired = (int)((labelWidth-applet.textWidth(label))/paddingLength);
			if(paddingRequired>0)
				label = String.format("%" + paddingRequired + "s", label);
			applet.text(label, 0,0,labelHeight,labelWidth);
		}
		applet.popMatrix();
	}
	
	public void displayTextHorizontally(String[] text,UDim2 position,UDim2 size)
	{
		float[] origin = origin();
		float[] ordinates = {origin[0] + position.absoluteX(), origin[1] + position.absoluteY()};
		float labelWidth = size.absoluteX()/text.length;
		float labelHeight = size.absoluteY();
		
		applet.pushMatrix();
		applet.translate(ordinates[0],ordinates[1]);
		for(int j=0;j<text.length;j++)
		{
			applet.text(text[j], 0,0,labelWidth,labelHeight);
			applet.translate(labelWidth,0);
		}
		applet.popMatrix();
	}
	
	protected int brightenColour(int colour)
	{
		int transparency = (colour & 0xFF000000) >> 24;
		int redComponent = colour & 0x000000FF;
		int greenComponent = (colour & 0x0000FF00) >> 8;
		int blueComponent = (colour & 0x00FF0000) >> 16;
		
		redComponent += 0x50;
		if (redComponent > 0xFF)
			redComponent = 0xFF;
		greenComponent += 0x50;
		if (greenComponent > 0xFF)
			greenComponent = 0xFF;
		blueComponent += 0x50;
		if (blueComponent > 0xFF)
			blueComponent = 0xFF;
		
		return (transparency << 24) + redComponent + (greenComponent << 8) + (blueComponent << 16);
	}

}
