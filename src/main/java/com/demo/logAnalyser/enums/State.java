package com.demo.logAnalyser.enums;

public enum State {
	STARTED,
	FINISHED,
	UNDEFINED;
	
	public static State getFromString(String s) {
		switch(s) {
		case "STARTED":
		case "started":
			return STARTED;
		case "FINISHED":
		case "finished":
			return FINISHED;
		default:
			return UNDEFINED;	
		}
	}
}