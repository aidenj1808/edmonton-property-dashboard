package com.example.cmpt305finalproject;


import com.example.cmpt305finalproject.DAOs.ApiPropertyAssessmentDAO;
import com.example.cmpt305finalproject.cmpt305.AssessValueYearMap;
import com.example.cmpt305finalproject.cmpt305.DataSeries;
import com.example.cmpt305finalproject.cmpt305.LineGraphSeries;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import org.controlsfx.control.SearchableComboBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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

    //@FXML
    //private TableView<ObservableList> propertyTable;

    @FXML
    private LineChart<String, Long> lineChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
    }

    public void generateGraphClick() {
        lineChart.getData().clear();
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();

        generateSumGraph(neighbourhood, assessmentClass);
    }

    private void generateSumGraph(String neighbourhood, String assessmentClass) {
        Map<String, Long> sumData = historicalDAO.getYearAssessedValueSum(neighbourhood, assessmentClass);

        LineGraphSeries lineGraphSeries = new LineGraphSeries(sumData);

        lineChart.setTitle("Historical Data - Sum of Assessed Values vs Year");
        lineChart.setLegendVisible(false);
        lineChart.getData().add(lineGraphSeries.getSeries());
    }

    public void generateSummaryClick() {
        //TODO
    }

    public void clearButtonClick() {
        //TODO
    }
}