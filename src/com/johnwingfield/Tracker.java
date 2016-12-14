package com.johnwingfield;

import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import javafx.scene.layout.*;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Project based time tracking
 *
 * @author John Wingfield
 *
 */
public class Tracker extends Application {
	private long startTime = 0;
	private long previousTime = 0;
	private long duration = 0;
	private static final String fileName = "Tracker.txt";
	private Button bSave, bStart, bStop, bContinue, bDelete, bReset;
	private TextField tJob, tCode, tDuration, tDate;
	private Timer durTimer;
	private Jobs[] jobList;
	private final TableView<Jobs> table = new TableView<>();
	private static ObservableList<Jobs> dataList;

	/**
	 * Convert duration string to milliseconds
	 *
	 * @param txtDuration eg. "08:30:00" to 293472386123
	 * @return Long
	 */
	private long convertToMS(String txtDuration) {
		return  (Long.parseLong(txtDuration.substring(0, 2)) * Globals.MS_PER_HOUR) +
				(Long.parseLong(txtDuration.substring(3, 5)) * Globals.MS_PER_MIN) +
				(Long.parseLong(txtDuration.substring(6, 8)) * Globals.MS_PER_SEC);
	}

	/**
	 * Convert duration in milliseconds to string
	 *
	 * @param msDuration eg. 12837127312 to "08:30:00"
	 * @return String
	 */
	private String convertToStr(long msDuration) {
		int seconds = (int) msDuration % Globals.SEC_PER_MIN;
		int minutes = (int) (msDuration / Globals.SEC_PER_MIN) % Globals.MIN_PER_HOUR;
		int hours   = (int) (msDuration / Globals.SEC_PER_HOUR) % Globals.HOUR_PER_DAY;

		return(String.format("%02d:%02d:%02d\n", hours, minutes, seconds));
	}

	/**
	 * Loads Tracker.txt file from current location into jobList
	 */
	private void loadLog() {
		File f = new File(fileName);

		if (!f.exists()) {
			try {
				//noinspection ResultOfMethodCallIgnored
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			ReadLog file = new ReadLog(fileName);
			int numOfJobs = file.CountLines();

			jobList = new Jobs[numOfJobs];
			jobList = file.OpenFile(jobList);

//			for (int i = 0; i < numOfJobs; ++i) {
//				System.out.println(jobList[i].getProject() + ", " +
//								   jobList[i].getCode() + ", " +
//								   jobList[i].getDate() + ", " +
//								   jobList[i].getDuration());
//			}
		}
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Tracker.txt file not found", "Name", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (Exception IOe) {
			System.out.println(IOe.getMessage());
		}
	}

	/**
	 * Saves current job info to dataList, removing any continued jobs from same day
	 */
	private void saveLog() {
		if (tJob.getText().length() == 0) {
			tJob.requestFocus();
			return;
		}

		if (tCode.getText().length() == 0) {
			tCode.requestFocus();
			return;
		}

		if (tDuration.getText().length() == 0) {
			tDuration.setText("00:00:00");
		}

		for (Jobs job : dataList) {
			if ((job.getProject().equals(tJob.getText())) &&
				(job.getCode().equals(tCode.getText())) &&
				(job.getDate().equals(tDate.getText()))) {
					dataList.remove(job);
					break;
				}
		}

		dataList.add(new Jobs(tJob.getText(),
							  tCode.getText(),
							  tDate.getText(),
							  tDuration.getText()));

		writeLog();

		bSave.setDisable(true);
	}

