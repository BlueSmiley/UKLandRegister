package database;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LineReader {
	private static final String VALUE_REGEX = "\"(%s)\",";
	private static final String[] REGULAR_EXPRESSIONS = {
		"\\{[A-Z0-9-]+\\}",																	// Unique Identifier
		"\\d+",																				// Price
		"((19|20)\\d{2})-([0]?[1-9]|[1][0-2])-([3][01]|[12][0-9]|[0]?[1-9])\\s[0:]{5}",		// Date (YYYY-MM-DD 00:00)
		"[A-Z0-9]+\\s[A-Z0-9]+",															// Post code
		"D|S|T|F|O",																		// Property Type
		"Y|N",																				// Condition
		"[A-Z0-9 ,-]*"																		// Other fields
	};
	private static final int UNIQUE_INDEX = 6;
	private static final int[] CORRESPONDS_TO = {1,2,3,0,0,4,5,7,9,10,11,12,13};
	
	private Scanner reader = null;
	
	LineReader (String filePath) throws IOException {
		reader = new Scanner (new File (filePath));
	}
	
	public String[] nextLine () {
		String line = reader.nextLine();
		String[] readValues = new String[16];
		for (int i = 0; i < readValues.length; i++) {
			String pattern = i < UNIQUE_INDEX ? REGULAR_EXPRESSIONS[i] : REGULAR_EXPRESSIONS[UNIQUE_INDEX];
			pattern = String.format(VALUE_REGEX, pattern);
			Matcher matcher = Pattern.compile(pattern).matcher(line);
			if (matcher.find()) {
				readValues[i] = matcher.group(1);
			} else {
				readValues[i] = "";
			}

			line = line.replaceFirst(pattern, "");
		}
		
		String[] values = new String [13];
		for (int i = 0; i < values.length; i++) {
			if (i < 3 || i > 4) {
				values[i] = readValues[CORRESPONDS_TO[i]];
				values[i] = values[i].equals("A") ? "" : values[i];
			} else {
				double[] coords = CoordinateConverter.convertPostcode(values[2]);
				values[3] = Double.toString(coords[0]);
				values[4] = Double.toString(coords[1]);
				i = 4;
			}
		}
		return values;
	}
	
	public boolean hasNextLine () {
		return reader.hasNextLine();
	}
}
