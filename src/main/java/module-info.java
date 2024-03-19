module com.example.cmpt305finalproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.net.http;

    opens com.example.cmpt305finalproject to javafx.fxml;
    exports com.example.cmpt305finalproject;
}