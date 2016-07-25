package com.johnwingfield;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

// TODO create file if it doesn't exist

class ReadLog {
	private final String path;

	ReadLog(String file_path) {
		path = file_path;
	}

/*	List<String> OpenFile() throws IOException {
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
	}*/

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
		catch (IOException e) {
			e.printStackTrace();
		}

		return jobs;
	}

	int CountLines() throws IOException {
		int numberOfLines = 0;
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		while ((br.readLine()) != null)
			++numberOfLines;

		br.close();
		return numberOfLines;
	}
}
