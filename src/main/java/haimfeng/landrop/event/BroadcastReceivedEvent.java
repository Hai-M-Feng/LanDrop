package haimfeng.landrop.event;

import haimfeng.landrop.model.BroadcastPacket;

public class BroadcastReceivedEvent extends AppEvent{
    public BroadcastPacket broadcastPacket;
    public BroadcastReceivedEvent(BroadcastPacket broadcastPacket) {
        super("Broadcast received");
        this.broadcastPacket = broadcastPacket;
    }
}
