module haimfeng.landrop {
    requires javafx.controls;
    requires javafx.fxml;


    opens haimfeng.landrop to javafx.fxml;
    exports haimfeng.landrop;
}