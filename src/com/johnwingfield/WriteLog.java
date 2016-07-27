/*package com.johnwingfield;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

class WriteLog {
	private final String path;
//	private boolean append_to_file = false;

	WriteLog(String file_path) {
		path = file_path;
	}

//	WriteLog(String file_path, boolean append_value) {
//		path = file_path;
//		append_to_file = true;
//	}
	
	void AddToFile(String textLine) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(path, true))) {
			pw.printf("%s" + "%n",textLine);
		}
		catch (IOException IOe) {
			System.out.println(IOe.getMessage());
		}
	}
}
*/