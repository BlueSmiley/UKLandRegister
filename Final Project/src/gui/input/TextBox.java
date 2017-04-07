package gui.input;

import datatypes.UDim2;
import gui.events.*;
import gui.primitive.GuiObject;
import gui.primitive.Parent;
import gui.primitive.TextButton;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class TextBox extends TextButton implements InputListener
{
	int cursor;
	StringBuilder queryString;
	boolean focus;
	String pattern;
	public final Event Output;
	public final Event TextChanged = new Event ();
	public final Event mouseClick;
	protected static final int MIN_HOLD_PERIOD = 20; 		// In milliseconds.
	protected static final int HOLD_PERIOD = 500;
	protected long startTime;
	protected static final int STOPPED = -1;
	protected int currentHoldPeriod;
	
	public TextBox(Parent parent, UDim2 position, UDim2 size,String regex)
	{
		super(parent,position,size,"");
		applet.registerMethod("keyEvent", this);
		applet.registerMethod("mouseEvent",this);
		focus = false;
		cursor = 0;
		queryString = new StringBuilder();
		pattern=regex;
		Output = new Event();
		mouseClick = new Event();
		mouseClick.connect(()->{
		if(mouseInside)
			setFocus();
		else
			removeFocus();
		});
		setHorizontalAlign(PApplet.LEFT);
		setVerticalAlign(PApplet.CENTER);
		startTime=STOPPED;
		currentHoldPeriod = HOLD_PERIOD-MIN_HOLD_PERIOD;
	}
	
	public void keyEvent(KeyEvent event)
	{
		float[] ordinates = getTruePosition();
		if(event.getAction() == KeyEvent.RELEASE)
		{
			startTime=STOPPED;
			currentHoldPeriod = HOLD_PERIOD-MIN_HOLD_PERIOD;
		}
		if(visible && focus && event.getAction() == KeyEvent.PRESS && applet.frame.getNextScreenId() == -1 && !tweening
				&& (ordinates[0]>=0 && ordinates[1]>=0 && ordinates[0]<applet.displayWidth && ordinates[1]<applet.displayHeight))
		{
			if(event.getKey()==PApplet.CODED && event.getKeyCode()>=37 && event.getKeyCode()<=40)
			{
				if(event.getKeyCode()==PApplet.LEFT)
				{
					moveCursorLeft();
				}
				else if(event.getKeyCode()==PApplet.RIGHT)
					moveCursorRight();
			}
			else if(event.getKey()==PApplet.BACKSPACE)
			{
				append(event.getKey());
				startTime=System.currentTimeMillis();
			}
			else if((event.getKey()>=32 && event.getKey()<=126) )
			{
				String key = Character.toString(event.getKey());
				if(key.matches(pattern))
				{
					append(event.getKey());
					startTime=System.currentTimeMillis();
				}

			}
			else
			{
				if(event.getKey()==PApplet.RETURN || event.getKey()==PApplet.ENTER)
				{
					removeFocus();
					Output.trigger();
				}
			}
		}
	}
	
	public void mouseEvent(MouseEvent event)
	{
		if(event.getAction()==MouseEvent.CLICK || event.getAction()==MouseEvent.PRESS 
				|| event.getAction()==MouseEvent.RELEASE)
		{
			mouseClick.trigger();
		}
	}
	
	public void setFocus()
	{
		focus = true;
		clearString();
		setText("");
	}
	
	public void removeFocus()
	{
		focus=false;
	}
	
	public void moveCursorLeft()
	{
		if(cursor>0)
			cursor--;
	}
	
	public void moveCursorRight()
	{
		if(cursor<queryString.length())
			cursor++;
	}
	
	public void append(char keyPressed)
	{
		if(keyPressed==PApplet.BACKSPACE)
		{
			if(cursor!=0)
				cursor--;
			if(queryString.toString()!="" && queryString.length()!=0)
				queryString = queryString.deleteCharAt(cursor);
		}
		else
		{
			queryString.insert(cursor, keyPressed);	
			cursor++;
		}
		setText(queryString.toString());
	}
	
	public void displayCursor()
	{
		float displayDistance=0;
		float lineNumber = 0;
		float textHeight = applet.textAscent()+applet.textDescent();
		if(cursor>0)
		{
			displayDistance = applet.textWidth(queryString.substring(0,cursor))%size.absoluteX();
			lineNumber = (int)(applet.textWidth(queryString.substring(0,cursor))/size.absoluteX());
		}
		
		if((textHeight+2)*(lineNumber+1)<size.absoluteY())
		{
			float[] ordinates = getTruePosition();
			applet.stroke(applet.TEXT_COLOR);
			applet.line(ordinates[0]+.25f+displayDistance+lineNumber*2,
					ordinates[1]+(textHeight+2)*lineNumber + ((size.absoluteY()/2)-textHeight/2),
					ordinates[0]+.25f+displayDistance+lineNumber*2,
					ordinates[1]+(textHeight+2)*(lineNumber+1)+((size.absoluteY()/2)-textHeight/2));
		}
	}
	
	
	public void clearString()
	{
		queryString = new StringBuilder();
		cursor = 0;
		setText("");
	}
	
	public String getString(int index)
	{
		return queryString.substring(index);
	}
	
	public String getString(int index,int lastindex)
	{
		return queryString.substring(index,lastindex);
	}
	
	@Override
	public void draw(GuiObject self)
	{
		if(startTime!=STOPPED)
		{
			if(System.currentTimeMillis()-startTime>MIN_HOLD_PERIOD+currentHoldPeriod)
			{
				startTime=System.currentTimeMillis();
				append(applet.key);
				currentHoldPeriod /= 5;
			}
		}
		super.draw(self);
		if(focus)
			displayCursor();
	}
	
	@Override
	public void setText (String text) {
		try {
			TextChanged.trigger();
		} catch (NullPointerException e) {
			System.out.println();
		}
		super.setText(text);
	}
}
