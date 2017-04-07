package database;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CoordinateConverter
{
	//Converts postcode to coordinated. Latitude is in index 0, while Longitude is in index 1.
	public static double[] convertPostcode(String postCode)
	{
		postCode = postCode.replaceAll("\\s", "");
		double latitude = 0;
		double longitude = 0;
		try
		{
			URL url = new URL("https://api.postcodes.io/postcodes/" + postCode);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			int responseCode = connection.getResponseCode();
			if(responseCode == 200)
			{
				JSONParser parser = new JSONParser();
				InputStream inputStream = connection.getInputStream();
				JSONObject jsonObject = (JSONObject) parser
				.parse(new InputStreamReader(inputStream));
				jsonObject = (JSONObject) jsonObject.get("result");
				latitude = (double) jsonObject.get("latitude");
				longitude = (double) jsonObject.get("longitude");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new double[] { latitude, longitude };
	}
}
