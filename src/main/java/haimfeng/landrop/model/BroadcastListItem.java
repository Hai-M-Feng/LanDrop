package haimfeng.landrop.model;

import haimfeng.landrop.util.TimeUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class BroadcastListItem {
    private final StringProperty receivedName = new SimpleStringProperty();
    private final StringProperty receivedTime = new SimpleStringProperty();
    private final BroadcastPacket broadcastPacket;

    /**
     * 构造函数
     * @param receivedName 收到用户名
     * @param receivedTime 收到时间
     */
    public BroadcastListItem(String receivedName, String receivedTime) {
        this.receivedName.set(receivedName);
        this.receivedTime.set(receivedTime);
        this.broadcastPacket = null;
    }

    /**
     * 构造函数
     * @param broadcastPacket 广播数据
     */
    public BroadcastListItem(BroadcastPacket broadcastPacket) {
        this.broadcastPacket = broadcastPacket;
        updateProperties();
    }

    /**
     * 更新属性
     */
    public void updateProperties() {
        if (broadcastPacket != null) {
            this.receivedName.set(broadcastPacket.userName);
            this.receivedTime.set(convertTime(broadcastPacket.time));
        }
    }

    // Getters & Setters (使用Property)
    public String getReceivedName() { return receivedName.get(); }
    public StringProperty receivedNameProperty() { return receivedName; }

    public String getReceivedTime() { return receivedTime.get(); }
    public StringProperty receivedTimeProperty() { return receivedTime; }

    public BroadcastPacket getBroadcastPacket() { return broadcastPacket; }

    /**
     * 时间转换
     * @param timeString 时间字符串
     * @return 时间字符串
     */
    public String convertTime(String timeString) {
        LocalDateTime time = TimeUtil.parseTime(timeString);
        long seconds = TimeUtil.betweenSeconds(time, TimeUtil.getCurrentTime());
        seconds = seconds < 0 ? -seconds : seconds;

        if (seconds < 5) return "刚刚";
        else if (seconds < 60) return seconds + " 秒前";
        else if (seconds < 60 * 60) return (seconds / 60) + " 分钟前";
        else return  (seconds / (60 * 60)) + " 小时前";
    }
}
