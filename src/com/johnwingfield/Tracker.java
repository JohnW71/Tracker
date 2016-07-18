package com.johnwingfield;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tracker extends Application {
	private long startTime = 0;
	private String file_name = "C:/Dropbox/Working/Tracker.txt";

	public void start(Stage myStage) {
		myStage.setTitle("Tracker");
		FlowPane rootNode = new FlowPane(10, 10);
		rootNode.setAlignment(Pos.CENTER);
		Scene myScene = new Scene(rootNode, 300, 200);
		myStage.setScene(myScene);

		Button bLoad = new Button("Load log file");
		Button bSave = new Button("Save project/code");
		bSave.setDisable(true);

		Button bStart = new Button("Start timer");
		Button bStop = new Button("Stop timer");
		bStop.setDisable(true);

		TextField tJob = new TextField();
		tJob.setPromptText("project");
		tJob.setPrefColumnCount(20);

		TextField tCode = new TextField();
		tCode.setPromptText("code");
		tCode.setPrefColumnCount(10);

		TextField tDate = new TextField();
		tDate.setPromptText("ddmmyy");
		tDate.setPrefColumnCount(6);

		TextField tDuration = new TextField();
		tDuration.setPromptText("1");
		tDuration.setPrefColumnCount(10);

		bLoad.setOnAction(ae -> {
			bLoad.setDisable(true);
			bSave.setDisable(false);

			try {
				ReadLog file = new ReadLog(file_name);
				List<String> aryLines = new ArrayList<>(file.OpenFile());
				aryLines.forEach(System.out::println);
			}
			catch (Exception IOe) {
				System.out.println(IOe.getMessage());
			}
		});

		bSave.setOnAction(ae -> {
			if (tJob.getText().length() > 0) {
				bLoad.setDisable(false);
				bSave.setDisable(true);

				try {
					WriteLog data = new WriteLog(file_name);
					data.AddToFile( tJob.getText() + "," +
									tCode.getText() + "," +
									tDuration.getText() +"," +
									tDate.getText());
				}
				catch (IOException IOe) {
					System.out.println(IOe.getMessage());
				}
			}
		});

		bStart.setOnAction(ae -> {
			bStart.setDisable(true);
			bStop.setDisable(false);

			startTime = System.currentTimeMillis();
		});

		bStop.setOnAction(ae -> {
			bStart.setDisable(false);
			bStop.setDisable(true);

			long currentTime = System.currentTimeMillis();
			long duration = (currentTime - startTime) / 1000;
			int seconds = (int) duration % 60;
			int minutes = (int) (duration / 60) % 60;
			int hours   = (int) (duration / 3600) % 24;

			tDuration.setText(String.valueOf(duration));
			System.out.printf("%02d:%02d:%02d\n", hours, minutes, seconds);
		});

		rootNode.getChildren().addAll(bLoad, bSave, tJob, tCode, tDate, bStart, bStop, tDuration);
		myStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
