package com.demo.logAnalyser.utiltiy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.demo.logAnalyser.AppLogManager;
import com.demo.logAnalyser.entity.Event;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public final class AppLogFileReader {
	
	public Map<String, List<Event>> readFromFile (String inputPath) {
		File file = new File(inputPath);
		return readFromFile(file);
	}
	 
	/**
	 * Reads a log event files and return a map of events grouped by id
	 * @param file
	 * @return map of events grouped by ID
	 */
	private Map<String, List<Event>> readFromFile(File file) {
		Map<String, List<Event>> eventMap = new HashMap<>();
		JSONParser parser = new JSONParser();
		try (Reader is = new FileReader(file)) {

	        BufferedReader bufferedReader = new BufferedReader(is);
	        
	        String currentLine;
	        while((currentLine=bufferedReader.readLine()) != null) {
	            JSONObject logLine = (JSONObject) parser.parse(currentLine);
	            Event event = new Event(logLine);
	            String id = event.getId();
	            if (eventMap.containsKey(id)) {
	            	eventMap.get(id).add(event);
	            } else {
	            	List<Event> eventList = new ArrayList<>();
	            	eventList.add(event);
	            	eventMap.put(id, eventList);
	            }
	        }
	        
		} catch (IOException | ParseException e) {
			( AppLogManager.LOGGER).log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException("Error with input file:"+e);
		} 

		return eventMap;
	}
}
