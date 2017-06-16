package sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class plays the role of generating the SQL scripts based on description
 * in the excel file and a template ContentBlockText.html file.
 * 
 * @author Tran Xuan Hoang
 */
public class SQLGenerator {
	/*
	 * main() method here is for testing purpose.
	 */
	/*public static void main(String[] args) {
		String excelFilePath = "C:\\Users\\qcs5907\\Desktop\\SQLs\\WebAPIカタログカスタマイズ.xlsx";
		String outputFolder = "C:\\Users\\qcs5907\\Desktop\\SQLs";

		try {
			readDataFromExcelFile(excelFilePath);
			generateSQLScripts(outputFolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end main*/

	/** The list of all the names of APIs. */
	static List<String> apiNames = new ArrayList<>();

	/** The list holding all field-value pairs for generating the SQL scripts. */
	static List<TreeMap<String, String>> apiFields = new ArrayList<>();

	/**
	 * Reads all data fields from the Excel file to generate the SQL
	 * script files for all declared APIs.
	 * @param filePath the abstract path of the Excel file.
	 */
	public static void readDataFromExcelFile(String filePath) throws Exception {
		// Open the Excel file to read data
		FileInputStream file = new FileInputStream(new File(filePath));

		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook = new XSSFWorkbook(file);

		// Get desired sheet from the workbook
		// XSSFSheet sheet = workbook.getSheetAt(5);
		// XSSFSheet sheet = workbook.getSheet("SetProductPage");
		readDataFromNormalSheet(workbook.getSheet("CreateProduct"));
		readDataFromNormalSheet(workbook.getSheet("CreateWebService"));
		readDataFromSpecialSheet(workbook.getSheet("SetProductPage"));
		readDataFromNormalSheet(workbook.getSheet("Tag"));
		readDataFromNormalSheet(workbook.getSheet("SetContent"));

		for (int i = 0; i < apiNames.size(); i++) {
			System.out.println(apiNames.get(i));
			System.out.println(apiFields.get(i));
			System.out.println();
		}
	}

	/**
	 * This methods is used to read the following sheets in the given
	 * Excel file (sheets that have the same declaration structure):
	 * <ul>
	 * <li>CreateProduct
	 * <li>CreateWebService
	 * <li>Tag
	 * <li>SetContent
	 * </ul>
	 * @param sheet the sheet in the Excel file to be read.
	 */
	private static void readDataFromNormalSheet(XSSFSheet sheet) {
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();

		// Process first row
		// Read the first row
		Row firstRow = rowIterator.next();

		// Check if we need to create a list of APIs from the first row
		if (apiNames.isEmpty()) {
			Iterator<Cell> firstRowCellIterator = firstRow.cellIterator();

			// Loop through the first row and create a hash map that associates
			// each API name (equivalent to ID or SQL file name) with its
			// corresponding hash map holding all information necessary for
			// generating the SQL script file
			while (firstRowCellIterator.hasNext()) {
				Cell cell = firstRowCellIterator.next();

				String cellContent = cell.getStringCellValue().trim();

				if (cellContent.length() > 0 && !apiNames.contains(cellContent)) {
					apiNames.add(cellContent);
					apiFields.add(new TreeMap<String, String>());
				}
			}
		}

		// process the remaining rows
		while (rowIterator.hasNext()) {
			readEachRow(rowIterator);
		}
	} // end method readDataFromNormalSheet

	/**
	 * This method is used to read the <b>Tag</b> sheet in the given
	 * Excel file.
	 * @param sheet the sheet in the Excel file to be read.
	 */
	private static void readDataFromSpecialSheet(XSSFSheet sheet) {
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();

		// Read the first row "１．DeveloperResources"
		rowIterator.next();

		// Read the second row "The upper table's heading"
		rowIterator.next();

		// Process the next 4 rows "The first table"
		for (int i = 0; i < 4; i++) {
			readEachRow(rowIterator);
		}

		// Read "2．ProductOverview"
		rowIterator.next();

		// Read "The bottom table's heading"
		rowIterator.next();

		// Process the next 4 rows "The second table"
		for (int i = 0; i < 4; i++) {
			readEachRow(rowIterator);
		}
	} // end method readDataFromSpecialSheet

