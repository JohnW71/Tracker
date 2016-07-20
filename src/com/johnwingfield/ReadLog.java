package com.johnwingfield;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

// TODO create file if it doesn't exist

class ReadLog {
	private final String path;

	ReadLog(String file_path) {
		path = file_path;
	}

	List<String> OpenFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		List<String> values = new ArrayList<>();

		while ((line = br.readLine()) != null) {
			String str[] = line.split(",");
			values.add(str[Globals.PROJECT]);
			values.add(str[Globals.CODE]);
			values.add(str[Globals.DATE]);
			values.add(str[Globals.DURATION]);
		}

		br.close();
		return values;
	}

/*	private int ReadLines() throws IOException {
		int numberOfLines = 0;
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		while ((br.readLine()) != null)
			++numberOfLines;

		br.close();
		return numberOfLines;
	}*/
}