package haimfeng.landrop.config;

public final class AppConstants {
    // App 基本信息
    public static final String APP_NAME = "LanDrop";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR = "HaiMFeng";
    public static final String APP_URL = "https://github.com/HaiMFeng/LanDrop";

    // Debug 模式
    public static final boolean DEBUG = true;

    // 连接配置
    public static final String BROADCAST_IP = "255.255.255.255";
    public static final int BROADCAST_PORT = 8888;
    public static final int BROADCAST_TIMEOUT = 1000;

    // 用户配置
    public static String USER_NAME = "HaiMFeng";
    public static String DEVICE_UUID = "HaiMFeng-PC";
    public static String LOCAL_IP = "127.0.0.1";
    public static int LOCAL_PORT = 8889;

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
                "Local Port: " + LOCAL_PORT + "\n";
    }
}
