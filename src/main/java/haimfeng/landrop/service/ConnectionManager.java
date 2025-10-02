package haimfeng.landrop.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

import com.google.gson.JsonSyntaxException;
import haimfeng.landrop.event.*;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.model.BroadcastPacket;
import haimfeng.landrop.model.TcpMessagePacket;
import haimfeng.landrop.util.TimeUtil;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public class ConnectionManager {
    // 连接状态
    private enum ConnectionState {
        WAITING,
        CONNECTING,
        CONNECTED
    }
    // 连接角色
    private enum ConnectionRole {
        NONE,
        CLIENT,
        SERVER
    }
    // 成员变量
    private final EventBus eventBus; // 事件总线
    private BroadcastPacket connectionTarget; // 连接目标
    private ServerSocket serverSocket; // 服务器套接字
    private Socket clientSocket; // 客户端套接字
    private DatagramSocket broadcastSocket; // 套接字
    private DatagramPacket broadcastPacket; // 数据包
    private final AtomicReference<ConnectionState> connectionState = new AtomicReference<>(ConnectionState.WAITING); // 连接状态
    private final AtomicReference<ConnectionRole> connectionRole = new AtomicReference<>(ConnectionRole.NONE); // 连接角色
    private Thread acceptConnectionThread; // 接受连接线程
    private BufferedReader reader; // 读取器
    private BufferedWriter writer; // 写入器

    /**
     * 构造函数
     * @param eventBus 事件总线
     */
    public ConnectionManager(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    /**
     * 获取广播数据
     * @return 广播数据
     */
    private String getBroadcastData() {
        BroadcastPacket requestPacket = new BroadcastPacket();

        // 构建数据包内容
        requestPacket.userName = AppConstants.USER_NAME;
        requestPacket.deviceUuid = AppConstants.DEVICE_UUID;
        requestPacket.ip = AppConstants.LOCAL_IP;
        requestPacket.port = AppConstants.LOCAL_TCP_LISTEN_PORT;
        requestPacket.message = BroadcastPacket.MessageType.CONNECTION_REQUEST;
        requestPacket.time = TimeUtil.getCurrentTimeString();

        return new Gson().toJson(requestPacket);
    }

    /**
     * 获取确认数据
     * @return 确认数据
     **/
    private String getConfirmData(TcpMessagePacket.MessageType messageType) {
        TcpMessagePacket confirmPacket = new TcpMessagePacket();

        confirmPacket.messageType = messageType;
        confirmPacket.userName = AppConstants.USER_NAME;
        confirmPacket.deviceUuid = AppConstants.DEVICE_UUID;
        confirmPacket.time = TimeUtil.getCurrentTimeString();

        return new Gson().toJson(confirmPacket);
    }

    /**
     * 监听发送连接请求事件
     * @param event 事件
     */
    @Subscribe
    public void onSendConnectionRequestEvent(SendConnectionRequestEvent event) {
        if (connectionState.get() != ConnectionState.WAITING || connectionRole.get() != ConnectionRole.NONE) {
            return;
        }
        close();
        connectionState.compareAndSet(ConnectionState.WAITING, ConnectionState.CONNECTING);
        connectionRole.compareAndSet(ConnectionRole.NONE, ConnectionRole.SERVER);
        connectionTarget = event.broadcastPacket;


        try {
            serverSocket = new ServerSocket(AppConstants.LOCAL_TCP_LISTEN_PORT);

            // 创建线程
            acceptConnectionThread = new Thread(this::acceptConnectionThread);
            acceptConnectionThread.setName("AcceptConnection Thread");
            acceptConnectionThread.setDaemon(true);
            acceptConnectionThread.start();

            // 创建数据包
            String json = getBroadcastData();
            broadcastPacket = new DatagramPacket(
                    json.getBytes(StandardCharsets.UTF_8),
                    json.length(),
                    InetAddress.getByName(connectionTarget.ip),
                    connectionTarget.port);

            // 发送数据包
            try {
                broadcastSocket = new DatagramSocket(AppConstants.REQUEST_BROADCAST_PORT);
                broadcastSocket.send(broadcastPacket);
            } catch (IOException e) {
                close();
                eventBus.post(new ExceptionEvent(
                        "haimfeng.landrop.service.ConnectionManager.onSendConnectionRequestEvent",
                        "Failed to send request broadcast packet",
                        e
                ));
            } finally {
                if (broadcastSocket != null && !broadcastSocket.isClosed()) {
                    broadcastSocket.close();
                }
            }
        }
        catch (IOException e) {
            close();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.onSendConnectionRequestEvent",
                    "Failed to create server socket",
                    e
            ));
        }
    }

    /**
     * 接受连接
     */
    private void acceptConnectionThread() {
        try {
            serverSocket.setSoTimeout(AppConstants.REQUEST_TIMEOUT);
            clientSocket = serverSocket.accept(); // 接受连接

            if (clientSocket != null) {
                clientSocket.setKeepAlive(true);

                // 开始校验程序
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                String receivedData = reader.readLine();
                if (receivedData != null) {
                    TcpMessagePacket confirmPacket = new Gson().fromJson(receivedData, TcpMessagePacket.class);
                    // 校验消息
                    if (confirmPacket.messageType == TcpMessagePacket.MessageType.CONNECTION_CONFIRM
                    && confirmPacket.deviceUuid.equals(connectionTarget.deviceUuid)) {
                        // 发送确认消息
                        writer.write(getConfirmData(TcpMessagePacket.MessageType.CONNECTION_ACK));
                        writer.newLine();
                        writer.flush();

                        connectionState.compareAndSet(ConnectionState.CONNECTING, ConnectionState.CONNECTED); // 更新状态机
                        eventBus.post(new ConnectionEstablishedEvent(connectionTarget)); // 发送连接建立事件
                    }
                }
            }
            else {
                connectionState.compareAndSet(ConnectionState.CONNECTING, ConnectionState.WAITING);
                close();
            }
        }
        catch (SocketTimeoutException e) {
            close();
            eventBus.post(new RequestOutOfTimeEvent("Request out of " + AppConstants.REQUEST_TIMEOUT + "ms"));
        }
        catch (InterruptedIOException e) {
            Thread.currentThread().interrupt(); // 重置中断状态
            close();
            return;
        }
        catch (SocketException e) {
            // 单独处理 setSoTimeout 引发的异常
            close();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.acceptConnection",
                    "Failed to accept connection",
                    e
            ));
        }
        catch (JsonSyntaxException e) {
            close();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.acceptConnection",
                    "Failed to convert Json",
                    e
            ));
        }
        catch (Exception e) {
            close();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.acceptConnection",
                    "Failed to accept connection",
                    e
            ));
        }
    }

    /**
     * 监听接收连接请求事件
     * @param event 事件
     */
    @Subscribe
    public void onReceivedConnectionRequestEvent(ReceivedConnectionRequestEvent event) {
        if (connectionState.get() != ConnectionState.WAITING || connectionRole.get() != ConnectionRole.NONE) {
            return;
        }
        close();
        connectionState.compareAndSet(ConnectionState.WAITING, ConnectionState.CONNECTING);
        connectionRole.compareAndSet(ConnectionRole.NONE, ConnectionRole.CLIENT);
        connectionTarget = event.receivedPacket;

        try {
            // 创建套接字
            clientSocket = new Socket(connectionTarget.ip, connectionTarget.port);
            clientSocket.setKeepAlive(true);
            clientSocket.setSoTimeout(AppConstants.REQUEST_TIMEOUT);
            // 发送确认消息
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            writer.write(getConfirmData(TcpMessagePacket.MessageType.CONNECTION_CONFIRM));
            writer.newLine();
            writer.flush();

            // 接收确认消息
            clientSocket.setSoTimeout(AppConstants.REQUEST_TIMEOUT);
            String receivedData = reader.readLine();
            if (receivedData != null) {
                TcpMessagePacket confirmPacket = new Gson().fromJson(receivedData, TcpMessagePacket.class);
                if (confirmPacket.messageType == TcpMessagePacket.MessageType.CONNECTION_ACK) {
                    connectionState.compareAndSet(ConnectionState.CONNECTING, ConnectionState.CONNECTED); // 更新状态机
                    eventBus.post(new ConnectionEstablishedEvent(connectionTarget)); // 发送连接建立事件
                }
            }
        }
        catch (SocketTimeoutException e) {
            close();
            eventBus.post(new RequestOutOfTimeEvent("Waiting accept out of " + AppConstants.REQUEST_TIMEOUT + "ms"));
        }
        catch (IOException e) {
            close();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.onReceivedConnectionRequestEvent",
                    "Failed to create client socket",
                    e
            ));
        }
        catch (JsonSyntaxException e) {
            close();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.onReceivedConnectionRequestEvent",
                    "Failed to parse confirm data",
                    e
            ));
        }
        catch (Exception e) {
            close();
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.onReceivedConnectionRequestEvent",
                    "Failed to establish connection",
                    e
            ));
        }
    }

    /**
     * 关闭
     */
    public void close() {
        connectionState.set(ConnectionState.WAITING);
        connectionRole.set(ConnectionRole.NONE);
        connectionTarget = null; // 清空连接目标
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            if (broadcastSocket != null && !broadcastSocket.isClosed()) {
                broadcastSocket.close();
                broadcastSocket = null;
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                clientSocket = null;
            }
            if (acceptConnectionThread != null) {
                acceptConnectionThread.interrupt();
                acceptConnectionThread = null;
            }
        }
        catch (Exception e) {
            eventBus.post(new ExceptionEvent(
                    "haimfeng.landrop.service.ConnectionManager.close",
                    "Failed to close ConnectionManager",
                    e
            ));
        }
    }

    /**
     * 监听应用停止事件
     * @param event 事件
     */
    @Subscribe
    public void onAppStopEvent(AppStopEvent event) {
        eventBus.unregister(this);
        close();
    }
}
