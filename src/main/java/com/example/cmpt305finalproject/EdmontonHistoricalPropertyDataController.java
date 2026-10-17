package com.example.cmpt305finalproject;

import com.example.cmpt305finalproject.DAOs.ApiPropertyAssessmentDAO;
import com.example.cmpt305finalproject.cmpt305.LineGraphSeries;
import com.example.cmpt305finalproject.cmpt305.TableViewEntry;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.SearchableComboBox;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class EdmontonHistoricalPropertyDataController implements Initializable {
    private final String currentYearEndpoint = "https://data.edmonton.ca/resource/q7d6-ambg.csv";
    private final String historicalEndpoint = "https://data.edmonton.ca/resource/qi6a-xuwt.csv";
    private ApiPropertyAssessmentDAO currentYearDAO;
    private ApiPropertyAssessmentDAO historicalDAO;
    private List<String> currentSeries = new ArrayList<>();

    // A single thread runs requests in order. It's a daemon so a request still in progress
    // doesn't keep the app running after the window closes.
    private final ExecutorService requestExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "api-requests");
        thread.setDaemon(true);
        return thread;
    });

    private record FilterOptions(List<String> assessmentClasses, List<String> neighbourhoods) {}

    @FXML
    private HBox controlsBar;

    @FXML
    private HBox tablePagingBar;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> assessmentClassesCB;

    @FXML
    private SearchableComboBox<String> neighbourhoodsCB;

    @FXML
    private ComboBox<String> seriesCB;

    @FXML
    private TableView<TableViewEntry> propertyTable;

    @FXML
    private Label tableViewTitle;

    @FXML
    private TableColumn<TableViewEntry, Integer> accountNumberColumn;

    @FXML
    private TableColumn<TableViewEntry, Integer> assessmentYearColumn;

    @FXML
    private TableColumn<TableViewEntry, Long> assessmentValueColumn;

    @FXML
    private TableColumn<TableViewEntry, String> suiteColumn;

    @FXML
    private TableColumn<TableViewEntry, String> houseNumberColumn;

    @FXML
    private TableColumn<TableViewEntry, String> streetNameColumn;

    @FXML
    private TableColumn<TableViewEntry, String> yearBuiltColumn;

    @FXML
    private TableColumn<TableViewEntry, String> lotSizeColumn;

    @FXML
    private TableColumn<TableViewEntry, String> garageColumn;

    @FXML
    private LineChart<String, Long> lineChart;

    @FXML
    private Label tableViewPageLabel;

    private int page = 0;
    private int pageSize;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pageSize = 100;

        List<String> seriesTypes = List.of("Sum", "Average", "Average without outliers", "Median");
        seriesCB.getItems().setAll(seriesTypes);

        setupTableColumns();

        runInBackground("Loading neighbourhoods and assessment classes...",
                this::loadFilterOptions, this::showFilterOptions);
    }

    /**
     * Connects to both datasets and fetches the options for the assessment class and neighbourhood
     * dropdowns. Runs on the background thread.
     *
     * @return the historical dataset's assessment classes and neighbourhoods
     */
    private FilterOptions loadFilterOptions() {
        currentYearDAO = new ApiPropertyAssessmentDAO(currentYearEndpoint);
        historicalDAO = new ApiPropertyAssessmentDAO(historicalEndpoint);

        List<String> historicalAssessmentClasses = historicalDAO.getAssessmentClasses();
        List<String> historicalNeighbourhoods = historicalDAO.getNeighbourhoods();
        return new FilterOptions(historicalAssessmentClasses, historicalNeighbourhoods);
    }

    /**
     * Fills the assessment class and neighbourhood dropdowns.
     *
     * @param options the options to show
     */
    private void showFilterOptions(FilterOptions options) {
        assessmentClassesCB.getItems().setAll(options.assessmentClasses());
        neighbourhoodsCB.getItems().setAll(options.neighbourhoods());
    }

    /**
     * Runs an API request on the background thread, then passes its result to the JavaFX thread.
     * The controls are disabled and a loading message is shown until the request finishes.
     *
     * @param status the message to show while the request runs
     * @param request the request to run off the JavaFX thread
     * @param onSuccess receives the request's result on the JavaFX thread
     * @param <T> the type of the request's result
     */
    private <T> void runInBackground(String status, Callable<T> request, Consumer<T> onSuccess) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return request.call();
            }
        };
        task.setOnSucceeded(event -> {
            hideLoading();
            onSuccess.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            hideLoading();
            showError(task.getException());
        });

        showLoading(status);
        requestExecutor.execute(task);
    }

    /**
     * Disables the controls and shows the loading indicator with a status message.
     *
     * @param status the message to show
     */
    private void showLoading(String status) {
        controlsBar.setDisable(true);
        tablePagingBar.setDisable(true);
        loadingIndicator.setVisible(true);
        statusLabel.setText(status);
    }

    /**
     * Re-enables the controls and hides the loading indicator and status message.
     */
    private void hideLoading() {
        controlsBar.setDisable(false);
        tablePagingBar.setDisable(false);
        loadingIndicator.setVisible(false);
        statusLabel.setText("");
    }

    /**
     * Logs a failed request and shows its error in a dialog.
     *
     * @param error the exception the request threw
     */
    private void showError(Throwable error) {
        error.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, Objects.requireNonNullElse(error.getMessage(), error.toString()));
        alert.setHeaderText("Couldn't load the data");
        alert.show();
    }

    /**
     * Shows a message in an information dialog.
     *
     * @param message the message to show
     */
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.show();
    }

    private void setupTableColumns() {
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, Integer>("accountNum"));
        assessmentYearColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, Integer>("assessmentYear"));
        assessmentValueColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, Long>("assessedValue"));
        suiteColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, String>("suite"));
        houseNumberColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, String>("houseNumber"));
        streetNameColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, String>("streetName"));
        yearBuiltColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, String>("yearBuilt"));
        lotSizeColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, String>("lotSize"));
        garageColumn.setCellValueFactory(new PropertyValueFactory<TableViewEntry, String>("garage"));
    }

    /**
     * Adds a series to the line graph and updates the graph's title and legend.
     *
     * @param series the series' name, such as "Median"
     * @param data the series' value for each assessment year
     */
    private void addSeriesToGraph(String series, Map<String, Long> data) {
        LineGraphSeries lineGraphSeries = new LineGraphSeries(data);
        this.currentSeries.add(series);

        if (lineChart.getTitle() == null) {
            lineChart.setTitle("Historical Data - Assessed Values vs Year For " + series);
        } else {
            lineChart.setTitle(lineChart.getTitle() + ", " + series);
        }
        lineGraphSeries.getSeries().setName(series);

        lineChart.setLegendVisible(true);
        lineChart.getData().add(lineGraphSeries.getSeries());
    }

    private Map<String, Long> getSeriesData(String neighbourhood, String assessmentClass, String series) throws IllegalArgumentException {
        return switch (series) {
            case "Sum" -> historicalDAO.getYearAssessedValueSum(neighbourhood, assessmentClass);
            case "Average" -> historicalDAO.getYearAssessedValueAvg(neighbourhood, assessmentClass);
            case "Median" -> historicalDAO.getYearAssessedValueMedian(neighbourhood, assessmentClass);
            case "Average without outliers" -> historicalDAO.getYearAvgNoOutliers(neighbourhood, assessmentClass);
            default -> throw new IllegalArgumentException("Invalid graph series selected");
        };
    }

    /**
     * Fetches the selected series in the background and adds it to the line graph.
     */
    public void graphSeriesButtonClick() {
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();
        String series = seriesCB.getValue();

        if (assessmentClass == null || neighbourhood == null || series == null) {
            showMessage("Choose an assessment class, neighbourhood and series first.");
            return;
        }
        if (this.currentSeries.contains(series)) {
            // Could do some kind of pop notification, silent return do nothing for now.
            return;
        }

        runInBackground("Loading " + series + " for " + neighbourhood + "...",
                () -> getSeriesData(neighbourhood, assessmentClass, series),
                data -> addSeriesToGraph(series, data));
    }

    /**
     * Loads the first page of properties for the selected assessment class and neighbourhood.
     */
    public void generatePropertiesTableButtonClick() {
        if (assessmentClassesCB.getValue() == null || neighbourhoodsCB.getValue() == null) {
            showMessage("Choose an assessment class and neighbourhood first.");
            return;
        }
        page = 0;
        setTableViewPageLabel();
        updateTableValues(0);
        updateTableTitle();
    }

    public void clearButtonClick() {
        lineChart.getData().clear();
        lineChart.setTitle(null);
        this.currentSeries.clear();
        tableViewPageLabel.setText("No Pages");
        propertyTable.getItems().clear();
        tableViewTitle.setText("");
    }

    /**
     * Fetches a page of properties in the background and shows it in the table.
     *
     * @param page the zero-based page number to show
     */
    public void updateTableValues(int page) {
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();

        runInBackground("Loading properties in " + neighbourhood + "...",
                () -> historicalDAO.getTablePage(neighbourhood, assessmentClass, page, pageSize),
                propertyTable::setItems);
    }

    private void updateTableTitle() {
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();

        tableViewTitle.setText(assessmentClass + " Property Assessments in " + neighbourhood);
    }

    public int tableViewNextButtonClick(){
        if (propertyTable.getItems().size() >= pageSize) {
            page += 1;
            setTableViewPageLabel();
            updateTableValues(page);
        }
        return page;
    }

    public int tableViewPreviousButtonClick(){
        if (page == 0){
            return page;
        }
        else {
            page -= 1;
            updateTableValues(page);
        }
        setTableViewPageLabel();
        return page;
    }

    public void setTableViewPageLabel(){
        String newText = "Page: " + (page+1); // add out of page size
        tableViewPageLabel.setText(newText);
    }
}