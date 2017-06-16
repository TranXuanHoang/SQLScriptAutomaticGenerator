package sql;

/**
 * This class is used to execute SQL scripts on a database.
 * 
 * @author Tran Xuan Hoang
 */
public class SQLExecutor {
	/**
	 * Executes a SQL script to add/update information relating to the added/updated API.
	 * @param serverName the name of the server where the database is set up.
	 * @param dbName the name of the database to which the script will be executed.
	 * @param filePath the path of the SQL script to be executed.
	 * @return <code>true</code> if the SQL script was successfully executed. <code>false</code> otherwise.
	 * @throws Exception if an error occurs while executing the SQL script.
	 */
	public static boolean runSQLScript(String serverName, String dbName, String filePath) {
		try {
			String[] sqlcmd = new String[] {"sqlcmd", "-S", serverName,
					"-d", dbName, "-E", "-f", "65001", "-i", filePath};

			Process process = Runtime.getRuntime().exec(sqlcmd);

			process.waitFor();
			int exitCode = process.exitValue();

			if (exitCode == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	} // end method runSQLScript
} // end class SQLExecutor