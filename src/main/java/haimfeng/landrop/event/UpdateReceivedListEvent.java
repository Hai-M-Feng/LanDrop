package haimfeng.landrop.event;

import haimfeng.landrop.service.BroadcastManager.BroadcastPacket;

import java.util.Collection;

public class UpdateReceivedListEvent extends AppEvent{
    public Collection<BroadcastPacket> receivedPackets;

    public UpdateReceivedListEvent(Collection<BroadcastPacket> receivedPackets) {
        super("Update Received List");
        this.receivedPackets = receivedPackets;
    }
}
