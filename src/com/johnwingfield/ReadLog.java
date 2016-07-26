package com.johnwingfield;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

// TODO create file if it doesn't exist

class ReadLog {
	private final String path;

	ReadLog(String file_path) {
		path = file_path;
	}

	Jobs[] OpenFile(Jobs[] jobs) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			int currentLine = 0;

			while ((line = br.readLine()) != null) {
				String str[] = line.split(",");

//				jobs[currentLine] = new Jobs();
//				jobs[currentLine].setProject(str[Globals.PROJECT]);
//				jobs[currentLine].setCode(str[Globals.CODE]);
//				jobs[currentLine].setDate(str[Globals.DATE]);
//				jobs[currentLine].setDuration(str[Globals.DURATION]);
				jobs[currentLine] = new Jobs(str[Globals.PROJECT], str[Globals.CODE], str[Globals.DATE], str[Globals.DURATION]);

				++currentLine;
			}

			br.close();
		}
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Tracker.txt file not found", "Name", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return jobs;
	}

	int CountLines() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		int numberOfLines = 0;

		while ((br.readLine()) != null)
			++numberOfLines;

		br.close();
		return numberOfLines;
	}
}
