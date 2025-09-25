package haimfeng.landrop.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.AppStopEvent;
import haimfeng.landrop.event.BroadcastReceivedEvent;
import haimfeng.landrop.event.ExceptionEvent;
import haimfeng.landrop.event.StartListenEvent;
import haimfeng.landrop.service.BroadcastManager.BroadcastPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcastListener {
    private final EventBus eventBus; // 事件总线
    private final AtomicBoolean isRunning = new AtomicBoolean(false); // 运行状态
    private volatile DatagramSocket socket; // 套接字
    private final Gson gson; // Gson
    private Thread listenerThread; // 监听线程

    /**
     * 构造函数
     * @param eventBus 事件总线
     */
    public BroadcastListener(EventBus eventBus)
    {
        this.eventBus = eventBus;
        eventBus.register(this);
        gson = new Gson();
    }

    /**
     * 监听开始广播事件
     * @param event 广播事件
     */
    @Subscribe
    public void onStartListenEvent(StartListenEvent event)
    {
        if (isRunning.get()) return;

        try {
            // 创建套接字
            socket = new DatagramSocket(AppConstants.LOCAL_PORT);

            listenerThread = new Thread(this::listen);
            listenerThread.setDaemon(true);
            listenerThread.setName("BroadcastListener Thread");
            isRunning.set(true);
            listenerThread.start();
        }
        catch (Exception e) {
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.BroadcastListener.onStartListenEvent",
                    "Failed to create socket",
                    e));
        }
    }

    /**
     * 监听广播
     */
    private void listen()
    {
        while (isRunning.get()) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                // 接收广播
                socket.receive(packet);
                String receivedData = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8).trim();

                if (receivedData.isEmpty()) {
                    continue;
                }

                BroadcastPacket receivedBroadcastPacket = gson.fromJson(receivedData,BroadcastPacket.class);

                // 过滤掉自己发送的广播
                if (receivedBroadcastPacket != null && receivedBroadcastPacket.deviceUuid != null) {
                    if (true || !receivedBroadcastPacket.deviceUuid.equals(AppConstants.DEVICE_UUID)) {
                        switch (receivedBroadcastPacket.message) {
                            case "DISCOVERY": {
                                eventBus.post(new BroadcastReceivedEvent(receivedBroadcastPacket));
                                break;
                            }

                            case "CONNECTION_REQUEST": {

                            }
                        }
                    }
                }
            }
            catch (SocketException e) {
                if (isRunning.get()) {
                    eventBus.post(new ExceptionEvent(
                            "haimfeng.landrop.service.BroadcastListener.listen",
                            "Socket error while receiving broadcast packet",
                            e));
                }
            } catch (JsonSyntaxException e) {
                continue;
            } catch (Exception e) {
                if (isRunning.get()) {
                    eventBus.post(new ExceptionEvent(
                            "haimfeng.landrop.service.BroadcastListener.listen",
                            "Failed to receive broadcast packet",
                            e));
                }
            }
        }
    }

    /**
     * 停止监听
     */
    public void stopListen()
    {
        isRunning.set(false);

        // 关闭socket
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception e) {
                eventBus.post(new ExceptionEvent(
                        "haimfeng.landrop.service.BroadcastListener.stopListen",
                        "Failed to close socket",
                        e));
            }
        }
        socket = null;

        if (listenerThread != null && listenerThread.isAlive()) {
            try {
                listenerThread.join(1000); // 等待最多1秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                eventBus.post(new ExceptionEvent(
                        "haimfeng.landrop.service.BroadcastListener.stopListen",
                        "Interrupted while waiting for listener thread to stop",
                        e));
            }
        }
    }

    /**
     * 监听停止事件
     * @param event 停止事件
     */
    @Subscribe
    public void onAppStopEvent(AppStopEvent event)
    {
        stopListen();
        if (eventBus != null) {
            eventBus.unregister(this);
        }
    }

    /**
     * 获取运行状态
     * @return 运行状态
     */
    public boolean isRunning()
    {
        return isRunning.get();
    }
}
