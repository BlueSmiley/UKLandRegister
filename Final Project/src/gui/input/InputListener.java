package gui.input;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public interface InputListener 
{
	public void keyEvent(KeyEvent event);
	public void mouseEvent(MouseEvent event);
}
