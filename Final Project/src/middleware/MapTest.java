package middleware;

import database.DBConnection;
import database.Queries;
import datatypes.UDim2;
import gui.map.Map;
import gui.primitive.Screen;

import java.util.ArrayList;

public class MapTest extends Screen
{
	public MapTest(Main applet)
	{
		super(applet);
		DBConnection dbConnection = new DBConnection();
		Queries queries = new Queries(dbConnection);
		ArrayList<Integer> filteredList = queries.filterPriceRange(100000, 200000);
		Map map = new Map(this, new UDim2(), new UDim2(1, 0, 1, 0), queries, filteredList);
	}

}
