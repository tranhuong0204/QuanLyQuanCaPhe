module com.example.quanlyquancaphe {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.quanlyquancaphe to javafx.fxml;
    exports com.example.quanlyquancaphe;
}