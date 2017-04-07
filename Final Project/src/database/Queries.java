package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Queries
{
	private DBConnection dbConnection;
	private SimpleDateFormat simpleDateFormat, storedDateFormat, outputDateFormat;

	public Queries(DBConnection dbConnection)
	{
		this.dbConnection = dbConnection;

		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		storedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	}

	public static String formatDatabaseDate(String databaseFormat)
	{
		String dateString = "";
		try
		{
			SimpleDateFormat storedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = storedDateFormat.parse(databaseFormat);
			dateString = outputDateFormat.format(date);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return dateString;
	}

	public static Date addDays(Date date, int days)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public ArrayList<Integer> getAllIDs()
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		try
		{
			String sql = "SELECT ID FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public boolean isValidDate(String date)
	{
		String[] dateProperties = date.split("/");

		if(dateProperties != null && dateProperties.length == 3)
		{
			int year = Integer.parseInt(dateProperties[2]);
			int month = Integer.parseInt(dateProperties[1]);
			int day = Integer.parseInt(dateProperties[0]);
			try
			{
				LocalDate.of(year, month, day);
				return true;
			}
			catch(DateTimeException e)
			{
				return false;
			}
		}
		return false;
	}

	public ArrayList<Integer> filterPriceRange(int lowerLimit, int upperLimit)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		try
		{
			String sql = "SELECT ID FROM LAND WHERE PRICE > ? AND PRICE <? ";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setInt(1, lowerLimit);
			preparedStatement.setInt(2, upperLimit);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterPriceRange(ArrayList<Integer> listOfIDs, int lowerLimit, int upperLimit)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT ID FROM LAND WHERE ID IN (" + ids + ") AND PRICE > ? AND PRICE <?";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setInt(1, lowerLimit);
			preparedStatement.setInt(2, upperLimit);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		return filteredList;
	}

	public ArrayList<Integer> filterPropertyType(ArrayList<Integer> listOfIDs, String[] types)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		String type = getTypeString(types);

		try
		{
			String sql = "SELECT ID FROM LAND WHERE ID IN (" + ids + ") AND (TYPE LIKE " + type + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterPropertyType(String[] types)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String type = getTypeString(types);
		try
		{
			String sql = "SELECT ID FROM LAND WHERE TYPE LIKE " + type + "";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterPropertyType(ArrayList<Integer> listOfIDs, String type)
	{
		return filterPropertyType(listOfIDs, new String[] { type });
	}

	public ArrayList<Integer> filterPropertyType(String type)
	{
		return filterPropertyType(new String[] { type });
	}

	public ArrayList<Integer> filterCondition(String condition)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		try
		{
			String sql = "SELECT ID FROM LAND WHERE CONDITION LIKE ?";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, condition);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	private String formatDate(String date)
	{
		String[] dateProperties = date.split("/");
		int year1 = Integer.parseInt(dateProperties[2]);
		int month1 = Integer.parseInt(dateProperties[1]);
		int day1 = Integer.parseInt(dateProperties[0]);
		return (year1 + "-" + ((month1 >= 10) ? month1 : "0" + month1) + "-" + ((day1 >= 10) ? day1 : "0" + day1));
	}

	public ArrayList<Integer> filterCondition(ArrayList<Integer> listOfIDs, String[] condition)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String conditionString = "";
			if(condition.length == 1) conditionString = "AND CONDITION LIKE '" + condition[0] + "'";
			else if(condition.length == 2) conditionString =
					"AND (CONDITION LIKE '" + condition[0] + "' OR CONDITION LIKE '" + condition[1] + "')";
			String sql = "SELECT ID FROM LAND WHERE ID IN (" + ids + ") " + conditionString;
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterCondition(ArrayList<Integer> listOfIDs, String condition)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT ID FROM LAND WHERE ID IN (" + ids + ") AND CONDITION LIKE ?";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, condition);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public String[] getRandomAddresses(int numberOfAddresses)
	{
		ArrayList<Integer> allIDs = getAllIDs();
		Collections.shuffle(allIDs);
		ArrayList<Integer> filteredList = new ArrayList<>();
		for(int i = 0; i <= numberOfAddresses; i++)
			filteredList.add(allIDs.get(i));

		String[] addresses = new String[numberOfAddresses];

		try
		{
			String ids = getIDString(filteredList);
			String sql = "SELECT NAME, STREET, LOCALITY, TOWN, DISTRICT, COUNTY FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			int count = 0;
			while(resultSet.next() & count < numberOfAddresses)
			{
				String name = resultSet.getString("NAME");
				String street = resultSet.getString("STREET");
				String locality = resultSet.getString("LOCALITY");
				String town = resultSet.getString("TOWN");
				String district = resultSet.getString("DISTRICT");
				String county = resultSet.getString("COUNTY");

				addresses[count] = (name + ", " + street + "," + locality + ", " + town + ", " + district + ", "
						+ county);
				count++;
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		return addresses;
	}

	public ArrayList<Integer> filterLocation(String postCode)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		try
		{
			String sql = "SELECT POSTCODE,ID FROM LAND WHERE POSTCODE LIKE '%" + postCode + "%'";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				if(resultSet.getString("POSTCODE").startsWith(postCode)) filteredList.add(resultSet.getInt("ID"));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterLocation(ArrayList<Integer> listOfIDs, String postCode)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String sql =
					"SELECT POSTCODE,ID FROM LAND WHERE ID IN (" + ids + ") AND POSTCODE LIKE '%" + postCode + "%'";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next() && resultSet.getString("POSTCODE").startsWith(postCode))
			{
				filteredList.add(resultSet.getInt("ID"));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	private ArrayList<Integer> filterFormattedDate(String formattedDate1, String formattedDate2)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		try
		{
			String sql = "SELECT ID FROM LAND WHERE DATETIME(DATE) BETWEEN DATE('" + formattedDate1 + "') AND DATE('"
					+ formattedDate2 + "')";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	private ArrayList<Integer> filterFormattedDate(ArrayList<Integer> listOfIDs, String formattedDate1,
			String formattedDate2)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String sql =
					"SELECT ID FROM LAND WHERE ID IN (" + ids + ") AND DATETIME(DATE) BETWEEN DATE('" + formattedDate1
							+ "') AND DATE('" + formattedDate2 + "')";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterDate(String lowerDate, String upperDate)
	{
		if(isValidDate(lowerDate) && isValidDate(upperDate))
		{
			return filterFormattedDate(formatDate(lowerDate), formatDate(upperDate));
		}
		else return null;
	}

	public ArrayList<Integer> filterDate(ArrayList<Integer> listOfIDs, String lowerDate, String upperDate)
	{
		if(isValidDate(lowerDate) && isValidDate(upperDate))
		{
			return filterFormattedDate(listOfIDs, formatDate(lowerDate), formatDate(upperDate));
		}
		else return null;
	}

	public ArrayList<Integer> filterByCounty(String[] county)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		try
		{
			String sql = "SELECT ID FROM LAND WHERE ";
			for(int i = 0; i < county.length; i++)
				sql += ((i == 0) ? "" : "OR ") + "COUNTY LIKE '" + county[i].toUpperCase() + "' ";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterByCounty(ArrayList<Integer> listOfIDs, String[] county)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT ID FROM LAND WHERE ID IN (" + ids + ") ";
			for(int i = 0; i < county.length; i++)
				sql += ((i == 0) ? "AND (" : "OR ") + "COUNTY LIKE '" + county[i].toUpperCase() + "' ";
			sql += ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<String> getAddressSuggestion(ArrayList<Integer> listOfIDs, String address)
	{
		ArrayList<String> filteredList = new ArrayList<>();
		String[] separatedAddress = address.split(",");
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT NAME, STREET, LOCALITY, TOWN, DISTRICT, COUNTY FROM LAND WHERE ID IN (" + ids + ") ";
			if(separatedAddress.length >= 1)
				sql += " AND NAME LIKE '" + separatedAddress[0].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 2)
				sql += " AND STREET LIKE '" + separatedAddress[1].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 3)
				sql += " AND LOCALITY LIKE '" + separatedAddress[2].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 4)
				sql += " AND TOWN LIKE '" + separatedAddress[3].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 5)
				sql += " AND DISTRICT LIKE '" + separatedAddress[4].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 6)
				sql += " AND COUNTY LIKE '" + separatedAddress[5].toUpperCase().trim() + "' ";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				String name = resultSet.getString("NAME");
				String street = resultSet.getString("STREET");
				String locality = resultSet.getString("LOCALITY");
				String town = resultSet.getString("TOWN");
				String district = resultSet.getString("DISTRICT");
				String county = resultSet.getString("COUNTY");

				filteredList.add(name + ", " + street + "," + locality + ", " + town + ", " + district + ", " + county);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public ArrayList<Integer> filterByAddress(ArrayList<Integer> listOfIDs, String address)
	{
		ArrayList<Integer> filteredList = new ArrayList<>();
		String[] separatedAddress = address.split(",");
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT ID FROM LAND WHERE ID IN (" + ids + ") ";
			if(separatedAddress.length >= 1)
				sql += " AND NAME LIKE '" + separatedAddress[0].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 2)
				sql += " AND STREET LIKE '" + separatedAddress[1].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 3)
				sql += " AND LOCALITY LIKE '" + separatedAddress[2].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 4)
				sql += " AND TOWN LIKE '" + separatedAddress[3].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 5)
				sql += " AND DISTRICT LIKE '" + separatedAddress[4].toUpperCase().trim() + "' ";
			if(separatedAddress.length >= 6)
				sql += " AND COUNTY LIKE '" + separatedAddress[5].toUpperCase().trim() + "' ";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) filteredList.add(resultSet.getInt("ID"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return filteredList;
	}

	public int getAveragePrice()
	{
		int average = - 1;
		try
		{
			String sql = "SELECT AVG(PRICE) FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				average = resultSet.getInt("AVG(PRICE)");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return average;
	}

	public int getAveragePrice(ArrayList<Integer> listOfIDs)
	{
		int average = - 1;
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT AVG(PRICE) FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				average = resultSet.getInt("AVG(PRICE)");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return average;
	}

	//Returns an array with the count in each division. The divisions are currently evenly spaced.
	public int[] priceChartingData(int lowerLimit, int higherLimit, int divisions)
	{
		int max = higherLimit;
		int min = lowerLimit;
		int division = ((max - min) / divisions);
		int startingPoint = min;
		int[] data = new int[divisions];
		String sql = "SELECT";
		for(int i = 0; i < divisions; i++)
		{
			sql += getPriceDataString(startingPoint, startingPoint + division) + + i + ((divisions != 1
					&& i != divisions - 1) ? "," : "");
			startingPoint += division;
		}
		sql += " FROM LAND";
		try
		{
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				for(int i = 0; i < divisions; i++)
					data[i] = resultSet.getInt("division" + i);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		return data;
	}

	//Returns an array with the count in each type
	public int[] priceChartingData(ArrayList<Integer> listOfIDs, int lowerLimit, int higherLimit, int divisions)
	{
		int max = higherLimit;
		int min = lowerLimit;
		int division = ((max - min) / divisions);
		int startingPoint = min;
		int[] data = new int[divisions];
		String sql = "SELECT";
		String ids = getIDString(listOfIDs);
		for(int i = 0; i < divisions; i++)
		{
			sql += getPriceDataString(startingPoint, startingPoint + division) + i + ((divisions != 1
					&& i != divisions - 1) ? "," : "");
			startingPoint += division;
		}
		sql += " FROM LAND WHERE ID IN (" + ids + ")";
		try
		{
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) for(int i = 0; i < divisions; i++)
				data[i] = resultSet.getInt("division" + i);
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return data;
	}

	public int[] monthChartingData(ArrayList<Integer> listOfIDs)
	{
		int[] data = new int[12];
		try
		{
			String ids = getIDString(listOfIDs);
			String sql = "SELECT SUM( CASE WHEN strftime('%m', DATE) = '01' THEN 1 ELSE 0 END)month1 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '02' THEN 1 ELSE 0 END)month2 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '03' THEN 1 ELSE 0 END)month3 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '04' THEN 1 ELSE 0 END)month4 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '05' THEN 1 ELSE 0 END)month5 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '06' THEN 1 ELSE 0 END)month6\n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '07' THEN 1 ELSE 0 END)month7 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '08' THEN 1 ELSE 0 END)month8 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '09' THEN 1 ELSE 0 END)month9 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '10' THEN 1 ELSE 0 END)month10 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '11' THEN 1 ELSE 0 END)month11\n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '12' THEN 1 ELSE 0 END)month12 \n" + "from LAND"
					+ " WHERE ID IN (" + ids + ")";

			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			for(int month = 1; month <= 12; month++)
			{
				data[month - 1] = resultSet.getInt("month" + month);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return data;
	}

	public DiscreteData monthSalesDiscreteData(ArrayList<Integer> listOfIDs)
	{
		int[] data = monthChartingData(listOfIDs);
		String[] labels = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		return new DiscreteData(data, labels, "Sales by Month");
	}

	public DiscreteData[] monthTypeDiscreteData(ArrayList<Integer> listOfIDs)
	{
		ArrayList<DiscreteData> discreteDatas = new ArrayList<>();
		String[] labels = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		try
		{
			String ids = getIDString(listOfIDs);
			String sql = "SELECT TYPE, SUM( CASE WHEN strftime('%m', DATE) = '01' THEN 1 ELSE 0 END)month1 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '02' THEN 1 ELSE 0 END)month2 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '03' THEN 1 ELSE 0 END)month3 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '04' THEN 1 ELSE 0 END)month4 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '05' THEN 1 ELSE 0 END)month5 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '06' THEN 1 ELSE 0 END)month6\n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '07' THEN 1 ELSE 0 END)month7 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '08' THEN 1 ELSE 0 END)month8 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '09' THEN 1 ELSE 0 END)month9 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '10' THEN 1 ELSE 0 END)month10 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '11' THEN 1 ELSE 0 END)month11\n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '12' THEN 1 ELSE 0 END)month12 \n" + "from LAND"
					+ " WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				String title = resultSet.getString("TYPE");
				int[] data = new int[12];
				for(int month = 1; month <= 12; month++)
				{
					data[month - 1] = resultSet.getInt("month" + month);
				}
				discreteDatas.add(new DiscreteData(data, labels, title));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return discreteDatas.toArray(new DiscreteData[discreteDatas.size()]);
	}

	public DiscreteData[] monthTypeAverageDiscreteData(ArrayList<Integer> listOfIDs)
	{
		ArrayList<DiscreteData> discreteDatas = new ArrayList<>();
		String[] labels = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		try
		{
			String ids = getIDString(listOfIDs);
			String sql = "SELECT TYPE, AVG( CASE WHEN strftime('%m', DATE) = '01' THEN PRICE ELSE null END)month1 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '02' THEN PRICE ELSE null END)month2 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '03' THEN PRICE ELSE null END)month3 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '04' THEN PRICE ELSE null END)month4 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '05' THEN PRICE ELSE null END)month5 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '06' THEN PRICE ELSE null END)month6\n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '07' THEN PRICE ELSE null END)month7 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '08' THEN PRICE ELSE null END)month8 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '09' THEN PRICE ELSE null END)month9 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '10' THEN PRICE ELSE null END)month10 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '11' THEN PRICE ELSE null END)month11\n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '12' THEN PRICE ELSE null END)month12 \n" + "from LAND"
					+ " WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				String title = resultSet.getString("TYPE");
				int[] data = new int[12];
				for(int month = 1; month <= 12; month++)
				{
					data[month - 1] = resultSet.getInt("month" + month);
				}
				discreteDatas.add(new DiscreteData(data, labels, title));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return discreteDatas.toArray(new DiscreteData[discreteDatas.size()]);
	}

	public DiscreteData[] monthConditionAverageDiscreteData(ArrayList<Integer> listOfIDs)
	{
		ArrayList<DiscreteData> discreteDatas = new ArrayList<>();
		String[] labels = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		try
		{
			String ids = getIDString(listOfIDs);
			String sql =
					"SELECT CONDITION, AVG( CASE WHEN strftime('%m', DATE) = '01' THEN PRICE ELSE null END)month1 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '02' THEN PRICE ELSE null END)month2 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '03' THEN PRICE ELSE null END)month3 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '04' THEN PRICE ELSE null END)month4 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '05' THEN PRICE ELSE null END)month5 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '06' THEN PRICE ELSE null END)month6\n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '07' THEN PRICE ELSE null END)month7 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '08' THEN PRICE ELSE null END)month8 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '09' THEN PRICE ELSE null END)month9 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '10' THEN PRICE ELSE null END)month10 \n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '11' THEN PRICE ELSE null END)month11\n"
							+ ",AVG( CASE WHEN strftime('%m', DATE) = '12' THEN PRICE ELSE null END)month12 \n"
							+ "from LAND" + " WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				String title = resultSet.getString("CONDITION");
				int[] data = new int[12];
				for(int month = 1; month <= 12; month++)
				{
					data[month - 1] = resultSet.getInt("month" + month);
				}
				discreteDatas.add(new DiscreteData(data, labels, title));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return discreteDatas.toArray(new DiscreteData[discreteDatas.size()]);
	}

	public DiscreteData[] monthConditionDiscreteData(ArrayList<Integer> listOfIDs)
	{
		ArrayList<DiscreteData> discreteDatas = new ArrayList<>();
		String[] labels = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		try
		{
			String ids = getIDString(listOfIDs);
			String sql = "SELECT CONDITION, SUM( CASE WHEN strftime('%m', DATE) = '01' THEN 1 ELSE 0 END)month1 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '02' THEN 1 ELSE 0 END)month2 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '03' THEN 1 ELSE 0 END)month3 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '04' THEN 1 ELSE 0 END)month4 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '05' THEN 1 ELSE 0 END)month5 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '06' THEN 1 ELSE 0 END)month6\n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '07' THEN 1 ELSE 0 END)month7 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '08' THEN 1 ELSE 0 END)month8 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '09' THEN 1 ELSE 0 END)month9 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '10' THEN 1 ELSE 0 END)month10 \n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '11' THEN 1 ELSE 0 END)month11\n"
					+ ",SUM( CASE WHEN strftime('%m', DATE) = '12' THEN 1 ELSE 0 END)month12 \n" + "from LAND"
					+ " WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				String title = resultSet.getString("CONDITION");
				int[] data = new int[12];
				for(int month = 1; month <= 12; month++)
				{
					data[month - 1] = resultSet.getInt("month" + month);
				}
				discreteDatas.add(new DiscreteData(data, labels, title));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return discreteDatas.toArray(new DiscreteData[discreteDatas.size()]);
	}

	public int[] monthAverageChartingData(ArrayList<Integer> listOfIDs)
	{
		int[] data = new int[12];
		try
		{
			String ids = getIDString(listOfIDs);
			String sql = "SELECT AVG( CASE WHEN strftime('%m', DATE) = '01' THEN PRICE ELSE null END)month1 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '02' THEN PRICE ELSE null END)month2 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '03' THEN PRICE ELSE null END)month3 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '04' THEN PRICE ELSE null END)month4 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '05' THEN PRICE ELSE null END)month5 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '06' THEN PRICE ELSE null END)month6\n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '07' THEN PRICE ELSE null END)month7 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '08' THEN PRICE ELSE null END)month8 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '09' THEN PRICE ELSE null END)month9 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '10' THEN PRICE ELSE null END)month10 \n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '11' THEN PRICE ELSE null END)month11\n"
					+ ",AVG( CASE WHEN strftime('%m', DATE) = '12' THEN PRICE ELSE null END)month12 \n" + "from LAND"
					+ " WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			for(int month = 1; month <= 12; month++)
			{
				data[month - 1] = resultSet.getInt("month" + month);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return data;
	}

	public DiscreteData monthAverageDiscretData(ArrayList<Integer> listOfIDs)
	{
		int[] data = monthAverageChartingData(listOfIDs);
		String[] labels = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		return new DiscreteData(data, labels, "Average Price by Month");
	}

	public DiscreteData dateAmountDiscreteData(ArrayList<Integer> listOfIDs, int divisions)
	{
		int[] data = new int[divisions];
		String[] labels = new String[divisions];

		try
		{
			String earliestDate = simpleDateFormat.format(outputDateFormat.parse(getEarliestDate(listOfIDs)));
			String latestDate = simpleDateFormat.format(outputDateFormat.parse(getLatestDate(listOfIDs)));
			String ids = getIDString(listOfIDs);

			Date startDate = simpleDateFormat.parse(earliestDate);
			Date endDate = simpleDateFormat.parse(latestDate);
			long diff = endDate.getTime() - startDate.getTime();
			diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			int division = (int) (diff / divisions);
			String sql = "SELECT ";
			for(int divisionCount = 1; divisionCount <= divisions; divisionCount++)
			{
				sql += ((divisionCount != 1) ? "," : "") + " SUM(CASE WHEN DATE BETWEEN DATETIME('" + earliestDate
						+ "', '+" + (division - 1) * divisionCount + " days') AND DATETIME('" + earliestDate + "', '+"
						+ division * divisionCount + " days') THEN 1 ELSE 0 END)division" + divisionCount;
				labels[divisionCount - 1] = simpleDateFormat.format(addDays(startDate, (divisionCount * division)));
			}

			sql += " FROM LAND WHERE ID IN (" + ids + ")";

			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) for(int i = 1; i <= divisions; i++)
				data[i - 1] = resultSet.getInt("division" + i);

		}
		catch(Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return new DiscreteData(data, labels, "Number of Properties Sold");
	}

	public DiscreteData dateAverageDiscreteData(ArrayList<Integer> listOfIDs, int divisions)
	{
		int[] data = new int[divisions];
		String[] labels = new String[divisions];

		try
		{
			String earliestDate = simpleDateFormat.format(outputDateFormat.parse(getEarliestDate(listOfIDs)));
			String latestDate = simpleDateFormat.format(outputDateFormat.parse(getLatestDate(listOfIDs)));
			String ids = getIDString(listOfIDs);

			Date startDate = simpleDateFormat.parse(earliestDate);
			Date endDate = simpleDateFormat.parse(latestDate);
			long diff = endDate.getTime() - startDate.getTime();
			diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			int division = (int) (diff / divisions);
			String sql = "SELECT ";
			for(int divisionCount = 1; divisionCount <= divisions; divisionCount++)
			{
				sql += ((divisionCount != 1) ? "," : "") + " AVG(CASE WHEN DATE BETWEEN DATETIME('" + earliestDate
						+ "', '+" + (division - 1) * divisionCount + " days') AND DATETIME('" + earliestDate + "', '+"
						+ division * divisionCount + " days') THEN PRICE ELSE null END)division" + divisionCount;
				labels[divisionCount - 1] = simpleDateFormat.format(addDays(startDate, (divisionCount * division)));
			}

			sql += " FROM LAND WHERE ID IN (" + ids + ")";

			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) for(int i = 1; i <= divisions; i++)
				data[i - 1] = resultSet.getInt("division" + i);

		}
		catch(Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return new DiscreteData(data, labels, "Average Price of Sold Properties");
	}

	private int[] getDataFromSQL(String sql, int[] data)
	{
		try
		{
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) for(int i = 0; i < data.length; i++)
				data[i] = resultSet.getInt("division" + i);
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return data;
	}

	private String getPriceDataString(int lowerPrice, int higherPrice)
	{
		return " SUM(CASE WHEN PRICE >" + lowerPrice + " AND PRICE<" + higherPrice + " THEN 1 ELSE 0 END) division";
	}

	public int[] propertyTypeChartingData(ArrayList<Integer> listOfIDs, String[] types)
	{
		int[] data = new int[types.length];
		String ids = getIDString(listOfIDs);
		String sql = "SELECT" + getPropertyTypeString(types) + " FROM LAND WHERE ID IN (" + ids + ")";
		return getDataFromSQL(sql, data);
	}

	public int[] propertyTypeChartingData(String[] types)
	{
		int[] data = new int[types.length];
		String sql = "SELECT" + getPropertyTypeString(types) + " FROM LAND";
		return getDataFromSQL(sql, data);
	}

	private String getPropertyTypeString(String[] types)
	{
		String s = "";
		for(int i = 0; i < types.length; i++)
		{
			s += " SUM(CASE WHEN TYPE LIKE '" + types[i] + "' THEN 1 ELSE 0 END) division" + i + ((types.length != 1
					&& i != types.length - 1) ? "," : "");
		}
		return s;
	}

	public int[] conditionChartingData()
	{
		int[] data = new int[2];
		String sql = "SELECT SUM(CASE WHEN CONDITION LIKE 'Established' THEN 1 ELSE 0 END) division0,"
				+ " SUM(CASE WHEN CONDITION LIKE 'Newly Built' THEN 1 ELSE 0 END)division1 FROM LAND";
		return getDataFromSQL(sql, data);
	}

	public int[] conditionChartingData(ArrayList<Integer> listOfIDs)
	{
		int[] data = new int[2];
		String ids = getIDString(listOfIDs);
		String sql = "SELECT SUM(CASE WHEN CONDITION LIKE 'Established' THEN 1 ELSE 0 END) division0,"
				+ " SUM(CASE WHEN CONDITION LIKE 'Newly Built' THEN 1 ELSE 0 END)division1 FROM LAND WHERE ID IN ("
				+ ids + ")";
		return getDataFromSQL(sql, data);
	}

	public ArrayList<double[]> getCoordinates()
	{
		ArrayList<double[]> coordinates = new ArrayList<>();
		try
		{
			String sql = "SELECT LATITUDE,LONGITUDE FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				double[] coordinate = new double[] { resultSet.getDouble("LATITUDE"),
						resultSet.getDouble("LONGITUDE") };
				coordinates.add(coordinate);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return coordinates;
	}

	public ArrayList<double[]> getCoordinates(ArrayList<Integer> listOfIDs)
	{
		ArrayList<double[]> coordinates = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT LATITUDE,LONGITUDE FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				double[] coordinate = new double[] { resultSet.getDouble("LATITUDE"),
						resultSet.getDouble("LONGITUDE") };
				coordinates.add(coordinate);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return coordinates;
	}

	public String[] getAllCounties()
	{
		ArrayList<String> listOfCounties = new ArrayList<>();
		try
		{
			String sql = "SELECT DISTINCT COUNTY FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) listOfCounties.add(resultSet.getString("COUNTY"));
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return listOfCounties.toArray(new String[listOfCounties.size()]);
	}

	public String getEarliestDate(ArrayList<Integer> listOfIDs)
	{
		String date = "";
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT MIN(DATE) FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) date = resultSet.getString("MIN(DATE)");
			date = outputDateFormat.format(storedDateFormat.parse(date));
		}
		catch(Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return date;
	}

	public String getLatestDate(ArrayList<Integer> listOfIDs)
	{
		String date = "";
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT MAX(DATE) FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) date = resultSet.getString("MAX(DATE)");
			date = outputDateFormat.format(storedDateFormat.parse(date));
		}
		catch(Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return date;
	}

	public int getMinPrice()
	{
		int min = - 1;
		try
		{
			String sql = "SELECT MIN(PRICE) FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) min = resultSet.getInt("MIN(PRICE)");
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return min;
	}

	public int getMinPrice(ArrayList<Integer> listOfIDs)
	{
		int min = - 1;
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT MIN(PRICE) FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) min = resultSet.getInt("MIN(PRICE)");
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return min;
	}

	public int getMaxPrice()
	{
		int max = - 1;
		try
		{
			String sql = "SELECT MAX(PRICE) FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) max = resultSet.getInt("MAX(PRICE)");
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return max;
	}

	public int getMaxPrice(ArrayList<Integer> listOfIDs)
	{
		int max = - 1;
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT MAX(PRICE) FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) max = resultSet.getInt("MAX(PRICE)");
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return max;
	}

	public int getTotalSize(ArrayList<Integer> list)
	{
		return list.size();
	}

	public int getTotalSize()
	{
		int size = - 1;
		try
		{
			String sql = "SELECT MAX(ID) FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			size = resultSet.getInt("MAX(ID)") + 1;
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return size;
	}

	public Land[] getLandObjects()
	{
		ArrayList<Land> landList = new ArrayList<>();
		try
		{
			String sql = "SELECT * FROM LAND";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				int price = resultSet.getInt("PRICE");
				String date = resultSet.getString("DATE");
				String postCode = resultSet.getString("POSTCODE");
				double latitude = resultSet.getDouble("LATITUDE");
				double longitude = resultSet.getDouble("LONGITUDE");
				String type = resultSet.getString("TYPE");
				String condition = resultSet.getString("CONDITION");
				String name = resultSet.getString("NAME");
				String street = resultSet.getString("STREET");
				String locality = resultSet.getString("LOCALITY");
				String town = resultSet.getString("TOWN");
				String district = resultSet.getString("DISTRICT");
				String county = resultSet.getString("COUNTY");
				landList.add(
						new Land(price, date, postCode, latitude, longitude, type, condition, name, street, locality,
								town, district, county));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return landList.toArray(new Land[] {});
	}

	public Land[] getLandObjects(ArrayList<Integer> listOfIDs)
	{
		ArrayList<Land> landList = new ArrayList<>();
		String ids = getIDString(listOfIDs);
		try
		{
			String sql = "SELECT * FROM LAND WHERE ID IN (" + ids + ")";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				int price = resultSet.getInt("PRICE");
				String date = resultSet.getString("DATE");
				String postCode = resultSet.getString("POSTCODE");
				double latitude = resultSet.getDouble("LATITUDE");
				double longitude = resultSet.getDouble("LONGITUDE");
				String type = resultSet.getString("TYPE");
				String condition = resultSet.getString("CONDITION");
				String name = resultSet.getString("NAME");
				String street = resultSet.getString("STREET");
				String locality = resultSet.getString("LOCALITY");
				String town = resultSet.getString("TOWN");
				String district = resultSet.getString("DISTRICT");
				String county = resultSet.getString("COUNTY");
				landList.add(
						new Land(price, date, postCode, latitude, longitude, type, condition, name, street, locality,
								town, district, county));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return landList.toArray(new Land[] {});
	}

	public String getIDString(ArrayList<Integer> listOfIDs)
	{
		String id = "";
		if(listOfIDs.size() > 0)
		{
			List<String> list = listOfIDs.stream().map(Object::toString).collect(Collectors.toList());
			id = String.join(",", list);
		}
		return id;
	}

	private String getTypeString(String[] types)
	{
		if(types.length > 0)
		{
			String s = "'" + types[0] + "'";
			for(int i = 1; i < types.length; i++)
				s += " OR TYPE LIKE" + "'" + types[i] + "'";
			return s;
		}
		else return "";
	}

	public DiscreteData propertyTypeDiscreteData(String[] types)
	{
		int[] data = new int[types.length];
		for(int i = 0; i < data.length; i++)
		{
			data[i] = filterPropertyType(types[i]).size();
		}
		return new DiscreteData(data, types, "Type of Property Sold");
	}

	public DiscreteData propertyTypeDiscreteData(ArrayList<Integer> listOfIDs, String[] types)
	{
		int[] data = new int[types.length];
		for(int i = 0; i < data.length; i++)
		{
			data[i] = filterPropertyType(listOfIDs, types[i]).size();
		}
		return new DiscreteData(data, types, "Type of Property Sold");
	}

	public DiscreteData conditionDiscreteData()
	{
		int[] data = new int[] { filterCondition("Established").size(), filterCondition("Newly Built").size() };
		return new DiscreteData(data, new String[] { "Established", "Newly Built" }, "Condition of Sold Properties");
	}

	public DiscreteData conditionDiscreteData(ArrayList<Integer> listOfIDs)
	{
		int[] data = new int[] { filterCondition(listOfIDs, "Established").size(),
				filterCondition(listOfIDs, "Newly Built").size() };
		return new DiscreteData(data, new String[] { "Established", "Newly Built" }, "Condition of Sold Properties");
	}

	public DiscreteData priceDiscreteData(ArrayList<Integer> listOfIDS, int lowerLimit, int higherLimit, int divisions)
	{
		if(higherLimit > 2000000) higherLimit = 2000000;
		int[] data = priceChartingData(listOfIDS, lowerLimit, higherLimit, divisions);
		String[] labels = new String[divisions];
		int divisionSize = (higherLimit - lowerLimit) / divisions;
		int startingPrice = lowerLimit + divisionSize;
		DecimalFormat numberFormat = new DecimalFormat("##.#");
		DecimalFormat millionFormat = new DecimalFormat("##.###");
		for(int i = 0; i < divisions; i++)
		{
			if(startingPrice > 1000000) labels[i] = "" + millionFormat.format((double) startingPrice / 1000000.0) + "m";
			else if(startingPrice > 1000) labels[i] = "" + numberFormat.format((double) startingPrice / 1000.0) + "k";
			else labels[i] = "" + startingPrice;
			startingPrice += divisionSize;
		}
		return new DiscreteData(data, labels, "Sale Prices of Properties");
	}

	public DiscreteData priceDiscreteData(int lowerLimit, int higherLimit, int divisions)
	{
		if(higherLimit > 2000000) higherLimit = 2000000;
		int[] data = priceChartingData(lowerLimit, higherLimit, divisions);
		String[] labels = new String[divisions];
		int divisionSize = (higherLimit - lowerLimit) / divisions;
		int startingPrice = lowerLimit + divisionSize;
		DecimalFormat numberFormat = new DecimalFormat("##.#");
		DecimalFormat millionFormat = new DecimalFormat("##.###");
		for(int i = 0; i < divisions; i++)
		{
			if(startingPrice > 1000000) labels[i] = "" + millionFormat.format((double) startingPrice / 1000000.0) + "m";
			else if(startingPrice > 1000) labels[i] = "" + numberFormat.format((double) startingPrice / 1000.0) + "k";
			else labels[i] = "" + startingPrice;
			startingPrice += divisionSize;
		}
		return new DiscreteData(data, labels, "Sale Prices of Properties");
	}

	public HashMap<String, String> getMapDisplayInfo(ArrayList<Integer> listOfIDs)
	{

		LinkedHashMap<String, String> info = new LinkedHashMap<>();
		info.put("Number of Properties", "" + listOfIDs.size());
		String[] types = { Land.TERRACED, Land.DETACHED, Land.SEMI_DETACHED, Land.FLATS_MAISONETTES, Land.OTHER };
		int[] typeData = propertyTypeChartingData(listOfIDs, types);
		for(int i = 0; i < types.length; i++)
		{
			info.put(types[i], "" + typeData[i]);
		}
		int[] conditionData = conditionChartingData(listOfIDs);
		info.put("Established", "" + conditionData[0]);
		info.put("Newly Built", "" + conditionData[1]);
		info.put("Average Price", "£" + getAveragePrice(listOfIDs));
		info.put("Maximum Price", "£" + getMaxPrice(listOfIDs));
		info.put("Minimum Price", "£" + getMinPrice(listOfIDs));
		return info;
	}

}

