package gui.map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

import java.util.HashMap;

public class InfoMarker extends SimplePointMarker
{

	private UnfoldingMap map;
	private float size;

	public InfoMarker(Location location, HashMap properties, UnfoldingMap map, int color)
	{
		super(location, properties);
		this.map = map;
		this.color = color;
		diameter = 7;
		size = diameter;
	}

	public void draw(PGraphics pg, float x, float y)
	{
		pg.pushStyle();
		pg.pushMatrix();
		if(selected) pg.translate(0, 0);
		pg.strokeWeight(strokeWeight);
		if(selected)
		{
			pg.fill(highlightColor);
			pg.stroke(highlightStrokeColor);
		}
		else
		{
			pg.fill(color);
			pg.stroke(strokeColor);
		}
		diameter = size * ((map.getZoomLevel() / 6f));
		pg.ellipse(x, y, diameter, diameter);
		pg.popMatrix();
		pg.popStyle();
	}

	public int getColor()
	{
		return color;
	}

}
