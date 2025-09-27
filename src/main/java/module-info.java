open module haimfeng.landrop {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.common;
    requires com.google.gson;
    requires java.logging;
    requires org.apache.logging.log4j;

    // 导出包含实际类的包
    exports haimfeng.landrop.application;
    exports haimfeng.landrop.controller;
    exports haimfeng.landrop.config;
    exports haimfeng.landrop.event;
    exports haimfeng.landrop.log;
    exports haimfeng.landrop.service;
    exports haimfeng.landrop.view;
    exports haimfeng.landrop.model;
}