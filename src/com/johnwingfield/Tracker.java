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
		myStage.setResizable(false);

		Group rootNode = new Group();
		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5));
		gridpane.setHgap(10);
		gridpane.setVgap(10);

		Scene myScene = new Scene(rootNode, 600, 400);

		Button bLoad = new Button("Load log file");
		GridPane.setHalignment(bLoad, HPos.CENTER);
		gridpane.add(bLoad, 0, 0);

		Button bSave = new Button("Save project/code");
		GridPane.setHalignment(bSave, HPos.CENTER);
		gridpane.add(bSave, 1, 0);
		bSave.setDisable(true);

		Button bStart = new Button("Start timer");
		GridPane.setHalignment(bStart, HPos.CENTER);
		gridpane.add(bStart, 0, 1);

		TextField tDuration = new TextField();
		tDuration.setPromptText("1");
		tDuration.setPrefColumnCount(8);
		GridPane.setHalignment(tDuration, HPos.CENTER);
		gridpane.add(tDuration, 1, 1);

		Button bStop = new Button("Stop timer");
		GridPane.setHalignment(bStop, HPos.CENTER);
		gridpane.add(bStop, 2, 1);
		bStop.setDisable(true);

		TextField tJob = new TextField();
		tJob.setPromptText("project");
		tJob.setPrefColumnCount(20);
		GridPane.setHalignment(tJob, HPos.CENTER);
		gridpane.add(tJob, 0, 2);

		TextField tCode = new TextField();
		tCode.setPromptText("code");
		tCode.setPrefColumnCount(15);
		GridPane.setHalignment(tCode, HPos.CENTER);
		gridpane.add(tCode, 1, 2);

		TextField tDate = new TextField();
		tDate.setPromptText("ddmmyy");
		tDate.setPrefColumnCount(6);
		GridPane.setHalignment(tDate, HPos.CENTER);
		gridpane.add(tDate, 2, 2);

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

		rootNode.getChildren().add(gridpane);

		GridPane gridpane2 = new GridPane();
		gridpane2.setPadding(new Insets(5));
		gridpane2.setHgap(10);
		gridpane2.setVgap(19);

		Label lProject1 = new Label("Project1");
		GridPane.setHalignment(lProject1, HPos.CENTER);
		gridpane2.add(lProject1, 0, 0);
		Label lCode1 = new Label("Code1");
		GridPane.setHalignment(lCode1, HPos.CENTER);
		gridpane2.add(lCode1, 1, 0);
		Label lDate1 = new Label("Date1");
		GridPane.setHalignment(lDate1, HPos.CENTER);
		gridpane2.add(lDate1, 2, 0);
		Label lDuration1 = new Label("Duration1");
		GridPane.setHalignment(lDuration1, HPos.CENTER);
		gridpane2.add(lDuration1, 3, 0);

		Label lProject2 = new Label("Project2");
		GridPane.setHalignment(lProject2, HPos.CENTER);
		gridpane2.add(lProject2, 0, 1);
		Label lCode2 = new Label("Code2");
		GridPane.setHalignment(lCode1, HPos.CENTER);
		gridpane2.add(lCode2, 1, 1);
		Label lDate2 = new Label("Date2");
		GridPane.setHalignment(lDate2, HPos.CENTER);
		gridpane2.add(lDate2, 2, 1);
		Label lDuration2 = new Label("Duration2");
		GridPane.setHalignment(lDuration2, HPos.CENTER);
		gridpane2.add(lDuration2, 3, 1);

		Label lProject3 = new Label("Project3");
		GridPane.setHalignment(lProject3, HPos.CENTER);
		gridpane2.add(lProject3, 0, 2);
		Label lCode3 = new Label("Code3");
		GridPane.setHalignment(lCode3, HPos.CENTER);
		gridpane2.add(lCode3, 1, 2);
		Label lDate3 = new Label("Date3");
		GridPane.setHalignment(lDate3, HPos.CENTER);
		gridpane2.add(lDate3, 2, 2);
		Label lDuration3 = new Label("Duration3");
		GridPane.setHalignment(lDuration3, HPos.CENTER);
		gridpane2.add(lDuration3, 3, 2);

		Label lProject4 = new Label("Project4");
		GridPane.setHalignment(lProject4, HPos.CENTER);
		gridpane2.add(lProject4, 0, 3);
		Label lCode4 = new Label("Code4");
		GridPane.setHalignment(lCode4, HPos.CENTER);
		gridpane2.add(lCode4, 1, 3);
		Label lDate4 = new Label("Date4");
		GridPane.setHalignment(lDate4, HPos.CENTER);
		gridpane2.add(lDate4, 2, 3);
		Label lDuration4 = new Label("Duration4");
		GridPane.setHalignment(lDuration4, HPos.CENTER);
		gridpane2.add(lDuration4, 3, 3);

		Label lProject5 = new Label("Project5");
		GridPane.setHalignment(lProject5, HPos.CENTER);
		gridpane2.add(lProject5, 0, 4);
		Label lCode5 = new Label("Code5");
		GridPane.setHalignment(lCode5, HPos.CENTER);
		gridpane2.add(lCode5, 1, 4);
		Label lDate5 = new Label("Date5");
		GridPane.setHalignment(lDate5, HPos.CENTER);
		gridpane2.add(lDate5, 2, 4);
		Label lDuration5 = new Label("Duration5");
		GridPane.setHalignment(lDuration5, HPos.CENTER);
		gridpane2.add(lDuration5, 3, 4);

		rootNode.getChildren().add(gridpane2);

