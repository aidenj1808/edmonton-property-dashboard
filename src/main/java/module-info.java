module com.example.cmpt305finalproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    requires org.controlsfx.controls;
    requires java.net.http;

    opens com.example.cmpt305finalproject to javafx.fxml, javafx.base;
    opens com.example.cmpt305finalproject.cmpt305 to javafx.base;
    exports com.example.cmpt305finalproject;
}