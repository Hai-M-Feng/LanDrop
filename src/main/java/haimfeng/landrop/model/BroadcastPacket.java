package haimfeng.landrop.model;

public class BroadcastPacket {
    // 消息类型
    public enum MessageType {
        DISCOVERY,
        CONNECTION_REQUEST
    }

    public String userName; // 用户名称
    public String deviceUuid; // 设备UUID
    public String ip; // 设备IP
    public int port; // 设备端口
    public MessageType message; // 消息
    public String time; // 时间戳

    @Override
    public String toString() {
        return "{" +
                "userName='" + userName + '\'' +
                ", deviceUuid='" + deviceUuid + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", message=" + message +
                ", time='" + time + '\'' +
                '}';
    }
}