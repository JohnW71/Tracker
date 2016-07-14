package com.johnwingfield;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

class Tracker extends JPanel implements ActionListener {
	private static JButton bLoad, bSave, bStart, bStop;
	private static JTextField tJob, tCode, tDuration;
	private long startTime = 0;

	private Tracker() {
		bLoad = new JButton("Load log file");
		bLoad.setActionCommand("load");
		bLoad.addActionListener(this);

		bSave = new JButton("Save project/code");
		bSave.setActionCommand("save");
		bSave.addActionListener(this);
		bSave.setEnabled(false);

		tJob = new JTextField("project", 20);
		tCode = new JTextField("code", 10);

		bStart = new JButton("Start timer");
		bStart.setActionCommand("start");
		bStart.addActionListener(this);

		bStop = new JButton("Stop timer");
		bStop.setActionCommand("stop");
		bStop.addActionListener(this);
		bStop.setEnabled(false);

		tDuration = new JTextField("1", 10);

		add(bLoad);
		add(bSave);
		add(tJob);
		add(tCode);
		add(bStart);
		add(bStop);
		add(tDuration);
	}

	public void actionPerformed(ActionEvent e) {
		String file_name = "C:/Dropbox/Working/Tracker.txt";

		switch(e.getActionCommand()) {
			case "load":
				bLoad.setEnabled(false);
				bSave.setEnabled(true);

				try {
/*					ReadFile file = new ReadFile(file_name);
					String[] aryLines = file.openFile();

					for (String aryLine : aryLines)
						System.out.println(aryLine);*/

					BufferedReader br = new BufferedReader(new FileReader(file_name));
					String line;

					while ((line = br.readLine()) != null) {
						String[] values = line.split(",");

						for (String str : values) {
							System.out.println(str);
						}
					}

					br.close();
				}
				catch (IOException IOe) {
					System.out.println(IOe.getMessage());
				}
				break;
			case "save":
				if (tJob.getText().length() > 0) {
					bLoad.setEnabled(true);
					bSave.setEnabled(false);

					try {
						WriteFile data = new WriteFile(file_name);
						data.addToFile(tJob.getText() + "," + tCode.getText() + "," + tDuration.getText());
					} catch (IOException IOe) {
						System.out.println(IOe.getMessage());
					}
				}
				break;
			case "start":
				bStart.setEnabled(false);
				bStop.setEnabled(true);
				startTime = System.currentTimeMillis();
				break;
			case "stop":
				bStart.setEnabled(true);
				bStop.setEnabled(false);
				long currentTime = System.currentTimeMillis();
				long duration = (currentTime - startTime) / 1000;
				int seconds = (int) duration % 60;
				int minutes = (int) (duration / 60) % 60;
				int hours   = (int) (duration / 3600) % 24;

				tDuration.setText(String.valueOf(duration));
				System.out.printf("%02d:%02d:%02d\n", hours, minutes, seconds);
				break;
		}
	}

	// Create the GUI and show it.
	private static void createGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the frame.
		JFrame frame = new JFrame("Tracker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		Tracker contentPane = new Tracker();
		contentPane.setOpaque(true); // content panes must be opaque
		frame.getRootPane().setDefaultButton(bLoad);
		frame.setContentPane(contentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(Tracker::createGUI);
	}
}
