package com.demo.logAnalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.demo.logAnalyser.entity.Event;
import com.demo.logAnalyser.entity.LogEvent;
import com.demo.logAnalyser.enums.State;
import com.demo.logAnalyser.utiltiy.AppLogFileReader;
import com.demo.logAnalyser.utiltiy.DataBaseManager;





public class AppLogManager {



	private static final int FLAG_EVENT_THRESHOLD_MS = 4;
	public static final Logger LOGGER = Logger.getLogger("AppLogManager");
	private static final String DEFAULT_INPUT_FILE = "src/main/resources/input.txt";



	private boolean alert= false;




	private String inputPath;
	//Stores for each id, the list of events that happened. Ex: {"IdA" : [started, finished]}
	private Map<String, List<Event>> linesMap;
	//Stores the list of flagged events to write in DB:
	private List<LogEvent> flaggedEventList = new ArrayList<>();
	private boolean dropExistingTable = false;

	public AppLogManager(String inputPath) {
		this.inputPath = inputPath;		
	}
	
	public void setLinesMap(Map<String, List<Event>> linesMap) {
		this.linesMap = linesMap;
	}
	
	public List<LogEvent> getFlaggedEventList() {
		return flaggedEventList;
	}
	
	public void setDropExistingTable(boolean dropExistingTable) {
		this.dropExistingTable = dropExistingTable;
	}
	
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}
	public boolean isAlert() {return alert;}

	public void setAlert(boolean alert) {this.alert = alert;}
	
	private void initLogger() {
		//Configured to log in the console:
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		 LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.INFO);
	}

	/**
	 * Main business logic of the manager:
	 *  - Reads the log file from its path
	 *  - Check for flagged events
	 *  - If there are flagged events, write them in the db
	 */
	public void processLogEvents() {
		//initLogger();
		AppLogFileReader reader = new AppLogFileReader();
		this.linesMap = reader.readFromFile(inputPath);
		createFlaggedEvents();
		if (!this.flaggedEventList.isEmpty()) {
			DataBaseManager dbmanager = new DataBaseManager();
			dbmanager.startHSQLDB();
			if(dropExistingTable) {
				dbmanager.dropHSQLDBLTable();
			}
			dbmanager.createHSQLDBTable();
			LOGGER.info("Content of db at startup:\n"+dbmanager.readEvents());
			dbmanager.insertEvents(this.flaggedEventList);
			LOGGER.info("Content of db before stopping:\n"+dbmanager.readEvents());
			dbmanager.stopHSQLDB();}	

		}
	
	/**
	 * Browse the existing list of events to find any long event that take longer than 4ms
	 * @return void, stores them in the private attribute flaggedEventLists
	 */

	public void createFlaggedEvents() {
				
		//Browse the map and compute the duration between finished and started if possible:
		for (List<Event> eventList : linesMap.values()) {
			if (eventList.size() >= 2) {
				
				Event startEvent = null;
				Event finishEvent = null;		
				
				for (Event event : eventList) {
					if (event.getState() == State.STARTED) {
						startEvent = event;
					} else if (event.getState() == State.FINISHED) {
						finishEvent = event;
					}
				}
				
				//If there are both 'started' and 'finished' line, add a log Event to the list if applicable
				if (startEvent != null && finishEvent != null) {
					long duration = finishEvent.getTimestamp() - startEvent.getTimestamp();
					if (duration >= FLAG_EVENT_THRESHOLD_MS) {
						setAlert(true);
						LogEvent e = new LogEvent(finishEvent.getId(), duration, finishEvent.getType(), finishEvent.getHost());
						flaggedEventList.add(e);
					}
				}		
			}
		}
	
	}
	
	
	
	public static void main(String[] args) {
		
		//Uses the following default input file if not provided in arguments:
		String inputFilePath=DEFAULT_INPUT_FILE;
		
		AppLogManager logManager = new AppLogManager(inputFilePath);

		// Gives the ability to set the file paths in a command line argument to be able to test different files
		// Command <inputFilePath> <outputFilePath>
		if (args.length > 2) {
			LOGGER.info("Too many arguments provided.");
		} else if (args.length == 2) {
			inputFilePath=args[0];
			if (args[1].equals("-drop")) {
				logManager.setDropExistingTable(true);
			}
		} else if (args.length == 1) {
			inputFilePath=args[0];
		}
		
		logManager.setInputPath(inputFilePath);


		//Business logic:
		logManager.processLogEvents();
		
	}

}
