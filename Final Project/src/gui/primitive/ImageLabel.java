package gui.primitive;

import datatypes.UDim2;
import processing.core.PImage;

public class ImageLabel extends Container
{

	protected PImage image;

	public ImageLabel(Parent parent, UDim2 position, UDim2 size, PImage image)
	{
		super(parent, position, size);
		setImage(image);
	}

	public ImageLabel(Parent parent, UDim2 size, PImage image)
	{
		this(parent, new UDim2(), size, image);
	}

	@Override public void draw(GuiObject self)
	{
		super.draw(this);
		if(image != null)
		{
			float[] drawPosition = getTruePosition();
			applet.image(image, drawPosition[0], drawPosition[1]);
		}
	}

	public PImage getImage()
	{
		return image;
	}

	public void setImage(PImage image)
	{
		this.image = image;
		if(this.image != null) this.image.resize((int) size.absoluteX(), (int) size.absoluteY());
	}

}
