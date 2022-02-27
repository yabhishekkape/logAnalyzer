package com.demo.logAnalyser.utiltiy;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;

import com.demo.logAnalyser.AppLogManager;
import com.demo.logAnalyser.entity.LogEvent;

import org.hsqldb.Server;





public class DataBaseManager {

	private static final String DB_NAME = "events";
	private static final String DB_PATH = "file:events";
	private static final String HOSTNAME = "localhost";
	private static final String TABLE_NAME = "alerts";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(id INTEGER IDENTITY PRIMARY KEY, logID VARCHAR(50) NOT NULL, duration BIGINT, host VARCHAR(50), type VARCHAR(50), alert BOOLEAN DEFAULT TRUE NOT NULL)";

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	private  boolean alert=true;
	private Server hsqlServer = new Server();
	private Connection connection;

	/**
	 * Starts the HSQL DB Server directly from the application
	 */
	public void startHSQLDB() {

		hsqlServer.setLogWriter(null);
		hsqlServer.setSilent(true);

		hsqlServer.setDatabaseName(0, DB_NAME);
		hsqlServer.setDatabasePath(0, DB_PATH);

		Logger.getLogger("hsqldb.db").setLevel(Level.WARNING);
		System.setProperty("hsqldb.reconfig_logging", "false");
		hsqlServer.start();
		AppLogManager.LOGGER.info("HSQL Server started");
	}

	/**
	 * Closes the existing connection and stops the HSQLDB server
	 */
	public void stopHSQLDB() {

		if (connection != null) {
			try {
				connection.close();
				AppLogManager.LOGGER.info("Connection to HSQL Server closed");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		hsqlServer.stop();
		AppLogManager.LOGGER.info("HSQL Server stopped");
	}

	/**
	 * Util method to create a connection
	 * @return opened connection to the db
	 */
	private Connection openConnection() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			return DriverManager.getConnection("jdbc:hsqldb:hsql://"+HOSTNAME+"/"+DB_NAME, "SA", "");
		} catch (SQLException | ClassNotFoundException e) {
			AppLogManager.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Util method to drop the existing HSQL Table
	 */
	public void dropHSQLDBLTable() {
		Statement stmt = null;

		try {
			if (connection == null) {
				connection = openConnection();
			}
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE "+TABLE_NAME);

		}  catch (SQLException e) {
			AppLogManager.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Creates the alerts table if not already existing
	 */
	public void createHSQLDBTable() {
		Statement stmt = null;

		try {
			if (connection == null) {
				connection = openConnection();
			}

			//Check first if table exists
			DatabaseMetaData dbm = connection.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME, new String[] {"TABLE"});

			if (!tables.next()) {
				stmt = connection.createStatement();
				stmt.executeUpdate(TABLE_CREATION_SQL);
				AppLogManager.LOGGER.fine("Table "+TABLE_NAME+" created");
			}

		}  catch (SQLException  e) {
			AppLogManager.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Inserts a list of events to the database
	 * @param eventList
	 */
	public void insertEvents(List<LogEvent> eventList) {
		Statement stmt = null;

		int insertedRows = 0;

		try {
			if (connection == null) {
				connection = openConnection();
			}
			stmt = connection.createStatement();

			for (LogEvent e : eventList) {

				//Prevent insertion if ID already exists:
				ResultSet existingRow = stmt.executeQuery("SELECT * FROM "+TABLE_NAME+" WHERE logID='"+e.getId()+"'");

				if (!existingRow.next()) {

					String updateStatement = "INSERT INTO "+TABLE_NAME+" (logID, duration, host, type) VALUES('"+e.getId()+"',"+e.getDuration()+",'"+e.getHost().orElse("")+"','"+e.getType().orElse("")+"')";
					insertedRows += stmt.executeUpdate(updateStatement);
				} else {
					AppLogManager.LOGGER.fine("Row not inserted row as ID "+e.getId()+ " already exists");
				}

			}
			connection.commit();
		} catch (SQLException e) {
			AppLogManager.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		AppLogManager.LOGGER.fine(insertedRows+" rows inserted in the db");
	}

	/**
	 * Returns a display-friendly string showing the content of the alerts table
	 * @return elements of the alerts db to be displayed
	 */
	public String readEvents() {
		StringBuilder sb  = new StringBuilder();
		Statement stmt = null;
		ResultSet result;

		if (connection == null) {
			connection = openConnection();
		}

		try {
			stmt = connection.createStatement();
			result = stmt.executeQuery("SELECT * FROM "+TABLE_NAME);
			ResultSetMetaData rsmd = result.getMetaData();


			while (result.next()) {
				for (int i = 1; i <= 5; i++) {
					if (i > 1) {
						sb.append(",  ");
					}
					String columnValue = result.getString(i);
					sb.append(rsmd.getColumnName(i)+":"+columnValue);
				}
				sb.append("\n");
			}
		} catch (SQLException e) {
			AppLogManager.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return sb.toString();

	}

}
