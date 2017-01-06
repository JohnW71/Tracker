package com.johnwingfield;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Create ReadLog class and its methods
 *
 * @author John Wingfield
 */
class ReadLog {
	/**
	 * Open the Tracker.txt file and read job details in jobs[]
	 *
	 * @param jobs array of Jobs[]
	 * @return Jobs[]
	 */
	Jobs[] OpenFile(Jobs[] jobs) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(Globals.fileName));
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

	/**
	 * Count lines in Tracker.txt file so Jobs[] can be created at correct size
	 *
	 * @return int
	 * @throws IOException maybe should be in try-catch?
	 */
	int CountLines() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Globals.fileName));
		int numberOfLines = 0;

		while ((br.readLine()) != null)
			++numberOfLines;

		br.close();
		return numberOfLines;
	}
}
