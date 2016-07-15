package com.johnwingfield;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

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
			values.add(str[0]);
			values.add(str[1]);
			values.add(str[2]);
			values.add(str[3]);
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
