package haimfeng.landrop.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.AppStartEvent;
import haimfeng.landrop.event.AppStopEvent;
import haimfeng.landrop.log.EventLogger;
import haimfeng.landrop.service.BroadcastListener;
import haimfeng.landrop.service.BroadcastManager;
import haimfeng.landrop.service.BroadcastSender;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.UUID;

public class MainController {
    // 创建一个事件总线
    public static final EventBus eventBus = new EventBus();
    private Stage primaryStage;

    /**
     * 初始化
     */
    @FXML
    public void initialize()
    {
        // 注册事件总线
        eventBus.register(this);

        AppConstants.DEVICE_UUID = UUID.randomUUID().toString(); // 生成设备唯一标识符

        EventLogger eventLogger = new EventLogger(eventBus); // 创建一个日志监听器

        // 启动广播
        BroadcastListener broadcastListener = new BroadcastListener(eventBus);
        BroadcastSender broadcastSender = new BroadcastSender(eventBus);
        BroadcastManager broadcastManager = new BroadcastManager(eventBus, broadcastSender, broadcastListener);

        // 启动
        eventBus.post(new AppStartEvent("AppStart"));
    }

    /**
     * 设置主窗口
     * @param primaryStage
     */
    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(this::close);
    }

    /**
     * 关闭
     */
    public void close(WindowEvent event) {
        eventBus.post(new AppStopEvent("AppStop"));
        eventBus.unregister(this);
    }
}