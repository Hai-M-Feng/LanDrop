package haimfeng.landrop.event;

import haimfeng.landrop.model.BroadcastPacket;

public class ReceivedConnectionRequestEvent extends AppEvent{
    public BroadcastPacket receivedPacket;

    public ReceivedConnectionRequestEvent(BroadcastPacket receivedPacket) {
        super("Received a broadcast packet");
        this.receivedPacket = receivedPacket;
    }
}
