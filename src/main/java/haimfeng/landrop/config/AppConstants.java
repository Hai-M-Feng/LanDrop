package haimfeng.landrop.config;

public final class AppConstants {
    // App 基本信息
    public static final String APP_NAME = "LanDrop";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR = "HaiMFeng";
    public static final String APP_URL = "https://github.com/HaiMFeng/LanDrop";

    // Debug 模式
    public static final boolean DEBUG = true;

    // 默认连接配置
    public static final String DEFAULT_BROADCAST_IP = "255.255.255.255";
    public static final String DEFAULT_LOCAL_IP = "127.0.0.1";
    public static final int DEFAULT_DISCOVERY_BROADCAST_PORT = 8888;
    public static final int DEFAULT_DISCOVERY_BROADCAST_TARGET_PORT = 8889;
    public static final int DEFAULT_REQUEST_BROADCAST_PORT = 8887;
    public static final int DEFAULT_REQUEST_BROADCAST_TARGET_PORT = 8889;
    public static final int DEFAULT_LOCAL_UDP_LISTEN_PORT = 8889;
    public static final int DEFAULT_LOCAL_TCP_LISTEN_PORT = 8890;
    public static final int DEFAULT_BROADCAST_TIMEOUT = 1000;
    public static final int DEFAULT_REQUEST_TIMEOUT = 10000;
    public static final String DEFAULT_USER_NAME = "DEFAULT_NAME";
    public static final String DEFAULT_UUID = "DEFAULT_UUID";

    // 连接配置
    public static String BROADCAST_IP; // 广播IP
    public static String LOCAL_IP; // 本地IP
    public static int DISCOVERY_BROADCAST_PORT; // 发现广播端口
    public static int DISCOVERY_BROADCAST_TARGET_PORT; // 目标广播监听端口
    public static int REQUEST_BROADCAST_PORT; // 请求广播端口
    public static int REQUEST_BROADCAST_TARGET_PORT; // 目标广播监听端口
    public static int LOCAL_UDP_LISTEN_PORT; // 本地UDP监听端口
    public static int LOCAL_TCP_LISTEN_PORT; // 本地TCP监听端口
    public static final int BROADCAST_TIMEOUT = DEFAULT_BROADCAST_TIMEOUT; // 广播超时时间
    public static final int REQUEST_TIMEOUT = DEFAULT_REQUEST_TIMEOUT; // 请求超时时间

    // 用户配置
    public static String USER_NAME; // 用户名称
    public static String DEVICE_UUID; // 设备UUID，自动生成

    /**
     * 重置所有配置为默认值
     */
    public static void resetToDefaults() {
        // 固定项
        BROADCAST_IP = DEFAULT_BROADCAST_IP;
        LOCAL_IP = DEFAULT_LOCAL_IP;
        DEVICE_UUID = DEFAULT_UUID;
        // 可变项
        DISCOVERY_BROADCAST_PORT = DEFAULT_DISCOVERY_BROADCAST_PORT;
        DISCOVERY_BROADCAST_TARGET_PORT = DEFAULT_DISCOVERY_BROADCAST_TARGET_PORT;
        REQUEST_BROADCAST_PORT = DEFAULT_REQUEST_BROADCAST_PORT;
        REQUEST_BROADCAST_TARGET_PORT = DEFAULT_REQUEST_BROADCAST_TARGET_PORT;
        LOCAL_UDP_LISTEN_PORT = DEFAULT_LOCAL_UDP_LISTEN_PORT;
        LOCAL_TCP_LISTEN_PORT = DEFAULT_LOCAL_TCP_LISTEN_PORT;
        USER_NAME = DEFAULT_USER_NAME;
    }

    /**
     * 设置调试模式下的常量
     */
    public static void setDebugConstants(String[] args) {
        if (DEBUG && args.length >= 7) {
            USER_NAME = args[0];
            DISCOVERY_BROADCAST_PORT = Integer.parseInt(args[1]);
            REQUEST_BROADCAST_PORT = Integer.parseInt(args[2]);
            LOCAL_UDP_LISTEN_PORT = Integer.parseInt(args[3]);
            LOCAL_TCP_LISTEN_PORT = Integer.parseInt(args[4]);
            DISCOVERY_BROADCAST_TARGET_PORT = Integer.parseInt(args[5]);
            REQUEST_BROADCAST_TARGET_PORT = Integer.parseInt(args[6]);
        }
    }

    /**
     * 获取App常量信息
     */
    public static String getAppConstants() {
        return "AppConstants:\n" +
                "  APP_NAME: " + APP_NAME + "\n" +
                "  APP_VERSION: " + APP_VERSION + "\n" +
                "  APP_AUTHOR: " + APP_AUTHOR + "\n" +
                "  APP_URL: " + APP_URL + "\n" +
                "  DEBUG: " + DEBUG + "\n" +
                "  BROADCAST_IP: " + BROADCAST_IP + "\n" +
                "  LOCAL_IP: " + LOCAL_IP + "\n" +
                "  DISCOVERY_BROADCAST_PORT: " + DISCOVERY_BROADCAST_PORT + "\n" +
                "  DISCOVERY_BROADCAST_TARGET_PORT: " + DISCOVERY_BROADCAST_TARGET_PORT + "\n" +
                "  REQUEST_BROADCAST_PORT: " + REQUEST_BROADCAST_PORT + "\n" +
                "  REQUEST_BROADCAST_TARGET_PORT: " + REQUEST_BROADCAST_TARGET_PORT + "\n" +
                "  LOCAL_UDP_LISTEN_PORT: " + LOCAL_UDP_LISTEN_PORT + "\n" +
                "  LOCAL_TCP_LISTEN_PORT: " + LOCAL_TCP_LISTEN_PORT + "\n" +
                "  BROADCAST_TIMEOUT: " + BROADCAST_TIMEOUT + "ms\n" +
                "  REQUEST_TIMEOUT: " + REQUEST_TIMEOUT + "ms\n" +
                "  USER_NAME: " + USER_NAME + "\n" +
                "  DEVICE_UUID: " + DEVICE_UUID;
    }
}