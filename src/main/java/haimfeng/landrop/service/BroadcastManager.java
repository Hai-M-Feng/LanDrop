package haimfeng.landrop.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.AppStartEvent;
import haimfeng.landrop.event.AppStopEvent;
import haimfeng.landrop.event.StartBroadcastEvent;
import haimfeng.landrop.event.StartListenEvent;

public class BroadcastManager {
    /**
     * 广播数据包类
     */
    public class BroadcastPacket {
        public String userName;
        public String deviceUuid;
        public String ip;
        public int port;

        @Override
        public String toString() {
            return "BroadcastPacket{" +
                    "userName='" + userName + '\'' +
                    ", deviceUuid='" + deviceUuid + '\'' +
                    ", ip='" + ip + '\'' +
                    ", port=" + port +
                    '}';
        }
    }

    private final EventBus eventBus; // 事件总线
    private final BroadcastSender broadcastSender; // 广播发送者
    private final BroadcastListener broadcastListener; // 监听者

    /**
     * 构造函数
     * @param eventBus 事件总线
     */
    public BroadcastManager(EventBus eventBus, BroadcastSender broadcastSender, BroadcastListener broadcastListener) {
        this.eventBus = eventBus;
        eventBus.register(this);
        this.broadcastSender = broadcastSender;
        this.broadcastListener = broadcastListener;
    }

    /**
     * 获取广播数据包
     * @return 广播数据包
     */
    private String getBroadcastPacket() {
        Gson gson = new Gson();

        BroadcastPacket packet = new BroadcastPacket();
        packet.userName = AppConstants.USER_NAME;
        packet.deviceUuid = AppConstants.DEVICE_UUID;
        packet.ip = AppConstants.LOCAL_IP;
        packet.port = AppConstants.LOCAL_PORT;

        return gson.toJson(packet);
    }

    /**
     * 启动广播
     * @param event 事件
     */
    @Subscribe
    private void startBroadcast(AppStartEvent event) {
        String packet = getBroadcastPacket();
        eventBus.post(new StartBroadcastEvent(packet));
        eventBus.post(new StartListenEvent("Start listen at port " + AppConstants.LOCAL_PORT));
    }

    /**
     * 停止广播
     */
    private void stopBroadcast() {
        if (broadcastSender != null) {
            broadcastSender.stopBroadcast();
        }
        if (broadcastListener != null) {
            broadcastListener.stopListen();
        }
    }

    /**
     * 监听停止事件
     * @param event 事件
     */
    @Subscribe
    private void onAppStopEvent(AppStopEvent event) {
        stopBroadcast();
        if (eventBus != null) {
            eventBus.unregister(this);
        }
    }

    /**
     * 获取广播状态
     * @return 状态
     */
    public boolean getBroadcastStatus() {
        return broadcastSender.isRunning();
    }

    /**
     * 获取监听状态
     * @return 状态
     */
    public boolean getListenStatus() {
        return broadcastListener.isRunning();
    }
}
