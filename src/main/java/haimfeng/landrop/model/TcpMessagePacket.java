package haimfeng.landrop.model;

public class TcpMessagePacket {
    public enum MessageType {
        CONNECTION_CONFIRM,
        CONNECTION_ACK,
    }

    // 消息类型
    public MessageType messageType;

    // 通用字段
    public String userName; // 用户名称
    public String deviceUuid; // 设备UUID
    public String time; // 时间戳
}
