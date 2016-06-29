package com.johnwingfield;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;

class Tracker extends JPanel implements ActionListener {
	private static JButton bLoad, bSave;
	private static JTextField tJob;

	private Tracker() {
		bLoad = new JButton("Load log file");
		bLoad.setActionCommand("load");
		bLoad.addActionListener(this);

		bSave = new JButton("Save log file");
		bSave.setActionCommand("save");
		bSave.addActionListener(this);
		bSave.setEnabled(false);

		tJob = new JTextField(20);

		add(bLoad);
		add(bSave);
		add(tJob);
	}

	public void actionPerformed(ActionEvent e) {
		String file_name = "C:/Dropbox/Working/Tracker.txt";

		if ("load".equals(e.getActionCommand())) {
			bLoad.setEnabled(false);
			bSave.setEnabled(true);

			try {
				ReadFile file = new ReadFile(file_name);
				String[] aryLines = file.OpenFile();

				for (String aryLine : aryLines)
					System.out.println(aryLine);
			}
			catch (IOException ioE) { // var e of type IOException
				System.out.println(ioE.getMessage());
			}
		}
		else {
			if (tJob.getText().length() > 0) {
				bLoad.setEnabled(true);
				bSave.setEnabled(false);

				try {
					WriteFile data = new WriteFile(file_name);
					data.WriteToFile(tJob.getText());
				} catch (IOException ioE) {
					System.out.println(ioE.getMessage());
				}
			}
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
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				createGUI();
			}
		});
	}
}
