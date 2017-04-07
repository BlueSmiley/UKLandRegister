package gui.primitive;

import datatypes.UDim2;
import gui.events.Event;

public class TextButton extends TextLabel {
	protected boolean mouseDown = false;
	protected static final int CLICK_PERIOD = 500; 		// In milliseconds.
	protected long mouseDownAt;
	public final Event MouseButton1Down  = new Event ();
	public final Event MouseButton1Up	 = new Event ();
	public final Event MouseButton1Click = new Event ();
	
	public TextButton (Parent parent, UDim2 position, UDim2 size, String text) {
		super(parent, position, size, text);
	}
	
	public TextButton (Parent parent, UDim2 size, String text) {
		this(parent, new UDim2 (), size, text);
	}
	
	@Override
	public void update () {
		super.update();
		if (visible) {
			if (mouseInside) {
				boolean isMouseDown = applet.mousePressed;
				if (mouseDown != isMouseDown) {
					mouseDown = isMouseDown;
					if (mouseDown) {
						mouseDownAt = System.currentTimeMillis();
						MouseButton1Down.trigger();
					} else {
						MouseButton1Up.trigger();
						if (System.currentTimeMillis() - mouseDownAt <= CLICK_PERIOD) {
							MouseButton1Click.trigger();
						}
					}
				}
			}
		}
	}
}