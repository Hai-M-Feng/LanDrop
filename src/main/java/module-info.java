module haimfeng.landrop {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.common;
    requires com.google.gson;
    requires java.logging;

    // 导出包含实际类的包
    exports haimfeng.landrop.application;
    exports haimfeng.landrop.controller;
    exports haimfeng.landrop.config;
    exports haimfeng.landrop.event;
    exports haimfeng.landrop.log;
    exports haimfeng.landrop.service;

    // 开放包以便FXMLLoader可以反射访问
    opens haimfeng.landrop.application to javafx.fxml;
    opens haimfeng.landrop.controller to javafx.fxml;
    opens haimfeng.landrop.service to com.google.common;

    // 开放资源包（即使没有类文件）
    opens haimfeng.landrop to javafx.fxml;
}