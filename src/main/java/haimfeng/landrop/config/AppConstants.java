package haimfeng.landrop.config;

public final class AppConstants {
    // App 基本信息
    public static final String APP_NAME = "LanDrop"; // App名称
    public static final String APP_VERSION = "1.0.0"; // App版本
    public static final String APP_AUTHOR = "HaiMFeng"; // App作者
    public static final String APP_URL = "https://github.com/HaiMFeng/LanDrop"; // App网址

    // Debug 模式
    public static final boolean DEBUG = true; // 是否为调试模式

    // 连接配置
    public static final String BROADCAST_IP = "255.255.255.255"; // 广播IP
    public static final int BROADCAST_PORT = 8888; // 广播端口
    public static final int BROADCAST_TIMEOUT = 1000; // 广播超时时间
    public static final int REQUEST_TIMEOUT = 10000; // 请求超时

    // 用户配置
    public static String USER_NAME = "HaiMFeng"; // 用户名称
    public static String DEVICE_UUID = "HaiMFeng-PC"; // 设备UUID
    public static String LOCAL_IP = "127.0.0.1"; // 本地IP
    public static int LOCAL_UDP_LISTEN_PORT = 8889; // 本地UDP监听端口
    public static int LOCAL_TCP_LISTEN_PORT = 8890; // 本地TCP监听端口

    /**
     * 获取App常量信息
     * @return App常量信息
     */
    public static String getAppConstants() {
        return "App Name: " + APP_NAME + "\n" +
                "App Version: " + APP_VERSION + "\n" +
                "App Author: " + APP_AUTHOR + "\n" +
                "App URL: " + APP_URL + "\n" +
                "Debug Mode: " + DEBUG + "\n" +
                "Broadcast IP: " + BROADCAST_IP + "\n" +
                "Broadcast Port: " + BROADCAST_PORT + "\n" +
                "Broadcast Timeout: " + BROADCAST_TIMEOUT + "\n" +
                "User Name: " + USER_NAME + "\n" +
                "Device UUID: " + DEVICE_UUID + "\n" +
                "Local IP: " + LOCAL_IP + "\n" +
                "Local Port: " + LOCAL_UDP_LISTEN_PORT + "\n";
    }
}
