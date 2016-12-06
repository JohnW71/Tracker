package com.johnwingfield;

import javafx.beans.property.SimpleStringProperty;

/**
 * Create Jobs class and its properties
 *
 * @author John Wingfield
 */
public class Jobs { // this is Public to allow the ObservableArrayList to view Jobs' data
	private final SimpleStringProperty project;
	private final SimpleStringProperty code;
	private final SimpleStringProperty date;
	private final SimpleStringProperty duration;

	Jobs(String p, String c, String d, String t) {
		this.project = new SimpleStringProperty(p);
		this.code = new SimpleStringProperty(c);
		this.date = new SimpleStringProperty(d);
		this.duration = new SimpleStringProperty(t);
	}

	void setProject(String p) {
		project.set(p);
	}

	String getProject() {
		return project.get();
	}

	public SimpleStringProperty projectProperty() {
		return project;
	}

	void setCode(String c) {
		code.set(c);
	}

	String getCode() {
		return code.get();
	}

	public SimpleStringProperty codeProperty() {
		return code;
	}

	void setDate(String d) {
		date.set(d);
	}

	String getDate() {
		return date.get();
	}

	public SimpleStringProperty dateProperty() {
		return date;
	}

	void setDuration(String t) {
		duration.set(t);
	}

	String getDuration() {
		return duration.get();
	}

	public SimpleStringProperty durationProperty() {
		return duration;
	}
}
