package broker;

import sendable.AckInfo;
import sendable.MSData;

public interface INetworkingBroker extends IBroker {
    void ackReceived(AckInfo ack);
    void registerService(MSData msData);
    void unregisterService(MSData msData);
}