	/**
	 * Saves current dataList to Tracker.txt file
	 */
	private static void writeLog() {
		try (FileWriter writer = new FileWriter(fileName)) {
			for (Jobs job : dataList) {
				writer.write(job.getProject() + "," +
							 job.getCode() + "," +
							 job.getDate() + "," +
							 job.getDuration() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the timer from current system time
	 */
	private void startTimer() {
		bStart.setDisable(true);
		bStop.setDisable(false);
		bReset.setDisable(false);

		startTime = System.currentTimeMillis();

		if (!tDuration.getText().isEmpty()) {
			previousTime = convertToMS(tDuration.getText());
		}

		durTimer.start();
	}

	/**
	 * Updates duration textfield with current duration
	 */
	private void updateTimer() {
		duration = ((System.currentTimeMillis() - startTime) + previousTime) / Globals.MS_PER_SEC;
		tDuration.setText(convertToStr(duration));
	}

	/**
	 * Stops the timer
	 */
	private void stopTimer() {
		bStart.setDisable(false);
		bStop.setDisable(true);
		bSave.setDisable(false);
		bContinue.setDisable(true);
//		bEdit.setDisable(true);
		bDelete.setDisable(true);
		durTimer.stop();
		previousTime = duration * Globals.MS_PER_SEC;
	}

	/**
	 * Gets selected previous job and starts timer. If job was from before today timer starts at 0, otherwise it continues.
	 */
	private void continueOldJob() {
		String currentDate, oldDate;

		bContinue.setDisable(true);

		Jobs job = table.getSelectionModel().getSelectedItem();
		tJob.setText(job.getProject());
		tCode.setText(job.getCode());

		// compare chosen job's date against current date. Continue will use current date if it's newer
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		currentDate = dateFormat.format(new Date());
		oldDate = job.getDate();
		oldDate = oldDate.substring(6, 8) + oldDate.substring(3, 5) + oldDate.substring(0,2);

		if (Integer.parseInt(currentDate) > Integer.parseInt(oldDate)) { // continued job is older than today
			previousTime = 0;
			tDuration.setText("00:00:00");
			setDate();
		}
		else { // job is continued from earlier today
			tDuration.setText(job.getDuration());
			tDate.setText(job.getDate());
			previousTime = convertToMS(job.getDuration());
		}

		startTimer();
	}

	/**
	 * Removes selected job from dataList
	 */
	private void deleteOldJob() {
		dataList.remove(table.getSelectionModel().getSelectedItem());
		writeLog();
	}

	/**
	 * Stops timer and resets all current job details
	 */
	private void resetJob() {
		if (durTimer.isRunning()) {
			stopTimer();
		}

		tJob.clear();
		tCode.clear();
		tDuration.clear();
		previousTime = 0;
		setDate();

		bReset.setDisable(true);
		bSave.setDisable(true);
	}

	/**
	 * Sets current date in textfield in correct format
	 */
	private void setDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		tDate.setText(dateFormat.format(new Date()));
	}

	/**
	 * Compare dates by YYMMDD for table sort
	 * @author John Wingfield
	 */
	public class DateComparator implements Comparator<String> {
		public int compare(String s1, String s2) {
			int i1 = Integer.parseInt(s1.substring(6, 8) + s1.substring(3, 5) + s1.substring(0,2));
			int i2 = Integer.parseInt(s2.substring(6, 8) + s2.substring(3, 5) + s2.substring(0,2));
//			System.out.println("i1 = " + i1 + " i2 = " + i2 + " = " + (i1 < i2 ? -1 : i1 == i2 ? 0 : 1));
			return i1 < i2 ? -1 : i1 == i2 ? 0 : 1;
		}
	}

	/**
	 * Build grid layouts, table setup and handle actions
	 * @param stage default
	 */
	public void start(Stage stage) {
		loadLog();

		stage.setTitle("Tracker v1.0 - John Wingfield");
		stage.setResizable(false);

		Group rootNode = new Group();
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(5));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Scene scene = new Scene(rootNode, 650, 450);

//		bLoad = new Button("Load log file");
//		GridPane.setHalignment(bLoad, HPos.CENTER);
//		gridPane.add(bLoad, 0, 0);

		bSave = new Button("Save project/code");
		GridPane.setHalignment(bSave, HPos.CENTER);
		gridPane.add(bSave, 1, 0);
		bSave.setDisable(true);

		bReset = new Button("Reset");
		GridPane.setHalignment(bReset, HPos.CENTER);
		gridPane.add(bReset, 2, 0);
		bReset.setDisable(true);

		bStart = new Button("Start timer");
		GridPane.setHalignment(bStart, HPos.CENTER);
		gridPane.add(bStart, 0, 1);

		tDuration = new TextField();
		tDuration.setPromptText("00:00:00");
		tDuration.setPrefColumnCount(8);
		GridPane.setHalignment(tDuration, HPos.CENTER);
		gridPane.add(tDuration, 1, 1);

		bStop = new Button("Stop timer");
		GridPane.setHalignment(bStop, HPos.CENTER);
		gridPane.add(bStop, 2, 1);
		bStop.setDisable(true);

		tJob = new TextField();
		tJob.setPromptText("project");
		tJob.setPrefColumnCount(15);
		GridPane.setHalignment(tJob, HPos.CENTER);
		gridPane.add(tJob, 0, 2);

		tCode = new TextField();
		tCode.setPromptText("code");
		tCode.setPrefColumnCount(15);
		GridPane.setHalignment(tCode, HPos.CENTER);
		gridPane.add(tCode, 1, 2);

		tDate = new TextField();
		tDate.setPromptText("ddmmyy");
		tDate.setPrefColumnCount(6);
		GridPane.setHalignment(tDate, HPos.CENTER);
		gridPane.add(tDate, 2, 2);

//		bLoad.setOnAction(ae -> loadLog());
		bSave.setOnAction(ae -> saveLog());
		bStart.setOnAction(ae -> startTimer());
		bStop.setOnAction(ae -> stopTimer());
		bReset.setOnAction(ae -> resetJob());

		dataList = FXCollections.observableArrayList(jobList);

		table.setPrefWidth(300);
		table.setPrefHeight(300);
		table.setEditable(true);

		TableColumn<Jobs, String> projectCol = new TableColumn<>("Project");
		projectCol.setCellValueFactory(new PropertyValueFactory<>("project"));
		projectCol.setCellFactory(TextFieldTableCell.forTableColumn());
		projectCol.setOnEditCommit(
			t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setProject(t.getNewValue())
		);

		TableColumn<Jobs, String> codeCol = new TableColumn<>("Code");
		codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
		codeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		codeCol.setOnEditCommit(
			t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setCode(t.getNewValue())
		);

		TableColumn<Jobs, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		dateCol.setSortType(TableColumn.SortType.ASCENDING);
		dateCol.setComparator(new DateComparator());
		dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
		dateCol.setOnEditCommit(
			t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setDate(t.getNewValue())
		);

		TableColumn<Jobs, String> durationCol = new TableColumn<>("Duration");
		durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
		durationCol.setCellFactory(TextFieldTableCell.forTableColumn());
		durationCol.setOnEditCommit(
			t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setDuration(t.getNewValue())
		);

		table.setItems(dataList);
		table.getColumns().add(projectCol); // added separately to avoid unchecked generics error
		table.getColumns().add(codeCol);
		table.getColumns().add(dateCol);
		table.getColumns().add(durationCol);
		table.getSortOrder().add(dateCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		table.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
			if (table.getSelectionModel().getSelectedItem() != null) {
				bContinue.setDisable(false);
//				bEdit.setDisable(false);
				bDelete.setDisable(false);
			}
			else {
				bContinue.setDisable(true);
//				bEdit.setDisable(true);
				bDelete.setDisable(true);
			}
		});

		table.setRowFactory(table2 -> {
			final TableRow<Jobs> row = new TableRow<>();

			row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				final int index = row.getIndex();

				if (index >= 0 && index < table.getItems().size() && table.getSelectionModel().isSelected(index)) {
					table.getSelectionModel().clearSelection();
					event.consume();
				}
			});

			return row;
		});

