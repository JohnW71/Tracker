package com.johnwingfield;

import javafx.beans.property.SimpleStringProperty;

public class Jobs {
	private final SimpleStringProperty project;
	private final SimpleStringProperty code;
	private final SimpleStringProperty date;
	private final SimpleStringProperty duration;
//	private String project;
//	private String code;
//	private String date;
//	private String duration;

	Jobs(String p, String c, String d, String t) {
		this.project = new SimpleStringProperty(p);
		this.code = new SimpleStringProperty(c);
		this.date = new SimpleStringProperty(d);
		this.duration = new SimpleStringProperty(t);
	}

//	void setProject(String p) {
//		project = p;
//	}
//
//	String getProject() {
//		return project;
//	}
//
//	void setProject(String p) {
//		project.set(p);
//	}
//
	String getProject() {
		return project.get();
	}

	public SimpleStringProperty projectProperty() {
		return project;
	}

//	void setCode(String c) {
//		code = c;
//	}
//
//	String getCode() {
//		return code;
//	}
//
//	void setCode(String c) {
//		code.set(c);
//	}
//
	String getCode() {
		return code.get();
	}

	public SimpleStringProperty codeProperty() {
		return code;
	}

//	void setDate(String d) {
//		date = d;
//	}
//
//	String getDate() {
//		return date;
//	}
//
//	void setDate(String d) {
//		date.set(d);
//	}
//
	String getDate() {
		return date.get();
	}

	public SimpleStringProperty dateProperty() {
		return date;
	}

//	void setDuration(String t) {
//		duration = t;
//	}
//
//	String getDuration() {
//		return duration;
//	}
//
//	void setDuration(String t) {
//		duration.set(t);
//	}
//
	String getDuration() {
		return duration.get();
	}

	public SimpleStringProperty durationProperty() {
		return duration;
	}
}
