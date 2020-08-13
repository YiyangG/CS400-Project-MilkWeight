package application;

/**
 * GUI class for the Milk Weight
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yiyang Gu ygu75@wisc.edu
 */


public class GUI extends Application {
  private static final int WINDOW_WIDTH = 900;
  private static final int WINDOW_HEIGHT = 500;
  private static final String APP_TITLE = "Milk Weight Manager";
  private static final String[] MONTH =
      {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
  private static final String[] ORDERS = {"ASC", "DESC"};
  private static final String[] SORTED_BY =
      {FiledType.ID.name(), FiledType.WEIGHT.name()};
  private static final int DEFAULT_DOUBLE_DECIMAL = 3;
  private Stage primaryStage;
  private DataManager dataManager;
  private FileManager fileManager;
  private TableView<Farm> tableView;
  private String[] farmIDs;
  private String[] years;
  private ComboBox<String> dropDownFarm;
  private ComboBox<String> dropDownYearF;
  private ComboBox<String> dropDownYearA;
  private ComboBox<String> dropDownYearM;
  private ComboBox<String> dropDownSorterF;
  private ComboBox<String> dropDownOrderF;
  private ComboBox<String> dropDownSorterA;
  private ComboBox<String> dropDownOrderA;
  private ComboBox<String> dropDownSorterM;
  private ComboBox<String> dropDownOrderM;
  private VBox outputFormat;
  private GridPane centerPane;
  private VBox vbRight;
  private TextArea reporter;

  public GUI() {
    dataManager = new DataManager();
    fileManager = new FileManager();

    outputFormat = new VBox();
    centerPane = new GridPane();
    dropDownFarm = new ComboBox<String>();
    dropDownYearA = new ComboBox<String>();
    dropDownYearF = new ComboBox<String>();
    dropDownYearM = new ComboBox<String>();
    dropDownSorterF = new ComboBox<String>();
    dropDownOrderF = new ComboBox<String>();
    dropDownSorterA = new ComboBox<String>();
    dropDownOrderA = new ComboBox<String>();
    dropDownSorterM = new ComboBox<String>();
    dropDownOrderM = new ComboBox<String>();
    farmIDs = new String[0];
    years = new String[0];

    dropDownSorterF.getItems().addAll(SORTED_BY);
    dropDownOrderF.getItems().addAll(ORDERS);
    dropDownSorterA.getItems().addAll(SORTED_BY);
    dropDownOrderA.getItems().addAll(ORDERS);
    dropDownSorterM.getItems().addAll(SORTED_BY);
    dropDownOrderM.getItems().addAll(ORDERS);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    BorderPane pane = new BorderPane();
    Scene mainScene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);

    VBox vbLeft = new VBox();
    vbLeft.setAlignment(Pos.BASELINE_LEFT);
    vbLeft.setPadding(new Insets(10, 20, 20, 20));
    vbLeft.setSpacing(5);

    Image image = new Image("1.JPG");
    ImageView imageView = new ImageView(image);
    vbLeft.getChildren().add(imageView);
    Label dashboard = new Label("Dashboard");
    dashboard.setFont(Font.font("Aller EN", FontWeight.BOLD, 18));
    vbLeft.getChildren().add(dashboard);
    Button inputView = new Button("Input");
    vbLeft.getChildren().add(inputView);
    Button outputView = new Button("Result");
    vbLeft.getChildren().add(outputView);
    pane.setLeft(vbLeft);

    HBox hbBot = new HBox(8);
    hbBot.setPadding(new Insets(10, 20, 20, 20));
    Button exit = new Button("Exit");
    hbBot.getChildren().add(exit);
    exit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        primaryStage.close();
      }
    });

    Button save = new Button("Save");
    hbBot.getChildren().add(save);
    save.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select your file(csv file only)");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (null != file) {
          boolean result = fileManager.writeToFile(file);
          if (result) {
            showMsg("Save done!");
          }
        }
      }
    });

    pane.setBottom(hbBot);

    initInputView();
    initOutputView();

    inputView.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        pane.setCenter(centerPane);
        pane.setRight(vbRight);
      }
    });

    outputView.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        pane.setCenter(outputFormat);
        pane.setRight(null);
      }
    });

    primaryStage.setTitle(APP_TITLE);
    primaryStage.setScene(mainScene);
    primaryStage.show();
  }

  private void parseOneFile(File file) {
    boolean readResult = fileManager.readFile(file);
    if (!readResult) {
      showMsg("Read file error, please check file");
      return;
    }
    List<String> fileContents = fileManager.getFileContents();
    List<Farm> farmList = new ArrayList<>();
    try {
      for (String content : fileContents) {
        farmList.add(Util.parseFarm(content));
      }
      farmList.forEach(CheeseFactory::insertFarm);
    } catch (Exception ex) {
      showMsg("Parse file:" + file.getName() + " error, " + ex.getMessage());
    }
  }


  /**
   * when a farm is added,edit or removed or a new file/folder is loaded, the dataset needs to be
   * updated
   */
  private void dataChanged() {
    List<Farm> farms = dataManager.getDataSortedByField(FiledType.ID, true);
    tableView.setItems(FXCollections.observableArrayList(farms));
    farmIDs = farms.stream().map(Farm::getId).distinct().sorted()
        .toArray(String[]::new);
    years = farms.stream().map(Farm::getYear).distinct().sorted()
        .map(String::valueOf).toArray(String[]::new);

    dropDownFarm.getItems().clear();
    dropDownYearA.getItems().clear();
    dropDownYearF.getItems().clear();
    dropDownYearM.getItems().clear();

    dropDownFarm.setValue("FarmID");
    dropDownYearA.setValue("Year");
    dropDownYearF.setValue("Year");
    dropDownYearM.setValue("Year");

    dropDownFarm.getItems().addAll(farmIDs);
    dropDownYearA.getItems().addAll(years);
    dropDownYearF.getItems().addAll(years);
    dropDownYearM.getItems().addAll(years);
  }

  private void initInputView() {
    centerPane = new GridPane();
    centerPane.setHgap(10);
    centerPane.setVgap(10);
    centerPane.setPadding(new Insets(20, 20, 20, 20));

    TextField textField = new TextField();
    textField.setPromptText(
        "Click \"Browse\" to select your csv file or \"Open\" for folder");
    textField.setPrefWidth(400);
    textField.setEditable(false);

    Button fileChooseBtn = new Button("Browse");
    Button open = new Button("Open");
    centerPane.add(textField, 0, 0);
    centerPane.add(fileChooseBtn, 1, 0);
    centerPane.add(open, 2, 0);

    fileChooseBtn.setOnAction(actionEvent -> {
      final FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Select your file(csv file only)");
      fileChooser.setInitialDirectory(new File("."));
      fileChooser.getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
      File file = fileChooser.showOpenDialog(primaryStage);
      if (null != file) {
        textField.setText(file.getAbsolutePath());
        parseOneFile(file);
        dataChanged();
      }
    });

    open.setOnAction(actionEvent -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      directoryChooser.setTitle("Select your folder");
      directoryChooser.setInitialDirectory(new File("."));
      File folder = directoryChooser.showDialog(primaryStage);
      if (null != folder) {
        textField.setText(folder.getAbsolutePath());
        File[] files = folder.listFiles();
        for (File file : files) {
          parseOneFile(file);
        }
        dataChanged();
      }
    });

    tableView = new TableView();
    tableView.setId("tableView");

    TableColumn<Farm, String> farmID = new TableColumn<Farm, String>("Farm ID");
    farmID.setCellValueFactory(new PropertyValueFactory<>("id"));
    farmID.setPrefWidth(200);

    TableColumn<Farm, LocalDate> date =
        new TableColumn<Farm, LocalDate>("Date");
    date.setCellValueFactory(new PropertyValueFactory<>("date"));
    date.setPrefWidth(200);

    TableColumn<Farm, Integer> weight =
        new TableColumn<Farm, Integer>("Weight");
    weight.setCellValueFactory(new PropertyValueFactory<>("weight"));
    weight.setPrefWidth(200);

    tableView.getColumns().addAll(farmID, date, weight);

    dataChanged();

    centerPane.add(tableView, 0, 1, 3, 1);

    vbRight = new VBox();
    vbRight.setAlignment(Pos.BASELINE_CENTER);
    vbRight.setPadding(new Insets(50, 20, 20, 20));
    vbRight.setSpacing(5);
    Button add = new Button("Add");
    vbRight.getChildren().add(add);

    add.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        Stage addDialog = new Stage();
        addDialog.initModality(Modality.APPLICATION_MODAL);
        addDialog.initOwner(primaryStage);
        HBox dialog = createFarmDialog();
        Button addBut = new Button("Add");
        dialog.getChildren().add(addBut);
        Button returnBut1 = new Button("Cancel");
        dialog.getChildren().add(returnBut1);
        returnBut1.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) {
            addDialog.close();
          }
        });

        addBut.setOnAction(event -> {
          try {
            Farm inputFarm = getInputFarm(dialog);
            boolean result = CheeseFactory.insertFarm(inputFarm);
            if (!result) {
              showMsg("Duplicated farm: " + inputFarm.getId());
            } else {
              dataChanged();
            }
          } catch (Exception ex) {
            showMsg(ex.getMessage());
          } finally {
            addDialog.close();
          }
        });
        Scene dialogScene = new Scene(dialog, 760, 40);
        addDialog.setScene(dialogScene);
        addDialog.show();
      }
    });

    Button remove = new Button("Remove");
    vbRight.getChildren().add(remove);
    remove.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        Stage removeStage = new Stage();
        removeStage.initModality(Modality.APPLICATION_MODAL);
        removeStage.initOwner(primaryStage);
        HBox dialog = createFarmDialog();
        Button removeBut = new Button("Remove");
        dialog.getChildren().add(removeBut);
        Button returnBut2 = new Button("Cancel");
        dialog.getChildren().add(returnBut2);
        returnBut2.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) {
            removeStage.close();
          }
        });
        removeBut.setOnAction(actionEvent -> {
          try {
            Farm inputFarm = getInputFarm(dialog);
            Farm removedFarm = CheeseFactory.removeFarm(inputFarm);
            if (null == removedFarm) {
              showMsg("Specified farm not exits, id:" + inputFarm.getId()
                  + " date: " + inputFarm.getDate());
            } else {
              dataChanged();
            }
          } catch (Exception ex) {
            showMsg(ex.getMessage());
          } finally {
            removeStage.close();
          }
        });
        Scene dialogScene = new Scene(dialog, 780, 40);
        removeStage.setScene(dialogScene);
        removeStage.show();
      }
    });

    Button edit = new Button("Edit");
    vbRight.getChildren().add(edit);
    edit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        Stage editDialog = new Stage();
        editDialog.initModality(Modality.APPLICATION_MODAL);
        editDialog.initOwner(primaryStage);
        HBox dialog = createFarmDialog();
        Button editBut = new Button("Edit");
        dialog.getChildren().add(editBut);
        Button returnBut3 = new Button("Cancel");
        dialog.getChildren().add(returnBut3);
        returnBut3.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) {
            editDialog.close();
          }
        });

        editBut.setOnAction(actionEvent -> {
          try {
            Farm inputFarm = getInputFarm(dialog);
            boolean editResult = CheeseFactory.editFarm(inputFarm);
            if (!editResult) {
              showMsg("Specified farm not exits, id:" + inputFarm.getId()
                  + " date: " + inputFarm.getDate());
            } else {
              dataChanged();
            }
          } catch (Exception ex) {
            showMsg(ex.getMessage());
          } finally {
            editDialog.close();
          }
        });
        Scene dialogScene = new Scene(dialog, 780, 40);
        editDialog.setScene(dialogScene);
        editDialog.show();
      }
    });
  }

  private void initOutputView() {
    outputFormat.setPadding(new Insets(10, 20, 20, 20));

    reporter = new TextArea();

    Label farmReport = new Label("FARM REPORT");
    outputFormat.getChildren().add(farmReport);

    HBox farm = new HBox();
    farm.setPadding(new Insets(5, 5, 5, 0));
    farm.setSpacing(8);

    dropDownFarm.setValue("FarmID");
    farm.getChildren().add(dropDownFarm);

    dropDownYearF.setValue("Year");
    farm.getChildren().add(dropDownYearF);

    Button bt1 = new Button("Check");
    farm.getChildren().add(bt1);

    outputFormat.getChildren().add(farm);


    bt1.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        String farmId = dropDownFarm.getValue();
        String year = dropDownYearF.getValue();
        if ("FarmID".equals(farmId) || "Year".equals(year)) {
          reporter.setText("No Data Yet");
        } else {
          List<DataManager.FarmReport> reports =
              dataManager.getFarmReport(farmId, Util.parseAsInt(year));
          if (!reports.isEmpty()) {
            final String format = "%-20s%-20s%-20s%-20s%-20s%-20s\n";
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(format, "Month", "Min", "Max", "Average",
                "Total", "Share(%)"));
            for (DataManager.FarmReport report : reports) {
              sb.append(String.format(format, String.valueOf(report.getMonth()),
                  String.valueOf(report.getMin()),
                  String.valueOf(report.getMax()),
                  Util.formatDouble(report.getAvg(), DEFAULT_DOUBLE_DECIMAL),
                  String.valueOf(report.getTotalWeight()), Util.formatDouble(
                      report.getPercent(), DEFAULT_DOUBLE_DECIMAL)));
            }
            reporter.setText(sb.toString());
          } else {
            reporter.setText("No Records");
          }
        }
      }
    });


    Label annualReport = new Label("ANNUAL REPORT");
    outputFormat.getChildren().add(annualReport);

    HBox annual = new HBox();
    annual.setPadding(new Insets(5, 5, 5, 0));
    annual.setSpacing(8);
    outputFormat.getChildren().add(annual);

    dropDownYearA.setValue("Year");
    annual.getChildren().add(dropDownYearA);

    dropDownSorterA.setValue("Sorted By");
    annual.getChildren().add(dropDownSorterA);

    dropDownOrderA.setValue("Order");
    annual.getChildren().add(dropDownOrderA);

    Button bt2 = new Button("Check");
    annual.getChildren().add(bt2);

    bt2.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        String year = dropDownYearA.getValue();
        String sortedBy = dropDownSorterA.getValue();
        String order = dropDownOrderA.getValue();
        if ("Year".equals(year)) {
          reporter.setText("No Year Selected");
        } else {
          if (!FiledType.getNames().contains(sortedBy)) {
            sortedBy = FiledType.ID.name();
          }
          if (!Arrays.stream(ORDERS).collect(Collectors.toList())
              .contains(order)) {
            order = ORDERS[0];
          }
          List<DataManager.DateRangeReport> reports =
              dataManager.getAnnualReport(Util.parseAsInt(year),
                  FiledType.valueOf(sortedBy), ORDERS[0].equals(order));
          showReports(reports);
        }
      }
    });

    Label monthlyReport = new Label("MONTHLY REPORT");
    outputFormat.getChildren().add(monthlyReport);

    HBox monthly = new HBox();
    monthly.setPadding(new Insets(5, 5, 5, 0));
    monthly.setSpacing(8);
    outputFormat.getChildren().add(monthly);

    dropDownYearM.setValue("Year");
    monthly.getChildren().add(dropDownYearM);

    ComboBox<String> dropDownMonth = new ComboBox<String>();
    dropDownMonth.setValue("Month");
    dropDownMonth.getItems().addAll(MONTH);
    monthly.getChildren().add(dropDownMonth);

    dropDownSorterM.setValue("Sorted By");
    monthly.getChildren().add(dropDownSorterM);

    dropDownOrderM.setValue("Order");
    monthly.getChildren().add(dropDownOrderM);

    Button bt3 = new Button("Check");
    monthly.getChildren().add(bt3);

    bt3.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        String year = dropDownYearM.getValue();
        String month = dropDownMonth.getValue();
        String sortedBy = dropDownSorterM.getValue();
        String order = dropDownOrderM.getValue();
        if ("Year".equals(year) || "Month".equals(month)) {
          reporter.setText("No Records");
        } else {
          if (!FiledType.getNames().contains(sortedBy)) {
            sortedBy = FiledType.ID.name();
          }
          if (!Arrays.stream(ORDERS).collect(Collectors.toList())
              .contains(order)) {
            order = ORDERS[0];
          }
          List<DataManager.DateRangeReport> reports = dataManager
              .getMonthlyReport(Util.parseAsInt(year), Util.parseAsInt(month),
                  FiledType.valueOf(sortedBy), ORDERS[0].equals(order));
          showReports(reports);
        }
      }
    });

    Label rangeReport = new Label("DATE RANGE REPORT");
    outputFormat.getChildren().add(rangeReport);

    HBox rangeLabel = new HBox();
    rangeLabel.setPadding(new Insets(5, 0, 0, 0));
    rangeLabel.setSpacing(138);
    outputFormat.getChildren().add(rangeLabel);

    Label start = new Label("Start Date");
    Label end = new Label("End Date");
    rangeLabel.getChildren().addAll(start, end);

    dropDownSorterF.setValue("Sorted By");
    dropDownOrderF.setValue("Order");

    HBox range = new HBox();
    range.setSpacing(8);
    range.setPadding(new Insets(5, 5, 5, 0));
    DatePicker startDate = new DatePicker();
    DatePicker endDate = new DatePicker();
    range.getChildren().add(startDate);
    range.getChildren().add(endDate);
    range.getChildren().add(dropDownSorterF);
    range.getChildren().add(dropDownOrderF);

    startDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
      @Override
      public void changed(ObservableValue<? extends LocalDate> observableValue,
          LocalDate oldDate, LocalDate newDate) {}
    });

    Button bt4 = new Button("Check");
    range.getChildren().add(bt4);

    bt4.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        String sortedBy = dropDownSorterF.getValue();
        String order = dropDownOrderF.getValue();
        if (end.isBefore(start)) {
          reporter.setText("No Records");
        } else {
          if (!FiledType.getNames().contains(sortedBy)) {
            sortedBy = FiledType.ID.name();
          }
          if (!Arrays.stream(ORDERS).collect(Collectors.toList())
              .contains(order)) {
            order = ORDERS[0];
          }
          List<DataManager.DateRangeReport> reports =
              dataManager.getDateRangeReports(start, end,
                  FiledType.valueOf(sortedBy), ORDERS[0].equals(order));
          showReports(reports);
        }
      }
    });

    outputFormat.getChildren().add(range);
    outputFormat.getChildren().add(reporter);
  }

  private void showReports(List<DataManager.DateRangeReport> reports) {
    if (!reports.isEmpty()) {
      final String format = "%-20s%-20s%-20s%-20s%-20s%-20s\n";
      StringBuilder sb = new StringBuilder();
      sb.append(String.format(format, "Farm", "Min", "Max", "Average", "Total",
          "Share(%)"));
      for (DataManager.DateRangeReport report : reports) {
        sb.append(String.format(format, String.valueOf(report.getFarmId()),
            String.valueOf(report.getMin()), String.valueOf(report.getMax()),
            Util.formatDouble(report.getAvg(), DEFAULT_DOUBLE_DECIMAL),
            String.valueOf(report.getTotalWeight()),
            Util.formatDouble(report.getPercent(), DEFAULT_DOUBLE_DECIMAL)));
      }
      reporter.setText(sb.toString());
    } else {
      reporter.setText("No Records");
    }
  }

  /**
   * create a farm dialog using adding,editing and removing
   *
   * @returna a HBox
   */
  private HBox createFarmDialog() {
    HBox dialog = new HBox(20);
    dialog.setSpacing(8);
    dialog.setPadding(new Insets(6, 3, 5, 5));
    Label farmLabel = new Label("Farm ID");
    dialog.getChildren().add(farmLabel);
    TextField farmText = new TextField();
    farmText.setPromptText("FarmID");
    dialog.getChildren().add(farmText);
    Label dateLabel = new Label("Date");
    dialog.getChildren().add(dateLabel);
    TextField dateText = new TextField();
    dialog.getChildren().add(dateText);
    dateText.setPromptText("Date(yyyy-MM-dd)");
    Label weightLabel = new Label("Weight");
    dialog.getChildren().add(weightLabel);
    TextField weightText = new TextField();
    weightText.setPromptText("Weight");
    dialog.getChildren().add(weightText);

    return dialog;
  }

  /**
   * get the input about the farm entered by user when adding,editing or removing a farm.
   *
   * @param pane the input dialog
   * @return the entered farm
   */
  private Farm getInputFarm(Pane pane) {
    ObservableList<Node> children = pane.getChildren();
    TextField farmText = null;
    TextField dateText = null;
    TextField weightText = null;
    for (int i = 0; i < children.size(); i++) {
      if (children.get(i) instanceof TextField) {
        TextField textField = (TextField) children.get(i);
        switch (textField.getPromptText()) {
          case "FarmID":
            farmText = textField;
            break;
          case "Date(yyyy-MM-dd)":
            dateText = textField;
            break;
          case "Weight":
            weightText = textField;
            break;
          default:
            break;
        }
      }
    }
    String farmId = farmText.getText();
    LocalDate parsedDate = Util.parseAsDate(dateText.getText());
    int weight = Util.parseAsInt(weightText.getText());
    return new Farm(farmId, parsedDate, weight);
  }

  private void showMsg(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.titleProperty().set("Message");
    alert.headerTextProperty().set(message);
    alert.showAndWait();
  }


}
