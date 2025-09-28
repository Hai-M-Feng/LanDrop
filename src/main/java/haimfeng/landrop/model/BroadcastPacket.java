package haimfeng.landrop.model;

public class BroadcastPacket {
    public String userName;
    public String deviceUuid;
    public String ip;
    public String message;
    public int port;
    public String time;

    @Override
    public String toString() {
        return "{" +
                "userName='" + userName + '\'' +
                ", deviceUuid='" + deviceUuid + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}