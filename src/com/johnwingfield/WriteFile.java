package com.johnwingfield;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

class WriteFile {
	private final String path;
//	private boolean append_to_file = false;

	// constructor
	WriteFile(String file_path) {
		path = file_path;
	}

	// constructor, if append value is passed when object created
//	WriteFile(String file_path, boolean append_value) {
//		path = file_path;
//		append_to_file = true;
//	}
	
	void addToFile(String textLine) throws IOException {
		try (PrintWriter pw = new PrintWriter(new FileWriter(path, true))) {
			pw.printf("%s" + "%n",textLine);
		} catch (IOException IOe) {
			System.out.println(IOe.getMessage());
		}
	}
}
