package com.example.cmpt305finalproject;


import com.example.cmpt305finalproject.DAOs.ApiPropertyAssessmentDAO;
import com.example.cmpt305finalproject.cmpt305.AssessValueYearMap;
import com.example.cmpt305finalproject.cmpt305.LineGraphSeries;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.SearchableComboBox;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private ComboBox<String> assessmentClassesCB;

    @FXML
    private SearchableComboBox<String> neighbourhoodsCB;

    @FXML
    private LineChart<String, Long> lineChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ApiPropertyAssessmentDAO propertyAssessmentDAO =
                new ApiPropertyAssessmentDAO("https://data.edmonton.ca/resource/q7d6-ambg.csv");
        ApiPropertyAssessmentDAO historicalPropertyAssessmentDAO =
                new ApiPropertyAssessmentDAO("https://data.edmonton.ca/resource/qi6a-xuwt.csv");

        //List<String> assessmentClasses = propertyAssessmentDAO.getAssessmentClasses();
        List<String> historicalAssessmentClasses = historicalPropertyAssessmentDAO.getAssessmentClasses();
        assessmentClassesCB.getItems().setAll(historicalAssessmentClasses);

        //List<String> neighbourhoods = propertyAssessmentDAO.getNeighbourhoods();
        List<String> historicalNeighbourhoods = historicalPropertyAssessmentDAO.getNeighbourhoods();
        neighbourhoodsCB.getItems().setAll(historicalNeighbourhoods);
    }

    public void generateGraphClick() {
        lineChart.getData().clear();
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();

        generateSumGraph(neighbourhood, assessmentClass);
    }

    private void generateSumGraph(String neighbourhood, String assessmentClass) {
        ApiPropertyAssessmentDAO paDAO =
                new ApiPropertyAssessmentDAO("https://data.edmonton.ca/resource/qi6a-xuwt.csv");
        Map<String, Long> sumData = paDAO.getYearAssessedValueSum(neighbourhood, assessmentClass);

        LineGraphSeries lineGraphSeries = new LineGraphSeries(sumData);

        lineChart.setTitle("Historical Data - Sum of Assessed Values vs Year");
        lineChart.setLegendVisible(false);
        lineChart.getData().add(lineGraphSeries.getSeries());
    }
}