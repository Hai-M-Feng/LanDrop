package haimfeng.landrop.event;

import haimfeng.landrop.model.BroadcastPacket;

public class ConnectionEstablishedEvent extends AppEvent{
    public BroadcastPacket broadcastPacket;
    public ConnectionEstablishedEvent(BroadcastPacket broadcastPacket) {
        super("Connected to target device");
        this.broadcastPacket = broadcastPacket;
    }

}