	/**
	 * Reads each row in the sheet of the give Excel file.
	 * @param rowIterator the iterator that will be used to iterate
	 * throw the row for getting the value in each cell and saving
	 * that value to the corresponding field in <code>apiFields</code>.
	 */
	private static void readEachRow(Iterator<Row> rowIterator) {
		Row row = rowIterator.next();
		List<String> rowElements = new ArrayList<>();

		// For each row, iterate through all the columns
		Iterator<Cell> cellIterator = row.cellIterator();

		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();

			String cellContent = cell.getStringCellValue().trim();
			rowElements.add(cellContent);

			//System.out.println(cellContent);
		}

		// Insert value into the apiFields
		String fieldKey = rowElements.get(0);

		for (int i = 1; i < rowElements.size(); i++) {
			TreeMap<String, String> apiField = apiFields.get(i - 1);

			// if the field hasn't appeared before, add it
			// into the tree map that holds all fields (information)
			// for generating the SQL script file
			if (!apiField.containsKey(fieldKey)) {
				String fieldValue = rowElements.get(i).trim();

				if (fieldValue.length() > 0)
					apiField.put(fieldKey, fieldValue);
			}
		}
	} // end method readEachRow

	/**
	 * Retrieves all the tags attached with the given API.
	 * @param apiName the name of the API (same as the name of the
	 * API's SQL file, or the heading of the table in each sheet
	 * of the Excel file).
	 * @return a String (i.e. <b>'Equities','CloudAPIs','RealTime'</b>)
	 * containing all the tags in the form that is ready for
	 * inserting into the SQL script.
	 */
	private static String getTags(String apiName) {
		// Get the index of the file
		int index = getIndexOfAPI(apiName);

		// Get the TreeMap containing all the field of the API
		if (index == -1)
			return null;
		else {
			TreeMap<String, String> fields = apiFields.get(index);
			Set<String> keys = fields.keySet();

			String tags = "";

			for (String key : keys) {
				if (key.startsWith("Tag")) {
					tags += "'" + fields.get(key) + "',";
				}
			}

			if (tags.length() > 0) {
				tags = tags.substring(0, tags.length() - 1);
			}

			return tags;
		}
	} // end method getTags

	public static void generateSQLScripts(String outputFolderPath) throws IOException {
		for (String apiName : apiNames) {
			// Generate the content of the SQL file
			String sql = generateSQLProcedure(apiName);

			System.out.println(sql + "\n\n");

			// Save the generated content to the SQL file in hard disk.
			FileOutputStream fos = new FileOutputStream(outputFolderPath + "\\" + apiName + ".sql");
			Writer out = new OutputStreamWriter(fos, "UTF8");
			out.write(sql);
			out.close();
		}
	} // end method generateSQLScripts

	/**
	 * Generates the content for one SQL file that contains all stored procedures
	 * for updating/adding new API.
	 * @param apiName the name of the API to be added.
	 * @return a String containing content of the SQL file.
	 */
	protected static String generateSQLProcedure(String apiName) {
		// Get the fields holding all information necessary
		// for generating SQL file
		TreeMap<String, String> fields = apiFields.get(getIndexOfAPI(apiName));

		// Get new line code using the system's one
		String nl = System.getProperty("line.separator");

		String sql = "-- Stored SQL Procedures for " + apiName + nl + nl +
				"-- Create Product" + nl +
				"exec CreateProduct " + fields.get("PlatformId") + ", " +
				"'" + fields.get("ProductGroupName") + "', " +
				"'" + fields.get("ProductTypeName") + "', " +
				"'" + fields.get("Identifier") + "', " +
				"'" + fields.get("EntitlementString") + "', " +
				"'" + fields.get("Name") + "', " + nl +
				"N'" + fields.get("Description") + "', " +
				fields.get("Version") + ", " +
				"'" + fields.get("UrlKeyword") + "', " +
				"'" + fields.get("ProductUrl") + "', " + nl +
				"0, " + //fields.get("IsActive") + ", " +
				fields.get("SortPriority") + ", " +
				fields.get("Availability") + ", " +
				"N'" + fields.get("BusinessIdentifier") + "'" + nl + nl +
				"update product set isactive = " + fields.get("IsActive") + " where identifier = " +
				"'" + fields.get("Identifier") + "'" + nl + nl + nl;

		sql += "-- Create Web Service" + nl +
				"exec CreateWebService " +
				"'" + fields.get("Identifier") + "', " +
				fields.get("Version") + ", " +
				fields.get("PlatformId") + ", " +
				"'" + fields.get("EndPointUrl") + "'" + nl + nl + nl;

		sql += "-- Set Produc Page" + nl +
				"-- Create the Developer Resources page which automatically generates the API list and test form pages" + nl +
				"exec SetProductPage " +
				fields.get("PlatformId") + ", " +
				"'" + fields.get("ProductIdentifier") + "', " +
				fields.get("ProductVersion") + ", " +
				"'" + fields.get("PageTypeNameDR") + "'" + nl + nl;

		sql += "-- Create the Product Overview page that has the data coverage and other product information" + nl +
				"-- The information is inserted using another stored procedure below called SetContent" + nl +
				"exec SetProductPage " +
				fields.get("PlatformId") + ", " +
				"'" + fields.get("ProductIdentifier") + "', " +
				fields.get("ProductVersion") + ", " +
				"'" + fields.get("PageTypeNamePO") + "'" + nl + nl + nl;

		sql += "declare @pid bigint" + nl +
				"select @pid = productid from Product where Identifier = '" +
				fields.get("Identifier") +
				"' and Version = " +
				fields.get("Version") +
				" and PlatformId = " +
				fields.get("PlatformId") + nl + nl + nl;

		sql += "-- Tags" + nl +
				"insert into ProductTag" + nl +
				"select @pid, tagid" + nl +
				"from Tag" + nl +
				"where tagname in (" +
				getTags(apiName) +
				")" + nl + nl + nl;

		String contentBlockText = getContentBlockText();

		sql += "-- Set Content for the Product Overview page" + nl +
				"-- Run this stored procedure multiple times to update product content" + nl +
				"exec SetContent " +
				fields.get("PlatformId") + ", " +
				"'" + fields.get("ProductIdentifier") + "', " +
				fields.get("ProductVersion") + ", " +
				"'" + fields.get("ContentBlockIdentifier") + "', " +
				"N'" + contentBlockText + "'" + nl +
				", " + "'" + fields.get("PageTypeName") + "'";

		return sql;
	} // end method generateSQLScript

	/**
	 * Reads the <b>ContentBlockText</b> field from the
	 * <code>ContentBlockText.html</code> file.
	 * @return a String for passing as the <code>ContentBlockText</code>
	 * parameter to the SQL command.
	 */
	private static String getContentBlockText() {
		StringBuffer buffer = new StringBuffer();

		try {
			String contentBlockTextFilePath = System.getProperty("user.dir") +
					"\\ContentBlockText.html";
			// NOTE This method requires that the ContentBlockText.html file
			// must be placed in the same folder where the application is run
			
			FileInputStream fis = new FileInputStream(contentBlockTextFilePath
					/*"C:\\Users\\qcs5907\\workspace\\SQLScriptAutomaticGenerator\\bin\\sql\\ContentBlockText.html"*/);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			Reader in = new BufferedReader(isr);
			int ch;

			while ((ch = in.read()) > -1) {
				buffer.append((char)ch);
			}

			in.close();
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	} // end method getContentBlockText

	/**
	 * Finds the index of the given API in the list of APIs
	 * <code>apiNames</code>.
	 * @param apiName the name of the API to be searched.
	 * @return the index of the API to be found out.
	 * <code>-1</code> otherwise.
	 */
	private static int getIndexOfAPI(String apiName) {
		// Get the index of the API
		int index = -1;

		for (int i = 0; i < apiNames.size(); i++) {
			if (apiNames.get(i).equals(apiName)) {
				index = i;
				break;
			}
		}

		return index;
	} // end method getIndexOfAPI
} // end class SQLGenerator