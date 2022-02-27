package com.demo.logAnalyser.entity;
import java.sql.Statement;
import java.util.Optional;

import com.demo.logAnalyser.enums.State;


import org.json.simple.JSONObject;

/**
 * This class represents a line from the input log file
 */
public class Event {
	
	public static final String JSON_ID = "id";
	public static final String JSON_STATE = "state";
	public static final String JSON_TYPE = "type";
	public static final String JSON_HOST = "host";
	public static final String JSON_TIMESTAMP = "timestamp";
		
	private String id;
	private State state;
	private long timestamp;
	private Optional<String> type;
	private Optional<String> host;
	
	
	public Event(JSONObject jsonObject) {
		this.id = (String) jsonObject.get(JSON_ID);
		this.state = State.getFromString((String) jsonObject.get(JSON_STATE));
		this.timestamp = (long) jsonObject.get(JSON_TIMESTAMP);
		this.type = Optional.ofNullable((String) jsonObject.get(JSON_TYPE));
		this.host = Optional.ofNullable((String) jsonObject.get(JSON_HOST));		
	}


	public String getId() {
		return id;
	}


	public State getState() {
		return state;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public Optional<String> getType() {
		return type;
	}


	public Optional<String> getHost() {
		return host;
	}
}
