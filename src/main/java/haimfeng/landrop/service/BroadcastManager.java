package haimfeng.landrop.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.*;
import haimfeng.landrop.model.BroadcastPacket;

import java.util.HashMap;
import java.util.Map;

public class BroadcastManager {
    private final EventBus eventBus; // 事件总线
    private final BroadcastSender broadcastSender; // 广播发送者
    private final BroadcastListener broadcastListener; // 监听者
    private final Map<String, BroadcastPacket> receivedBroadcastPackets; // 接收到的广播数据包

    /**
     * 构造函数
     * @param eventBus 事件总线
     */
    public BroadcastManager(EventBus eventBus, BroadcastSender broadcastSender, BroadcastListener broadcastListener) {
        this.eventBus = eventBus;
        eventBus.register(this);
        this.broadcastSender = broadcastSender;
        this.broadcastListener = broadcastListener;
        receivedBroadcastPackets = new HashMap<>();
    }

    /**
     * 获取广播数据包
     * @return 广播数据包
     */
    private BroadcastPacket getBroadcastPacket() {
        BroadcastPacket packet = new BroadcastPacket();
        packet.userName = AppConstants.USER_NAME;
        packet.deviceUuid = AppConstants.DEVICE_UUID;
        packet.ip = AppConstants.LOCAL_IP;
        packet.port = AppConstants.LOCAL_UDP_LISTEN_PORT;
        packet.message = BroadcastPacket.MessageType.DISCOVERY;

        return packet;
    }

    /**
     * 启动广播
     * @param event 事件
     */
    @Subscribe
    private void startBroadcast(AppStartEvent event) {
        BroadcastPacket packet = getBroadcastPacket();
        eventBus.post(new StartBroadcastEvent(packet));
        eventBus.post(new StartListenEvent("Start listen at port " + AppConstants.LOCAL_UDP_LISTEN_PORT));
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

    /**
     * 接收到广播数据包
     * @param event 事件
     */
    @Subscribe
    private void onBroadcastReceivedEvent(BroadcastReceivedEvent event) {
        // 获取数据包
        BroadcastPacket receivedPacket = event.broadcastPacket;
        if(receivedPacket == null) return;
        String uuid = receivedPacket.deviceUuid;

        if (uuid != null && !uuid.isEmpty()) {
            receivedBroadcastPackets.put(uuid, receivedPacket); // 更新数据包
            eventBus.post(new UpdateReceivedListEvent(receivedBroadcastPackets.values())); // 发送更新事件
        }
    }

    /**
     * 连接建立事件
     * @param event 事件
     */
    @Subscribe
    private void onConnectionEstablishedEvent(ConnectionEstablishedEvent event) {
        stopBroadcast();
    }
}
