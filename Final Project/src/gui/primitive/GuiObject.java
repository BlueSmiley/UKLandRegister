package gui.primitive;

import datatypes.UDim2;
import gui.events.Event;
import middleware.Main;

import java.util.ArrayList;
import java.util.HashMap;

public abstract  class GuiObject implements Parent {
	protected enum TweeningType {
		Position, Size, PositionAndSize;
	}
	
	public interface TweenCallback {
		public void execute ();
	}
	
	protected static final String NEGATIVE_SIZE_ERROR = "One of the components computes to negative or zero.";
	private static final String ZINDEX_OUT_OF_BOUNDS = "The ZIndex entered is out of bounds.";
	
	protected static Main applet;
	protected Screen screen;
	protected Parent parent;
	protected HashMap<Integer, ArrayList<GuiObject>> children = new HashMap<Integer, ArrayList<GuiObject>> ();
	
	protected UDim2 position;
	protected UDim2 size;
	
	protected static int tweenNumber = 0;
	
	protected boolean tweening = false;
	protected TweeningType type;
	protected UDim2 originalPosition;
	protected UDim2 destination;
	protected UDim2 originalSize;
	protected UDim2 finalSize;
	protected int duration;
	protected long startTime;
	protected TweenCallback callback;
	
	protected final Event SizeChange = new Event ();
	
	protected boolean visible = true;
	public static final int ZLEVELS = 10;
	protected int zIndex = 2;
	
	protected boolean clipDescendants = false;
	
	public GuiObject (Parent parent, UDim2 position, UDim2 size) {
		this.parent = parent;
		parent.addChild(this);
		applet = parent.getApplet();
		screen = parent.getScreen();
		
		clearAllChildren();
		
		setPosition(position);
		setSize(size);
		setZIndex(zIndex);
		SizeChange.connect(() -> {
			for (int z = children.size() - 1; z >= 0; z--) {
				for (GuiObject child: children.get(z)) {
					child.setPosition(child.position);
					child.setSize(child.size);
				}
			}
		});
	}
	
	public GuiObject (Parent parent, UDim2 size) {
		this(parent, new UDim2 (), size);
	}
	
	public void draw () {
		tween();
		
		if (visible) {
			draw (this);
			for (int z = children.size() - 1; z >= 0; z--) {
				for (GuiObject child: children.get(z)) {
					child.draw();
				}
			}
		}
	}
	
	public abstract void draw (GuiObject self);
	
	public void update () {
		tween();
		if (visible) {
			draw (this);
			float[][] prevRegion = screen.getClippingRegion();
			if (clipDescendants) {
				float[] truePosition = getTruePosition();
				float[][] newRegion = {{truePosition[0]-1, truePosition[1]-1}, {size.absoluteX()+truePosition[0]+1, size.absoluteY()+truePosition[1]+1}};
	
				newRegion[0][0] = newRegion[0][0] < prevRegion[0][0] ? prevRegion[0][0] : newRegion[0][0];
				newRegion[0][1] = newRegion[0][1] < prevRegion[0][1] ? prevRegion[0][1] : newRegion[0][1];
				newRegion[1][0] = newRegion[1][0] > prevRegion[1][0] ? prevRegion[1][0] : newRegion[1][0];
				newRegion[1][1] = newRegion[1][1] > prevRegion[1][1] ? prevRegion[1][1] : newRegion[1][1];
	
				screen.setClippingRegion(newRegion);
				applet.clip(newRegion[0][0], newRegion[0][1], newRegion[1][0], newRegion[1][1]);
			}
			
			for (int z = children.size() - 1; z >= 0; z--) {
				for (GuiObject child: children.get(z)) {
					child.update();
				}
			}
			
			screen.setClippingRegion(prevRegion);
			applet.clip(prevRegion[0][0], prevRegion[0][1], prevRegion[1][0], prevRegion[1][1]);
		}
	}
	
	private void tween () {
		if (tweening) {
			long timeDifference = System.currentTimeMillis() - startTime;
			if (timeDifference <= duration) {
				float alpha = (float) timeDifference / (float) duration;
				if (type == TweeningType.Position || type == TweeningType.PositionAndSize)
					setPosition(originalPosition.lerp(destination, alpha));
				if (type == TweeningType.Size || type == TweeningType.PositionAndSize) {
					setSize(originalSize.lerp(finalSize, alpha));
				}
				if (callback != null) {
					callback.execute();
				}
			} else {
				screen.setCanBeChanged(true);
				tweening = false;
				callback = null;
				if (type == TweeningType.Position || type == TweeningType.PositionAndSize)
					setPosition(destination);
				if (type == TweeningType.Size || type == TweeningType.PositionAndSize)
					setSize(finalSize);
			}
		}
	}
	
	public void tweenPosition (UDim2 destination, float duration) {
		screen.setCanBeChanged(false);
		tweening = true;
		type = TweeningType.Position;
		originalPosition = position;
		this.destination = destination;
		this.duration = (int) (duration * 1000);
		startTime = System.currentTimeMillis();
	}
	
	public void tweenPosition (UDim2 destination, float duration, TweenCallback callback) {
		this.callback = callback;
		tweenPosition(destination, duration);
	}
	
	public void tweenPosition (UDim2 originalPosition, UDim2 destination, float duration) {
		setPosition(originalPosition);
		tweenPosition(destination, duration);
	}
	
