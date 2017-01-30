package com.johnwingfield;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//TODO save screen position
//TODO add data and do full test

/**
 * Project based time tracking
 *
 * @author John Wingfield
 *
 */
public class Tracker extends Application {
	private Jobs[] jobList;
	private final TableView<Jobs> table = new TableView<>();
	private static ObservableList<Jobs> dataList;
	private static double startTime = 0;
	private static double previousTime = 0;
	private static double duration = 0;
	private static boolean isRunning = false;
	private static Button bSave, bStart, bStop, bContinue, bDelete, bReset;
	private static TextField tJob, tCode, tDuration, tDate;
	private static final DurTimerTask durTask = new DurTimerTask();
	private static final Timer durTimer = new Timer(true);
	private final MenuBar menuBar = new MenuBar();
	private ToolBar toolBar = new ToolBar();
	private EventHandler<ActionEvent> eHandler;
	private final TableColumn<Jobs, String> projectCol = new TableColumn<>("Project");
	private final TableColumn<Jobs, String> codeCol = new TableColumn<>("Code");
	private final TableColumn<Jobs, String> dateCol = new TableColumn<>("Date");
	private final TableColumn<Jobs, String> durationCol = new TableColumn<>("Duration");
	private DatePicker startDatePicker;
	private DatePicker endDatePicker;
	private String startDate;
	private String endDate;

	public static void main(String[] args) {
		try {
			durTimer.schedule(durTask, 500, 500); // start delay, update delay
		}
		catch (Exception e){
			System.out.println("Error starting timer");
			System.out.println(e.getMessage());
			System.exit(1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			durTimer.cancel();
			writeLog();
		}));

		launch(args);
	}

//	private void showList() {
//		for (int i = 0; i < jobList.length; ++i) {
//			System.out.println(jobList[i].getProject() + ", " +
//								jobList[i].getCode() + ", " +
//								jobList[i].getDate() + ", " +
//								jobList[i].getDuration());
//		}
//	}

