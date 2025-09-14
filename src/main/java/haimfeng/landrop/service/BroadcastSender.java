package haimfeng.landrop.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.AppStopEvent;
import haimfeng.landrop.event.ExceptionEvent;
import haimfeng.landrop.event.StartBroadcastEvent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcastSender {
    // 成员变量
    private final EventBus eventBus; // 事件总线
    private final AtomicBoolean isRunning = new AtomicBoolean(false); // 运行状态
    private String broadcastPacket; // 广播数据包
    private DatagramSocket socket; // 套接字
    private DatagramPacket packet; // 数据包
    private ScheduledExecutorService scheduler; // 定时器

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
        if (event.EventData.isEmpty()) return;
        else broadcastPacket = event.EventData;

        try {
            // 创建套接字
            socket = new DatagramSocket(AppConstants.BROADCAST_PORT);
            socket.setBroadcast(true);

            // 创建数据包
            packet = new DatagramPacket(
                    broadcastPacket.getBytes(StandardCharsets.UTF_8),
                    broadcastPacket.length(),
                    InetAddress.getByName(AppConstants.BROADCAST_IP),
                    AppConstants.BROADCAST_PORT);

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
                    "Failed to start broadcast.",
                    e));
        }
    }

    /**
     * 发送广播
     */
    public void sendBroadcast() {
        if (!isRunning.get()) return;

        try {
            if (socket != null && !socket.isClosed()) {
                socket.send(packet);
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
    private void stopBroadcast() {
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
}
