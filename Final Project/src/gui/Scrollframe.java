package gui;

import datatypes.UDim2;
import gui.events.Event;
import gui.events.Event.EventOperation;
import gui.primitive.Container;
import gui.primitive.Parent;
import gui.primitive.TextButton;
import processing.event.MouseEvent;

public class Scrollframe extends Container implements Parent{
	private static final int SCROLLBAR_THICKNESS = 10;
	
	protected Container holder;
	protected Container scrollbarHolder;
	protected TextButton scrollbar;
	
	private boolean scrollbarActive = false;
	private float lastMouseY = -1;
	
	public Scrollframe (Parent parent, UDim2 position, UDim2 size) {
		super(parent, position, size);

		this.setClipDescendants(true);
		this.setBackgroundColor(applet.TRANSPARENT);
		this.setBorder(false);
		
		this.holder = new Container (this, new UDim2 (0, 0, 0, 0), new UDim2 (1,-SCROLLBAR_THICKNESS,1,1));
		holder.setBorder(false);

		scrollbarHolder = new Container (this, new UDim2 (1, -SCROLLBAR_THICKNESS, 0, 0), new UDim2 (0, SCROLLBAR_THICKNESS, 1, 0));
		scrollbarHolder.setBorder(false);
		scrollbarHolder.setBackgroundColor(applet.TRANSPARENT);
		
		scrollbar = new TextButton (scrollbarHolder, new UDim2 (1, 0, this.size.absoluteY()/holder.getSize().absoluteY(), 0), "");
		scrollbar.setBackgroundColor(255);
		
		EventOperation beginDrag = () -> {
			lastMouseY = applet.mouseY;
		};
		
		scrollbar.MouseButton1Down.connect(beginDrag);
		applet.registerMethod("mouseEvent",this);
	}
	
	public Scrollframe (Parent parent, UDim2 size) {
		this(parent, new UDim2 (), size);
	}
	
	public void mouseEvent(MouseEvent event)
	{
		if(event.getAction()==MouseEvent.WHEEL)
		{
			if(mouseInside)
			{
				moveScrollbar(event.getCount()/10f);
			}
		}
	}
	
	@Override
	public void update () {
		super.update();
		if (!applet.mousePressed)
			lastMouseY = -1;
		if (lastMouseY != -1) {
			float alpha = (applet.mouseY - applet.pmouseY)/(size.absoluteY());//lastMouseY;
			moveScrollbar(alpha);
			lastMouseY = applet.mouseY;
		}
		scrollbar.setSize(new UDim2 (1, 0, this.size.absoluteY()/holder.getSize().absoluteY(), 0));
	}
	
	protected void moveScrollbar(float percentage) {
		float newScaleY = scrollbar.getPosition().yScale + percentage;
		newScaleY = newScaleY < 0 ? 0 : newScaleY > (1 - scrollbar.getSize().yScale) ? 1 - scrollbar.getSize().yScale : newScaleY;
		scrollbar.setPosition(new UDim2 (0,0,newScaleY,0));
		newScaleY = -newScaleY*holder.getSize().yScale;
		newScaleY = newScaleY < -holder.getSize().yScale ? -holder.getSize().yScale : newScaleY > 0 ? 0 : newScaleY;
		holder.setPosition(new UDim2(0,0,newScaleY,0));
	}
	
	public void setContentSize (float yScale) {
		holder.setSize(new UDim2 (1, -SCROLLBAR_THICKNESS, yScale, 0));
		holder.setPosition(new UDim2 ());
		scrollbarActive = yScale >= 1f;
		scrollbar.setVisible(scrollbarActive);
	}
	
	public Container getContentHolder () {
		return holder;
	}
	
	public void changeContentBackground (int color) {
		holder.setBackgroundColor(color);
	}
}