//	private void showData() {
//		for (Jobs job : dataList) {
//			System.out.println(job.getProject() + "," +
//								job.getCode() + "," +
//								fixDate(job.getDate()) + "," +
//								job.getDuration());
//		}
//		System.out.println("\n");
//	}

	/**
	 *  Define the timer for updating the duration text field
	 */
	static class DurTimerTask extends TimerTask {
		public void run() {
			if (isRunning) { // Update duration text field with current duration
				duration = ((System.currentTimeMillis() - startTime) + previousTime); //   / Globals.MS_PER_SEC;
				tDuration.setText(convertToStr(duration));
			}
		}
	}

	/**
	 * Convert duration string to milliseconds
	 *
	 * @param txtDuration eg. "08:30:00" to 293472386123
	 * @return double
	 */
	private double convertToMS(String txtDuration) {
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
	private static String convertToStr(double msDuration) {
		int hours   = (int) (msDuration / Globals.MS_PER_HOUR);
		int minutes = (int) ((msDuration / Globals.MS_PER_MIN) % Globals.MIN_PER_HOUR);
		int seconds = (int) (msDuration / Globals.MS_PER_SEC) % Globals.SEC_PER_MIN;
		return(String.format("%02d:%02d:%02d\n", hours, minutes, seconds));
	}

	/**
	 * Returns specified amount of spaces for padding
	 *
	 * @param length Amount of spaces to be returned
	 * @return String
	 */
	private String spaces(int length) {
		String padding = "";
		for (int i = 0; i <= length; ++i) padding += " ";
		return padding;
	}

	/**
	 * Checks dates for single digits and resolves it
	 * @param d date string to be checked
	 * @return String
	 */
	private static String fixDate(String d) {
		if (d.length() < 8) {
			String[] parts = d.split("/");

			if (parts.length != 3) {
				JOptionPane.showMessageDialog(null, "Failed fixing date " + d, "FixDate()", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}

			if (parts[0].length() == 1) {
				parts[0] = "0" + parts[0];
			}

			if (parts[1].length() == 1) {
				parts[1] = "0" + parts[1];
			}

			if (parts[2].length() == 1) {
				parts[2] = "0" + parts[2];
			}

			d = parts[0] + "/" + parts[1] + "/" + parts[2];
		}

		return d;
	}

	/**
	 * Loads Tracker.txt file from current location into jobList
	 */
	private void loadLog() {
		File f = new File(Globals.fileName);

		if (!f.exists()) {
			try {
				//noinspection ResultOfMethodCallIgnored
				f.createNewFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Unable to create Tracker.txt", "LoadLog()", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
//				e.printStackTrace();
			}
		}

		try {
			ReadLog file = new ReadLog();
			int numOfJobs = file.CountLines();

			jobList = new Jobs[numOfJobs];
			jobList = file.OpenFile(jobList);
		}
		catch (FileNotFoundException e) { // file should already have been created if not found
			JOptionPane.showMessageDialog(null, "Tracker.txt file not found", "LoadLog()", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Saves current job info to dataList, removing any continued jobs from same day
	 */
	private void saveJob() {
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
			// check for * and remove it so project can be matched and replaced
			String projectName = job.getProject();
			if (projectName.startsWith("*")) {
				projectName = projectName.substring(1);
			}

			if ((projectName.equals(tJob.getText())) &&
				(job.getCode().equals(tCode.getText())) &&
				(job.getDate().equals(fixDate(tDate.getText())))) {
					dataList.remove(job);
					break;
			}
		}

		dataList.add(new Jobs(tJob.getText(),
							  tCode.getText(),
							  fixDate(tDate.getText()),
							  tDuration.getText()));

		resetJob();
		writeLog();
	}

	/**
	 * Saves current dataList to Tracker.txt file
	 */
	private static void writeLog() {
		try (FileWriter writer = new FileWriter(Globals.fileName)) {
			for (Jobs job : dataList) {
				writer.write(job.getProject() + "," +
							 job.getCode() + "," +
							 fixDate(job.getDate()) + "," +
							 job.getDuration() + "\n");
			}

			if (!tDuration.getText().isEmpty()) {
				writer.write("*" + tJob.getText() +
							 "," + tCode.getText() +
							 "," + fixDate(tDate.getText()) +
							 "," + tDuration.getText() + "\n");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed writing to file", "WriteLog()", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
//			e.printStackTrace();
		}
	}

	/**
	 * Saves supplied report data to file
	 *
	 * @param reportTxt collated report data string
	 */
	private static void writeReport(String reportTxt, int reportType) {
		String reportFile = "";

		switch (reportType) {
			case Globals.REPORT_BY_DATE:
				reportFile = Globals.reportByDate;
				break;
			case Globals.REPORT_BY_RANGE:
				reportFile = Globals.reportByDateRange;
				break;
			case Globals.REPORT_BY_PROJECT:
				reportFile = Globals.reportByProject;
				break;
			case Globals.REPORT_SPECIFIC_PROJECT:
				reportFile = Globals.reportSpecificProject;
				break;
		}

		try (FileWriter writer = new FileWriter(reportFile)) {
			writer.write(reportTxt);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed writing report file", "WriteReport()", JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
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
		isRunning = true;

		if (!tDuration.getText().isEmpty()) {
			previousTime = convertToMS(tDuration.getText());
		}

		bStop.requestFocus();
		table.getSelectionModel().clearSelection();
	}

	/**
	 * Stops the timer
	 */
	private void stopTimer() {
		bStart.setDisable(false);
		bStop.setDisable(true);
		bSave.setDisable(false);
		bContinue.setDisable(true);
		bDelete.setDisable(true);

		isRunning = false;
		previousTime = duration * Globals.MS_PER_SEC;

		bSave.requestFocus();
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
			setDate();
			previousTime = convertToMS(job.getDuration());
		}

		startTimer();
	}

	/**
	 * Removes selected job from dataList
	 */
	private void deleteOldJob() {
		dataList.remove(table.getSelectionModel().getSelectedItem());
		table.getSelectionModel().clearSelection();
		writeLog();
	}

	/**
	 * Stops timer and resets all current job details
	 */
	private void resetJob() {
		tJob.clear();
		tCode.clear();
		tDuration.clear();
		previousTime = 0;
		duration = 0;

		stopTimer();
		setDate();

		bReset.setDisable(true);
		bSave.setDisable(true);
	}

	/**
	 * Sets current date in text field in correct format
	 */
	private void setDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		tDate.setText(dateFormat.format(new Date()));
	}

	/**
	 * Compare dates by YYMMDD for table sort
	 */
	private class DateComparator implements Comparator<String> {
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

		// remove window decoration
//		stage.initStyle(StageStyle.UNDECORATED);

		stage.setTitle("Tracker v1.0 - John Wingfield");
		stage.setResizable(false);

		Group rootNode = new Group();
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(5));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Scene scene = new Scene(rootNode, 360, 460);

		// add buttons and text fields
		bSave = new Button("Save project/code");
		GridPane.setHalignment(bSave, HPos.CENTER);
		bSave.setDisable(true);

		bReset = new Button("Reset");
		GridPane.setHalignment(bReset, HPos.CENTER);
		bReset.setDisable(true);

		bStart = new Button("Start timer");
		GridPane.setHalignment(bStart, HPos.CENTER);

		bStop = new Button("Stop timer");
		GridPane.setHalignment(bStop, HPos.CENTER);
		bStop.setDisable(true);

		bContinue = new Button("Continue");
		GridPane.setHalignment(bContinue, HPos.CENTER);

		bDelete = new Button("Delete");
		GridPane.setHalignment(bDelete, HPos.CENTER);

		tJob = new TextField();
		tJob.setPromptText("project");
		tJob.setPrefColumnCount(15);
		GridPane.setHalignment(tJob, HPos.CENTER);
		gridPane.add(tJob, 0, 3);

		tCode = new TextField();
		tCode.setPromptText("code");
		tCode.setPrefColumnCount(15);
		GridPane.setHalignment(tCode, HPos.CENTER);
		gridPane.add(tCode, 0, 4);

		tDuration = new TextField();
		tDuration.setPromptText("00:00:00");
		tDuration.setPrefColumnCount(8);
		GridPane.setHalignment(tDuration, HPos.CENTER);
		gridPane.add(tDuration, 1, 3);

		tDate = new TextField();
		tDate.setPromptText("ddmmyy");
		tDate.setPrefColumnCount(8);
		GridPane.setHalignment(tDate, HPos.CENTER);
		gridPane.add(tDate, 1, 4);

		// action events
		bStart.setOnAction(ae -> startTimer());
		bStop.setOnAction(ae -> stopTimer());
		bSave.setOnAction(ae -> saveJob());
		bReset.setOnAction(ae -> resetJob());
		bContinue.setOnAction(ae -> continueOldJob());
		bDelete.setOnAction(ae -> deleteOldJob());

		// create table
		dataList = FXCollections.observableArrayList(jobList);
		table.setPrefWidth(350);
		table.setPrefHeight(300);
		table.setEditable(true);

		// add table columns
		projectCol.setCellValueFactory(new PropertyValueFactory<>("project"));
		projectCol.setCellFactory(TextFieldTableCell.forTableColumn());
		projectCol.setOnEditCommit(
			t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setProject(t.getNewValue())
		);

		codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
		codeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		codeCol.setOnEditCommit(
			t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setCode(t.getNewValue())
		);

		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		dateCol.setSortType(TableColumn.SortType.DESCENDING);
		dateCol.setComparator(new DateComparator());
		dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
		dateCol.setOnEditCommit(
			t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setDate(t.getNewValue())
		);

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

		// enable/disable Continue & Delete depending if a row is selected or not
		table.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
			if (table.getSelectionModel().getSelectedItem() != null) {
				bContinue.setDisable(false);
				bDelete.setDisable(false);
			}
			else {
				bContinue.setDisable(true);
				bDelete.setDisable(true);
			}
		});

		// determine selected row
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

		GridPane gridPane2 = new GridPane();
		gridPane2.setPadding(new Insets(5));
		gridPane2.setHgap(10);
		gridPane2.setVgap(10);

		// create and add toolbar
		makeToolBar();
		gridPane2.add(toolBar, 0, 0);

		// add table to grid
		gridPane2.add(table, 0, 1);
		rootNode.getChildren().add(gridPane2);
		gridPane.add(gridPane2, 0, 5, 2, 1);
		rootNode.getChildren().add(gridPane);

		// create one event handler for all menu action events
		eHandler = ae -> {
			String name = ((MenuItem)ae.getTarget()).getText();
			Report(name);
		};

		// create and add the Reporting menu
		makeReportMenu();
		rootNode.getChildren().add(menuBar);

		stage.setScene(scene);
		stage.show();
		setDate();
		gridPane.requestFocus(); // otherwise project text field starts with focus

		bContinue.setDisable(true);
		bDelete.setDisable(true);

		// ensure file contains data before checking for in-progress jobs
		for (int i = 0; i < dataList.size(); ++i) {
			if (dataList.get(i).getProject().startsWith("*")) {
				table.getSelectionModel().select(i);
				Jobs job = table.getSelectionModel().getSelectedItem();

				String currentDate, oldDate;
				DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
				currentDate = dateFormat.format(new Date());
				oldDate = job.getDate();
				oldDate = oldDate.substring(6, 8) + oldDate.substring(3, 5) + oldDate.substring(0,2);

				if (Integer.parseInt(currentDate) > Integer.parseInt(oldDate)) { // saved job is older than today
					job.setProject(job.getProject().substring(1));
					writeLog();
				}
				else { // job is saved from earlier today
					tJob.setText(job.getProject().substring(1));
					tCode.setText(job.getCode());
					tDuration.setText(job.getDuration());
					setDate();
					previousTime = convertToMS(job.getDuration());

					dataList.remove(table.getSelectionModel().getSelectedItem());
					bReset.setDisable(false);
					bSave.setDisable(false);
				}

				table.getSelectionModel().clearSelection();
				break;
			}
		}
	}

	/**
	 * Create the Report menu
	 */
	private void makeReportMenu() {
		Menu reportMenu = new Menu("_Reporting");

		MenuItem byDate = new MenuItem("By date");
		MenuItem byRange = new MenuItem("By date range");
		MenuItem byProject = new MenuItem("By project");
		MenuItem byAProject = new MenuItem("Specific project");

		reportMenu.getItems().addAll(byDate, byRange, byProject, byAProject);

		byDate.setOnAction(eHandler);
		byRange.setOnAction(eHandler);
		byProject.setOnAction(eHandler);
		byAProject.setOnAction(eHandler);

		menuBar.getMenus().add(reportMenu);
	}

	/**
	 * Create the toolbar
	 */
	private void makeToolBar() {
		// create and size icons
		ImageView startIcon = new ImageView("/com/johnwingfield/images/start.png");
		startIcon.setFitWidth(Globals.ICON_SIZE);
		startIcon.setFitHeight(Globals.ICON_SIZE);

		ImageView stopIcon = new ImageView("/com/johnwingfield/images/stop.png");
		stopIcon.setFitWidth(Globals.ICON_SIZE);
		stopIcon.setFitHeight(Globals.ICON_SIZE);

		ImageView saveIcon = new ImageView("/com/johnwingfield/images/save.png");
		saveIcon.setFitWidth(Globals.ICON_SIZE);
		saveIcon.setFitHeight(Globals.ICON_SIZE);

		ImageView resetIcon = new ImageView("/com/johnwingfield/images/reset.png");
		resetIcon.setFitWidth(Globals.ICON_SIZE);
		resetIcon.setFitHeight(Globals.ICON_SIZE);

		ImageView continueIcon = new ImageView("/com/johnwingfield/images/continue.png");
		continueIcon.setFitWidth(Globals.ICON_SIZE);
		continueIcon.setFitHeight(Globals.ICON_SIZE);

		ImageView deleteIcon = new ImageView("/com/johnwingfield/images/delete.png");
		deleteIcon.setFitWidth(Globals.ICON_SIZE);
		deleteIcon.setFitHeight(Globals.ICON_SIZE);

		// create toolbar items
		bStart.setGraphic(startIcon);
		bStop.setGraphic(stopIcon);
		bSave.setGraphic(saveIcon);
		bReset.setGraphic(resetIcon);
		bContinue.setGraphic(continueIcon);
		bDelete.setGraphic(deleteIcon);

		// turn off text in the buttons
		bStart.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		bStop.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		bSave.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		bReset.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		bContinue.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		bDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

		// add tooltips
		bStart.setTooltip(new Tooltip("Start"));
		bStop.setTooltip(new Tooltip("Stop"));
		bSave.setTooltip(new Tooltip("Save"));
		bReset.setTooltip(new Tooltip("Reset"));
		bContinue.setTooltip(new Tooltip("Continue"));
		bDelete.setTooltip(new Tooltip("Delete"));

		// create the toolbar
		toolBar = new ToolBar(bStart, bStop, bSave, bReset, bContinue, bDelete);
	}

	/**
	 * Generates chosen report
	 * @param choice name of selected report
	 */
	private void Report(String choice) {
		String reportTxt = "";
		int longestProj = 0;
		int longestCode = 0;

		switch (choice) {
			case "By date":
				table.getSortOrder().clear();
				table.getSortOrder().add(dateCol);
				table.getSortOrder().add(projectCol);
				table.getSortOrder().add(codeCol);

				// find longest project & code fields
				for (Jobs row : dataList) {
					String curProj = row.getProject();
					String curCode = row.getCode();
					if (curProj.length() > longestProj) longestProj = curProj.length();
					if (curCode.length() > longestCode) longestCode = curCode.length();
				}

				// collate data
				for (int i = 0; i < dataList.size(); ++i) {
					String curDate = dataList.get(i).getDate(); // get first row data
					String curProj = dataList.get(i).getProject();
					String curCode = dataList.get(i).getCode();
					String curDur = dataList.get(i).getDuration();
					double totalDur = convertToMS(curDur);
					reportTxt += curDate + " " + curProj + spaces(longestProj - curProj.length()) + curCode + spaces(longestCode - curCode.length()) + curDur + "\n";
					++i;

					if (i < dataList.size() && dataList.get(i).getDate().equals(curDate)) {
						// while next date matches first row
						while (i < dataList.size() && dataList.get(i).getDate().equals(curDate)) {
							curProj = dataList.get(i).getProject();
							curCode = dataList.get(i).getCode();
							curDur = dataList.get(i).getDuration();
							totalDur += convertToMS(curDur);
							reportTxt += spaces(curDate.length()) + curProj + spaces(longestProj - curProj.length()) + curCode + spaces(longestCode - curCode.length()) + curDur + "\n";
							++i;
						}

						if (i < dataList.size() && !dataList.get(i).getDate().equals(curDate)) {
							--i;
						}
					}
					else {
						--i;
					}

					reportTxt += spaces(curDate.length()) + spaces(longestProj) + spaces(longestCode) + convertToStr(totalDur) + "\n";
				}

				table.getSortOrder().clear();
				writeReport(reportTxt, Globals.REPORT_BY_DATE);
				JOptionPane.showMessageDialog(null, "Report saved as " + Globals.reportByDate, "Report", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "By date range":
				DateSelector();
				break;
			case "By project":
				table.getSortOrder().clear();
				table.getSortOrder().add(projectCol);
				table.getSortOrder().add(codeCol);
				table.getSortOrder().add(dateCol);

				// find longest project & code fields
				for (Jobs row : dataList) {
					String curProj = row.getProject();
					String curCode = row.getCode();
					if (curProj.length() > longestProj) longestProj = curProj.length();
					if (curCode.length() > longestCode) longestCode = curCode.length();
				}

				// collate data
				for (int i = 0; i < dataList.size(); ++i) {
					String curProj = dataList.get(i).getProject(); // get first row data
					String curCode = dataList.get(i).getCode();
					String curDate = dataList.get(i).getDate();
					String curDur = dataList.get(i).getDuration();
					double totalDur = convertToMS(curDur);
					reportTxt += curProj + spaces(longestProj - curProj.length()) + curCode + spaces(longestCode - curCode.length()) + curDate + " " + curDur + "\n";
					++i;

					if (i < dataList.size() && dataList.get(i).getProject().equals(curProj)) {
						// while next date matches first row
						while (i < dataList.size() && dataList.get(i).getProject().equals(curProj)) {
							curCode = dataList.get(i).getCode();
							curDate = dataList.get(i).getDate();
							curDur = dataList.get(i).getDuration();
							totalDur += convertToMS(curDur);
							reportTxt += curProj + spaces(longestProj - curProj.length()) + curCode + spaces(longestCode - curCode.length()) + curDate + " " + curDur + "\n";
							++i;
						}

						if (i < dataList.size() && !dataList.get(i).getProject().equals(curProj)) {
							--i;
						}
					}
					else {
						--i;
					}

					reportTxt += spaces(curDate.length()) + spaces(longestProj) + spaces(longestCode) + convertToStr(totalDur) + "\n";
				}

				table.getSortOrder().clear();
				writeReport(reportTxt, Globals.REPORT_BY_PROJECT);
				JOptionPane.showMessageDialog(null, "Report saved as " + Globals.reportByProject, "Report", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "Specific project":
				String specifiedProject = JOptionPane.showInputDialog("Report on specific project", "Enter project name");

				if (specifiedProject.isEmpty() || specifiedProject.equals("Enter project name")) {
					return;
				}

				table.getSortOrder().clear();
				table.getSortOrder().add(projectCol);
				table.getSortOrder().add(codeCol);
				table.getSortOrder().add(dateCol);

				// find project & longest code
				for (Jobs row : dataList) {
					String curCode = row.getCode();
					if (row.getProject().equals(specifiedProject) && curCode.length() > longestCode) {
						longestCode = curCode.length();
					}
				}

				if (longestCode == 0) { // project not found
					JOptionPane.showMessageDialog(null, "Project: " + specifiedProject + " not found", "Not found", JOptionPane.WARNING_MESSAGE);
					table.getSortOrder().clear();
					return;
				}

				// collate data
				for (int i = 0; i < dataList.size(); ++i) {
					if (!dataList.get(i).getProject().equals(specifiedProject)) {
						continue;
					}

					String curProj = dataList.get(i).getProject(); // get first row data
					String curCode = dataList.get(i).getCode();
					String curDate = dataList.get(i).getDate();
					String curDur = dataList.get(i).getDuration();
					double totalDur = convertToMS(curDur);
					longestProj = curProj.length();
					reportTxt += curProj + " " + curCode + spaces(longestCode - curCode.length()) + curDate + " " + curDur + "\n";
					++i;

					if (i < dataList.size() && dataList.get(i).getProject().equals(curProj)) {
						// while next date matches first row
						while (i < dataList.size() && dataList.get(i).getProject().equals(curProj)) {
							curCode = dataList.get(i).getCode();
							curDate = dataList.get(i).getDate();
							curDur = dataList.get(i).getDuration();
							totalDur += convertToMS(curDur);
							reportTxt += curProj + " " + curCode + spaces(longestCode - curCode.length()) + curDate + " " + curDur + "\n";
							++i;
						}

						if (i < dataList.size() && !dataList.get(i).getProject().equals(curProj)) {
							--i;
						}
					}
					else {
						--i;
					}

					reportTxt += spaces(curDate.length()) + spaces(longestProj) + spaces(longestCode) + convertToStr(totalDur) + "\n";
				}

				table.getSortOrder().clear();
				writeReport(reportTxt, Globals.REPORT_SPECIFIC_PROJECT);
				JOptionPane.showMessageDialog(null, "Report saved as " + Globals.reportSpecificProject, "Report", JOptionPane.INFORMATION_MESSAGE);
				break;
		}
	}

	/**
	 * Create date selection form with start & end date pickers
	 */
	private void DateSelector() {
		Stage stage2 = new Stage();
		stage2.setTitle("Date Selector");

		VBox vbox = new VBox(10);
		vbox.setStyle("-fx-padding: 10;");
		Scene scene = new Scene(vbox, 100, 180);
		stage2.setScene(scene);
		stage2.setResizable(false);

		startDatePicker = new DatePicker();
		endDatePicker = new DatePicker();
		startDatePicker.setValue(LocalDate.now());
		endDatePicker.setValue(startDatePicker.getValue().plusDays(1));

		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label startLabel = new Label("Start Date:");
		gridPane.add(startLabel, 0, 0);
		GridPane.setHalignment(startLabel, HPos.LEFT);
		gridPane.add(startDatePicker, 0, 1);

		Label endLabel = new Label("End Date:");
		gridPane.add(endLabel, 0, 2);
		GridPane.setHalignment(endLabel, HPos.LEFT);
		gridPane.add(endDatePicker, 0, 3);

		Button bOK = new Button("OK");
		GridPane.setHalignment(bOK, HPos.CENTER);
		gridPane.add(bOK, 0, 4);

		bOK.setOnAction(e -> {
			String sd = startDatePicker.getValue().toString();
			String ed = endDatePicker.getValue().toString();
			startDate = sd.substring(8,10) + "/" + sd.substring(5,7) + "/" + sd.substring(2,4);
			endDate = ed.substring(8,10) + "/" + ed.substring(5,7) + "/" + ed.substring(2,4);
			stage2.close();
			ReportDateRange();
		});

		vbox.getChildren().add(gridPane);
		stage2.show();
	}

	/**
	 * Report on date range, after selecting start & end dates
	 */
	private void ReportDateRange() {
		String reportTxt = "";
		int longestProj = 0;
		int longestCode = 0;

		table.getSortOrder().clear();
		table.getSortOrder().add(dateCol);
		table.getSortOrder().add(projectCol);
		table.getSortOrder().add(codeCol);

		// find longest project & code fields within the date range
		for (Jobs row : dataList) {
			String curDate = row.getDate();
			String curProj = row.getProject();
			String curCode = row.getCode();
			int rowDate = Integer.parseInt(curDate.substring(6, 8) + curDate.substring(3, 5) + curDate.substring(0,2));

			if (rowDate >= Integer.parseInt(startDate.substring(6, 8) + startDate.substring(3, 5) + startDate.substring(0,2)) &&
					rowDate <= Integer.parseInt(endDate.substring(6, 8) + endDate.substring(3, 5) + endDate.substring(0,2))) {
				if (curProj.length() > longestProj) longestProj = curProj.length();
				if (curCode.length() > longestCode) longestCode = curCode.length();
			}
		}

		// collate data
		for (int i = 0; i < dataList.size(); ++i) {
			String curDate = dataList.get(i).getDate(); // get first row data
			String curProj = dataList.get(i).getProject();
			String curCode = dataList.get(i).getCode();
			String curDur = dataList.get(i).getDuration();

			int nextDate = Integer.parseInt(curDate.substring(6, 8) + curDate.substring(3, 5) + curDate.substring(0,2));

			if (nextDate < Integer.parseInt(startDate.substring(6, 8) + startDate.substring(3, 5) + startDate.substring(0,2)) ||
					nextDate > Integer.parseInt(endDate.substring(6, 8) + endDate.substring(3, 5) + endDate.substring(0,2))) {
				continue;
			}

			double totalDur = convertToMS(curDur);
			reportTxt += curDate + " " + curProj + spaces(longestProj - curProj.length()) + curCode + spaces(longestCode - curCode.length()) + curDur + "\n";
			++i;

			if (i < dataList.size() && dataList.get(i).getDate().equals(curDate)) {
				// while next date matches first row
				while (i < dataList.size() && dataList.get(i).getDate().equals(curDate)) {
					curProj = dataList.get(i).getProject();
					curCode = dataList.get(i).getCode();
					curDur = dataList.get(i).getDuration();
					totalDur += convertToMS(curDur);
					reportTxt += spaces(curDate.length()) + curProj + spaces(longestProj - curProj.length()) + curCode + spaces(longestCode - curCode.length()) + curDur + "\n";
					++i;
				}

				if (i < dataList.size() && !dataList.get(i).getDate().equals(curDate)) {
					--i;
				}
			}
			else {
				--i;
			}

			reportTxt += spaces(curDate.length()) + spaces(longestProj) + spaces(longestCode) + convertToStr(totalDur) + "\n";
		}

		table.getSortOrder().clear();
		writeReport(reportTxt, Globals.REPORT_BY_RANGE);
		JOptionPane.showMessageDialog(null, "Report saved as " + Globals.reportByDateRange, "Report", JOptionPane.INFORMATION_MESSAGE);
	}
}