//		GridPane.setHalignment(gridpane2, HPos.CENTER);
		gridpane.add(gridpane2, 0, 3);

		GridPane gridpane3 = new GridPane();
		gridpane3.setPadding(new Insets(5));
		gridpane3.setHgap(10);
		gridpane3.setVgap(10);

		Button bContinue1 = new Button("Continue");
		GridPane.setHalignment(bContinue1, HPos.CENTER);
		gridpane3.add(bContinue1, 0, 0);
		Button bEdit1 = new Button("Edit");
		GridPane.setHalignment(bEdit1, HPos.CENTER);
		gridpane3.add(bEdit1, 1, 0);
		Button bDelete1 = new Button("Delete");
		GridPane.setHalignment(bDelete1, HPos.CENTER);
		gridpane3.add(bDelete1, 2, 0);

		Button bContinue2 = new Button("Continue");
		GridPane.setHalignment(bContinue2, HPos.CENTER);
		gridpane3.add(bContinue2, 0, 1);
		Button bEdit2 = new Button("Edit");
		GridPane.setHalignment(bEdit2, HPos.CENTER);
		gridpane3.add(bEdit2, 1, 1);
		Button bDelete2 = new Button("Delete");
		GridPane.setHalignment(bDelete2, HPos.CENTER);
		gridpane3.add(bDelete2, 2, 1);

		Button bContinue3 = new Button("Continue");
		GridPane.setHalignment(bContinue3, HPos.CENTER);
		gridpane3.add(bContinue3, 0, 2);
		Button bEdit3 = new Button("Edit");
		GridPane.setHalignment(bEdit3, HPos.CENTER);
		gridpane3.add(bEdit3, 1, 2);
		Button bDelete3 = new Button("Delete");
		GridPane.setHalignment(bDelete3, HPos.CENTER);
		gridpane3.add(bDelete3, 2, 2);

		Button bContinue4 = new Button("Continue");
		GridPane.setHalignment(bContinue4, HPos.CENTER);
		gridpane3.add(bContinue4, 0, 3);
		Button bEdit4 = new Button("Edit");
		GridPane.setHalignment(bEdit4, HPos.CENTER);
		gridpane3.add(bEdit4, 1, 3);
		Button bDelete4 = new Button("Delete");
		GridPane.setHalignment(bDelete4, HPos.CENTER);
		gridpane3.add(bDelete4, 2, 3);

		Button bContinue5 = new Button("Continue");
		GridPane.setHalignment(bContinue5, HPos.CENTER);
		gridpane3.add(bContinue5, 0, 4);
		Button bEdit5 = new Button("Edit");
		GridPane.setHalignment(bEdit5, HPos.CENTER);
		gridpane3.add(bEdit5, 1, 4);
		Button bDelete5 = new Button("Delete");
		GridPane.setHalignment(bDelete5, HPos.CENTER);
		gridpane3.add(bDelete5, 2, 4);

		rootNode.getChildren().add(gridpane3);

//		GridPane.setHalignment(gridpane3, HPos.CENTER);
		gridpane.add(gridpane3, 1, 3);

		myStage.setScene(myScene);
		myStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
