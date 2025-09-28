package haimfeng.landrop.event;

import haimfeng.landrop.model.BroadcastPacket;

public class SendConnectionRequestEvent extends AppEvent{
    public final BroadcastPacket broadcastPacket;

    public SendConnectionRequestEvent(BroadcastPacket broadcastPacket) {
        super("User send a connection request");
        this.broadcastPacket = broadcastPacket;
    }
}
