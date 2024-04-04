package com.example.cmpt305finalproject;


import com.example.cmpt305finalproject.DAOs.ApiPropertyAssessmentDAO;
import com.example.cmpt305finalproject.cmpt305.Graphing;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import org.controlsfx.control.SearchableComboBox;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
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
        ApiPropertyAssessmentDAO paDAO =
                new ApiPropertyAssessmentDAO("https://data.edmonton.ca/resource/qi6a-xuwt.csv");
        String assessmentClass = assessmentClassesCB.getValue();
        String neighbourhood = neighbourhoodsCB.getValue();
        //List<String> data = paDAO.getYearAssessedValue(neighbourhood, assessmentClass);
        List<String> sumData = paDAO.getYearAssessedValueSum(neighbourhood, assessmentClass);
        List<String> avgData = paDAO.getYearAssessedValueAvg(neighbourhood, assessmentClass);


        Graphing graphing = new Graphing(avgData);
        XYChart.Series series = new XYChart.Series();
        HashMap<String, Long> map = graphing.getYearAccessedValueMap();

        map.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByKey())
                               .forEach(p -> series.getData().add(new XYChart.Data(p.getKey(), p.getValue())));
        lineChart.setTitle("Historical Data - Sum of Assessed Values vs Year");
        lineChart.setLegendVisible(false);
        lineChart.getData().add(series);
    }

    public void generateSummaryClick() {
        //TODO
    }

    public void clearClick() {
        //TODO
    }
}