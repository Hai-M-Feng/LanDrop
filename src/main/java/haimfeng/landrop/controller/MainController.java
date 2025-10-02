package haimfeng.landrop.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.AppStartEvent;
import haimfeng.landrop.event.AppStopEvent;
import haimfeng.landrop.event.UpdateReceivedListEvent;
import haimfeng.landrop.log.EventLogger;
import haimfeng.landrop.model.BroadcastListItem;
import haimfeng.landrop.model.BroadcastPacket;
import haimfeng.landrop.service.BroadcastListener;
import haimfeng.landrop.service.BroadcastManager;
import haimfeng.landrop.service.BroadcastSender;
import haimfeng.landrop.service.ConnectionManager;
import haimfeng.landrop.view.BroadcastListItemCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {
    public static final EventBus eventBus = new EventBus(); // 创建一个事件总线
    private Stage primaryStage; // 主窗口，不能删除，用于调用关闭
    private ScheduledExecutorService scheduler; // 定时器，用于刷新列表时间

    // region FXML 控件声明
    @FXML private ListView<BroadcastListItem> broadcastListView; // 广播列表
    private final ObservableList<BroadcastListItem> broadcastList = FXCollections.observableArrayList(); // 创建一个广播列表
    // endregion

    /**
     * 初始化
     */
    @FXML
    public void initialize()
    {
        eventBus.register(this); // 注册事件总线
        AppConstants.DEVICE_UUID = UUID.randomUUID().toString(); // 生成设备唯一标识符
        EventLogger eventLogger = new EventLogger(eventBus); // 创建一个日志监听器

        initializeControls(); // 初始化控件

        // 启动广播
        BroadcastListener broadcastListener = new BroadcastListener(eventBus);
        BroadcastSender broadcastSender = new BroadcastSender(eventBus);
        BroadcastManager broadcastManager = new BroadcastManager(eventBus, broadcastSender, broadcastListener);

        // 启动连接管理器
        ConnectionManager connectionManager = new ConnectionManager(eventBus);

        // 启动
        eventBus.post(new AppStartEvent("AppStart"));
    }

    /**
     * 初始化控件
     */
    public void initializeControls()
    {
        // 初始化列表
        broadcastListView.setCellFactory(lv -> new BroadcastListItemCell(eventBus));
        broadcastListView.setItems(broadcastList);

        // 创建一个定时器, 用于刷新列表时间
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::listFlushThread,
                0,
                AppConstants.BROADCAST_TIMEOUT,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 设置主窗口
     * @param primaryStage 主窗口
     */
    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(this::close);
    }

    /**
     * 关闭
     */
    public void close(WindowEvent event) {
        eventBus.post(new AppStopEvent("AppStop"));
        eventBus.unregister(this);

        // 停止定时器
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 更新接收列表
     * @param event 更新接收列表事件
     */
    @Subscribe
    public void onUpdateReceivedListEvent(UpdateReceivedListEvent event) {
        Platform.runLater(() -> {
            broadcastList.clear();
            for (BroadcastPacket item : event.receivedPackets) {
                broadcastList.add(new BroadcastListItem(item));
            }
        });
    }

    /**
     * 刷新列表
     */
    public void listFlushThread() {
        Platform.runLater(() -> {
            for (BroadcastListItem item : broadcastList) {
                item.updateProperties();
            }
        });
    }
}