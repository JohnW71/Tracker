package com.johnwingfield;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

class ReadFile {
	private final String path;

	ReadFile(String file_path) {
		path = file_path;
	}

	String[] openFile() throws IOException {
		int numberOfLines = readLines();
		String[] textData = new String[numberOfLines];
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		for (int i = 0; i < numberOfLines; ++i)
			textData[i] = br.readLine();

		br.close();
		return textData;
	}

	private int readLines() throws IOException {
		int numberOfLines = 0;
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		while ((br.readLine()) != null)
			++numberOfLines;

		br.close();

		return numberOfLines;
	}
}
