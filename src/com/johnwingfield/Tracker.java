package com.johnwingfield;

import javafx.application.*;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.*;
import javafx.scene.layout.*;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO log should be loaded on program start
// TODO duration should update constantly
// TODO re-arrange layout to intended look
// TODO change old job list to scrolling list, buttons should only apply to first row
// TODO what will EDIT actually do?
// TODO decide how log will be imported and stored in memory, for editing/deleting
// TODO log file location should detect/default to program location

public class Tracker extends Application {
	private long startTime = 0;
	private String file_name = "C:/Dropbox/Working/Tracker.txt";
	private Button bLoad, bSave, bStart, bStop, bContinue, bEdit, bDelete;
	private TextField tJob, tCode, tDuration, tDate;
	private Label lProject1, lCode1, lDuration1, lDate1;
	private Timer durTimer;

	private void loadLog() {
//		bLoad.setDisable(true);
//		bSave.setDisable(false);

		try {
			ReadLog file = new ReadLog(file_name);
			List<String> aryLines = new ArrayList<>(file.OpenFile());
			aryLines.forEach(System.out::println);
		}
		catch (Exception IOe) {
			System.out.println(IOe.getMessage());
		}
	}

	private void saveLog() {
		if (tJob.getText().length() > 0) {
//			bLoad.setDisable(false);
//			bSave.setDisable(true);

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
	}

	private void startTimer() {
		bStart.setDisable(true);
		bStop.setDisable(false);

		startTime = System.currentTimeMillis();

		durTimer = new Timer(500, e -> updateTimer());
		durTimer.start();
	}

	private void updateTimer() {
		long currentTime = System.currentTimeMillis();
		long duration = (currentTime - startTime) / 1000;
		int seconds = (int) duration % 60;
		int minutes = (int) (duration / 60) % 60;
		int hours   = (int) (duration / 3600) % 24;

		tDuration.setText(String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds));
//		System.out.printf("%02d:%02d:%02d\n", hours, minutes, seconds);
	}

	private void stopTimer() {
		bStart.setDisable(false);
		bStop.setDisable(true);
		bContinue.setDisable(false);
		durTimer.stop();
	}

	private void continueOldJob() {
		tJob.setText(lProject1.getText());
		tCode.setText(lCode1.getText());
		tDuration.setText(lDuration1.getText());
		tDate.setText(lDate1.getText());
		bContinue.setDisable(true);
		startTimer();
	}

	private void editOldJob() {
		System.out.println("TODO");
	}

	private void deleteOldJob() {
		System.out.println("TODO");
	}

	public void start(Stage myStage) {
		myStage.setTitle("Tracker");
		myStage.setResizable(false);

		Group rootNode = new Group();
		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5));
		gridpane.setHgap(10);
		gridpane.setVgap(10);

		Scene myScene = new Scene(rootNode, 600, 400);

		bLoad = new Button("Load log file");
		GridPane.setHalignment(bLoad, HPos.CENTER);
		gridpane.add(bLoad, 0, 0);

		bSave = new Button("Save project/code");
		GridPane.setHalignment(bSave, HPos.CENTER);
		gridpane.add(bSave, 1, 0);
//		bSave.setDisable(true);

		bStart = new Button("Start timer");
		GridPane.setHalignment(bStart, HPos.CENTER);
		gridpane.add(bStart, 0, 1);

		tDuration = new TextField();
		tDuration.setPromptText("1");
		tDuration.setPrefColumnCount(8);
		GridPane.setHalignment(tDuration, HPos.CENTER);
		gridpane.add(tDuration, 1, 1);

		bStop = new Button("Stop timer");
		GridPane.setHalignment(bStop, HPos.CENTER);
		gridpane.add(bStop, 2, 1);
		bStop.setDisable(true);

		tJob = new TextField();
		tJob.setPromptText("project");
		tJob.setPrefColumnCount(20);
		GridPane.setHalignment(tJob, HPos.CENTER);
		gridpane.add(tJob, 0, 2);

		tCode = new TextField();
		tCode.setPromptText("code");
		tCode.setPrefColumnCount(15);
		GridPane.setHalignment(tCode, HPos.CENTER);
		gridpane.add(tCode, 1, 2);

		tDate = new TextField();
		tDate.setPromptText("ddmmyy");
		tDate.setPrefColumnCount(6);
		GridPane.setHalignment(tDate, HPos.CENTER);
		gridpane.add(tDate, 2, 2);

		bLoad.setOnAction(ae -> loadLog());
		bSave.setOnAction(ae -> saveLog());
		bStart.setOnAction(ae -> startTimer());
		bStop.setOnAction(ae -> stopTimer());

		rootNode.getChildren().add(gridpane);

		GridPane gridpane2 = new GridPane();
		gridpane2.setPadding(new Insets(5));
		gridpane2.setHgap(10);
		gridpane2.setVgap(19);

		lProject1 = new Label("Project1");
		GridPane.setHalignment(lProject1, HPos.CENTER);
		gridpane2.add(lProject1, 0, 0);
		lCode1 = new Label("Code1");
		GridPane.setHalignment(lCode1, HPos.CENTER);
		gridpane2.add(lCode1, 1, 0);
		lDate1 = new Label("Date1");
		GridPane.setHalignment(lDate1, HPos.CENTER);
		gridpane2.add(lDate1, 2, 0);
		lDuration1 = new Label("Duration1");
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

		bContinue = new Button("Continue");
		GridPane.setHalignment(bContinue, HPos.CENTER);
		gridpane3.add(bContinue, 0, 0);
		bEdit = new Button("Edit");
		GridPane.setHalignment(bEdit, HPos.CENTER);
		gridpane3.add(bEdit, 1, 0);
		bDelete = new Button("Delete");
		GridPane.setHalignment(bDelete, HPos.CENTER);
		gridpane3.add(bDelete, 2, 0);

/*		Button bContinue2 = new Button("Continue");
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
		gridpane3.add(bDelete5, 2, 4);*/

		bContinue.setOnAction(ae -> continueOldJob());
		bEdit.setOnAction(ae -> editOldJob());
		bDelete.setOnAction(ae -> deleteOldJob());

		rootNode.getChildren().add(gridpane3);

//		GridPane.setHalignment(gridpane3, HPos.CENTER);
		gridpane.add(gridpane3, 1, 3);

		myStage.setScene(myScene);
		myStage.show();
		loadLog();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
