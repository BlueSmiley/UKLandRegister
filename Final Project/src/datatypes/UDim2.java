package datatypes;

// UDim2 stands for Universal Dimension x2.
// It is a standard for 2D coordinates that allows finer control of its elements.

public class UDim2 {
	public final float xScale, xOffset, yScale, yOffset;
	private float[] scale = {(float) middleware.Main.SCREEN[0], (float) middleware.Main.SCREEN[1]};
	
	public UDim2 (float xScale, float xOffset, float yScale, float yOffset) {
		this.xScale  = xScale;
		this.xOffset = xOffset;
		this.yScale  = yScale;
		this.yOffset = yOffset;
	}
	
	public UDim2 (float xScale, int xOffset, float yScale, int yOffset) {
		this(xScale, (float) xOffset, yScale, (float) yOffset);
	}
	
	public UDim2 (float xOffset, float yOffset) {
		this(0, xOffset, 0, yOffset);
	}
	
	public UDim2 (int xOffset, int yOffset) {
		this(0, (float) xOffset, 0, (float) yOffset);
	}
	
	public UDim2 () {
		this(0, 0, 0, 0);
	}
	
	public void setScale (float[] scale) {
		this.scale = scale;
	}
	
	public float absoluteX () {
		return xScale * scale[0] + xOffset;
	}
	
	public float absoluteY () {
		return yScale * scale[1] + yOffset;
	}
	
	public UDim2 add (UDim2 dim) {
		return new UDim2(this.xScale + dim.xScale, this.xOffset + dim.xOffset, this.yScale + dim.yScale, this.yOffset + dim.yOffset);
	}
	
	public UDim2 sub (UDim2 dim) {
		return new UDim2(this.xScale - dim.xScale, this.xOffset - dim.xOffset, this.yScale - dim.yScale, this.yOffset - dim.yOffset);
	}
	
	public UDim2 lerp (UDim2 goal, float alpha) {
		UDim2 delta = goal.sub(this);
		return new UDim2 (this.xScale + delta.xScale * alpha, this.xOffset + delta.xOffset * alpha,
									 this.yScale + delta.yScale * alpha, this.yOffset + delta.yOffset * alpha);
	}
	
	public float[] toFloatArray () {
		float[] array = new float[2];
		array[0] = absoluteX();
		array[1] = absoluteY();
		return array;
	}
	
	@Override
	public String toString () {
		return "X{" + xScale + ", " + xOffset + "} Y{" + yScale + ", " + yOffset + "}";
	}
}