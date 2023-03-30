module com.example.photoeditormain {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires opencv;
    requires com.google.gson;


    opens com.example.photoeditormain to javafx.fxml;
    exports com.example.photoeditormain;
}