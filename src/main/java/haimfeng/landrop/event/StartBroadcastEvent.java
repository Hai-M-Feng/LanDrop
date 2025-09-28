package haimfeng.landrop.event;

import haimfeng.landrop.model.BroadcastPacket;

public class StartBroadcastEvent extends AppEvent {
    public BroadcastPacket broadcastPacket;

    public StartBroadcastEvent(BroadcastPacket broadcastPacket)
    {
        super("Start Broadcast");
        this.broadcastPacket = broadcastPacket;
    }
}
