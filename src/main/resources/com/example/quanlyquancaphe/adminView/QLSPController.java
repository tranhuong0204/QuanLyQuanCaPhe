import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class QLSPController {

    @FXML private TextField txtMaSanPham, txtTenSanPham, txtDonGia, txtTimKiem;
    @FXML private TextArea txtMoTa;
    @FXML private ImageView imgSanPham;
    @FXML private TableView<?> tableSanPham;

    @FXML
    private void onChonAnh() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh sản phẩm");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Ảnh", "*.jpg", "*.png", "*.jpeg")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            imgSanPham.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML private void onThem() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã thêm sản phẩm (demo)!");
        alert.show();
    }

    @FXML private void onSua() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã sửa sản phẩm (demo)!");
        alert.show();
    }

    @FXML private void onXoa() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Đã xóa sản phẩm (demo)!");
        alert.show();
    }

    @FXML private void onTim() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đang tìm kiếm...");
        alert.show();
    }

    @FXML private void onChonDong() {
        // Chọn dòng trong bảng → hiển thị chi tiết lên phần trên
    }
}
