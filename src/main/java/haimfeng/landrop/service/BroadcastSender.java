package haimfeng.landrop.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.AppStopEvent;
import haimfeng.landrop.event.ExceptionEvent;
import haimfeng.landrop.event.StartBroadcastEvent;
import haimfeng.landrop.model.BroadcastPacket;
import haimfeng.landrop.util.TimeUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcastSender {
    // 成员变量
    private final EventBus eventBus; // 事件总线
    private final AtomicBoolean isRunning = new AtomicBoolean(false); // 运行状态
    private BroadcastPacket broadcastPacket; // 广播数据包
    private String broadcastPacketData; // 广播数据包数据
    private DatagramSocket socket; // 套接字
    private DatagramPacket packet; // 数据包
    private ScheduledExecutorService scheduler; // 定时器
    private Gson gson = new Gson(); // Gson

    /**
     * 构造函数
     * @param eventBus 事件总线
     */
    public BroadcastSender(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    /**
     * 监听开始广播事件
     * @param event 事件
     */
    @Subscribe
    public void onStartBroadcastEvent(StartBroadcastEvent event) {
        if (isRunning.get()) stopBroadcast();
        if (event.broadcastPacket == null) return;
        else broadcastPacket = event.broadcastPacket;

        try {
            // 创建套接字
            socket = new DatagramSocket(AppConstants.DISCOVERY_BROADCAST_PORT);
            socket.setBroadcast(true);

            // 创建定时器
            scheduler = Executors.newSingleThreadScheduledExecutor();
            isRunning.set(true);
            scheduler.scheduleAtFixedRate(
                    this::sendBroadcast,
                    0,
                    AppConstants.BROADCAST_TIMEOUT,
                    TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            stopBroadcast();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.BroadcastSender.onStartBroadcastEvent",
                    "Failed to start broadcast",
                    e));
        }
    }

    /**
     * 发送广播
     */
    public void sendBroadcast() {
        if (!isRunning.get()) return;

        broadcastPacket.time = TimeUtil.getCurrentTimeString(); // 设置时间
        broadcastPacketData = new Gson().toJson(broadcastPacket); // 转换为JSON

        try {
            // 创建数据包
            packet = new DatagramPacket(
                    broadcastPacketData.getBytes(StandardCharsets.UTF_8),
                    broadcastPacketData.length(),
                    InetAddress.getByName(AppConstants.BROADCAST_IP),
                    AppConstants.DISCOVERY_BROADCAST_TARGET_PORT);

            // 发送数据包
            if (socket != null && !socket.isClosed()) {
                socket.send(packet);
            }

        } catch (UnknownHostException e) {
            if (isRunning.get()) {
                stopBroadcast();
                eventBus.post(new ExceptionEvent(
                        "haimfeng.landrop.service.BroadcastSender.sendBroadcast",
                        "Invalid IP string",
                        e
                ));
            }
        } catch (Exception e) {
            if (isRunning.get()) {
                eventBus.post(new ExceptionEvent(
                        "haimfeng.landrop.service.BroadcastSender.sendBroadcast",
                        "Failed to send broadcast packet",
                        e));
            }
        }
    }

    /**
     * 停止广播
     */
    public void stopBroadcast() {
        // 停止广播
        isRunning.set(false);

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

        // 停止套接字
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            }
            catch (Exception e) {
                eventBus.post(new ExceptionEvent(
                        "haimfeng.landrop.service.BroadcastSender.stopBroadcast",
                        "Failed to close socket",
                        e));
            }
            socket = null;
        }

        packet = null;
        broadcastPacket = null;
    }

    /**
     * 监听停止事件
     * @param event 事件
     */
    @Subscribe
    public void onAppStopEvent(AppStopEvent event) {
        stopBroadcast();
        if (eventBus != null) {
            eventBus.unregister(this);
        }
    }

    /**
     * 获取运行状态
     * @return 运行状态
     */
    public boolean isRunning() {
        return isRunning.get();
    }
}
