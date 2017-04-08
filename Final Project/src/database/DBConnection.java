package database;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class DBConnection {
	private static final String SUCCESS_MESSAGE = "Connected to database successfully";
	private static final String INITILIAZE_MESSAGE = "Initializing database...";
	private static final String DATABASE_EXISTS = "Database already exists, will not build a new one.";
	private static final String FINISHED_DATABASE = "Database has been built with %d queries.\n Took %.2f seconds to build.\n";
	// SQL statement strings
	private static final String CREATE_LAND_TABLE = "CREATE TABLE IF NOT EXISTS LAND " +
													"(ID INT PRIMARY KEY	NOT NULL," +
													" PRICE			INT		NOT NULL," +
													" DATE     		TEXT	NOT NULL," +
													" POSTCODE		TEXT	NOT NULL," +
													" LATITUDE      DOUBLE PRECISION		NOT NULL," +
													" LONGITUDE     DOUBLE PRECISION		NOT NULL," +
													" TYPE			TEXT	NOT NULL," +
													" CONDITION     TEXT	NOT NULL," +
													" NAME     		TEXT	NOT NULL," +
													" STREET		TEXT," +
													" LOCALITY 		TEXT," +
													" TOWN			TEXT	NOT NULL," +
													" DISTRICT		TEXT	NOT NULL," +
													" COUNTY		TEXT	NOT NULL);";
	private static final String INSERT_EXPRESSION = "INSERT INTO LAND";
	
	private static HashMap<String, String> expandedValue = new HashMap<String, String> ();
	
	private Connection connection = null;
	private Statement statement = null;
	
	public DBConnection () {
		expandedValue.put("D", "Detached");
		expandedValue.put("S", "Semi-Detached");
		expandedValue.put("T", "Terraced");
		expandedValue.put("F", "Flats/Maisonettes");
		expandedValue.put("O", "Other");
		
		expandedValue.put("Y", "Newly Built");
		expandedValue.put("N", "Established");
		
		loadDB();
	}
	
	public void loadDB () {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite::resource:land.db");
			statement = connection.createStatement();

			DatabaseMetaData dbm = connection.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "LAND", null);
			if (tables.next()) {
				System.out.println(DATABASE_EXISTS);
			} else {
				buildDB();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println(SUCCESS_MESSAGE);
	}
	
	public void buildDB () throws SQLException {
		long start = System.currentTimeMillis();
		System.out.println(INITILIAZE_MESSAGE);
		statement.executeUpdate(CREATE_LAND_TABLE);
		try {
			LineReader reader = new LineReader("input.csv");
			int id;
			for (id = 0; reader.hasNextLine(); id++) {
				String[] newLine = reader.nextLine();
				
				String insertSQL = INSERT_EXPRESSION + " VALUES (" + id;
				for (int i = 0; i < newLine.length; i++) {
					String formattedValue = i == 5 || i == 6 ? expandedValue.get(newLine[i]) : newLine[i];
					formattedValue = i != 0 ? "'" + formattedValue + "'" : formattedValue;
					
					insertSQL = insertSQL + ", " + formattedValue;
				}
				insertSQL = insertSQL + ");";
				statement.executeUpdate(insertSQL);
			}
			long end = System.currentTimeMillis();
			System.out.printf(FINISHED_DATABASE, id + 1, (double) (end - start) / 1000.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}
}