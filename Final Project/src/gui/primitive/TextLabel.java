package gui.primitive;

import datatypes.UDim2;
import middleware.Main;
import processing.core.PFont;

public class TextLabel extends Container {
	protected String text = "";
	protected int textColor = 0x00000000;
	protected int textSize = 14;
	protected int horizontalAlign = Main.CENTER;
	protected int verticalAlign = Main.CENTER;
	protected PFont font = applet.defaultFont;
	
	public TextLabel (Parent parent, UDim2 position, UDim2 size, String text) {
		super(parent, position, size);
		setText(text);

		setTextColor(0xffffffff);
		setBackgroundColor(0x00ffffff);
		setBorder(false);
	}
	
	public TextLabel (Parent parent, UDim2 size, String text) {
		this(parent, new UDim2 (), size, text);
	}
	
	@Override
	public void draw (GuiObject self) {
		try {
			super.draw(this);
			float[] drawPosition = getTruePosition();
			
			applet.fill(textColor);
			applet.textSize(textSize);
			applet.textAlign(horizontalAlign, verticalAlign);
			applet.textFont(font, textSize);
			applet.text(text, drawPosition[0], drawPosition[1], size.absoluteX(), size.absoluteY());
		} catch (Exception e) {
			
		}
	}
	
	public void setText (String text) {
		this.text = text;
	}
	
	public String getText () {
		return text;
	}
	
	public void setTextColor (int color) {
		this.textColor = color;
	}
	
	public int getTextColor () {
		return textColor;
	}
	
	public void setTextSize (int size) {
		this.textSize = size;
	}
	
	public int getTextSize () {
		return textSize;
	}
	
	public void setHorizontalAlign (int align) {
		this.horizontalAlign = align;
	}
	
	public int getHorizontalAlign () {
		return horizontalAlign;
	}
	
	public void setVerticalAlign (int align) {
		this.verticalAlign = align;
	}
	
	public int getVerticalAlign () {
		return verticalAlign;
	}
	
	public void setFont (PFont font) {
		this.font = font;
	}
}