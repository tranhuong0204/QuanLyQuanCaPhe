module com.example.quanlyquancaphe {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;


    opens com.example.quanlyquancaphe to javafx.fxml;
    exports com.example.quanlyquancaphe;
    exports com.example.quanlyquancaphe.controllers;
    opens com.example.quanlyquancaphe.controllers to javafx.fxml;
}