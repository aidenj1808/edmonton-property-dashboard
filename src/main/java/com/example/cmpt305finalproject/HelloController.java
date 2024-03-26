package com.example.cmpt305finalproject;


import com.example.cmpt305finalproject.DAOs.ApiPropertyAssessmentDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.SearchableComboBox;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private ComboBox<String> assessmentClassesCB;

    @FXML
    private SearchableComboBox<String> neighbourhoodsCB;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ApiPropertyAssessmentDAO propertyAssessmentDAO =
                new ApiPropertyAssessmentDAO("https://data.edmonton.ca/resource/q7d6-ambg.csv");

        List<String> assessmentClasses = propertyAssessmentDAO.getAssessmentClasses();
        assessmentClassesCB.getItems().setAll(assessmentClasses);

        List<String> neighbourhoods = propertyAssessmentDAO.getNeighbourhoods();
        neighbourhoodsCB.getItems().setAll(neighbourhoods);
    }
}