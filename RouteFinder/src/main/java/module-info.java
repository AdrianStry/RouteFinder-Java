module com.example.ca2prog {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.RouteFinder to javafx.fxml;
    exports com.example.RouteFinder;
}