package com.example.cmpt305finalproject;


import com.example.cmpt305finalproject.DAOs.ApiPropertyAssessmentDAO;
import com.example.cmpt305finalproject.cmpt305.LineGraphSeries;
import com.example.cmpt305finalproject.cmpt305.TableViewEntry;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.control.SearchableComboBox;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {
    private final String currentYearEndpoint = "https://data.edmonton.ca/resource/q7d6-ambg.csv";
    private final String historicalEndpoint = "https://data.edmonton.ca/resource/qi6a-xuwt.csv";
    private ApiPropertyAssessmentDAO currentYearDAO;
    private ApiPropertyAssessmentDAO historicalDAO;

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

    private int page = 0;
    private int pageSize;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pageSize = 100;
        currentYearDAO = new ApiPropertyAssessmentDAO(currentYearEndpoint);
        historicalDAO = new ApiPropertyAssessmentDAO(historicalEndpoint);

        //List<String> assessmentClasses = propertyAssessmentDAO.getAssessmentClasses();
        List<String> historicalAssessmentClasses = historicalDAO.getAssessmentClasses();
        assessmentClassesCB.getItems().setAll(historicalAssessmentClasses);

        //List<String> neighbourhoods = propertyAssessmentDAO.getNeighbourhoods();
        List<String> historicalNeighbourhoods = historicalDAO.getNeighbourhoods();
        neighbourhoodsCB.getItems().setAll(historicalNeighbourhoods);

        List<String> seriesTypes = List.of("Sum", "Average", "Average without outliers", "Median");
        seriesCB.getItems().setAll(seriesTypes);

        setupTableColumns();
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

    private void generateGraph(String neighbourhood, String assessmentClass, String series) {

        Map<String, Long> data = getSeriesData(neighbourhood, assessmentClass, series);

        LineGraphSeries lineGraphSeries = new LineGraphSeries(data);
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

    public void graphSeriesButtonClick() {
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();
        String series = seriesCB.getValue();

        generateGraph(neighbourhood, assessmentClass, series);
    }

    public void generatePropertiesTableButtonClick() {
        page = 0;
        System.out.println(page);

        updateTableValues(0);
        updateTableTitle();
    }

    public void clearButtonClick() {
        lineChart.getData().clear();
        lineChart.setTitle(null);

        propertyTable.getItems().clear();
        tableViewTitle.setText("");
    }

    public void updateTableValues(int page) {
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();

        ObservableList<TableViewEntry> data = historicalDAO.getTablePage(neighbourhood, assessmentClass, page, pageSize);
        propertyTable.setItems(data);
    }

    private void updateTableTitle() {
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();

        tableViewTitle.setText(assessmentClass + " Property Assessments in " + neighbourhood);
    }

    public int tableViewNextButtonClick(){
        if (propertyTable.getItems().size() >= pageSize) {
            page += 1;
            System.out.println(page);

            updateTableValues(page);
        }
        return page;
    }

    public int tableViewPreviousButtonClick(){

        page -= 1;
        System.out.println(page);
        updateTableValues(page);
        return page;
    }
}