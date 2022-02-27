package com.demo.logAnalyser.entity;

import java.util.Optional;

public class LogEvent {

	private String id;
	private long duration;
	private Optional<String> type;
	private Optional<String> host;


	
	public LogEvent(String id, long duration, Optional<String> type, Optional<String> host ) {
		this.id = id;
		this.duration = duration;
		this.type = type;
		this.host = host;



	}
	
	public Optional<String> getType() {
		return type;
	}
	public void setType(Optional<String> type) {
		this.type = type;
	}
	public Optional<String> getHost() {
		return host;
	}
	public void setHost(Optional<String> host) {
		this.host = host;
	}
	public String getId() {
		return id;
	}
	public long getDuration() {
		return duration;
	}

}
