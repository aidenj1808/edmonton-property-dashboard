package com.example.cmpt305finalproject;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.SearchableComboBox;
import java.io.BufferedReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private ComboBox<String> assessmentClassesCB;

    @FXML
    private SearchableComboBox<String> neighbourhoodsCB;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> list = FXCollections.observableArrayList("ALL",
                "FARMLAND",
                "NONRES MUNICIPAL/RES EDUCATION",
                "COMMERCIAL",
                "OTHER RESIDENTIAL",
                "RESIDENTIAL");
        assessmentClassesCB.setItems(list);

        List<String> allNeighbourhoods = getAllNeighbourhoods("neighbourhoods.txt");
        neighbourhoodsCB.getItems().setAll(allNeighbourhoods);
    }

    public List<String> getAllNeighbourhoods(String filename) {
        List<String> allNeighbourhoods = new ArrayList<>();
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of("src/main/java/com/example/cmpt305finalproject", filename));
            String line;
            while ( (line = reader.readLine()) != null) {
                allNeighbourhoods.add(line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return allNeighbourhoods;
    }
}