		gridPane.add(table, 0, 3);
		rootNode.getChildren().add(gridPane);

//		GridPane gridPane2 = new GridPane();
//		gridPane2.setPadding(new Insets(5));
//		gridPane2.setHgap(10);
//		gridPane2.setVgap(19);

//		rootNode.getChildren().add(gridPane2);

//		GridPane.setHalignment(gridPane2, HPos.CENTER);
//		gridPane.add(gridPane2, 0, 3);

		GridPane gridPane3 = new GridPane();
		gridPane3.setPadding(new Insets(5));
		gridPane3.setHgap(10);
		gridPane3.setVgap(10);

		bContinue = new Button("Continue");
		GridPane.setHalignment(bContinue, HPos.CENTER);
		gridPane3.add(bContinue, 0, 0);

//		bEdit = new Button("Edit");
//		GridPane.setHalignment(bEdit, HPos.CENTER);
//		gridPane3.add(bEdit, 1, 0);

		bDelete = new Button("Delete");
		GridPane.setHalignment(bDelete, HPos.CENTER);
		gridPane3.add(bDelete, 2, 0);

		bContinue.setOnAction(ae -> continueOldJob());
//		bEdit.setOnAction(ae -> editOldJob());
		bDelete.setOnAction(ae -> deleteOldJob());

		rootNode.getChildren().add(gridPane3);

//		GridPane.setHalignment(gridPane3, HPos.CENTER);
		gridPane.add(gridPane3, 1, 3);

		stage.setScene(scene);
		stage.show();
		setDate();

		bContinue.setDisable(true);
//		bEdit.setDisable(true);
		bDelete.setDisable(true);

		durTimer = new Timer(500, e -> updateTimer()); // update timer every .5 seconds
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
//				System.out.println("Shutting down");
				writeLog();
			}
		});

		launch(args);
	}
}