	public void tweenPosition (UDim2 originalPosition, UDim2 destination, float duration, TweenCallback callback) {
		this.callback = callback;
		tweenPosition(originalPosition, destination, duration);
	}
	
	public void tweenSize (UDim2 finalSize, float duration) {
		screen.setCanBeChanged(false);
		tweening = true;
		type = TweeningType.Size;
		originalSize = size;
		this.finalSize = finalSize;
		this.duration = (int) (duration * 1000);
		startTime = System.currentTimeMillis();
	}
	
	public void tweenSize (UDim2 finalSize, float duration, TweenCallback callback) {
		this.callback = callback;
		tweenSize(finalSize, duration);
	}
	
	public void tweenSize (UDim2 originalSize, UDim2 finalSize, float duration) {
		setSize(originalSize);
		tweenSize(finalSize, duration);
	}
	
	public void tweenSize (UDim2 originalSize, UDim2 finalSize, float duration, TweenCallback callback) {
		this.callback = callback;
		tweenSize(originalSize, finalSize, duration);
	}
	
	public void tweenPositionAndSize (UDim2 destination, UDim2 finalSize, float duration) {
		screen.setCanBeChanged(false);
		tweening = true;
		type = TweeningType.PositionAndSize;
		originalPosition = position;
		originalSize = size;
		this.destination = destination;
		this.finalSize = finalSize;
		this.duration = (int) (duration * 1000);
		startTime = System.currentTimeMillis();
	}
	
	public void tweenPositionAndSize (UDim2 destination, UDim2 finalSize, float duration, TweenCallback callback) {
		this.callback = callback;
		tweenPositionAndSize(destination, finalSize, duration);
	}
	
	public void tweenPositionAndSize (UDim2 originalPosition, UDim2 destination, UDim2 originalSize, UDim2 finalSize, float duration) {
		setPosition(originalPosition);
		setSize(originalSize);
		tweenPositionAndSize(destination, finalSize, duration);
	}
	
	public void tweenPositionAndSize (UDim2 originalPosition, UDim2 destination, UDim2 originalSize, UDim2 finalSize, float duration, TweenCallback callback) {
		this.callback = callback;
		tweenPositionAndSize(originalPosition, destination, originalSize, finalSize, duration);
	}
	
	public float[] getTruePosition () {
		float[] origin = parent.origin();
		float[] truePosition = {origin[0] + position.absoluteX(), origin[1] + position.absoluteY()};
		return truePosition;
	}
	
	public void setPosition (UDim2 position) {
		position.setScale(parent.getSize().toFloatArray());
		this.position = position;
	}
	
	public UDim2 getPosition () {
		return this.position;
	}
	
	public void setSize (UDim2 size) throws IllegalArgumentException {
		if (size.absoluteX() < 0 || size.absoluteY() < 0) {
			throw new IllegalArgumentException(NEGATIVE_SIZE_ERROR);
		} else {
			this.size = size;
			this.size.setScale(parent.getSize().toFloatArray());
			SizeChange.trigger();
		}
	}
	
	public UDim2 getSize () {
		return size;
	}
	
	public void setZIndex (int zIndex) throws IllegalArgumentException {
		if (zIndex < 0 || zIndex >= ZLEVELS)
			throw new IllegalArgumentException (ZINDEX_OUT_OF_BOUNDS);
		this.zIndex = zIndex;
		parent.changeZIndex(this, zIndex);
	}
	
	public int getZIndex () {
		return zIndex;
	}
	
	public void setVisible (boolean visible) {
		this.visible = visible;
	}
	
	public boolean getVisible () {
		return visible;
	}
	
	public void setClipDescendants (boolean clip) {
		this.clipDescendants = clip;
	}
	
	public boolean getClipDescendants () {
		return this.clipDescendants;
	}
	
	// Interface implementation
	@Override
	public float[] origin() {
		return getTruePosition();
	}

	@Override
	public ArrayList<GuiObject> getChildren() {
		ArrayList<GuiObject> allChildren = new ArrayList<GuiObject> ();
		
		for (int z = 0; z < children.size(); z++) {
			allChildren.addAll(allChildren.size(), children.get(z));
		}
		
		return allChildren;
	}
	
	@Override
	public GuiObject addChild (GuiObject child) {
		children.get(child.getZIndex()).add(child);
		return child;
	}
	
	@Override
	public void removeChild (GuiObject child) {
		children.get(child.getZIndex()).remove(child);
	}
	
	@Override 
	public void clearAllChildren () {
		for (int i = 0; i < GuiObject.ZLEVELS; i++) {
			children.put(i,  new ArrayList<GuiObject> ());
		}
	}
	
	@Override
	public void clearAllChildren (int zIndex) {
		children.put(zIndex, new ArrayList<GuiObject> ());
	}
	
	@Override
	public void changeZIndex (GuiObject child, int newZIndex) {
		children.get(child.getZIndex()).remove(child);
		children.get(newZIndex).add(child);
	}
	
	@Override
	public Main getApplet () {
		return applet;
	}
	
	@Override
	public Screen getScreen () {
		return screen;
	}
	
	public HashMap<Integer, ArrayList<GuiObject>> getZIndexChildren () {
		return children;
	}
}
