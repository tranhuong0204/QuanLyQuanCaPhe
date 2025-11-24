package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.MonDemo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class SanPhamController {
//    @FXML
//    private GridPane gridMon;

    @FXML
    private ScrollPane scrollPane; // bind this to a ScrollPane in your FXML


    private final ObservableList<MonDemo> danhSachMon = FXCollections.observableArrayList();

    // added field to track selected item
    private VBox selectedBox;

    private MonDemo mon;
//    @FXML
//    public void initialize() {
//        try {
//            danhSachMon.addAll(
//                    new MonDemo("Iced Mocha", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 140),
//                    new MonDemo("Hot Chocolate", getClass().getResource("/com/example/quanlyquancaphe/images/ca-phe-kem-muoi-beo-ngay.jpg").toExternalForm(), 100),
//                    new MonDemo("Chai Latte", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 100),
//                    new MonDemo("Cappuccino", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 80),
//                    new MonDemo("Espresso", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 50),
//                    new MonDemo("Americano", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 60),
//                    new MonDemo("Macchiato", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 90),
//                    new MonDemo("Tea", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 40),
//                    new MonDemo("Croissant", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 70),
//                    new MonDemo("Iced Mocha", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 140),
//                    new MonDemo("Hot Chocolate", getClass().getResource("/com/example/quanlyquancaphe/images/ca-phe-kem-muoi-beo-ngay.jpg").toExternalForm(), 100),
//                    new MonDemo("Chai Latte", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 100),
//                    new MonDemo("Cappuccino", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 80),
//                    new MonDemo("Espresso", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 50),
//                    new MonDemo("Americano", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 60),
//                    new MonDemo("Macchiato", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 90),
//                    new MonDemo("Tea", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 40),
//                    new MonDemo("Croissant", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 70)
//            );
//
//            int col = 0;
//            int row = 0;
//
//            for (MonDemo mon : danhSachMon) {
//                ImageView img = new ImageView(new Image(mon.getHinhAnh()));
//                img.setFitWidth(100);
//                img.setFitHeight(100);
//
//                Label ten = new Label(mon.getTenMon());
//                Label gia = new Label(String.format(" %.2f", mon.getGiaCa()));
//
//                VBox box = new VBox(5, img, ten, gia);
//                box.setAlignment(Pos.CENTER);
//                box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
//
//                // make the VBox clickable
//                box.setOnMouseClicked(e -> {
//                    // reset previous selection style
//                    if (selectedBox != null) {
//                        selectedBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
//                    }
//                    // apply selected style
//                    box.setStyle("-fx-background-color: #cce5ff; -fx-padding: 10; -fx-border-color: #2a9df4; -fx-border-width: 2;");
//                    selectedBox = box;
//
//                    // show details or perform any action
//                    showDetails(mon);
//                });
//
//                gridMon.add(box, col, row);
//
//                col++;
//                if (col == 3) {
//                    col = 0;
//                    row++;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    public void initialize() {
        if (scrollPane == null) {
            System.err.println("SanPhamController.initialize: scrollPane is null. Check your FXML: set fx:controller and fx:id=\"scrollPane\" on the ScrollPane.");
            return;
        }
        // sample data (replace with real data)
        danhSachMon.addAll(
                new MonDemo("Iced Mocha", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 140),
                new MonDemo("Hot Chocolate", getClass().getResource("/com/example/quanlyquancaphe/images/ca-phe-kem-muoi-beo-ngay.jpg").toExternalForm(), 100),
                new MonDemo("Chai Latte", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 100),
                new MonDemo("Cappuccino", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 80),
                new MonDemo("Espresso", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 50),
                new MonDemo("Americano", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 60),
                new MonDemo("Macchiato", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 90),
                new MonDemo("Tea", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 40),
                new MonDemo("Croissant", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 70),
                new MonDemo("Iced Mocha", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 140),
                new MonDemo("Hot Chocolate", getClass().getResource("/com/example/quanlyquancaphe/images/ca-phe-kem-muoi-beo-ngay.jpg").toExternalForm(), 100),
                new MonDemo("Chai Latte", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 100),
                new MonDemo("Cappuccino", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 80),
                new MonDemo("Espresso", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 50),
                new MonDemo("Americano", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 60),
                new MonDemo("Macchiato", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 90),
                new MonDemo("Tea", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 40),
                new MonDemo("Croissant", getClass().getResource("/com/example/quanlyquancaphe/images/hinhnencafe.jpg").toExternalForm(), 70)
        );

        // create a TilePane that wraps items horizontally and flows to next row
        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefColumns(3); // desired columns before wrapping
//        tilePane.setTileAlignment(Pos.TOP_CENTER);
//        tilePane.setAlignment(Pos.TOP_CENTER);

        // build item boxes and add to tilePane
        for (MonDemo mon : danhSachMon) {
            ImageView img = new ImageView(new Image(mon.getHinhAnh()));
            img.setFitWidth(100);
            img.setFitHeight(100);

            Label ten = new Label(mon.getTenMon());
            Label gia = new Label(String.format(" %.2f", mon.getGiaCa()));

            VBox box = new VBox(5, img, ten, gia);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");

            box.setOnMouseClicked(e -> {
                if (selectedBox != null) {
                    selectedBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
                }
                box.setStyle("-fx-background-color: #cce5ff; -fx-padding: 10; -fx-border-color: #2a9df4; -fx-border-width: 2;");
                selectedBox = box;
                showDetails(mon);
            });


            tilePane.getChildren().add(box);
        }

        // put TilePane into the ScrollPane so content can scroll when there are many items
        scrollPane.setContent(tilePane);
        scrollPane.setFitToWidth(true);
    }

    // added helper to show product details
    private void showDetails(MonDemo mon) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText(mon.getTenMon());
        alert.setContentText(String.format("Price: %.2f", mon.getGiaCa()));
        alert.showAndWait();
    }
}
