package haimfeng.landrop.event;

import haimfeng.landrop.service.BroadcastManager.BroadcastPacket;

public class BroadcastReceivedEvent extends AppEvent{
    public BroadcastPacket broadcastPacket;
    public BroadcastReceivedEvent(BroadcastPacket broadcastPacket) {
        super("Broadcast received");
        this.broadcastPacket = broadcastPacket;
    }
}
