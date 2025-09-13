module haimfeng.landrop {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.common;

    // 导出包含实际类的包
    exports haimfeng.landrop.application;
    exports haimfeng.landrop.controller;

    // 开放包以便FXMLLoader可以反射访问
    opens haimfeng.landrop.application to javafx.fxml;
    opens haimfeng.landrop.controller to javafx.fxml;

    // 开放资源包（即使没有类文件）
    opens haimfeng.landrop to javafx.fxml;
}